import {
  computed,
  defineAsyncComponent,
  defineComponent,
  inject,
  onBeforeUnmount,
  onMounted,
  ref,
  watch,
  type Ref,
  type PropType,
} from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useHead } from '@unhead/vue';

import useDataUtils from '@/shared/data/data-utils.service.ts';
import { type IArticle } from '@/shared/model/article.model.ts';
import { useAlertService } from '@/shared/alert/alert.service.ts';
import { useDateFormat } from '@/shared/composables';

import Splitter from 'primevue/splitter';
import SplitterPanel from 'primevue/splitterpanel';
import AppLoader from '@/core/loader/app-loader.vue';
import ArticleDetailsSkeletonV1 from './article-details-skeleton-v1.vue';
import ArticleServiceV1 from '@/entities/article/v1/article.service-v1.ts';
const mode = (import.meta as any).env?.MODE;
const debugSkeleton =
  mode === 'development' &&
  (((window as any).location?.search ?? '').includes('debugSkeleton=1') ||
    (import.meta as any).env?.VITE_DEBUG_SHELL === 'true' ||
    localStorage.getItem('debugSkeleton') === 'true');
const makeLoader = (cb: () => Promise<any>) => {
  if (!debugSkeleton) return cb;
  return () =>
    new Promise<any>(resolve => {
      setTimeout(async () => resolve(await cb()), 800);
    });
};
const ArticleInfoV1 = defineAsyncComponent(makeLoader(() => import('../info/article-info-v1.vue')));
const Adsense = defineAsyncComponent(makeLoader(() => import('@/core/adsense/adsense.vue')));
import { useRenderGateStore } from '@/shared/config/store/render-gate-store';

/**
 * Composant V1 d’affichage des détails d’un article.
 * Récupère l’article par identifiant, affiche le contenu Markdown localisé,
 * et intègre les blocs d’information et de publicité.
 */
