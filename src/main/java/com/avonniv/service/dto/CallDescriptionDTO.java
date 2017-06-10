package com.avonniv.service.dto;

import com.avonniv.domain.CallDescription;

import java.time.Instant;

public class CallDescriptionDTO extends AbstractEntityDTO {

    private String title;

    private String description;

    private GrantCallDTO grantCallDTO;

    private FileInfoDTO fileInfoDTO;

    public CallDescriptionDTO() {
    }

    public CallDescriptionDTO(CallDescription callDescription) {
        this(callDescription.getId(), callDescription.getCreatedDate(), callDescription.getLastModifiedDate(), callDescription.getStatus(),
            callDescription.getTitle(), callDescription.getDescription(), new GrantCallDTO(callDescription.getGrantCall()), new FileInfoDTO(callDescription.getFileInfo()));
    }

    public CallDescriptionDTO(Long id, Instant createdDate, Instant lastModifiedDate, int status,
                              String title, String description, GrantCallDTO grantCallDTO, FileInfoDTO fileInfoDTO) {
        this.id = id;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.status = status;
        this.title = title;
        this.description = description;
        this.grantCallDTO = grantCallDTO;
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

    public GrantCallDTO getGrantCallDTO() {
        return grantCallDTO;
    }

    public void setGrantCallDTO(GrantCallDTO grantCallDTO) {
        this.grantCallDTO = grantCallDTO;
    }

    public FileInfoDTO getFileInfoDTO() {
        return fileInfoDTO;
    }

    public void setFileInfoDTO(FileInfoDTO fileInfoDTO) {
        this.fileInfoDTO = fileInfoDTO;
    }
}
