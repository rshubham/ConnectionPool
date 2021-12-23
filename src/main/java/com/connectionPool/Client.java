package com.connectionPool;

import com.connectionPool.clients.ClientProcessor;
import com.connectionPool.connectionPools.SimpleJDBCConnectionPool;
import com.connectionPool.connections.Connection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Client {

    public static void main(String[] args) throws ExecutionException {

        ClientProcessor employeeProcessor = new ClientProcessor("select * from EMPLOYEES where rownum <= 1");
        ClientProcessor locationProcessor = new ClientProcessor("select * from LOCATIONS where rownum <= 1");
        ClientProcessor departmentProcessor = new ClientProcessor("select * from DEPARTMENTS where rownum <= 1");
        ClientProcessor countriesProcessor = new ClientProcessor("select * from COUNTRIES where rownum <= 1");
        ClientProcessor jobHistoryProcessor = new ClientProcessor("select * from JOB_HISTORY where rownum <= 1");

        FutureTask employeeTask = employeeProcessor.processQueryWithNewConnection();
        FutureTask locationTask = locationProcessor.processQueryWithNewConnection();
        FutureTask departmentTask = departmentProcessor.processQueryWithNewConnection();
        FutureTask countriesTask = countriesProcessor.processQueryWithNewConnection();
        FutureTask jobHistoryTask = jobHistoryProcessor.processQueryWithNewConnection();

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        executorService.execute(employeeTask);
        executorService.execute(locationTask);
        executorService.execute(departmentTask);
        executorService.execute(countriesTask);
        executorService.execute(jobHistoryTask);

        while(true){

            if(employeeTask.isDone() && locationTask.isDone() && departmentTask.isDone() && countriesTask.isDone() && jobHistoryTask.isDone()){
                System.out.println("Done");
                //shut down executor service
                executorService.shutdown();
                System.out.println("Discarding All Connections in Connection Pool, usedQueue size: "+ SimpleJDBCConnectionPool.getConnectionPool().getUsedConnectionQueue().size());
                SimpleJDBCConnectionPool.getConnectionPool().discardAllConnections();
                System.out.println("Discarded ConnectionPool => usedQueue size: "+ SimpleJDBCConnectionPool.getConnectionPool().getUsedConnectionQueue().size());
                for(Connection con : SimpleJDBCConnectionPool.getConnectionPool().getUsedConnectionQueue()){
                    System.out.print(con.getConnectionState().name()+ ", ");
                }
                break;
            }

            if(!employeeTask.isDone()){
                //wait indefinitely for future task to complete
                try {
                    System.out.println("employeeTask output = ");
                    printResultSet((List<Map<String,String>>)employeeTask.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Waiting for locationTask to complete");
            List<Map<String,String>> s = null;
            try {
                s = (List<Map<String,String>>) locationTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(s !=null){
                System.out.println("locationTask output = ");
                try {
                    printResultSet((List<Map<String,String>>)locationTask.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            System.out.println("Waiting for departmentTask to complete");
            try {
                s = (List<Map<String,String>>) departmentTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(s !=null){
                System.out.println("departmentTask output = ");
                try {
                    printResultSet((List<Map<String,String>>)departmentTask.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Waiting for countriesTask to complete");
            try {
                s = (List<Map<String,String>>) countriesTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(s !=null){
                System.out.println("countriesTask output = ");
                try {
                    printResultSet((List<Map<String,String>>)countriesTask.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Waiting for jobHistoryTask to complete");
            try {
                s = (List<Map<String,String>>) countriesTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(s !=null){
                System.out.println("jobHistoryTask output = ");
                try {
                    printResultSet((List<Map<String,String>>)jobHistoryTask.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    public static void printResultSet(List<Map<String,String>> resultSetList){
        for(Map<String,String> map : resultSetList){
            System.out.print("[");
            for(Map.Entry m : map.entrySet()){
                System.out.print("(" + m.getKey() + " : " + m.getValue() + "), ");
            }
            System.out.print("]");
            System.out.println();
        }
    }




}
