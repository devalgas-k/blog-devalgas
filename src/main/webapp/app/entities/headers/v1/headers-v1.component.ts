import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';

import { useRouter } from 'vue-router';
import { useLoginModal } from '@/account/login-modal';
import type AccountService from '@/account/account.service';
import languages from '@/shared/config/languages';
import themes from '@/shared/config/themes';
import EntitiesMenu from '@/entities/entities-menu.vue';

import { useStore } from '@/store';
import Title from '@/core/title/title.vue';
import MessageContactV1 from '@/entities/message/v1/message-contact/message-contact-v1.vue';

import Separator from '@/core/separator/separator.vue';

/**
 * Composant V1 d’en-tête du site.
 * Affiche la navigation, le changement de langue et de thème, un menu entités,
 * et une zone de contact intégrée.
 */
export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'HeadersV1',
  components: {
    'p-separator': Separator,
    'entities-menu': EntitiesMenu,
    'title-app': Title,
    'message-contact-v1': MessageContactV1,
  },
  setup() {
    const { showLogin } = useLoginModal();
    const accountService = inject<AccountService>('accountService');
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);
    const currentTheme = inject(
      'currentTheme',
      () =>
        computed(
          () => localStorage.getItem('currentTheme') ?? (window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'),
        ),
      true,
    );
    const changeLanguage = inject<(lang: string) => Promise<void>>('changeLanguage', async () => {}, true);
    const changeTheme = inject<(theme: string) => Promise<void>>('changeTheme', async () => {}, true);
    // TODO Get data headers

    const { t } = useI18n();
    const separatorLabel = computed(() => t('globalV1.separator.or').toString());

    const isActiveLanguage = (key: string) => {
      return key === currentLanguage.value;
    };

    const isActiveTheme = (key: string) => {
      return key === currentTheme.value;
    };

    const router = useRouter();
    const store = useStore();

    const version = `v${APP_VERSION}`;
    const hasAnyAuthorityValues: Ref<any> = ref({});

    const openAPIEnabled = computed(() => store.activeProfiles.indexOf('api-docs') > -1);
    const inProduction = computed(() => store.activeProfiles.indexOf('prod') > -1);
    const authenticated = computed(() => store.authenticated);

    const subIsActive = (input: string | string[]) => {
      const paths = Array.isArray(input) ? input : [input];
      return paths.some(path => {
        return router.currentRoute.value.path.startsWith(path);
      });
    };

    const logout = async () => {
      localStorage.removeItem('jhi-authenticationToken');
      sessionStorage.removeItem('jhi-authenticationToken');
      store.logout();
      if (router.currentRoute.value.path !== '/') {
        router.push('/');
      }
    };

    return {
      logout,
      subIsActive,
      accountService,
      showLogin,
      changeLanguage,
      languages: languages(),
      isActiveLanguage,
      version,
      currentLanguage,
      separatorLabel,
      hasAnyAuthorityValues,
      openAPIEnabled,
      inProduction,
      authenticated,
      t$: t,

      themes: themes(),
      changeTheme,
      isActiveTheme,
    };
  },
  methods: {
    hasAnyAuthority(authorities: any): boolean {
      if (!this.accountService) {
        return false;
      }
      this.accountService.hasAnyAuthorityAndCheckAuth(authorities).then(value => {
        if (this.hasAnyAuthorityValues[authorities] !== value) {
          this.hasAnyAuthorityValues = { ...this.hasAnyAuthorityValues, [authorities]: value };
        }
      });
      return this.hasAnyAuthorityValues[authorities] ?? false;
    },
    hideDropdown(refName: string) {
      const dropdown: any = (this.$refs as any)?.[refName];
      if (dropdown && typeof dropdown.hide === 'function') {
        dropdown.hide(true);
      }
    },
    onClick() {
      this.hideDropdown('dropdown');
    },
    onContactSaved() {
      const dropdown: any = (this.$refs as any)?.contactDropdown;
      if (dropdown && typeof dropdown.hide === 'function') {
        dropdown.hide(true);
      } else {
        this.hideDropdown('contactDropdown');
      }
    },
    openWhatsApp() {
      window.location.href = WHATSAPP_URL;
    },
    openMailTo() {
      window.location.href = MAIL_TO;
    },
  },
});
