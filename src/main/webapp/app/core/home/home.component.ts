import { type ComputedRef, defineComponent, inject, onMounted, onBeforeUnmount, onActivated, onDeactivated, ref, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { useHead } from '@unhead/vue';

import { useLoginModal } from '@/account/login-modal';
import ArticleSearchV1 from '@/entities/article/v1/search/article-search-v1.vue';
import ArticlesHome from '@/entities/article/v1/home/articles-home-v1.vue';

declare const WRITING_HASH: string;

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Home',
  components: {
    'article-search': ArticleSearchV1,
    'articles-home': ArticlesHome,
  },
  setup() {
    const { showLogin } = useLoginModal();
    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const username = inject<ComputedRef<string>>('currentUsername');
    const { t: t$ } = useI18n();
    const currentLanguage = inject<ComputedRef<string>>('currentLanguage');

    const hashRef = ref<string>(window?.location?.hash ?? '');
    const baseLang = computed(() => (currentLanguage?.value ?? 'fr').toString().split('-')[0].toLowerCase());
    const isWriting = computed(() => hashRef.value === WRITING_HASH);
    const title = computed(() => (isWriting.value ? t$('globalV1.headers.blog') : t$('global.title')).toString());
    const description = computed(() =>
      (isWriting.value ? t$('globalV1.seo.description.writing') : t$('globalV1.seo.description.base')).toString(),
    );

    useHead({
      title,
      meta: [{ name: 'description', content: description }],
    });

    const onHashChange = () => {
      hashRef.value = window?.location?.hash ?? '';
    };
    const listenerAttached = ref(false);
    const attach = () => {
      if (!listenerAttached.value) {
        onHashChange();
        window.addEventListener('hashchange', onHashChange);
        listenerAttached.value = true;
      }
    };
    const detach = () => {
      if (listenerAttached.value) {
        window.removeEventListener('hashchange', onHashChange);
        listenerAttached.value = false;
      }
    };
    onMounted(attach);
    onActivated(attach);
    onDeactivated(detach);
    onBeforeUnmount(detach);

    return {
      authenticated,
      username,
      showLogin,
      t$,
      baseLang,
    };
  },
});
