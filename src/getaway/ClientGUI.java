package getaway;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.sun.glass.ui.Cursor.setVisible;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 * Created by Diljot on 4/2/2016.
 */
public class ClientGUI implements ActionListener, ListSelectionListener {


    private JTextField tfServer;
    private JTextField tfPort;
    private JTextField textField;
    private JTextArea textArea;
    private JList listOnlinePlayers;
    private JButton btnLogin;
    private JButton btnWhoIsOnline;
    private JButton btnCreateGame;
    private JButton btnLogout;
    private JButton btnPrivateChat;
    private JButton btnJoinGame;
    private JLabel label;
    private JPanel clientPanel;
    private JScrollPane scrollPaneOnlinePlayers;
    private JScrollPane cardListField;
    private JList cardList;
    private JButton selectCardButton;
    private JScrollPane currentPileScrollPane;
    private JList currentPileList;
    private JTextField turnTextField;
    private JList inGamePlayersList;
    private JScrollPane inGamePlayersScrollPane;
    private JTextField usernameField;
    private JFrame clientFrame;

    private boolean connected;
    private Client client;
    private int defaultPort;
    private String defaultHost;
    private DefaultListModel listModel = new DefaultListModel();
    private DefaultListModel cardListModel = new DefaultListModel();
    private DefaultListModel currentPlayersModel = new DefaultListModel();
    private boolean gameOngoing;
    private String[] playerHand;


    ClientGUI(String host, int port) {
        defaultPort = port;
        defaultHost = host;

        textArea.setEditable(false);

        listOnlinePlayers = new JList(listModel);
        listOnlinePlayers.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        listOnlinePlayers.setVisibleRowCount(10);
        listOnlinePlayers.addListSelectionListener(this);
        scrollPaneOnlinePlayers.setViewportView(listOnlinePlayers);

        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClickLogin();
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClickLogout();
            }
        });
        btnLogout.setEnabled(false);

        btnWhoIsOnline.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClickWhoIsOnline();
            }
        });
        btnWhoIsOnline.setEnabled(false);

        btnCreateGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClickCreateGame();
            }
        });
        btnCreateGame.setEnabled(false);

        btnJoinGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClickJoinGame();
            }
        });
        btnJoinGame.setEnabled(false);

        btnPrivateChat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClickPrivateChat();
            }
        });
        btnPrivateChat.setEnabled(false);


        selectCardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClickSelectCardButton();
            }
        });
        selectCardButton.setEnabled(false);

        clientFrame = new JFrame("Client Window");
        clientFrame.setContentPane(clientPanel);
        clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clientFrame.setSize(920, 602);
        clientFrame.setResizable(false);
        textField.requestFocus();
        clientFrame.setVisible(true);




    }
    void append(String str) {
        textArea.append(str);
        textArea.setCaretPosition(textArea.getText().length() - 1);
    }
    void connectionFailed() {
        btnLogin.setEnabled(true);
        btnLogout.setEnabled(false);
        btnWhoIsOnline.setEnabled(false);
        label.setText("Enter your username below");
        textField.setText("Anonymous");
        tfPort.setText("" + defaultPort);
        tfServer.setText(defaultHost);
        tfServer.setEditable(false);
        tfPort.setEditable(false);
        textField.removeActionListener(this);
        connected = false;
    }
    private void onClickLogin() {
        String username = textField.getText().trim();
        if(username.length() == 0)
            return;
        String server = tfServer.getText().trim();
        if(server.length() == 0)
            return;
        String portNumber = tfPort.getText().trim();
        if(portNumber.length() == 0)
            return;
        int port = 0;
        try {
            port = Integer.parseInt(portNumber);
        }
        catch(Exception en) {
            return;
        }

        client = new Client(server, port, username, this);
        if(!client.start())
            return;
        textField.setText("");
        label.setText("Enter your message below");
        connected = true;

        btnLogin.setEnabled(false);
        btnLogout.setEnabled(true);

        btnJoinGame.setEnabled(true);
        if(!gameOngoing)
        {
            btnWhoIsOnline.setEnabled(true);
            btnCreateGame.setEnabled(true);
        }


        btnPrivateChat.setEnabled(true);
        tfServer.setEditable(false);
        tfPort.setEditable(false);
        textField.addActionListener(this);

        usernameField.setText(username);
    }

    public void showOnlinePlayers (String [] playerNames) {
        listModel.removeAllElements();
        for (int i = 0; i < playerNames.length; i++) {
            listModel.addElement(playerNames[i]);
        }
        listOnlinePlayers.setModel(listModel);
    }

    public void updateHand(String[] playerHand)
    {
        cardListModel.removeAllElements();
        for (int i = 0; i < playerHand.length; i++) {
            cardListModel.addElement(playerHand[i]);
        }
        cardList.setModel(cardListModel);


    }

    public void updateTurn(String turn, Boolean isMyTurn)
    {
        turnTextField.setText("Turn: " + turn);

        if (!isMyTurn)
        {
            selectCardButton.setEnabled(false);
        }

        else
        {
            selectCardButton.setEnabled(true);
        }
    }

    public void fillCurrentPlayers(String[] players)
    {
        currentPlayersModel.removeAllElements();
        for (int i = 0; i < players.length; i++) {
            currentPlayersModel.addElement(players[i]);
        }
        inGamePlayersList.setModel(currentPlayersModel);

        btnWhoIsOnline.setEnabled(false);
        btnCreateGame.setEnabled(false);

        gameOngoing=true;
    }

    private void onClickLogout() {
        client.sendMessage(new Message(Message.LOGOUT, ""));
    }

    private void onClickWhoIsOnline () {
        client.sendMessage(new Message(Message.WHOISIN, ""));
    }

    private void onClickJoinGame()
    {

    }

    private void onClickSelectCardButton()
    {
        try
        {
            String selectedCard = cardList.getSelectedValue().toString();
            System.out.println("WORKS");
            client.sendMessage(new Message(Message.TURNPROGRESSION, selectedCard));
        }
        catch(Exception e)
        {

        }

        // client.setCurrentHand(removeElement());

    }

   /* //borrowed rrom online
    public String[] removeElement(int[] original, int element){
        for(int i = studentChoice+1; i<studentNamesArray.length; i++) {
            studentNamesArray[i-1] = studentNamesArray[i];
        }
    }*/

    private void onClickCreateGame()
    {
        client.sendMessage(new Message(Message.CREATEGAME, ""));
        System.out.println("HIHIHI");
    }

    private void onClickPrivateChat () {
        Object obj = listOnlinePlayers.getSelectedValue();
        String selectedPlayer = obj.toString();
        String [] messageToPlayer = {selectedPlayer, textField.getText()};
        client.sendMessage(new Message(Message.PRIVATE_MESSAGE, messageToPlayer));
        append("Message To " + "*" + selectedPlayer + "*: " + textField.getText());
    }

    public void actionPerformed(ActionEvent e) {
        if(connected) {
            String messageEntered = textField.getText();
            client.sendMessage(new Message(Message.CHATMESSAGE, messageEntered));
            textField.setText("");
            return;
        }
    }
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {

            if (listOnlinePlayers.getSelectedIndex() == -1) {
                //No selection, disable select button.
                btnCreateGame.setEnabled(false);

            } else {
                //Selection, enable the select button.
                btnCreateGame.setEnabled(true);
            }
        }
    }

    public static void main(String[] args) {
        new ClientGUI("localhost", 1500);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
