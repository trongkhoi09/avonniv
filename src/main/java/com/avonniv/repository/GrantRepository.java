package com.avonniv.repository;

import com.avonniv.domain.Area;
import com.avonniv.domain.Grant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GrantRepository extends JpaRepository<Grant, Long> {
    List<Grant> findAllByAreasContains(Area area);

    Optional<Grant> findOneByName(String name);
}
