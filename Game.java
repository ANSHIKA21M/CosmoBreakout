import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Game extends JPanel implements ActionListener, KeyListener {

    private Timer timer;
    private Player player;
    private ArrayList<Alien> aliens;
    private ArrayList<Bullet> bullets;
    private Random random = new Random();

    private int score = 0, level = 1, lives = 3;
    private boolean gameOver = false;
    private long lastAlienSpawnTime = 0;
    private long gameStartTime;

    private int screenWidth, screenHeight;

    private final int playerWidth = 60;
    private final int playerHeight = 25;

    private final int alienWidth = 40;
    private final int alienHeight = 35;

    private final int bulletWidth = 4;
    private final int bulletHeight = 10;
    
    private Image cyanAlien, magentaAlien, yellowAlien;
    private Image[] alienImages;
    
    private Image heartIcon, starIcon, trophyIcon, homeIcon;
    
    private boolean isFading = false;
    private float opacity = 1.0f;
    private Timer fadeTimer;
    private JFrame frame;

    public Game(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
        this.gameStartTime = System.currentTimeMillis();
        
        this.frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (this.frame == null) {
            SwingUtilities.invokeLater(() -> {
                this.frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            });
        }

        setFocusable(true);
        setBackground(new Color(0, 30, 40));  
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        addKeyListener(this);
        
        cyanAlien = new ImageIcon("alien-cyan.png").getImage();
        magentaAlien = new ImageIcon("alien-magenta.png").getImage();
        yellowAlien = new ImageIcon("alien-yellow.png").getImage();
        alienImages = new Image[]{cyanAlien, magentaAlien, yellowAlien};
        
        try {
            heartIcon = new ImageIcon("heart.png").getImage();
            if (heartIcon.getWidth(null) <= 0) throw new Exception("Invalid heart icon");
        } catch (Exception e) {
            heartIcon = createHeartIcon();
        }
        
        try {
            starIcon = new ImageIcon("star.png").getImage();
            if (starIcon.getWidth(null) <= 0) throw new Exception("Invalid star icon");
        } catch (Exception e) {
            starIcon = createStarIcon();
        }
        
        try {
            trophyIcon = new ImageIcon("trophy.png").getImage();
            if (trophyIcon.getWidth(null) <= 0) throw new Exception("Invalid trophy icon");
        } catch (Exception e) {
            trophyIcon = createTrophyIcon();
        }
        
        try {
            homeIcon = new ImageIcon("home.png").getImage();
            if (homeIcon.getWidth(null) <= 0) throw new Exception("Invalid home icon");
        } catch (Exception e) {
            homeIcon = createHomeIcon();
        }

        player = new Player(screenWidth / 2 - playerWidth / 2, screenHeight - 60);
        bullets = new ArrayList<>();
        aliens = new ArrayList<>();

        lastAlienSpawnTime = System.currentTimeMillis();
        
        timer = new Timer(16, this);
        timer.start();
        
        initFadeEffect();
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getX() >= 20 && e.getX() <= 70 && 
                    e.getY() >= 20 && e.getY() <= 70) {
                    startFadeOut();
                }
            }
        });
    }
    
    private Image createHeartIcon() {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int[] xPoints = {16, 8, 0, 8, 16, 24, 32, 24};
        int[] yPoints = {8, 0, 8, 16, 24, 16, 8, 0};
        
        g2.setColor(new Color(255, 60, 100));
        g2.fillPolygon(xPoints, yPoints, 8);
        g2.setColor(new Color(200, 0, 50));
        g2.drawPolygon(xPoints, yPoints, 8);
        g2.dispose();
        
        return img;
    }
    
    private Image createStarIcon() {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int[] xPoints = {16, 21, 32, 23, 26, 16, 6, 9, 0, 11};
        int[] yPoints = {0, 10, 10, 16, 28, 22, 28, 16, 10, 10};
        
        g2.setColor(new Color(255, 215, 0));
        g2.fillPolygon(xPoints, yPoints, 10);
        g2.setColor(new Color(230, 180, 0));
        g2.drawPolygon(xPoints, yPoints, 10);
        g2.dispose();
        
        return img;
    }
    
    private Image createTrophyIcon() {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(new Color(255, 215, 0));
        g2.fillRect(10, 5, 12, 3);
        g2.fillRect(12, 8, 8, 16);
        g2.fillRect(8, 24, 16, 3);
        g2.fillRect(13, 27, 6, 5);
        
        g2.setColor(new Color(230, 180, 0));
        g2.drawRect(10, 5, 12, 3);
        g2.drawRect(12, 8, 8, 16);
        g2.drawRect(8, 24, 16, 3);
        g2.drawRect(13, 27, 6, 5);
        g2.dispose();
        
        return img;
    }
    
    private Image createHomeIcon() {
        BufferedImage img = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(new Color(40, 200, 150));
        
        int[] xPoints = {20, 5, 10, 10, 30, 30, 35};
        int[] yPoints = {5, 20, 20, 35, 35, 20, 20};
        
        g2.fillPolygon(xPoints, yPoints, 7);
        g2.setColor(new Color(20, 150, 120));
        g2.drawPolygon(xPoints, yPoints, 7);
        g2.dispose();
        
        return img;
    }
    
    private void initFadeEffect() {
        fadeTimer = new Timer(50, e -> {
            opacity -= 0.05f;
            if (opacity <= 0) {
                opacity = 0;
                ((Timer)e.getSource()).stop();
                returnToMainMenu();
            }
            repaint();
        });
    }
    
    private void startFadeOut() {
        if (!isFading) {
            isFading = true;
            fadeTimer.start();
        }
    }
    
    private void returnToMainMenu() {
        timer.stop();
        
        if (frame == null) {
            frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        }
        
        if (frame != null) {
            frame.dispose();
            SwingUtilities.invokeLater(() -> {
                new MainMenu(); 
            });
        } else {
            System.exit(0);
        }
    }
    
    private void addNewAlien() {
        if (aliens.size() < level * 5) { 
            int x = random.nextInt(screenWidth - alienWidth);
            aliens.add(new Alien(x, -alienHeight, random.nextInt(3))); 
            lastAlienSpawnTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        if (isFading) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        }
        
        drawStarBackground(g2d);

        drawPlayer(g2d);

        drawBullets(g2d);

        drawAliens(g2d);
        
        g2d.dispose();
        
        drawHUD(g);
        
        if (gameOver) {
            drawGameOver(g);
        }
    }
    
    private void drawStarBackground(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, 100));
        long currentTime = System.currentTimeMillis();
        Random rand = new Random(42); 
        
        for (int i = 0; i < 100; i++) {
            int x = rand.nextInt(screenWidth);
            int y = rand.nextInt(screenHeight);
            int size = rand.nextInt(3) + 1;
            
            float brightness = 0.5f + 0.5f * (float)Math.sin((currentTime + i * 100) / 1000.0);
            g2d.setColor(new Color(1f, 1f, 1f, 0.3f + 0.7f * brightness));
            
            g2d.fillRect(x, y, size, size);
        }
    }
    
    private void drawPlayer(Graphics2D g2d) {
        Composite originalComposite = g2d.getComposite();
        for (int i = 10; i > 0; i--) {
            float alpha = 0.03f * i;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.setColor(new Color(0, 255, 150));
            g2d.fillRoundRect(player.x - i, player.y - i, 
                          playerWidth + i*2, playerHeight + i*2, 
                          10, 10);
        }
        g2d.setComposite(originalComposite);
        
        GradientPaint gradient = new GradientPaint(
            player.x, player.y, new Color(0, 255, 150),
            player.x, player.y + playerHeight, new Color(0, 150, 100)
        );
        g2d.setPaint(gradient);
        g2d.fillRoundRect(player.x, player.y, playerWidth, playerHeight, 10, 10);
        
        g2d.setColor(new Color(0, 255, 200, 150));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(player.x, player.y, playerWidth, playerHeight, 10, 10);
        
        GradientPaint engineGlow = new GradientPaint(
            player.x + playerWidth/2, player.y + playerHeight,
            new Color(255, 100, 0, 200),
            player.x + playerWidth/2, player.y + playerHeight + 15,
            new Color(255, 100, 0, 0)
        );
        g2d.setPaint(engineGlow);
        g2d.fillRect(player.x + playerWidth/2 - 5, player.y + playerHeight, 10, 15);
    }
    
    private void drawBullets(Graphics2D g2d) {
        for (Bullet b : bullets) {
            GradientPaint trail = new GradientPaint(
                b.x, b.y + bulletHeight,
                new Color(0, 200, 255, 150),
                b.x, b.y + bulletHeight + 20,
                new Color(0, 200, 255, 0)
            );
            g2d.setPaint(trail);
            g2d.fillRect(b.x - 1, b.y + bulletHeight, bulletWidth + 2, 20);
            
            g2d.setColor(new Color(0, 200, 255));
            g2d.fillRect(b.x, b.y, bulletWidth, bulletHeight);
            
            Composite originalComposite = g2d.getComposite();
            for (int i = 3; i > 0; i--) {
                float alpha = 0.1f * i;
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2d.setColor(new Color(0, 200, 255));
                g2d.fillRect(b.x - i, b.y - i, bulletWidth + i*2, bulletHeight + i*2);
            }
            g2d.setComposite(originalComposite);
        }
    }
    
    private void drawAliens(Graphics2D g2d) {
        for (Alien a : aliens) {
            Composite originalComposite = g2d.getComposite();
            for (int i = 5; i > 0; i--) {
                float alpha = 0.03f * i;
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2d.setColor(a.imageIndex == 0 ? new Color(0, 255, 255) : 
                           a.imageIndex == 1 ? new Color(255, 0, 255) :
                           new Color(255, 255, 0));
                g2d.fillOval(a.x - i, a.y - i, alienWidth + i*2, alienHeight + i*2);
            }
            g2d.setComposite(originalComposite);
            
            g2d.drawImage(alienImages[a.imageIndex], a.x, a.y, alienWidth, alienHeight, null);
        }
    }
    
    private void drawHUD(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        Font hudFont = new Font("Brush Script MT", Font.BOLD, 24);
        if (!isFontAvailable("Brush Script MT")) {
            hudFont = new Font("Comic Sans MS", Font.BOLD, 22);
            if (!isFontAvailable("Comic Sans MS")) {
                hudFont = new Font("Serif", Font.BOLD, 22);
            }
        }
        g2d.setFont(hudFont);
        
        int scoreY = 40;
        g2d.drawImage(starIcon, 20, scoreY - 25, 32, 32, null);
        g2d.setColor(new Color(255, 215, 0)); 
        g2d.drawString("Score: " + score, 60, scoreY);
        
        int livesY = 80;
        for (int i = 0; i < lives; i++) {
            g2d.drawImage(heartIcon, 20 + i * 35, livesY - 25, 32, 32, null);
        }
        
        int levelY = 40;
        g2d.drawImage(trophyIcon, screenWidth - 150, levelY - 25, 32, 32, null);
        g2d.setColor(new Color(255, 215, 0)); // Gold color
        g2d.drawString("Level: " + level, screenWidth - 110, levelY);
        
        long gameTimeSeconds = (System.currentTimeMillis() - gameStartTime) / 1000;
        String timeStr = String.format("%02d:%02d", gameTimeSeconds / 60, gameTimeSeconds % 60);
        g2d.setColor(new Color(200, 200, 255));
        g2d.drawString(timeStr, screenWidth / 2 - 30, 40);
        
        int buttonSize = 50;
        int buttonX = 20;
        int buttonY = 20;
        
        
        GradientPaint gradient = new GradientPaint(
            buttonX, buttonY, new Color(0, 180, 150, 200),
            buttonX, buttonY + buttonSize, new Color(0, 100, 100, 200)
        );
        g2d.setPaint(gradient);
        g2d.fillRoundRect(buttonX, buttonY, buttonSize, buttonSize, 15, 15);
        
        
        g2d.setColor(new Color(100, 255, 200, 150));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(buttonX, buttonY, buttonSize, buttonSize, 15, 15);
        
        
        g2d.drawImage(homeIcon, buttonX + 5, buttonY + 5, buttonSize - 10, buttonSize - 10, null);
        
        g2d.dispose();
    }
    
    private void drawGameOver(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, screenWidth, screenHeight);
        
        Font gameOverFont = new Font("Arial", Font.BOLD, 60);
        g2d.setFont(gameOverFont);
        String gameOverText = "GAME OVER";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(gameOverText);
        int x = screenWidth / 2 - textWidth / 2;
        int y = screenHeight / 2;
        
        for (int i = 10; i > 0; i--) {
            float alpha = 0.03f * i;
            g2d.setColor(new Color(1f, 0f, 0f, alpha));
            g2d.drawString(gameOverText, x - i/2, y - i/2);
            g2d.drawString(gameOverText, x + i/2, y + i/2);
        }
        
        g2d.setColor(Color.RED);
        g2d.drawString(gameOverText, x, y);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        g2d.setColor(new Color(255, 215, 0));
        String finalScore = "Final Score: " + score;
        g2d.drawString(finalScore, screenWidth / 2 - g2d.getFontMetrics().stringWidth(finalScore) / 2, y + 60);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 24));
        g2d.setColor(Color.WHITE);
        String restartText = "Press ESC to return to menu";
        g2d.drawString(restartText, screenWidth / 2 - g2d.getFontMetrics().stringWidth(restartText) / 2, y + 120);
        
        g2d.dispose();
    }
    
    private boolean isFontAvailable(String fontName) {
        return Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getAvailableFontFamilyNames()).contains(fontName);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver || isFading) return;

        player.update(screenWidth);
        
        long currentTime = System.currentTimeMillis();
        long spawnDelay = 3000 - (level * 200); 
        if (spawnDelay < 500) spawnDelay = 500;
        
        if (currentTime - lastAlienSpawnTime > spawnDelay) {
            addNewAlien();
        }
        
        for (Bullet b : bullets) b.update();
        bullets.removeIf(b -> b.y < 0);

        for (Alien a : aliens) {
            a.update(level);
        }

        ArrayList<Alien> aliensToRemove = new ArrayList<>();
        ArrayList<Bullet> bulletsToRemove = new ArrayList<>();

        for (Bullet b : bullets) {
            for (Alien a : aliens) {
                if (b.x >= a.x && b.x <= a.x + alienWidth && b.y >= a.y && b.y <= a.y + alienHeight) {
                    aliensToRemove.add(a);
                    bulletsToRemove.add(b);
                    score += 10;
                }
            }
        }

        aliens.removeAll(aliensToRemove);
        bullets.removeAll(bulletsToRemove);

        ArrayList<Alien> reachedBottomAliens = new ArrayList<>();
        for (Alien a : aliens) {
            if (a.y + alienHeight >= screenHeight) {
                lives--;
                reachedBottomAliens.add(a);
                if (lives <= 0) {
                    gameOver = true;
                }
            }
        }
        aliens.removeAll(reachedBottomAliens);

        if (score >= level * 100) {
            level++;
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                startFadeOut();
            }
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) player.left = true;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) player.right = true;

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            bullets.add(new Bullet(player.x + playerWidth / 2 - 2, player.y));
        }

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            startFadeOut();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) player.left = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) player.right = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    // Player Class
    class Player {
        int x, y, speed = 7;
        boolean left, right;

        Player(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void update(int maxWidth) {
            if (left && x > 0) x -= speed;
            if (right && x < maxWidth - playerWidth) x += speed;
        }
    }

    class Alien {
        int x, y, imageIndex;
        float baseSpeed = 1.0f; 
        float speed;

        Alien(int x, int y, int imageIndex) {
            this.x = x;
            this.y = y;
            this.imageIndex = imageIndex;
            this.speed = baseSpeed;
        }

        void update(int level) {
            speed = baseSpeed + (level * 0.2f);
            y += speed;
            
            x += Math.sin(y / 30.0) * 2;
            
            if (x < 0) x = 0;
            if (x > screenWidth - alienWidth) x = screenWidth - alienWidth;
        }
    }

    class Bullet {
        int x, y, speed = 8;

        Bullet(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void update() {
            y -= speed;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("✨ SPACE GUARDIANS ✨");
        frame.setUndecorated(true);
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        Game game = new Game(width, height);
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }
}
