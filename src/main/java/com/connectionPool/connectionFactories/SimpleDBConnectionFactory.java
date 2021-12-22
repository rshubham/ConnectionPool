package com.connectionPool.connectionFactories;

import com.connectionPool.configurations.ConnectionConfig;
import com.connectionPool.connections.Connection;
import com.connectionPool.connections.dbConnections.SimpleDBConnection;
import com.connectionPool.enums.ConnectionStateEnum;

public class SimpleDBConnectionFactory implements ConnectionFactory{

    ConnectionConfig config;

    public static final String JDBC_URL = "ConnectionPool.SimpleDBConnection.jdbcURL";
    public static final String DRIVER_URL = "ConnectionPool.SimpleDBConnection.driverURL";
    public static final String USERNAME = "ConnectionPool.SimpleDBConnection.username";
    public static final String PASSWORD = "ConnectionPool.SimpleDBConnection.password";

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
                .setJdbcURL(config.getConfig(JDBC_URL))
                .setDriverURL(config.getConfig(DRIVER_URL))
                .setUserName(config.getConfig(USERNAME))
                .setPassword(config.getConfig(PASSWORD))
                .setConnectionState(ConnectionStateEnum.OPEN)
                .build();
        connection.connect();
        return connection;

    }
}
