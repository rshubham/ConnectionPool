package com.connectionPool.connections.dbConnections;

import com.connectionPool.connections.Connection;

public interface DBConnection extends Connection {
    public java.sql.Connection getConnection();

}
