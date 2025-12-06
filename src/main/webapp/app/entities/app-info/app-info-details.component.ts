import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import AppInfoService from './app-info.service';
import { type IAppInfo } from '@/shared/model/app-info.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'AppInfoDetails',
  setup() {
    const appInfoService = inject('appInfoService', () => new AppInfoService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const appInfo: Ref<IAppInfo> = ref({});

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

    return {
      alertService,
      appInfo,

      previousState,
      t$: useI18n().t,
    };
  },
});
