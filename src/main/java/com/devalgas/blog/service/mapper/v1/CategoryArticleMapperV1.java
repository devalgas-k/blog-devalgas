package com.devalgas.blog.service.mapper.v1;

import com.devalgas.blog.domain.Article;
import com.devalgas.blog.domain.CategoryArticle;
import com.devalgas.blog.service.dto.ArticleDTO;
import com.devalgas.blog.service.dto.CategoryArticleDTO;
import com.devalgas.blog.service.mapper.ArticleMapper;
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
@Mapper(componentModel = "spring", uses = { ArticleMapper.class })
public interface CategoryArticleMapperV1 extends EntityMapper<CategoryArticleDTO, CategoryArticle> {
    @Mapping(target = "articles", source = "articles")
    CategoryArticleDTO toDto(CategoryArticle s);

    @Mapping(target = "articles", ignore = true)
    @Mapping(target = "removeArticle", ignore = true)
    CategoryArticle toEntity(CategoryArticleDTO categoryArticleDTO);

    @Named("articleForDetail")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "labelEn", source = "labelEn")
    @Mapping(target = "labelFr", source = "labelFr")
    @Mapping(target = "descriptionFr", source = "descriptionFr")
    @Mapping(target = "descriptionEn", source = "descriptionEn")
    @Mapping(target = "markdownFr", source = "markdownFr")
    @Mapping(target = "markdownFrContentType", source = "markdownFrContentType")
    @Mapping(target = "markdownEn", source = "markdownEn")
    @Mapping(target = "markdownEnContentType", source = "markdownEnContentType")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "badge", source = "badge")
    @Mapping(target = "badgeContentType", source = "badgeContentType")
    ArticleDTO toDtoArticleForDetail(Article article);

    @Named("articleForDetailSet")
    default Set<ArticleDTO> toDtoArticleForDetailSet(Set<Article> article) {
        return article.stream().map(this::toDtoArticleForDetail).collect(Collectors.toSet());
    }
}
