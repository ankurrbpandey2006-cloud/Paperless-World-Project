package com.paperless.model;

/**
 * User roles for the e-signature platform.
 */
public enum Role {
    ADMINISTRATOR("Administrator"),
    SIGNER("Signer"),
    REVIEWER("Reviewer");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
