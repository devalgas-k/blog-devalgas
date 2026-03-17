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

**Solution 1+ — AppShell instantané pour App.vue (navbar + shell visibles immédiatement)**

- Constat
  - Le header attend i18nReady pour afficher le contenu [headers-v1.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/entities/headers/v1/headers-v1.vue#L19-L33), alors que le chargement des messages de langue est déclenché au montage [main.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/main.ts#L138-L152). Résultat: navbar masquée pendant l’import des traductions.
  - Des sous-composants du header (EntitiesMenu, MessageContactV1) sont importés statiquement [headers-v1.component.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/entities/headers/v1/headers-v1.component.ts#L25-L31), ils augmentent la pression sur le bundle initial même si le dropdown n’est pas ouvert.
- Objectif
  - Afficher simultanément et rapidement la navbar et les autres blocs d’App.vue, en supprimant la dépendance au “i18nReady” global pour le rendu structurel, tout en repoussant les contenus lourds et les textes détaillés.
- Approche
  1. Pré-initialiser i18n avant le montage et marquer i18nReady tôt
     ```ts
     // src/main/webapp/app/main.ts (setup)
     const initialLang =
       translationService.getLocalStoreLanguage() ??
       (translationService.isLanguageSupported(navigator.language) ? navigator.language : 'fr');
     await translationService.refreshTranslation(initialLang); // précharge le bundle dès setup
     translationStore.setCurrentLanguage(initialLang);
     i18nReady.value = true; // permet au header d’apparaître immédiatement
     // Les mises à jour liées au compte peuvent recaler la langue ensuite (watch store.account)
     ```
     - Impact: la navbar peut s’afficher sans attendre onMounted; les textes se raffinent si la langue change plus tard.
  2. Rendu partiel sans gating total
     ```vue
     <!-- src/main/webapp/app/entities/headers/v1/headers-v1.vue -->
     <b-collapse is-nav id="header-tabs">
       <!-- Rendre la structure (brand, icônes, toggler) sans dépendre de i18nReady -->
       <b-navbar-nav class="headers-v1__nav-center justify-content-center">
         <!-- Icônes et liens peuvent s’afficher, les libellés utilisent t$ (clé si non prête) -->
       </b-navbar-nav>
       <b-navbar-nav class="ml-auto">
         <!-- Dropdowns visibles, mais leur contenu détaillé reste conditionnel -->
       </b-navbar-nav>
     </b-collapse>
     <!-- Supprimer le v-else loader global; utiliser des skeletons ciblés pour les libellés -->
     ```
     - Impact: la structure du header est visible immédiatement; les libellés se mettent à jour dès disponibilité des traductions, sans bloquer l’apparition.
  3. Menus lourds en lazy et au “open” du dropdown
     ```ts
     // src/main/webapp/app/entities/headers/v1/headers-v1.component.ts
     import { defineAsyncComponent } from 'vue';
     const EntitiesMenuAsync = defineAsyncComponent(() => import('@/entities/entities-menu.vue'));
     const MessageContactAsync = defineAsyncComponent(() => import('@/entities/message/v1/message-contact/message-contact-v1.vue'));
     export default defineComponent({
       components: {
         'entities-menu': EntitiesMenuAsync,
         'message-contact-v1': MessageContactAsync,
       },
     });
     ```
     ```vue
     <!-- src/main/webapp/app/entities/headers/v1/headers-v1.vue -->
     <b-nav-item-dropdown id="entity-menu" v-if="authenticated" @shown="showEntities = true">
       <template #button-content>...</template>
       <entities-menu v-if="showEntities"></entities-menu>
     </b-nav-item-dropdown>
     <b-nav-item-dropdown id="contactUsnavBarDropdown" ref="contactDropdown" @shown="showContact = true">
       <template #button-content>...</template>
       <message-contact-v1 v-if="showContact" @saved="onContactSaved"></message-contact-v1>
     </b-nav-item-dropdown>
     ```
     - Impact: les composants complexes ne sont pas importés tant que le dropdown n’est pas ouvert; le header lui-même rend vite.
  4. App.vue: paralléliser le shell
     - Garder le header non-suspensible; placer Suspense uniquement autour de router-view et des sections de page lourdes, pour que header + footer apparaissent en même temps que le container.
     - Ajouter content-visibility: auto sur le container principal pour réduire le coût de paint initial, tout en montrant le cadre.
  5. Préchauffage ciblé des dépendances UI
     ```ts
     // src/main/webapp/app/main.ts
     useHead({
       link: [
         { rel: 'preconnect', href: 'https://use.fontawesome.com', crossorigin: '' },
         { rel: 'dns-prefetch', href: 'https://use.fontawesome.com' },
       ],
     });
     ```
     - Impact: les icônes s’affichent plus vite, évitant un header visuellement vide.
- Impact attendu
  - AppShell visible quasi instantanément (brand, toggler, icônes, structure) pendant que les traductions se chargent et que le contenu lourd reste en lazy.
  - Navbar et autres blocs de App.vue apparaissent ensemble, réduisant la perception de latence et améliorant le Speed Index/INP.
  - Réduction du bundle initial effectif grâce aux menus en lazy et aux préchauffages ciblés.
- Références
  - i18n init et i18nReady → [main.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/main.ts#L97-L121)
  - Header/vue structure → [headers-v1.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/entities/headers/v1/headers-v1.vue)
  - Imports actuels du header → [headers-v1.component.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/entities/headers/v1/headers-v1.component.ts#L25-L31)
