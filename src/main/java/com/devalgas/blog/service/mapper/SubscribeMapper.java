package com.devalgas.blog.service.mapper;

import com.devalgas.blog.domain.Subscribe;
import com.devalgas.blog.service.dto.SubscribeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Subscribe} and its DTO {@link SubscribeDTO}.
 */
@Mapper(componentModel = "spring")
public interface SubscribeMapper extends EntityMapper<SubscribeDTO, Subscribe> {}
