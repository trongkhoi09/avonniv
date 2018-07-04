package com.avonniv.web.rest;

import com.avonniv.repository.OauthClientDetailRepository;
import com.avonniv.service.dto.OauthClientDetailDTO;
import com.codahale.metrics.annotation.Timed;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class OauthClientDetailResource {

    private OauthClientDetailRepository oauthClientDetailRepository;

    public OauthClientDetailResource(OauthClientDetailRepository oauthClientDetailRepository) {
        this.oauthClientDetailRepository = oauthClientDetailRepository;
    }

    @GetMapping("/oauth")
    @Timed
    public ResponseEntity<List<OauthClientDetailDTO>> getAllOauthClientDetail() {
        final List<OauthClientDetailDTO> page = oauthClientDetailRepository.findAll().stream().map(OauthClientDetailDTO::new).collect(Collectors.toList());
        return new ResponseEntity<>(page, new HttpHeaders(), HttpStatus.OK);
    }
}
