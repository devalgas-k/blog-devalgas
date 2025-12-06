package com.devalgas.blog.service.mapper;

import com.devalgas.blog.domain.Headers;
import com.devalgas.blog.service.dto.HeadersDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Headers} and its DTO {@link HeadersDTO}.
 */
@Mapper(componentModel = "spring")
public interface HeadersMapper extends EntityMapper<HeadersDTO, Headers> {}
