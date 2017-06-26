package com.avonniv.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "tbl_call_description")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CallDescription extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    private String title;

    @Lob
    private String description;

    @ManyToOne
    @JoinColumn(name = "grant_id")
    private Grant grant;

    @ManyToOne
    @JoinColumn(name = "file_info_id")
    private FileInfo fileInfo;

    public Grant getGrant() {
        return grant;
    }

    public void setGrant(Grant grant) {
        this.grant = grant;
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

    public FileInfo getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }
}
