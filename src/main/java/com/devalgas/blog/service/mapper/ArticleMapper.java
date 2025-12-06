package com.devalgas.blog.service.mapper;

import com.devalgas.blog.domain.Article;
import com.devalgas.blog.domain.CategoryArticle;
import com.devalgas.blog.service.dto.ArticleDTO;
import com.devalgas.blog.service.dto.CategoryArticleDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Article} and its DTO {@link ArticleDTO}.
 */
@Mapper(componentModel = "spring")
public interface ArticleMapper extends EntityMapper<ArticleDTO, Article> {
    @Mapping(target = "categoryArticles", source = "categoryArticles", qualifiedByName = "categoryArticleLabelSet")
    ArticleDTO toDto(Article s);

    @Mapping(target = "removeCategoryArticle", ignore = true)
    Article toEntity(ArticleDTO articleDTO);

    @Named("categoryArticleLabel")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "label", source = "label")
    CategoryArticleDTO toDtoCategoryArticleLabel(CategoryArticle categoryArticle);

    @Named("categoryArticleLabelSet")
    default Set<CategoryArticleDTO> toDtoCategoryArticleLabelSet(Set<CategoryArticle> categoryArticle) {
        return categoryArticle.stream().map(this::toDtoCategoryArticleLabel).collect(Collectors.toSet());
    }
}
