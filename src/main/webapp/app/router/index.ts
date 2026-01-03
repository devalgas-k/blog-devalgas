import { createRouter as createVueRouter, createWebHistory } from 'vue-router';

const Home = () => import('@/core/home/home.vue');
const Error = () => import('@/core/error/error.vue');
import account from '@/router/account';
import admin from '@/router/admin';
import entities from '@/router/entities';
import pages from '@/router/pages';

//Custom
import articles from '@/router/articles';

export const createRouter = () =>
  createVueRouter({
    history: createWebHistory(),
    scrollBehavior(to, from, savedPosition) {
      if (savedPosition) {
        return savedPosition;
      }
      if (to.hash) {
        const el = document.querySelector(to.hash);
        if (el) {
          return {
            el: to.hash,
            behavior: 'smooth',
          } as any;
        }
        return { left: 0, top: 0 };
      }
      return { left: 0, top: 0 };
    },
    routes: [
      {
        path: '/',
        name: 'Home',
        component: Home,
      },
      {
        path: '/forbidden',
        name: 'Forbidden',
        component: Error,
        meta: { error403: true },
      },
      {
        path: '/not-found',
        name: 'NotFound',
        component: Error,
        meta: { error404: true },
      },
      ...account,
      ...admin,
      entities,
      ...pages,
      ...articles,
    ],
  });

const router = createRouter();

router.beforeResolve(async (to, from, next) => {
  if (!to.matched.length) {
    next({ path: '/not-found' });
    return;
  }
  next();
});

export default router;
