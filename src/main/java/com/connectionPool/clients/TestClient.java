package com.connectionPool.clients;

import com.connectionPool.configurations.ConnectionConfig;
import com.connectionPool.connectionPools.DBConnectionPool;
import com.connectionPool.connectionPools.SimpleJDBCConnectionPool;
import com.connectionPool.enums.ConnectionConfigEnum;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class TestClient {

    public static void main(String[] args){

        System.out.println("Getting hold of ConnectionPool object..");
        DBConnectionPool dbConnectionPool = SimpleJDBCConnectionPool.getConnectionPool();

        System.out.println("Getting hold of java.sql.Connection object..");
        java.sql.Connection connection = dbConnectionPool.getConnection().getConnection();

        System.out.println("Executing Query using Connection object..");
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from employees where rownum <=1");
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            System.out.println("Column Count : " + resultSetMetaData.getColumnCount());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
