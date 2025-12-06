package com.devalgas.blog.service.mapper;

import com.devalgas.blog.domain.Footers;
import com.devalgas.blog.service.dto.FootersDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Footers} and its DTO {@link FootersDTO}.
 */
@Mapper(componentModel = "spring")
public interface FootersMapper extends EntityMapper<FootersDTO, Footers> {}
