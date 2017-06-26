package com.avonniv.service;

import com.avonniv.domain.CrawlHistory;
import com.avonniv.repository.CrawlHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Transactional
public class CrawHistoryService {

    private final Logger log = LoggerFactory.getLogger(CrawHistoryService.class);

    private final CrawlHistoryRepository crawlHistoryRepository;

    public CrawHistoryService(CrawlHistoryRepository crawlHistoryRepository) {
        this.crawlHistoryRepository = crawlHistoryRepository;
    }

    public Optional<CrawlHistory> findByName(String name) {
        return crawlHistoryRepository.findOneByName(name);
    }

    public void save(String name, Instant lastDateCrawl) {
        CrawlHistory crawlHistory = new CrawlHistory();
        crawlHistory.setName(name);
        crawlHistory.setLastDateCrawl(lastDateCrawl);
        crawlHistoryRepository.save(crawlHistory);
    }

    public CrawlHistory update(String name, Instant lastDateCrawl) {
        Optional<CrawlHistory> optional = findByName(name);
        if (optional.isPresent()) {
            return Optional.of(crawlHistoryRepository
                .findOne(optional.get().getId()))
                .map(crawlHistory -> {
                    crawlHistory.setLastDateCrawl(lastDateCrawl);
                    return crawlHistory;
                }).get();
        } else {
            CrawlHistory crawlHistory = new CrawlHistory();
            crawlHistory.setName(name);
            crawlHistory.setLastDateCrawl(lastDateCrawl);
            return crawlHistoryRepository.save(crawlHistory);
        }
    }
}
