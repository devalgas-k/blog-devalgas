Titre: Conception Technique Détaillée (CTD) — Optimisation du rendu simultané Home et ArticleDetails

Rôle: Tu es un architecte logiciel senior. Tu dois produire en français une CTD exhaustive pour une nouvelle conception visant à synchroniser l’affichage des composants principaux (en‑tête, contenu, publicité) et à optimiser les performances de la page d’accueil et des pages de détails d’article.

Objectif: Définir l’architecture de « gating » de rendu, les choix techniques et les spécifications détaillées pour améliorer LCP/CLS et la perception utilisateur, puis fournir les éléments nécessaires à l’implémentation et à la validation locale.

Entrées (contexte et artefacts):

- Front Vue 3 (compat), Pinia, BootstrapVue, PrimeVue, Unhead (head/SEO)
- Layout actuel: [app.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/app.vue), en‑tête [headers-v1.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/entities/headers/v1/headers-v1.vue), accueil [home.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/core/home/home.vue)
- Accueil: recherche + liste articles V1 ([article-search-v1.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/entities/article/v1/search/article-search-v1.vue), [articles-home-v1.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/entities/article/v1/home/articles-home-v1.vue))
- Détails article: V1 ([article-details-v1.component.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/entities/article/v1/details/article-details-v1.component.ts)) et legacy ([article-details.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/entities/article/article-details.vue))
- Initialisation et services globaux: [main.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/main.ts)
- Terraform (hébergement et CDN): [app_service.tf](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/terraform/app_service.tf), [cdn.tf](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/terraform/cdn.tf)

Problème observé:

- L’en‑tête (headers-v1) s’affiche avant le reste, créant un rendu perçu « en deux temps », avec risque de CLS et de fragmentation du LCP sur l’accueil et les détails d’article.
- Objectif produit: faire apparaître les composants principaux au même moment avec un fallback skeleton cohérent, tout en conservant la réactivité et le lazy‑loading des parties non‑critiques (ex: contact, pubs).

Contraintes et dépendances:

- Stack: Spring Boot + Vue 3 (compat), Pinia, PrimeVue, BootstrapVue, Unhead<mccoremem id="03fe19oiu0zxufuavvq76clzw|01KGQ7CJJEW6K9RA78V7Z49C4A" />
- SEO/Head via Unhead (@unhead/vue) avec préchargements (fonts/images) et metas<mccoremem id="01KGQ7CJJEW6K9RA78V7Z49C4A|03fjih8axvt94kceqx45xfa0k" />
- Performance UI: réduire CLS, stabiliser LCP; conserver placeholders/skeletons cohérents<mccoremem id="01KGSMHVZXMK1S0T8TQ79C8G65" />
- CDN Front Door optionnel avec variable d’environnement CDN_BASE_URL injectée<mccoremem id="03fp4z92wfqlqr8pk88e3lyds|03fp4cvf9vj3h90f98v6i3wei" />
- Publicité AdSense et consentement (gating déjà présent côté app)<mccoremem id="01KJE35E7NSNA2VR2E9QBD9CGV" />

Volumétrie et SLA (cibles):

- LCP accueil cible < 2.5s (mobile), CLS < 0.1; First Paint simultané header + contenu principal
- Détails article: favoriser LCP stable avec image/banner et markdown localisé

Livrables attendus:

- CTD complète au format Markdown (docs/ctd/ctd-optimisation-rendu-simultane.md)
- Spécifications détaillées: store de « gating » global, Suspense, lazy non‑critique
- Liste des fichiers à modifier (front) avec chemins absolus
- Patchs proposés (front) et plan de tests vitest + audits Lighthouse

Architecture & choix techniques (cible):

- Introduire un « RenderGateStore » (Pinia) qui orchestre la disponibilité des blocs critiques: i18nReady, routeReady, dataReady
- Extraire les signaux de readiness des pages: accueil (articles-home-v1 + article-search-v1) et détails (article-details-v1 / legacy)
- Encapsuler en‑tête + contenu principal dans un conteneur synchronisé:
  - Option A (recommandée): Suspense parent avec fallback skeleton unifié; rendre headers-v1 et le composant de route en async (defineAsyncComponent) pour révélation simultanée
  - Option B: Gating via store (v-if/v-show) avec placeholders dédiés; révélation alignée quand RenderGateStore.ready === true
