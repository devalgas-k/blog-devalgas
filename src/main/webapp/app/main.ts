// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.common with an alias.
import Vue, { computed, createApp, onMounted, provide, watch, ref } from 'vue';
import { createPinia, storeToRefs } from 'pinia';
import { useI18n } from 'vue-i18n';

import App from './app.vue';
import router from './router';
import { useTrackerService } from './admin/tracker/tracker.service';
import { useStore, useTranslationStore } from '@/store';
import { setupAxiosInterceptors } from '@/shared/config/axios-interceptor';

import { initFortAwesome, initI18N } from '@/shared/config/config';
import { initBootstrapVue } from '@/shared/config/config-bootstrap-vue';
import JhiItemCountComponent from '@/shared/jhi-item-count.vue';
import JhiSortIndicatorComponent from '@/shared/sort/jhi-sort-indicator.vue';
import { useLoginModal } from '@/account/login-modal';
import AccountService from '@/account/account.service';

import '../content/scss/vendor.scss';
import '../content/scss/global.scss';
import TranslationService from '@/locale/translation.service';

import PrimeVue from 'primevue/config';
import 'primeicons/primeicons.css';
import 'primevue/resources/primevue.min.css';
import 'primevue/resources/themes/bootstrap4-dark-blue/theme.css';
import ScrollTop from 'primevue/scrolltop';
import primeiconsWoff2Url from 'primeicons/fonts/primeicons.woff2?url';

import { createHead, useHead } from '@unhead/vue';
const pinia = createPinia();

// jhipster-needle-add-entity-service-to-main-import - JHipster will import entities services here

initBootstrapVue(Vue);

Vue.configureCompat({
  MODE: 2,
  ATTR_FALSE_VALUE: 'suppress-warning',
  COMPONENT_FUNCTIONAL: 'suppress-warning',
  COMPONENT_V_MODEL: 'suppress-warning',
  CONFIG_OPTION_MERGE_STRATS: 'suppress-warning',
  CONFIG_WHITESPACE: 'suppress-warning',
  CUSTOM_DIR: 'suppress-warning',
  GLOBAL_EXTEND: 'suppress-warning',
  GLOBAL_MOUNT: 'suppress-warning',
  GLOBAL_PRIVATE_UTIL: 'suppress-warning',
  GLOBAL_PROTOTYPE: 'suppress-warning',
  GLOBAL_SET: 'suppress-warning',
  INSTANCE_ATTRS_CLASS_STYLE: 'suppress-warning',
  INSTANCE_CHILDREN: 'suppress-warning',
  INSTANCE_DELETE: 'suppress-warning',
  INSTANCE_DESTROY: 'suppress-warning',
  INSTANCE_EVENT_EMITTER: 'suppress-warning',
  INSTANCE_EVENT_HOOKS: 'suppress-warning',
  INSTANCE_LISTENERS: 'suppress-warning',
  INSTANCE_SCOPED_SLOTS: 'suppress-warning',
  INSTANCE_SET: 'suppress-warning',
  OPTIONS_BEFORE_DESTROY: 'suppress-warning',
  OPTIONS_DATA_MERGE: 'suppress-warning',
  OPTIONS_DESTROYED: 'suppress-warning',
  RENDER_FUNCTION: 'suppress-warning',
  WATCH_ARRAY: 'suppress-warning',
  PRIVATE_APIS: 'suppress-warning',
});

const i18n = initI18N();

