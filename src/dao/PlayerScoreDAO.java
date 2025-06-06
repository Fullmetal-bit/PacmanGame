package dao;

import model.PlayerScore;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerScoreDAO {

    // Insert a score (existing method)
    public boolean insertScore(PlayerScore score) {
        String query = "INSERT INTO scores (player_name, score) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, score.getPlayerName());
            stmt.setInt(2, score.getScore());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get top scores globally (existing method)
    public List<PlayerScore> getAllScores() {
        List<PlayerScore> list = new ArrayList<>();
        String query = "SELECT * FROM scores ORDER BY score DESC LIMIT 10";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                PlayerScore score = new PlayerScore(
                        rs.getInt("id"),
                        rs.getString("player_name"),
                        rs.getInt("score"),
                        rs.getString("date_played")
                );
                list.add(score);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // New: Get top scores by player name
    public List<PlayerScore> getTopScoresByPlayer(String playerName) {
        List<PlayerScore> list = new ArrayList<>();
        String query = "SELECT player_name, score, date_played FROM scores WHERE player_name = ? ORDER BY score DESC LIMIT 10";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, playerName);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PlayerScore score = new PlayerScore(
                        0,
                        rs.getString("player_name"),
                        rs.getInt("score"),
                        rs.getString("date_played")
                );
                list.add(score);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
