package com.avonniv.service;

import com.avonniv.domain.Publisher;
import com.avonniv.repository.PublisherRepository;
import com.avonniv.service.dto.PublisherDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PublisherService {

    private final Logger log = LoggerFactory.getLogger(PublisherService.class);

    private final PublisherRepository publisherRepository;

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    public Publisher createPublisher(PublisherDTO publisherDTO) {
        Publisher newPublisher = new Publisher();
        newPublisher.setName(publisherDTO.getName());
        newPublisher.setDescription(publisherDTO.getDescription());
        newPublisher.setAddress(publisherDTO.getAddress());
        newPublisher.setEmail(publisherDTO.getEmail());
        newPublisher.setPhone(publisherDTO.getPhone());
        newPublisher.setUrl(publisherDTO.getUrl());

        publisherRepository.save(newPublisher);
        log.debug("Created Information for Publisher: {}", newPublisher);
        return newPublisher;
    }

    public List<PublisherDTO> getAll() {
        return publisherRepository.findAll().stream().map(PublisherDTO::new).collect(Collectors.toList());
    }

    public Optional<PublisherDTO> updatePublisher(PublisherDTO publisherDTO) {
        return Optional.of(publisherRepository
            .findOne(publisherDTO.getId()))
            .map(publisher -> {
                publisher.setName(publisherDTO.getName());
                publisher.setDescription(publisherDTO.getDescription());
                publisher.setAddress(publisherDTO.getAddress());
                publisher.setEmail(publisherDTO.getEmail());
                publisher.setPhone(publisherDTO.getPhone());
                publisher.setUrl(publisherDTO.getUrl());
                log.debug("Changed Information for Publisher: {}", publisher);
                return publisher;
            })
            .map(PublisherDTO::new);
    }

    public void deletePublisher(PublisherDTO publisherDTO) {
        publisherRepository.delete(publisherDTO.getId());
    }
}
