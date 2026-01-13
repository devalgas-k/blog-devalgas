import { Authority } from '@/shared/security/authority';
const Entities = () => import('@/entities/entities.vue');

const Headers = () => import('@/entities/headers/headers.vue');
const HeadersUpdate = () => import('@/entities/headers/headers-update.vue');
const HeadersDetails = () => import('@/entities/headers/headers-details.vue');

const Footers = () => import('@/entities/footers/footers.vue');
const FootersUpdate = () => import('@/entities/footers/footers-update.vue');
const FootersDetails = () => import('@/entities/footers/footers-details.vue');

const AppInfo = () => import('@/entities/app-info/app-info.vue');
const AppInfoUpdate = () => import('@/entities/app-info/app-info-update.vue');
const AppInfoDetails = () => import('@/entities/app-info/app-info-details.vue');

const CategoryArticle = () => import('@/entities/category-article/category-article.vue');
const CategoryArticleUpdate = () => import('@/entities/category-article/category-article-update.vue');
const CategoryArticleDetails = () => import('@/entities/category-article/category-article-details.vue');

const Article = () => import('@/entities/article/article.vue');
const ArticleUpdate = () => import('@/entities/article/article-update.vue');
const ArticleDetails = () => import('@/entities/article/article-details.vue');

const Subscribe = () => import('@/entities/subscribe/subscribe.vue');
const SubscribeUpdate = () => import('@/entities/subscribe/subscribe-update.vue');
const SubscribeDetails = () => import('@/entities/subscribe/subscribe-details.vue');

const Message = () => import('@/entities/message/message.vue');
const MessageUpdate = () => import('@/entities/message/message-update.vue');
const MessageDetails = () => import('@/entities/message/message-details.vue');

const Subject = () => import('@/entities/subject/subject.vue');
const SubjectUpdate = () => import('@/entities/subject/subject-update.vue');
const SubjectDetails = () => import('@/entities/subject/subject-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'headers',
      name: 'Headers',
      component: Headers,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'headers/new',
      name: 'HeadersCreate',
      component: HeadersUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'headers/:headersId/edit',
      name: 'HeadersEdit',
      component: HeadersUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'headers/:headersId/view',
      name: 'HeadersView',
      component: HeadersDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'footers',
      name: 'Footers',
      component: Footers,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'footers/new',
      name: 'FootersCreate',
      component: FootersUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'footers/:footersId/edit',
      name: 'FootersEdit',
      component: FootersUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'footers/:footersId/view',
      name: 'FootersView',
      component: FootersDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'app-info',
      name: 'AppInfo',
      component: AppInfo,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'app-info/new',
      name: 'AppInfoCreate',
      component: AppInfoUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'app-info/:appInfoId/edit',
      name: 'AppInfoEdit',
      component: AppInfoUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'app-info/:appInfoId/view',
      name: 'AppInfoView',
      component: AppInfoDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'category-article',
      name: 'CategoryArticle',
      component: CategoryArticle,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'category-article/new',
      name: 'CategoryArticleCreate',
      component: CategoryArticleUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'category-article/:categoryArticleId/edit',
      name: 'CategoryArticleEdit',
      component: CategoryArticleUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'category-article/:categoryArticleId/view',
      name: 'CategoryArticleView',
      component: CategoryArticleDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'article',
      name: 'Article',
      component: Article,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'article/new',
      name: 'ArticleCreate',
      component: ArticleUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'article/:articleId/edit',
      name: 'ArticleEdit',
      component: ArticleUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'article/:articleId/view',
      name: 'ArticleView',
      component: ArticleDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'subscribe',
      name: 'Subscribe',
      component: Subscribe,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'subscribe/new',
      name: 'SubscribeCreate',
      component: SubscribeUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'subscribe/:subscribeId/edit',
      name: 'SubscribeEdit',
      component: SubscribeUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'subscribe/:subscribeId/view',
      name: 'SubscribeView',
      component: SubscribeDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'message',
      name: 'Message',
      component: Message,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'message/new',
      name: 'MessageCreate',
      component: MessageUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'message/:messageId/edit',
      name: 'MessageEdit',
      component: MessageUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'message/:messageId/view',
      name: 'MessageView',
      component: MessageDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'subject',
      name: 'Subject',
      component: Subject,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'subject/new',
      name: 'SubjectCreate',
      component: SubjectUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'subject/:subjectId/edit',
      name: 'SubjectEdit',
      component: SubjectUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'subject/:subjectId/view',
      name: 'SubjectView',
      component: SubjectDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
