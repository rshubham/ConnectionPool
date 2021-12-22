package com.connectionPool.connections.dbConnections;

import com.connectionPool.enums.ConnectionStateEnum;

import java.sql.Connection;

public class SSHDBConnection implements DBConnection {
    @Override
    public void connect() {

    }

    @Override
    public void setConnectionState(ConnectionStateEnum connectionState) {

    }

    @Override
    public ConnectionStateEnum getConnectionState() {
        return null;
    }

    @Override
    public Connection getConnection() {
        return null;
    }
}
