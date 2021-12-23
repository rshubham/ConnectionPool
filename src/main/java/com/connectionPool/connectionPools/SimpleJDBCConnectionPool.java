package com.connectionPool.connectionPools;

import com.connectionPool.connectionFactories.SimpleDBConnectionFactory;
import com.connectionPool.connections.Connection;
import com.connectionPool.connections.dbConnections.DBConnection;
import com.connectionPool.enums.ConnectionStateEnum;
import java.sql.SQLException;



public class SimpleJDBCConnectionPool extends DBConnectionPool{

    /* ------------------ Singleton Code -------------------- */

    private SimpleJDBCConnectionPool(){
        super();
    }

    private static class SimpleJDBCConnectionPoolHelper{
        private static DBConnectionPool instance = new SimpleJDBCConnectionPool();
    }

    public static DBConnectionPool getConnectionPool(){
        return SimpleJDBCConnectionPool.SimpleJDBCConnectionPoolHelper.instance;
    }

    /* ----------------------------------------------------- */

    @Override
    public synchronized DBConnection getConnection() {
        System.out.println("Inside SimpleJDBCConnectionPool => getConnection()..");
        DBConnection connection;
        if(this.getUsedConnectionQueue().size() == this.getMaxPoolCapacity() && !isConnectionAvailableForUse()) {
            System.out.println("Used BlockingQueue is Full => going to wait.. usedQueue size : "+ this.getUsedConnectionQueue().size());
            try {
                System.out.println("Wait Called on Incoming Thread!");
                this.wait();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(" Used BlockingQueue current Size : " + this.getUsedConnectionQueue().size());
        if(this.getUsedConnectionQueue().size() < this.getMaxPoolCapacity() && !isConnectionAvailableForUse()){
            System.out.println("Used BlockingQueue is not full => creating and caching new Connection..");
            connection = (DBConnection) SimpleDBConnectionFactory.getFactory().createConnection();
            connection.setConnectionState(ConnectionStateEnum.ACTIVE);
            this.getUsedConnectionQueue().add(connection);
            return connection;
        }
        System.out.println("fetching connection from pool..");
        connection = (DBConnection) this.getUsedConnectionQueue().peek();
        connection.setConnectionState(ConnectionStateEnum.ACTIVE);
        return connection;
    }

    @Override
    public synchronized void releaseConnection(Connection connection) {
        System.out.println("Inside releaseConnection() => Releasing Connection back to Pool");
        if(this.getUsedConnectionQueue().contains(connection)){
            connection.setConnectionState(ConnectionStateEnum.IDLE);
        }
        if(this.getUsedConnectionQueue().size() == this.getMaxPoolCapacity()) {
            System.out.println("Connection is released and State changed to Idle, notifyAll() Called");
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

    @Override
    public synchronized void discardAllConnections() {
        System.out.println("Inside discardAllConnections() => Discarding Connections back to Pool");
        System.out.println("usedQueue size : " + this.getUsedConnectionQueue().size());
        if(this.getUsedConnectionQueue().size() == this.getMaxPoolCapacity()){
            while(!this.getUsedConnectionQueue().isEmpty()){
                DBConnection connection = (DBConnection) this.getUsedConnectionQueue().poll();
                try {
                    connection.getConnection().close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }


    public boolean isConnectionAvailableForUse(){

        if (!this.getUsedConnectionQueue().isEmpty() && this.getUsedConnectionQueue().peek().getConnectionState() == ConnectionStateEnum.IDLE){
            this.notifyAll();
            return true;
        }
        return false;
    }

}
