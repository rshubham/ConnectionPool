package com.connectionPool.configurations;

import com.connectionPool.enums.ConnectionConfigEnum;

import java.io.*;
import java.util.Properties;

public class ConnectionConfig {

    private Properties connectionProperties;

    /* ------------------ Singleton Code -------------------- */

    private ConnectionConfig(){
        this.connectionProperties = new Properties();
    }

    private static class ConnectionConfigHelper{
        private static ConnectionConfig instance = new ConnectionConfig();
    }

    public static ConnectionConfig getConfig(){
        return ConnectionConfig.ConnectionConfigHelper.instance;
    }

    /* ----------------------------------------------------- */

    public String getConfig(String key){
        String value = "";

        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classloader.getResourceAsStream(ConnectionConfigEnum.CONFIG_FILE.getValue());
            this.connectionProperties.load(inputStream);
            value = this.connectionProperties.getProperty(key);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }
}
