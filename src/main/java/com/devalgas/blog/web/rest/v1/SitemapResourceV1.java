package com.devalgas.blog.web.rest.v1;

import com.devalgas.blog.domain.enumeration.Status;
import com.devalgas.blog.service.dto.v1.ArticleHomeV1DTO;
import com.devalgas.blog.service.v1.ArticleServiceV1;
import java.text.Normalizer;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class SitemapResourceV1 {

    private static final Logger log = LoggerFactory.getLogger(SitemapResourceV1.class);
    private static final int PAGE_SIZE = 500;

    private final ArticleServiceV1 articleServiceV1;

    public SitemapResourceV1(ArticleServiceV1 articleServiceV1) {
        this.articleServiceV1 = articleServiceV1;
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> sitemap() {
        log.debug("REST request to generate sitemap.xml");
        String baseUrl = ServletUriComponentsBuilder.fromCurrentRequest().replacePath(null).build().toUriString();
        List<ArticleHomeV1DTO> all = new ArrayList<>();
        int page = 0;
        while (true) {
            Page<ArticleHomeV1DTO> p = articleServiceV1.findAllArticlesHome(Status.COMPLETED, true, PageRequest.of(page, PAGE_SIZE));
            List<ArticleHomeV1DTO> content = p.getContent();
            if (content.isEmpty()) {
                break;
            }
            all.addAll(content);
            if (!p.hasNext()) {
                break;
            }
            page++;
        }
        StringBuilder sb = new StringBuilder(4096);
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append(
            "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:xhtml=\"http://www.w3.org/1999/xhtml\" xmlns:image=\"http://www.google.com/schemas/sitemap-image/1.1\">\n"
        );
        DateTimeFormatter iso = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        for (ArticleHomeV1DTO a : all) {
            String title = a.getLabelFr() != null && !a.getLabelFr().isBlank() ? a.getLabelFr() : a.getLabelEn();
            String slug = slugify(title);
            String loc = baseUrl + "/v1/articles/" + a.getId() + "-" + slug + "/view";
            String locFr = baseUrl + "/v1/articles/" + a.getId() + "-" + slugify(a.getLabelFr()) + "/view";
            String locEn = baseUrl + "/v1/articles/" + a.getId() + "-" + slugify(a.getLabelEn()) + "/view";
            String img = baseUrl + "/api/v1/articles/" + a.getId() + "/og-image";
            String lastmod = a.getDate() != null ? iso.format(a.getDate()) : null;
            sb.append("  <url>\n");
            sb.append("    <loc>").append(escapeXml(loc)).append("</loc>\n");
            sb.append("    <xhtml:link rel=\"alternate\" hreflang=\"fr\" href=\"").append(escapeXml(locFr)).append("\"/>\n");
            sb.append("    <xhtml:link rel=\"alternate\" hreflang=\"en\" href=\"").append(escapeXml(locEn)).append("\"/>\n");
            sb.append("    <xhtml:link rel=\"alternate\" hreflang=\"x-default\" href=\"").append(escapeXml(loc)).append("\"/>\n");
            sb.append("    <image:image>\n");
            sb.append("      <image:loc>").append(escapeXml(img)).append("</image:loc>\n");
            sb.append("    </image:image>\n");
            if (lastmod != null) {
                sb.append("    <lastmod>").append(escapeXml(lastmod)).append("</lastmod>\n");
            }
            sb.append("  </url>\n");
        }
        sb.append("</urlset>\n");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(sb.toString());
    }

    private static String slugify(String s) {
        if (s == null) {
            return "";
        }
        String lower = s.toLowerCase();
        String normalized = Normalizer.normalize(lower, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String hyphenated = normalized.replaceAll("[^a-z0-9]+", "-").replaceAll("(^-+|-+$)", "");
        if (hyphenated.length() > 80) {
            hyphenated = hyphenated.substring(0, 80);
        }
        return hyphenated;
    }

    private static String escapeXml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
    }
}
