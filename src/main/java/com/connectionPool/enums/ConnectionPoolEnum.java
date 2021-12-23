package com.connectionPool.enums;

public enum ConnectionPoolEnum {

    /* ----------------------- Define Enums ---------------------------*/

    MAX_POOL_CAPACITY("MAX_POOL_CAPACITY"),
    MAX_IDLE_TIMEOUT("MAX_IDLE_TIMEOUT"),
    DEFAULT_MAX_POOL_CAPACITY("10"),
    DEFAULT_MAX_IDLE_TIMEOUT("300000");

    /* ----------------------- Constructor and Methods ---------------------------*/

    private String val;
    ConnectionPoolEnum(String val){this.val = val;}

    public String getValue(){return this.val;}

    /* --------------------------------------------------------------------------*/

}
