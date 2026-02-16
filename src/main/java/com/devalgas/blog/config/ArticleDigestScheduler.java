package com.devalgas.blog.config;

import com.devalgas.blog.service.impl.v1.ArticleDigestServiceV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableScheduling
public class ArticleDigestScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleDigestScheduler.class);

    private final ArticleDigestServiceV1 service;

    public ArticleDigestScheduler(ArticleDigestServiceV1 service) {
        this.service = service;
    }

    @Transactional
    @Scheduled(cron = "${app.article-digest.cron}", zone = "${app.article-digest.zone:Europe/Paris}")
    public void run() {
        LOG.info("ArticleDigestScheduler triggered");
        service.processDigest();
    }
}
