import { defineComponent, provide } from 'vue';
import { useI18n } from 'vue-i18n';
import { storeToRefs } from 'pinia';

import { useLoginModal } from '@/account/login-modal';
import LoginForm from '@/account/login-form/v1/login-form-v1.vue';
import Ribbon from '@/core/ribbon/ribbon.vue';
import JhiFooter from '@/core/jhi-footer/jhi-footer.vue';
import JhiNavbar from '@/core/jhi-navbar/jhi-navbar.vue';
import HeadersV1 from '@/entities/headers/v1/headers-v1.vue';
import { useAlertService } from '@/shared/alert/alert.service';
import '@/shared/config/dayjs';
import FootersV1T from '@/entities/footers/v1/footers-v1.vue';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'App',
  components: {
    ribbon: Ribbon,
    'jhi-navbar': JhiNavbar,
    'login-form': LoginForm,
    'jhi-footer': JhiFooter,
    'headers-v1': HeadersV1,
    'footers-v1': FootersV1T,
  },
  setup() {
    provide('alertService', useAlertService());
    const { loginModalOpen } = storeToRefs(useLoginModal());

    return {
      loginModalOpen,
      t$: useI18n().t,
    };
  },
});
