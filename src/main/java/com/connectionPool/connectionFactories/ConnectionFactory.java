package com.connectionPool.connectionFactories;

import com.connectionPool.connections.Connection;

public interface ConnectionFactory {
    public Connection createConnection();
}
