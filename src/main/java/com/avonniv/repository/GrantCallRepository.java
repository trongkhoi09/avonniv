package com.avonniv.repository;

import com.avonniv.domain.GrantCall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GrantCallRepository extends JpaRepository<GrantCall, Long> {
    Optional<GrantCall> findOneByTitle(String title);
}
