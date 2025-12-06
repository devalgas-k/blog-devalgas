import { type IArticle } from '@/shared/model/article.model';

export interface ICategoryArticle {
  id?: number;
  label?: string;
  code?: string;
  badgeContentType?: string | null;
  badge?: string | null;
  descriptionFr?: string | null;
  descriptionEn?: string | null;
  articles?: IArticle[] | null;
}

export class CategoryArticle implements ICategoryArticle {
  constructor(
    public id?: number,
    public label?: string,
    public code?: string,
    public badgeContentType?: string | null,
    public badge?: string | null,
    public descriptionFr?: string | null,
    public descriptionEn?: string | null,
    public articles?: IArticle[] | null,
  ) {}
}
