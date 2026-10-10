package com.paperless.model;

public class SignatureTemplate extends BaseEntity {
    private String name;
    private String signatureData; // Base64 / styled text
    private int userId;
    private boolean isDefault;

    public SignatureTemplate() { super(); }

    public SignatureTemplate(String name, String signatureData, int userId) {
        super();
        this.name = name;
        this.signatureData = signatureData;
        this.userId = userId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSignatureData() { return signatureData; }
    public void setSignatureData(String signatureData) { this.signatureData = signatureData; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }

    @Override
    public String getDisplaySummary() {
        return String.format("SignatureTemplate[%d] %s (user %d)", id, name, userId);
    }

    @Override public String toString() { return getDisplaySummary(); }
}
