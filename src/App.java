import com.paperless.ui.LoginFrame;
import com.paperless.util.AsyncExecutor;
import com.paperless.util.DBUtil;

import javax.swing.*;

/**
 * Entry point -- Paperless World E-Signature Platform.
 * Initializes JDBC (H2) and launches the login window.
 */
public class App {
    public static void main(String[] args) throws Exception {
        // Look & feel
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        // Init DB (creates tables + seeds 3 demo users if empty)
        try {
            DBUtil.initDatabase();
            System.out.println("[Paperless] Database ready — ./data/paperless.mv.db");
        } catch (Exception e) {
            System.err.println("[Paperless] DB init failed: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database init failed:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Shutdown pool on exit
        Runtime.getRuntime().addShutdownHook(new Thread(AsyncExecutor::shutdown));

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
