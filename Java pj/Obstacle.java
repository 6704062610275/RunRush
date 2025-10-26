// Obstacle.java
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class Obstacle {
    private int x, y, w, h;
    private int speed;
    private Image image;

    public Obstacle(int x, int y, int w, int h, int speed, String imagePath) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.speed = speed;
        try {
            this.image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.err.println("Error loading obstacle image: " + imagePath);
            this.image = createPlaceholderImage(w, h, Color.BLUE);
        }
    }
    
    private Image createPlaceholderImage(int width, int height, Color color) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        g.dispose();
        return img;
    }

    public void setSpeed(int newSpeed) {
        this.speed = newSpeed;
    }

    public void update() {
        // ใช้ตัวแปร speed ในการเคลื่อนที่
        y += speed; 
    }

    public void draw(Graphics g) {
        g.drawImage(image, x, y, w, h, null);
        
        // สำหรับการ Debug ดู Hit Box (ลบออกได้)
        // g.setColor(Color.RED);
        // g.drawRect(getHitBox().x, getHitBox().y, getHitBox().width, getHitBox().height);
    }

    public Rectangle getHitBox() {
        // ปรับ Hit box 
        int hitBoxX = x + (w / 4);
        int hitBoxY = y + (h / 4);
        int hitBoxW = w / 2;
        int hitBoxH = h / 2;
        return new Rectangle(hitBoxX, hitBoxY, hitBoxW, hitBoxH);
    }
    public int getY() { return y; }
}