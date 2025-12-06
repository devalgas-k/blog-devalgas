package com.devalgas.blog.repository;

import com.devalgas.blog.domain.Footers;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Footers entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FootersRepository extends JpaRepository<Footers, Long> {}
