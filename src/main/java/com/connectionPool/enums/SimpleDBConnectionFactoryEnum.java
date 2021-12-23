package com.connectionPool.enums;

public enum SimpleDBConnectionFactoryEnum {
    /* ----------------------- Define Enums ---------------------------*/

    JDBC_URL("ConnectionPool.SimpleDBConnection.jdbcURL"),
    DRIVER_URL("ConnectionPool.SimpleDBConnection.driverURL"),
    USERNAME("ConnectionPool.SimpleDBConnection.username"),
    PASSWORD("ConnectionPool.SimpleDBConnection.password");

    /* ----------------------- Constructor and Methods ---------------------------*/

    private String val;
    SimpleDBConnectionFactoryEnum(String val){this.val = val;}

    public String getValue(){return this.val;}

    /* --------------------------------------------------------------------------*/

}
