package com.avonniv.repository;

import com.avonniv.domain.Grant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface GrantRepository extends JpaRepository<Grant, Long> {
    Optional<Grant> findOneByTitle(String title);

    Optional<Grant> findOneByExternalId(String externalId);

    Page<Grant> findAllByCloseDateAfterAndGrantProgramTypeIn(Pageable pageable, Instant dateTime, List<Integer> listType);

    Page<Grant> findAllByGrantProgram_Publisher_NameInAndCloseDateAfterAndGrantProgramTypeIn(Pageable pageable, List<String> listPublisherId, Instant dateTime, List<Integer> listType);

    Page<Grant> findAllByGrantProgramNameLikeAndCloseDateAfter(Pageable pageable, String search, Instant dateTime);

    Integer countAllByOpenDateBeforeAndCloseDateAfter(Instant dateBefore, Instant dateAfter);
}
