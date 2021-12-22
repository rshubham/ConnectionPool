package com.connectionPool.connectionPools;

import com.connectionPool.connections.Connection;

public interface ConnectionPool {
    public Connection getConnection();
    public void releaseConnection(Connection connection);
    public void discardConnection(Connection connection);
}
