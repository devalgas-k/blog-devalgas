import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import CategoryArticleService from './category-article.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { type ICategoryArticle } from '@/shared/model/category-article.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CategoryArticleDetails',
  setup() {
    const categoryArticleService = inject('categoryArticleService', () => new CategoryArticleService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const categoryArticle: Ref<ICategoryArticle> = ref({});

    const retrieveCategoryArticle = async categoryArticleId => {
      try {
        const res = await categoryArticleService().find(categoryArticleId);
        categoryArticle.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.categoryArticleId) {
      retrieveCategoryArticle(route.params.categoryArticleId);
    }

    return {
      alertService,
      categoryArticle,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
