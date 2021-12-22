package com.connectionPool.observers;

import com.connectionPool.connectionPools.ConnectionPool;
import com.connectionPool.connections.Connection;
import com.connectionPool.enums.ConnectionStateEnum;

import java.sql.Timestamp;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueSynchronizer implements Runnable {

    private ConnectionPool connectionPool;
    private boolean interuptSignal = false;
    private Timestamp lastSyncTimeStamp;

    public BlockingQueueSynchronizer(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public void run() {

        BlockingQueue<Connection> idleQueue = this.connectionPool.getIdleConnectionQueue();
        BlockingQueue<Connection> usedQueue = this.connectionPool.getUsedConnectionQueue();

        if(usedQueue.size() == this.connectionPool.getMaxPoolCapacity()){
            lastSyncTimeStamp = new Timestamp(System.currentTimeMillis());
            this.connectionPool.notifyConnectionsAvailability();
            while(!usedQueue.isEmpty() && usedQueue.peek().getConnectionState() == ConnectionStateEnum.IDLE) {
                idleQueue.offer(usedQueue.poll());
            }
        }
    }

    public void interuptSynchronizer(){
        this.interuptSignal = true;
    }

    public Timestamp lastSyncTimeStamp(){
        return lastSyncTimeStamp;
    }

    public void triggerSync() {
        Thread syncProcess = new Thread(this);
        while(!interuptSignal) {
            syncProcess.start();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}
