package com.connectionPool.connections.sshConnections;

import com.connectionPool.connections.Connection;
import com.jcraft.jsch.Session;

public interface SSHConnection  extends Connection {
    public Session getConnection();
}
