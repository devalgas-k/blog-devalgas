package com.devalgas.blog.repository;

import com.devalgas.blog.domain.AppInfo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AppInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AppInfoRepository extends JpaRepository<AppInfo, Long> {}
