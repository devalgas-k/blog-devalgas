import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import useDataUtils from '@/shared/data/data-utils.service';
import { type ICategoryArticle } from '@/shared/model/category-article.model';
import { useAlertService } from '@/shared/alert/alert.service';
import CategoryArticleServiceV1 from './category-article-v1.service';

/**
 * Composant V1 d’affichage des détails d’une catégorie d’articles.
 * Récupère la catégorie par identifiant et expose ses données au template.
 */
export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CategoryArticleDetailsV1',
  setup() {
    const categoryArticleService = inject('categoryArticleService', () => new CategoryArticleServiceV1());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const categoryArticle: Ref<ICategoryArticle> = ref({});

    const retrieveCategoryArticle = async (categoryArticleId: number) => {
      try {
        const res = await categoryArticleService().find(categoryArticleId);
        categoryArticle.value = res;
      } catch (error: any) {
        alertService.showHttpError(error?.response);
      }
    };

    if (route.params?.categoryArticleId) {
      retrieveCategoryArticle(Number(route.params.categoryArticleId));
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
