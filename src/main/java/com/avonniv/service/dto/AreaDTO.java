package com.avonniv.service.dto;

import com.avonniv.domain.Area;

import java.time.Instant;

public class AreaDTO extends AbstractEntityDTO {
    private String name;

    public AreaDTO() {
    }

    public AreaDTO(Area area) {
        this(area.getId(), area.getCreatedDate(), area.getLastModifiedDate(), area.getStatus(), area.getName());
    }

    public AreaDTO(Long id, Instant createdDate, Instant lastModifiedDate, int status, String name) {
        this.id = id;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.status = status;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
