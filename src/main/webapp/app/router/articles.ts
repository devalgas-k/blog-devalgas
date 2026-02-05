/* tslint:disable */
// prettier-ignore

const ArticleDetailsV1 = () => import('@/entities/article/v1/details/article-details-v1.vue');

export default [
  {
    path: '/v1/articles/:articleId/view',
    name: 'ArticleDetailsViewV1',
    component: ArticleDetailsV1,
  },
  {
    path: '/v1/articles/:articleId-:slug/view',
    name: 'ArticleDetailsViewV1Slug',
    component: ArticleDetailsV1,
  },
];
