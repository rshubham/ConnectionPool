package com.connectionPool.connectionPools;

import com.connectionPool.configurations.ConnectionConfig;
import com.connectionPool.connections.Connection;
import com.connectionPool.enums.ConnectionStateEnum;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.concurrent.*;

public abstract class ConnectionPool {

    private BlockingQueue<Connection> usedConnectionQueue;
    private Timestamp waitStartTimeStamp;
    private Integer MAX_POOL_CAPACITY = Integer.parseInt(ConnectionConfig.getConfig().getConfig("MAX_POOL_CAPACITY"));

    ConnectionPool(){
        this.usedConnectionQueue = new PriorityBlockingQueue<>(MAX_POOL_CAPACITY, new Comparator<Connection>() {
            @Override
            public int compare(Connection o1, Connection o2) {
                if(o1.getConnectionState() == ConnectionStateEnum.IDLE && o2.getConnectionState() == ConnectionStateEnum.ACTIVE) return -1;
                if((o1.getConnectionState() == ConnectionStateEnum.IDLE && o2.getConnectionState() == ConnectionStateEnum.IDLE)
                        || ((o1.getConnectionState() == ConnectionStateEnum.ACTIVE && o2.getConnectionState() == ConnectionStateEnum.ACTIVE)))
                {
                    return 0;
                }
                return 1;
            }
        });
    }


    public abstract Connection getConnection();
    public abstract void releaseConnection(Connection connection);
    public abstract void discardConnection(Connection connection);
    public abstract void discardAllConnections();

    // Getters
    public Timestamp getWaitStartTimeStamp() {
        return waitStartTimeStamp;
    }

    public void setWaitStartTimeStamp(Timestamp waitStartTimeStamp) {
        this.waitStartTimeStamp = waitStartTimeStamp;
    }

    public BlockingQueue<Connection> getUsedConnectionQueue() {
        return usedConnectionQueue;
    }

    public void setUsedConnectionQueue(BlockingQueue<Connection> usedConnectionQueue) {
        this.usedConnectionQueue = usedConnectionQueue;
    }

    public Integer getMaxPoolCapacity() {
        return MAX_POOL_CAPACITY;
    }

    public void setMAX_POOL_CAPACITY(Integer MAX_POOL_CAPACITY) {
        this.MAX_POOL_CAPACITY = MAX_POOL_CAPACITY;
    }

}
