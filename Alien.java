import java.awt.*;

public class Alien {
    private int x, y;
    private int speed;
    private Image img;

    public Alien(int x, int y, int speed, Image img) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.img = img;
    }

    public void update() {
        y += speed;
    }

    public void draw(Graphics g) {
        g.drawImage(img, x, y, null);
    }

    public int getY() {
        return y;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, img.getWidth(null), img.getHeight(null));
    }
}
