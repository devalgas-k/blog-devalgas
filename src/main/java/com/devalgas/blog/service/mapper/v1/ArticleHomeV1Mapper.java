package com.devalgas.blog.service.mapper.v1;

import com.devalgas.blog.domain.Article;
import com.devalgas.blog.domain.v1.ArticleHomeV1;
import com.devalgas.blog.domain.v1.CategoryArticleHomeV1;
import com.devalgas.blog.service.dto.ArticleDTO;
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
 * Mapper for the entity {@link Article} and its DTO {@link ArticleDTO}.
 */
@Mapper(componentModel = "spring")
public interface ArticleHomeV1Mapper extends EntityMapper<ArticleHomeV1DTO, ArticleHomeV1> {
    @Mapping(target = "categoryArticles", source = "categoryArticles", qualifiedByName = "categoryArticleLabelSet")
    ArticleHomeV1DTO toDto(ArticleHomeV1 s);

    ArticleHomeV1 toEntity(ArticleHomeV1DTO articleDTO);

    @Named("articleLabel")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "labelEn", source = "labelEn")
    @Mapping(target = "labelFr", source = "labelFr")
    @Mapping(target = "date", source = "date")
    ArticleHomeV1DTO toDtoArticleLabel(ArticleHomeV1 article);

    @Named("articleLabelSet")
    default Set<ArticleHomeV1DTO> toDtoArticleLabelSet(Set<ArticleHomeV1> articles) {
        return articles.stream().map(this::toDtoArticleLabel).collect(Collectors.toSet());
    }

    @Named("categoryArticleLabel")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "label", source = "label")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "descriptionFr", source = "descriptionFr")
    @Mapping(target = "descriptionEn", source = "descriptionEn")
    CategoryArticleHomeV1DTO toDtoCategoryArticleLabel(CategoryArticleHomeV1 categoryArticle);

    @Named("categoryArticleLabelSet")
    default Set<CategoryArticleHomeV1DTO> toDtoCategoryArticleLabelSet(Set<CategoryArticleHomeV1> categoryArticle) {
        return categoryArticle.stream().map(this::toDtoCategoryArticleLabel).collect(Collectors.toSet());
    }
}
