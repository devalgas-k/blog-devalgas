import { type ComputedRef, defineComponent, inject } from 'vue';
import { useI18n } from 'vue-i18n';

import { useLoginModal } from '@/account/login-modal';
import ArticleSearchV1 from '@/entities/article/v1/search/article-search-v1.vue';
import Separator from '@/core/separator/separator.vue';
import ArticlesHome from '@/entities/article/v1/home/articles-home-v1.vue';

export default defineComponent({
  compatConfig: { MODE: 3 },
  components: {
    'article-search': ArticleSearchV1,
    'p-separator': Separator,
    'articles-home': ArticlesHome,
  },
  setup() {
    const { showLogin } = useLoginModal();
    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const username = inject<ComputedRef<string>>('currentUsername');

    return {
      authenticated,
      username,
      showLogin,
      t$: useI18n().t,
    };
  },
});
