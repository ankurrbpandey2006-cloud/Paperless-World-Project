package com.paperless.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Central style constants for a consistent aesthetic. */
public final class UIStyle {
    private UIStyle() {}

    public static final Color PRIMARY = new Color(0x1A_73_E8);
    public static final Color PRIMARY_DARK = new Color(0x0D_47_A1);
    public static final Color BG = new Color(0xF5_F7_FB);
    public static final Color CARD = Color.WHITE;
    public static final Color TEXT = new Color(0x1F_29_37);
    public static final Color MUTED = new Color(0x6B_72_80);
    public static final Color SUCCESS = new Color(0x0E_9F_6E);
    public static final Color WARN = new Color(0xF5_9E_0B);
    public static final Color DANGER = new Color(0xEF_44_44);

    public static final Font TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font H2 = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font BODY = new Font("Segoe UI", Font.PLAIN, 13);

    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(8, 18, 8, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton outlineButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(Color.WHITE);
        b.setForeground(PRIMARY);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY, 1, true),
                new EmptyBorder(7, 16, 7, 16)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE5_E7_EB), 1, true),
                new EmptyBorder(16, 16, 16, 16)));
        return p;
    }

    public static void styleTable(JTable table) {
        table.setFont(BODY);
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(0xEE_F2_FF));
        table.getTableHeader().setForeground(TEXT);
        table.setGridColor(new Color(0xE5_E7_EB));
        table.setSelectionBackground(new Color(0xDB_EA_FE));
    }
}
