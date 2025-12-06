import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import CategoryArticleService from './category-article.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import ArticleService from '@/entities/article/article.service';
import { type IArticle } from '@/shared/model/article.model';
import { CategoryArticle, type ICategoryArticle } from '@/shared/model/category-article.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CategoryArticleUpdate',
  setup() {
    const categoryArticleService = inject('categoryArticleService', () => new CategoryArticleService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const categoryArticle: Ref<ICategoryArticle> = ref(new CategoryArticle());

    const articleService = inject('articleService', () => new ArticleService());

    const articles: Ref<IArticle[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

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

    const initRelationships = () => {
      articleService()
        .retrieve()
        .then(res => {
          articles.value = res.data;
        });
    };

    initRelationships();

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      label: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 256 }).toString(), 256),
      },
      code: {
        required: validations.required(t$('entity.validation.required').toString()),
        minLength: validations.minLength(t$('entity.validation.minlength', { min: 2 }).toString(), 2),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 2 }).toString(), 2),
      },
      badge: {},
      descriptionFr: {},
      descriptionEn: {},
      articles: {},
    };
    const v$ = useVuelidate(validationRules, categoryArticle as any);
    v$.value.$validate();

    return {
      categoryArticleService,
      alertService,
      categoryArticle,
      previousState,
      isSaving,
      currentLanguage,
      articles,
      ...dataUtils,
      v$,
      t$,
    };
  },
  created(): void {
    this.categoryArticle.articles = [];
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.categoryArticle.id) {
        this.categoryArticleService()
          .update(this.categoryArticle)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('devalgasApp.categoryArticle.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.categoryArticleService()
          .create(this.categoryArticle)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('devalgasApp.categoryArticle.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },

    clearInputImage(field, fieldContentType, idInput): void {
      if (this.categoryArticle && field && fieldContentType) {
        if (Object.hasOwn(this.categoryArticle, field)) {
          this.categoryArticle[field] = null;
        }
        if (Object.hasOwn(this.categoryArticle, fieldContentType)) {
          this.categoryArticle[fieldContentType] = null;
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
