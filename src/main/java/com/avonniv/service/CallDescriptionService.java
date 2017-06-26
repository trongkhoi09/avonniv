package com.avonniv.service;

import com.avonniv.domain.CallDescription;
import com.avonniv.domain.FileInfo;
import com.avonniv.domain.Grant;
import com.avonniv.repository.CallDescriptionRepository;
import com.avonniv.repository.FileInfoRepository;
import com.avonniv.repository.GrantRepository;
import com.avonniv.service.dto.CallDescriptionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CallDescriptionService {

    private final Logger log = LoggerFactory.getLogger(CallDescriptionService.class);

    private final CallDescriptionRepository callDescriptionRepository;

    private final GrantRepository grantRepository;

    private final FileInfoRepository fileInfoRepository;

    public CallDescriptionService(CallDescriptionRepository callDescriptionRepository,
                                  GrantRepository grantRepository,
                                  FileInfoRepository fileInfoRepository) {
        this.callDescriptionRepository = callDescriptionRepository;
        this.grantRepository = grantRepository;
        this.fileInfoRepository = fileInfoRepository;
    }

    public CallDescription createCallDescription(CallDescriptionDTO callDescriptionDTO) {
        CallDescription newCallDescription = new CallDescription();
        newCallDescription.setTitle(callDescriptionDTO.getTitle());
        newCallDescription.setDescription(callDescriptionDTO.getDescription());

        Grant grant = grantRepository.findOne(callDescriptionDTO.getGrantDTO().getId());
        newCallDescription.setGrant(grant);

        FileInfo fileInfo = fileInfoRepository.findOne(callDescriptionDTO.getFileInfoDTO().getId());
        newCallDescription.setFileInfo(fileInfo);

        callDescriptionRepository.save(newCallDescription);
        log.debug("Created Information for Call Description: {}", newCallDescription);
        return newCallDescription;
    }

    public List<CallDescriptionDTO> getAll() {
        return callDescriptionRepository.findAll().stream().map(CallDescriptionDTO::new).collect(Collectors.toList());
    }

    public Optional<CallDescription> updateCallDescription(CallDescriptionDTO callDescriptionDTO) {
        return Optional.of(callDescriptionRepository
            .findOne(callDescriptionDTO.getId()))
            .map(callDescription -> {
                callDescription.setTitle(callDescriptionDTO.getTitle());
                callDescription.setDescription(callDescriptionDTO.getDescription());

                Grant grant = grantRepository.findOne(callDescriptionDTO.getGrantDTO().getId());
                callDescription.setGrant(grant);

                FileInfo fileInfo = fileInfoRepository.findOne(callDescriptionDTO.getFileInfoDTO().getId());
                callDescription.setFileInfo(fileInfo);
                log.debug("Changed Information for Call Description: {}", callDescription);
                return callDescription;
            });
    }

    public void deleteCallDescription(CallDescriptionDTO callDescriptionDTO) {
        callDescriptionRepository.delete(callDescriptionDTO.getId());
    }
}
