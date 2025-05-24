package ui;

import javax.swing.*;

public class GameWindow extends JFrame {
    public GameWindow() {
        setTitle("Pacman Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GameBoard gameBoard = new GameBoard();
        add(gameBoard);

        pack(); // adjust frame size to GameBoard
        setLocationRelativeTo(null); // center
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameWindow::new);
    }
}