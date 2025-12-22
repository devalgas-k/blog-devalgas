import { vitest, describe, it, expect, beforeEach } from 'vitest';
import { shallowMount } from '@vue/test-utils';
import { computed, nextTick } from 'vue';

vitest.mock('vue-i18n', () => ({
  useI18n: () => ({ t: vitest.fn(msg => msg) }),
}));

import ArticleSearchV1 from './article-search-v1.vue';

describe('ArticleSearchV1', () => {
  const mockLoginService = { openLogin: vitest.fn() };
  const mockAlertService = { showHttpError: vitest.fn() };
  const mockRetrieve = vitest.fn();

  const categoryGroups = [
    {
      id: 10,
      labelFr: 'Catégorie A',
      labelEn: 'Category A',
      badge: 'ZmFrZQ==',
      badgeContentType: 'image/png',
      articles: [
        { id: 1, labelFr: 'Développeur', labelEn: 'Developer' },
        { id: 2, labelFr: 'Designer', labelEn: 'Designer' },
      ],
    },
  ];

  const mountOptions = (lang = 'en-US', theme = 'dark') => ({
    global: {
      stubs: {
        'p-auto-complete': true,
        'p-input-group': true,
        'p-avatar': true,
        'router-link': true,
      },
      provide: {
        loginService: mockLoginService,
        alertService: () => mockAlertService,
        categoryArticleService: () => ({ retrieve: mockRetrieve }),
        authenticated: computed(() => true),
        currentUsername: computed(() => 'john'),
        currentLanguage: computed(() => lang),
        currentTheme: computed(() => theme),
      },
    },
  });

  beforeEach(() => {
    mockLoginService.openLogin.mockReset();
    mockAlertService.showHttpError.mockReset();
    mockRetrieve.mockReset();
    Object.defineProperty(globalThis as any, 'matchMedia', {
      writable: true,
      value: vitest.fn(query => ({ matches: query.includes('dark') })),
    });
  });

  it('charge les catégories et filtre en anglais', async () => {
    mockRetrieve.mockResolvedValue({
      headers: { 'x-total-count': '2', link: '' },
      data: categoryGroups,
    });
    const wrapper = shallowMount(ArticleSearchV1, mountOptions('en-US', 'dark'));
    await nextTick();
    const vm = wrapper.vm as any;
    vm.search({ query: 'Dev' });
    expect(vm.filteredArticles.length).toBe(1);
    expect(vm.filteredArticles[0].items.some((i: any) => i.label === 'Developer')).toBe(true);
  });

  it('filtre en français selon currentLanguage', async () => {
    mockRetrieve.mockResolvedValue({
      headers: { 'x-total-count': '2', link: '' },
      data: categoryGroups,
    });
    const wrapper = shallowMount(ArticleSearchV1, mountOptions('fr-FR', 'light'));
    await nextTick();
    const vm = wrapper.vm as any;
    vm.search({ query: 'Des' });
    expect(vm.filteredArticles.length).toBe(1);
    expect(vm.filteredArticles[0].items.some((i: any) => i.label === 'Designer')).toBe(true);
  });

  it('bascule viewArticle selon selectedArticle', async () => {
    mockRetrieve.mockResolvedValue({
      headers: { 'x-total-count': '2', link: '' },
      data: categoryGroups,
    });
    const wrapper = shallowMount(ArticleSearchV1, mountOptions());
    await nextTick();
    const vm = wrapper.vm as any;
    vm.selectedArticle = {};
    await nextTick();
    expect(vm.viewArticle).toBe(false);
    vm.selectedArticle = { label: 'Developer' };
    await nextTick();
    expect(vm.viewArticle).toBe(true);
  });

  it("openLogin appelle le service d'authentification", async () => {
    mockRetrieve.mockResolvedValue({
      headers: { 'x-total-count': '2', link: '' },
      data: categoryGroups,
    });
    const wrapper = shallowMount(ArticleSearchV1, mountOptions());
    await nextTick();
    const vm = wrapper.vm as any;
    vm.openLogin();
    expect(mockLoginService.openLogin).toHaveBeenCalledTimes(1);
  });

  it('backgroundImageSrc reflète currentTheme', async () => {
    mockRetrieve.mockResolvedValue({
      headers: { 'x-total-count': '2', link: '' },
      data: categoryGroups,
    });
    const wDark = shallowMount(ArticleSearchV1, mountOptions('en-US', 'dark'));
    await nextTick();
    const vmDark = wDark.vm as any;
    expect(vmDark.backgroundImageSrc).toBe('/content/images/abort-d.jpg');

    const wLight = shallowMount(ArticleSearchV1, mountOptions('en-US', 'light'));
    await nextTick();
    const vmLight = wLight.vm as any;
    expect(vmLight.backgroundImageSrc).toBe('/content/images/about.jpg');
  });
});
