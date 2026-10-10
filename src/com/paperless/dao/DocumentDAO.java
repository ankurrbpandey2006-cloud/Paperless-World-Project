package com.paperless.dao;

import com.paperless.model.ComplianceStatus;
import com.paperless.model.Document;
import com.paperless.model.DocumentStatus;
import com.paperless.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DocumentDAO implements GenericDAO<Document, Integer> {

    @Override
    public Document save(Document d) throws SQLException {
        String sql = "INSERT INTO documents (title,content,file_path,status,owner_id,compliance_status) VALUES (?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getTitle());
            ps.setString(2, d.getContent());
            ps.setString(3, d.getFilePath());
            ps.setString(4, d.getStatus().name());
            ps.setInt(5, d.getOwnerId());
            ps.setString(6, d.getComplianceStatus().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) d.setId(rs.getInt(1)); }
        }
        return d;
    }

    @Override
    public Optional<Document> findById(Integer id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM documents WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        }
        return Optional.empty();
    }

    @Override
    public List<Document> findAll() throws SQLException {
        List<Document> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery("SELECT * FROM documents ORDER BY id DESC")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Document> findByOwner(int ownerId) throws SQLException {
        List<Document> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM documents WHERE owner_id=? ORDER BY id DESC")) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public List<Document> findByStatus(DocumentStatus status) throws SQLException {
        List<Document> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM documents WHERE status=?")) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    @Override
    public boolean update(Document d) throws SQLException {
        String sql = "UPDATE documents SET title=?,content=?,file_path=?,status=?,compliance_status=?,updated_at=CURRENT_TIMESTAMP WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, d.getTitle());
            ps.setString(2, d.getContent());
            ps.setString(3, d.getFilePath());
            ps.setString(4, d.getStatus().name());
            ps.setString(5, d.getComplianceStatus().name());
            ps.setInt(6, d.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM documents WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Document map(ResultSet rs) throws SQLException {
        Document d = new Document();
        d.setId(rs.getInt("id"));
        d.setTitle(rs.getString("title"));
        d.setContent(rs.getString("content"));
        d.setFilePath(rs.getString("file_path"));
        d.setStatus(DocumentStatus.valueOf(rs.getString("status")));
        d.setOwnerId(rs.getInt("owner_id"));
        d.setComplianceStatus(ComplianceStatus.valueOf(rs.getString("compliance_status")));
        Timestamp ca = rs.getTimestamp("created_at"); if (ca != null) d.setCreatedAt(ca.toLocalDateTime());
        Timestamp ua = rs.getTimestamp("updated_at"); if (ua != null) d.setUpdatedAt(ua.toLocalDateTime());
        return d;
    }
}
