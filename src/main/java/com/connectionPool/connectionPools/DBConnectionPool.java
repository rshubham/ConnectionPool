package com.connectionPool.connectionPools;

import com.connectionPool.connections.dbConnections.DBConnection;

public interface DBConnectionPool extends ConnectionPool {
    @Override
    public DBConnection getConnection();
}
