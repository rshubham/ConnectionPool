package com.connectionPool.connectionPools;

import com.connectionPool.connectionFactories.SSHConnectionFactory;
import com.connectionPool.connections.Connection;
import com.connectionPool.connections.sshConnections.SSHConnection;
import com.connectionPool.connections.sshConnections.SimpleSSHConnection;
import com.connectionPool.enums.ConnectionPoolEnum;
import com.connectionPool.enums.ConnectionStateEnum;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;

import java.util.concurrent.PriorityBlockingQueue;

public class SimpleSSHConnectionPool extends ConnectionPool{

    private static Integer MAX_CAPACITY;
    private Session session;

    /* ------------------ Singleton Code -------------------- */

    private SimpleSSHConnectionPool(){
        this(null);
    }

    private SimpleSSHConnectionPool(Integer maxCapacity){
        super();
        this.setMAX_POOL_CAPACITY(maxCapacity);
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

    private static class SSHConnectionPoolHelper{
        private static SimpleSSHConnectionPool instance = new SimpleSSHConnectionPool(MAX_CAPACITY);
    }

    public static SimpleSSHConnectionPool getConnectionPool(Integer maxCapacity){
        MAX_CAPACITY = maxCapacity;
        return SimpleSSHConnectionPool.SSHConnectionPoolHelper.instance;
    }

    /* ----------------------------------------------------- */


    @Override
    public synchronized SSHConnection getConnection() {

        System.out.println("Inside SimpleSSHConnectionPool => getConnection()..");
        SSHConnection connection;
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
            /*connection = (DBConnection) SimpleDBConnectionFactory.getFactory().createConnection();*/
            SSHConnectionFactory sshConnectionFactory = SSHConnectionFactory.getFactory();
            connection = (SSHConnection) sshConnectionFactory.createConnection();
            //connection.connect();
            session = connection.getConnection();
            connection.setConnectionState(ConnectionStateEnum.ACTIVE);
            this.getUsedConnectionQueue().add(connection);
            return connection;
        }
        System.out.println("fetching connection from pool..");
        connection = (SimpleSSHConnection) this.getUsedConnectionQueue().peek();
        connection.setConnectionState(ConnectionStateEnum.ACTIVE);
        return connection;
    }

    @Override
    public synchronized void releaseConnection(Connection connection) {
        SimpleSSHConnection connection1 = (SimpleSSHConnection) connection;
        Channel channel = connection1.getConnectedChannel();
        if(channel != null){
            channel.disconnect();
        }
        super.releaseConnection(connection);
    }

    public boolean isConnectionAvailableForUse(){
        if (!this.getUsedConnectionQueue().isEmpty() && this.getUsedConnectionQueue().peek().getConnectionState() == ConnectionStateEnum.IDLE){
            this.notifyAll();
            return true;
        }
        return false;
    }

}
