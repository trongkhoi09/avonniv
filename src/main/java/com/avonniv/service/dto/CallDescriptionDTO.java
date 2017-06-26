package com.avonniv.service.dto;

import com.avonniv.domain.CallDescription;

import java.time.Instant;

public class CallDescriptionDTO extends AbstractEntityDTO {

    private String title;

    private String description;

    private GrantDTO grantDTO;

    private FileInfoDTO fileInfoDTO;

    public CallDescriptionDTO() {
    }

    public CallDescriptionDTO(CallDescription callDescription) {
        this(callDescription.getId(), callDescription.getCreatedDate(), callDescription.getLastModifiedDate(), callDescription.getStatus(),
            callDescription.getTitle(), callDescription.getDescription(), new GrantDTO(callDescription.getGrant()), new FileInfoDTO(callDescription.getFileInfo()));
    }

    public CallDescriptionDTO(Long id, Instant createdDate, Instant lastModifiedDate, int status,
                              String title, String description, GrantDTO grantDTO, FileInfoDTO fileInfoDTO) {
        this.id = id;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.status = status;
        this.title = title;
        this.description = description;
        this.grantDTO = grantDTO;
        this.fileInfoDTO = fileInfoDTO;
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

    public GrantDTO getGrantDTO() {
        return grantDTO;
    }

    public void setGrantDTO(GrantDTO grantDTO) {
        this.grantDTO = grantDTO;
    }

    public FileInfoDTO getFileInfoDTO() {
        return fileInfoDTO;
    }

    public void setFileInfoDTO(FileInfoDTO fileInfoDTO) {
        this.fileInfoDTO = fileInfoDTO;
    }
}
