package com.avonniv.service;

import com.avonniv.domain.Area;
import com.avonniv.repository.AreaRepository;
import com.avonniv.service.dto.AreaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AreaService {

    private final Logger log = LoggerFactory.getLogger(AreaService.class);

    private final AreaRepository areaRepository;

    public AreaService(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    public Area createArea(String name) {
        Area newArea = new Area();
        newArea.setName(name);
        areaRepository.save(newArea);
        log.debug("Created Information for Area: {}", newArea);
        return newArea;
    }

    public List<AreaDTO> getAll() {
        return areaRepository.findAll().stream()
            .map(AreaDTO::new)
            .collect(Collectors.toList());
    }

    public Optional<AreaDTO> updateArea(AreaDTO areaDTO) {
        return Optional.of(areaRepository
            .findOne(areaDTO.getId()))
            .map(area -> {
                area.setName(areaDTO.getName());
                area.setStatus(areaDTO.getStatus());
                log.debug("Changed Information for Area: {}", area);
                return area;
            })
            .map(AreaDTO::new);
    }

    public void deleteArea(String name) {
        areaRepository.findOneByName(name).ifPresent(area -> {
            areaRepository.delete(area);
            log.debug("Deleted Area: {}", area);
        });
    }
}
