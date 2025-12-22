package com.devalgas.blog.repository;

import com.devalgas.blog.domain.Subscribe;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Subscribe entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {}
