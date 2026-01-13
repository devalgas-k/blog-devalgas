package com.devalgas.blog.service.mapper;

import com.devalgas.blog.domain.Message;
import com.devalgas.blog.domain.Subject;
import com.devalgas.blog.service.dto.MessageDTO;
import com.devalgas.blog.service.dto.SubjectDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Message} and its DTO {@link MessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface MessageMapper extends EntityMapper<MessageDTO, Message> {
    @Mapping(target = "subject", source = "subject", qualifiedByName = "subjectTitleFr")
    MessageDTO toDto(Message s);

    @Named("subjectTitleFr")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titleFr", source = "titleFr")
    SubjectDTO toDtoSubjectTitleFr(Subject subject);
}
