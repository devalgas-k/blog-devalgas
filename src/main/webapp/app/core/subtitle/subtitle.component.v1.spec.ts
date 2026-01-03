import { describe, it, expect } from 'vitest';
import { shallowMount } from '@vue/test-utils';
import Subtitle from './subtitle.vue';

describe('Subtitle v1', () => {
  it('affiche le texte de la prop title', () => {
    const wrapper = shallowMount(Subtitle, {
      props: { title: 'Hello' },
    });
    const p = wrapper.find('.content__container__text');
    expect(p.exists()).toBe(true);
    expect(p.text()).toBe('Hello');
  });

  it('met à jour le texte quand la prop title change', async () => {
    const wrapper = shallowMount(Subtitle, {
      props: { title: 'Hello' },
    });
    expect(wrapper.find('.content__container__text').text()).toBe('Hello');
    await wrapper.setProps({ title: 'World' });
    expect(wrapper.find('.content__container__text').text()).toBe('World');
  });
});
