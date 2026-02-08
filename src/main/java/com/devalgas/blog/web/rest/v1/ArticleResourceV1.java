package com.devalgas.blog.web.rest.v1;

import com.devalgas.blog.domain.enumeration.Status;
import com.devalgas.blog.service.dto.v1.ArticleDetailV1DTO;
import com.devalgas.blog.service.dto.v1.ArticleHomeV1DTO;
import com.devalgas.blog.service.v1.ArticleServiceV1;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

/**
 * REST controller for managing {@link com.devalgas.blog.domain.Article}.
 */
@RestController
@RequestMapping("/api/v1/articles")
public class ArticleResourceV1 {

    private static final Logger log = LoggerFactory.getLogger(ArticleResourceV1.class);

    private final ArticleServiceV1 articleServiceV1;

    public ArticleResourceV1(ArticleServiceV1 articleServiceV1) {
        this.articleServiceV1 = articleServiceV1;
    }

    @GetMapping("/summary")
    public ResponseEntity<List<ArticleHomeV1DTO>> getAllArticlesV1(
        @ParameterObject Pageable pageable,
        @RequestParam(name = "status", required = false, defaultValue = "COMPLETED") Status status,
        @RequestParam(name = "display", required = false, defaultValue = "true") boolean display
    ) {
        log.debug("REST request to get a page of optimized Articles V1");
        Page<ArticleHomeV1DTO> page = articleServiceV1.findAllArticlesHome(status, display, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/summary/{id}")
    public ResponseEntity<ArticleDetailV1DTO> getArticleV1(
        @PathVariable("id") Long id,
        @RequestParam(value = "lang", required = false) String lang,
        @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage
    ) {
        log.debug("REST request to get optimized Article V1 : {}, lang={}", id, lang);
        ArticleDetailV1DTO article = articleServiceV1
            .findOneArticleDetails(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok().body(article);
    }

    @GetMapping(value = "/{id}/og-image")
    public ResponseEntity<byte[]> getArticleOgImage(@PathVariable("id") Long id) {
        log.debug("REST request to get OG image for Article V1 : {}", id);
        ArticleDetailV1DTO article = articleServiceV1
            .findOneArticleDetails(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        byte[] bytes = null;
        String contentType = null;
        if (article.getBanner() != null && article.getBanner().length > 0) {
            bytes = article.getBanner();
            contentType = article.getBannerContentType();
        } else if (article.getBadge() != null && article.getBadge().length > 0) {
            bytes = article.getBadge();
            contentType = article.getBadgeContentType();
        }

        if (bytes != null) {
            MediaType mt = contentType != null ? MediaType.parseMediaType(contentType) : MediaType.IMAGE_PNG;
            String etag = "\"" + DigestUtils.md5DigestAsHex(bytes) + "\"";
            return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic())
                .eTag(etag)
                .contentType(mt)
                .body(bytes);
        }

        try {
            ClassPathResource resource = new ClassPathResource("static/content/images/logo-app.png");
            byte[] logo = StreamUtils.copyToByteArray(resource.getInputStream());
            String etag = "\"" + DigestUtils.md5DigestAsHex(logo) + "\"";
            return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
                .eTag(etag)
                .contentType(MediaType.IMAGE_PNG)
                .body(logo);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "OG image not available");
        }
    }
}
