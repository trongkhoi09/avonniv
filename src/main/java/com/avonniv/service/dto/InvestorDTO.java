package com.avonniv.service.dto;

import com.avonniv.domain.Investor;

import java.time.Instant;

public class InvestorDTO {

    private Long id;

    private String value;

    private String title;

    private String description;

    private String financing;

    private String applyBy;

    private String status;

    private String login;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    public InvestorDTO() {
        // Empty constructor needed for Jackson.
    }

    public InvestorDTO(Investor investor) {
        this(investor.getId(), investor.getValue(), investor.getTitle(), investor.getDescription(),
            investor.getFinancing(), investor.getApplyBy(), investor.getStatus(), investor.getUser() == null ? null : investor.getUser().getLogin(),
            investor.getCreatedDate(), investor.getLastModifiedBy(), investor.getLastModifiedDate());
    }

    public InvestorDTO(Long id, String value, String title, String description,
                       String financing, String applyBy, int status, String login,
                       Instant createdDate, String lastModifiedBy, Instant lastModifiedDate) {
        this(id, value, title, description,
            financing, applyBy, Investor.Status.getStatusByValue(status).toString(), login,
            createdDate, lastModifiedBy, lastModifiedDate);
    }

    public InvestorDTO(Long id, String value, String title, String description,
                       String financing, String applyBy, String status, String login,
                       Instant createdDate, String lastModifiedBy, Instant lastModifiedDate) {
        this.id = id;
        this.value = value;
        this.title = title;
        this.description = description;
        this.financing = financing;
        this.applyBy = applyBy;
        this.status = status;
        this.login = login;
        this.createdDate = createdDate;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFinancing() {
        return financing;
    }

    public void setFinancing(String financing) {
        this.financing = financing;
    }

    public String getApplyBy() {
        return applyBy;
    }

    public void setApplyBy(String applyBy) {
        this.applyBy = applyBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
