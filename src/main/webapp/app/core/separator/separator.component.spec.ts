import { describe, it, expect } from 'vitest';
import { shallowMount } from '@vue/test-utils';
import Separator from './separator.vue';

describe('Separator', () => {
  it("rend l'icône passée en prop", () => {
    const wrapper = shallowMount(Separator, {
      props: { icon: 'home' },
      global: { stubs: { 'font-awesome-icon': true } },
    });
    expect(wrapper.find('.or-spacer').exists()).toBe(true);
    const faEl = wrapper.find('font-awesome-icon-stub');
    expect(faEl.exists()).toBe(true);
    expect(faEl.attributes('icon')).toBe('home');
  });
});
