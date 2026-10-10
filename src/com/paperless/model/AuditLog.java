package com.paperless.model;

import java.time.LocalDateTime;

public class AuditLog extends BaseEntity {
    private String action;
    private int userId;
    private int documentId;
    private String details;
    private LocalDateTime timestamp;

    public AuditLog() {
        super();
        this.timestamp = LocalDateTime.now();
    }

    public AuditLog(String action, int userId, int documentId, String details) {
        super();
        this.action = action;
        this.userId = userId;
        this.documentId = documentId;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getDocumentId() { return documentId; }
    public void setDocumentId(int documentId) { this.documentId = documentId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String getDisplaySummary() {
        return String.format("Audit[%d] %s - doc %d by user %d @ %s", id, action, documentId, userId, timestamp);
    }

    @Override public String toString() { return getDisplaySummary(); }
}
