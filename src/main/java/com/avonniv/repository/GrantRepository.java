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

    Page<Grant> findAllByGrantProgram_Publisher_NameInAndOpenDateBeforeAndCloseDateAfterOrGrantProgram_Publisher_NameInAndOpenDateIsNullAndStatus(Pageable pageable, List<String> listPublisherId, Instant dateBefore, Instant dateAfter, List<String> listPublisherId2, int status);

    Page<Grant> findAllByGrantProgram_Publisher_NameInAndCloseDateAfterOrGrantProgram_Publisher_NameInAndStatusIn(Pageable pageable, List<String> listPublisherId, Instant dateAfter, List<String> listPublisherId2, List<Integer> status);

    Page<Grant> findAllByGrantProgram_Publisher_NameInAndOpenDateAfterAndCloseDateAfterOrGrantProgram_Publisher_NameInAndOpenDateIsNullAndStatus(Pageable pageable, List<String> listPublisherId, Instant dateAfter, Instant dateAfter2, List<String> listPublisherId2, int status);

    Page<Grant> findAllByGrantProgram_Publisher_NameInAndCloseDateAfterAndGrantProgramTypeIn(Pageable pageable, List<String> listPublisherId, Instant dateTime, List<Integer> listType);

    Page<Grant> findAllByGrantProgramNameLikeAndCloseDateAfter(Pageable pageable, String search, Instant dateTime);

    Integer countAllByOpenDateBeforeAndCloseDateAfter(Instant dateBefore, Instant dateAfter);

    Integer countAllByStatusAndOpenDateIsNull(Integer status);
}
