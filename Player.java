import java.awt.*;
import javax.swing.ImageIcon;

public class Player {
    int x;
    int y;
    boolean left;
    boolean right;
    private Image img;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.img = new ImageIcon("player.png").getImage();
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void update() {
        if (left) x -= 5;
        if (right) x += 5;

        if (x < 0) x = 0;
        if (x > 464) x = 464;
    }

    public void draw(Graphics g) {
        g.drawImage(img, x, y, null);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
