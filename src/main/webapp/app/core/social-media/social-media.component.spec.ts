import { vitest, describe, it, expect } from 'vitest';
import { shallowMount } from '@vue/test-utils';

vitest.mock('vue-i18n', () => ({
  useI18n: () => ({ t: vitest.fn(() => 'Software engineer') }),
}));

import SocialMedia from './social-media.vue';

describe('SocialMedia', () => {
  it('affiche le sous-titre via i18n', () => {
    const wrapper = shallowMount(SocialMedia);
    const p = wrapper.find('.role p');
    expect(p.exists()).toBe(true);
    expect(p.text()).toBe('Software engineer');
  });
});
