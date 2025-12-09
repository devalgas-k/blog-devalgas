import { vitest } from 'vitest';
import { shallowMount } from '@vue/test-utils';
import { createTestingPinia } from '@pinia/testing';
import { computed } from 'vue';
vitest.mock('vue-router', () => ({
  useRouter: () => ({ currentRoute: { value: { path: '/' } } }),
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
});
