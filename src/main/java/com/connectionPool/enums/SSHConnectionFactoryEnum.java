package com.connectionPool.enums;

import java.util.Hashtable;

public enum SSHConnectionFactoryEnum {

    /* ----------------------- Define Enums ---------------------------*/

    HOST("ConnectionPool.SimpleSSHConnection.host"),
    PORT("ConnectionPool.SimpleSSHConnection.port"),
    USERNAME("ConnectionPool.SimpleSSHConnection.username"),
    PK("ConnectionPool.SimpleSSHConnection.privateKey"),
    PK_PASS_PHRASE("ConnectionPool.SimpleSSHConnection.privateKeyPass"),
    CONNECTION_WAIT_TIMEOUT("ConnectionPool.SimpleSSHConnection.connectionWaitTimeOut"),
    SESSION_CONFIGS("ConnectionPool.SimpleSSHConnection.sessionConfig.");

    /* ----------------------- Constructor and Methods ---------------------------*/

    private String val;
    SSHConnectionFactoryEnum(String val){this.val = val;}

    public String getValue(){return this.val;}

    /* --------------------------------------------------------------------------*/

}
