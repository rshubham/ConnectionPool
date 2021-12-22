package com.connectionPool.connectionPools;

import com.connectionPool.configurations.ConnectionConfig;
import com.connectionPool.connectionFactories.SimpleDBConnectionFactory;
import com.connectionPool.connections.Connection;
import com.connectionPool.connections.dbConnections.DBConnection;
import com.connectionPool.enums.ConnectionStateEnum;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class SimpleJDBCConnectionPool implements DBConnectionPool{

    private BlockingQueue<DBConnection> idleConnectionQueue;
    private BlockingQueue<DBConnection> usedConnectionQueue;
    private static final Integer MAX_POOL_CAPACITY = Integer.parseInt(ConnectionConfig.getConfig().getConfig("MAX_POOL_CAPACITY"));


    /* ------------------ Singleton Code -------------------- */

    private SimpleJDBCConnectionPool(){
        this.idleConnectionQueue = new ArrayBlockingQueue<DBConnection>(MAX_POOL_CAPACITY);
        this.usedConnectionQueue = new ArrayBlockingQueue<DBConnection>(MAX_POOL_CAPACITY);
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
                System.out.println("Wait Called on Incoming Thread!");
                wait();
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
        connection = this.idleConnectionQueue.poll();
        connection.setConnectionState(ConnectionStateEnum.ACTIVE);
        return connection;
    }

    @Override
    public synchronized void releaseConnection(Connection connection) {
        if(this.usedConnectionQueue.contains(connection)){
            connection.setConnectionState(ConnectionStateEnum.IDLE);
        }
        this.idleConnectionQueue.add((DBConnection)connection);
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
