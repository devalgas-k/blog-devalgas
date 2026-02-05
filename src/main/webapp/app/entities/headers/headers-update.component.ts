import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import HeadersService from './headers.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useAlertService } from '@/shared/alert/alert.service';

import { Headers, type IHeaders } from '@/shared/model/headers.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'HeadersUpdate',
  setup() {
    const headersService = inject('headersService', () => new HeadersService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const headers: Ref<IHeaders> = ref(new Headers());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveHeaders = async headersId => {
      try {
        const res = await headersService().find(headersId);
        headers.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.headersId) {
      retrieveHeaders(route.params.headersId);
    }

    const initRelationships = () => {};

    initRelationships();

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validationRules = {
      logoHeaders: {},
      appInfoHeaders: {},
    };
    const v$ = useVuelidate(validationRules, headers as any);
    v$.value.$validate();

    return {
      headersService,
      alertService,
      headers,
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
      if (this.headers.id) {
        this.headersService()
          .update(this.headers)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('devalgasApp.headers.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.headersService()
          .create(this.headers)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('devalgasApp.headers.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },

    clearInputImage(field, fieldContentType, idInput): void {
      if (this.headers && field && fieldContentType) {
        if (Object.hasOwn(this.headers, field)) {
          this.headers[field] = null;
        }
        if (Object.hasOwn(this.headers, fieldContentType)) {
          this.headers[fieldContentType] = null;
        }
        if (idInput) {
          (<any>this).$refs[idInput] = null;
        }
      }
    },
  },
});
