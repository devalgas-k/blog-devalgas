import { type ComputedRef, computed, defineComponent, inject, onMounted, type Ref, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';

import type LoginService from '@/account/login.service';
import InputGroup from 'primevue/inputgroup';
import AutoComplete from 'primevue/autocomplete';
import Avatar from 'primevue/avatar';
import { FilterMatchMode, FilterService } from 'primevue/api';
import type { ICategoryArticle } from '@/shared/model/category-article.model';
import CategoryArticleService from '@/entities/category-article/v1/category-article-v1.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useAlertService } from '@/shared/alert/alert.service';
import Subtitle from '@/core/subtitle/subtitle.vue';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ArticleSearchV1',
  components: {
    'p-auto-complete': AutoComplete,
    'p-avatar': Avatar,
    subtitle: Subtitle,
    InputGroup,
  },
  setup: function () {
    const loginService = inject<LoginService>('loginService');
    const dataUtils = useDataUtils();
    const categoryArticleService = inject('categoryArticleService', () => new CategoryArticleService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const defaultTheme = window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    const currentTheme = inject<ComputedRef<string>>(
      'currentTheme',
      () => computed(() => localStorage.getItem('currentTheme') ?? defaultTheme),
      true,
    );
    const username = inject<ComputedRef<string>>('currentUsername');
    const currentLanguage = inject<ComputedRef<string>>('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);

    const openLogin = () => {
      loginService.openLogin();
    };

    const isFetching = ref(false);
    const itemsPerPage = ref(20);
    const queryCount: Ref<number> = ref(null);
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
        const res = await categoryArticleService().retrieve(paginationQuery);
        totalItems.value = Number(res.headers['x-total-count']);
        queryCount.value = totalItems.value;
        links.value = dataUtils.parseLinks(res.headers?.['link']);
        categoryArticles.value.push(...(res.data ?? []));
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const viewArticle = ref(false);
    const selectedArticle = ref();
    const filteredArticles = ref<any[]>([]);

    const search = (event: any) => {
      const query: string = event?.query ?? '';
      const newFilteredGroups: any[] = [];

      const lang = (currentLanguage.value ?? 'fr').split('-')[0]?.toLowerCase();
      const useFr = lang === 'fr';

      for (const groupedArticle of categoryArticles.value) {
        const normalized = (groupedArticle.articles ?? [])
          .map(article => ({
            ...article,
            label: useFr ? article.labelFr : article.labelEn,
          }))
          .filter(a => a.label && typeof a.label === 'string');

        normalized.sort((a, b) => (a.label ?? '').localeCompare(b.label ?? ''));

        const filteredItems = FilterService.filter(normalized, ['label', 'labelFr', 'labelEn'], query, FilterMatchMode.CONTAINS);

        if (filteredItems && filteredItems.length) {
          newFilteredGroups.push({ ...groupedArticle, items: filteredItems });
        }
      }

      filteredArticles.value = newFilteredGroups;
    };

    onMounted(async () => {
      await retrieveCategoryArticles();
    });

    watch(selectedArticle, value => {
      if (value.label === undefined) {
        viewArticle.value = false;
      } else {
        viewArticle.value = true;
      }
    });

    const backgroundImageSrc = computed(() =>
      currentTheme.value === 'dark' ? '/content/images/abort-d.jpg' : '/content/images/about.jpg',
    );

    return {
      authenticated,
      username,
      openLogin,
      t$: useI18n().t,
      search,
      selectedArticle,
      filteredArticles,
      viewArticle,
      currentTheme,
      currentLanguage,
      backgroundImageSrc,
    };
  },
});
