import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import SubscribeService from './subscribe.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type ISubscribe, Subscribe } from '@/shared/model/subscribe.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'SubscribeUpdate',
  setup() {
    const subscribeService = inject('subscribeService', () => new SubscribeService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const subscribe: Ref<ISubscribe> = ref(new Subscribe());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveSubscribe = async subscribeId => {
      try {
        const res = await subscribeService().find(subscribeId);
        res.date = new Date(res.date);
        subscribe.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.subscribeId) {
      retrieveSubscribe(route.params.subscribeId);
    }

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      email: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      langKey: {
        minLength: validations.minLength(t$('entity.validation.minlength', { min: 2 }).toString(), 2),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 2 }).toString(), 2),
      },
      countryKey: {
        minLength: validations.minLength(t$('entity.validation.minlength', { min: 2 }).toString(), 2),
      },
      date: {},
    };
    const v$ = useVuelidate(validationRules, subscribe as any);
    v$.value.$validate();

    return {
      subscribeService,
      alertService,
      subscribe,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: subscribe }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.subscribe.id) {
        this.subscribeService()
          .update(this.subscribe)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('devalgasApp.subscribe.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.subscribeService()
          .create(this.subscribe)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('devalgasApp.subscribe.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
