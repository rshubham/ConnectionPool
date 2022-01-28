package com.connectionPool.connectionPools;

import com.connectionPool.configurations.ConnectionConfig;
import com.connectionPool.connections.Connection;
import com.connectionPool.connections.dbConnections.DBConnection;
import com.connectionPool.enums.ConnectionPoolEnum;
import com.connectionPool.enums.ConnectionStateEnum;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public abstract class ConnectionPool {

    private BlockingQueue<Connection> usedConnectionQueue;
    private Integer MAX_POOL_CAPACITY;
    private Integer MAX_IDLE_TTL;
    private Timestamp idleQueueTimestamp;

    public ConnectionPool(){

        String MAX_POOL_CAPACITY = ConnectionConfig.getConfig().getConfig(ConnectionPoolEnum.MAX_POOL_CAPACITY.getValue());
        if(MAX_POOL_CAPACITY == null)
            this.setMAX_POOL_CAPACITY(Integer.parseInt(ConnectionPoolEnum.DEFAULT_MAX_POOL_CAPACITY.getValue()));
        else
            this.setMAX_POOL_CAPACITY(Integer.parseInt(ConnectionConfig.getConfig().getConfig(ConnectionPoolEnum.MAX_POOL_CAPACITY.getValue())));

        String MAX_IDLE_TTL = ConnectionConfig.getConfig().getConfig(ConnectionPoolEnum.MAX_IDLE_TIMEOUT.getValue());
        if(MAX_IDLE_TTL == null)
            this.setMAX_IDLE_TTL(Integer.parseInt(ConnectionPoolEnum.DEFAULT_MAX_IDLE_TIMEOUT.getValue()));
        else
            this.setMAX_IDLE_TTL(Integer.parseInt(ConnectionConfig.getConfig().getConfig(ConnectionPoolEnum.MAX_IDLE_TIMEOUT.getValue())));


    }


    public abstract Connection getConnection();

    public synchronized void discardConnection(Connection connection) {
        connection.setConnectionState(ConnectionStateEnum.DISCARDED);
        try {
            ((DBConnection) connection).getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

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


    public synchronized void discardAllConnectionsPostTimeOut() {
        // wait for IdleStartTime
        Timestamp currTimestamp = new Timestamp(System.currentTimeMillis());
        if(getIdleQueueTimestamp().getTime() - currTimestamp.getTime() > getMAX_IDLE_TTL()){
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

    // Getters

    public BlockingQueue<Connection> getUsedConnectionQueue() {
        return usedConnectionQueue;
    }

    public void setUsedConnectionQueue(BlockingQueue<Connection> usedConnectionQueue) {
        this.usedConnectionQueue = usedConnectionQueue;
    }

    public Integer getMaxPoolCapacity() {
        return MAX_POOL_CAPACITY;
    }

    public Integer getMAX_IDLE_TTL() {
        return MAX_IDLE_TTL;
    }

    public Timestamp getIdleQueueTimestamp() {
        if(this.idleQueueTimestamp == null) this.idleQueueTimestamp = new Timestamp(System.currentTimeMillis());
        return idleQueueTimestamp;
    }

    public void setIdleQueueTimestamp() {
        List<Connection> list = new ArrayList<>(this.usedConnectionQueue);
        boolean areAllIdle = true;
        for(Connection con : list){
            areAllIdle &= (con.getConnectionState() == ConnectionStateEnum.IDLE)? true : false;
        }
        this.idleQueueTimestamp = areAllIdle ? new Timestamp(System.currentTimeMillis()) : this.idleQueueTimestamp;
    }

    public synchronized void releaseConnection(Connection connection) {
        System.out.println("Inside releaseConnection() => Releasing Connection back to Pool");
        if(this.getUsedConnectionQueue().contains(connection)){
            connection.setConnectionState(ConnectionStateEnum.IDLE);
            this.notifyAll();
        }
        this.setIdleQueueTimestamp();
        this.discardAllConnectionsPostTimeOut();
    }

    public void setMAX_POOL_CAPACITY(Integer MAX_POOL_CAPACITY) {
        this.MAX_POOL_CAPACITY = MAX_POOL_CAPACITY;
    }

    public void setMAX_IDLE_TTL(Integer MAX_IDLE_TTL) {
        this.MAX_IDLE_TTL = MAX_IDLE_TTL;
    }
}
