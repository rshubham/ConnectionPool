package com.connectionPool.clients;

import com.connectionPool.connectionPools.ConnectionPool;
import com.connectionPool.connectionPools.SimpleSSHConnectionPool;
import com.connectionPool.connections.sshConnections.SimpleSSHConnection;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Client_SSHConnectionPool {

    public static void main(String[] args) throws IOException {
        ConnectionPool myConnectionPool = SimpleSSHConnectionPool.getConnectionPool(4);
        SimpleSSHConnection sshConnection = (SimpleSSHConnection) myConnectionPool.getConnection();
        //sshConnection.getConnection();
        while(true) {
            Channel channel = sshConnection.getChannel("exec");
            ChannelExec channelExec = (ChannelExec) channel;
            Scanner reader = new Scanner(System.in);
            System.out.println("Enter Command to execute..");
            String command = reader.nextLine();
            if ("exit".equals(command)) break;
            channelExec.setCommand(command);
            InputStream inputStream = channelExec.getInputStream();
            sshConnection.getChannelOutput(inputStream);
        }
        myConnectionPool.releaseConnection(sshConnection);
    }


}
