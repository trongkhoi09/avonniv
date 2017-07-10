package com.avonniv.repository;

import com.avonniv.domain.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    List<Publisher> findAllByNameLike(String name);

    Optional<Publisher> findOneByName(String name);

    List<Publisher> findAllByCrawledIs(Boolean crawled);
}
