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
    private String[] currentHand;
    private String currentTurn;
    private String currentGamePlayers;
   //private GameRoom myGameWindow;
    
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
            display("Error connectiong to server:" + ec);
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

    private void populateHand(int[] newHand)
    {

    }


    public void setCurrentHand(String[] currentHand) {
        this.currentHand = currentHand;
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
                String[] secondMessageArray = message.getSecondMessageArray();
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

                    case Message.TURNPROGRESSION:

                        currentTurn = chatMessage;
                        Boolean isMyTurn;
                        if(currentTurn.equals(username))
                        {
                            isMyTurn=true;
                        }

                        else
                        {
                            isMyTurn=false;
                        }

                        clientGUI.updateTurn(currentTurn, isMyTurn);
                        break;

                    case Message.REMOVECARD:

                        //search for card to remove

                        int removeIndex=0;
                        for (int i =0; i < currentHand.length; i ++)
                        {
                            if (chatMessage.equals(currentHand[i]))
                            {
                                removeIndex=i;
                            }
                        }

                        //remove card and shift down

                        for (int i=removeIndex; i<currentHand.length-1; i++)
                        {
                            currentHand[i] = currentHand[i+1];

                            if(! ( (i+1) < currentHand.length-1))
                            {
                                currentHand[i+1]="";
                            }
                        }




                        clientGUI.updateHand(currentHand);
                        break;

                    case Message.CREATEGAME:
                        //create new game window
                        //myGameWindow = new GameRoom();
                        currentHand = messageArray;

                        currentTurn= chatMessage;


                        if(currentTurn.equals(username))
                        {
                            isMyTurn=true;
                        }

                        else
                        {
                            isMyTurn=false;
                        }
                        clientGUI.updateHand(currentHand);
                        clientGUI.updateTurn(currentTurn, isMyTurn);
                        clientGUI.fillCurrentPlayers(secondMessageArray);


                        break;

                }
            }
        }
    }
}

