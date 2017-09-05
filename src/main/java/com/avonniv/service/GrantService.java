package com.avonniv.service;

import com.avonniv.domain.Grant;
import com.avonniv.domain.GrantFilter;
import com.avonniv.domain.GrantProgram;
import com.avonniv.repository.GrantProgramRepository;
import com.avonniv.repository.GrantRepository;
import com.avonniv.service.dto.GrantDTO;
import com.avonniv.service.fetchdata.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
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
        newGrant.setDataFromUrl(grantDTO.getDataFromUrl());

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
                Util.setStatus(grantDTO, Instant.now());
                grantCall.setStatus(grantDTO.getStatus());
                grantCall.setDataFromUrl(grantDTO.getDataFromUrl());

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
            return grantRepository.findAllByGrantProgramNameLikeAndStatusIn(pageable, "%" + grantFilter.getSearch().trim() + "%", Collections.singletonList(GrantDTO.Status.open.getValue())).map(GrantDTO::new);
        }
        Instant instant = Instant.now();
        List<String> listPublisher = new ArrayList<>();
        listPublisher.addAll(grantFilter.getPublisherDTOs());
//        List<Integer> listType = new ArrayList<>();
        if (grantFilter.isOpenGrant()) {
            if (grantFilter.isComingGrant()) {
                return grantRepository.findAllByGrantProgram_Publisher_NameInAndStatusIn(pageable, listPublisher, Arrays.asList(GrantDTO.Status.open.getValue(), GrantDTO.Status.coming.getValue())).map(GrantDTO::new);
            } else {
                return grantRepository.findAllByGrantProgram_Publisher_NameInAndStatusIn(pageable, listPublisher, Collections.singletonList(GrantDTO.Status.open.getValue())).map(GrantDTO::new);
            }
        } else {
            if (grantFilter.isComingGrant()) {
                return grantRepository.findAllByGrantProgram_Publisher_NameInAndStatusIn(pageable, listPublisher, Collections.singletonList(GrantDTO.Status.coming.getValue())).map(GrantDTO::new);
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
        return grantRepository.countAllByStatusAndGrantProgram_Publisher_CrawledIsTrue(GrantDTO.Status.open.getValue());
    }

    @Scheduled(fixedDelay = 3600000)
    public void updateStatusForGrant() {
        Instant instant = Instant.now();
        updateStatusForList(
            grantRepository.findAllByCloseDateBefore(instant),
            GrantDTO.Status.close.getValue()
        );
        updateStatusForList(
            grantRepository.findAllByStatusInAndOpenDateAfter(Collections.singletonList(GrantDTO.Status.undefined.getValue()), instant),
            GrantDTO.Status.coming.getValue()
        );
        updateStatusForList(
            grantRepository.findAllByStatusInAndOpenDateBefore(Arrays.asList(GrantDTO.Status.coming.getValue(), GrantDTO.Status.undefined.getValue()), instant),
            GrantDTO.Status.open.getValue()
        );
//        List<Grant> list = grantRepository.findAllByStatus(GrantDTO.Status.undefined.getValue());
//        System.out.println(list.size());
    }

    private void updateStatusForList(List<Grant> list, int status) {
        List<GrantDTO> grantsOpen = list.stream()
            .map(GrantDTO::new)
            .collect(Collectors.toList());
        for (GrantDTO grant : grantsOpen) {
            grant.setStatus(status);
            update(grant);
        }
    }
}
