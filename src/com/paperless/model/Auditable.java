package com.paperless.model;

import java.time.LocalDateTime;

/**
 * Interface for entities that support auditing.
 * Demonstrates interface + default method usage.
 */
public interface Auditable {
    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    default String auditInfo() {
        return "Created: " + getCreatedAt();
    }

    /** Polymorphic description for dashboards. */
    String getDisplaySummary();
}
