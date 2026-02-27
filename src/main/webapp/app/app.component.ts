import { defineComponent, provide, inject, computed } from 'vue';
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
import Adsense from '@/core/adsense/adsense.vue';

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
    adsense: Adsense,
  },
  setup() {
    provide('alertService', useAlertService());
    const { loginModalOpen } = storeToRefs(useLoginModal());
    const i18nReady = inject('i18nReady', () => computed(() => false), true);
    const consentGiven = inject('consentGiven', () => computed(() => true), true);
    const adsenseScriptReady = inject('adsenseScriptReady', () => computed(() => false), true);
    const slotTopValid = computed(() => !!ADSENSE_SLOT_TOP && !ADSENSE_SLOT_TOP.startsWith('000000'));
    const slotLeftValid = computed(() => !!ADSENSE_SLOT_SIDEBAR_LEFT && !ADSENSE_SLOT_SIDEBAR_LEFT.startsWith('000000'));
    const slotRightValid = computed(() => !!ADSENSE_SLOT_SIDEBAR_RIGHT && !ADSENSE_SLOT_SIDEBAR_RIGHT.startsWith('000000'));
    const slotFooterValid = computed(() => !!ADSENSE_SLOT_FOOTER && !ADSENSE_SLOT_FOOTER.startsWith('000000'));

    return {
      loginModalOpen,
      i18nReady,
      consentGiven,
      adsenseScriptReady,
      t$: useI18n().t,
      ADSENSE_ENABLED,
      ADSENSE_SLOT_TOP,
      ADSENSE_SLOT_SIDEBAR_LEFT,
      ADSENSE_SLOT_SIDEBAR_RIGHT,
      ADSENSE_SLOT_FOOTER,
      slotTopValid,
      slotLeftValid,
      slotRightValid,
      slotFooterValid,
    };
  },
});
