package ui;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
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
            "O       bpo       O",
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

    // New: Track desired direction for smoother turning
    private char desiredDirection = 'R'; // Initial direction

    // New: Pause flag
    private boolean paused = false;

    public GameBoard() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        // Load images
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
            return new ImageIcon(getClass().getResource(path)).getImage();
        } catch (Exception e) {
            System.err.println("Image not found: " + path);
            return null;
        }
    }

    public void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char tileMapChar = tileMap[r].charAt(c);

                int x = c * tileSize;
                int y = r * tileSize;

                switch (tileMapChar) {
                    case 'X' -> walls.add(new Block(wallImage, x, y, tileSize, tileSize));
                    case 'b' -> ghosts.add(new Block(blueGhostImage, x, y, tileSize, tileSize));
                    case 'o' -> ghosts.add(new Block(orangeGhostImage, x, y, tileSize, tileSize));
                    case 'p' -> ghosts.add(new Block(pinkGhostImage, x, y, tileSize, tileSize));
                    case 'r' -> ghosts.add(new Block(redGhostImage, x, y, tileSize, tileSize));
                    case 'P' -> pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                    case ' ' -> foods.add(new Block(null, x + 14, y + 14, 4, 4));
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);

        // Draw paused overlay if paused
        if (paused) {
            g.setColor(new Color(255, 255, 255, 150)); // semi-transparent white overlay
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String pauseMsg = "PAUSED";
            FontMetrics fm = g.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(pauseMsg)) / 2;
            int y = getHeight() / 2;
            g.drawString(pauseMsg, x, y);
        }
    }

    public void draw(Graphics g) {
        for (Block wall : walls) {
            if (wall.image != null)
                g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        for (Block ghost : ghosts) {
            if (ghost.image != null)
                g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        if (pacman.image != null)
            g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            g.drawString("Game Over! Final Score: " + score, tileSize / 2, tileSize / 2);
        } else {
            g.drawString("Lives: " + lives + "  Score: " + score, tileSize / 2, tileSize / 2);
        }
    }

    public void move() {
        // Don't move if paused or game over
        if (paused || gameOver) return;

        // Try to change direction to desiredDirection if possible
        if (canMoveInDirection(pacman, desiredDirection)) {
            pacman.updateDirection(desiredDirection);
            switch (desiredDirection) {
                case 'U' -> pacman.image = pacmanUpImage;
                case 'D' -> pacman.image = pacmanDownImage;
                case 'L' -> pacman.image = pacmanLeftImage;
                case 'R' -> pacman.image = pacmanRightImage;
            }
        }

        // Move Pacman by velocity
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        // Collision with walls - revert if collided
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        // Ghosts movement and collision
        for (Block ghost : ghosts) {
            if (collision(ghost, pacman)) {
                lives--;
                if (lives <= 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }

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
        }

        // Check food eaten
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
                break;
            }
        }
        if (foodEaten != null) foods.remove(foodEaten);

        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    private boolean canMoveInDirection(Block block, char direction) {
        int speed = tileSize / 4;
        int nextX = block.x;
        int nextY = block.y;

        switch (direction) {
            case 'U' -> nextY -= speed;
            case 'D' -> nextY += speed;
            case 'L' -> nextX -= speed;
            case 'R' -> nextX += speed;
        }

        Block nextPos = new Block(block.image, nextX, nextY, block.width, block.height);
        for (Block wall : walls) {
            if (collision(nextPos, wall)) return false;
        }
        return true;
    }

    private boolean collision(Block a, Block b) {
        Rectangle rectA = new Rectangle(a.x, a.y, a.width, a.height);
        Rectangle rectB = new Rectangle(b.x, b.y, b.width, b.height);
        return rectA.intersects(rectB);
    }

    private void resetPositions() {
        pacman.x = pacman.startX;
        pacman.y = pacman.startY;
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        pacman.direction = 'R';
        desiredDirection = 'R';

        for (Block ghost : ghosts) {
            ghost.x = ghost.startX;
            ghost.y = ghost.startY;
            ghost.velocityX = 0;
            ghost.velocityY = 0;
            ghost.direction = directions[random.nextInt(directions.length)];
            ghost.updateVelocity();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!paused && !gameOver) {
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

        if (key == 'P') {
            paused = !paused;
            repaint();
            return;
        }

        if (paused || gameOver) return;

        switch (key) {
            case 'W', 'I', KeyEvent.VK_UP -> desiredDirection = 'U';
            case 'S', 'K', KeyEvent.VK_DOWN -> desiredDirection = 'D';
            case 'A', 'J', KeyEvent.VK_LEFT -> desiredDirection = 'L';
            case 'D', 'L', KeyEvent.VK_RIGHT -> desiredDirection = 'R';
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }
}