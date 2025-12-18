import { vitest, describe, it, expect } from 'vitest';
import { shallowMount } from '@vue/test-utils';
import { computed } from 'vue';
import ArticleInfoV1 from './article-info-v1.vue';
import { useDateFormat } from '@/shared/composables';

vitest.mock('vue-i18n', () => ({
  useI18n: () => ({ t: vitest.fn() }),
}));

describe('ArticleInfoV1', () => {
  const dateFormat = useDateFormat();

  it('affiche le titre en français et les tags', async () => {
    const article = {
      labelFr: 'Titre FR',
      labelEn: 'Title EN',
      date: '2025-01-01T00:00:00Z',
      categoryArticles: [
        { id: 1, label: 'JS' },
        { id: 2, label: 'Vue' },
      ],
    } as any;
    const wrapper = shallowMount(ArticleInfoV1, {
      props: { article },
      global: {
        stubs: { 'font-awesome-icon': true },
        provide: {
          loginService: { openLogin: vitest.fn() },
          authenticated: computed(() => true),
          currentLanguage: computed(() => 'fr'),
        },
      },
    });
    await wrapper.vm.$nextTick();
    const title = wrapper.find('.article-info__title').text();
    const tags = wrapper.findAll('.article-info__tags a');
    const value = wrapper.find('.article-info__value').text();
    expect(title).toBe('Titre FR');
    expect(tags.length).toBe(2);
    expect(value).toBe(dateFormat.formatDate(article.date));
  });

  it('affiche le titre en anglais quand la langue est en', async () => {
    const article = {
      labelFr: 'Titre FR',
      labelEn: 'Title EN',
      date: '2025-01-01T00:00:00Z',
    } as any;
    const wrapper = shallowMount(ArticleInfoV1, {
      props: { article },
      global: {
        stubs: { 'font-awesome-icon': true },
        provide: {
          loginService: { openLogin: vitest.fn() },
          authenticated: computed(() => true),
          currentLanguage: computed(() => 'en'),
        },
      },
    });
    await wrapper.vm.$nextTick();
    const title = wrapper.find('.article-info__title').text();
    expect(title).toBe('Title EN');
  });

  it('n’affiche pas la date si absente', async () => {
    const article = {
      labelFr: 'Sans date',
      labelEn: 'No date',
    } as any;
    const wrapper = shallowMount(ArticleInfoV1, {
      props: { article },
      global: {
        stubs: { 'font-awesome-icon': true },
        provide: {
          loginService: { openLogin: vitest.fn() },
          authenticated: computed(() => true),
          currentLanguage: computed(() => 'fr'),
        },
      },
    });
    await wrapper.vm.$nextTick();
    const values = wrapper.findAll('.article-info__row:not(.d-none) .article-info__value');
    expect(values.length).toBe(0);
  });
});
