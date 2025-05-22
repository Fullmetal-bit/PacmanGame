package ui;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameWindow extends JFrame {

    public GameWindow() {
        setTitle("Pacman Game");
        setSize(600, 600); // width, height
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center on screen
        setResizable(false);

        // Add game panel (will draw Pacman later here)
        JPanel gamePanel = new JPanel();
        gamePanel.setBackground(java.awt.Color.BLACK); // Set background color
        add(gamePanel);


        setVisible(true); // show the window
    }

    // Main method to run the game
    public static void main(String[] args) {
        new GameWindow();
    }
}
