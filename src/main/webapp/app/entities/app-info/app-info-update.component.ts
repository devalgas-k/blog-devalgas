import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import AppInfoService from './app-info.service';
import { useAlertService } from '@/shared/alert/alert.service';

import HeadersService from '@/entities/headers/headers.service';
import { type IHeaders } from '@/shared/model/headers.model';
import FootersService from '@/entities/footers/footers.service';
import { type IFooters } from '@/shared/model/footers.model';
import { AppInfo, type IAppInfo } from '@/shared/model/app-info.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'AppInfoUpdate',
  setup() {
    const appInfoService = inject('appInfoService', () => new AppInfoService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const appInfo: Ref<IAppInfo> = ref(new AppInfo());

    const headersService = inject('headersService', () => new HeadersService());

    const headers: Ref<IHeaders[]> = ref([]);

    const footersService = inject('footersService', () => new FootersService());

    const footers: Ref<IFooters[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveAppInfo = async appInfoId => {
      try {
        const res = await appInfoService().find(appInfoId);
        appInfo.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.appInfoId) {
      retrieveAppInfo(route.params.appInfoId);
    }

    const initRelationships = () => {
      headersService()
        .retrieve()
        .then(res => {
          headers.value = res.data;
        });
      footersService()
        .retrieve()
        .then(res => {
          footers.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validationRules = {
      keyInfo: {},
      valueInfo: {},
      headers: {},
      footers: {},
    };
    const v$ = useVuelidate(validationRules, appInfo as any);
    v$.value.$validate();

    return {
      appInfoService,
      alertService,
      appInfo,
      previousState,
      isSaving,
      currentLanguage,
      headers,
      footers,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.appInfo.id) {
        this.appInfoService()
          .update(this.appInfo)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('devalgasApp.appInfo.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.appInfoService()
          .create(this.appInfo)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('devalgasApp.appInfo.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
