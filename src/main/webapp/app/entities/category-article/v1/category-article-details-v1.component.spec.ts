import { describe, it, expect, vi } from 'vitest';

vi.mock('vue-router', () => ({
  useRoute: () => ({ params: { categoryArticleId: 123 } }),
  useRouter: () => ({ go: vi.fn() }),
}));

vi.mock('vue-i18n', () => ({
  useI18n: () => ({ t: (k: string) => k }),
}));

vi.mock('vue', async () => {
  const actual = await vi.importActual<any>('vue');
  return {
    ...actual,
    inject: (key: string) => {
      if (key === 'categoryArticleService') {
        return () => ({
          find: async (id: number) => Promise.resolve({ id, label: 'Test' }),
        });
      }
      if (key === 'alertService') {
        return () => ({
          showHttpError: vi.fn(),
        });
      }
      return actual.inject(key);
    },
  };
});

describe('CategoryArticleDetailsV1 component', () => {
  it('charge la catégorie et expose les données', async () => {
    const Component = (await import('./category-article-details-v1.component')).default;
    const out = Component.setup?.({}, {} as any) as any;
    // attente de la promesse find
    await new Promise(r => setTimeout(r, 0));
    expect(out.categoryArticle.value.id).toBe(123);
    expect(out.t$('devalgasApp.categoryArticle.detail.title')).toBe('devalgasApp.categoryArticle.detail.title');
  });
});
