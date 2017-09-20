package com.avonniv.repository;

import com.avonniv.domain.Grant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface GrantRepository extends JpaRepository<Grant, Long> {
    List<Grant> findAllByStatus(int status);

    List<Grant> findAllByCloseDateBefore(Instant dateBefore);

    List<Grant> findAllByStatusInAndOpenDateBefore(List<Integer> listStatus, Instant dateBefore);

    List<Grant> findAllByStatusInAndOpenDateAfter(List<Integer> listStatus, Instant dateAfter);

    Optional<Grant> findOneByTitle(String title);

    Optional<Grant> findOneByExternalId(String externalId);

    Page<Grant> findAllByGrantProgram_Publisher_NameInAndOpenDateBeforeAndCloseDateAfterOrGrantProgram_Publisher_NameInAndStatus(Pageable pageable, List<String> listPublisherId, Instant dateBefore, Instant dateAfter, List<String> listPublisherId2, int status);

    Page<Grant> findAllByGrantProgram_Publisher_NameInAndCloseDateAfterOrGrantProgram_Publisher_NameInAndStatusIn(Pageable pageable, List<String> listPublisherId, Instant dateAfter, List<String> listPublisherId2, List<Integer> status);

    Page<Grant> findAllByGrantProgram_Publisher_NameInAndStatusIn(Pageable pageable, List<String> listPublisherId, List<Integer> status);

    Page<Grant> findAllByGrantProgram_Publisher_NameInAndCloseDateAfterAndGrantProgramTypeIn(Pageable pageable, List<String> listPublisherId, Instant dateTime, List<Integer> listType);

    Page<Grant> findAllByGrantProgramNameLikeAndStatusIn(Pageable pageable, String search, List<Integer> listStatus);

    Integer countAllByOpenDateBeforeAndCloseDateAfter(Instant dateBefore, Instant dateAfter);

    Integer countAllByStatusAndGrantProgram_Publisher_CrawledIsTrue(Integer status);

    Integer countAllByStatusInAndGrantProgram_Publisher_CrawledIsTrue(List<Integer> status);
}
