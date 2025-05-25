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
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));

        startButton = new JButton("Start");
        highScoreButton = new JButton("High Score");
        exitButton = new JButton("Exit");

        startButton.addActionListener(this);
        highScoreButton.addActionListener(this);
        exitButton.addActionListener(this);

        panel.add(startButton);
        panel.add(highScoreButton);
        panel.add(exitButton);

        add(panel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("PACMAN GAME", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        add(titleLabel, BorderLayout.NORTH);

        setVisible(true);
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