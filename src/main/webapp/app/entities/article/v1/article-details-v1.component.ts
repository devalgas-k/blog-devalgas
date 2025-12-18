import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import useDataUtils from '@/shared/data/data-utils.service';
import { type IArticle } from '@/shared/model/article.model';
import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat } from '@/shared/composables';

import Panel from 'primevue/panel';
import Splitter from 'primevue/splitter';
import SplitterPanel from 'primevue/splitterpanel';
import Skeleton from 'primevue/skeleton';
import ArticleServiceV1 from '@/entities/article/v1/article.service-v1';
import ArticleInfoV1 from './info/article-info-v1.vue';
import Adsense from '@/core/adsense/adsense.vue';

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
  setup() {
    const dateFormat = useDateFormat();
    const articleService = inject('articleService', () => new ArticleServiceV1());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const article: Ref<IArticle> = ref({});

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
    };
  },
});
