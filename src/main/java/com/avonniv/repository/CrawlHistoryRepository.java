package com.avonniv.repository;

import com.avonniv.domain.CrawlHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CrawlHistoryRepository extends JpaRepository<CrawlHistory, Long> {
    Optional<CrawlHistory> findOneByName(String name);
}
