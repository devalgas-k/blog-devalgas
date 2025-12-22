import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';

import SubscribeV1Service from './subscribe-v1.service.ts';
import { type ISubscribe, Subscribe } from '@/shared/model/subscribe.model';
import { EMAIL_ALREADY_USED_TYPE } from '@/constants';
import { email, helpers, maxLength, minLength, required } from '@vuelidate/validators';
import { useVuelidate } from '@vuelidate/core';
import Message from 'primevue/message';

import { VueRecaptcha } from 'vue-recaptcha';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'SubscribeV1',
  components: {
    VueRecaptcha,
  },
  validations() {
    return {
      subscribe: {
        email: {
          required,
          minLength: minLength(5),
          maxLength: maxLength(254),
          email,
        },
      },
    };
  },
  setup() {
    const error: Ref<string> = ref('');
    const subscribeService = inject('subscribeService', () => new SubscribeV1Service(), true);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);
    const errorEmailExists: Ref<string> = ref('');
    const success: Ref<boolean> = ref(false);
    const emailFormatError: Ref<boolean> = ref(false);
    const recaptchaError: Ref<boolean> = ref(false);
    const recaptchaVerified: Ref<boolean> = ref(false);

    const subscribe: Ref<ISubscribe> = ref(new Subscribe());

    const handleSuccess = (response: string) => {
      recaptchaVerified.value = true;
      recaptchaError.value = false;
      console.log('Recaptcha verified:', response);
    };

    const handleError = () => {
      recaptchaVerified.value = false;
      recaptchaError.value = true;
      console.log('Recaptcha error');
    };

    const siteKey = computed(() => {
      return '6LcGCEEqAAAAAN4j0K5PEZHZhAMcdLKLFjuSULsn';
    });

    const validationRules = {
      subscribe: {
        email: {
          required,
          email,
        },
      },
    };

    const v$ = useVuelidate(validationRules, { subscribe });

    const subscribeEmail = (): void => {
      error.value = '';
      errorEmailExists.value = '';
      emailFormatError.value = false;

      // Vérifier si la validation échoue
      v$.value.$validate();
      if (v$.value.$error) {
        emailFormatError.value = true;
        return;
      }

      if (!recaptchaVerified.value) {
        recaptchaError.value = true;
        return;
      }

      subscribe.value.langKey = currentLanguage.value;
      subscribeService
        .processSubscribe(subscribe.value)
        .then(() => {
          success.value = true;
        })
        .catch(error => {
          success.value = false;
          if (error.response.status === 400 && error.response.data.type === EMAIL_ALREADY_USED_TYPE) {
            errorEmailExists.value = 'ERROR';
          } else {
            error.value = 'ERROR';
          }
        });
    };

    const refreshPage = () => {
      window.location.reload(); // Réactualise la page
    };

    return {
      error,
      success,
      subscribe,
      currentLanguage,
      subscribeService,
      errorEmailExists,
      emailFormatError,
      t$: useI18n().t,
      v$,
      recaptchaError,
      siteKey,
      recaptchaVerified,
      handleSuccess,
      handleError,
      subscribeEmail,
      refreshPage,
    };
  },
});
