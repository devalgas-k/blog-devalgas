package com.devalgas.blog.service.mapper.v1;

import com.devalgas.blog.repository.v1.projection.ArticleDetailsBasicProjectionV1;
import com.devalgas.blog.repository.v1.projection.ArticleLabelProjection;
import com.devalgas.blog.repository.v1.projection.ArticleSummaryBasicProjectionV1;
import com.devalgas.blog.repository.v1.projection.ArticleSummaryProjectionV1;
import com.devalgas.blog.repository.v1.projection.CategoryArticleLabelProjectionV1;
import com.devalgas.blog.service.dto.v1.ArticleDetailsBasicDTOV1;
import com.devalgas.blog.service.dto.v1.ArticleLabelBasicDTOV1;
import com.devalgas.blog.service.dto.v1.ArticleSummaryBasicDTOV1;
import com.devalgas.blog.service.dto.v1.CategoryLabelBasicDTOV1;
import java.time.ZonedDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ArticleProjectionMapperV1 {
    ArticleDetailsBasicDTOV1 toDto(ArticleDetailsBasicProjectionV1 projection);

    @Mapping(target = "date", source = "date")
    @Mapping(target = "labelFr", source = "labelFr")
    @Mapping(target = "labelEn", source = "labelEn")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "categoryArticles", ignore = true)
    ArticleSummaryBasicDTOV1 toDto(ArticleSummaryBasicProjectionV1 projection);

    @Mapping(target = "categoryArticles", ignore = true)
    ArticleSummaryBasicDTOV1 toDto(ArticleSummaryProjectionV1 projection);

    CategoryLabelBasicDTOV1 toDto(CategoryArticleLabelProjectionV1 projection);

    ArticleLabelBasicDTOV1 toDto(ArticleLabelProjection projection);

    default CategoryLabelBasicDTOV1 toCategoryLabelDto(Long id, String label) {
        CategoryLabelBasicDTOV1 dto = new CategoryLabelBasicDTOV1();
        dto.setId(id);
        dto.setLabel(label);
        return dto;
    }

    default ArticleLabelBasicDTOV1 toArticleLabelBasic(Long id, String labelFr, String labelEn, ZonedDateTime date) {
        ArticleLabelBasicDTOV1 dto = new ArticleLabelBasicDTOV1();
        dto.setId(id);
        dto.setLabelFr(labelFr);
        dto.setLabelEn(labelEn);
        dto.setDate(date);
        return dto;
    }
}
