import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';

import { useRouter } from 'vue-router';
import { useLoginModal } from '@/account/login-modal';
import type AccountService from '@/account/account.service';
import languages from '@/shared/config/languages';
import themes from '@/shared/config/themes';
import EntitiesMenu from '@/entities/entities-menu.vue';

import { useStore } from '@/store';
import HeadersService from '@/entities/headers/headers.service.ts';
import Title from '@/core/title/title.vue';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'HeadersV1',
  components: {
    'entities-menu': EntitiesMenu,
    'title-app': Title,
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
    const changeLanguage = inject<(string) => Promise<void>>('changeLanguage');
    const changeTheme = inject<(string) => Promise<void>>('changeTheme');
    // TODO Get data headers
    const headersService = inject('headersService', () => new HeadersService());

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
        return router.currentRoute.value.path.indexOf(path) === 0; // current path starts with this path string
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
      hasAnyAuthorityValues,
      openAPIEnabled,
      inProduction,
      authenticated,
      t$: useI18n().t,

      themes: themes(),
      changeTheme,
      isActiveTheme,
    };
  },
  methods: {
    hasAnyAuthority(authorities: any): boolean {
      this.accountService.hasAnyAuthorityAndCheckAuth(authorities).then(value => {
        if (this.hasAnyAuthorityValues[authorities] !== value) {
          this.hasAnyAuthorityValues = { ...this.hasAnyAuthorityValues, [authorities]: value };
        }
      });
      return this.hasAnyAuthorityValues[authorities] ?? false;
    },
  },
});
