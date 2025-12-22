import { describe, it, expect } from 'vitest';
import { shallowMount } from '@vue/test-utils';
import Subtitle from './subtitle.vue';

describe('Subtitle', () => {
  it('affiche le texte de la prop title', () => {
    const wrapper = shallowMount(Subtitle, {
      props: { title: 'Hello' },
    });
    const p = wrapper.find('.content__container__text');
    expect(p.exists()).toBe(true);
    expect(p.text()).toBe('Hello');
  });
});
