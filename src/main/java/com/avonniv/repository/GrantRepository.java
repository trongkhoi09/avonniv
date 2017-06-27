package com.avonniv.repository;

import com.avonniv.domain.Grant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GrantRepository extends JpaRepository<Grant, Long> {
    Optional<Grant> findOneByTitle(String title);

    Optional<Grant> findOneByExternalId(String externalId);

    Page<Grant> findAllByGrantProgram_Publisher_IdIn(Pageable pageable, List<Long> listPublisherId);
}
