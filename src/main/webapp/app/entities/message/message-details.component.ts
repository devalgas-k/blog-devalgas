import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import MessageService from './message.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { type IMessage } from '@/shared/model/message.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MessageDetails',
  setup() {
    const dateFormat = useDateFormat();
    const messageService = inject('messageService', () => new MessageService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const message: Ref<IMessage> = ref({});

    const retrieveMessage = async messageId => {
      try {
        const res = await messageService().find(messageId);
        message.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.messageId) {
      retrieveMessage(route.params.messageId);
    }

    return {
      ...dateFormat,
      alertService,
      message,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
