package model;

public class PlayerScore {
    private int id;
    private String playerName;
    private int score;
    private String datePlayed;

    public PlayerScore(int id, String playerName, int score, String datePlayed) {
        this.id = id;
        this.playerName = playerName != null ? playerName : "Unknown";
        this.score = score;
        this.datePlayed = datePlayed != null ? datePlayed : "N/A";
    }

    public PlayerScore(String playerName, int score) {
        this.playerName = playerName != null ? playerName : "Unknown";
        this.score = score;
        this.datePlayed = "N/A";
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) {
        this.playerName = (playerName != null) ? playerName : "Unknown";
    }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getDatePlayed() { return datePlayed; }
    public void setDatePlayed(String datePlayed) {
        this.datePlayed = (datePlayed != null) ? datePlayed : "N/A";
    }

    @Override
    public String toString() {
        return playerName + " - " + score + " (" + datePlayed + ")";
    }
}
