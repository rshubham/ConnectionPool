package com.connectionPool.enums;

public enum ConnectionStateEnum {
    OPEN("Open Status"), ACTIVE("Active Status"), IDLE("Idle Status"), DISCARDED("Discarded Status");

    private String val;
    ConnectionStateEnum(String val){this.val = val;}

    public String getValue(){return this.val;}

}
