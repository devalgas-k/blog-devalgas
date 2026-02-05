package com.devalgas.blog.service.mapper.v1;

import com.devalgas.blog.domain.Article;
import com.devalgas.blog.domain.v1.ArticleDetailV1;
import com.devalgas.blog.domain.v1.CategoryArticleHomeV1;
import com.devalgas.blog.service.dto.ArticleDTO;
import com.devalgas.blog.service.dto.v1.ArticleDetailV1DTO;
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
public interface ArticleDetailV1Mapper extends EntityMapper<ArticleDetailV1DTO, ArticleDetailV1> {
    @Mapping(target = "categoryArticles", source = "categoryArticles", qualifiedByName = "categoryArticleLabelSet")
    @Mapping(target = "badge", source = "badge")
    @Mapping(target = "badgeContentType", source = "badgeContentType")
    @Mapping(target = "banner", source = "banner")
    @Mapping(target = "bannerContentType", source = "bannerContentType")
    ArticleDetailV1DTO toDto(ArticleDetailV1 s);

    ArticleDetailV1 toEntity(ArticleDetailV1 articleDTO);

    @Named("articleLabel")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "labelEn", source = "labelEn")
    @Mapping(target = "labelFr", source = "labelFr")
    ArticleDetailV1DTO toDtoArticleLabel(ArticleDetailV1 article);

    @Named("articleLabelSet")
    default Set<ArticleDetailV1DTO> toDtoArticleLabelSet(Set<ArticleDetailV1> articles) {
        return articles.stream().map(this::toDtoArticleLabel).collect(Collectors.toSet());
    }

    @Named("categoryArticleLabel")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "label", source = "label")
    CategoryArticleHomeV1DTO toDtoCategoryArticleLabel(CategoryArticleHomeV1 categoryArticle);

    @Named("categoryArticleLabelSet")
    default Set<CategoryArticleHomeV1DTO> toDtoCategoryArticleLabelSet(Set<CategoryArticleHomeV1> categoryArticle) {
        return categoryArticle.stream().map(this::toDtoCategoryArticleLabel).collect(Collectors.toSet());
    }
}
