package com.connectionPool.connectionFactories;

import com.connectionPool.connections.Connection;
import com.connectionPool.connections.SSHConnection;

public class SSHConnectionFactory implements ConnectionFactory{

    /* ------------------ Singleton Code -------------------- */

    private SSHConnectionFactory(){}

    private static class SSHConnectionFactoryHelper{
        private static SSHConnectionFactory instance = new SSHConnectionFactory();
    }

    public static SSHConnectionFactory getFactory(){
        return SSHConnectionFactoryHelper.instance;
    }

    /* ----------------------------------------------------- */

    @Override
    public Connection createConnection() {
        return new SSHConnection();
    }
}
