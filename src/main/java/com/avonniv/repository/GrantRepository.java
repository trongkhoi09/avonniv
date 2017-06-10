package com.avonniv.repository;

import com.avonniv.domain.Grant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrantRepository extends JpaRepository<Grant, Long> {
}
