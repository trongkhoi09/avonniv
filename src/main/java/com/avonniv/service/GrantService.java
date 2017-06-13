package com.avonniv.service;

import com.avonniv.domain.Area;
import com.avonniv.domain.Grant;
import com.avonniv.domain.Publisher;
import com.avonniv.repository.AreaRepository;
import com.avonniv.repository.GrantRepository;
import com.avonniv.repository.PublisherRepository;
import com.avonniv.service.dto.GrantDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class GrantService {

    private final Logger log = LoggerFactory.getLogger(GrantService.class);

    private final GrantRepository grantRepository;

    private final PublisherRepository publisherRepository;

    private final AreaRepository areaRepository;

    public GrantService(GrantRepository grantRepository,
                        PublisherRepository publisherRepository,
                        AreaRepository areaRepository) {
        this.grantRepository = grantRepository;
        this.publisherRepository = publisherRepository;
        this.areaRepository = areaRepository;
    }

    public Grant createGrant(GrantDTO grantDTO) {
        Grant newGrant = new Grant();
        newGrant.setName(grantDTO.getName());
        newGrant.setDescription(grantDTO.getDescription());
        newGrant.setType(grantDTO.getType());
        Publisher publisher = publisherRepository.findOne(grantDTO.getPublisherDTO().getId());
        newGrant.setPublisher(publisher);

        if (grantDTO.getAreaDTOs() != null) {
            Set<Area> areas = new HashSet<>();
            grantDTO.getAreaDTOs().forEach(
                areaDTO -> areas.add(areaRepository.findOne(areaDTO.getId()))
            );
            newGrant.setAreas(areas);
        }

        grantRepository.save(newGrant);
        log.debug("Created Information for Grant: {}", newGrant);
        return newGrant;
    }

    public List<GrantDTO> getAll() {
        return grantRepository.findAll().stream().map(GrantDTO::new).collect(Collectors.toList());
    }

    public Optional<Grant> updateGrant(GrantDTO grantDTO) {
        return Optional.of(grantRepository
            .findOne(grantDTO.getId()))
            .map(grant -> {
                grant.setName(grantDTO.getName());
                grant.setDescription(grantDTO.getDescription());
                grant.setType(grantDTO.getType());
                Publisher publisher = publisherRepository.findOne(grantDTO.getPublisherDTO().getId());
                grant.setPublisher(publisher);

                if (grantDTO.getAreaDTOs() != null) {
                    Set<Area> areas = new HashSet<>();
                    grantDTO.getAreaDTOs().forEach(
                        areaDTO -> areas.add(areaRepository.findOne(areaDTO.getId()))
                    );
                    grant.setAreas(areas);
                }
                log.debug("Changed Information for Grant: {}", grant);
                return grant;
            });
    }

    public void deleteGrant(GrantDTO grantDTO) {
        grantRepository.delete(grantDTO.getId());
    }
}
