package com.paperless.dao;

import com.paperless.model.AuditLog;
import com.paperless.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuditLogDAO implements GenericDAO<AuditLog, Integer> {

    @Override
    public AuditLog save(AuditLog a) throws SQLException {
        String sql = "INSERT INTO audit_logs (action,user_id,document_id,details) VALUES (?,?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getAction());
            ps.setInt(2, a.getUserId());
            ps.setInt(3, a.getDocumentId());
            ps.setString(4, a.getDetails());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) a.setId(rs.getInt(1)); }
        }
        return a;
    }

    @Override public Optional<AuditLog> findById(Integer id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM audit_logs WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        }
        return Optional.empty();
    }

    @Override public List<AuditLog> findAll() throws SQLException {
        List<AuditLog> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery("SELECT * FROM audit_logs ORDER BY timestamp DESC")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<AuditLog> findByDocument(int docId) throws SQLException {
        List<AuditLog> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM audit_logs WHERE document_id=? ORDER BY timestamp DESC")) {
            ps.setInt(1, docId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    @Override public boolean update(AuditLog e) throws SQLException { return false; }
    @Override public boolean delete(Integer id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM audit_logs WHERE id=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        }
    }

    private AuditLog map(ResultSet rs) throws SQLException {
        AuditLog a = new AuditLog();
        a.setId(rs.getInt("id"));
        a.setAction(rs.getString("action"));
        a.setUserId(rs.getInt("user_id"));
        a.setDocumentId(rs.getInt("document_id"));
        a.setDetails(rs.getString("details"));
        Timestamp t = rs.getTimestamp("timestamp"); if (t != null) a.setTimestamp(t.toLocalDateTime());
        return a;
    }
}
