package com.avonniv.web.rest;

import com.avonniv.security.SecurityUtils;
import com.avonniv.service.PreferencesService;
import com.avonniv.service.dto.PreferencesDTO;
import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/preferences")
public class PreferencesResource {

    private final Logger log = LoggerFactory.getLogger(PreferencesResource.class);

    private final PreferencesService preferencesService;

    public PreferencesResource(PreferencesService preferencesService) {
        this.preferencesService = preferencesService;
    }

    @GetMapping("")
    @Timed
    public ResponseEntity<List<PreferencesDTO>> getAll() {
        final String userLogin = SecurityUtils.getCurrentUserLogin();
        return new ResponseEntity<>(preferencesService.getAll(userLogin), HttpStatus.OK);
    }


    @PostMapping("")
    @Timed
    public ResponseEntity updatePreferences(@RequestParam("publisherId") Long publisherId, @RequestParam("notification") boolean notification) throws URISyntaxException {
        final String userLogin = SecurityUtils.getCurrentUserLogin();
        PreferencesDTO preferencesDTO = preferencesService.updatePreferences(userLogin, publisherId, notification);
        if (preferencesDTO != null) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
