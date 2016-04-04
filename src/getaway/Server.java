package getaway;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class Server {
	private static int uniqueId;
	private ArrayList<ClientThread> clientList;
	private ServerGUI serverGUI;
	private SimpleDateFormat dateFormat;
	private int port;
	private boolean keepGoing;
    private String[][] deckArray;
    private String[] linearDeckArray;
    private String[] shuffledDeckArray;
    private String currentTurn;
    private int turnUsernameIndex=0;
    private String[] inGamePlayers;
    private String[] currentPile;
	
	public Server(int port) {
	    this(port, null);
	}
	public Server(int port, ServerGUI serverGUI) {
	    this.serverGUI = serverGUI;
	    this.port = port;
	    dateFormat = new SimpleDateFormat("HH:mm:ss");
	    clientList = new ArrayList<ClientThread>();
	}
	public void start() {
	    keepGoing = true;
	    try
	    {
	        ServerSocket serverSocket = new ServerSocket(port);
	        while(keepGoing)
	        {
	            display("Server waiting for Clients on port " + port + ".");
	            Socket socket = serverSocket.accept();
	            if(!keepGoing)
	                break;
	            ClientThread t = new ClientThread(socket);
	            clientList.add(t);                                  
	            t.start();
	        }
	        try {
	            serverSocket.close();
	            for(int i = 0; i < clientList.size(); ++i) {
	                ClientThread clientThread = clientList.get(i);
	                try {
	                clientThread.sInput.close();
	                clientThread.sOutput.close();
	                clientThread.socket.close();
	                }
	                catch(IOException ioE) {
	                }
	            }
	        }
	        catch(Exception e) {
	            display("Exception closing the server and clients: " + e);
	        }
	    }
	    catch (IOException e) {
	        String msg = dateFormat.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
	        display(msg);
	    }
	}      
	
	@SuppressWarnings("resource")
	protected void stop() {
	    keepGoing = false;
	
	    try {
	        new Socket("localhost", port);
	    }
	    catch(Exception e) {
	    }
	}

    public String[] getNames()
    {
        String[] names = new String[clientList.size()];
        inGamePlayers=new String[clientList.size()];

        for(int i = 0; i <clientList.size() ;i++)

        {
            ClientThread ct = clientList.get(i);
            names[i] = ct.username;
            inGamePlayers[i]=ct.username;
            //writeMsg((i+1) + ") " + ct.username + " since " + ct.date);
        }

        return names;
    }


    private void display(String msg) {
	    String time = dateFormat.format(new Date()) + " " + msg;
	    serverGUI.appendEvent(time + "\n");
	}
	
	private synchronized void broadcast(String message) {
	    String time = dateFormat.format(new Date());
	    String messageLf = time + " " + message + "\n";
	    serverGUI.appendRoom(messageLf);

        List<String[]> splittedArray  = splitArray(linearDeckArray, (52/clientList.size()) );
       /* System.out.println(splittedArray.get(0).length);
        System.out.println(splittedArray.get(1).length);
        System.out.println(splittedArray.get(2).length);

        for (int i = 0; i < splittedArray.get(0).length; i++) {
            System.out.println(splittedArray.get(0)[i] + " ");
        }*/

	    for(int i = clientList.size(); --i >= 0;) {
	        ClientThread ct = clientList.get(i);
            System.out.println("i is " + i);
            //if creating game room
            if (message.equals("CREATEGAME"))
            {
                //distribute hand
                if(!ct.sendMessage(new Message(Message.CREATEGAME, splittedArray.get(i), currentTurn, getNames()))) {
                    clientList.remove(i);
                    display("Disconnected Client " + ct.username + " removed from list.");
                }
            }

            else if (message.equals("TURNPROGRESSION"))
            {
                //distribute hand
                if(!ct.sendMessage(new Message(Message.TURNPROGRESSION, currentTurn)))
                {
                    clientList.remove(i);
                    display("Disconnected Client " + ct.username + " removed from list.");
                }
            }

	        else if(!ct.sendMessage(new Message(Message.CHATMESSAGE, messageLf))) {
	        	clientList.remove(i);
	            display("Disconnected Client " + ct.username + " removed from list.");
	        }


	    }
	}
	private void sendPrivateMessage(String player, String message) {
		for(int i = 0; i < clientList.size(); i++) {
			ClientThread ct = clientList.get(i);
			if (ct.username == player) {
				if (!ct.sendMessage(new Message(Message.PRIVATE_MESSAGE, message))) {
					clientList.remove(i);
					display("Disconnected Client " + ct.username + " removed from list.");
				}
			}
		}


	}

    public static <T> List<T[]> splitArray(T[] items, int maxSubArraySize) {
        List<T[]> result = new ArrayList<T[]>();
        if (items ==null || items.length == 0) {
            return result;
        }

        int from = 0;
        int to = 0;
        int slicedItems = 0;
        while (slicedItems < items.length) {
            to = from + Math.min(maxSubArraySize, items.length - to);
            T[] slice = Arrays.copyOfRange(items, from, to);
            result.add(slice);
            slicedItems += slice.length;
            from = to;
        }
        return result;
    }

    private void shuffleCards()
    {
       /* for (int i = 0; i < linearDeckArray.length; i++) {
            System.out.println(linearDeckArray[i] + " ");
        }*/
        //shuffledDeckArray = new String[52];
        Collections.shuffle(Arrays.asList(linearDeckArray));

       /* for (int i = 0; i < linearDeckArray.length; i++) {
            System.out.println(linearDeckArray[i] + " ");
        }*/
    }

    /*private void ShuffleArray(String[] array)
    {
        String index, temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }*/

	private synchronized void remove(int id) {
	    for(int i = 0; i < clientList.size(); ++i) {
	        ClientThread ct = clientList.get(i);
	        if(ct.id == id) {
	        	clientList.remove(i);
	            return;
	        }
	    }
	}

	 
	public static void main(String[] args)
    {
	    int portNumber = 1500;
	    Server server = new Server(portNumber);
	    server.start();


        
    }

	
	class ClientThread extends Thread {
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username;
        Message message;
        String date;

        ClientThread(Socket socket) {
            id = ++uniqueId;
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
                display(username + " just connected.");
            } catch (IOException e) {
                display("Exception creating new Input/output Streams: " + e);
                return;
            } catch (ClassNotFoundException e) {
            }
            date = new Date().toString() + "\n";
        }



	    public void run() {
	        boolean keepGoing = true;
	        while(keepGoing) {
	            try {
	                message = (Message) sInput.readObject();
	            }
	            catch (IOException e) {
	                display(username + " Exception reading Streams: " + e);
	                break;             
	            }
	            catch(ClassNotFoundException e2) {
	                break;
	            }
	            String chatMessage = message.getMessage();
				String [] messageArray = message.getMessageArray();
	            switch(message.getType())
                {
                    case Message.CHATMESSAGE:
                        broadcast(username + ": " + chatMessage);
                        //sendMessage(new Message(Message.CHATMESSAGE, chatMessage));
                        break;
                    case Message.LOGOUT:
                        display(username + " disconnected with a LOGOUT cmessage.");
                        keepGoing = false;
                        break;
                    case Message.WHOISIN:
                        //writeMsg("List of the users connected at " + dateFormat.format(new Date()) + "\n");
                        String[] names = getNames();
                        sendMessage(new Message(Message.WHOISIN, names));
                        break;
                    case Message.PRIVATE_MESSAGE:
                        String nameofPlayer = messageArray[0];
                        String messageToPlayer = messageArray[1];
                        sendPrivateMessage(nameofPlayer, messageToPlayer);
                        break;

                    case Message.TURNPROGRESSION:
                        String cardRemoved = chatMessage;
                        sendMessage(new Message(Message.REMOVECARD, cardRemoved));
                        turnUsernameIndex++;
                        if (turnUsernameIndex + 1 > inGamePlayers.length)
                        {
                            turnUsernameIndex=0;


                            //since we back at start, new round, so clear pile if not already cleared.
                        }

                        currentTurn = inGamePlayers[turnUsernameIndex];

                        broadcast("TURNPROGRESSION");

                        break;

                    case Message.CREATEGAME:
                        deckArray = new String[4][13];

                        //user who creates game goes first by default for simplicity
                        currentTurn=username;


                        for (int suitIndex =0; suitIndex<4; suitIndex++)
                        {
                            for (int numberIndex = 0; numberIndex < 13; numberIndex++)
                            {
                                if (suitIndex == 0)
                                {

                                    if (numberIndex == 0)
                                    {
                                        deckArray[suitIndex][numberIndex] = "Ace" + " of " + "Diamonds";
                                    }

                                    else if (numberIndex == 10)
                                    {
                                        deckArray[suitIndex][numberIndex] = "Jack" + " of " + "Diamonds";
                                    }

                                    else if (numberIndex == 11)
                                    {
                                        deckArray[suitIndex][numberIndex] = "Queen" + " of " + "Diamonds";
                                    }

                                    else if (numberIndex == 12)
                                    {
                                        deckArray[suitIndex][numberIndex] = "King" + " of " + "Diamonds";
                                    }

                                    else
                                    {
                                        deckArray[suitIndex][numberIndex] = Integer.toString(numberIndex) + " of " + "Diamonds";
                                    }
                                }


                                else if (suitIndex == 1)
                                {
                                    if (numberIndex == 0)
                                    {
                                        deckArray[suitIndex][numberIndex] = "Ace" + " of " + "Clubs";
                                    }

                                    else if (numberIndex == 10)
                                    {
                                        deckArray[suitIndex][numberIndex] = "Jack" + " of " + "Clubs";
                                    }

                                    else if (numberIndex == 11)
                                    {
                                        deckArray[suitIndex][numberIndex] = "Queen" + " of " + "Clubs";
                                    }

                                    else if (numberIndex == 12)
                                    {
                                        deckArray[suitIndex][numberIndex] = "King" + " of " + "Clubs";
                                    }

                                    else
                                    {
                                        deckArray[suitIndex][numberIndex] = Integer.toString(numberIndex) + " of " + "Clubs";
                                    }
                                }

                                else if (suitIndex == 2)
                                {
                                    if (numberIndex == 0)
                                    {
                                        deckArray[suitIndex][numberIndex] = "Ace" + " of " + "Hearts";
                                    }

                                    else if (numberIndex == 10)
                                    {
                                        deckArray[suitIndex][numberIndex] = "Jack" + " of " + "Hearts";
                                    }

                                    else if (numberIndex == 11)
                                    {
                                        deckArray[suitIndex][numberIndex] = "Queen" + " of " + "Hearts";
                                    }

                                    else if (numberIndex == 12)
                                    {
                                        deckArray[suitIndex][numberIndex] = "King" + " of " + "Hearts";
                                    }

                                    else
                                    {
                                        deckArray[suitIndex][numberIndex] = Integer.toString(numberIndex) + " of " + "Hearts";
                                    }
                                }

                                else if (suitIndex == 3)
                                {
                                    if (numberIndex == 0)
                                    {
                                        deckArray[suitIndex][numberIndex] = "Ace" + " of " + "Spades";
                                    }

                                    else if (numberIndex == 10)
                                    {
                                        deckArray[suitIndex][numberIndex] = "Jack" + " of " + "Spades";
                                    }

                                    else if (numberIndex == 11)
                                    {
                                        deckArray[suitIndex][numberIndex] = "Queen" + " of " + "Spades";
                                    }

                                    else if (numberIndex == 12)
                                    {
                                        deckArray[suitIndex][numberIndex] = "King" + " of " + "Spades";
                                    }

                                    else
                                    {
                                        deckArray[suitIndex][numberIndex] = Integer.toString(numberIndex) + " of " + "Spades";
                                    }
                                }


                            }
                        }

                        int linearIndex = 0;
                        linearDeckArray= new String[52];
                        
                        for (int suitIndex =0; suitIndex<4; suitIndex++)
                        {
                            for (int numberIndex = 0; numberIndex < 13; numberIndex++)
                            {
                                linearDeckArray[linearIndex]= deckArray[suitIndex][numberIndex];
                                linearIndex++;
                            }

                        }

                        shuffleCards();

                        broadcast("CREATEGAME");
                        break;
	            }

	        }
	        remove(id);
	        close();
	    }

	    private void close() {
	        try {
	            if(sOutput != null) sOutput.close();
	        }
	        catch(Exception e) {}
	        try {
	            if(sInput != null) sInput.close();
	        }
	        catch(Exception e) {};
	        try {
	            if(socket != null) socket.close();
	        }
	        catch (Exception e) {}
	    }
		private boolean sendMessage(Message msg) {
			if(!socket.isConnected()) {
				close();
				return false;
			}
			try {
				sOutput.writeObject(msg);
			} catch (IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}

	 }
}