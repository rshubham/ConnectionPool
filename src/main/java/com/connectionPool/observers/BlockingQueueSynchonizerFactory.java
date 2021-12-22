package com.connectionPool.observers;

import com.connectionPool.connectionPools.ConnectionPool;
import com.connectionPool.connections.Connection;

import java.util.concurrent.BlockingQueue;

public class BlockingQueueSynchonizerFactory {

    public static BlockingQueueSynchronizer createBlockingQueueSynchronizer(BlockingQueue<Connection> idleQueue, BlockingQueue<Connection> usedQueue, Integer maxCapacity){
        return new BlockingQueueSynchronizer(idleQueue,usedQueue,maxCapacity);
    }

}
