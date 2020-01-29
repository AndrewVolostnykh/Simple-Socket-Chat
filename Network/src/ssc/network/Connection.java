package ssc.network;

//This class describe TCP connection.

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class Connection {

    private final Socket socket;
    private final Thread thread;
    private final ConnectionListener connectionListener;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    public Connection(ConnectionListener connectionListener, String ipAdr, int port) throws Exception // this constructor create socket
    {
         this(connectionListener, new Socket(ipAdr, port));
    }

    public Connection(ConnectionListener connectionListener, Socket socket) throws Exception  // this constructor taking ready socket
    {
        this.connectionListener = connectionListener;
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    connectionListener.connectionReady(Connection.this);
                    while(!thread.isInterrupted())
                    {
                        connectionListener.receiveString(Connection.this, reader.readLine());
                    }
                } catch (Exception e)
                {
                    connectionListener.exception(Connection.this, e);
                } finally {
                    connectionListener.disconnect(Connection.this);
                }
            }
        });
        thread.start();
    }

    public synchronized void sendString(String value){
        try {
            writer.write(value + "\r\n");
            writer.flush(); // send all buffers
        } catch (IOException e) {
            connectionListener.exception(Connection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect()
    {
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            connectionListener.exception(Connection.this, e);
        }
    }

    @Override
    public String toString() {
        return  "Connection: " + socket.getInetAddress() + ":" + socket.getPort();
    }
}
