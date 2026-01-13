package com.devalgas.blog.repository.v1;

import com.devalgas.blog.domain.Subscribe;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Subscribe entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SubscribeRepositoryV1 extends JpaRepository<Subscribe, Long> {
    Optional<Subscribe> findOneByEmailIgnoreCase(String email);
}
