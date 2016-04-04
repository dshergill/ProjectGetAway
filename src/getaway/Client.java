package getaway;

import java.net.*;
import java.io.*;

public class Client {
	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;
    private Socket socket;
    private ClientGUI clientGUI;
    private String server, username;
    private int port;

    
    Client(String server, int port, String username, ClientGUI clientGUI) {
        this.server = server;
        this.port = port;
        this.username = username;
        this.clientGUI = clientGUI;
    }
     
    public boolean start() {
        try {
            socket = new Socket(server, port);
        }
        catch(Exception ec) {
            display("Error connecting to server:" + ec);
            return false;
        }
        
        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);
        
        try
        {
            sInput  = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException eIO) {
            display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }
        new ListenFromServer().start();
        try
        {
            sOutput.writeObject(username);
        }
        catch (IOException eIO) {
            display("Exception doing login : " + eIO);
            disconnect();
            return false;
        }
        return true;
    }
 
    private void display(String msg) {
        clientGUI.append(msg + "\n");  
    }

    private void getOnlineUsers() {

    }

    void sendMessage(Message msg) {
        try {
            sOutput.writeObject(msg);
        }
        catch(IOException e) {
            display("Exception writing to server: " + e);
        }
    }
 
    private void disconnect() {
        try {
            if(sInput != null) sInput.close();
        }
        catch(Exception e) {} 
        try {
            if(sOutput != null) sOutput.close();
        }
        catch(Exception e) {} 
        try{
            if(socket != null) socket.close();
        }
        catch(Exception e) {} 
         
        if(clientGUI != null)
            clientGUI.connectionFailed();
             
    }

     
    class ListenFromServer extends Thread {
        Message message;
        public void run() {
            while(true) {
                try {
                    message = (Message) sInput.readObject();
                }
                catch(IOException e) {
                    display("Server has close the connection: " + e);
                    if(clientGUI != null)
                        clientGUI.connectionFailed();
                    break;
                }
                catch(ClassNotFoundException e2) {
                }
                String chatMessage = message.getMessage();
                String [] messageArray = message.getMessageArray();
                switch(message.getType()) {
                    case Message.CHATMESSAGE:
                        clientGUI.append(chatMessage);
                        break;
                    case Message.WHOISIN:
                        String [] listOnlinePlayers = messageArray;
                        clientGUI.showOnlinePlayers(listOnlinePlayers);
                        break;
                    case Message.PRIVATE_MESSAGE:
                        clientGUI.append("Private Message: " + chatMessage);
                    break;
                }
            }
        }
    }
}

