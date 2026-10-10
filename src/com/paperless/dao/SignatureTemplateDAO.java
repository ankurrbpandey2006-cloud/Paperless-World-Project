package com.paperless.dao;

import com.paperless.model.SignatureTemplate;
import com.paperless.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SignatureTemplateDAO implements GenericDAO<SignatureTemplate, Integer> {

    @Override
    public SignatureTemplate save(SignatureTemplate t) throws SQLException {
        String sql = "INSERT INTO signature_templates (name,signature_data,user_id,is_default) VALUES (?,?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getName());
            ps.setString(2, t.getSignatureData());
            ps.setInt(3, t.getUserId());
            ps.setBoolean(4, t.isDefault());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) t.setId(rs.getInt(1)); }
        }
        return t;
    }

    @Override
    public Optional<SignatureTemplate> findById(Integer id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM signature_templates WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        }
        return Optional.empty();
    }

    @Override
    public List<SignatureTemplate> findAll() throws SQLException {
        List<SignatureTemplate> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery("SELECT * FROM signature_templates ORDER BY id")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<SignatureTemplate> findByUser(int userId) throws SQLException {
        List<SignatureTemplate> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM signature_templates WHERE user_id=?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    @Override
    public boolean update(SignatureTemplate t) throws SQLException {
        String sql = "UPDATE signature_templates SET name=?,signature_data=?,is_default=?,updated_at=CURRENT_TIMESTAMP WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, t.getName());
            ps.setString(2, t.getSignatureData());
            ps.setBoolean(3, t.isDefault());
            ps.setInt(4, t.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM signature_templates WHERE id=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        }
    }

    private SignatureTemplate map(ResultSet rs) throws SQLException {
        SignatureTemplate t = new SignatureTemplate();
        t.setId(rs.getInt("id"));
        t.setName(rs.getString("name"));
        t.setSignatureData(rs.getString("signature_data"));
        t.setUserId(rs.getInt("user_id"));
        t.setDefault(rs.getBoolean("is_default"));
        Timestamp ca = rs.getTimestamp("created_at"); if (ca != null) t.setCreatedAt(ca.toLocalDateTime());
        Timestamp ua = rs.getTimestamp("updated_at"); if (ua != null) t.setUpdatedAt(ua.toLocalDateTime());
        return t;
    }
}
