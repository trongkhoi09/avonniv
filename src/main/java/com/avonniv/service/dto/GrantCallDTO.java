package com.avonniv.service.dto;

import com.avonniv.domain.GrantCall;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class GrantCallDTO extends AbstractEntityDTO {

    private GrantDTO grantDTO;

    private String title;

    private String excerpt;

    private String description;

    private Instant openDate;

    private Instant closeDate;

    private Instant announcedDate;

    private Instant projectStartDate;

    public GrantCallDTO() {
    }

    public GrantCallDTO(GrantCall grantCall) {
        this(grantCall.getId(), grantCall.getCreatedDate(), grantCall.getLastModifiedDate(), grantCall.getStatus(),
            new GrantDTO(grantCall.getGrant()), grantCall.getTitle(), grantCall.getExcerpt(), grantCall.getDescription(),
            grantCall.getOpenDate(), grantCall.getCloseDate(), grantCall.getAnnouncedDate(), grantCall.getProjectStartDate());
    }

    public GrantCallDTO(Long id, Instant createdDate, Instant lastModifiedDate, int status,
                        GrantDTO grantDTO, String title, String excerpt, String description,
                        Instant openDate, Instant closeDate, Instant announcedDate, Instant projectStartDate) {
        this.id = id;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.status = status;
        this.grantDTO = grantDTO;
        this.title = title;
        this.excerpt = excerpt;
        this.description = description;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.announcedDate = announcedDate;
        this.projectStartDate = projectStartDate;
    }

    public GrantDTO getGrantDTO() {
        return grantDTO;
    }

    public void setGrantDTO(GrantDTO grantDTO) {
        this.grantDTO = grantDTO;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Instant openDate) {
        this.openDate = openDate;
    }

    public Instant getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Instant closeDate) {
        this.closeDate = closeDate;
    }

    public Instant getAnnouncedDate() {
        return announcedDate;
    }

    public void setAnnouncedDate(Instant announcedDate) {
        this.announcedDate = announcedDate;
    }

    public Instant getProjectStartDate() {
        return projectStartDate;
    }

    public void setProjectStartDate(Instant projectStartDate) {
        this.projectStartDate = projectStartDate;
    }
}
