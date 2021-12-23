package com.connectionPool.clients;

import com.connectionPool.connectionPools.SimpleJDBCConnectionPool;
import com.connectionPool.connections.dbConnections.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ClientProcessor implements Callable {

    private String query;
    private List<Map<String,String>> resultSetList;

    public ClientProcessor(String query) {
        this.query = query;
        this.resultSetList = new ArrayList<Map<String, String>>();
    }

    public String getQuery() {
        return query;
    }

    @Override
    public Object call() throws Exception {

        // Create Connection pool
        System.out.println("Getting hold of Connection object..");
        DBConnection connection = SimpleJDBCConnectionPool.getConnectionPool().getConnection();
        System.out.println("Getting hold of java.sql.Connection object..");
        Connection dbConnection = connection.getConnection();
        System.out.println("Got java.sql.Connection object..");
        // Create Statement Object
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(getQuery());
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            Map<String,String> resultMap = new HashMap<String,String>();

            while(rs.next()){
                int i = 1;
                while(i <= resultSetMetaData.getColumnCount()){
                    resultMap.put(resultSetMetaData.getColumnName(i), rs.getString(resultSetMetaData.getColumnName(i)));
                    i++;
                }
                this.resultSetList.add(resultMap);
                resultMap = new HashMap<String, String>();
            }
            Thread.sleep(6000);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            SimpleJDBCConnectionPool.getConnectionPool().releaseConnection(connection);

        }

        return this.resultSetList;
    }

    public FutureTask processQueryWithNewConnection(){
        return new FutureTask(this);
    }


}
