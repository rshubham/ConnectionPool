package com.connectionPool.observers;

import com.connectionPool.connectionPools.ConnectionPool;
import com.connectionPool.connections.Connection;

import java.util.concurrent.BlockingQueue;

public class BlockingQueueSynchonizerFactory {

    public static BlockingQueueSynchronizer createBlockingQueueSynchronizer(ConnectionPool connectionPool){
        return new BlockingQueueSynchronizer(connectionPool);
    }

}