export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ArticleDetailsV1',
  components: {
    'p-splitter': Splitter,
    'p-splitter-panel': SplitterPanel,
    'article-info-v1': ArticleInfoV1,
    adsense: Adsense,
    'app-loader': AppLoader,
    'article-details-skeleton-v1': ArticleDetailsSkeletonV1,
  },
  props: {
    articleId: { type: Number, required: false, default: undefined },
    initialArticle: { type: Object as PropType<IArticle | null>, required: false, default: null },
  },
  setup(props) {
    const dateFormat = useDateFormat();
    const articleService = inject('articleService', () => new ArticleServiceV1());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);
    const langBase = computed(() => (currentLanguage?.value ?? 'fr').toString().split('-')[0].toLowerCase());

    const route = useRoute();
    const router = useRouter();
    const isDesktop = ref(true);
    const isFullyReady = ref(false);
    let mediaQuery: MediaQueryList | null = null;
    const onViewportChange = (event: MediaQueryListEvent) => {
      isDesktop.value = event.matches;
    };

    onMounted(() => {
      if (typeof window === 'undefined' || typeof window.matchMedia !== 'function') return;
      mediaQuery = window.matchMedia('(min-width: 768px)');
      isDesktop.value = mediaQuery.matches;
      if (typeof mediaQuery.addEventListener === 'function') {
        mediaQuery.addEventListener('change', onViewportChange);
      } else {
        (mediaQuery as any).addListener(onViewportChange);
      }
    });

    onBeforeUnmount(() => {
      if (!mediaQuery) return;
      if (typeof mediaQuery.removeEventListener === 'function') {
        mediaQuery.removeEventListener('change', onViewportChange);
      } else {
        (mediaQuery as any).removeListener(onViewportChange);
      }
    });

    const previousState = () => router.go(-1);
    const article: Ref<IArticle> = ref(props.initialArticle ?? {});
    const consentGiven = inject('consentGiven', () => computed(() => true), true);
    const adsenseScriptReady = inject('adsenseScriptReady', () => computed(() => false), true);
    let gate: any;
    try {
      gate = useRenderGateStore();
    } catch {}

    const retrieveArticle = async (articleId: number) => {
      try {
        const res = await articleService().find(articleId);
        article.value = res;
        if (gate && typeof gate.markDataReady === 'function') {
          gate.markDataReady();
        }
      } catch (error: any) {
        alertService.showHttpError(error?.response);
      }
    };

    if (route.params?.articleId) {
      retrieveArticle(Number(route.params.articleId));
    }
    watch(
      () => route.params?.articleId,
      newId => {
        const idNum = typeof newId === 'string' ? parseInt(newId, 10) : Number(newId);
        if (!Number.isNaN(idNum) && idNum > 0) {
          retrieveArticle(idNum);
        }
      },
    );
    const decodedMarkdownContent = computed(() => {
      const isFr = langBase.value === 'fr';
      const base64 = isFr ? (article.value.markdownFr ?? '') : (article.value.markdownEn ?? '');
      const contentType = isFr ? (article.value.markdownFrContentType ?? '') : (article.value.markdownEnContentType ?? '');
      return dataUtils.decodeMarkdownContent(base64, contentType);
    });

    const articleTitle = computed(() => {
      const isFr = langBase.value === 'fr';
      const art = article.value;
      return (isFr ? art?.labelFr : art?.labelEn) ?? '';
    });

    const descriptionCandidate = computed(() => {
      const isFr = langBase.value === 'fr';
      const art = article.value;
      const desc = (isFr ? art?.descriptionFr : art?.descriptionEn) ?? '';
      if (desc && desc.trim().length > 0) return desc.trim();
      const html = decodedMarkdownContent.value.html ?? '';
      if (!html) return '';
      const tmp = document.createElement('div');
      tmp.innerHTML = html;
      const text = (tmp.textContent ?? tmp.innerText ?? '').trim();
      if (!text) return '';
      const max = 160;
      return text.length > max ? text.substring(0, max - 1) + '…' : text;
    });

    const description = computed(() => {
      const d = descriptionCandidate.value;
      if (d && d.trim().length > 0) return d;
      return useI18n().t('globalV1.seo.description.articleFallback').toString();
    });

    const canonicalUrl = computed(() => {
      const origin = window?.location?.origin ?? '';
      const path = router?.currentRoute?.value?.path ?? route.path;
      return origin + path;
    });
    const ogImage = computed(() => {
      const origin = window?.location?.origin ?? '';
      const id = article.value?.id;
      if (id) {
        return `${origin}/api/v1/articles/${id}/og-image`;
      }
      return `${origin}${IMAGE_BASE_PATH}/logo-app.png`;
    });

    const ogLocale = computed(() => langBase.value);
    const siteName = 'Devalgas.net';
    const twitterSite = '@devalgas';
    const twitterImageAlt = articleTitle;

    const isLoading = computed(() => {
      return !article.value.id || !decodedMarkdownContent.value.html;
    });

    const buildSlug = (s: string) =>
      (s ?? '')
        .toLowerCase()
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .replace(/[^a-z0-9]+/g, '-')
        .replace(/^-+|-+$/g, '')
        .substring(0, 80);
    const altLinks = computed(() => {
      const origin = window?.location?.origin ?? '';
      const id = article.value?.id;
      if (!id) return [];
      const frSlug = buildSlug(article.value?.labelFr ?? '');
      const enSlug = buildSlug(article.value?.labelEn ?? '');
      return [
        { rel: 'alternate', hreflang: 'fr', href: `${origin}/v1/articles/${id}-${frSlug}/view` },
        { rel: 'alternate', hreflang: 'en', href: `${origin}/v1/articles/${id}-${enSlug}/view` },
        { rel: 'alternate', hreflang: 'x-default', href: `${origin}/v1/articles/${id}-${frSlug}/view` },
      ];
    });

    useHead({
      title: articleTitle,
      link: [
        { rel: 'canonical', href: canonicalUrl },
        { rel: 'preload', as: 'font', href: '/content/fonts/inter-v12-latin-regular.woff2', type: 'font/woff2', crossorigin: 'anonymous' },
        ...altLinks.value,
      ],
      meta: [
        { name: 'description', content: description },
        { property: 'og:title', content: articleTitle },
        { property: 'og:description', content: description },
        { property: 'og:url', content: canonicalUrl },
        { property: 'og:type', content: 'article' },
        { property: 'og:site_name', content: siteName },
        { property: 'og:locale', content: ogLocale },
        { property: 'og:image', content: ogImage },
        { name: 'twitter:card', content: 'summary_large_image' },
        { name: 'twitter:site', content: twitterSite },
        { name: 'twitter:title', content: articleTitle },
        { name: 'twitter:description', content: description },
        { name: 'twitter:image', content: ogImage },
        { name: 'twitter:image:alt', content: twitterImageAlt },
      ],
      script: [
        {
          type: 'application/ld+json',
          children: computed(() =>
            JSON.stringify({
              '@context': 'https://schema.org',
              '@type': 'BlogPosting',
              headline: articleTitle.value,
              description: description.value,
              inLanguage: langBase.value,
              mainEntityOfPage: canonicalUrl.value,
              datePublished: article.value?.date ? new Date(article.value.date).toISOString() : undefined,
              author: { '@type': 'Person', name: 'Devalgas.net' },
              publisher: { '@type': 'Organization', name: 'Devalgas.net' },
              image: ogImage.value,
              isPartOf: { '@type': 'Blog', name: siteName, url: canonicalUrl.value },
              keywords: Array.from(article.value?.categoryArticles ?? [])
                .map((c: any) => c?.code || c?.label)
                .filter((x: any) => !!x)
                .join(', '),
            }),
          ),
        },
      ],
    });

    // dynamic head handled via Unhead

    const onInfoMounted = () => {
      isFullyReady.value = true;
    };

    return {
      ...dateFormat,
      alertService,
      article,
      ...dataUtils,

      previousState,
      t$: useI18n().t,
      currentLanguage,
      decodedMarkdownContent,
      articleTitle,
      description,
      isDesktop,
      isFullyReady,
      isLoading,
      onInfoMounted,
      adsenseClient: ADSENSE_CLIENT,
      adsenseSlot: ADSENSE_SLOT,
      consentGiven,
      adsenseScriptReady,
    };
  },
});
