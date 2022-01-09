package com.connectionPool.connections.sshConnections;

import com.connectionPool.connections.Connection;
import com.connectionPool.enums.ConnectionStateEnum;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Objects;

public class SimpleSSHConnection implements SSHConnection {

    private Session sshConnection;
    private ConnectionStateEnum connectionStateEnum;
    private Channel channel;
    /*----------------------------------------------*/
    private int connectionWaitTimeOut; // timeout in ms
    private int timeoutPeriod;
    private String privateKey;
    private String privateKeypassPhrase;
    private String username;
    private String host;
    private Integer port;
    private Hashtable sessionConfig;

    @Override
    public void setConnectionState(ConnectionStateEnum connectionState) {
        this.connectionStateEnum = connectionState;
    }

    @Override
    public ConnectionStateEnum getConnectionState() {
        return this.connectionStateEnum;
    }

    private void setSshConnection(Session sshConnection) {
        this.sshConnection = sshConnection;
    }

    public boolean isChannelConnected(){
        return channel.isConnected();
    }
    public Channel getConnectedChannel(){
        if(isChannelConnected()){
            return channel;
        }
        return null;
    }

    public int getConnectionWaitTimeOut() {
        return connectionWaitTimeOut;
    }

    public int getTimeoutPeriod() {
        return timeoutPeriod;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPrivateKeypassPhrase() {
        return privateKeypassPhrase;
    }

    public String getUsername() {
        return username;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public Hashtable getSessionConfig() {
        return sessionConfig;
    }

    @Override
    public void connect() {

        System.out.println("Getting Session Object..");
        Session session = getSession();
        label: while(true) {
             try {
                 // create connection
                 session.connect();
             } catch (JSchException e) {
                timeoutPeriod += session.getTimeout()/1000;
                 try {
                     Thread.sleep(timeoutPeriod);
                 } catch (InterruptedException interruptedException) {
                     interruptedException.printStackTrace();
                 }
                 System.out.println(timeoutPeriod + "s passed => Timed Out. Reconnecting..");
                continue label;
             }
            if(session.isConnected()) break;
        }
        System.out.println("SSH Connection Established!");
        setSshConnection(session);

    }

    private Session getSession() {
        Session session = null;
        try {
            // SSH Connection
            JSch jsch = new JSch();
            // Public Key Auth
            jsch.addIdentity(privateKey,privateKeypassPhrase);
            // Create Session Object
            if(port == null){
                session = jsch.getSession(username, host);
            } else {
                session = jsch.getSession(username, host, port);
            }
            // updating session config
            session.setConfig(sessionConfig);
        } catch(JSchException e){
            System.out.println("Exception thrown while getting Session!");
            e.printStackTrace();
        }
        return session;
    }

    public Channel getChannel(String type) {

        if(sshConnection.isConnected()) {
            try {
                if(channel == null || !channel.isConnected()){
                    channel = sshConnection.openChannel(type);
                }
            } catch (JSchException e) {
                e.printStackTrace();
            }
        }
        return channel;
    }

    public String getChannelOutput(InputStream inputStream){
        StringBuilder message = new StringBuilder();
        if(!channel.isConnected()){
            try {
                channel.connect();
            while(channel.getExitStatus() < 0){
                Thread.sleep(1000);
                System.out.println("Waiting for command execution to finish..");
            }
            message = new StringBuilder();
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = reader1.readLine()) != null)
            {
                message.append(line).append("\n");
            }
            System.out.println("Exit Status : "+channel.getExitStatus());
            System.out.println(message.toString() + "\n");
            channel.disconnect();
            } catch (JSchException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return message.toString();
    }

    private SimpleSSHConnection(SSHConnectionBuilder builder){
        this.connectionWaitTimeOut = builder.connectionWaitTimeOut;
        this.timeoutPeriod = builder.timeoutPeriod;
        this.privateKey = builder.privateKey;
        this.privateKeypassPhrase = builder.privateKeypassPhrase;
        this.username = builder.username;
        this.host = builder.host;
        this.port = builder.port;
        this.sessionConfig = builder.sessionConfig;
    }

    @Override
    public Session getConnection() {
        return this.sshConnection;
    }


    public static class SSHConnectionBuilder {

        private int connectionWaitTimeOut; // timeout in ms
        private int timeoutPeriod;
        private String privateKey;
        private String privateKeypassPhrase;
        private String username;
        private String host;
        private Integer port;
        private Hashtable sessionConfig;

        public SSHConnectionBuilder(){}


        public SSHConnectionBuilder setConnectionWaitTimeOut(int connectionWaitTimeOut) {
            this.connectionWaitTimeOut = connectionWaitTimeOut;
            return this;
        }

        public SSHConnectionBuilder setTimeoutPeriod(int timeoutPeriod) {

            this.timeoutPeriod = timeoutPeriod;
            return this;
        }

        public SSHConnectionBuilder setPrivateKey(String privateKey) {

            this.privateKey = privateKey;
            return this;
        }

        public SSHConnectionBuilder setPrivateKeypassPhrase(String privateKeypassPhrase) {
            this.privateKeypassPhrase = privateKeypassPhrase;
            return this;
        }

        public SSHConnectionBuilder setUsername(String username) {
            this.username = username;
            return this;
        }

        public SSHConnectionBuilder setHost(String host) {
            this.host = host;
            return this;
        }

        public SSHConnectionBuilder setPort(Integer port) {
            this.port = port;
            return this;
        }

        public SSHConnectionBuilder setSessionConfig(Hashtable sessionConfig) {
            this.sessionConfig = sessionConfig;
            return this;
        }

        public SimpleSSHConnection build() {
            return new SimpleSSHConnection(this);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleSSHConnection)) return false;
        SimpleSSHConnection that = (SimpleSSHConnection) o;
        return getConnectionWaitTimeOut() == that.getConnectionWaitTimeOut() &&
                getTimeoutPeriod() == that.getTimeoutPeriod() &&
                getConnection().equals(that.getConnection()) &&
                connectionStateEnum == that.connectionStateEnum &&
                channel.equals(that.channel) &&
                getPrivateKey().equals(that.getPrivateKey()) &&
                getPrivateKeypassPhrase().equals(that.getPrivateKeypassPhrase()) &&
                getUsername().equals(that.getUsername()) &&
                getHost().equals(that.getHost()) &&
                getPort().equals(that.getPort()) &&
                getSessionConfig().equals(that.getSessionConfig());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getConnection(), connectionStateEnum, channel, getConnectionWaitTimeOut(), getTimeoutPeriod(), getPrivateKey(), getPrivateKeypassPhrase(), getUsername(), getHost(), getPort(), getSessionConfig());
    }
}
