import { type ICategoryArticle } from '@/shared/model/category-article.model';

import { type Status } from '@/shared/model/enumerations/status.model';
export interface IArticle {
  id?: number;
  labelEn?: string;
  labelFr?: string;
  descriptionFr?: string | null;
  descriptionEn?: string | null;
  markdownFrContentType?: string | null;
  markdownFr?: string | null;
  markdownEnContentType?: string | null;
  markdownEn?: string | null;
  status?: keyof typeof Status;
  date?: Date;
  badgeContentType?: string | null;
  badge?: string | null;
  bannerContentType?: string | null;
  banner?: string | null;
  views?: number | null;
  stars?: number | null;
  display?: boolean | null;
  categoryArticles?: ICategoryArticle[] | null;
}

export class Article implements IArticle {
  constructor(
    public id?: number,
    public labelEn?: string,
    public labelFr?: string,
    public descriptionFr?: string | null,
    public descriptionEn?: string | null,
    public markdownFrContentType?: string | null,
    public markdownFr?: string | null,
    public markdownEnContentType?: string | null,
    public markdownEn?: string | null,
    public status?: keyof typeof Status,
    public date?: Date,
    public badgeContentType?: string | null,
    public badge?: string | null,
    public bannerContentType?: string | null,
    public banner?: string | null,
    public views?: number | null,
    public stars?: number | null,
    public display?: boolean | null,
    public categoryArticles?: ICategoryArticle[] | null,
  ) {
    this.display = this.display ?? false;
  }
}
