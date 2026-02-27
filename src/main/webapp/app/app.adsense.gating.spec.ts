import { describe, it, expect, vi } from 'vitest';
import { mount } from '@vue/test-utils';
import { computed } from 'vue';

vi.mock('@/shared/alert/alert.service', () => ({
  useAlertService: () => ({
    showHttpError: () => {},
  }),
}));
vi.mock('pinia', () => ({
  storeToRefs: (obj: any) => obj,
  defineStore: () => () => ({ loginModalOpen: { value: false } }),
}));

describe('App.vue — gating Adsense', () => {
  const baseMount = async (consent = false, scriptReady = false) => {
    vi.stubGlobal('ADSENSE_ENABLED', true);
    vi.stubGlobal('ADSENSE_SLOT_TOP', '0000000001');
    vi.stubGlobal('ADSENSE_SLOT_SIDEBAR_LEFT', '0000000002');
    vi.stubGlobal('ADSENSE_SLOT_SIDEBAR_RIGHT', '0000000003');
    vi.stubGlobal('ADSENSE_SLOT_FOOTER', '0000000004');
    const App = (await import('./app.vue')).default;
    return mount(App, {
      global: {
        stubs: {
          ribbon: true,
          'jhi-navbar': true,
          'login-form': true,
          'jhi-footer': true,
          'headers-v1': true,
          'footers-v1': true,
          'b-modal': true,
          ScrollTop: true,
          'router-view': {
            template: '<div></div>',
          },
        },
        provide: {
          consentGiven: computed(() => consent),
          adsenseScriptReady: computed(() => scriptReady),
        },
      },
    });
  };

  it('ne rend aucune annonce si consentement absent', () => {
    const wrapperPromise = baseMount(false, true);
    return wrapperPromise.then(wrapper => {
      expect(wrapper.findAll('ins.adsbygoogle').length).toBe(0);
    });
  });

  it('ne rend aucune annonce si script indisponible', () => {
    const wrapperPromise = baseMount(true, false);
    return wrapperPromise.then(wrapper => {
      expect(wrapper.findAll('ins.adsbygoogle').length).toBe(0);
    });
  });
});
