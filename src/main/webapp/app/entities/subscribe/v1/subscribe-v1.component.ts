import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';

import SubscribeV1Service from './subscribe-v1.service.ts';
import { type ISubscribe, Subscribe } from '@/shared/model/subscribe.model';
import { EMAIL_ALREADY_USED_TYPE } from '@/constants';
import { email, maxLength, minLength, required } from '@vuelidate/validators';
import { useVuelidate } from '@vuelidate/core';
import { VueRecaptcha } from 'vue-recaptcha';

import SocialMedia from '@/core/social-media/social-media.vue';
import { useAlertService } from '@/shared/alert/alert.service';

/**
 * Composant V1 d’abonnement à la newsletter.
 * Gère le formulaire, la validation, reCAPTCHA et l’appel API pour créer l’abonné.
 */
export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'SubscribeV1',
  components: {
    VueRecaptcha,
    'social-media': SocialMedia,
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
    const alertService = inject('alertService', () => useAlertService(), true);
    const errorEmailExists: Ref<string> = ref('');
    const success: Ref<boolean> = ref(false);
    const emailFormatError: Ref<boolean> = ref(false);
    const recaptchaError: Ref<boolean> = ref(false);
    const recaptchaVerified: Ref<boolean> = ref(false);
    const recaptchaToken: Ref<string> = ref('');

    const subscribe: Ref<ISubscribe> = ref(new Subscribe());

    const handleSuccess = (_response: string) => {
      recaptchaVerified.value = true;
      recaptchaError.value = false;
      recaptchaToken.value = _response;
    };

    const handleError = () => {
      recaptchaVerified.value = false;
      recaptchaError.value = true;
      recaptchaToken.value = '';
    };

    const siteKey = computed(() => RECAPTCHA_SITE_KEY);

    const validationRules = {
      subscribe: {
        email: {
          required,
          email,
        },
      },
    };

    const v$ = useVuelidate(validationRules, { subscribe });
    const { t: t$ } = useI18n();

    const showRecaptcha = computed(() => v$.value?.$invalid === false);

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
      subscribe.value.recaptchaToken = recaptchaToken.value;
      subscribeService
        .processSubscribe(subscribe.value)
        .then(() => {
          alertService.showSuccessCustom(
            t$('devalgasApp.subscribeV1.messages.success.subscribeSent'),
            t$('devalgasApp.subscribeV1.messages.success.sent'),
            'primary',
          );
          subscribe.value = new Subscribe();
          v$.value.$reset();
          recaptchaVerified.value = false;
          recaptchaError.value = false;
          recaptchaToken.value = '';
          emailFormatError.value = false;
          errorEmailExists.value = '';
          success.value = false;
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
      alertService,
      errorEmailExists,
      emailFormatError,
      t$,
      v$,
      recaptchaError,
      siteKey,
      recaptchaVerified,
      handleSuccess,
      handleError,
      showRecaptcha,
      subscribeEmail,
      refreshPage,
    };
  },
});
