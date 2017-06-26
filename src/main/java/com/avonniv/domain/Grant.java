package com.avonniv.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.Instant;


@Entity
@Table(name = "tbl_grant")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Grant extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "grant_program_id")
    private GrantProgram grantProgram;

    private String title;

    private String excerpt;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "external_url")
    private String externalUrl;

    @Column(name = "finance_description")
    private String financeDescription;

    @Lob
    private String description;

    @Column(name = "open_date")
    private Instant openDate;

    @Column(name = "close_date")
    private Instant closeDate;

    @Column(name = "announced_date")
    private Instant announcedDate;

    @Column(name = "project_start_date")
    private Instant projectStartDate;

    public GrantProgram getGrantProgram() {
        return grantProgram;
    }

    public void setGrantProgram(GrantProgram grantProgram) {
        this.grantProgram = grantProgram;
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
}
