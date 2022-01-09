package com.connectionPool.connectionPools;

import com.connectionPool.connectionFactories.SimpleDBConnectionFactory;
import com.connectionPool.connections.Connection;
import com.connectionPool.connections.dbConnections.DBConnection;
import com.connectionPool.enums.ConnectionPoolEnum;
import com.connectionPool.enums.ConnectionStateEnum;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;


public class SimpleJDBCConnectionPool extends DBConnectionPool{

    /* ------------------ Singleton Code -------------------- */

    private SimpleJDBCConnectionPool(){
        super();
        if(this.getMaxPoolCapacity() == null) this.setMAX_POOL_CAPACITY(Integer.parseInt(ConnectionPoolEnum.DEFAULT_MAX_POOL_CAPACITY.getValue()));
        this.setUsedConnectionQueue(new PriorityBlockingQueue<>(this.getMaxPoolCapacity(), (o1, o2) -> {
                if(o1.getConnectionState() == ConnectionStateEnum.IDLE && o2.getConnectionState() == ConnectionStateEnum.ACTIVE) return -1;
                if((o1.getConnectionState() == ConnectionStateEnum.IDLE && o2.getConnectionState() == ConnectionStateEnum.IDLE)
                        || ((o1.getConnectionState() == ConnectionStateEnum.ACTIVE && o2.getConnectionState() == ConnectionStateEnum.ACTIVE)))
                {
                    return 0;
                }
                return 1;
        }));
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
        System.out.println("Used BlockingQueue current Size : " + this.getUsedConnectionQueue().size());
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


    public boolean isConnectionAvailableForUse(){
        if (!this.getUsedConnectionQueue().isEmpty() && this.getUsedConnectionQueue().peek().getConnectionState() == ConnectionStateEnum.IDLE){
            this.notifyAll();
            return true;
        }
        return false;
    }

}