const app = createApp({
  compatConfig: { MODE: 3 },
  components: { App },
  setup() {
    const { hideLogin, showLogin } = useLoginModal();
    const store = useStore();
    const accountService = new AccountService(store);
    const i18n = useI18n();
    const translationStore = useTranslationStore();
    const translationService = new TranslationService(i18n);
    const i18nReady = ref(false);
    const applyTheme = (theme: string) => {
      const root = document.documentElement;
      root.classList.remove('theme-light', 'theme-dark');
      root.classList.add(theme === 'dark' ? 'theme-dark' : 'theme-light');
    };
    const themeFromStorage = localStorage.getItem('currentTheme');
    const themeRef = ref(themeFromStorage ? themeFromStorage : 'light');
    const changeTheme = async (newTheme: string) => {
      if (themeRef.value !== newTheme) {
        themeRef.value = newTheme;
        localStorage.setItem('currentTheme', newTheme);
        applyTheme(newTheme);
      }
    };
    applyTheme(themeRef.value);

    const changeLanguage = async (newLanguage: string) => {
      if (i18n.locale.value !== newLanguage) {
        i18nReady.value = false;
        await translationService.refreshTranslation(newLanguage);
        translationStore.setCurrentLanguage(newLanguage);
        i18nReady.value = true;
      }
    };

    provide('currentLanguage', i18n.locale);
    provide('changeLanguage', changeLanguage);
    provide(
      'i18nReady',
      computed(() => i18nReady.value),
    );
    provide(
      'currentTheme',
      computed(() => themeRef.value),
    );
    provide('changeTheme', changeTheme);

    watch(
      () => store.account,
      async value => {
        if (!translationService.getLocalStoreLanguage()) {
          await changeLanguage(value.langKey);
        }
      },
    );

    watch(
      () => translationStore.currentLanguage,
      value => {
        translationService.setLocale(value);
      },
    );

    onMounted(async () => {
      const lang = [translationService.getLocalStoreLanguage(), store.account?.langKey, navigator.language, 'fr'].find(
        lang => lang && translationService.isLanguageSupported(lang),
      );
      await changeLanguage(lang);
    });

    /* TODO onMounted(() => {
      const scriptId = 'adsbygoogle-js';
      if (!document.getElementById(scriptId)) {
        const s = document.createElement('script');
        s.async = true;
        s.type = 'text/javascript';
        s.src = 'https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=ca-pub-6181972205565553';
        s.crossOrigin = 'anonymous';
        s.id = scriptId;
        document.head.appendChild(s);
      }
    });*/

    router.beforeResolve(async (to, from, next) => {
      // Make sure login modal is closed
      hideLogin();

      if (!store.authenticated) {
        await accountService.update();
      }
      if (to.meta?.authorities && to.meta.authorities.length > 0) {
        const value = await accountService.hasAnyAuthorityAndCheckAuth(to.meta.authorities);
        if (!value) {
          if (from.path !== '/forbidden') {
            next({ path: '/forbidden' });
            return;
          }
        }
      }
      next();
    });

    setupAxiosInterceptors(
      error => {
        const url = error.response?.config?.url;
        const status = error.status || error.response.status;
        if (status === 401) {
          // Store logged out state.
          store.logout();
          if (!url.endsWith('api/account') && !url.endsWith('api/authenticate')) {
            // Ask for a new authentication
            showLogin();
            return;
          }
        }
        return Promise.reject(error);
      },
      error => {
        return Promise.reject(error);
      },
    );

    const { authenticated } = storeToRefs(store);
    provide('authenticated', authenticated);
    provide(
      'currentUsername',
      computed(() => store.account?.login),
    );

    provide('translationService', translationService);
    provide('accountService', accountService);
    // jhipster-needle-add-entity-service-to-main - JHipster will import entities services here

    provide('trackerService', useTrackerService({ authenticated }));
    useHead({
      htmlAttrs: {
        lang: computed(() => i18n.locale.value.toString().split('-')[0].toLowerCase()),
      },
      link: [{ rel: 'preload', as: 'font', href: primeiconsWoff2Url, crossorigin: 'anonymous' }],
    });

    const s = document.createElement('style');
    s.textContent =
      "@font-face{font-family:'primeicons';src:url('" +
      primeiconsWoff2Url +
      "') format('woff2');font-weight:normal;font-style:normal;font-display:swap}";
    document.head.appendChild(s);
  },
  template: '<App/>',
});

initFortAwesome(app);

const head = createHead();

app
  .component('JhiItemCount', JhiItemCountComponent)
  .component('jhi-sort-indicator', JhiSortIndicatorComponent)
  .component('ScrollTop', ScrollTop)
  .use(router)
  .use(pinia)
  .use(i18n)
  .use(head)
  .use(PrimeVue, { inputStyle: 'filled', ripple: true })
  .mount('#app');
