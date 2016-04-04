package getaway;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GameLobby extends JFrame {
	private final JPanel northPanel = new JPanel();
	private final JPanel lobbyDetails = new JPanel();
	private final JButton btnCreateGameLobby = new JButton("Create Game Lobby");
	private final JButton btnInvitePlayer = new JButton("Invite Player");
	private final JLabel lblRoomName = new JLabel("Enter Room Name:");
	private final JTextField tfRoomName = new JTextField();
	private final JScrollPane scrollPane = new JScrollPane();
	private final JTextArea txtAreaLobby = new JTextArea();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GameLobby frame = new GameLobby();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GameLobby() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		getContentPane().add(northPanel, BorderLayout.NORTH);
		northPanel.setLayout(new GridLayout(1, 0, 0, 0));
		northPanel.add(lobbyDetails);
		lobbyDetails.add(lblRoomName);
		lobbyDetails.add(tfRoomName);
		tfRoomName.setColumns(10);
		
		JLabel lblLobbyID = new JLabel("Lobby ID Is:");
		lobbyDetails.add(lblLobbyID);
		
		JPanel centerPanel = new JPanel();
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new GridLayout(1, 0, 0, 0));
		centerPanel.add(scrollPane);
		txtAreaLobby.setText("Welcome to Room:");
		scrollPane.setViewportView(txtAreaLobby);
		
		JPanel southPanel = new JPanel();
		getContentPane().add(southPanel, BorderLayout.SOUTH);
		southPanel.setLayout(new GridLayout(1, 0, 0, 0));
		southPanel.add(btnCreateGameLobby);
		southPanel.add(btnInvitePlayer);
		
		JButton btnStartGame = new JButton("Start Game");
		southPanel.add(btnStartGame);
	}

}
