import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import ArticleService from './article.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { type IArticle } from '@/shared/model/article.model';
import { useAlertService } from '@/shared/alert/alert.service';
import { useRenderGateStore } from '@/shared/config/store/render-gate-store';
import AppLoader from '@/core/loader/app-loader.vue';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ArticleDetails',
  components: {
    'app-loader': AppLoader,
  },
  setup() {
    const dateFormat = useDateFormat();
    const articleService = inject('articleService', () => new ArticleService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const article: Ref<IArticle> = ref({});
    let gate: any;
    try {
      gate = useRenderGateStore();
    } catch {}

    const retrieveArticle = async (articleId: number) => {
      try {
        const res = await articleService().find(articleId);
        article.value = res;
        if (gate && typeof gate.markDataReady === 'function') {
          gate.markDataReady();
        }
      } catch (error: any) {
        alertService.showHttpError(error?.response);
      }
    };

    if (route.params?.articleId) {
      const routeId = Array.isArray(route.params.articleId) ? route.params.articleId[0] : route.params.articleId;
      const idNum = Number(routeId);
      if (!Number.isNaN(idNum) && idNum > 0) {
        retrieveArticle(idNum);
      }
    }

    return {
      ...dateFormat,
      alertService,
      article,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
