import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import SubscribeService from './subscribe.service';
import { useDateFormat } from '@/shared/composables';
import { type ISubscribe } from '@/shared/model/subscribe.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'SubscribeDetails',
  setup() {
    const dateFormat = useDateFormat();
    const subscribeService = inject('subscribeService', () => new SubscribeService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const subscribe: Ref<ISubscribe> = ref({});

    const retrieveSubscribe = async subscribeId => {
      try {
        const res = await subscribeService().find(subscribeId);
        subscribe.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.subscribeId) {
      retrieveSubscribe(route.params.subscribeId);
    }

    return {
      ...dateFormat,
      alertService,
      subscribe,

      previousState,
      t$: useI18n().t,
    };
  },
});
