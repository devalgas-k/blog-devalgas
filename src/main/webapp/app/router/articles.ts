/* tslint:disable */
// prettier-ignore

const ArticleDetailsV1 = () => import('@/entities/article/v1/article-details-v1.vue');

export default [
  {
    path: '/v1/articles/:articleId/view',
    name: 'ArticleDetailsViewV1',
    component: ArticleDetailsV1,
  },
];
