package com.avonniv.web.rest;

import com.avonniv.service.AreaService;
import com.avonniv.service.dto.AreaDTO;
import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/area")
public class AreaResource {

    private final Logger log = LoggerFactory.getLogger(AreaResource.class);

    private final AreaService areaService;

    public AreaResource(AreaService areaService) {
        this.areaService = areaService;
    }

    @GetMapping("")
    @Timed
    public ResponseEntity<List<AreaDTO>> getAll() {
        return new ResponseEntity<>(areaService.getAll(), HttpStatus.OK);
    }
}
