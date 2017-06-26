package com.avonniv.service;

import com.avonniv.domain.GrantProgram;
import com.avonniv.domain.Grant;
import com.avonniv.repository.GrantRepository;
import com.avonniv.repository.GrantProgramRepository;
import com.avonniv.service.dto.GrantDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        GrantProgram grantProgram = grantProgramRepository.findOne(grantDTO.getGrantProgramDTO().getId());
        newGrant.setGrantProgram(grantProgram);

        grantRepository.save(newGrant);
        log.debug("Created Information for GrantProgram Call: {}", newGrant);
        return newGrant;
    }

    public List<GrantDTO> getAll() {
        return grantRepository.findAll().stream().map(GrantDTO::new).collect(Collectors.toList());
    }

    public Optional<Grant> updateGrantCall(GrantDTO grantDTO) {
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
                log.debug("Changed Information for GrantProgram Call: {}", grantCall);
                return grantCall;
            });
    }

    public void deleteGrantCall(GrantDTO grantDTO) {
        grantRepository.delete(grantDTO.getId());
    }
}
