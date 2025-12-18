import { type ComputedRef, type PropType, computed, defineComponent, inject, toRef } from 'vue';
import { useI18n } from 'vue-i18n';

import type { IArticle } from '@/shared/model/article.model';
import { useDateFormat } from '@/shared/composables';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ArticleInfoV1',
  props: {
    article: {
      type: Object as PropType<IArticle>,
      required: false,
      default: undefined,
    },
  },
  setup: function (props) {
    type LoginSvc = { openLogin: () => void };
    const loginService = inject<LoginSvc>('loginService')!;
    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const openLogin = () => {
      loginService.openLogin();
    };

    const dateFormat = useDateFormat();
    const articleData = toRef(props, 'article');
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);
    const publishedDate = computed(() => {
      const d = articleData.value?.date as any;
      if (!d) return '';
      return dateFormat.formatDate(d);
    });
    const label = computed(() => {
      const lang = (currentLanguage?.value ?? 'fr').split('-')[0]?.toLowerCase();
      const useFr = lang === 'fr';
      const art = articleData.value;
      return useFr ? art?.labelFr : art?.labelEn;
    });

    return {
      authenticated,
      openLogin,
      t$: useI18n().t,
      ...dateFormat,
      publishedDate,
      currentLanguage,
      label,
    };
  },
});
