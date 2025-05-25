package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/pacman_db";
    private static final String USER = "root";
    private static final String PASSWORD = "@No1jocker"; // Replace with your password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Test connection
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Connected to MySQL database successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed:");
            e.printStackTrace();
        }
    }
}
