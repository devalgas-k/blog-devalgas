import { type Ref, computed, defineAsyncComponent, defineComponent, inject, onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';

import { type IArticle } from '@/shared/model/article.model';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';
import ArticleServiceV1 from '../article.service-v1';

import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
const ArticleInfo = defineAsyncComponent(() => import('@/entities/article/v1/info/article-info-v1.vue'));
import Skeleton from 'primevue/skeleton';

/**
 * Composant V1 de page d’accueil des articles.
 * Liste paginée des articles avec projection summary, tri et skeleton lors du chargement.
 */
export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Article',
  components: {
    FontAwesomeIcon,
    'article-info': ArticleInfo,
    'p-skeleton': Skeleton,
  },
  setup() {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const dataUtils = useDataUtils();
    const articleService = inject('articleService', () => new ArticleServiceV1());
    const alertService = inject('alertService', () => useAlertService(), true);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);
    const langBase = computed(() => (currentLanguage?.value ?? 'fr').toString().split('-')[0].toLowerCase());

    const itemsPerPage = ref(10);
    const queryCount: Ref<number | null> = ref(null);
    const page: Ref<number> = ref(1);
    const propOrder = ref('id');
    const reverse = ref(false);
    const totalItems = ref(0);

    const articles: Ref<IArticle[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      page.value = 1;
    };

    const sort = (): Array<any> => {
      const result = [`${propOrder.value},${reverse.value ? 'desc' : 'asc'}`];
      if (propOrder.value !== 'id') {
        result.push('id');
      }
      return result;
    };

    const retrieveArticles = async () => {
      isFetching.value = true;
      try {
        const paginationQuery = {
          page: page.value - 1,
          size: itemsPerPage.value,
          sort: sort(),
        };
        const res = await articleService().retrieve(paginationQuery);
        totalItems.value = Number(res.headers['x-total-count']);
        queryCount.value = totalItems.value;
        articles.value = res.data;
      } catch (err: any) {
        alertService.showHttpError(err?.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveArticles();
    };

    onMounted(async () => {
      await retrieveArticles();
    });

    const removeId: Ref<number | null> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IArticle) => {
      removeId.value = instance.id ?? null;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };

    const changeOrder = (newOrder: string) => {
      if (propOrder.value === newOrder) {
        reverse.value = !reverse.value;
      } else {
        reverse.value = false;
      }
      propOrder.value = newOrder;
    };

    // Whenever order changes, reset the pagination
    watch([propOrder, reverse], async () => {
      if (page.value === 1) {
        // first page, retrieve new data
        await retrieveArticles();
      } else {
        // reset the pagination
        clear();
      }
    });

    // Whenever page changes, switch to the new page.
    watch(page, async () => {
      await retrieveArticles();
    });

    const filter = null;
    const slugify = (s: string) =>
      s
        .toLowerCase()
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .replace(/[^a-z0-9]+/g, '-')
        .replace(/^-+|-+$/g, '')
        .substring(0, 80);
    const slugForArticle = (a: IArticle) => {
      const isFr = langBase.value === 'fr';
      const title = (isFr ? (a as any)?.labelFr : (a as any)?.labelEn) ?? '';
      return slugify(title);
    };

    return {
      articles,
      handleSyncList,
      isFetching,
      retrieveArticles,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      itemsPerPage,
      queryCount,
      page,
      propOrder,
      reverse,
      totalItems,
      changeOrder,
      filter,
      t$,
      ...dataUtils,
      slugForArticle,
    };
  },
});
