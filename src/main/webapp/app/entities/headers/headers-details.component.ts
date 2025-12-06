import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import HeadersService from './headers.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { type IHeaders } from '@/shared/model/headers.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'HeadersDetails',
  setup() {
    const headersService = inject('headersService', () => new HeadersService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const headers: Ref<IHeaders> = ref({});

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

    return {
      alertService,
      headers,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
