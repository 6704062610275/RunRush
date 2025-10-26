// Runrush.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage; 

public class Runrush extends JPanel implements ActionListener, KeyListener {
    static final int WIDTH = 400, HEIGHT = 600;
    private Player player;
    private List<Obstacle> obstacles = new ArrayList<>();
    private javax.swing.Timer timer;
    private Random rand = new Random();
    private boolean leftPressed = false, rightPressed = false;
    
    private JButton startButton;
    private JButton quitButton;
    private JButton restartButton; 
    
    private enum GameState { LEVEL_SELECTION, RUNNING, GAME_OVER }
    private GameState gameState = GameState.LEVEL_SELECTION; 
    private int selectedLevel = 1; 
    
    private double distanceKm = 0.0;
    private double distancePerTick = 0.001; 
    private int tick = 0, difficultyTick = 0;
    
    private Image bg, bg1, bg2; 
    private Image gameOverImage;
    private int currentLevel = 1; 
    private int baseObstacleSpeed = 3; 
    
    private int currentObstacleSpeed; 
    private static final double SPEED_INCREASE_RATE = 0.0015; 
    
    private int roadLeft = 80;
    private int roadRight = 320;

    public Runrush() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        
        setLayout(null); 
        setupButtons(); 
        
        // **หมายเหตุ:** บรรทัดนี้ไม่สำคัญเท่าใน startGame แต่ตั้งค่า Player เริ่มต้น
        player = new Player((WIDTH - 80) / 2, HEIGHT - 100, 80, 80, 9); 
        
        loadImages();
        
        timer = new javax.swing.Timer(16, this);
        timer.start();
        
