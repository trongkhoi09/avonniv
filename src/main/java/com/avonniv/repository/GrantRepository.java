package com.avonniv.repository;

import com.avonniv.domain.Grant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface GrantRepository extends JpaRepository<Grant, Long> {
    List<Grant> findAllByStatusInOrderByCreatedDateDesc(Pageable pageable, List<Integer> listStatus);

    List<Grant> findAllByCloseDateBefore(Instant dateBefore);

    List<Grant> findAllByStatusInAndOpenDateBefore(List<Integer> listStatus, Instant dateBefore);

    List<Grant> findAllByStatusInAndOpenDateAfter(List<Integer> listStatus, Instant dateAfter);

    Optional<Grant> findOneByTitle(String title);

    Optional<Grant> findOneByExternalId(String externalId);

    Page<Grant> findAllByGrantProgram_Publisher_NameInAndStatusIn(Pageable pageable, List<String> listPublisherId, List<Integer> status);

    Page<Grant> findAllByTitleLikeAndStatusIn(Pageable pageable, String search, List<Integer> listStatus);

    Integer countAllByStatusInAndGrantProgram_Publisher_CrawledIsTrue(List<Integer> status);
}
