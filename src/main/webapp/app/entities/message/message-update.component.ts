import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MessageService from './message.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import SubjectService from '@/entities/subject/subject.service';
import { type ISubject } from '@/shared/model/subject.model';
import { type IMessage, Message } from '@/shared/model/message.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MessageUpdate',
  setup() {
    const messageService = inject('messageService', () => new MessageService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const message: Ref<IMessage> = ref(new Message());

    const subjectService = inject('subjectService', () => new SubjectService());

    const subjects: Ref<ISubject[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveMessage = async messageId => {
      try {
        const res = await messageService().find(messageId);
        res.date = new Date(res.date);
        message.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.messageId) {
      retrieveMessage(route.params.messageId);
    }

    const initRelationships = () => {
      subjectService()
        .retrieve()
        .then(res => {
          subjects.value = res.data;
        });
    };

    initRelationships();

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 256 }).toString(), 256),
      },
      email: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      phone: {},
      message: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 256 }).toString(), 256),
      },
      file: {},
      date: {},
      langKey: {
        minLength: validations.minLength(t$('entity.validation.minlength', { min: 2 }).toString(), 2),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 2 }).toString(), 2),
      },
      countryKey: {
        minLength: validations.minLength(t$('entity.validation.minlength', { min: 2 }).toString(), 2),
      },
      subject: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
    };
    const v$ = useVuelidate(validationRules, message as any);
    v$.value.$validate();

    return {
      messageService,
      alertService,
      message,
      previousState,
      isSaving,
      currentLanguage,
      subjects,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: message }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.message.id) {
        this.messageService()
          .update(this.message)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('devalgasApp.message.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.messageService()
          .create(this.message)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('devalgasApp.message.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
