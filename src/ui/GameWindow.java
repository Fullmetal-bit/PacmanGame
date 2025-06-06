package ui;

import javax.swing.*;

public class GameWindow extends JFrame {
    public GameWindow() {
        try {
            setTitle("Pacman Game");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setResizable(false);

            GameBoard gameBoard = new GameBoard();
            add(gameBoard);

            pack(); // adjust frame size to GameBoard
            setLocationRelativeTo(null); // center
            setVisible(true);
        } catch (Exception ex) {
            showErrorDialog("Failed to initialize game window.\n" + ex.getMessage());
            // Optionally exit if game window cannot start
            System.exit(1);
        }
    }

    private void showErrorDialog(String message) {
        // Use JOptionPane on the Event Dispatch Thread
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new GameWindow();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Failed to launch the game window.\n" + ex.getMessage(),
                        "Fatal Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
