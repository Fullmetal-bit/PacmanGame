package ui;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import javax.swing.*;

public class GameBoard extends JPanel implements ActionListener, KeyListener {

    class Block {
        int x, y, width, height;
        Image image;
        int startX, startY;
        char direction = 'U'; // U D L R
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;

            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            int speed = tileSize / 4;
            switch (this.direction) {
                case 'U' -> {
                    velocityX = 0;
                    velocityY = -speed;
                }
                case 'D' -> {
                    velocityX = 0;
                    velocityY = speed;
                }
                case 'L' -> {
                    velocityX = -speed;
                    velocityY = 0;
                }
                case 'R' -> {
                    velocityX = speed;
                    velocityY = 0;
                }
                default -> {
                    velocityX = 0;
                    velocityY = 0;
                }
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
            velocityX = 0;
            velocityY = 0;
        }
    }

    private final int rowCount = 21;
    private final int columnCount = 19;
    private final int tileSize = 32;
    private final int boardWidth = columnCount * tileSize;
    private final int boardHeight = rowCount * tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    private final String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "X       bpo       X",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    private HashSet<Block> walls;
    private HashSet<Block> foods;
    private HashSet<Block> ghosts;
    private Block pacman;

    private Timer gameLoop;
    private final char[] directions = {'U', 'D', 'L', 'R'};
    private final Random random = new Random();
    private int score = 0;
    private int lives = 3;
    private boolean gameOver = false;
    private boolean paused = false;
    private boolean gameStarted = false;  // <-- NEW: game start flag

    private char desiredDirection = 'R';

    public GameBoard() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        wallImage = loadImage("/images/wall.png");
        blueGhostImage = loadImage("/images/blueGhost.png");
        orangeGhostImage = loadImage("/images/orangeGhost.png");
        pinkGhostImage = loadImage("/images/pinkGhost.png");
        redGhostImage = loadImage("/images/redGhost.png");

        pacmanUpImage = loadImage("/images/pacmanUp.png");
        pacmanDownImage = loadImage("/images/pacmanDown.png");
        pacmanLeftImage = loadImage("/images/pacmanLeft.png");
        pacmanRightImage = loadImage("/images/pacmanRight.png");

