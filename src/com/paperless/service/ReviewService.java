package com.paperless.service;

import com.paperless.dao.AuditLogDAO;
import com.paperless.dao.DocumentDAO;
import com.paperless.dao.ReviewDAO;
import com.paperless.exception.ValidationException;
import com.paperless.model.*;
import com.paperless.util.AsyncExecutor;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Reviewer → Document Review + Compliance Check */
public class ReviewService {
    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final DocumentDAO documentDAO = new DocumentDAO();
    private final AuditLogDAO auditDAO = new AuditLogDAO();

    public Review submitReview(int documentId, int reviewerId, String comments, ComplianceStatus compliance, boolean approved)
            throws ValidationException, SQLException {
        if (documentDAO.findById(documentId).isEmpty()) throw new ValidationException("Document not found");
        Review r = new Review(documentId, reviewerId, comments, compliance, approved);
        Review saved = reviewDAO.save(r);

        // update document
        var doc = documentDAO.findById(documentId).get();
        doc.setComplianceStatus(compliance);
        doc.setStatus(approved ? DocumentStatus.REVIEWED : DocumentStatus.REJECTED);
        documentDAO.update(doc);

        AsyncExecutor.get().submit(() -> {
            try { auditDAO.save(new AuditLog("REVIEW", reviewerId, documentId, (approved ? "APPROVED" : "REJECTED") + " - " + compliance)); }
            catch (SQLException e) { e.printStackTrace(); }
        });
        return saved;
    }

    public List<Review> getAll() throws SQLException { return reviewDAO.findAll(); }

    public Map<ComplianceStatus, Long> complianceCounts() throws SQLException {
        return reviewDAO.findAll().stream()
                .filter(r -> r.getComplianceStatus() != null)
                .collect(Collectors.groupingBy(Review::getComplianceStatus, Collectors.counting()));
    }
}
