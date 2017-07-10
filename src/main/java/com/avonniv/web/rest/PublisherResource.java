package com.avonniv.web.rest;

import com.avonniv.domain.Publisher;
import com.avonniv.repository.PublisherRepository;
import com.avonniv.security.AuthoritiesConstants;
import com.avonniv.service.MailService;
import com.avonniv.service.PublisherService;
import com.avonniv.service.dto.PublisherDTO;
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

@RestController
@RequestMapping("/api/publishers")
public class PublisherResource {

    private final Logger log = LoggerFactory.getLogger(PublisherResource.class);

    private static final String ENTITY_NAME = "publisherManagement";

    private final PublisherRepository publisherRepository;

    private final MailService mailService;

    private final PublisherService publisherService;

    public PublisherResource(PublisherRepository publisherRepository, MailService mailService,
                             PublisherService publisherService) {

        this.publisherRepository = publisherRepository;
        this.mailService = mailService;
        this.publisherService = publisherService;
    }

    @PostMapping("")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity createPublisher(@Valid @RequestBody PublisherDTO publisherDTO) throws URISyntaxException {
        log.debug("REST request to save Publisher : {}", publisherDTO);

        if (publisherDTO.getId() != null) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new publisher cannot already have an ID"))
                .body(null);
        } else if (publisherRepository.findOneByName(publisherDTO.getName()).isPresent()) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "publisherexists", "Name already in use"))
                .body(null);
        } else {
            Publisher newPublisher = publisherService.createPublisher(publisherDTO);
            return ResponseEntity.created(new URI("/api/publishers/" + newPublisher.getName()))
                .headers(HeaderUtil.createAlert("publisherManagement.created", newPublisher.getName()))
                .body(newPublisher);
        }
    }

    @PutMapping("")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<PublisherDTO> updatePublisher(@Valid @RequestBody PublisherDTO publisherDTO) {
        log.debug("REST request to update Publisher : {}", publisherDTO);
        Optional<Publisher> existingPublisher = publisherRepository.findOneByName(publisherDTO.getName());
        if (existingPublisher.isPresent() && (!existingPublisher.get().getId().equals(publisherDTO.getId()))) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "publisherexists", "Name already in use")).body(null);
        }
        Optional<PublisherDTO> updatedPublisher = publisherService.updatePublisher(publisherDTO);

        return ResponseUtil.wrapOrNotFound(updatedPublisher,
            HeaderUtil.createAlert("publisherManagement.updated", publisherDTO.getName()));
    }

    @GetMapping("")
    @Timed
    public ResponseEntity<List<PublisherDTO>> getAllPublishers(@ApiParam Pageable pageable) {
        final Page<PublisherDTO> page = publisherService.getAllPublisher(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/publishers");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{name}")
    @Timed
    public ResponseEntity<PublisherDTO> getPublisher(@PathVariable String name) {
        log.debug("REST request to get Publisher : {}", name);
        return ResponseUtil.wrapOrNotFound(
            publisherService.getPublisherByName(name)
                .map(PublisherDTO::new));
    }

    @DeleteMapping("/{name}")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deletePublisher(@PathVariable String name) {
        log.debug("REST request to delete Publisher: {}", name);
        publisherService.deletePublisher(name);
        return ResponseEntity.ok().headers(HeaderUtil.createAlert("publisherManagement.deleted", name)).build();
    }
}