        loadMap();

        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(directions.length)];
            ghost.updateDirection(newDirection);
        }

        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    private Image loadImage(String path) {
        try {
            Image img = new ImageIcon(getClass().getResource(path)).getImage();
            return img;
        } catch (Exception e) {
            System.err.println("Exception while loading image: " + path);
            e.printStackTrace();
            return null;
        }
    }

    public void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();

        for (int r = 0; r < rowCount; r++) {
            if (r >= tileMap.length) break;
            for (int c = 0; c < columnCount; c++) {
                if (c >= tileMap[r].length()) break;
                char tileMapChar = tileMap[r].charAt(c);

                int x = c * tileSize;
                int y = r * tileSize;

                switch (tileMapChar) {
                    case 'X' -> {
                        if (wallImage != null)
                            walls.add(new Block(wallImage, x, y, tileSize, tileSize));
                    }
                    case 'b' -> {
                        if (blueGhostImage != null)
                            ghosts.add(new Block(blueGhostImage, x, y, tileSize, tileSize));
                    }
                    case 'o' -> {
                        if (orangeGhostImage != null)
                            ghosts.add(new Block(orangeGhostImage, x, y, tileSize, tileSize));
                    }
                    case 'p' -> {
                        if (pinkGhostImage != null)
                            ghosts.add(new Block(pinkGhostImage, x, y, tileSize, tileSize));
                    }
                    case 'r' -> {
                        if (redGhostImage != null)
                            ghosts.add(new Block(redGhostImage, x, y, tileSize, tileSize));
                    }
                    case 'P' -> {
                        if (pacmanRightImage != null)
                            pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                    }
                    case ' ' -> foods.add(new Block(null, x + 14, y + 14, 4, 4));
                    default -> {
                        // unknown character
                    }
                }
            }
        }
    }
    private void draw(Graphics g) {
        // Draw walls
        for (Block wall : walls) {
            if (wall.image != null)
                g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, this);
            else {
                // fallback: draw a blue rectangle for walls if image missing
                g.setColor(Color.BLUE);
                g.fillRect(wall.x, wall.y, wall.width, wall.height);
            }
        }

        // Draw foods
        g.setColor(Color.YELLOW);
        for (Block food : foods) {
            g.fillOval(food.x, food.y, food.width, food.height);
        }

        // Draw ghosts
        for (Block ghost : ghosts) {
            if (ghost.image != null)
                g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, this);
            else {
                g.setColor(Color.PINK);
                g.fillOval(ghost.x, ghost.y, ghost.width, ghost.height);
            }
        }

        // Draw Pacman
        if (pacman != null) {
            if (pacman.image != null)
                g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, this);
            else {
                g.setColor(Color.ORANGE);
                g.fillOval(pacman.x, pacman.y, pacman.width, pacman.height);
            }
        }

        // Draw score and lives
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Lives: " + lives, 10, 40);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!gameStarted) {
            drawStartScreen(g);
        } else {
            draw(g);

            if (paused) {
                g.setColor(new Color(255, 255, 255, 150));
                g.fillRect(0, 0, getWidth(), getHeight());

                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 48));
                String pauseMsg = "PAUSED";
                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(pauseMsg)) / 2;
                int y = getHeight() / 2;
                g.drawString(pauseMsg, x, y);
            }

            if (gameOver) {
                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 48));
                String gameOverMsg = "GAME OVER!";
                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(gameOverMsg)) / 2;
                int y = getHeight() / 2 - 30;
                g.drawString(gameOverMsg, x, y);

                String scoreMsg = "Final Score: " + score;
                int sx = (getWidth() - g.getFontMetrics().stringWidth(scoreMsg)) / 2;
                int sy = y + 50;
                g.drawString(scoreMsg, sx, sy);

                String restartMsg = "Press 'R' to Restart";
                int rx = (getWidth() - g.getFontMetrics().stringWidth(restartMsg)) / 2;
                int ry = sy + 40;
                g.drawString(restartMsg, rx, ry);
            }
        }
    }

    private void drawStartScreen(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        String title = "PACMAN GAME";
        int x = (getWidth() - g.getFontMetrics().stringWidth(title)) / 2;
        int y = getHeight() / 4;
        g.drawString(title, x, y);

        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String[] instructions = {
                "Instructions:",
                "P - Pause/Unpause",
                "R - Restart",
                "Arrow Keys / WASD - Move Pacman",
                "Press ENTER to Start"
        };

        int lineHeight = g.getFontMetrics().getHeight() + 5;
        int startY = y + 60;

        for (int i = 0; i < instructions.length; i++) {
            int ix = (getWidth() - g.getFontMetrics().stringWidth(instructions[i])) / 2;
            int iy = startY + i * lineHeight;
            g.drawString(instructions[i], ix, iy);
        }
    }

    public void move() {
        if (paused || gameOver || !gameStarted) return;

        if (pacman == null) return;

        // Try to change direction to desiredDirection if possible
        if (canMoveInDirection(pacman, desiredDirection)) {
            pacman.updateDirection(desiredDirection);
            switch (desiredDirection) {
                case 'U' -> {
                    if (pacmanUpImage != null) pacman.image = pacmanUpImage;
                }
                case 'D' -> {
                    if (pacmanDownImage != null) pacman.image = pacmanDownImage;
                }
                case 'L' -> {
                    if (pacmanLeftImage != null) pacman.image = pacmanLeftImage;
                }
                case 'R' -> {
                    if (pacmanRightImage != null) pacman.image = pacmanRightImage;
                }
            }
        }

        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        Iterator<Block> ghostIterator = ghosts.iterator();
        while (ghostIterator.hasNext()) {
            Block ghost = ghostIterator.next();
            if (ghost == null) continue;

            if (collision(ghost, pacman)) {
                lives--;
                if (lives <= 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
                break;
            }

            try {
                if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D') {
                    ghost.updateDirection('U');
                }

                ghost.x += ghost.velocityX;
                ghost.y += ghost.velocityY;

                for (Block wall : walls) {
                    if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                        ghost.x -= ghost.velocityX;
                        ghost.y -= ghost.velocityY;
                        char newDirection = directions[random.nextInt(directions.length)];
                        ghost.updateDirection(newDirection);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Iterator<Block> foodIterator = foods.iterator();
        while (foodIterator.hasNext()) {
            Block food = foodIterator.next();
            if (collision(pacman, food)) {
                score += 10;
                foodIterator.remove();
                break;
            }
        }

        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    private boolean canMoveInDirection(Block block, char direction) {
        if (block == null) return false;

        int speed = tileSize / 4;
        int nextX = block.x;
        int nextY = block.y;

        switch (direction) {
            case 'U' -> nextY -= speed;
            case 'D' -> nextY += speed;
            case 'L' -> nextX -= speed;
            case 'R' -> nextX += speed;
            default -> {
                return false;
            }
        }

        Block nextPos = new Block(block.image, nextX, nextY, block.width, block.height);
        for (Block wall : walls) {
            if (collision(nextPos, wall)) return false;
        }
        return true;
    }

    private boolean collision(Block a, Block b) {
        if (a == null || b == null) return false;

        Rectangle rectA = new Rectangle(a.x, a.y, a.width, a.height);
        Rectangle rectB = new Rectangle(b.x, b.y, b.width, b.height);
        return rectA.intersects(rectB);
    }

    private void resetPositions() {
        if (pacman != null) {
            pacman.x = pacman.startX;
            pacman.y = pacman.startY;
            pacman.velocityX = 0;
            pacman.velocityY = 0;
            pacman.direction = 'R';
            desiredDirection = 'R';
        }

        for (Block ghost : ghosts) {
            if (ghost == null) continue;
            ghost.x = ghost.startX;
            ghost.y = ghost.startY;
            ghost.velocityX = 0;
            ghost.velocityY = 0;
            ghost.direction = directions[random.nextInt(directions.length)];
            ghost.updateVelocity();
        }
    }

    private void restartGame() {
        score = 0;
        lives = 3;
        gameOver = false;
        paused = false;
        gameStarted = true;  // also start game on restart
        loadMap();
        resetPositions();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameStarted && !paused && !gameOver) {
            move();
            repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char key = Character.toUpperCase(e.getKeyChar());

        if (!gameStarted) {
            // On start screen, only ENTER to start the game
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                gameStarted = true;
                repaint();
            }
            return;
        }

        if (key == 'P') {
            paused = !paused;
            repaint();
            return;
        }

        if (key == 'R') {
            restartGame();
            return;
        }

        if (paused || gameOver) return;

        switch (key) {
            case 'W', 'I' -> desiredDirection = 'U';
            case 'S', 'K' -> desiredDirection = 'D';
            case 'A', 'J' -> desiredDirection = 'L';
            case 'D', 'L' -> desiredDirection = 'R';
            default -> {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_UP) desiredDirection = 'U';
                else if (code == KeyEvent.VK_DOWN) desiredDirection = 'D';
                else if (code == KeyEvent.VK_LEFT) desiredDirection = 'L';
                else if (code == KeyEvent.VK_RIGHT) desiredDirection = 'R';
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }
}
