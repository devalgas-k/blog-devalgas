**Objectif Global**

- Optimiser le rendu, la latence des données et la stabilité visuelle de la page d’accueil (localhost:9000) en combinant quatre leviers complémentaires: asynchronisme piloté, préchauffage réseau, gating des scripts tiers en dev, et content-visibility pour les sections hors-viewport.

**Solution 1 — Home asynchrone et montage à la demande (améliorée)**

- Constat
  - Home charge ArticleSearchV1 et ArticlesHomeV1 statiquement, déclenchant deux appels API au premier rendu: catégories groupées [article-search-v1.component.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/entities/article/v1/search/article-search-v1.component.ts#L62-L80) via [category-article-v1.service.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/entities/category-article/v1/category-article-v1.service.ts#L33-L50) et articles summary via [articles-home.component-v1.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/entities/article/v1/home/articles-home.component-v1.ts#L61-L80).
- Implémentation
  - Composants asynchrones et Suspense locaux:
    ```ts
    // src/main/webapp/app/core/home/home.component.ts
    import { defineAsyncComponent, defineComponent, onMounted, ref } from 'vue';
    const ArticleSearchAsync = defineAsyncComponent(() => import('@/entities/article/v1/search/article-search-v1.vue'));
    const ArticlesHomeAsync = defineAsyncComponent(() => import('@/entities/article/v1/home/articles-home-v1.vue'));
    export default defineComponent({
      name: 'Home',
      components: { 'article-search': ArticleSearchAsync, 'articles-home': ArticlesHomeAsync },
      setup() {
        const listVisible = ref(false);
        const homeListAnchor = ref<HTMLElement | null>(null);
        onMounted(() => {
          const el = homeListAnchor.value;
          if (!el) return;
          const io = new IntersectionObserver(
            entries => {
              if (entries.some(e => e.isIntersecting)) {
                listVisible.value = true;
                io.disconnect();
              }
            },
            { rootMargin: '300px' }, // précharge proche du viewport
          );
          io.observe(el);
        });
        return { listVisible, homeListAnchor };
      },
    });
    ```
    ```vue
    <!-- src/main/webapp/app/core/home/home.vue -->
    <Suspense>
      <template #default><article-search /></template>
      <template #fallback><div aria-busy="true" style="height:55px"></div></template>
    </Suspense>
    <div ref="homeListAnchor"></div>
    <Suspense>
      <template #default><articles-home v-if="listVisible" /></template>
      <template #fallback><div aria-busy="true" style="min-height:200px"></div></template>
    </Suspense>
    ```
  - Chargement des données “à l’intention” pour ArticleSearchV1 (lazy fetch): ne pas précharger les catégories au montage; déclencher retrieveHome lors du premier focus/saisie sur l’AutoComplete, avec préchauffage de secours sur idle:
    ```ts
    // src/main/webapp/app/entities/article/v1/search/article-search-v1.component.ts
    import { onMounted, ref } from 'vue';
    const dataLoaded = ref(false);
    const loadData = async () => {
      if (dataLoaded.value) return;
      await retrieveCategoryArticles();
      dataLoaded.value = true;
      search({ query: '' });
    };
    const onAutoFocus = () => {
      inFocus.value = true;
      loadData();
    };
    onMounted(() => {
      const idle = (cb: () => void) => {
        try {
          (window as any).requestIdleCallback(cb, { timeout: 2000 });
        } catch {
          setTimeout(cb, 1500);
        }
      };
      idle(() => {
        if (!inFocus.value) loadData();
      });
    });
    ```
  - Préchargement du chunk ArticlesHome à l’approche du viewport (rootMargin) et au idle via import() pour réduire le délai d’apparition:
    ```ts
    // src/main/webapp/app/core/home/home.component.ts (complément)
    const prefetchArticlesHome = () => import('@/entities/article/v1/home/articles-home-v1.vue');
    onMounted(() => {
      const idle = () => {
        try {
          (window as any).requestIdleCallback(() => prefetchArticlesHome(), { timeout: 2000 });
        } catch {
          setTimeout(() => prefetchArticlesHome(), 1500);
        }
      };
      idle();
    });
    ```
- Impact attendu
  - Réduction du JS initial et des appels API au first paint; meilleure TTI/INP/ressenti.
  - Apparition rapide des blocs au moment opportun, avec skeletons locaux.
- Références
  - Home statique → [home.component.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/core/home/home.component.ts#L14-L17)
  - Recherche (préchargement actuel) → [article-search-v1.component.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/entities/article/v1/search/article-search-v1.component.ts#L62-L80)
  - Liste articles → [articles-home-v1.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/entities/article/v1/home/articles-home-v1.vue#L1-L36)

**Solution 2 — Preconnect/dns-prefetch vers le backend dev**

- Constat
  - Proxy Vite pointe vers 8080 [vite.config.mts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/vite.config.mts#L106-L118), pas de preconnect/dns-prefetch déclaré.
- Implémentation
  ```ts
  // src/main/webapp/app/main.ts
  useHead({
    link: [
      { rel: 'preconnect', href: 'http://localhost:8080', crossorigin: '' },
      { rel: 'dns-prefetch', href: 'http://localhost:8080' },
    ],
  });
  ```
- Impact attendu
  - TTFB réduit sur les premiers appels Axios vers /api/v1/… (articles/catégories).

**Solution 3 — Gating AdSense en dev et injection conditionnelle**

- Constat
  - Script AdSense injecté à l’initialisation [main.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/main.ts#L232-L240), ADSENSE_ENABLED par défaut true [vite.config.mts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/vite.config.mts#L35-L95).
- Implémentation
  - Forcer ADSENSE_ENABLED=false en dev et injecter le script uniquement si ADSENSE_ENABLED && consentGiven:
    ```ts
    // vite.config.mts
    const ADSENSE_ENABLED_VAL = mode === 'development' ? false : env.ADSENSE_ENABLED ? env.ADSENSE_ENABLED === 'true' : true;
    // src/main/webapp/app/main.ts
    if (ADSENSE_ENABLED && consentRef.value) {
      useHead({
        script: [
          {
            src: `https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=${ADSENSE_CLIENT}`,
            async: true,
            crossorigin: 'anonymous',
          },
        ],
      });
    }
    ```
- Impact attendu
  - En dev: zéro coût Ads; en prod: respect du consentement et stabilité visuelle.
- Références
  - Blocs Ads → [app.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/app.vue#L12-L86)

**Solution 4 — content-visibility pour les sections hors-viewport**

- Constat
  - Liste paginée et pied de page calculent layout/paint dès le premier rendu.
- Implémentation
  ```scss
  /* src/main/webapp/app/entities/article/v1/home/articles-home-v1.vue */
  .writing {
    content-visibility: auto;
    contain-intrinsic-size: 600px;
  }
  /* src/main/webapp/app/entities/footers/v1/footers-v1.vue */
  .footer-v1 {
    content-visibility: auto;
    contain-intrinsic-size: 420px;
  }
  ```
- Impact attendu
  - Moins de layout/paint au premier rendu; speed index et INP améliorés; CLS maîtrisé si tailles ajustées.

**Synthèse**

- 1 cible le coût initial JS/données avec lazy mount + lazy fetch + prefetch sur idle.
- 2 réduit la latence réseau dev.
- 3 supprime le coût Ads en dev et protège la prod via consent gating.
- 4 évite layout/paint hors-viewport et limite le travail initial du navigateur.
