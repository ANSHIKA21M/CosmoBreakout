import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SplashScreen splash = new SplashScreen();
        splash.showSplash();

        SwingUtilities.invokeLater(() -> new MainMenu());
    }
}
