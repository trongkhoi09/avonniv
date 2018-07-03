package com.avonniv.repository;

import com.avonniv.domain.OauthClientDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface OauthClientDetailRepository extends JpaRepository<OauthClientDetail, String> {
}
