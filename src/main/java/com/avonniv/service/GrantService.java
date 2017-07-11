package com.avonniv.service;

import com.avonniv.domain.Grant;
import com.avonniv.domain.GrantFilter;
import com.avonniv.domain.GrantProgram;
import com.avonniv.repository.GrantProgramRepository;
import com.avonniv.repository.GrantRepository;
import com.avonniv.service.dto.GrantDTO;
import com.avonniv.service.dto.GrantProgramDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
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
        List<String> listPublisher = new ArrayList<>();
        List<Integer> listType = new ArrayList<>();
        if (grantFilter.isPrivateGrant()) {
            listType.add(GrantProgramDTO.TYPE.PRIVATE.getValue());
        }
        if (grantFilter.isPublicGrant()) {
            listType.add(GrantProgramDTO.TYPE.PUBLIC.getValue());
        }
        listPublisher.addAll(grantFilter.getPublisherDTOs());
//        if (listPublisher.isEmpty()) {
//            return grantRepository.findAllByCloseDateAfterAndGrantProgramTypeIn(pageable, Instant.now(), listType).map(GrantDTO::new);
//        } else {
        return grantRepository.findAllByGrantProgram_Publisher_NameInAndCloseDateAfterAndGrantProgramTypeIn(pageable, listPublisher, Instant.now(), listType).map(GrantDTO::new);
//        }
    }

    public int getCount() {
        Instant instant = Instant.now();
        return grantRepository.countAllByOpenDateBeforeAndCloseDateAfter(instant, instant);
    }
}
