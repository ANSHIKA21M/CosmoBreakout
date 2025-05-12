import java.awt.*;

public class Bullet {
    private int x, y;
    private final int speed = 8;

    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        y -= speed;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, 4, 10);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 4, 10);
    }
}
