package com.connectionPool.observers;

import com.connectionPool.connections.Connection;
import com.connectionPool.enums.ConnectionStateEnum;

import java.sql.Timestamp;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueSynchronizer implements Runnable {

    private BlockingQueue<Connection> idleQueue;
    private BlockingQueue<Connection> usedQueue;
    private Integer maxCapacity;
    private boolean interuptSignal = false;
    private Timestamp lastSyncTimeStamp;

    public BlockingQueueSynchronizer(BlockingQueue<Connection> idleQueue, BlockingQueue<Connection> usedQueue, Integer maxCapacity){
        this.idleQueue = idleQueue;
        this.usedQueue = usedQueue;
        this.maxCapacity = maxCapacity;
    }

    @Override
    public void run() {
        if(this.usedQueue.size() == this.maxCapacity){
            lastSyncTimeStamp = new Timestamp(System.currentTimeMillis());
            while(!this.usedQueue.isEmpty() && this.usedQueue.peek().getConnectionState() == ConnectionStateEnum.IDLE) {
                this.idleQueue.offer(this.usedQueue.poll());
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
