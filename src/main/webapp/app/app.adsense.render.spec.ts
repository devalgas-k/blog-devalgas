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

describe('App.vue — rendu des 4 annonces avec slots valides', () => {
  it('rend 4 <ins.adsbygoogle> quand gating et slots valides', async () => {
    vi.stubGlobal('ADSENSE_ENABLED', true);
    vi.stubGlobal('ADSENSE_CLIENT', 'ca-pub-1234567890');
    vi.stubGlobal('ADSENSE_SLOT_TOP', '1111111111');
    vi.stubGlobal('ADSENSE_SLOT_SIDEBAR_LEFT', '2111111111');
    vi.stubGlobal('ADSENSE_SLOT_SIDEBAR_RIGHT', '3111111111');
    vi.stubGlobal('ADSENSE_SLOT_FOOTER', '4111111111');
    const App = (await import('./app.vue')).default;
    const wrapper = mount(App, {
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
          'router-view': { template: '<div></div>' },
        },
        provide: {
          consentGiven: computed(() => true),
          adsenseScriptReady: computed(() => true),
        },
      },
    });
    expect(wrapper.findAll('ins.adsbygoogle').length).toBe(4);
  });
});
