package com.avonniv.service.dto;

import com.avonniv.domain.Preferences;

import java.time.Instant;

public class PreferencesDTO extends AbstractEntityDTO {
    private PublisherDTO publisherDTO;

    private UserDTO userDTO;

    private boolean notification = false;

    public PreferencesDTO() {
    }

    public PreferencesDTO(Preferences preferences) {
        this(preferences.getId(), preferences.getCreatedDate(),
            preferences.getLastModifiedDate(), preferences.getStatus(),
            new PublisherDTO(preferences.getPublisher()), new UserDTO(preferences.getUser()),
            preferences.isNotification());
    }

    public PreferencesDTO(PublisherDTO publisherDTO, UserDTO userDTO,
                          boolean notification) {
        this.publisherDTO = publisherDTO;
        this.userDTO = userDTO;
        this.notification = notification;
    }

    public PreferencesDTO(Long id, Instant createdDate,
                          Instant lastModifiedDate, int status,
                          PublisherDTO publisherDTO, UserDTO userDTO,
                          boolean notification) {
        this.id = id;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.status = status;
        this.publisherDTO = publisherDTO;
        this.userDTO = userDTO;
        this.notification = notification;
    }


    public PublisherDTO getPublisherDTO() {
        return publisherDTO;
    }

    public void setPublisherDTO(PublisherDTO publisherDTO) {
        this.publisherDTO = publisherDTO;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }
}
