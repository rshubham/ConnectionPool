package com.connectionPool.connectionPools;

import com.connectionPool.connections.sshConnections.SSHConnection;

public abstract class SSHConnectionPool extends ConnectionPool{
    @Override
    public abstract SSHConnection getConnection();
}
