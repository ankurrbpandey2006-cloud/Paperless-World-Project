package com.paperless.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract base for all persistent entities.
 * Demonstrates inheritance + abstraction.
 */
public abstract class BaseEntity implements Auditable {
    protected int id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    protected BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    protected BaseEntity(int id) {
        this();
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @Override public LocalDateTime getCreatedAt() { return createdAt; }
    @Override public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public void touch() { this.updatedAt = LocalDateTime.now(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
