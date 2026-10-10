package com.paperless.dao;

import com.paperless.model.Signature;
import com.paperless.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SignatureDAO implements GenericDAO<Signature, Integer> {

    @Override
    public Signature save(Signature sig) throws SQLException {
        String sql = "INSERT INTO signatures (document_id,signer_id,template_id,signature_hash,ip_address) VALUES (?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, sig.getDocumentId());
            ps.setInt(2, sig.getSignerId());
            if (sig.getTemplateId() == 0) ps.setNull(3, Types.INTEGER); else ps.setInt(3, sig.getTemplateId());
            ps.setString(4, sig.getSignatureHash());
            ps.setString(5, sig.getIpAddress());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) sig.setId(rs.getInt(1)); }
        }
        return sig;
    }

    @Override
    public Optional<Signature> findById(Integer id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM signatures WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        }
        return Optional.empty();
    }

    @Override
    public List<Signature> findAll() throws SQLException {
        List<Signature> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery("SELECT * FROM signatures ORDER BY signed_at DESC")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Signature> findByDocument(int docId) throws SQLException {
        List<Signature> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM signatures WHERE document_id=?")) {
            ps.setInt(1, docId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    @Override public boolean update(Signature e) throws SQLException { return false; }
    @Override public boolean delete(Integer id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM signatures WHERE id=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        }
    }

    private Signature map(ResultSet rs) throws SQLException {
        Signature s = new Signature();
        s.setId(rs.getInt("id"));
        s.setDocumentId(rs.getInt("document_id"));
        s.setSignerId(rs.getInt("signer_id"));
        s.setTemplateId(rs.getInt("template_id"));
        s.setSignatureHash(rs.getString("signature_hash"));
        Timestamp t = rs.getTimestamp("signed_at"); if (t != null) s.setSignedAt(t.toLocalDateTime());
        s.setIpAddress(rs.getString("ip_address"));
        Timestamp ca = rs.getTimestamp("created_at"); if (ca != null) s.setCreatedAt(ca.toLocalDateTime());
        Timestamp ua = rs.getTimestamp("updated_at"); if (ua != null) s.setUpdatedAt(ua.toLocalDateTime());
        return s;
    }
}
