package com.paperless.model;

import java.time.LocalDateTime;

public class Review extends BaseEntity {
    private int documentId;
    private int reviewerId;
    private String comments;
    private ComplianceStatus complianceStatus;
    private boolean approved;
    private LocalDateTime reviewedAt;

    public Review() {
        super();
        this.reviewedAt = LocalDateTime.now();
    }

    public Review(int documentId, int reviewerId, String comments, ComplianceStatus complianceStatus, boolean approved) {
        super();
        this.documentId = documentId;
        this.reviewerId = reviewerId;
        this.comments = comments;
        this.complianceStatus = complianceStatus;
        this.approved = approved;
        this.reviewedAt = LocalDateTime.now();
    }

    public int getDocumentId() { return documentId; }
    public void setDocumentId(int documentId) { this.documentId = documentId; }
    public int getReviewerId() { return reviewerId; }
    public void setReviewerId(int reviewerId) { this.reviewerId = reviewerId; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public ComplianceStatus getComplianceStatus() { return complianceStatus; }
    public void setComplianceStatus(ComplianceStatus complianceStatus) { this.complianceStatus = complianceStatus; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    @Override
    public String getDisplaySummary() {
        return String.format("Review[%d] doc=%d %s - %s", id, documentId, approved ? "APPROVED" : "REJECTED", complianceStatus);
    }

    @Override public String toString() { return getDisplaySummary(); }
}
