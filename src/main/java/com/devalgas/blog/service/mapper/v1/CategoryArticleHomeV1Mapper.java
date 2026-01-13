package com.devalgas.blog.service.mapper.v1;

import com.devalgas.blog.domain.CategoryArticle;
import com.devalgas.blog.domain.v1.ArticleHomeV1;
import com.devalgas.blog.domain.v1.CategoryArticleHomeV1;
import com.devalgas.blog.service.dto.CategoryArticleDTO;
import com.devalgas.blog.service.dto.v1.ArticleHomeV1DTO;
import com.devalgas.blog.service.dto.v1.CategoryArticleHomeV1DTO;
import com.devalgas.blog.service.mapper.EntityMapper;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link CategoryArticle} and its DTO {@link CategoryArticleDTO}.
 */
@Mapper(componentModel = "spring")
public interface CategoryArticleHomeV1Mapper extends EntityMapper<CategoryArticleHomeV1DTO, CategoryArticleHomeV1> {
    @Mapping(target = "articles", source = "articles", qualifiedByName = "articleIdSet")
    CategoryArticleHomeV1DTO toDto(CategoryArticleHomeV1 s);

    @Mapping(target = "articles", ignore = true)
    @Mapping(target = "removeArticle", ignore = true)
    CategoryArticleHomeV1 toEntity(CategoryArticleHomeV1DTO categoryArticleDTO);

    @Named("articleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "labelEn", source = "labelEn")
    @Mapping(target = "labelFr", source = "labelFr")
    ArticleHomeV1DTO toDtoArticleId(ArticleHomeV1 article);

    @Named("articleIdSet")
    default Set<ArticleHomeV1DTO> toDtoArticleIdSet(Set<ArticleHomeV1> article) {
        return article.stream().map(this::toDtoArticleId).collect(Collectors.toSet());
    }
}
