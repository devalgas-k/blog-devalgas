import { defineComponent, provide, inject, computed, ref, defineAsyncComponent } from 'vue';
import { useI18n } from 'vue-i18n';
import { storeToRefs } from 'pinia';

import { useLoginModal } from '@/account/login-modal';
import LoginForm from '@/account/login-form/v1/login-form-v1.vue';
import Ribbon from '@/core/ribbon/ribbon.vue';
import JhiFooter from '@/core/jhi-footer/jhi-footer.vue';
import JhiNavbar from '@/core/jhi-navbar/jhi-navbar.vue';
const HeadersAsync = defineAsyncComponent({
  loader: () => import('@/entities/headers/v1/headers-v1.vue'),
  suspensible: false,
});
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
    'headers-async': HeadersAsync,
    'footers-v1': FootersV1T,
  },
  setup() {
    provide('alertService', useAlertService());
    const { loginModalOpen } = storeToRefs(useLoginModal());
    const i18nReady = inject('i18nReady', () => computed(() => true), true);
    const routeReady = inject('routeReady', () => computed(() => true), true);

    const shellReady = computed(() => i18nReady.value && routeReady.value);

    return {
      loginModalOpen,
      i18nReady,
      t$: useI18n().t,
      shellReady,
    };
  },
});
