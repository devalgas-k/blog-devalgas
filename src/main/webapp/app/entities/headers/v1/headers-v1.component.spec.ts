import { describe, it, expect, vitest } from 'vitest';
import { shallowMount } from '@vue/test-utils';
import { createTestingPinia } from '@pinia/testing';
import { computed } from 'vue';
vitest.mock('vue-router', () => ({
  useRouter: () => ({ currentRoute: { value: { path: '/' } } }),
}));
vitest.mock('vue-i18n', () => ({
  useI18n: () => ({
    t: (msg: string) => {
      if (msg === 'globalV1.separator.or') {
        const doc: any = (globalThis as any).document;
        const lang = (doc?.documentElement?.getAttribute('lang') ?? '').toString();
        const base = lang.split(/[-_]/)[0]?.toLowerCase();
        return base === 'fr' ? 'OU' : 'OR';
      }
      return msg;
    },
  }),
}));

import Headers from './headers-v1.vue';

describe('Headers V1', () => {
  const mountOptions = {
    stubs: {
      'font-awesome-icon': true,
      'b-nav-item': true,
      'b-nav-item-dropdown': true,
      'b-dropdown-item': true,
      'b-navbar': true,
      'b-navbar-brand': true,
      'b-navbar-nav': true,
      'b-navbar-toggle': true,
      'b-collapse': true,
      'router-link': true,
    },
    provide: {
      accountService: { hasAnyAuthorityAndCheckAuth: vitest.fn().mockResolvedValue(true) },
      currentLanguage: computed(() => 'fr'),
      changeLanguage: vitest.fn(),
      currentTheme: computed(() => 'dark'),
      changeTheme: vitest.fn(),
      headersService: () => ({ retrieve: vitest.fn() }),
    },
    plugins: [createTestingPinia()],
  };

  it('expose themes Solar and Slate', () => {
    const wrapper = shallowMount(Headers, { global: mountOptions });
    const comp = wrapper.vm as any;
    expect(comp.themes.light.name).toBe('Solar');
    expect(comp.themes.dark.name).toBe('Slate');
  });

  it('isActiveTheme reflects currentTheme', () => {
    const wrapper = shallowMount(Headers, { global: mountOptions });
    const comp = wrapper.vm as any;
    expect(comp.isActiveTheme('dark')).toBe(true);
    expect(comp.isActiveTheme('light')).toBe(false);
  });

  it('changeTheme is callable', async () => {
    const changeTheme = vitest.fn();
    const wrapper = shallowMount(Headers, {
      global: {
        ...mountOptions,
        provide: { ...mountOptions.provide, changeTheme },
      },
    });
    const comp = wrapper.vm as any;
    await comp.changeTheme('light');
    expect(changeTheme).toHaveBeenCalledWith('light');
  });

  it('isActiveLanguage reflète currentLanguage', () => {
    const wrapper = shallowMount(Headers, { global: mountOptions });
    const comp = wrapper.vm as any;
    expect(comp.isActiveLanguage('fr')).toBe(true);
    expect(comp.isActiveLanguage('en')).toBe(false);
  });

  it('changeLanguage est appelable', async () => {
    const changeLanguage = vitest.fn();
    const wrapper = shallowMount(Headers, {
      global: {
        ...mountOptions,
        provide: { ...mountOptions.provide, changeLanguage },
      },
    });
    const comp = wrapper.vm as any;
    await comp.changeLanguage('en');
    expect(changeLanguage).toHaveBeenCalledWith('en');
  });

  it('ferme le dropdown après saved', () => {
    const wrapper = shallowMount(Headers, { global: mountOptions });
    const hideSpy = vitest.fn();
    const onContactSaved = (wrapper.vm as any).$options.methods.onContactSaved;
    onContactSaved.call({ $refs: { contactDropdown: { hide: hideSpy } } });
    expect(hideSpy).toHaveBeenCalledWith(true);
  });

  it('separatorLabel vaut OU en fr et OR sinon', () => {
    (globalThis as any).document.documentElement.setAttribute('lang', 'fr');
    const wrapperFr = shallowMount(Headers, {
      global: {
        ...mountOptions,
        provide: { ...mountOptions.provide, currentLanguage: computed(() => 'fr') },
      },
    });
    const compFr = wrapperFr.vm as any;
    expect(compFr.separatorLabel).toBe('OU');

    (globalThis as any).document.documentElement.setAttribute('lang', 'en');
    const wrapperEn = shallowMount(Headers, {
      global: {
        ...mountOptions,
        provide: { ...mountOptions.provide, currentLanguage: computed(() => 'en') },
      },
    });
    const compEn = wrapperEn.vm as any;
    expect(compEn.separatorLabel).toBe('OR');

    (globalThis as any).document.documentElement.setAttribute('lang', 'fr-FR');
    const wrapperFrFr = shallowMount(Headers, {
      global: {
        ...mountOptions,
        provide: { ...mountOptions.provide, currentLanguage: computed(() => 'fr-FR') },
      },
    });
    const compFrFr = wrapperFrFr.vm as any;
    expect(compFrFr.separatorLabel).toBe('OU');

    (globalThis as any).document.documentElement.setAttribute('lang', 'fr_FR');
    const wrapperFr_FR = shallowMount(Headers, {
      global: {
        ...mountOptions,
        provide: { ...mountOptions.provide, currentLanguage: computed(() => 'fr_FR') },
      },
    });
    const compFr_FR = wrapperFr_FR.vm as any;
    expect(compFr_FR.separatorLabel).toBe('OU');

    (globalThis as any).document.documentElement.setAttribute('lang', 'en-US');
    const wrapperEnUS = shallowMount(Headers, {
      global: {
        ...mountOptions,
        provide: { ...mountOptions.provide, currentLanguage: computed(() => 'en-US') },
      },
    });
    const compEnUS = wrapperEnUS.vm as any;
    expect(compEnUS.separatorLabel).toBe('OR');
  });
});
