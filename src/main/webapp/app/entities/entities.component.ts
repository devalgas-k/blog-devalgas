import { defineComponent, provide } from 'vue';

import HeadersService from './headers/headers.service';
import FootersService from './footers/footers.service';
import AppInfoService from './app-info/app-info.service';
import CategoryArticleService from './category-article/category-article.service';
import ArticleService from './article/article.service';
import UserService from '@/entities/user/user.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('headersService', () => new HeadersService());
    provide('footersService', () => new FootersService());
    provide('appInfoService', () => new AppInfoService());
    provide('categoryArticleService', () => new CategoryArticleService());
    provide('articleService', () => new ArticleService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
