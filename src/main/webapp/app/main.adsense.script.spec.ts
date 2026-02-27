import { describe, it, expect, vi } from 'vitest';

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

vi.mock('@unhead/vue', () => {
  const useHead = vi.fn();
  const createHead = () => ({});
  return { useHead, createHead };
});

vi.mock('./router', () => ({ default: { beforeResolve: () => {}, isReady: async () => {} } }));
vi.mock('pinia', () => ({ createPinia: () => ({}), storeToRefs: (obj: any) => obj }));
vi.mock('vue-i18n', () => ({ useI18n: () => ({ t: (k: string) => k, locale: { value: 'fr-FR' } }) }));
vi.mock('primevue/config', () => ({ default: {} }));
vi.mock('primeicons/fonts/primeicons.woff2?url', () => ({ default: '/mock.woff2' }));
vi.mock('@/shared/config/config', () => ({ initFortAwesome: () => {}, initI18N: () => ({}) }));
vi.mock('@/shared/config/config-bootstrap-vue', () => ({ initBootstrapVue: () => {} }));
vi.mock('@/admin/tracker/tracker.service', () => ({ useTrackerService: () => () => ({}) }));
vi.mock('@/store', () => ({
  useStore: () => ({ account: {}, authenticated: true }),
  useTranslationStore: () => ({ currentLanguage: 'fr' }),
}));
vi.mock('@/account/login-modal', () => ({
  useLoginModal: () => ({ loginModalOpen: { value: false }, hideLogin: () => {}, showLogin: () => {} }),
}));
vi.mock('@/account/account.service', () => ({
  default: class {
    async update() {}
    async hasAnyAuthorityAndCheckAuth() {
      return true;
    }
  },
}));
vi.mock('@/shared/jhi-item-count.vue', () => ({ default: {} }));
vi.mock('@/shared/sort/jhi-sort-indicator.vue', () => ({ default: {} }));
vi.mock('primevue/scrolltop', () => ({ default: {} }));
vi.mock('primeicons/primeicons.css', () => ({}));
vi.mock('primevue/resources/primevue.min.css', () => ({}));
vi.mock('primevue/resources/themes/bootstrap4-dark-blue/theme.css', () => ({}));

describe('main.ts — injection du script AdSense via Unhead', async () => {
  it('appelle useHead avec le script AdSense', async () => {
    const unhead = await import('@unhead/vue');
    const { useHead } = unhead as any;
    await import('./main.ts');
    const calls = useHead.mock.calls;
    const found = calls.find((c: unknown[]) => {
      const arg = c[0] as any;
      return (
        arg?.script &&
        Array.isArray(arg.script) &&
        String(arg.script[0]?.src).includes('pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=')
      );
    });
    expect(found).toBeTruthy();
  });
});
