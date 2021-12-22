package com.connectionPool.connectionFactories;

import com.connectionPool.connections.Connection;
import com.connectionPool.connections.dbConnections.SSHDBConnection;

public class SSHDBConectionFactory implements ConnectionFactory{
    /* ------------------ Singleton Code -------------------- */

    private SSHDBConectionFactory(){}

    private static class SSHDBConectionFactoryHelper{
        private static SSHDBConectionFactory instance = new SSHDBConectionFactory();
    }

    public static SSHDBConectionFactory getFactory(){
        return SSHDBConectionFactory.SSHDBConectionFactoryHelper.instance;
    }

    /* ----------------------------------------------------- */

    @Override
    public Connection createConnection() {
        return new SSHDBConnection();
    }
}
