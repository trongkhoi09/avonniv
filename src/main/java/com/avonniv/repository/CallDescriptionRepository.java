package com.avonniv.repository;

import com.avonniv.domain.CallDescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CallDescriptionRepository extends JpaRepository<CallDescription, Long> {
    Optional<CallDescription> findOneByTitle(String title);
}
