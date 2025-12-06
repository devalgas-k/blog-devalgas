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
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
