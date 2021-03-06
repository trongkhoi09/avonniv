package com.avonniv.service;

import com.avonniv.domain.Area;
import com.avonniv.domain.GrantProgram;
import com.avonniv.domain.Publisher;
import com.avonniv.repository.AreaRepository;
import com.avonniv.repository.GrantProgramRepository;
import com.avonniv.repository.PublisherRepository;
import com.avonniv.service.dto.GrantProgramDTO;
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
public class GrantProgramService {

    private final Logger log = LoggerFactory.getLogger(GrantProgramService.class);

    private final GrantProgramRepository grantProgramRepository;

    private final PublisherRepository publisherRepository;

    private final AreaRepository areaRepository;

    public GrantProgramService(GrantProgramRepository grantProgramRepository,
                               PublisherRepository publisherRepository,
                               AreaRepository areaRepository) {
        this.grantProgramRepository = grantProgramRepository;
        this.publisherRepository = publisherRepository;
        this.areaRepository = areaRepository;
    }

    public GrantProgram createGrant(GrantProgramDTO grantProgramDTO) {
        GrantProgram newGrantProgram = new GrantProgram();
        newGrantProgram.setName(grantProgramDTO.getName());
        newGrantProgram.setDescription(grantProgramDTO.getDescription());
        newGrantProgram.setType(grantProgramDTO.getType());
        Publisher publisher = publisherRepository.findOne(grantProgramDTO.getPublisherDTO().getId());
        newGrantProgram.setPublisher(publisher);
        newGrantProgram.setExternalId(grantProgramDTO.getExternalId());
        newGrantProgram.setExternalUrl(grantProgramDTO.getExternalUrl());
        newGrantProgram.setReleaseDate(grantProgramDTO.getReleaseDate());

        if (grantProgramDTO.getAreaDTOs() != null) {
            Set<Area> areas = new HashSet<>();
            grantProgramDTO.getAreaDTOs().forEach(
                areaDTO -> areas.add(areaRepository.findOne(areaDTO.getId()))
            );
            newGrantProgram.setAreas(areas);
        } else {
            newGrantProgram.setAreas(new HashSet<>());
        }

        grantProgramRepository.save(newGrantProgram);
        log.debug("Created Information for GrantProgram: {}", newGrantProgram);
        return newGrantProgram;
    }

    public List<GrantProgramDTO> getAll() {
        return grantProgramRepository.findAll().stream().map(GrantProgramDTO::new).collect(Collectors.toList());
    }

    public Optional<GrantProgram> getByExternalId(String externalId) {
        return grantProgramRepository.findOneByExternalId(externalId);
    }

    public Optional<GrantProgramDTO> update(GrantProgramDTO grantProgramDTO) {
        return Optional.of(grantProgramRepository
            .findOne(grantProgramDTO.getId()))
            .map(grantProgram -> {
                grantProgram.setName(grantProgramDTO.getName());
                grantProgram.setDescription(grantProgramDTO.getDescription());
                grantProgram.setType(grantProgramDTO.getType());
                grantProgram.setReleaseDate(grantProgramDTO.getReleaseDate());
                grantProgram.setExternalUrl(grantProgramDTO.getExternalUrl());
                Publisher publisher = publisherRepository.findOne(grantProgramDTO.getPublisherDTO().getId());
                grantProgram.setPublisher(publisher);

                if (grantProgramDTO.getAreaDTOs() != null) {
                    Set<Area> areas = new HashSet<>();
                    grantProgramDTO.getAreaDTOs().forEach(
                        areaDTO -> areas.add(areaRepository.findOne(areaDTO.getId()))
                    );
                    grantProgram.setAreas(areas);
                }
                log.debug("Changed Information for GrantProgram: {}", grantProgram);
                return grantProgram;
            })
            .map(GrantProgramDTO::new);
    }

    public void deleteGrant(GrantProgramDTO grantProgramDTO) {
        grantProgramRepository.delete(grantProgramDTO.getId());
    }

    public Optional<GrantProgram> getById(Long grantProgramId) {
        return Optional.of(grantProgramRepository.findOne(grantProgramId));
    }
}
