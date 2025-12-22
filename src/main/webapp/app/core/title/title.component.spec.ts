import { vitest, describe, it, expect } from 'vitest';
import { shallowMount } from '@vue/test-utils';

vitest.mock('vue-i18n', () => ({
  useI18n: () => ({ t: vitest.fn(() => 'Software engineer') }),
}));

import Title from './title.vue';

describe('Title', () => {
  it('affiche le sous-titre via i18n', () => {
    const wrapper = shallowMount(Title);
    const p = wrapper.find('.role p');
    expect(p.exists()).toBe(true);
    expect(p.text()).toBe('Software engineer');
  });
});
