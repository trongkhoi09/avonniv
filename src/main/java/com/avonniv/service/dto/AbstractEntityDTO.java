package com.avonniv.service.dto;

import java.io.Serializable;
import java.time.Instant;

public abstract class AbstractEntityDTO implements Serializable {

    protected Long id;

    protected Instant createdDate = Instant.now();

    protected Instant lastModifiedDate = Instant.now();

    protected int status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
