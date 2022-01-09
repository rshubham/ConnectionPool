package com.connectionPool.connectionFactories;

import com.connectionPool.configurations.ConnectionConfig;
import com.connectionPool.connections.Connection;
import com.connectionPool.connections.sshConnections.SimpleSSHConnection;
import com.connectionPool.enums.SSHConnectionFactoryEnum;
import java.util.Hashtable;

public class SSHConnectionFactory implements ConnectionFactory{

    ConnectionConfig config;

    /* ------------------ Singleton Code -------------------- */

    private SSHConnectionFactory(){this.config = ConnectionConfig.getConfig();}

    private static class SSHConnectionFactoryHelper{
        private static SSHConnectionFactory instance = new SSHConnectionFactory();
    }

    public static SSHConnectionFactory getFactory(){
        return SSHConnectionFactoryHelper.instance;
    }

    /* ----------------------------------------------------- */

    @Override
    public Connection createConnection() {
        Connection connection = new SimpleSSHConnection.SSHConnectionBuilder()
                                .setHost(this.config.getConfig(SSHConnectionFactoryEnum.HOST.getValue()))
                                .setPort((this.config.getConfig(SSHConnectionFactoryEnum.PORT.getValue()) == null)?null:Integer.parseInt(this.config.getConfig(SSHConnectionFactoryEnum.PORT.getValue())))
                                .setUsername(this.config.getConfig(SSHConnectionFactoryEnum.USERNAME.getValue()))
                                .setPrivateKey(this.config.getConfig(SSHConnectionFactoryEnum.PK.getValue()))
                                .setPrivateKeypassPhrase(this.config.getConfig(SSHConnectionFactoryEnum.PK_PASS_PHRASE.getValue()))
                                .setConnectionWaitTimeOut(Integer.parseInt(this.config.getConfig(SSHConnectionFactoryEnum.CONNECTION_WAIT_TIMEOUT.getValue())))
                                .setSessionConfig(this.config.getPrefixedConfigs(SSHConnectionFactoryEnum.SESSION_CONFIGS.getValue()))
                                .build();
        connection.connect();
        return connection;
    }


}
