package com.devalgas.blog.web.rest.v1;

import com.devalgas.blog.service.SubjectService;
import com.devalgas.blog.service.dto.SubjectDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

/**
 * REST controller for managing {@link com.devalgas.blog.domain.Subject}.
 */
@RestController
@RequestMapping("/api/v1/subjects")
public class SubjectResourceV1 {

    private static final Logger LOG = LoggerFactory.getLogger(SubjectResourceV1.class);

    private final SubjectService subjectService;

    public SubjectResourceV1(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    /**
     * {@code GET  /subjects} : get all the subjects.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of subjects in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SubjectDTO>> getAllSubjects(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Subjects");
        Page<SubjectDTO> page = subjectService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
