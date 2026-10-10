package com.paperless.ui;

import com.paperless.dao.AuditLogDAO;
import com.paperless.model.Role;
import com.paperless.model.User;
import com.paperless.service.UserService;
import com.paperless.util.PasswordUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class AdminDashboard extends JFrame {
    private final User currentUser;
    private final UserService userService = new UserService();
    private final AuditLogDAO auditDAO = new AuditLogDAO();

    private final DefaultTableModel userModel = new DefaultTableModel(new Object[]{"ID","Name","Email","Role","Active"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable userTable = new JTable(userModel);
    private final JLabel metricsLabel = new JLabel();

    public AdminDashboard(User user) {
        super("Administrator — Paperless World  |  " + user.getName());
        this.currentUser = user;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 640);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIStyle.BG);
        setLayout(new BorderLayout());

        add(header(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIStyle.BODY);
        tabs.addTab("User Management", userManagementPanel());
        tabs.addTab("Access Control", accessControlPanel());
        tabs.addTab("System Maintenance", systemPanel());
        add(tabs, BorderLayout.CENTER);

        loadUsers();
        loadMetrics();
    }

    private JPanel header() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UIStyle.PRIMARY_DARK);
        p.setBorder(new EmptyBorder(12, 18, 12, 18));
        JLabel t = new JLabel("Administrator Dashboard");
        t.setFont(new Font("Segoe UI", Font.BOLD, 16));
        t.setForeground(Color.WHITE);
        JLabel u = new JLabel(currentUser.getEmail() + "  •  " + currentUser.getRole().getDisplayName());
        u.setForeground(new Color(0xBF_D3_FF));
        JButton logout = UIStyle.outlineButton("Logout");
        logout.setBackground(Color.WHITE);
        logout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        p.add(t, BorderLayout.WEST);
        p.add(u, BorderLayout.CENTER);
        p.add(logout, BorderLayout.EAST);
        return p;
    }

    private JPanel userManagementPanel() {
        JPanel p = new JPanel(new BorderLayout(10,10));
        p.setBackground(UIStyle.BG);
        p.setBorder(new EmptyBorder(12,12,12,12));
        UIStyle.styleTable(userTable);
        JScrollPane sp = new JScrollPane(userTable);
        sp.setBorder(BorderFactory.createLineBorder(new Color(0xE5_E7_EB)));
        p.add(sp, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setBackground(UIStyle.BG);
        JButton addBtn = UIStyle.primaryButton("Add User");
        JButton editBtn = UIStyle.outlineButton("Edit Selected");
        JButton delBtn = UIStyle.outlineButton("Delete Selected");
        JButton refreshBtn = UIStyle.outlineButton("Refresh");
        actions.add(addBtn); actions.add(editBtn); actions.add(delBtn); actions.add(refreshBtn);
        p.add(actions, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> showUserDialog(null));
        editBtn.addActionListener(e -> {
            int r = userTable.getSelectedRow();
            if (r < 0) { JOptionPane.showMessageDialog(this, "Select a user"); return; }
            int id = (int) userModel.getValueAt(r, 0);
            try { userService.getById(id).ifPresent(this::showUserDialog); } catch (Exception ex) { showErr(ex); }
        });
        delBtn.addActionListener(e -> {
            int r = userTable.getSelectedRow();
            if (r < 0) { JOptionPane.showMessageDialog(this, "Select a user"); return; }
            int id = (int) userModel.getValueAt(r, 0);
            if (JOptionPane.showConfirmDialog(this, "Delete user " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION) != 0) return;
            try { userService.deleteUser(id); loadUsers(); loadMetrics(); } catch (Exception ex) { showErr(ex); }
        });
        refreshBtn.addActionListener(e -> { loadUsers(); loadMetrics(); });
        return p;
    }

    private JPanel accessControlPanel() {
        JPanel p = new JPanel(new BorderLayout(10,10));
        p.setBackground(UIStyle.BG);
        p.setBorder(new EmptyBorder(12,12,12,12));
        JLabel title = new JLabel("Access permissions by role");
        title.setFont(UIStyle.H2);
        p.add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1,3,12,12));
        cards.setBackground(UIStyle.BG);
        // will be populated in loadMetrics
        cards.setName("cards");
        p.add(cards, BorderLayout.CENTER);

        JTextArea perms = new JTextArea();
        perms.setEditable(false);
        perms.setFont(new Font("Consolas", Font.PLAIN, 12));
        perms.setText("""
                ADMINISTRATOR → User Management, Access Control, System Maintenance
                SIGNER        → Document Signing, Signature Management
                REVIEWER      → Document Review, Compliance Check""");
        perms.setBorder(BorderFactory.createTitledBorder("Permission Matrix"));
        p.add(perms, BorderLayout.SOUTH);
        return p;
    }

    private JPanel systemPanel() {
        JPanel p = new JPanel(new BorderLayout(10,10));
        p.setBackground(UIStyle.BG);
        p.setBorder(new EmptyBorder(12,12,12,12));
        metricsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        metricsLabel.setVerticalAlignment(SwingConstants.TOP);
        JScrollPane sp = new JScrollPane(metricsLabel);
        sp.setBorder(BorderFactory.createTitledBorder("System Status & Performance Metrics"));
        p.add(sp, BorderLayout.CENTER);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btns.setBackground(UIStyle.BG);
        JButton refresh = UIStyle.primaryButton("Refresh Metrics");
        JButton auditBtn = UIStyle.outlineButton("View Audit Logs");
        btns.add(refresh); btns.add(auditBtn);
        p.add(btns, BorderLayout.SOUTH);
        refresh.addActionListener(e -> loadMetrics());
        auditBtn.addActionListener(e -> showAuditLogs());
        return p;
    }

    private void loadUsers() {
        try {
            List<User> users = userService.getSortedByName();
            userModel.setRowCount(0);
            for (User u : users) userModel.addRow(new Object[]{u.getId(), u.getName(), u.getEmail(), u.getRole().name(), u.isActive()});
        } catch (Exception ex) { showErr(ex); }
    }

    private void loadMetrics() {
        try {
            Map<Role, Long> counts = userService.countByRole();
            long total = counts.values().stream().mapToLong(Long::longValue).sum();
            long auditCount = auditDAO.findAll().size();
            Runtime rt = Runtime.getRuntime();
            long usedMb = (rt.totalMemory() - rt.freeMemory()) / (1024*1024);
            long maxMb = rt.maxMemory() / (1024*1024);
            metricsLabel.setText(String.format("""
                    <html>
                    <b>Users:</b> %d &nbsp; | &nbsp; Admin: %d &nbsp; Signer: %d &nbsp; Reviewer: %d<br>
                    <b>Audit logs:</b> %d<br>
                    <b>JVM Memory:</b> %d MB used / %d MB max<br>
                    <b>Available processors:</b> %d<br>
                    <b>DB:</b> H2 file ./data/paperless (JDBC)<br>
                    <b>Status:</b> <span style='color:#0E9F6E'>● Operational</span>
                    </html>""",
                    total,
                    counts.getOrDefault(Role.ADMINISTRATOR, 0L),
                    counts.getOrDefault(Role.SIGNER, 0L),
                    counts.getOrDefault(Role.REVIEWER, 0L),
                    auditCount, usedMb, maxMb, rt.availableProcessors()));
        } catch (Exception ex) { showErr(ex); }
    }

    private void showAuditLogs() {
        try {
            var logs = auditDAO.findAll();
            String[] cols = {"ID","Action","User","Doc","Details","Time"};
            DefaultTableModel m = new DefaultTableModel(cols,0);
            for (var a : logs) m.addRow(new Object[]{a.getId(), a.getAction(), a.getUserId(), a.getDocumentId(), a.getDetails(), a.getTimestamp()});
            JTable t = new JTable(m); UIStyle.styleTable(t);
            JScrollPane sp = new JScrollPane(t); sp.setPreferredSize(new Dimension(800,400));
            JOptionPane.showMessageDialog(this, sp, "Audit Logs", JOptionPane.PLAIN_MESSAGE);
        } catch (Exception ex) { showErr(ex); }
    }

    private void showUserDialog(User existing) {
        boolean isNew = existing == null;
        JTextField nameF = new JTextField(isNew ? "" : existing.getName());
        JTextField emailF = new JTextField(isNew ? "" : existing.getEmail());
        JPasswordField passF = new JPasswordField();
        JComboBox<Role> roleBox = new JComboBox<>(Role.values());
        if (!isNew) roleBox.setSelectedItem(existing.getRole());
        JCheckBox activeBox = new JCheckBox("Active", isNew || existing.isActive());

        JPanel form = new JPanel(new GridLayout(0,2,8,8));
        form.add(new JLabel("Name")); form.add(nameF);
        form.add(new JLabel("Email")); form.add(emailF);
        form.add(new JLabel(isNew ? "Password" : "New Password (blank=keep)")); form.add(passF);
        form.add(new JLabel("Role")); form.add(roleBox);
        form.add(new JLabel("Status")); form.add(activeBox);

        int res = JOptionPane.showConfirmDialog(this, form, isNew ? "Add User" : "Edit User", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;
        try {
            String name = nameF.getText().trim();
            String email = emailF.getText().trim();
            String pass = new String(passF.getPassword());
            Role role = (Role) roleBox.getSelectedItem();
            if (isNew) {
                if (pass.isBlank()) throw new IllegalArgumentException("Password required");
                com.paperless.service.AuthService auth = new com.paperless.service.AuthService();
                auth.register(name, email, pass, role);
            } else {
                existing.setName(name); existing.setEmail(email); existing.setRole(role); existing.setActive(activeBox.isSelected());
                if (!pass.isBlank()) existing.setPasswordHash(PasswordUtil.hash(pass));
                userService.updateUser(existing);
            }
            loadUsers(); loadMetrics();
        } catch (Exception ex) { showErr(ex); }
    }

    private void showErr(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
