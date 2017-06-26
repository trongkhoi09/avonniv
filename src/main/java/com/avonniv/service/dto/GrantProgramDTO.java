package com.avonniv.service.dto;

import com.avonniv.domain.GrantProgram;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class GrantProgramDTO extends AbstractEntityDTO {

    private String name;

    private String description;

    private int type;

    private PublisherDTO publisherDTO;

    private Set<AreaDTO> areaDTOs = new HashSet<>();

    private String externalId;

    private String externalUrl;

    private Instant releaseDate;

    public GrantProgramDTO() {
    }

    public GrantProgramDTO(GrantProgram grantProgram) {
        this(grantProgram.getId(), grantProgram.getCreatedDate(), grantProgram.getLastModifiedDate(), grantProgram.getStatus(),
            grantProgram.getName(), grantProgram.getDescription(), grantProgram.getType(), new PublisherDTO(grantProgram.getPublisher()),
            grantProgram.getAreas().stream().map(AreaDTO::new).collect(Collectors.toSet()), grantProgram.getExternalId(), grantProgram.getExternalUrl(),
            grantProgram.getReleaseDate());
    }


    public GrantProgramDTO(Long id, Instant createdDate, Instant lastModifiedDate, int status,
                           String name, String description, int type, PublisherDTO publisherDTO,
                           Set<AreaDTO> areaDTOs, String externalId, String externalUrl,
                           Instant releaseDate) {
        this.id = id;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.status = status;
        this.name = name;
        this.description = description;
        this.type = type;
        this.publisherDTO = publisherDTO;
        this.areaDTOs = areaDTOs;
        this.externalId = externalId;
        this.externalUrl = externalUrl;
        this.releaseDate = releaseDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public PublisherDTO getPublisherDTO() {
        return publisherDTO;
    }

    public void setPublisherDTO(PublisherDTO publisherDTO) {
        this.publisherDTO = publisherDTO;
    }

    public Set<AreaDTO> getAreaDTOs() {
        return areaDTOs;
    }

    public void setAreaDTOs(Set<AreaDTO> areaDTOs) {
        this.areaDTOs = areaDTOs;
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

    public Instant getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Instant releaseDate) {
        this.releaseDate = releaseDate;
    }

    public enum TYPE {
        PUBLIC(1), PRIVATE(2);
        final int value;

        TYPE(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
