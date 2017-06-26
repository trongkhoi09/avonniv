package com.avonniv.repository;

import com.avonniv.domain.Area;
import com.avonniv.domain.GrantProgram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GrantProgramRepository extends JpaRepository<GrantProgram, Long> {
    List<GrantProgram> findAllByAreasContains(Area area);

    Optional<GrantProgram> findOneByName(String name);


    Optional<GrantProgram> findOneByExternalId(String externalId);
}
