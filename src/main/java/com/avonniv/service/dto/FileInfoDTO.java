package com.avonniv.service.dto;

import com.avonniv.domain.AbstractEntity;
import com.avonniv.domain.FileInfo;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;


public class FileInfoDTO extends AbstractEntityDTO {
    private String name;

    private String mineType;

    private long size;

    private String path;

    private String checkSum;


    public FileInfoDTO() {
    }

    public FileInfoDTO(FileInfo fileInfo) {
        this(fileInfo.getId(), fileInfo.getCreatedDate(), fileInfo.getLastModifiedDate(), fileInfo.getStatus(),
            fileInfo.getName(), fileInfo.getMineType(), fileInfo.getSize(), fileInfo.getPath(), fileInfo.getCheckSum());
    }

    public FileInfoDTO(Long id, Instant createdDate, Instant lastModifiedDate, int status,
                       String name, String mineType, long size, String path, String checkSum) {
        this.id = id;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.status = status;
        this.name = name;
        this.mineType = mineType;
        this.size = size;
        this.path = path;
        this.checkSum = checkSum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMineType() {
        return mineType;
    }

    public void setMineType(String mineType) {
        this.mineType = mineType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }
}