- Déporter le chargement non‑critique du header (menus admin, contact, menus entités, AdSense) en async/lazy après first reveal (requestIdleCallback ou setTimeout court)
- Stabiliser le LCP: précharger police PrimeIcons (déjà fait) et image LCP sur accueil via Unhead (link rel=preload as=image, imagesrcset/imagesizes)<mccoremem id="03fjih8axvt94kceqx45xfa0k" />
- Réduire le CLS: réserver la hauteur du header et des sections via min-height/skeleton, animer uniquement en transform<mccoremem id="01KGSMHVZXMK1S0T8TQ79C8G65" />
- Continuer l’async des sous-composants d’article (article-info-v1, adsense) pour limiter blocage

Options de conception (3 solutions — avantages et inconvénients):

- Solution A — Suspense parent + composants async

  - Description: encapsuler headers‑v1 et le composant de route dans un Suspense parent avec fallback skeleton unifié; rendre headers‑v1 et le composant de page async (defineAsyncComponent). La révélation a lieu quand i18nReady et dataReady sont vrais via RenderGateStore.
  - Avantages:
    - Révélation simultanée réelle header + contenu avec un seul fallback
    - Intégration native au flux asynchrone Vue; moins de code « glue »
    - Améliore LCP/CLS en évitant les réordonnancements visuels
    - Mutualise la logique de chargement et limite la duplication de skeletons
  - Inconvénients:
    - Conversion de headers‑v1 en async ou wrapper dédié
    - Interactions du header indisponibles avant ready (à gérer par UX)
    - Complexité avec keep‑alive/nested Suspense et gestion d’erreurs async
    - Attention aux scripts externes (Adsense) et à leur readiness

- Solution B — Store Pinia RenderGateStore (v‑if/v‑show)

  - Description: contrôler l’affichage de headers‑v1, contenu central et pub via un store (i18nReady, routeReady, dataReady), avec placeholders skeleton dédiés; émission des signaux dans home et détails.
  - Avantages:
    - Contrôle fin, explicite et compatible Mode Vue 3 compat
    - N’exige pas d’asyncifier l’en‑tête; plus simple à raisonner/déboguer
    - Dégrade proprement si les signaux arrivent en décalé
  - Inconvénients:
    - Instrumentation manuelle sur plusieurs composants/pages
    - Risque d’oubli de signaux ou de séquencement entraînant du clignotement
    - Duplication de placeholders et surcoût de maintenance
    - Coordination plus lourde pour garantir simultanéité stricte

- Solution C — Révélation étagée via overlay skeleton + scheduling (idle)
  - Description: afficher un shell minimal du header avec hauteur réservée; placer un overlay skeleton sur la zone centrale; charger sous‑composants lourds (contact, menus entités/admin, adsense) via requestIdleCallback/setTimeout court/IntersectionObserver; retirer overlay quand dataReady && i18nReady.
  - Avantages:
    - Faible intrusion structurale; réduit TBT et améliore perception
    - Conserve accessibilité du header dès T0 avec structure stable
    - Robuste face aux variabilités de latence réseau/API
  - Inconvénients:
    - Scheduling plus complexe (idle/IO/timeout) et orchestration à fiabiliser
    - Risque d’overlay intrusif (focus/a11y) si mal paramétré
    - Soins particuliers sur z‑index/CLS (header, dropdown contact, autocomplete)
    - Coordination nécessaire avec Adsense/consentement pour éviter blocage initial

Choix recommandé:

- Privilégier Solution A pour une simultanéité forte et une réduction nette du CLS, avec Solution B comme alternative si un contrôle explicite par page est requis. Utiliser Solution C pour minimiser les changements structuraux tout en améliorant la perception, notamment sur dispositifs à latence élevée.

Spécifications détaillées (front):

- Store RenderGateStore (Pinia)
  - État: i18nReady:boolean, routeReady:boolean, dataReady:boolean, ready:computed
  - Actions: markI18nReady(), markRouteMounted(routeName), markDataReady(key)