        updateComponentVisibility();
    }
    
    private void setupButtons() {
        startButton = new JButton("START GAME");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        startButton.setBackground(new Color(50, 200, 50));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusable(false); 
        startButton.addActionListener(e -> startGame()); 

        quitButton = new JButton("QUIT GAME");
        quitButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        quitButton.setBackground(new Color(200, 50, 50));
        quitButton.setForeground(Color.WHITE);
        quitButton.setFocusable(false);
        quitButton.addActionListener(e -> System.exit(0)); 
        
        restartButton = new JButton("RESTART");
        restartButton.setFont(new Font("SansSerif", Font.BOLD, 22));
        restartButton.setBackground(new Color(50, 50, 200));
        restartButton.setForeground(Color.WHITE);
        restartButton.setFocusable(false);
        restartButton.addActionListener(e -> restart());
        
        this.add(startButton);
        this.add(quitButton);
        this.add(restartButton); 
    }
    
    private void updateComponentVisibility() {
        boolean showLevelSelectionButtons = (gameState == GameState.LEVEL_SELECTION);
        boolean showRestartButton = (gameState == GameState.GAME_OVER);

        startButton.setVisible(showLevelSelectionButtons);
        quitButton.setVisible(showLevelSelectionButtons);
        restartButton.setVisible(showRestartButton);

        int buttonW = 250;
        int buttonH = 45;
        
        if (showLevelSelectionButtons) {
            int startY = HEIGHT - 110;
            int quitY = HEIGHT - 55;
            
            startButton.setBounds((WIDTH - buttonW) / 2, startY, buttonW, buttonH);
            quitButton.setBounds((WIDTH - buttonW) / 2, quitY, buttonW, buttonH);
        }
        
        if (showRestartButton) {
            restartButton.setBounds((WIDTH - buttonW) / 2, HEIGHT - 50 - buttonH, buttonW, buttonH);
        }
    }

    private void loadImages() {
        try {
            this.bg = ImageIO.read(new File("img/BG.png")); 
            this.bg1 = ImageIO.read(new File("img/BG1.png")); 
            this.bg2 = ImageIO.read(new File("img/BG2.png")); 
            this.gameOverImage = ImageIO.read(new File("img/over2.png")); 
        } catch (IOException e) {
            System.err.println("Error loading images. Using placeholders. " + e.getMessage());
            this.bg = createPlaceholderImage(Color.GREEN);
            this.bg1 = createPlaceholderImage(Color.CYAN);
            this.bg2 = createPlaceholderImage(Color.DARK_GRAY);
            this.gameOverImage = createPlaceholderImage(Color.RED);
        }
    }
    
    private Image createPlaceholderImage(Color color) {
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, 100, 100);
        g.dispose();
        return img;
    }

    private void startGame() {
        if (selectedLevel == 1) baseObstacleSpeed = 3; 
        else if (selectedLevel == 2) baseObstacleSpeed = 5; 
        else if (selectedLevel == 3) baseObstacleSpeed = 7; 
        
        currentLevel = selectedLevel; 
        
        currentObstacleSpeed = baseObstacleSpeed; 
        
        final double NORMAL_SPEED = 5.0; 
        final double BASE_INCREMENT = 0.001; 
        this.distancePerTick = BASE_INCREMENT * (baseObstacleSpeed / NORMAL_SPEED);
        
        distanceKm = 0.0;
        tick = 0;
        difficultyTick = 0;
        
        // 🚀 แก้ไขตรงนี้: กำหนดความเร็วของ Player ให้เป็น 9 คงที่
        player = new Player((WIDTH - 80) / 2, HEIGHT - 100, 80, 80, 9);
        
        obstacles.clear();
        gameState = GameState.RUNNING;
        
        updateComponentVisibility();
        requestFocusInWindow(); 
    }

    private void restart() {
        obstacles.clear();
        distanceKm = 0.0;
        tick = 0;
        difficultyTick = 0;
        
        gameState = GameState.LEVEL_SELECTION; 
        selectedLevel = 1; 
        
        player.setX((WIDTH - player.getW()) / 2);
        player.setY(HEIGHT - 100);
        
        updateComponentVisibility();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == GameState.RUNNING) {
            tick++;
            difficultyTick++;
            
            currentObstacleSpeed = baseObstacleSpeed + (int)(difficultyTick * SPEED_INCREASE_RATE);
            
            player.updateAnimation(); 

            if (leftPressed) player.moveLeft();
            if (rightPressed) player.moveRight();

            player.setX(Math.max(roadLeft, Math.min(roadRight - player.getW(), player.getX())));
            player.setY(HEIGHT - player.getH() - 20);

            int interval = Math.max(18, 50 - difficultyTick / 800); 
            if (tick % interval == 0) spawnObstacle();

            Iterator<Obstacle> it = obstacles.iterator();
            while (it.hasNext()) {
                Obstacle o = it.next();
                
                o.setSpeed(currentObstacleSpeed);
                o.update();

                if (o.getY() > HEIGHT) {
                    it.remove();
                } else {
                    if (player.getHitBox().intersects(o.getHitBox())) {
                        gameState = GameState.GAME_OVER; 
                        updateComponentVisibility(); 
                        break;
                    }
                }
            }
            
            distanceKm += distancePerTick; 
        }
        repaint();
    }
    
    private void spawnObstacle() {
        int kind = rand.nextInt(3); 
        int w, h;
        String path;
        
        if (currentLevel == 2) {
            int kindLevel2 = rand.nextInt(2); 
            if (kindLevel2 == 0) {
                w = 90; h = 120; path = "img/rock.png"; 
            } else {
                w = 90; h = 90; path = "img/fire.png"; 
            }
        } else if (currentLevel == 3) {
            int kindLevel3 = rand.nextInt(3); 
            
            if (kindLevel3 == 0) {
                w = 70; h = 70; path = "img/donut.png"; 
            } else if (kindLevel3 == 1) {
                w = 70; h = 70; path = "img/cookie.png"; 
            } else { 
                w = 70; h = 70; path = "img/j.png"; 
            }
        } else {
            switch (kind) {
                case 0: w = 60; h = 80; path = "img/motor.png"; break;
                case 1: w = 80; h = 110; path = "img/van.png"; break;
                case 2: w = 65; h = 90; path = "img/car.png"; break;
                default: w = 65; h = 90; path = "img/car.png"; break; 
            }
        }
        
        int x = rand.nextInt(roadRight - roadLeft - w) + roadLeft;
        
        int speed = currentObstacleSpeed + rand.nextInt(2); 
        
        obstacles.add(new Obstacle(x, -h, w, h, speed, path));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Image currentBG;
        if (currentLevel == 1) currentBG = bg; 
        else if (currentLevel == 2) currentBG = bg1; 
        else currentBG = bg2; 
        g.drawImage(currentBG, 0, 0, WIDTH, HEIGHT, null);

        if (gameState == GameState.RUNNING || gameState == GameState.GAME_OVER) {
            player.draw(g);
            for (Obstacle o : obstacles) o.draw(g);
            
            if (gameState == GameState.RUNNING) {
                 g.setFont(new Font("SansSerif", Font.BOLD, 16));
                 String distanceText = String.format("DISTANCE: %.1f km", distanceKm);
                 FontMetrics fm = g.getFontMetrics();
                 int textW = fm.stringWidth(distanceText);
                 int textH = fm.getHeight();
                 g.setColor(Color.WHITE);
                 g.fillRect(10, 2, textW + 6, textH);
                 g.setColor(Color.BLACK);
                 g.drawRect(10, 2, textW + 6, textH);
                 g.setColor(Color.BLACK);
                 g.drawString(distanceText, 13, 2 + fm.getAscent());
            }
        }

        switch (gameState) {
            case LEVEL_SELECTION:
                drawLevelSelection(g);
                break;
            case GAME_OVER:
                drawGameOver(g);
                break;
            case RUNNING:
                break;
        }
    }

    private void drawLevelSelection(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200)); 
        g.fillRect(0, 0, WIDTH, HEIGHT);

        String title = "SELECT DIFFICULTY LEVEL";
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, 50);

        Image[] levelBGs = {bg, bg1, bg2};
        String[] levelNames = {"EASY (3)", "NORMAL (5)", "HARD (7)"}; 
        
        int boxWidth = 100;
        int boxHeight = 150;
        int padding = 30;
        int startX = (WIDTH - (3 * boxWidth + 2 * padding)) / 2;
        int startY = 150;

        for (int i = 0; i < 3; i++) {
            int x = startX + i * (boxWidth + padding);
            int y = startY;
            
            if (selectedLevel == i + 1) {
                g.setColor(Color.YELLOW);
                g.fillRect(x - 5, y - 5, boxWidth + 10, boxHeight + 10);
            }

            Image preview = levelBGs[i];
            int previewH = boxHeight - 30;
            g.drawImage(preview, x, y, boxWidth, previewH, null);
            
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(x, y + previewH, boxWidth, 30);
            
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            String name = levelNames[i];
            g.drawString(name, x + (boxWidth - g.getFontMetrics().stringWidth(name)) / 2, y + previewH + 20);
        }
    }
    
    private void drawGameOver(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150)); 
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        if (gameOverImage != null) {
            int imgW = gameOverImage.getWidth(null);
            int imgH = gameOverImage.getHeight(null);
            
            double scale = 1.0;
            if (imgW > WIDTH * 0.9 || imgH > HEIGHT * 0.9) { 
                scale = Math.min((double)WIDTH * 0.9 / imgW, (double)HEIGHT * 0.9 / imgH);
            }
            int scaledW = (int)(imgW * scale);
            int scaledH = (int)(imgH * scale);

            int x = (WIDTH - scaledW) / 2;
            int y = (HEIGHT - scaledH) / 2 - 50; 

            g.drawImage(gameOverImage, x, y, scaledW, scaledH, null);
        }
        
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        
        String finalDistanceText = String.format("FINAL DISTANCE: %.1f km", distanceKm);
        g.drawString(finalDistanceText, (WIDTH - fm.stringWidth(finalDistanceText)) / 2, HEIGHT - 120);
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        
        if (gameState == GameState.RUNNING) {
            if (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_A) leftPressed = true;
            if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D) rightPressed = true;
        
        } else if (gameState == GameState.LEVEL_SELECTION) {
            if (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_A) {
                selectedLevel = Math.max(1, selectedLevel - 1);
            } else if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D) {
                selectedLevel = Math.min(3, selectedLevel + 1);
            } else if (k == KeyEvent.VK_ENTER || k == KeyEvent.VK_SPACE) {
                startGame(); 
            }
            
        } else if (gameState == GameState.GAME_OVER) {
            if (k == KeyEvent.VK_R) restart(); 
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_A) leftPressed = false;
        if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D) rightPressed = false;
    }

    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Runrush OOP");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setContentPane(new Runrush());
            f.pack();
            f.setResizable(false);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}