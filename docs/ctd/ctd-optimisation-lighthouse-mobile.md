# Conception technique — Optimisation Lighthouse mobile (Home SPA)

## Résumé exécutif

- Contexte
  - Le rapport Lighthouse mobile sur la home (http://localhost:9000/) montre des Web Vitals très dégradés: FCP ≈ 23,2 s, LCP ≈ 44,6 s, TTI ≈ 44,6 s, CLS ≈ 0,552, malgré un TTFB très bon (≈ 30 ms) et un TBT faible (≈ 130 ms).
- Objectifs
  - Ramener LCP ≤ 2,5 s, CLS ≤ 0,1, TBT ≤ 200 ms, Perf ≥ 90.
  - Stabiliser l’élément LCP et réduire le JS initial pour accélérer la découverte/affichage.
- Portée
  - Page d’accueil SPA, chemin critique de rendu, assets statiques (images, CSS), configuration build/front.
- Non‑objectifs
  - Refactor complet de l’architecture applicative; migration de stack côté backend.

## Synthèse web (desktop)

- Contexte
  - Rapport web sur la home (http://localhost:9000/) en mode dev (Vite). Score Performance ≈ 48.
- Web Vitals
  - FCP ≈ 3,8 s; LCP ≈ 8 s; TTI ≈ 8 s; TBT ≈ 12 ms; CLS ≈ 0,176; TTFB ≈ 228 ms.
- Élément LCP
  - Identifié comme l’image “À propos” [about.jpg](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/content/images/about.jpg), chemin: `div.row > div#about > picture.about__picture > img.about__image`.
- Phases LCP (insight)
  - TTFB ≈ 246 ms; Resource load delay ≈ 807 ms; Resource load duration ≈ 3 ms; Element render delay ≈ 47 ms.
- Implications
  - TTFB et TBT excellents; LCP dominé par la découverte/chargement tardifs de l’image LCP et la mise en place des ressources dev. Les recommandations mobile s’appliquent: `preload` de l’image LCP, passage en WebP/AVIF, `preconnect` des fonts, code‑splitting des lib lourdes.
- Notes dev
  - Les signaux “unminified/unused JS/CSS” et “text compression” sont attendus en dev; à valider en build prod.

## Constat et diagnostic

- Élément LCP
  - LCP identifié comme l’image de la section “À propos”: [about.jpg](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/content/images/about.jpg) via `img.about__image` dans `picture.about__picture`.
  - Chaîne de découverte: l’image est découverte tard via le runtime (Vue/chunks), pas par le parser HTML.
  - Phases LCP: TTFB ≈ 451 ms, Load Delay ≈ 20,4 s, Render Delay ≈ 23,7 s → le bloc LCP dépend de gros bundles JS avant d’être connu/rendu.
- Poids et parsing JS au premier écran
  - Unused/Unminified JS: bootstrap‑vue (~1,7 MiB), Vue compat (~607 KiB), markdown‑it/katex (~232 KiB), pinia/devtools, sockjs/rx‑stomp, vue‑i18n, primevue/autocomplete, etc. → gros chunk initial, parsé avant l’affichage du hero.
  - Compression HTTP désactivée en dev: “Enable text compression” signale ≈ 5,0 MiB compressibles. En prod, Brotli/Gzip supprimeront ce signal.
- CLS
  - ≈ 0,552 avec 15 grands shifts: insertion tardive de blocs et réorganisation du DOM au montage/hydratation SPA (images dimensionnées correctement, mais sections et polices/ICONS peuvent déplacer le contenu).
- Ressources bloquantes
  - CSS de “loading” (≈ 150 ms) signalé; impact limité mais à optimiser (inline ou defer).

## Architecture & choix techniques

- Stabilisation de l’élément LCP
  - Découverte par le parser: précharger la ressource LCP en `link[rel=preload][as=image]` dès le `<head>` et rendre le hero en HTML accessible sans dépendance lourde au runtime (SSR/island pour le bloc hero ou injection statique contrôlée).
  - Format moderne: convertir `about.jpg` en WebP/AVIF, garder fallback si nécessaire.
  - Cache long: servir l’image LCP avec cache agressif en prod.
- Réduction du JS initial (code‑splitting)
  - Charger dynamiquement markdown‑it/katex, primevue/autocomplete, sockjs/rx‑stomp, devtools Pinia, modules i18n lourds seulement sur les routes qui les utilisent.
  - Réduire/retirer `@vue/compat` si possible (préférer Vue 3 native) pour diminuer le chunk principal.
- Performance d’assets
  - Activer minification et compression HTTP (Brotli/Gzip) en prod (serveur/App Service ou reverse‑proxy).
  - Preconnect/prefetch Google Fonts; `font-display: swap` pour éviter les reflows tardifs.
- CLS
  - Réserver l’espace des blocs dynamiques via CSS (hauteurs/containers), éviter l’insertion au-dessus de contenu déjà affiché; vérifier icônes/polices.
- CSS critique
  - Inline du CSS “loading” critique sur la home; defer le reste.

## Interfaces & contrats

- Head/meta (SPA)
  - Gestion via Unhead (`useHead`): ajouter `link[rel=preload]` pour l’image LCP et `preconnect` fonts.
  - Canonical/meta inchangés; s’assurer que les tags ne bloquent pas le rendu.

## Spécification de configuration

- Build/front
  - Minifier JS/CSS en prod (configuration par défaut Vite/Maven).
  - Générer assets `.br`/`.gz` et configurer leur service par le serveur (selon App Service/NGINX).
- Fonts
  - `preconnect` vers `https://fonts.googleapis.com` et `https://fonts.gstatic.com`; `font-display: swap`.
- Images
  - Conversion `about.jpg` → `about.webp` + `link rel=preload` + `<picture>` avec source WebP prioritaire.

## Sécurité & durcissement

- Aucune modification sensible; ne pas committer de secrets; compression côté serveur sans exposer headers sensibles.

## Performance & scalabilité

- KPI cibles
  - LCP ≤ 2,5 s; CLS ≤ 0,1; TBT ≤ 200 ms; Perf ≥ 90; SEO ≥ 90; A11y ≥ 90; Best Practices ≥ 95.
- Approche
  - Déplacer hors chemin critique les libs lourdes; réduire le nombre de requêtes, compresser et précharger les assets critiques.

## Observabilité

- Lancer LHCI (scripts npm du projet) après build prod; conserver rapports pour suivi.
- Ajouter métriques Web Vitals côté front (optionnel) pour observer LCP/CLS en réel.

## Plan de tests et validation

- Local
  - `npm run webapp:build` (prod), puis `npm run lhci:autorun` pour valider seuils.
  - Lighthouse mobile sur / (home) et sous‑pages lourdes (écrans avec markdown/katex, recherche/autocomplete).
- Scénarios
  - LCP image préchargée: vérifier que la ressource apparaît dans le waterfall avant les gros chunks JS.
  - CLS: vérifier absence de grands shifts; auditer “layout-shifts” détaillés.
  - Compression: vérifier `content-encoding: br/gzip` sur assets.

## Fichiers à créer/modifier

- Front (home, head et assets)
  - [home.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/core/home/home.vue): ajouter `useHead` pour `preload` LCP + `preconnect` fonts; réserver espace des sections.
  - [about.jpg → about.webp](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/content/images/about.jpg): convertir en WebP, mettre à jour `<picture>`.
  - [vite.config.mts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/vite.config.mts): s’assurer minification prod; optionnel: génération `.br/.gz`.
  - Composants lourds (routes concernées): charger en `import()` dynamique markdown‑it/katex, primevue/autocomplete, sockjs/rx‑stomp, devtools Pinia.
  - [content/css/loading.css](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/content/css/loading.css): inline critique sur la home.

## Patchs proposés (extraits indicatifs)

- Preload LCP + preconnect fonts (via Unhead dans home.vue)

```ts
import { useHead } from '@unhead/vue';

useHead({
  link: [
    { rel: 'preload', as: 'image', href: '/content/images/about.webp' },
    { rel: 'preconnect', href: 'https://fonts.googleapis.com' },
    { rel: 'preconnect', href: 'https://fonts.gstatic.com', crossorigin: '' },
  ],
});
```

- `<picture>` avec WebP prioritaire

```html
<picture class="about__picture">
  <source srcset="/content/images/about.webp" type="image/webp" />
  <img class="about__image" src="/content/images/about.jpg" alt="background" width="542" height="115" />
</picture>
```

- Code‑splitting (exemple)

```ts
const MarkdownIt = () => import('markdown-it');
const Katex = () => import('markdown-it-katex');
```

## Risques, limites et alternatives

- SSR/islands
  - Améliore la découverte LCP par le parser; coût d’intégration à évaluer (stack actuelle SPA).
- Compatibilité Vue
  - Retrait de `@vue/compat` peut nécessiter adaptation si du code legacy 2.x subsiste.
- Compression
  - Activée en prod uniquement; en dev, les signaux “unminified/compression” sont attendus.

## Annexes

- Éléments Lighthouse du rapport
  - FCP: ≈ 23,2 s; LCP: ≈ 44,6 s; TTI: ≈ 44,6 s; TBT: ≈ 130 ms; CLS: ≈ 0,552; TTFB: ≈ 30 ms.
- Références
  - LCP element: `div.row > div#about > picture.about__picture > img.about__image`
  - LHCI: scripts npm configurés pour l’autorun et seuils Core Web Vitals.
  - Web (desktop)
    - Score Performance ≈ 48 ([report-web.report.json](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/lighthouse/report-web.report.json#L27567-L27568)).
    - FCP ≈ 3,9 s ([report-web.report.json:L36-L49](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/lighthouse/report-web.report.json#L36-L49)).
    - LCP ≈ 8,0 s ([report-web.report.json:L50-L63](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/lighthouse/report-web.report.json#L50-L63)).
    - Speed Index ≈ 4,0 s ([report-web.report.json:L72-L83](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/lighthouse/report-web.report.json#L72-L83)).
    - TBT ≈ 20 ms ([report-web.report.json:L151-L164](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/lighthouse/report-web.report.json#L151-L164)).
    - TTI ≈ 8,0 s ([report-web.report.json:L239-L248](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/lighthouse/report-web.report.json#L239-L248)).
    - CLS ≈ 0,177 ([report-web.report.json:L175-L183](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/lighthouse/report-web.report.json#L175-L183)).
    - TTFB ≈ 10 ms ([report-web.report.json:L202-L210](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/lighthouse/report-web.report.json#L202-L210)).
    - Phases LCP (TTFB/resource delay/load/render): 13,7 ms / 585,8 ms / 2,3 ms / 45,5 ms ([report-web.report.json:L25340-L25359](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/lighthouse/report-web.report.json#L25340-L25359)).
    - Élément LCP confirmé: `div.row > div#about > picture.about__picture > img.about__image` ([report-web.report.json:L25365-L25377](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/lighthouse/report-web.report.json#L25365-L25377)).
