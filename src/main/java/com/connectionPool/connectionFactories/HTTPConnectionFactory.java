package com.connectionPool.connectionFactories;

import com.connectionPool.connections.Connection;
import com.connectionPool.connections.HTTPConnection;

public class HTTPConnectionFactory implements ConnectionFactory {
    /* ------------------ Singleton Code -------------------- */

    private HTTPConnectionFactory(){}

    private static class HTTPConnectionFactoryHelper{
        private static HTTPConnectionFactory instance = new HTTPConnectionFactory();
    }

    public static HTTPConnectionFactory getFactory(){
        return HTTPConnectionFactory.HTTPConnectionFactoryHelper.instance;
    }

    /* ----------------------------------------------------- */

    @Override
    public Connection createConnection() {
        return new HTTPConnection();
    }
}
