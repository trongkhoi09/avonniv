package com.avonniv.repository;

import java.util.Optional;

import com.avonniv.domain.Area;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AreaRepository extends JpaRepository<Area, Long> {

    Optional<Area> findOneByName(String name);

    Page<Area> findAllByNameLike(String name, Pageable pageable);
}
