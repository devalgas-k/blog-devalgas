import { type ComputedRef, defineComponent, inject, onMounted, onBeforeUnmount } from 'vue';
import { useI18n } from 'vue-i18n';

import { useLoginModal } from '@/account/login-modal';
import ArticleSearchV1 from '@/entities/article/v1/search/article-search-v1.vue';
import ArticlesHome from '@/entities/article/v1/home/articles-home-v1.vue';

declare const WRITING_HASH: string;

export default defineComponent({
  compatConfig: { MODE: 3 },
  components: {
    'article-search': ArticleSearchV1,
    'articles-home': ArticlesHome,
  },
  setup() {
    const { showLogin } = useLoginModal();
    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const username = inject<ComputedRef<string>>('currentUsername');
    const { t: t$ } = useI18n();

    const updateTitle = () => {
      document.title = (window?.location?.hash === WRITING_HASH ? t$('globalV1.headers.blog') : t$('global.title')).toString();
    };

    onMounted(() => {
      updateTitle();
      window.addEventListener('hashchange', updateTitle);
    });
    onBeforeUnmount(() => {
      window.removeEventListener('hashchange', updateTitle);
    });

    return {
      authenticated,
      username,
      showLogin,
      t$,
    };
  },
});