- Accueil ([home.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/core/home/home.vue))
  - Émettre « dataReady » après premier lot d’articles et Ready de la recherche
  - Fallback skeleton global couvrant header + zone centrale
- Détails (V1 et legacy)
  - Émettre « dataReady » une fois article récupéré ([article-details-v1.component.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/entities/article/v1/details/article-details-v1.component.ts), [article-details.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/entities/article/article-details.vue))
  - Unifier markdown affiché et placeholders skeleton (PrimeVue)
- En‑tête ([headers-v1.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/entities/headers/v1/headers-v1.vue))
  - Convertir en composant async (ou charger ses sous-parties en async) et attendre RenderGateStore.ready pour la révélation visuelle simultanée
  - Conserver le loader i18nReady en fallback local si rendu isolé
- App.vue ([app.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/app.vue))
  - Remplacer la composition actuelle par un conteneur Suspense ou une révélation conditionnelle sur RenderGateStore.ready
  - Maintenir keep-alive sur routes ayant besoin de cache

Spécification de configuration:

- Front (Unhead): préchargement des assets critiques (fonts/images) et fetchpriority pour image LCP
- Environnement (optionnel): utiliser CDN_BASE_URL pour assets statiques si activé mappé via Front Door<mccoremem id="03fp4z92wfqlqr8pk88e3lyds|03fp4cvf9vj3h90f98v6i3wei" />
- Terraform CDN: conserver purge post-déploiement et compression des types texte<mccoremem id="03fp4cvf9vj3h90f98v6i3wei|03fp4z92wfqlqr8pk88e3lyds" />

Sécurité & durcissement:

- Aucun secret embarqué; variables d’environnement injectées côté App Service (Key Vault si activé)
- Ne pas bloquer la modale de login ni les menus AuthZ; lazy‑load non‑critique uniquement si authenticated === false
- Respect des invariants Liquibase<mccoremem id="01KGYM6D45WK7MK91Q3W7TTR0P" />

Performance & scalabilité:

- Cibles: LCP < 2.5s (mobile), CLS < 0.1; révélation synchronisée header + contenu
- Lazy de sous-parties (contact/message, menus entités, adsense) après first reveal
- Préloader image LCP accueil via Unhead/vite-imagetools<mccoremem id="03fjih8axvt94kceqx45xfa0k" />
- Continuer caching côté API V1 via fetch join; éviter caches spécifiques V1<mccoremem id="03fe19oiu0zxufuavvq76clzw" />

Observabilité:

- Journaliser temps de montage route et latence dataReady via tracker service
- Métriques Lighthouse et vitest pour vérifier absence de drift CLS

Plan de tests et validation locale:

- Front: exécuter tests unitaires et composants
  - npm run vitest-run<mccoremem id="03fe9uxhbt23xdhqq4pvpuzuq" />
- Backend (si besoin de validation globale): npm run backend:unit:test<mccoremem id="03fe9uxhbt23xdhqq4pvpuzuq" />
- Audits Lighthouse (scripts npm fournis) pour accueil et détails<mccoremem id="01KGRRQ36XE8171D589WCPAC18" />
- Validation visuelle: simultanéité affichage header + contenu avec skeleton fallback

Risques, limites et alternatives:

- Suspense peut nécessiter rendre header async (ou wrapper faux-async); alternative: gating store uniquement
- Lazy excessive du header pourrait impacter accessibilité; garder structure nav visible avec skeleton minimal
- Adsense et consentement: maintenir gating existant et éviter blocage initial<mccoremem id="01KJE35E7NSNA2VR2E9QBD9CGV" />

Paramètres renseignés:

- Nom du sujet: Optimisation du rendu simultané Home et ArticleDetails
- Contexte et objectifs: synchroniser affichage header/contenu; optimiser LCP/CLS
- Hypothèses et contraintes: Vue 3 compat, Unhead, PrimeVue, BootstrapVue; JHipster; CDN optionnel
- Choix techniques: Suspense + RenderGateStore; lazy non‑critique; préchargements Unhead
- Risques et alternatives: Voir section dédiée ci‑dessus
