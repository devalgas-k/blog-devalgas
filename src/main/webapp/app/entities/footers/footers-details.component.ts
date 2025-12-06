import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import FootersService from './footers.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { type IFooters } from '@/shared/model/footers.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'FootersDetails',
  setup() {
    const footersService = inject('footersService', () => new FootersService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const footers: Ref<IFooters> = ref({});

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

    return {
      alertService,
      footers,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
