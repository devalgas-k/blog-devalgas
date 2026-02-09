import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useVuelidate } from '@vuelidate/core';
import { email, helpers, maxLength, minLength, required, sameAs } from '@vuelidate/validators';
import { useLoginModal } from '@/account/login-modal';
import RegisterService from '@/account/register/register.service';
import { EMAIL_ALREADY_USED_TYPE, LOGIN_ALREADY_USED_TYPE } from '@/constants';
import { useHead } from '@unhead/vue';

const loginPattern = helpers.regex(/^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^[_.@A-Za-z0-9-]+$/);

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Register',
  validations() {
    return {
      registerAccount: {
        login: {
          required,
          minLength: minLength(1),
          maxLength: maxLength(50),
          pattern: loginPattern,
        },
        email: {
          required,
          minLength: minLength(5),
          maxLength: maxLength(254),
          email,
        },
        password: {
          required,
          minLength: minLength(4),
          maxLength: maxLength(254),
        },
      },
      confirmPassword: {
        required,
        minLength: minLength(4),
        maxLength: maxLength(50),
        sameAsPassword: sameAs(this.registerAccount.password),
      },
    };
  },
  setup() {
    const { showLogin } = useLoginModal();
    const registerService = inject('registerService', () => new RegisterService(), true);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);
    const { t: t$ } = useI18n();

    const error: Ref<string | null> = ref('');
    const errorEmailExists: Ref<string | null> = ref('');
    const errorUserExists: Ref<string | null> = ref('');
    const success: Ref<boolean | null> = ref(false);

    const confirmPassword: Ref<any> = ref(null);
    const registerAccount: Ref<any> = ref({
      login: undefined,
      email: undefined,
      password: undefined,
    });

    useHead({
      title: computed(() => t$('register.title').toString()),
      meta: [
        { name: 'robots', content: 'noindex,nofollow' },
        { name: 'googlebot', content: 'noindex,nofollow' },
      ],
    });

    return {
      showLogin,
      currentLanguage,
      registerService,
      error,
      errorEmailExists,
      errorUserExists,
      success,
      confirmPassword,
      registerAccount,
      v$: useVuelidate(),
      t$,
    };
  },
  methods: {
    register(): void {
      this.error = null;
      this.errorUserExists = null;
      this.errorEmailExists = null;
      this.registerAccount.langKey = this.currentLanguage;
      this.registerService
        .processRegistration(this.registerAccount)
        .then(() => {
          this.success = true;
        })
        .catch(error => {
          this.success = null;
          if (error.response.status === 400 && error.response.data.type === LOGIN_ALREADY_USED_TYPE) {
            this.errorUserExists = 'ERROR';
          } else if (error.response.status === 400 && error.response.data.type === EMAIL_ALREADY_USED_TYPE) {
            this.errorEmailExists = 'ERROR';
          } else {
            this.error = 'ERROR';
          }
        });
    },
  },
});
