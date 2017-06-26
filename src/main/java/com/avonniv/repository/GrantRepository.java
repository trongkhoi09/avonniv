package com.avonniv.repository;

import com.avonniv.domain.Grant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GrantRepository extends JpaRepository<Grant, Long> {
    Optional<Grant> findOneByTitle(String title);
}
