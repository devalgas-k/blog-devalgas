package com.devalgas.blog.service.mapper;

import com.devalgas.blog.domain.AppInfo;
import com.devalgas.blog.domain.Footers;
import com.devalgas.blog.domain.Headers;
import com.devalgas.blog.service.dto.AppInfoDTO;
import com.devalgas.blog.service.dto.FootersDTO;
import com.devalgas.blog.service.dto.HeadersDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AppInfo} and its DTO {@link AppInfoDTO}.
 */
@Mapper(componentModel = "spring")
public interface AppInfoMapper extends EntityMapper<AppInfoDTO, AppInfo> {
    @Mapping(target = "headers", source = "headers", qualifiedByName = "headersId")
    @Mapping(target = "footers", source = "footers", qualifiedByName = "footersId")
    AppInfoDTO toDto(AppInfo s);

    @Named("headersId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    HeadersDTO toDtoHeadersId(Headers headers);

    @Named("footersId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FootersDTO toDtoFootersId(Footers footers);
}
