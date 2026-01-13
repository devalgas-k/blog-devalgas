import { type PropType, computed, defineComponent, inject, toRef } from 'vue';
import { useI18n } from 'vue-i18n';

import type { IArticle } from '@/shared/model/article.model';
import { useDateFormat } from '@/shared/composables';
import Skeleton from 'primevue/skeleton';

/**
 * Composant V1 d’info d’article.
 * Affiche le label localisé, la date publiée et les tags de l’article.
 */
export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ArticleInfoV1',
  components: {
    'p-skeleton': Skeleton,
  },
  props: {
    article: {
      type: Object as PropType<IArticle>,
      required: false,
      default: undefined,
    },
  },
  setup: function (props) {
    const dateFormat = useDateFormat();
    const articleData = toRef(props, 'article');
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);
    const publishedDate = computed(() => {
      const d = articleData.value?.date as any;
      if (!d) return '';
      return dateFormat.formatDateShort(d);
    });
    const label = computed(() => {
      const lang = (currentLanguage?.value ?? 'fr').split('-')[0]?.toLowerCase();
      const useFr = lang === 'fr';
      const art = articleData.value;
      return useFr ? art?.labelFr : art?.labelEn;
    });

    return {
      t$: useI18n().t,
      ...dateFormat,
      publishedDate,
      currentLanguage,
      label,
    };
  },
});
