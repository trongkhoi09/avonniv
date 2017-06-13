package com.avonniv.repository;

import com.avonniv.domain.Publisher;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    List<Publisher> findAllByNameLike(String name);

    Optional<Publisher> findOneByName(String name);
}
