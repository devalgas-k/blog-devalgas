import { computed, defineAsyncComponent, defineComponent, inject, ref, type Ref, type PropType } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import useDataUtils from '@/shared/data/data-utils.service.ts';
import { type IArticle } from '@/shared/model/article.model.ts';
import { useAlertService } from '@/shared/alert/alert.service.ts';
import { useDateFormat } from '@/shared/composables';

import Panel from 'primevue/panel';
import Splitter from 'primevue/splitter';
import SplitterPanel from 'primevue/splitterpanel';
import Skeleton from 'primevue/skeleton';
import ArticleServiceV1 from '@/entities/article/v1/article.service-v1.ts';
const ArticleInfoV1 = defineAsyncComponent(() => import('../info/article-info-v1.vue'));
const Adsense = defineAsyncComponent(() => import('@/core/adsense/adsense.vue'));

/**
 * Composant V1 d’affichage des détails d’un article.
 * Récupère l’article par identifiant, affiche le contenu Markdown localisé,
 * et intègre les blocs d’information et de publicité.
 */
export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ArticleDetailsV1',
  components: {
    'p-panel': Panel,
    'p-splitter': Splitter,
    'p-splitter-panel': SplitterPanel,
    'p-skeleton': Skeleton,
    'article-info-v1': ArticleInfoV1,
    adsense: Adsense,
  },
  props: {
    articleId: { type: Number, required: false, default: undefined },
    initialArticle: { type: Object as PropType<IArticle | null>, required: false, default: null },
  },
  setup(props) {
    const dateFormat = useDateFormat();
    const articleService = inject('articleService', () => new ArticleServiceV1());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const article: Ref<IArticle> = ref(props.initialArticle ?? {});

    const retrieveArticle = async (articleId: number) => {
      try {
        const res = await articleService().find(articleId);
        article.value = res;
      } catch (error: any) {
        alertService.showHttpError(error?.response);
      }
    };

    if (route.params?.articleId) {
      retrieveArticle(Number(route.params.articleId));
    }
    const decodedMarkdownContent = computed(() => {
      const isFr = currentLanguage.value === 'fr';
      const base64 = isFr ? (article.value.markdownFr ?? '') : (article.value.markdownEn ?? '');
      const contentType = isFr ? (article.value.markdownFrContentType ?? '') : (article.value.markdownEnContentType ?? '');
      return dataUtils.decodeMarkdownContent(base64, contentType);
    });

    return {
      ...dateFormat,
      alertService,
      article,
      ...dataUtils,

      previousState,
      t$: useI18n().t,
      currentLanguage,
      decodedMarkdownContent,
      adsenseClient: ADSENSE_CLIENT,
      adsenseSlot: ADSENSE_SLOT,
    };
  },
});
