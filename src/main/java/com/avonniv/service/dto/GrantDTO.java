package com.avonniv.service.dto;

import com.avonniv.domain.Grant;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class GrantDTO extends AbstractEntityDTO {

    private String name;

    private String description;

    private int type;

    private PublisherDTO publisherDTO;

    private Set<AreaDTO> areaDTOs = new HashSet<>();

    public GrantDTO() {
    }

    public GrantDTO(Grant grant) {
        this(grant.getId(), grant.getCreatedDate(), grant.getLastModifiedDate(), grant.getStatus(),
            grant.getName(), grant.getDescription(), grant.getType(), new PublisherDTO(grant.getPublisher()),
            grant.getAreas().stream().map(AreaDTO::new).collect(Collectors.toSet()));
    }


    public GrantDTO(Long id, Instant createdDate, Instant lastModifiedDate, int status,
                    String name, String description, int type, PublisherDTO publisherDTO,
                    Set<AreaDTO> areaDTOs) {
        this.id = id;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.status = status;
        this.name = name;
        this.description = description;
        this.type = type;
        this.publisherDTO = publisherDTO;
        this.areaDTOs = areaDTOs;
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
}
