package com.connectionPool.connectionFactories;

import com.connectionPool.configurations.ConnectionConfig;
import com.connectionPool.connections.Connection;
import com.connectionPool.connections.dbConnections.SimpleDBConnection;
import com.connectionPool.enums.ConnectionStateEnum;
import com.connectionPool.enums.SimpleDBConnectionFactoryEnum;

public class SimpleDBConnectionFactory implements ConnectionFactory{

    ConnectionConfig config;

    /* ------------------ Singleton Code -------------------- */

    private SimpleDBConnectionFactory(){
        this.config = ConnectionConfig.getConfig();
    }

    private static class SimpleDBConnectionFactoryHelper{
        private static SimpleDBConnectionFactory instance = new SimpleDBConnectionFactory();
    }

    public static SimpleDBConnectionFactory getFactory(){
        return SimpleDBConnectionFactory.SimpleDBConnectionFactoryHelper.instance;
    }

    /* ----------------------------------------------------- */

    @Override
    public Connection createConnection() {
        Connection connection = new SimpleDBConnection.SimpleDBConnectionBuilder()
                .setJdbcURL(config.getConfig(SimpleDBConnectionFactoryEnum.JDBC_URL.getValue()))
                .setDriverURL(config.getConfig(SimpleDBConnectionFactoryEnum.DRIVER_URL.getValue()))
                .setUserName(config.getConfig(SimpleDBConnectionFactoryEnum.USERNAME.getValue()))
                .setPassword(config.getConfig(SimpleDBConnectionFactoryEnum.PASSWORD.getValue()))
                .setConnectionState(ConnectionStateEnum.OPEN)
                .build();
        connection.connect();
        return connection;

    }
}
