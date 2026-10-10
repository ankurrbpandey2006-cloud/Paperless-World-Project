package com.paperless.dao;

import com.paperless.model.ComplianceStatus;
import com.paperless.model.Review;
import com.paperless.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReviewDAO implements GenericDAO<Review, Integer> {

    @Override
    public Review save(Review r) throws SQLException {
        String sql = "INSERT INTO reviews (document_id,reviewer_id,comments,compliance_status,approved) VALUES (?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getDocumentId());
            ps.setInt(2, r.getReviewerId());
            ps.setString(3, r.getComments());
            ps.setString(4, r.getComplianceStatus() != null ? r.getComplianceStatus().name() : null);
            ps.setBoolean(5, r.isApproved());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) r.setId(rs.getInt(1)); }
        }
        return r;
    }

    @Override public Optional<Review> findById(Integer id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM reviews WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        }
        return Optional.empty();
    }

    @Override public List<Review> findAll() throws SQLException {
        List<Review> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery("SELECT * FROM reviews ORDER BY reviewed_at DESC")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    @Override public boolean update(Review r) throws SQLException {
        String sql = "UPDATE reviews SET comments=?,compliance_status=?,approved=?,updated_at=CURRENT_TIMESTAMP WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.getComments());
            ps.setString(2, r.getComplianceStatus() != null ? r.getComplianceStatus().name() : null);
            ps.setBoolean(3, r.isApproved());
            ps.setInt(4, r.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override public boolean delete(Integer id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM reviews WHERE id=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        }
    }

    private Review map(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setId(rs.getInt("id"));
        r.setDocumentId(rs.getInt("document_id"));
        r.setReviewerId(rs.getInt("reviewer_id"));
        r.setComments(rs.getString("comments"));
        String cs = rs.getString("compliance_status");
        if (cs != null) r.setComplianceStatus(ComplianceStatus.valueOf(cs));
        r.setApproved(rs.getBoolean("approved"));
        Timestamp t = rs.getTimestamp("reviewed_at"); if (t != null) r.setReviewedAt(t.toLocalDateTime());
        return r;
    }
}
