package ssc.network;

//there is layer of abstraction. Working as multipurpose object: for client, for server or for connection

public interface ConnectionListener {
    void connectionReady(Connection connection);
    void receiveString(Connection connection, String rString);
    void disconnect(Connection connection);
    void exception(Connection connection, Exception e);
}
