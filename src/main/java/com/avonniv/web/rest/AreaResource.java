package com.avonniv.web.rest;

import com.avonniv.domain.Area;
import com.avonniv.repository.AreaRepository;
import com.avonniv.security.AuthoritiesConstants;
import com.avonniv.service.AreaService;
import com.avonniv.service.dto.AreaDTO;
import com.avonniv.web.rest.util.HeaderUtil;
import com.avonniv.web.rest.util.PaginationUtil;
import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;

@RestController
@RequestMapping("/api/areas")
public class AreaResource {

    private final Logger log = LoggerFactory.getLogger(AreaResource.class);

    private final AreaService areaService;

    private final AreaRepository areaRepository;

    public AreaResource(AreaService areaService,
                        AreaRepository areaRepository) {
        this.areaService = areaService;
        this.areaRepository = areaRepository;
    }

    @GetMapping("/all")
    @Timed
    public ResponseEntity<List<AreaDTO>> getAll() {
        return new ResponseEntity<>(areaService.getAll(), HttpStatus.OK);
    }


    @PostMapping("")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity createArea(@Valid @RequestBody AreaDTO areaDTO) throws URISyntaxException {
        log.debug("REST request to save Area : {}", areaDTO);
        areaDTO.setName(areaDTO.getName().trim());
        if (areaDTO.getName().isEmpty()) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "areaEmpty", "Name can not empty"))
                .body(null);
        }
        if (areaDTO.getId() != null) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new area cannot already have an ID"))
                .body(null);
        } else if (areaRepository.findOneByName(areaDTO.getName()).isPresent()) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "areaexists", "Name already in use"))
                .body(null);
        } else {
            Area newArea = areaService.createArea(areaDTO);
            return ResponseEntity.created(new URI("/api/areas/" + newArea.getName().replace(" ", "%20")))
                .headers(HeaderUtil.createAlert("areaManagement.created", newArea.getName()))
                .body(newArea);
        }
    }

    @PutMapping("")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<AreaDTO> updateArea(@Valid @RequestBody AreaDTO areaDTO) {
        log.debug("REST request to update Area : {}", areaDTO);
        Optional<Area> existingArea = areaRepository.findOneByName(areaDTO.getName());
        if (existingArea.isPresent() && (!existingArea.get().getId().equals(areaDTO.getId()))) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "areaexists", "Name already in use")).body(null);
        }
        Optional<AreaDTO> updatedArea = areaService.updateArea(areaDTO);

        return ResponseUtil.wrapOrNotFound(updatedArea,
            HeaderUtil.createAlert("areaManagement.updated", areaDTO.getName()));
    }

    @GetMapping("")
    @Timed
    public ResponseEntity<List<AreaDTO>> getAllAreas(@ApiParam Pageable pageable) {
        final Page<AreaDTO> page = areaService.getAllArea(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/areas");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/search/{searchName}")
    @Timed
    public ResponseEntity<List<AreaDTO>> searchAllAreas(@ApiParam Pageable pageable, @PathVariable String searchName) {
        final Page<AreaDTO> page = areaService.search(pageable, searchName);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/areas");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{name}")
    @Timed
    public ResponseEntity<AreaDTO> getArea(@PathVariable String name) {
        log.debug("REST request to get Area : {}", name);
        return ResponseUtil.wrapOrNotFound(
            areaService.getAreaByName(name)
                .map(AreaDTO::new));
    }

    @DeleteMapping("/{name}")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteArea(@PathVariable String name) {
        log.debug("REST request to delete Area: {}", name);
        areaService.deleteArea(name);
        return ResponseEntity.ok().headers(HeaderUtil.createAlert("areaManagement.deleted", name)).build();
    }
}
