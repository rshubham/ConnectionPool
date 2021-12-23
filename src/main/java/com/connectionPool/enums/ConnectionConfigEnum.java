package com.connectionPool.enums;

public enum ConnectionConfigEnum {

    /* ----------------------- Define Enums ---------------------------*/

    CONFIG_FILE("connection.properties");

    /* ----------------------- Constructor and Methods ---------------------------*/

    private String val;
    ConnectionConfigEnum(String val){this.val = val;}

    public String getValue(){return this.val;}

    /* --------------------------------------------------------------------------*/

}
