package com.avonniv.repository;

import com.avonniv.domain.Area;
import com.avonniv.domain.Grant;
import com.avonniv.domain.GrantProgram;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface GrantRepository extends JpaRepository<Grant, Long> {
    List<Grant> findAllByStatusInOrderByCreatedDateDesc(Pageable pageable, List<Integer> listStatus);

    List<Grant> findAllByCloseDateBefore(Instant dateBefore);

    List<Grant> findAllByGrantProgramIdAndStatusAndIdIsNotIn(Long id, int status, List<Long> listIgnore);

    List<Grant> findAllByStatusInAndOpenDateBefore(List<Integer> listStatus, Instant dateBefore);

    List<Grant> findAllByStatusInAndOpenDateAfter(List<Integer> listStatus, Instant dateAfter);

    Optional<Grant> findOneByTitle(String title);

    Optional<Grant> findOneByExternalId(String externalId);

    Page<Grant> findAllByGrantProgram_Publisher_NameInAndStatusIn(Pageable pageable, List<String> listPublisherId, List<Integer> status);

    List<Grant> findAllByGrantProgram_Publisher_NameInAndStatusInAndCreatedDateAfter(List<String> listPublisherId, List<Integer> status, Instant dateAfter);

    List<Grant> findAllByStatusInAndCreatedDateAfterOrderByCreatedDateDesc(Pageable pageable, List<Integer> status, Instant dateBefore);

    Page<Grant> findAllByGrantProgram_Publisher_NameInAndStatusInOrderByGrantProgram_Publisher_NameDesc(Pageable pageable, List<String> listPublisherId, List<Integer> status);

    Page<Grant> findAllByGrantProgram_Publisher_NameInAndStatusInOrderByGrantProgram_Publisher_NameAsc(Pageable pageable, List<String> listPublisherId, List<Integer> status);

    Page<Grant> findAllByTitleLikeAndStatusIn(Pageable pageable, String search, List<Integer> listStatus);

    Page<Grant> findAllByTitleLikeAndStatusInOrDescriptionLikeAndStatusIn(Pageable pageable, String searchTitle,List<Integer> listStatusForTile, String searchDescription, List<Integer> listStatusForDescription);

    Integer countAllByStatusInAndGrantProgram_Publisher_CrawledIsTrue(List<Integer> status);

    Page<Grant> findAllByGrantProgram_Publisher_NameInAndTitleLikeAndStatusInOrGrantProgram_Publisher_NameInAndDescriptionLikeAndStatusInOrderByGrantProgram_Publisher_NameAsc(Pageable pageable, List<String> listPublisherIdWithTitle,String title, List<Integer> statusWithTitle, List<String> listPublisherIdWithDescription,String Description, List<Integer> statusWithDescription);
    Page<Grant> findAllByGrantProgramInAndGrantProgram_Publisher_NameInAndStatusInOrderByGrantProgram_Publisher_NameDesc(Pageable pageable, List<GrantProgram> grantPrograms, List<String> listPublisherId,  List<Integer> statuses);
}
