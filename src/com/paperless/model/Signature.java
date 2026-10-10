package com.paperless.model;

import java.time.LocalDateTime;

public class Signature extends BaseEntity {
    private int documentId;
    private int signerId;
    private int templateId;
    private String signatureHash;
    private LocalDateTime signedAt;
    private String ipAddress;

    public Signature() { super(); }

    public Signature(int documentId, int signerId, int templateId, String signatureHash) {
        super();
        this.documentId = documentId;
        this.signerId = signerId;
        this.templateId = templateId;
        this.signatureHash = signatureHash;
        this.signedAt = LocalDateTime.now();
    }

    public int getDocumentId() { return documentId; }
    public void setDocumentId(int documentId) { this.documentId = documentId; }
    public int getSignerId() { return signerId; }
    public void setSignerId(int signerId) { this.signerId = signerId; }
    public int getTemplateId() { return templateId; }
    public void setTemplateId(int templateId) { this.templateId = templateId; }
    public String getSignatureHash() { return signatureHash; }
    public void setSignatureHash(String signatureHash) { this.signatureHash = signatureHash; }
    public LocalDateTime getSignedAt() { return signedAt; }
    public void setSignedAt(LocalDateTime signedAt) { this.signedAt = signedAt; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    @Override
    public String getDisplaySummary() {
        return String.format("Signature[%d] doc=%d by user=%d at %s", id, documentId, signerId, signedAt);
    }

    @Override public String toString() { return getDisplaySummary(); }
}
