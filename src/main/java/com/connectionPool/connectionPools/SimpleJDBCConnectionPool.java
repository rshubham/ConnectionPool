package com.connectionPool.connectionPools;

import com.connectionPool.configurations.ConnectionConfig;
import com.connectionPool.connectionFactories.SimpleDBConnectionFactory;
import com.connectionPool.connections.Connection;
import com.connectionPool.connections.dbConnections.DBConnection;
import com.connectionPool.enums.ConnectionStateEnum;
import com.connectionPool.observers.BlockingQueueSynchonizerFactory;
import com.connectionPool.observers.BlockingQueueSynchronizer;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;


public class SimpleJDBCConnectionPool implements DBConnectionPool{

    private BlockingQueue<Connection> idleConnectionQueue;
    private BlockingQueue<Connection> usedConnectionQueue;
    private static final Integer MAX_POOL_CAPACITY = Integer.parseInt(ConnectionConfig.getConfig().getConfig("MAX_POOL_CAPACITY"));
    private BlockingQueueSynchronizer blockingQueueSynchonizer;

    /* ------------------ Singleton Code -------------------- */

    private SimpleJDBCConnectionPool(){
        this.idleConnectionQueue = new ArrayBlockingQueue<>(MAX_POOL_CAPACITY);
        this.usedConnectionQueue = new PriorityBlockingQueue<>(MAX_POOL_CAPACITY, new Comparator<Connection>() {
            @Override
            public int compare(Connection o1, Connection o2) {
                if(o1.getConnectionState() == ConnectionStateEnum.IDLE && o2.getConnectionState() == ConnectionStateEnum.ACTIVE) return 1;
                if((o1.getConnectionState() == ConnectionStateEnum.IDLE && o2.getConnectionState() == ConnectionStateEnum.IDLE)
                        || ((o1.getConnectionState() == ConnectionStateEnum.ACTIVE && o2.getConnectionState() == ConnectionStateEnum.ACTIVE)))
                {
                    return 0;
                }
                return -1;
            }
        });
        blockingQueueSynchonizer = BlockingQueueSynchonizerFactory.createBlockingQueueSynchronizer(this.idleConnectionQueue,this.usedConnectionQueue,MAX_POOL_CAPACITY);
        blockingQueueSynchonizer.triggerSync();
    }

    private static class SimpleJDBCConnectionPoolHelper{
        private static SimpleJDBCConnectionPool instance = new SimpleJDBCConnectionPool();
    }

    public static SimpleJDBCConnectionPool getFactory(){
        return SimpleJDBCConnectionPool.SimpleJDBCConnectionPoolHelper.instance;
    }

    /* ----------------------------------------------------- */

    @Override
    public synchronized DBConnection getConnection() {
        DBConnection connection;
        if(this.idleConnectionQueue.isEmpty() && this.usedConnectionQueue.size() == MAX_POOL_CAPACITY) {
            try {
                Timestamp waitStartTimeStamp = blockingQueueSynchonizer.lastSyncTimeStamp();
                System.out.println("Wait Called on Incoming Thread!");
                this.wait();
                if(waitStartTimeStamp.before(blockingQueueSynchonizer.lastSyncTimeStamp())) {
                    this.notify();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(this.idleConnectionQueue.isEmpty()){
            connection = (DBConnection) SimpleDBConnectionFactory.getFactory().createConnection();
            connection.setConnectionState(ConnectionStateEnum.ACTIVE);
            this.usedConnectionQueue.add(connection);
            return connection;
        }
        connection = (DBConnection) this.idleConnectionQueue.poll();
        connection.setConnectionState(ConnectionStateEnum.ACTIVE);
        return connection;
    }

    @Override
    public synchronized void releaseConnection(Connection connection) {
        if(this.usedConnectionQueue.contains(connection)){
            connection.setConnectionState(ConnectionStateEnum.IDLE);
        }
        if(this.usedConnectionQueue.size() == MAX_POOL_CAPACITY) {
            this.usedConnectionQueue.remove(connection);
            System.out.println("Connection is released back to Idle, notifyAll() Called");
            notifyAll();
        }
    }

    @Override
    public synchronized void discardConnection(Connection connection) {
        connection.setConnectionState(ConnectionStateEnum.DISCARDED);
        try {
            ((DBConnection) connection).getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
