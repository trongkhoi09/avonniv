package com.avonniv.repository;

import com.avonniv.domain.Preferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PreferencesRepository extends JpaRepository<Preferences, Long> {
    List<Preferences> findAllByUserLogin(String userLogin);

    Optional<Preferences> findFirstByUserLoginAndPublisherId(String userLogin, Long publisherId);
}
