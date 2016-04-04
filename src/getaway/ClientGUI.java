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
    private JFrame clientFrame;

    private boolean connected;
    private Client client;
    private int defaultPort;
    private String defaultHost;
    private DefaultListModel listModel = new DefaultListModel();


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

        clientFrame = new JFrame("Client Window");
        clientFrame.setContentPane(clientPanel);
        clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clientFrame.setSize(720, 602);
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
        btnWhoIsOnline.setEnabled(true);
        btnJoinGame.setEnabled(true);
        btnCreateGame.setEnabled(true);
        btnPrivateChat.setEnabled(true);
        tfServer.setEditable(false);
        tfPort.setEditable(false);
        textField.addActionListener(this);
    }

    public void showOnlinePlayers (String [] playerNames) {
        listModel.removeAllElements();
        for (int i = 0; i < playerNames.length; i++) {
            listModel.addElement(playerNames[i]);
        }
        listOnlinePlayers.setModel(listModel);
    }

    private void onClickLogout() {
        client.sendMessage(new Message(Message.LOGOUT, ""));
    }

    private void onClickWhoIsOnline () {
        client.sendMessage(new Message(Message.WHOISIN, ""));
    }

    private void onClickJoinGame() {

    }

    private void onClickCreateGame() {

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
}
