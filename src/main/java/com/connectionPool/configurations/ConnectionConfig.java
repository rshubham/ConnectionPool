package com.connectionPool.configurations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConnectionConfig {

    private Properties connectionProperties;
    private static final String configFilePath = "src/main/resources/connection.properties";

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
            FileReader fileReader = new FileReader(configFilePath);
            this.connectionProperties.load(fileReader);
            value = this.connectionProperties.getProperty(key);
            fileReader.close();
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }
}
