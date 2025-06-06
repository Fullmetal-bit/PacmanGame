package dao;

import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreDAO {

    public static void saveScore(String playerName, int score) {
        if (playerName == null || playerName.trim().isEmpty()) {
            System.out.println("❌ Player name cannot be empty.");
            return;
        }

        String sql = "INSERT INTO scores (player_name, score) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, playerName.trim());
            stmt.setInt(2, score);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Score saved successfully.");
            } else {
                System.out.println("❌ No rows affected, score not saved.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to save score:");
            e.printStackTrace();
        }
    }

    public static List<String> getTopScores() {
        List<String> scores = new ArrayList<>();
        String sql = "SELECT player_name, score, date_played FROM scores ORDER BY score DESC, date_played ASC LIMIT 10";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Timestamp datePlayed = rs.getTimestamp("date_played");
                String dateStr = (datePlayed != null) ? datePlayed.toString() : "N/A";
                String row = rs.getString("player_name") + " - " + rs.getInt("score") +
                        " (" + dateStr + ")";
                scores.add(row);
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to fetch scores:");
            e.printStackTrace();
        }

        return scores;
    }
}
