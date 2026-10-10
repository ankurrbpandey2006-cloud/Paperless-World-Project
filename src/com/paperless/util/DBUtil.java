package com.paperless.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * JDBC utility + schema initialization.
 * Uses H2 file DB at ./data/paperless (auto-created) + in-memory fallback.
 * Demonstrates JDBC connectivity requirement for Review 1.
 */
public final class DBUtil {
    private static final String URL = "jdbc:h2:file:./data/paperless;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASS = "";

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("H2 driver not found. Ensure lib/h2-2.2.224.jar is on classpath.");
        }
    }

    private DBUtil() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /** Creates tables if not exist. Idempotent. */
    public static void initDatabase() throws SQLException {
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(150) NOT NULL UNIQUE,
                    password_hash VARCHAR(256) NOT NULL,
                    role VARCHAR(20) NOT NULL,
                    active BOOLEAN DEFAULT TRUE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
            s.execute("""
                CREATE TABLE IF NOT EXISTS documents (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    content CLOB,
                    file_path VARCHAR(500),
                    status VARCHAR(30) DEFAULT 'DRAFT',
                    owner_id INT,
                    compliance_status VARCHAR(20) DEFAULT 'PENDING',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (owner_id) REFERENCES users(id)
                )
            """);
            s.execute("""
                CREATE TABLE IF NOT EXISTS signature_templates (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    signature_data CLOB,
                    user_id INT NOT NULL,
                    is_default BOOLEAN DEFAULT FALSE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);
            s.execute("""
                CREATE TABLE IF NOT EXISTS signatures (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    document_id INT NOT NULL,
                    signer_id INT NOT NULL,
                    template_id INT,
                    signature_hash VARCHAR(500),
                    signed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    ip_address VARCHAR(45),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (document_id) REFERENCES documents(id),
                    FOREIGN KEY (signer_id) REFERENCES users(id)
                )
            """);
            s.execute("""
                CREATE TABLE IF NOT EXISTS audit_logs (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    action VARCHAR(100) NOT NULL,
                    user_id INT,
                    document_id INT,
                    details CLOB,
                    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
            s.execute("""
                CREATE TABLE IF NOT EXISTS reviews (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    document_id INT NOT NULL,
                    reviewer_id INT NOT NULL,
                    comments CLOB,
                    compliance_status VARCHAR(20),
                    approved BOOLEAN,
                    reviewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (document_id) REFERENCES documents(id),
                    FOREIGN KEY (reviewer_id) REFERENCES users(id)
                )
            """);
            // Seed default users if empty
            var rs = s.executeQuery("SELECT COUNT(*) FROM users");
            rs.next();
            if (rs.getInt(1) == 0) {
                String adminHash = PasswordUtil.hash("admin123");
                String signerHash = PasswordUtil.hash("signer123");
                String reviewerHash = PasswordUtil.hash("reviewer123");
                s.executeUpdate("INSERT INTO users (name,email,password_hash,role) VALUES ('Admin','admin@paperless.com','" + adminHash + "','ADMINISTRATOR')");
                s.executeUpdate("INSERT INTO users (name,email,password_hash,role) VALUES ('Alice Signer','alice@paperless.com','" + signerHash + "','SIGNER')");
                s.executeUpdate("INSERT INTO users (name,email,password_hash,role) VALUES ('Bob Reviewer','bob@paperless.com','" + reviewerHash + "','REVIEWER')");
            }
        }
    }
}
