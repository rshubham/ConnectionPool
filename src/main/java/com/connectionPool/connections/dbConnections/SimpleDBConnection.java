package com.connectionPool.connections.dbConnections;

import com.connectionPool.enums.ConnectionStateEnum;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class SimpleDBConnection implements DBConnection {

    /*----- Connection Attributes ------*/

    private String userName;
    private String password;
    private String jdbcURL;
    private String dbName;
    private String driverURL;
    private Connection connection;
    private ConnectionStateEnum connectionState;

    /*----------------------------------*/

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getJdbcURL() {
        return jdbcURL;
    }

    public String getDriverURL() {
        return driverURL;
    }

    public String getDbName() {
        return dbName;
    }

    @Override
    public ConnectionStateEnum getConnectionState() {
        return this.connectionState;
    }

    @Override
    public void connect(){

        /*--- Create a JDBC Connection -----*/
        try {
            // load driver class
            Class.forName(getDriverURL());
            //DriverManager.registerDriver(new mysql.);
            //Creation Connection Object
            this.connection = DriverManager.getConnection(getJdbcURL(),getUserName(),getPassword());
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void setConnectionState(ConnectionStateEnum connectionState) {
        this.connectionState = connectionState;
    }

    @Override
    public Connection getConnection(){
        return this.connection;
    }

    private SimpleDBConnection(SimpleDBConnectionBuilder builder){
        this.userName = builder.userName;
        this.password = builder.password;
        this.dbName = builder.dbName;
        this.driverURL = builder.driverURL;
        this.jdbcURL = builder.jdbcURL;
        this.connectionState = builder.connectionState;
    }

    /*-------------- Builder Inner Class --------------------*/
    public static class SimpleDBConnectionBuilder{

        private String userName;
        private String password;
        private String jdbcURL;
        private String dbName;
        private String driverURL;
        private ConnectionStateEnum connectionState;

        public SimpleDBConnectionBuilder(){}

        public SimpleDBConnectionBuilder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public SimpleDBConnectionBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public SimpleDBConnectionBuilder setJdbcURL(String jdbcURL) {
            this.jdbcURL = jdbcURL;
            return this;
        }

        public SimpleDBConnectionBuilder setDriverURL(String driverURL) {
            this.driverURL = driverURL;
            return this;
        }

        public SimpleDBConnectionBuilder setDbName(String dbName) {
            this.dbName = dbName;
            return this;
        }

        public SimpleDBConnectionBuilder setConnectionState(ConnectionStateEnum connectionState) {
            this.connectionState = connectionState;
            return this;
        }

        public SimpleDBConnection build() {
            return new SimpleDBConnection(this);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleDBConnection that = (SimpleDBConnection) o;
        return Objects.equals(userName, that.userName) &&
                Objects.equals(password, that.password) &&
                Objects.equals(jdbcURL, that.jdbcURL) &&
                Objects.equals(dbName, that.dbName) &&
                Objects.equals(driverURL, that.driverURL) &&
                Objects.equals(connection, that.connection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, password, jdbcURL, dbName, driverURL, connection);
    }
}
