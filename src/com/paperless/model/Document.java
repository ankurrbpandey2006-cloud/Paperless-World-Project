package com.paperless.model;

public class Document extends BaseEntity {
    private String title;
    private String content;
    private String filePath;
    private DocumentStatus status = DocumentStatus.DRAFT;
    private int ownerId;
    private ComplianceStatus complianceStatus = ComplianceStatus.PENDING;

    public Document() { super(); }

    public Document(String title, String content, int ownerId) {
        super();
        this.title = title;
        this.content = content;
        this.ownerId = ownerId;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public DocumentStatus getStatus() { return status; }
    public void setStatus(DocumentStatus status) { this.status = status; }
    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
    public ComplianceStatus getComplianceStatus() { return complianceStatus; }
    public void setComplianceStatus(ComplianceStatus complianceStatus) { this.complianceStatus = complianceStatus; }

    @Override
    public String getDisplaySummary() {
        return String.format("Document[%d] %s - %s (%s)", id, title, status, complianceStatus);
    }

    @Override public String toString() { return getDisplaySummary(); }
}
