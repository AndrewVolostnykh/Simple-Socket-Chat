package ssc.server;

import ssc.network.Connection;
import ssc.network.ConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements ConnectionListener {

    public static void main(String[] args) {
        new ChatServer();
    }

    private final ArrayList<Connection> connections = new ArrayList<>();

    private ChatServer()
    {
        System.out.println("===Server started===");
        try(ServerSocket serverSocket = new ServerSocket(8189))
        {
            while(true)
            {
                try
                {
                    new Connection(this, serverSocket.accept());
                } catch (Exception e)
                {
                    System.out.println("Connection fail: " + e);
                }
            }
        } catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    public synchronized void connectionReady(Connection connection) {
        connections.add(connection);
        System.out.println("Client connected: " + connection);
    }

    @Override
    public synchronized void receiveString(Connection connection, String rString) {
        sendToAllConnections(rString);
    }

    @Override
    public synchronized void disconnect(Connection connection) {
        connections.remove(connection);
        System.out.println("Client disconnected: " + connection);
    }

    @Override
    public synchronized void exception(Connection connection, Exception e) {
        System.out.println("(Server)Connection exception: " + e);
    }

    private void sendToAllConnections(String value)
    {
        System.out.println(value);
        for(int i = 0; i < connections.size(); i++) connections.get(i).sendString(value);

    }
}
