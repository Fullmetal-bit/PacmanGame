package ui;

import dao.PlayerScoreDAO;
import model.PlayerScore;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class GameMenu extends JFrame implements ActionListener {

    private JButton startButton, highScoreButton, exitButton;

    public GameMenu() {
        setTitle("Pacman Game Menu");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Use a main panel with padding and background color
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(30, 30, 30)); // dark background
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        mainPanel.setLayout(new BorderLayout());

        // Title label with custom font and color
        JLabel titleLabel = new JLabel("PACMAN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 40));
        titleLabel.setForeground(new Color(255, 204, 0)); // Pacman yellow color
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel for buttons with vertical layout and spacing
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 0, 20));
        buttonPanel.setOpaque(false); // transparent background

        startButton = createMenuButton("Start");
        highScoreButton = createMenuButton("High Score");
        exitButton = createMenuButton("Exit");

        startButton.addActionListener(this);
        highScoreButton.addActionListener(this);
        exitButton.addActionListener(this);

        buttonPanel.add(startButton);
        buttonPanel.add(highScoreButton);
        buttonPanel.add(exitButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);

        setVisible(true);
    }

    // Helper to create styled buttons with hover effect
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Verdana", Font.PLAIN, 20));
        button.setBackground(new Color(255, 204, 0));
        button.setForeground(new Color(30, 30, 30));
        button.setBorder(BorderFactory.createLineBorder(new Color(255, 204, 0), 2));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add simple hover effect using mouse listener
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 229, 102)); // lighter yellow
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 204, 0));
            }
        });

        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            new GameWindow();
            this.dispose();

        } else if (e.getSource() == highScoreButton) {
            PlayerScoreDAO dao = new PlayerScoreDAO();
            List<PlayerScore> scores = dao.getAllScores();

            if (scores.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No high scores available.");
            } else {
                StringBuilder sb = new StringBuilder("Top 10 High Scores:\n");
                int rank = 1;
                for (PlayerScore ps : scores) {
                    sb.append(rank++).append(". ").append(ps.toString()).append("\n");
                }
                JOptionPane.showMessageDialog(this, sb.toString());
            }

        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameMenu::new);
    }
}
