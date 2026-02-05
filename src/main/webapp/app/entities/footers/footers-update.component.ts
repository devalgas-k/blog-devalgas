import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import FootersService from './footers.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useAlertService } from '@/shared/alert/alert.service';

import { Footers, type IFooters } from '@/shared/model/footers.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'FootersUpdate',
  setup() {
    const footersService = inject('footersService', () => new FootersService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const footers: Ref<IFooters> = ref(new Footers());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveFooters = async footersId => {
      try {
        const res = await footersService().find(footersId);
        footers.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.footersId) {
      retrieveFooters(route.params.footersId);
    }

    const initRelationships = () => {};

    initRelationships();

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validationRules = {
      logoFooters: {},
      appInfoFooters: {},
    };
    const v$ = useVuelidate(validationRules, footers as any);
    v$.value.$validate();

    return {
      footersService,
      alertService,
      footers,
      previousState,
      isSaving,
      currentLanguage,
      ...dataUtils,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.footers.id) {
        this.footersService()
          .update(this.footers)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('devalgasApp.footers.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.footersService()
          .create(this.footers)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('devalgasApp.footers.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },

    clearInputImage(field, fieldContentType, idInput): void {
      if (this.footers && field && fieldContentType) {
        if (Object.hasOwn(this.footers, field)) {
          this.footers[field] = null;
        }
        if (Object.hasOwn(this.footers, fieldContentType)) {
          this.footers[fieldContentType] = null;
        }
        if (idInput) {
          (<any>this).$refs[idInput] = null;
        }
      }
    },
  },
});
