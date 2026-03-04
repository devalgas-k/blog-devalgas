import { type ComputedRef, computed, defineComponent, inject, onMounted, onUnmounted, type Ref, ref, watch, nextTick } from 'vue';
import { useHead } from '@unhead/vue';
import { useI18n } from 'vue-i18n';

import InputGroup from 'primevue/inputgroup';
import AutoComplete from 'primevue/autocomplete';
import Avatar from 'primevue/avatar';
import { FilterMatchMode, FilterService } from 'primevue/api';
import type { ICategoryArticle } from '@/shared/model/category-article.model';
import CategoryArticleService from '@/entities/category-article/v1/category-article-v1.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useAlertService } from '@/shared/alert/alert.service';
import ArticleInfoV1 from '../info/article-info-v1.vue';
import { useRenderGateStore } from '@/shared/config/store/render-gate-store';

/**
 * Composant V1 de recherche d’articles.
 * Précharge les articles groupés par catégories et propose une recherche assistée,
 * avec optimisation par langue et déduplication des résultats.
 */
export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ArticleSearchV1',
  components: {
    'p-auto-complete': AutoComplete,
    'p-avatar': Avatar,
    'p-input-group': InputGroup,
    'article-info-v1': ArticleInfoV1,
  },
  setup: function () {
    const dataUtils = useDataUtils();
    const categoryArticleService = inject('categoryArticleService', () => new CategoryArticleService());
    const alertService = inject('alertService', () => useAlertService(), true);
    const loginService = inject<any>('loginService', undefined);

    const defaultTheme = window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    const currentTheme = inject<ComputedRef<string>>(
      'currentTheme',
      () => computed(() => localStorage.getItem('currentTheme') ?? defaultTheme),
      true,
    );
    const currentLanguage = inject<ComputedRef<string>>('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);

    const isFetching = ref(false);
    const itemsPerPage = ref(100);
    const queryCount: Ref<number> = ref(0);
    const page: Ref<number> = ref(1);
    const totalItems = ref(0);
    const links: Ref<any> = ref({});
    const propOrder = ref('id');
    const reverse = ref(false);

    const categoryArticles: Ref<ICategoryArticle[]> = ref([]);

    const sort = (): Array<any> => {
      const result = [propOrder.value + ',' + (reverse.value ? 'desc' : 'asc')];
      if (propOrder.value !== 'id') {
        result.push('id');
      }
      return result;
    };
    const retrieveCategoryArticles = async () => {
      isFetching.value = true;
      try {
        const paginationQuery = {
          page: page.value - 1,
          size: itemsPerPage.value,
          sort: sort(),
        };
        const res = await categoryArticleService().retrieveHome(paginationQuery);
        totalItems.value = Number(res.headers['x-total-count']);
        queryCount.value = totalItems.value;
        links.value = dataUtils.parseLinks(res.headers?.['link']);
        categoryArticles.value.push(...(res.data ?? []));
      } catch (err: any) {
        alertService.showHttpError(err?.response);
      } finally {
        isFetching.value = false;
      }
    };

    const selectedArticle = ref();
    const filteredArticles = ref<any[]>([]);
    const lastQuery = ref<string>('');

    const search = (event: any) => {
      const query: string = event?.query ?? '';
      lastQuery.value = query;
      const newFilteredGroups: any[] = [];

      const lang = (currentLanguage.value ?? 'fr').split('-')[0]?.toLowerCase();
      const useFr = lang === 'fr';
      const seenKeys = new Set<string>();
      const categoriesByArticleKey = new Map<string, Set<string>>();

      // Precompute categories per article across all groups
      for (const groupedArticle of categoryArticles.value) {
        const groupLabel =
          useFr && (groupedArticle as any).labelFr
            ? (groupedArticle as any).labelFr
            : !useFr && (groupedArticle as any).labelEn
              ? (groupedArticle as any).labelEn
              : groupedArticle.label;
        const normalizedAll = (groupedArticle.articles ?? [])
          .map(article => ({
            ...article,
            label: useFr ? article.labelFr : article.labelEn,
          }))
          .filter(a => a.label && typeof a.label === 'string');
        for (const a of normalizedAll) {
          const id = a.id as number | undefined;
          const key = typeof id === 'number' ? `id:${id}` : `lbl:${(a.label ?? '').toString().trim().toLocaleLowerCase()}`;
          if (!key) continue;
          const set = categoriesByArticleKey.get(key) ?? new Set<string>();
          if (groupLabel) set.add(groupLabel);
          categoriesByArticleKey.set(key, set);
        }
      }

      for (const groupedArticle of categoryArticles.value) {
        const normalized = (groupedArticle.articles ?? [])
          .map(article => ({
            ...article,
            label: useFr ? article.labelFr : article.labelEn,
          }))
          .filter(a => a.label && typeof a.label === 'string');

        normalized.sort((a, b) => (a.label ?? '').localeCompare(b.label ?? ''));

        const filteredItems = FilterService.filter(normalized, ['label'], query, FilterMatchMode.CONTAINS);

        const groupLabel =
          useFr && (groupedArticle as any).labelFr
            ? (groupedArticle as any).labelFr
            : !useFr && (groupedArticle as any).labelEn
              ? (groupedArticle as any).labelEn
              : groupedArticle.label;

        const q = (query ?? '').toLocaleLowerCase();
        const categoryMatches = !!q && (groupLabel ?? '').toLocaleLowerCase().includes(q);

        const itemsSrc = categoryMatches ? normalized : (filteredItems ?? []);
        const filteredUnique = itemsSrc
          .filter(a => {
            const id = a.id as number | undefined;
            const key = typeof id === 'number' ? `id:${id}` : `lbl:${(a.label ?? '').toString().trim().toLocaleLowerCase()}`;
            if (!key) return true;
            if (seenKeys.has(key)) return false;
            seenKeys.add(key);
            return true;
          })
          .map(a => {
            const id = a.id as number | undefined;
            const key = typeof id === 'number' ? `id:${id}` : `lbl:${(a.label ?? '').toString().trim().toLocaleLowerCase()}`;
            const cats = Array.from(categoriesByArticleKey.get(key) ?? []);
            return {
              ...a,
              categoryLabels: cats.join(', '),
              categoryArticles: cats.map(label => ({ label })),
            };
          });

        if (filteredUnique.length) {
          const groupCatsSet = new Set<string>();
          for (const it of filteredUnique) {
            const id = it.id as number | undefined;
            const key = typeof id === 'number' ? `id:${id}` : `lbl:${(it.label ?? '').toString().trim().toLocaleLowerCase()}`;
            const cats = Array.from(categoriesByArticleKey.get(key) ?? []);
            for (const c of cats) groupCatsSet.add(c);
          }
          const groupCategoryLabels = Array.from(groupCatsSet).join(', ');
          newFilteredGroups.push({
            ...groupedArticle,
            label: groupLabel,
            items: filteredUnique,
            groupCategoryLabels,
          });
        }
      }

      filteredArticles.value = newFilteredGroups;
    };

    let gate: any;
    try {
      gate = useRenderGateStore();
    } catch (e) {
      void e;
    }
    onMounted(async () => {
      await retrieveCategoryArticles();
      search({ query: '' });
      if (gate && typeof gate.markDataReady === 'function') {
        gate.markDataReady();
      }
    });

    watch(currentLanguage, () => {
      search({ query: lastQuery.value });
    });

    const viewArticle = computed(() => !!selectedArticle.value?.label);
    const inFocus = ref(false);
    const justSelected = ref(false);
    const onAutoBlur = () => {
      inFocus.value = false;
      if (!justSelected.value) {
        selectedArticle.value = '';
      }
    };
    const onAutoFocus = () => {
      inFocus.value = true;
    };
    const onPanelHide = () => {
      if (!inFocus.value && !justSelected.value) {
        selectedArticle.value = '';
      }
    };
    const onItemSelect = () => {
      justSelected.value = true;
      nextTick(() => {
        justSelected.value = false;
      });
    };
    watch(filteredArticles, newVal => {
      if ((!newVal || newVal.length === 0) && typeof selectedArticle.value === 'string') {
        selectedArticle.value = '';
      }
    });

    const backgroundImageSrc = computed(() =>
      currentTheme.value === 'dark' ? '/content/images/abort-d.jpg' : '/content/images/about.jpg',
    );

    useHead({
      link: [
        {
          rel: 'preload',
          as: 'image',
          href: computed(() => backgroundImageSrc.value),
          media: '(min-width: 576px)',
        },
      ],
    });

    const showBackgroundImage = (() => {
      try {
        const mq = window.matchMedia('(min-width: 576px)');
        const v = ref(mq.matches);
        const handler = (e: MediaQueryListEvent) => {
          v.value = e.matches;
        };
        onMounted(() => {
          try {
            mq.addEventListener('change', handler);
          } catch (e) {
            void e;
          }
        });
        onUnmounted(() => {
          try {
            mq.removeEventListener('change', handler);
          } catch (e) {
            void e;
          }
        });
        return v;
      } catch {
        return ref(true);
      }
    })();

    const imageBasePath = computed(() => (import.meta as any).env?.VITE_IMAGE_BASE_PATH ?? '/content/images');
    const slugify = (s: string) =>
      s
        .toLowerCase()
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .replace(/[^a-z0-9]+/g, '-')
        .replace(/^-+|-+$/g, '')
        .substring(0, 80);
    const slugForArticle = (a: any) => {
      const lang = (currentLanguage.value ?? 'fr').split('-')[0]?.toLowerCase();
      const useFr = lang === 'fr';
      const title = useFr ? a?.labelFr : a?.labelEn;
      return slugify(title ?? '');
    };

    return {
      t$: useI18n().t,
      search,
      selectedArticle,
      filteredArticles,
      viewArticle,
      onAutoFocus,
      onAutoBlur,
      onPanelHide,
      onItemSelect,
      currentTheme,
      currentLanguage,
      backgroundImageSrc,
      showBackgroundImage,
      imageBasePath,
      panelMaxHeight: '300px',
      slugForArticle,
      openLogin: () => {
        if (loginService && typeof loginService.openLogin === 'function') {
          loginService.openLogin();
        }
      },
    };
  },
});
