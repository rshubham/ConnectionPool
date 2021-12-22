package com.connectionPool.connections;

import com.connectionPool.enums.ConnectionStateEnum;

public interface Connection {
    public void connect();
    public void setConnectionState(ConnectionStateEnum connectionState);
    public ConnectionStateEnum getConnectionState();
}
