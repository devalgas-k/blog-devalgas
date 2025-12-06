package com.devalgas.blog.repository;

import com.devalgas.blog.domain.Headers;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Headers entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HeadersRepository extends JpaRepository<Headers, Long> {}
