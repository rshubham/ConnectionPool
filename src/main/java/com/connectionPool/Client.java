package com.connectionPool;

import com.connectionPool.connectionPools.SimpleJDBCConnectionPool;
import com.connectionPool.connections.dbConnections.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {

    public static void main(String[] args){
        // Create Connection pool
        DBConnection connection = SimpleJDBCConnectionPool.getFactory().getConnection();
        Connection dbConnection = connection.getConnection();
        // Create Statement Object
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from EMPLOYEES where rownum <= 1");
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            System.out.println("Column Count : " + resultSetMetaData.getColumnCount());
            Map<String,String> resultMap = new HashMap<String,String>();
            List<Map<String,String>> resultSetList = new ArrayList<Map<String, String>>();

            while(rs.next()){
                int i = 1;
                while(i <= resultSetMetaData.getColumnCount()){
                    resultMap.put(resultSetMetaData.getColumnName(i), rs.getString(resultSetMetaData.getColumnName(i)));
                    System.out.println(resultSetMetaData.getColumnName(i) + " : " + rs.getString(resultSetMetaData.getColumnName(i)));
                    i++;
                }
                resultSetList.add(resultMap);
                resultMap = new HashMap<String, String>();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            SimpleJDBCConnectionPool.getFactory().releaseConnection(connection);
        }
    }



}
