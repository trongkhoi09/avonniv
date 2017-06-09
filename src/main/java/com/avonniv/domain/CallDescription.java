package com.avonniv.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name = "tbl_call_description")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CallDescription extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    private String title;

    private String description;

    @ManyToOne
    @JoinColumn(name = "grant_call_id")
    private GrantCall grantCall;

    @ManyToOne
    @JoinColumn(name = "file_info_id")
    private FileInfo fileInfo;

    public GrantCall getGrantCall() {
        return grantCall;
    }

    public void setGrantCall(GrantCall grantCall) {
        this.grantCall = grantCall;
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
