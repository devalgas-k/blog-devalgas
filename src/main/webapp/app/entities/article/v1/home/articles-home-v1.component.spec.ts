import { vitest, describe, it, expect } from 'vitest';
import { shallowMount } from '@vue/test-utils';
import { createTestingPinia } from '@pinia/testing';
import ArticlesHomeV1 from './articles-home-v1.vue';
import { computed, nextTick } from 'vue';

vitest.mock('vue-i18n', () => ({
  useI18n: () => ({ t: vitest.fn(), d: vitest.fn((ts: number) => ts.toString()) }),
}));

describe('ArticlesHomeV1', () => {
  const makeWrapper = () =>
    shallowMount(ArticlesHomeV1, {
      global: {
        stubs: {
          'jhi-item-count': true,
          'b-pagination': true,
          'router-link': true,
          'article-info': true,
          'font-awesome-icon': true,
        },
        plugins: [createTestingPinia({ createSpy: vitest.fn })],
        provide: {
          alertService: { showHttpError: vitest.fn() },
          articleService: () => ({
            retrieve: vitest.fn().mockResolvedValue({ data: [], headers: { 'x-total-count': '0' } }),
          }),
          authenticated: computed(() => true),
          currentLanguage: computed(() => 'fr'),
        },
      },
    });

  it('affiche article-info en mode skeleton pendant le chargement', async () => {
    const wrapper = makeWrapper();
    wrapper.vm.isFetching = true;
    await nextTick();
    const rows = wrapper.findAll('tbody tr');
    expect(rows.length).toBeGreaterThan(0);
    expect(wrapper.find('article-info-stub').exists()).toBe(true);
  });

  it('affiche la table des articles quand les données sont disponibles', async () => {
    const wrapper = makeWrapper();
    wrapper.vm.articles = [
      {
        id: 1,
        labelFr: 'Titre FR',
        labelEn: 'Title EN',
        date: '2025-01-01T00:00:00Z',
        views: 10,
        stars: 2,
      } as any,
    ];
    wrapper.vm.isFetching = false;
    await nextTick();
    expect(wrapper.findAll('tbody tr').length).toBe(1);
    expect(wrapper.find('p-skeleton-stub').exists()).toBe(false);
  });
});
