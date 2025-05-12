import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Arrays;

public class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Cosmo Breakout - Main Menu");

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel() {
            Image bg = new ImageIcon("OIP.jpg").getImage(); 
            private final Color NEON_CYAN = new Color(0, 255, 255);
            private long startTime = System.currentTimeMillis();
            
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
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
                int titleY = 150;
                
                
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
                AffineTransform transform = AffineTransform.getTranslateInstance(titleX, titleY);
                g2d.draw(transform.createTransformedShape(titleShape));
            }
            
            private boolean isFontAvailable(String fontName) {
                return Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getAvailableFontFamilyNames()).contains(fontName);
            }
        };
        panel.setLayout(null);

        JButton playBtn = new JButton() {
            private long startTime = System.currentTimeMillis();
            private Color glowColor = new Color(40, 255, 150); 
            private boolean isHovered = false;
            
            {
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                long elapsed = System.currentTimeMillis() - startTime;
                float pulse = (float) (0.7 + 0.3 * Math.sin(elapsed / 300.0));
                
                if (isHovered) {
                    for (int i = 10; i > 0; i--) {
                        float alpha = 0.05f * (11-i) * pulse;
                        g2d.setColor(new Color(
                            glowColor.getRed()/255f, 
                            glowColor.getGreen()/255f, 
                            glowColor.getBlue()/255f, 
                            alpha
                        ));
                        g2d.fillRoundRect(i/2, i/2, getWidth()-i, getHeight()-i, 30, 30);
                    }
                }
                
                GradientPaint gradient;
                if (isHovered) {
                    gradient = new GradientPaint(
                        0, 0, new Color(20, 255, 180), 
                        0, getHeight(), new Color(0, 180, 150) 
                    );
                } else {
                    gradient = new GradientPaint(
                        0, 0, new Color(0, 180, 150),
                        0, getHeight(), new Color(0, 100, 100) 
                    );
                }
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
                
                g2d.setStroke(new BasicStroke(2f));
                g2d.setColor(new Color(100, 255, 200, isHovered ? 230 : 150)); // Green-cyan border
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
                
                String text = "Play";
                Font buttonFont = new Font("Brush Script MT", Font.BOLD, 40);
                if (!Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getAvailableFontFamilyNames()).contains("Brush Script MT")) {
                    buttonFont = new Font("Comic Sans MS", Font.BOLD, 36);
                    if (!Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getAvailableFontFamilyNames()).contains("Comic Sans MS")) {
                        buttonFont = new Font("Serif", Font.ITALIC, 36);
                    }
                }
                
                g2d.setFont(buttonFont);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                int textX = (getWidth() - textWidth) / 2;
                int textY = (getHeight() - textHeight) / 2 + fm.getAscent();
                
                g2d.setColor(new Color(0, 50, 50, 100));
                g2d.drawString(text, textX+2, textY+2);
                
                g2d.setColor(isHovered ? Color.WHITE : new Color(220, 255, 240)); 
                g2d.drawString(text, textX, textY);
            }
        };
        
        int buttonWidth = 220;
        int buttonHeight = 80;
        playBtn.setBounds(width / 2 - buttonWidth / 2, height / 2 + 150, buttonWidth, buttonHeight);
        
        playBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();

                JFrame gameWindow = new JFrame("✨ SPACE GUARDIANS ✨");
                gameWindow.setUndecorated(true);

                int w = gd.getDisplayMode().getWidth();
                int h = gd.getDisplayMode().getHeight();

                Game game = new Game(w, h); 
                gameWindow.add(game);
                gameWindow.pack();
                gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                gameWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
                gameWindow.setVisible(true);
            }
        });

        panel.add(playBtn);
        setContentPane(panel);
        setVisible(true);
        
        Timer animationTimer = new Timer(30, e -> panel.repaint());
        animationTimer.start();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu());
    }
}
