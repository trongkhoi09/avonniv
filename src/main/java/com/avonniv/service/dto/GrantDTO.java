package com.avonniv.service.dto;

import com.avonniv.domain.Grant;

import java.time.Instant;

public class GrantDTO extends AbstractEntityDTO {

    private GrantProgramDTO grantProgramDTO;

    private String title;

    private String excerpt;

    private String description;

    private Instant openDate;

    private Instant closeDate;

    private Instant announcedDate;

    private Instant projectStartDate;

    private String externalId;

    private String externalUrl;

    private String dataFromUrl;

    private String financeDescription;

    public GrantDTO() {
    }

    public GrantDTO(Grant grant) {
        this(grant.getId(), grant.getCreatedDate(), grant.getLastModifiedDate(), grant.getStatus(),
            new GrantProgramDTO(grant.getGrantProgram()), grant.getTitle(), grant.getExcerpt(), grant.getDescription(),
            grant.getOpenDate(), grant.getCloseDate(), grant.getAnnouncedDate(), grant.getProjectStartDate(),
            grant.getExternalId(), grant.getExternalUrl(), grant.getFinanceDescription(), grant.getDataFromUrl());
    }

    public GrantDTO(Long id, Instant createdDate, Instant lastModifiedDate, int status,
                    GrantProgramDTO grantProgramDTO, String title, String excerpt, String description,
                    Instant openDate, Instant closeDate, Instant announcedDate, Instant projectStartDate,
                    String externalId, String externalUrl, String financeDescription, String dataFromUrl) {
        this.id = id;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.status = status;
        this.grantProgramDTO = grantProgramDTO;
        this.title = title;
        this.excerpt = excerpt;
        this.description = description;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.announcedDate = announcedDate;
        this.projectStartDate = projectStartDate;
        this.externalId = externalId;
        this.externalUrl = externalUrl;
        this.financeDescription = financeDescription;
        this.dataFromUrl = dataFromUrl;
    }

    public GrantProgramDTO getGrantProgramDTO() {
        return grantProgramDTO;
    }

    public void setGrantProgramDTO(GrantProgramDTO grantProgramDTO) {
        this.grantProgramDTO = grantProgramDTO;
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

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public String getFinanceDescription() {
        return financeDescription;
    }

    public void setFinanceDescription(String financeDescription) {
        this.financeDescription = financeDescription;
    }

    public String getDataFromUrl() {
        return dataFromUrl;
    }

    public void setDataFromUrl(String dataFromUrl) {
        this.dataFromUrl = dataFromUrl;
    }

    public enum Status {
        open(1), close(-1), coming(2), undefined(0), un_publish(3);

        private final int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
