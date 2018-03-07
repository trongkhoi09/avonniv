package com.avonniv.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tbl_publisher")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Publisher extends AbstractEntity{

    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(unique = true, nullable = false)
    private String name;

    @Lob
    private String description;

    @Lob
    private String descriptionEN;

    private String address;

    private String email;

    private String phone;

    private String url;

    @Column(name = "is_crawled")
    private boolean crawled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptionSWE() {
        return description;
    }

    public void setDescriptionSWE(String descriptionSWE) {
        this.description = descriptionSWE;
    }

    public String getDescriptionEN() {
        return descriptionEN;
    }

    public void setDescriptionEN(String descriptionEN) {
        this.descriptionEN = descriptionEN;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean isCrawled() {
        return crawled;
    }

    public void setCrawled(boolean crawled) {
        this.crawled = crawled;
    }
}
