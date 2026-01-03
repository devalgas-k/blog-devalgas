package com.devalgas.blog.service.mapper;

import com.devalgas.blog.domain.Subject;
import com.devalgas.blog.service.dto.SubjectDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Subject} and its DTO {@link SubjectDTO}.
 */
@Mapper(componentModel = "spring")
public interface SubjectMapper extends EntityMapper<SubjectDTO, Subject> {}
