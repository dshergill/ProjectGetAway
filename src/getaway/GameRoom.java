package getaway;

import javax.swing.*;

/**
 * Created by Vincent on 03/04/2016.
 */
public class GameRoom {
    private JPanel gameRoomPanel;
    private JFrame gameRoomFrame;

    public GameRoom()
    {
        gameRoomFrame = new JFrame("Game Room Window");
        gameRoomFrame.setContentPane(gameRoomPanel);
        gameRoomFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameRoomFrame.setSize(720, 602);
        gameRoomFrame.setResizable(false);
        //textField.requestFocus();
        gameRoomFrame.setVisible(true);
    }
}
