package com.connectionPool.configurations;

import com.connectionPool.enums.ConnectionConfigEnum;
import com.connectionPool.enums.SSHConnectionFactoryEnum;

import java.io.*;
import java.util.*;

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

    public Hashtable<String,String> getPrefixedConfigs(String prefix){
        Hashtable<String,String> hashtable = new Hashtable<>();
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classloader.getResourceAsStream(ConnectionConfigEnum.CONFIG_FILE.getValue());
            this.connectionProperties.load(inputStream);
            for(Map.Entry m : this.connectionProperties.entrySet()){
                String key = (String) m.getKey();
                if(key.contains(prefix)){
                    hashtable.put(key.substring(key.lastIndexOf('.')+1),(String) m.getValue());
                }
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hashtable;
    }


}
