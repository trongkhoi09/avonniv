package com.avonniv.web.rest;

import com.avonniv.security.AuthoritiesConstants;
import com.avonniv.service.GrantProgramService;
import com.avonniv.service.dto.GrantProgramDTO;
import com.avonniv.web.rest.util.HeaderUtil;
import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/grantProgram")
public class GrantProgramResource {

    private final Logger log = LoggerFactory.getLogger(GrantProgramResource.class);

    private final GrantProgramService grantProgramService;

    public GrantProgramResource(GrantProgramService grantProgramService) {
        this.grantProgramService = grantProgramService;
    }

    @GetMapping("/{grantProgramId}")
    @Timed
    public ResponseEntity<GrantProgramDTO> getGrant(@PathVariable Long grantProgramId) {
        return ResponseUtil.wrapOrNotFound(
            grantProgramService.getById(grantProgramId)
                .map(GrantProgramDTO::new));
    }

    @PutMapping("")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<GrantProgramDTO> updateGrant(@RequestBody GrantProgramDTO grantProgramDTO) {
        return ResponseUtil.wrapOrNotFound(grantProgramService.update(grantProgramDTO),
            HeaderUtil.createAlert("grantProgramEdit.updated", grantProgramDTO.getName()));
    }
}
