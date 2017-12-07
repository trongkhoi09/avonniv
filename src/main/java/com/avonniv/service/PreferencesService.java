package com.avonniv.service;

import com.avonniv.domain.Preferences;
import com.avonniv.domain.Publisher;
import com.avonniv.domain.User;
import com.avonniv.repository.PreferencesRepository;
import com.avonniv.service.dto.PreferencesDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PreferencesService {

    private final Logger log = LoggerFactory.getLogger(PreferencesService.class);

    private final PreferencesRepository preferencesRepository;

    private final UserService userService;
    private final PublisherService publisherService;

    public PreferencesService(PreferencesRepository preferencesRepository,
                              UserService userService,
                              PublisherService publisherService) {
        this.preferencesRepository = preferencesRepository;
        this.userService = userService;
        this.publisherService = publisherService;
    }

    public PreferencesDTO createPreferences(String userLogin, Long publisherId, boolean notification) {
        Preferences newPreferences = new Preferences();
        Optional<User> userOptional = userService.getUserWithAuthoritiesByLogin(userLogin);
        if (!userOptional.isPresent()) {
            return null;
        }
        newPreferences.setUser(userOptional.get());

        Optional<Publisher> publisherOptional = publisherService.getById(publisherId);
        if (!publisherOptional.isPresent()) {
            return null;
        }
        newPreferences.setPublisher(publisherOptional.get());

        newPreferences.setNotification(notification);
        preferencesRepository.save(newPreferences);
        log.debug("Created Information for Preferences: {}", newPreferences);
        return new PreferencesDTO(newPreferences);
    }

    public Optional<Preferences> getByPublisherId(String userLogin, Long publisherId) {
        return preferencesRepository.findFirstByUserLoginAndPublisherId(userLogin, publisherId);
    }

    public List<PreferencesDTO> getAll(String userLogin) {
        return preferencesRepository.findAllByUserLogin(userLogin).stream()
            .map(PreferencesDTO::new)
            .collect(Collectors.toList());
    }

    public PreferencesDTO updatePreferences(String userLogin, Long publisherId, boolean notification) {
        Optional<Preferences> preferencesOptional = getByPublisherId(userLogin, publisherId);
        if (preferencesOptional.isPresent()) {
            PreferencesDTO preferencesDTO = new PreferencesDTO(preferencesOptional.get());
            preferencesDTO.setNotification(notification);
            return updatePreferences(preferencesDTO).orElse(null);
        } else {
            return createPreferences(userLogin, publisherId, notification);
        }
    }

    public Optional<PreferencesDTO> updatePreferences(PreferencesDTO preferencesDTO) {
        return Optional.of(preferencesRepository
            .findOne(preferencesDTO.getId()))
            .map(preferences -> {
                preferences.setNotification(preferencesDTO.isNotification());
                log.debug("Changed Information for Preferences: {}", preferences);
                return preferences;
            })
            .map(PreferencesDTO::new);
    }
}
