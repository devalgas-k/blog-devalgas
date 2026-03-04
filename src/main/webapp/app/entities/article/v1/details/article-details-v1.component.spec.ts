import { vitest, describe, it, expect } from 'vitest';
import { shallowMount } from '@vue/test-utils';
import { computed } from 'vue';
import flushPromises from 'flush-promises';
vitest.mock('vue-router', () => ({
  useRouter: () => ({ go: vitest.fn(), currentRoute: { value: { path: '/' } } }),
  useRoute: () => ({ params: { articleId: 1 } }),
}));

import ArticleDetails from './article-details-v1.vue';

const toBase64 = (text: string) => btoa(unescape(encodeURIComponent(text)));

describe('ArticleDetails V1', () => {
  const mkFr = toBase64('# Titre FR');
  const mkEn = toBase64('# Title EN');
  const article = {
    id: 1,
    labelFr: 'Titre FR',
    labelEn: 'Title EN',
    markdownFr: mkFr,
    markdownFrContentType: 'text/markdown',
    markdownEn: mkEn,
    markdownEnContentType: 'text/markdown',
  };

  const commonStubs = {
    'p-panel': true,
    'p-splitter': true,
    'p-splitter-panel': true,
    'p-skeleton': true,
    ScrollTop: true,
    adsense: true,
    'article-info-v1': true,
    'router-link': true,
  };

  it('affiche markdownFr quand la langue est fr', async () => {
    const wrapper = shallowMount(ArticleDetails, {
      global: {
        stubs: commonStubs,
        provide: {
          alertService: { showHttpError: vitest.fn() },
          articleService: () => ({ find: vitest.fn().mockResolvedValue(article) }),
          currentLanguage: computed(() => 'fr'),
        },
      },
    });
    await flushPromises();
    const comp = wrapper.vm as any;
    expect(comp.decodedMarkdownContent.html).toContain('Titre FR');
  });

  it('affiche markdownEn quand la langue est en', async () => {
    const wrapper = shallowMount(ArticleDetails, {
      global: {
        stubs: commonStubs,
        provide: {
          alertService: { showHttpError: vitest.fn() },
          articleService: () => ({ find: vitest.fn().mockResolvedValue(article) }),
          currentLanguage: computed(() => 'en'),
        },
      },
    });
    await flushPromises();
    const comp = wrapper.vm as any;
    expect(comp.decodedMarkdownContent.html).toContain('Title EN');
  });

  it('génère une description fallback depuis le markdown quand absente', async () => {
    const wrapper = shallowMount(ArticleDetails, {
      global: {
        stubs: commonStubs,
        provide: {
          alertService: { showHttpError: vitest.fn() },
          articleService: () => ({ find: vitest.fn().mockResolvedValue({ ...article, descriptionFr: '', descriptionEn: '' }) }) as any,
          currentLanguage: computed(() => 'fr'),
        },
      },
    });
    await flushPromises();
    const comp = wrapper.vm as any;
    expect(String(comp.description)).toContain('Titre FR');
  });
});
