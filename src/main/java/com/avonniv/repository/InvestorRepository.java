package com.avonniv.repository;

import com.avonniv.domain.Investor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestorRepository extends JpaRepository<Investor, Long> {
    Page<Investor> findAll(Pageable pageable);
}
