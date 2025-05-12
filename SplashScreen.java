import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.geom.AffineTransform;
import java.util.Arrays;

public class SplashScreen {
    private JWindow window;

    public void showSplash() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        window = new JWindow();
        JPanel content = new JPanel() {
            private BufferedImage backgroundImage;
            private long startTime = System.currentTimeMillis();

            {
                try {
                    backgroundImage = ImageIO.read(new File("OIP.jpg")); // Your splash background
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }

               
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

                int titleY = 150; 
                
                Font titleFont = new Font("Brush Script MT", Font.BOLD, 90);
                
                if (!isFontAvailable("Brush Script MT")) {
                    titleFont = new Font("Comic Sans MS", Font.BOLD, 90);
                    if (!isFontAvailable("Comic Sans MS")) {
                        titleFont = new Font("Serif", Font.ITALIC, 90);
                    }
                }
                g2d.setFont(titleFont);
                
                long elapsed = System.currentTimeMillis() - startTime;
                float pulse = (float) (0.5 + 0.5 * Math.sin(elapsed / 500.0));
                
                String title = "Cosmo Breakout";
                FontMetrics fm = g2d.getFontMetrics();
                int titleWidth = fm.stringWidth(title);
                int titleX = width / 2 - titleWidth / 2;
                
                for (int i = 8; i > 0; i--) {
                    float alpha = (0.1f + 0.05f * pulse) * (9-i)/9f;
                    g2d.setColor(new Color(0f, 1f, 0.5f, alpha)); 
                    g2d.drawString(title, titleX - i/2, titleY - i/2);
                    g2d.drawString(title, titleX + i/2, titleY + i/2);
                }
                
                GradientPaint gradient = new GradientPaint(
                    titleX, titleY - 50, new Color(0, 255, 180), 
                    titleX, titleY + 10, new Color(0, 200, 255) 
                );
                
                g2d.setPaint(gradient);
                g2d.drawString(title, titleX, titleY);
                
                g2d.setColor(new Color(180, 255, 220, 180));
                g2d.setStroke(new BasicStroke(1.5f));
                Shape titleShape = titleFont.createGlyphVector(g2d.getFontRenderContext(), title).getOutline();
                AffineTransform at = AffineTransform.getTranslateInstance(titleX, titleY);
                g2d.draw(at.createTransformedShape(titleShape));
                
                Font subtitleFont = new Font("Brush Script MT", Font.ITALIC, 40);
                if (!isFontAvailable("Brush Script MT")) {
                    subtitleFont = new Font("Comic Sans MS", Font.ITALIC, 36);
                    if (!isFontAvailable("Comic Sans MS")) {
                        subtitleFont = new Font("Serif", Font.ITALIC, 36);
                    }
                }
                g2d.setFont(subtitleFont);
                String subtitle = "Defend the Galaxy";
                int subtitleWidth = g2d.getFontMetrics().stringWidth(subtitle);
                
                GradientPaint subtitleGradient = new GradientPaint(
                    0, titleY + 60, new Color(100, 255, 200),
                    0, titleY + 90, new Color(150, 200, 255)
                );
                g2d.setPaint(subtitleGradient);
                g2d.drawString(subtitle, width / 2 - subtitleWidth / 2, titleY + 80);
                
                g2d.setColor(new Color(180, 255, 220));
                g2d.setFont(new Font("Arial", Font.PLAIN, 18));
                String loadingText = "Loading...";
                g2d.drawString(loadingText, width / 2 - g2d.getFontMetrics().stringWidth(loadingText) / 2, height - 50);
                
                int barWidth = 300;
                int barHeight = 10;
                int barX = width / 2 - barWidth / 2;
                int barY = height - 30;
                
                g2d.setColor(new Color(0, 50, 50, 150));
                g2d.fillRoundRect(barX, barY, barWidth, barHeight, 10, 10);
                
                int progress = (int)(barWidth * ((System.currentTimeMillis() % 3000) / 3000.0));
                GradientPaint barGradient = new GradientPaint(
                    barX, barY, new Color(0, 255, 200),
                    barX + progress, barY, new Color(100, 200, 255)
                );
                g2d.setPaint(barGradient);
                g2d.fillRoundRect(barX, barY, progress, barHeight, 10, 10);
            }
            
            private boolean isFontAvailable(String fontName) {
                return Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getAvailableFontFamilyNames()).contains(fontName);
            }
        };

        window.setContentPane(content);
        window.setSize(width, height); 
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        try {
            Thread.sleep(3000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        window.setVisible(false);
        window.dispose(); 
    }
}
