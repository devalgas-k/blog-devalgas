import { describe, it, expect, vi } from 'vitest';

let resolveCb: any;

vi.mock('vue', async () => {
  const actual = await vi.importActual<any>('vue');
  return {
    ...actual,
    default: { configureCompat: () => {} },
    createApp: (opts: any) => {
      if (opts && typeof opts.setup === 'function') {
        opts.setup();
      }
      return {
        component() {
          return this;
        },
        use() {
          return this;
        },
        mount() {
          return this;
        },
      };
    },
  };
});

vi.mock('./router', () => ({ default: { beforeResolve: (cb: any) => (resolveCb = cb), isReady: async () => {} } }));
vi.mock('pinia', () => ({ createPinia: () => ({}), storeToRefs: (obj: any) => obj }));
vi.mock('vue-i18n', () => ({ useI18n: () => ({ t: (k: string) => k, locale: { value: 'fr-FR' } }) }));
vi.mock('primevue/config', () => ({ default: {} }));
vi.mock('primeicons/fonts/primeicons.woff2?url', () => ({ default: '/mock.woff2' }));
vi.mock('@unhead/vue', () => {
  const useHead = vi.fn();
  const createHead = () => ({});
  return { useHead, createHead };
});
vi.mock('@/shared/config/config', () => ({ initFortAwesome: () => {}, initI18N: () => ({}) }));
vi.mock('@/shared/config/config-bootstrap-vue', () => ({ initBootstrapVue: () => {} }));
vi.mock('@/admin/tracker/tracker.service', () => ({ useTrackerService: () => () => ({}) }));
vi.mock('@/store', () => ({
  useStore: () => ({ account: {}, authenticated: true, logout: () => {} }),
  useTranslationStore: () => ({ currentLanguage: 'fr' }),
}));
vi.mock('@/account/login-modal', () => ({
  useLoginModal: () => ({ loginModalOpen: { value: false }, hideLogin: () => {}, showLogin: () => {} }),
}));
vi.mock('@/account/account.service', () => ({
  default: class {
    async update() {}
    async hasAnyAuthorityAndCheckAuth() {
      return false;
    }
  },
}));
vi.mock('@/shared/jhi-item-count.vue', () => ({ default: {} }));
vi.mock('@/shared/sort/jhi-sort-indicator.vue', () => ({ default: {} }));
vi.mock('primevue/scrolltop', () => ({ default: {} }));
vi.mock('primeicons/primeicons.css', () => ({}));
vi.mock('primevue/resources/primevue.min.css', () => ({}));
vi.mock('primevue/resources/themes/bootstrap4-dark-blue/theme.css', () => ({}));

describe('main.ts — auth guard', async () => {
  it('redirige vers /forbidden quand hasAnyAuthorityAndCheckAuth retourne false', async () => {
    await import('./main.ts');
    const next = vi.fn();
    await resolveCb({ meta: { authorities: ['ADMIN'] } }, { path: '/home' }, next);
    expect(next).toHaveBeenCalledWith({ path: '/forbidden' });
  });
});
