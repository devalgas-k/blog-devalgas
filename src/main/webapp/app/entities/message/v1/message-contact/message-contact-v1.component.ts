import { type Ref, computed, defineComponent, inject, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';
import { email, helpers } from '@vuelidate/validators';
import { VueRecaptcha } from 'vue-recaptcha';

import MessageService from '../message-v1.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import SubjectService from '@/entities/subject/v1/subject-v1.service';
import { type ISubject } from '@/shared/model/subject.model';
import { type IMessage, Message } from '@/shared/model/message.model';

/**
 * Composant V1 de formulaire de contact.
 * Gère la saisie, la validation, reCAPTCHA, et l’envoi du message,
 * avec sélection du sujet et feedback utilisateur.
 */
export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MessageContactV1',
  components: {
    VueRecaptcha,
  },
  emits: ['saved'],
  setup() {
    const messageService = inject('messageService', () => new MessageService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const message: Ref<IMessage> = ref(new Message());

    const subjectService = inject('subjectService', () => new SubjectService());

    const subjects: Ref<ISubject[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);

    const router = useRouter();

    const previousState = () => router.go(-1);

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
        required: validations.required('entity.validation.required'),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 256 }).toString(), 256),
      },
      email: {
        required: validations.required('entity.validation.required'),
        email: helpers.withMessage('devalgasApp.messageContactV1.validation.email', email),
      },
      phone: {},
      message: {
        required: validations.required('entity.validation.required'),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 256 }).toString(), 256),
      },
      file: {
        maxSize: helpers.withMessage('devalgasApp.messageContactV1.validation.file.maxsize', (value: string | null) => {
          return !value || dataUtils.size(value) <= 5 * 1024 * 1024;
        }),
      },
      date: {},
      langKey: {
        minLength: validations.minLength(t$('entity.validation.minlength', { min: 2 }).toString(), 2),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 2 }).toString(), 2),
      },
      countryKey: {
        minLength: validations.minLength(t$('entity.validation.minlength', { min: 2 }).toString(), 2),
      },
      subject: {
        required: validations.required('entity.validation.required'),
      },
    };
    const v$ = useVuelidate(validationRules, message as any);
    v$.value.$validate();

    const recaptchaError: Ref<boolean> = ref(false);
    const recaptchaVerified: Ref<boolean> = ref(false);
    const recaptchaToken: Ref<string> = ref('');
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
    const showRecaptcha = computed(() => v$.value?.$invalid === false);
    const disableSubmit = computed(() => v$.value.$invalid || isSaving.value);

    const filteredSubjects = computed(() => {
      const lang = (currentLanguage.value ?? 'fr').split('-')[0]?.toLowerCase();
      const useFr = lang === 'fr';
      const list = subjects.value ?? [];
      return list.filter(s => {
        const t = useFr ? s.titleFr : s.titleEn;
        return Boolean((t ?? '').toString().trim());
      });
    });

    watch(filteredSubjects, list => {
      const current = message.value.subject;
      if (!list || list.length === 0) {
        return;
      }
      if (!current || !list.some(s => s.id === current.id)) {
        message.value.subject = list[0];
      }
    });

    return {
      messageService,
      alertService,
      message,
      previousState,
      isSaving,
      currentLanguage,
      subjects,
      filteredSubjects,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: message }),
      t$,
      recaptchaError,
      recaptchaVerified,
      siteKey,
      showRecaptcha,
      disableSubmit,
      handleSuccess,
      handleError,
      recaptchaToken,
    };
  },
  created(): void {},
  methods: {
    clearInputs(): void {
      this.message.name = '';
      this.message.email = '';
      this.message.message = '';
      this.message.file = null;
      this.message.fileContentType = null;
      const list = (this as any).filteredSubjects?.value ?? [];
      if (list && list.length > 0) {
        this.message.subject = list[0];
      }
      const fileInput = (this.$refs as any)?.file_file;
      if (fileInput && 'value' in fileInput) {
        (fileInput as any).value = null;
      }
      this.v$.$reset();
      this.recaptchaVerified = false;
      this.recaptchaError = false;
      this.recaptchaToken = '';
    },
    clearFile(event?: Event): void {
      event?.preventDefault();
      event?.stopPropagation();
      this.message.file = null;
      this.message.fileContentType = null;
    },
    save(): void {
      if (!this.recaptchaVerified) {
        this.recaptchaError = true;
        return;
      }
      this.$emit('saved');
      this.isSaving = true;
      this.message.recaptchaToken = this.recaptchaToken;
      this.messageService()
        .create(this.message)
        .then(_param => {
          this.isSaving = false;
          this.alertService.showSuccessCustom(
            this.t$('devalgasApp.messageContactV1.success.messageSent'),
            this.t$('devalgasApp.messageContactV1.success.sent'),
            'primary',
          );
          this.clearInputs();
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService.showHttpError((error as any).response);
        });
    },
  },
});
