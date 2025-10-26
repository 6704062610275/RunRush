import java.awt.*;

public abstract class GameObject {
    
    protected int x;
    protected int y;
    protected int w;
    protected int h;
    protected int speed; 
    public GameObject(int x, int y, int w, int h, int speed) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.speed = speed; 
    }

    public abstract void draw(Graphics g);
    public abstract void update();
    public abstract Rectangle getHitBox();

    public Rectangle getBounds() {
        return new Rectangle(x, y, w, h);
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getW() { return w; }
    public int getH() { return h; }
}