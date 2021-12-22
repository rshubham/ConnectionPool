package com.connectionPool.connectionPools;

import com.connectionPool.connections.dbConnections.DBConnection;

public abstract class DBConnectionPool extends ConnectionPool {
    @Override
    public abstract DBConnection getConnection();
}
