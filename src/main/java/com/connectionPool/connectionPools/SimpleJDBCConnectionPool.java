package com.connectionPool.connectionPools;

import com.connectionPool.connectionFactories.SimpleDBConnectionFactory;
import com.connectionPool.connections.Connection;
import com.connectionPool.connections.dbConnections.DBConnection;
import com.connectionPool.enums.ConnectionStateEnum;
import com.connectionPool.synchronizers.BlockingQueueSynchonizerFactory;
import java.sql.SQLException;


public class SimpleJDBCConnectionPool extends DBConnectionPool{

    /* ------------------ Singleton Code -------------------- */

    private SimpleJDBCConnectionPool(){
        super();
        this.setBlockingQueueSynchonizer(BlockingQueueSynchonizerFactory.createBlockingQueueSynchronizer(this));
        this.getBlockingQueueSynchonizer().triggerSync();
        this.setWaitStartTimeStamp(this.getBlockingQueueSynchonizer().lastSyncTimeStamp());
    }

    private static class SimpleJDBCConnectionPoolHelper{
        private static SimpleJDBCConnectionPool instance = new SimpleJDBCConnectionPool();
    }

    public static SimpleJDBCConnectionPool getConnectionPool(){
        return SimpleJDBCConnectionPool.SimpleJDBCConnectionPoolHelper.instance;
    }

    /* ----------------------------------------------------- */

    @Override
    public synchronized DBConnection getConnection() {
        DBConnection connection;
        if(this.getIdleConnectionQueue().isEmpty() && this.getUsedConnectionQueue().size() == getMaxPoolCapacity()) {
            try {
                this.setWaitStartTimeStamp(this.getBlockingQueueSynchonizer().lastSyncTimeStamp());
                System.out.println("Wait Called on Incoming Thread!");
                this.wait();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(this.getIdleConnectionQueue().isEmpty()){
            connection = (DBConnection) SimpleDBConnectionFactory.getFactory().createConnection();
            connection.setConnectionState(ConnectionStateEnum.ACTIVE);
            this.getUsedConnectionQueue().add(connection);
            return connection;
        }
        connection = (DBConnection) this.getIdleConnectionQueue().poll();
        connection.setConnectionState(ConnectionStateEnum.ACTIVE);
        return connection;
    }

    @Override
    public synchronized void releaseConnection(Connection connection) {
        if(this.getUsedConnectionQueue().contains(connection)){
            connection.setConnectionState(ConnectionStateEnum.IDLE);
        }
        if(this.getUsedConnectionQueue().size() == this.getMaxPoolCapacity()) {
            this.getUsedConnectionQueue().remove(connection);
            System.out.println("Connection is released back to Idle, notifyAll() Called");
            this.notifyAll();
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
