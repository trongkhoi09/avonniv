package com.avonniv.service;

import com.avonniv.domain.Grant;
import com.avonniv.domain.GrantFilter;
import com.avonniv.domain.GrantProgram;
import com.avonniv.repository.GrantProgramRepository;
import com.avonniv.repository.GrantRepository;
import com.avonniv.service.dto.GrantDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class GrantService {

    private final Logger log = LoggerFactory.getLogger(GrantService.class);

    private final GrantRepository grantRepository;

    private final GrantProgramRepository grantProgramRepository;

    public GrantService(GrantRepository grantRepository,
                        GrantProgramRepository grantProgramRepository) {
        this.grantRepository = grantRepository;
        this.grantProgramRepository = grantProgramRepository;
    }

    public Grant createGrantCall(GrantDTO grantDTO) {
        Grant newGrant = new Grant();
        newGrant.setTitle(grantDTO.getTitle());
        newGrant.setExcerpt(grantDTO.getExcerpt());
        newGrant.setDescription(grantDTO.getDescription());
        newGrant.setOpenDate(grantDTO.getOpenDate());
        newGrant.setCloseDate(grantDTO.getCloseDate());
        newGrant.setAnnouncedDate(grantDTO.getAnnouncedDate());
        newGrant.setProjectStartDate(grantDTO.getProjectStartDate());
        newGrant.setExternalId(grantDTO.getExternalId());
        newGrant.setExternalUrl(grantDTO.getExternalUrl());
        newGrant.setFinanceDescription(grantDTO.getFinanceDescription());
        GrantProgram grantProgram = grantProgramRepository.findOne(grantDTO.getGrantProgramDTO().getId());
        newGrant.setGrantProgram(grantProgram);
        newGrant.setStatus(grantDTO.getStatus());

        grantRepository.save(newGrant);
        log.debug("Created Information for Grant: {}", newGrant);
        return newGrant;
    }

    public List<GrantDTO> getAll() {
        return grantRepository.findAll().stream().map(GrantDTO::new).collect(Collectors.toList());
    }

    public Optional<Grant> getByExternalId(String externalId) {
        return grantRepository.findOneByExternalId(externalId);
    }

    public Optional<Grant> getById(Long id) {
        return Optional.of(grantRepository.findOne(id));
    }

    public Optional<GrantDTO> update(GrantDTO grantDTO) {
        return Optional.of(grantRepository
            .findOne(grantDTO.getId()))
            .map(grantCall -> {
                grantCall.setTitle(grantDTO.getTitle());
                grantCall.setExcerpt(grantDTO.getExcerpt());
                grantCall.setDescription(grantDTO.getDescription());
                grantCall.setOpenDate(grantDTO.getOpenDate());
                grantCall.setCloseDate(grantDTO.getCloseDate());
                grantCall.setAnnouncedDate(grantDTO.getAnnouncedDate());
                grantCall.setProjectStartDate(grantDTO.getProjectStartDate());
                grantCall.setFinanceDescription(grantDTO.getFinanceDescription());
                grantCall.setExternalUrl(grantDTO.getExternalUrl());
                grantCall.setStatus(grantDTO.getStatus());

                GrantProgram grantProgram = grantProgramRepository.findOne(grantDTO.getGrantProgramDTO().getId());
                grantCall.setGrantProgram(grantProgram);
                log.debug("Changed Information for Grant: {}", grantCall);
                return grantCall;
            })
            .map(GrantDTO::new);
    }

    public void deleteGrantCall(GrantDTO grantDTO) {
        grantRepository.delete(grantDTO.getId());
    }

    @Transactional(readOnly = true)
    public Page<GrantDTO> getAll(GrantFilter grantFilter, Pageable pageable) {
        if (grantFilter.getSearch() != null && !grantFilter.getSearch().trim().isEmpty()) {
            return grantRepository.findAllByGrantProgramNameLikeAndCloseDateAfter(pageable, "%" + grantFilter.getSearch().trim() + "%", Instant.now()).map(GrantDTO::new);
        }
        Instant instant = Instant.now();
        List<String> listPublisher = new ArrayList<>();
        listPublisher.addAll(grantFilter.getPublisherDTOs());
//        List<Integer> listType = new ArrayList<>();
        if (grantFilter.isOpenGrant()) {
            if (grantFilter.isComingGrant()) {
                return grantRepository.findAllByGrantProgram_Publisher_NameInAndCloseDateAfterOrGrantProgram_Publisher_NameInAndStatusIn(
                    pageable, listPublisher, instant, listPublisher, Arrays.asList(GrantDTO.Status.open.getValue(), GrantDTO.Status.coming.getValue())
                ).map(GrantDTO::new);
            } else {
                return grantRepository.findAllByGrantProgram_Publisher_NameInAndOpenDateBeforeAndCloseDateAfterOrGrantProgram_Publisher_NameInAndOpenDateIsNullAndStatus(
                    pageable, listPublisher, instant, instant, listPublisher, GrantDTO.Status.open.getValue()
                ).map(GrantDTO::new);
            }
        } else {
            if (grantFilter.isComingGrant()) {
                return grantRepository.findAllByGrantProgram_Publisher_NameInAndOpenDateAfterAndCloseDateAfterOrGrantProgram_Publisher_NameInAndOpenDateIsNullAndStatus(
                    pageable, listPublisher, instant, instant, listPublisher, GrantDTO.Status.coming.getValue()
                ).map(GrantDTO::new);
            } else {
                return new PageImpl<>(new ArrayList<>(), pageable, 0L);
            }
        }
//        if (grantFilter.isOpenGrant()) {
//            listType.add(GrantProgramDTO.TYPE.PUBLIC.getValue());
//        }
//        return grantRepository.findAllByGrantProgram_Publisher_NameInAndCloseDateAfterAndGrantProgramTypeIn(pageable, listPublisher, Instant.now(), listType).map(GrantDTO::new);
    }

    public int getCount() {
        Instant instant = Instant.now();
        return grantRepository.countAllByOpenDateBeforeAndCloseDateAfter(instant, instant) +
            grantRepository.countAllByStatusAndOpenDateIsNull(GrantDTO.Status.open.getValue());
    }
}
