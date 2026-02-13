import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ArticleService from './article.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import CategoryArticleService from '@/entities/category-article/category-article.service';
import { type ICategoryArticle } from '@/shared/model/category-article.model';
import { Article, type IArticle } from '@/shared/model/article.model';
import { Status } from '@/shared/model/enumerations/status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ArticleUpdate',
  setup() {
    const articleService = inject('articleService', () => new ArticleService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const article: Ref<IArticle> = ref(new Article());

    const categoryArticleService = inject('categoryArticleService', () => new CategoryArticleService());

    const categoryArticles: Ref<ICategoryArticle[]> = ref([]);
    const statusValues: Ref<string[]> = ref(Object.keys(Status));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveArticle = async articleId => {
      try {
        const res = await articleService().find(articleId);
        res.date = new Date(res.date);
        article.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.articleId) {
      retrieveArticle(route.params.articleId);
    }

    const initRelationships = () => {
      categoryArticleService()
        .retrieve()
        .then(res => {
          categoryArticles.value = res.data;
        });
    };

    initRelationships();

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      labelEn: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 256 }).toString(), 256),
      },
      labelFr: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 256 }).toString(), 256),
      },
      descriptionFr: {},
      descriptionEn: {},
      markdownFr: {},
      markdownEn: {},
      status: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      date: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      badge: {},
      banner: {},
      views: {},
      stars: {},
      display: {},
      newsletter: {},
      categoryArticles: {},
    };
    const v$ = useVuelidate(validationRules, article as any);
    v$.value.$validate();

    return {
      articleService,
      alertService,
      article,
      previousState,
      statusValues,
      isSaving,
      currentLanguage,
      categoryArticles,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: article }),
      t$,
    };
  },
  created(): void {
    this.article.categoryArticles = [];
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.article.id) {
        this.articleService()
          .update(this.article)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('devalgasApp.article.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.articleService()
          .create(this.article)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('devalgasApp.article.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },

    clearInputImage(field, fieldContentType, idInput): void {
      if (this.article && field && fieldContentType) {
        if (Object.hasOwn(this.article, field)) {
          this.article[field] = null;
        }
        if (Object.hasOwn(this.article, fieldContentType)) {
          this.article[fieldContentType] = null;
        }
        if (idInput) {
          (<any>this).$refs[idInput] = null;
        }
      }
    },

    getSelected(selectedVals, option, pkField = 'id'): any {
      if (selectedVals) {
        return selectedVals.find(value => option[pkField] === value[pkField]) ?? option;
      }
      return option;
    },
  },
});
