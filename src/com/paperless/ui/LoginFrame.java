package com.paperless.ui;

import com.paperless.model.User;
import com.paperless.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final AuthService authService = new AuthService();

    public LoginFrame() {
        super("Paperless World — E-Signature Platform  |  Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(520, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIStyle.BG);

        JPanel center = new JPanel();
        center.setBackground(UIStyle.BG);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(30, 40, 20, 40));

        JLabel logo = new JLabel("Paperless World", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logo.setForeground(UIStyle.PRIMARY_DARK);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Secure & efficient e-signature platform", SwingConstants.CENTER);
        sub.setFont(UIStyle.SUBTITLE);
        sub.setForeground(UIStyle.MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel card = UIStyle.card();
        card.setLayout(new GridBagLayout());
        card.setMaximumSize(new Dimension(420, 320));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lTitle = new JLabel("Sign in");
        lTitle.setFont(UIStyle.H2);
        card.add(lTitle, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1;
        card.add(new JLabel("Email"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField("admin@paperless.com");
        card.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        card.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        JPasswordField passField = new JPasswordField("admin123");
        card.add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JLabel hint = new JLabel("<html><small>Demo accounts:<br>admin@paperless.com / admin123 &nbsp;|&nbsp; alice@paperless.com / signer123 &nbsp;|&nbsp; bob@paperless.com / reviewer123</small></html>");
        hint.setForeground(UIStyle.MUTED);
        card.add(hint, gbc);

        gbc.gridy = 4;
        JButton loginBtn = UIStyle.primaryButton("Login");
        card.add(loginBtn, gbc);

        JLabel status = new JLabel(" ", SwingConstants.CENTER);
        status.setForeground(UIStyle.DANGER);
        status.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn.addActionListener(e -> {
            try {
                User u = authService.login(emailField.getText().trim(), new String(passField.getPassword()));
                dispose();
                switch (u.getRole()) {
                    case ADMINISTRATOR -> new AdminDashboard(u).setVisible(true);
                    case SIGNER -> new SignerDashboard(u).setVisible(true);
                    case REVIEWER -> new ReviewerDashboard(u).setVisible(true);
                }
            } catch (Exception ex) {
                status.setText(ex.getMessage());
            }
        });

        // Enter key triggers login
        getRootPane().setDefaultButton(loginBtn);

        center.add(logo);
        center.add(Box.createVerticalStrut(4));
        center.add(sub);
        center.add(Box.createVerticalStrut(18));
        center.add(card);
        center.add(Box.createVerticalStrut(10));
        center.add(status);

        add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel();
        footer.setBackground(UIStyle.BG);
        footer.add(new JLabel("© 2024 GUVI Geek Network — Paperless World"));
        footer.setBorder(new EmptyBorder(6, 0, 10, 0));
        add(footer, BorderLayout.SOUTH);
    }
}
