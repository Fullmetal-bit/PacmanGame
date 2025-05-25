package dao;

import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreDAO {

    public static void saveScore(String playerName, int score) {
        String sql = "INSERT INTO scores (player_name, score) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, playerName);
            stmt.setInt(2, score);
            stmt.executeUpdate();

            System.out.println("✅ Score saved successfully.");
        } catch (SQLException e) {
            System.out.println("❌ Failed to save score:");
            e.printStackTrace();
        }
    }

    public static List<String> getTopScores() {
        List<String> scores = new ArrayList<>();
        String sql = "SELECT player_name, score, date_played FROM scores ORDER BY score DESC LIMIT 10";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String row = rs.getString("player_name") + " - " + rs.getInt("score") +
                        " (" + rs.getTimestamp("date_played") + ")";
                scores.add(row);
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to fetch scores:");
            e.printStackTrace();
        }

        return scores;
    }
}