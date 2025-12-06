package com.devalgas.blog.service.mapper;

import com.devalgas.blog.domain.Article;
import com.devalgas.blog.domain.CategoryArticle;
import com.devalgas.blog.service.dto.ArticleDTO;
import com.devalgas.blog.service.dto.CategoryArticleDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CategoryArticle} and its DTO {@link CategoryArticleDTO}.
 */
@Mapper(componentModel = "spring")
public interface CategoryArticleMapper extends EntityMapper<CategoryArticleDTO, CategoryArticle> {
    @Mapping(target = "articles", source = "articles", qualifiedByName = "articleIdSet")
    CategoryArticleDTO toDto(CategoryArticle s);

    @Mapping(target = "articles", ignore = true)
    @Mapping(target = "removeArticle", ignore = true)
    CategoryArticle toEntity(CategoryArticleDTO categoryArticleDTO);

    @Named("articleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ArticleDTO toDtoArticleId(Article article);

    @Named("articleIdSet")
    default Set<ArticleDTO> toDtoArticleIdSet(Set<Article> article) {
        return article.stream().map(this::toDtoArticleId).collect(Collectors.toSet());
    }
}
