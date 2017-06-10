package com.avonniv.service;

import com.avonniv.domain.CallDescription;
import com.avonniv.domain.FileInfo;
import com.avonniv.domain.GrantCall;
import com.avonniv.repository.CallDescriptionRepository;
import com.avonniv.repository.FileInfoRepository;
import com.avonniv.repository.GrantCallRepository;
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

    private final GrantCallRepository grantCallRepository;

    private final FileInfoRepository fileInfoRepository;

    public CallDescriptionService(CallDescriptionRepository callDescriptionRepository,
                                  GrantCallRepository grantCallRepository,
                                  FileInfoRepository fileInfoRepository) {
        this.callDescriptionRepository = callDescriptionRepository;
        this.grantCallRepository = grantCallRepository;
        this.fileInfoRepository = fileInfoRepository;
    }

    public CallDescription createCallDescription(CallDescriptionDTO callDescriptionDTO) {
        CallDescription newCallDescription = new CallDescription();
        newCallDescription.setTitle(callDescriptionDTO.getTitle());
        newCallDescription.setDescription(callDescriptionDTO.getDescription());

        GrantCall grantCall = grantCallRepository.findOne(callDescriptionDTO.getGrantCallDTO().getId());
        newCallDescription.setGrantCall(grantCall);

        FileInfo fileInfo = fileInfoRepository.findOne(callDescriptionDTO.getFileInfoDTO().getId());
        newCallDescription.setFileInfo(fileInfo);

        callDescriptionRepository.save(newCallDescription);
        log.debug("Created Information for Call Description: {}", newCallDescription);
        return newCallDescription;
    }

    public List<CallDescriptionDTO> getAll() {
        return callDescriptionRepository.findAll().stream().map(CallDescriptionDTO::new).collect(Collectors.toList());
    }

    public Optional<CallDescriptionDTO> updateCallDescription(CallDescriptionDTO callDescriptionDTO) {
        return Optional.of(callDescriptionRepository
            .findOne(callDescriptionDTO.getId()))
            .map(callDescription -> {
                callDescription.setTitle(callDescriptionDTO.getTitle());
                callDescription.setDescription(callDescriptionDTO.getDescription());

                GrantCall grantCall = grantCallRepository.findOne(callDescriptionDTO.getGrantCallDTO().getId());
                callDescription.setGrantCall(grantCall);

                FileInfo fileInfo = fileInfoRepository.findOne(callDescriptionDTO.getFileInfoDTO().getId());
                callDescription.setFileInfo(fileInfo);
                log.debug("Changed Information for Call Description: {}", callDescription);
                return callDescription;
            })
            .map(CallDescriptionDTO::new);
    }

    public void deleteCallDescription(CallDescriptionDTO callDescriptionDTO) {
        callDescriptionRepository.delete(callDescriptionDTO.getId());
    }
}
