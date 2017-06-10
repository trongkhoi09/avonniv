package com.avonniv.service;

import com.avonniv.domain.Grant;
import com.avonniv.domain.GrantCall;
import com.avonniv.repository.GrantCallRepository;
import com.avonniv.repository.GrantRepository;
import com.avonniv.service.dto.GrantCallDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class GrantCallService {

    private final Logger log = LoggerFactory.getLogger(GrantCallService.class);

    private final GrantCallRepository grantCallRepository;

    private final GrantRepository grantRepository;

    public GrantCallService(GrantCallRepository grantCallRepository,
                            GrantRepository grantRepository) {
        this.grantCallRepository = grantCallRepository;
        this.grantRepository = grantRepository;
    }

    public GrantCall createGrantCall(GrantCallDTO grantCallDTO) {
        GrantCall newGrantCall = new GrantCall();
        newGrantCall.setTitle(grantCallDTO.getTitle());
        newGrantCall.setExcerpt(grantCallDTO.getExcerpt());
        newGrantCall.setDescription(grantCallDTO.getDescription());
        newGrantCall.setOpenDate(grantCallDTO.getOpenDate());
        newGrantCall.setCloseDate(grantCallDTO.getCloseDate());
        newGrantCall.setAnnouncedDate(grantCallDTO.getAnnouncedDate());
        newGrantCall.setProjectStartDate(grantCallDTO.getProjectStartDate());

        Grant grant = grantRepository.findOne(grantCallDTO.getGrantDTO().getId());
        newGrantCall.setGrant(grant);

        grantCallRepository.save(newGrantCall);
        log.debug("Created Information for Grant Call: {}", newGrantCall);
        return newGrantCall;
    }

    public List<GrantCallDTO> getAll() {
        return grantCallRepository.findAll().stream().map(GrantCallDTO::new).collect(Collectors.toList());
    }

    public Optional<GrantCallDTO> updateGrantCall(GrantCallDTO grantCallDTO) {
        return Optional.of(grantCallRepository
            .findOne(grantCallDTO.getId()))
            .map(grantCall -> {
                grantCall.setTitle(grantCallDTO.getTitle());
                grantCall.setExcerpt(grantCallDTO.getExcerpt());
                grantCall.setDescription(grantCallDTO.getDescription());
                grantCall.setOpenDate(grantCallDTO.getOpenDate());
                grantCall.setCloseDate(grantCallDTO.getCloseDate());
                grantCall.setAnnouncedDate(grantCallDTO.getAnnouncedDate());
                grantCall.setProjectStartDate(grantCallDTO.getProjectStartDate());

                Grant grant = grantRepository.findOne(grantCallDTO.getGrantDTO().getId());
                grantCall.setGrant(grant);
                log.debug("Changed Information for Grant Call: {}", grantCall);
                return grantCall;
            })
            .map(GrantCallDTO::new);
    }

    public void deleteGrantCall(GrantCallDTO grantCallDTO) {
        grantCallRepository.delete(grantCallDTO.getId());
    }
}
