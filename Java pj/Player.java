import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
// import java.awt.Color;

public class Player extends GameObject { 
    
    // 1. ตัวแปรใหม่สำหรับ Animation
    private List<Image> runFrames = new ArrayList<>();
    private int currentFrame = 0;    
    private int animationTick = 0;   
    private final int FRAME_SPEED = 4; 
    
    public Player(int x, int y, int w, int h, int speed) {
        super(x, y, w, h, speed); 
        try {
            for (int i = 1; i <= 4; i++) {
                runFrames.add(new ImageIcon("img/player" + i + ".png").getImage()); 
            }
        } catch (Exception e) {
            System.err.println("Error loading player run frames: " + e.getMessage());
        }
    }

    public void updateAnimation() {
        animationTick++;
        
        if (animationTick >= FRAME_SPEED) {
            animationTick = 0; 
            currentFrame++;    
            
            if (currentFrame >= runFrames.size()) {
                currentFrame = 0;
            }
        }
    }

    @Override
    public void update() {
    }
    @Override
    public void draw(Graphics g) {
        if (!runFrames.isEmpty()) {
            Image currentImage = runFrames.get(currentFrame);
            g.drawImage(currentImage, x, y, w, h, null);
        }
        // Rectangle hitBox = getHitBox();
        // g.setColor(new Color(255, 0, 0, 120)); 
        // g.fillRect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);
    }
    
    public void moveLeft() {
        x -= speed;
    }

    public void moveRight() {
        x += speed;
    }
    
    @Override
    public Rectangle getHitBox() {
        int marginLeft = 25; 
        int marginRight = 25; 
        int marginTop = 15; 
        int marginBottom = 30; 
        
        return new Rectangle(
            x + marginLeft,
            y + marginTop,
            w - (marginLeft + marginRight),
            h - (marginTop + marginBottom)
        );
    }
}