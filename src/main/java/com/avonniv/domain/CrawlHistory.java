package com.avonniv.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "tbl_crawl_history")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CrawlHistory extends AbstractEntity {
    private static final long serialVersionUID = 1L;
    @NotNull
    @Column(unique = true, nullable = false)
    private String name;

    @NotNull
    private Instant lastDateCrawl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getLastDateCrawl() {
        return lastDateCrawl;
    }

    public void setLastDateCrawl(Instant lastDateCrawl) {
        this.lastDateCrawl = lastDateCrawl;
    }
}
