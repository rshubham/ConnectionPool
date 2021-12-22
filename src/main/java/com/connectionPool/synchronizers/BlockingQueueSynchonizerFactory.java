package com.connectionPool.synchronizers;

import com.connectionPool.connectionPools.ConnectionPool;

public class BlockingQueueSynchonizerFactory {

    public static BlockingQueueSynchronizer createBlockingQueueSynchronizer(ConnectionPool connectionPool){
        return new BlockingQueueSynchronizer(connectionPool);
    }

}
