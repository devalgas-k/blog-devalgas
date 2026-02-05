# Conception technique SEO — Spring Boot + Vue 3

## Contexte et Objectifs

- Améliorer le référencement naturel d’une application Spring Boot (API) + Vue 3 (SPA), sans SSR.
- Objectifs: signal sémantique fort, métadonnées complètes, discovery (sitemap/robots), internationalisation, performance.
- Approche: incrémentale, simple et efficace, avec autocritique à chaque étape.

## Implémentation Réalisée

- Gestion du head avec Unhead (@unhead/vue), html lang dynamique, metas par page.
- Détails article: title/description i18n, canonical sans query, hreflang alternates (fr/en/x‑default), OG/Twitter enrichis, JSON‑LD BlogPosting.
- Endpoint d’image sociale par article: `/api/v1/articles/{id}/og-image` (banner prioritaire, sinon badge, fallback logo).
- Sitemap `/sitemap.xml` consolidé: URLs slug, alternates hreflang et image sitemap, lastmod.
- Robots.txt statique: Disallow ciblés pour routes techniques + directive `Sitemap: /sitemap.xml`.

## Stratégie SEO Progressive

### 1. Métas globales (index.html)

- Solution: définir title et meta description par défaut; robots index/follow; OG/Twitter de base; locale.
- Autocritique:
  - Pro: rapide, couvre la home et le chargement initial.
  - Con: une SPA exige des metas par vue; la description globale peut être sur‑générale.
  - Décision: conserver des valeurs par défaut claires; privilégier metas dynamiques par page.

### 2. Métas dynamiques par page (Vue)

- Solution: lors de la navigation, mettre à jour:
  - title, meta description i18n, canonical (sans query), OG (title/description/url/type/site_name/locale/image), Twitter (card/site/title/description/image/alt).
- Autocritique:
  - Pro: améliore nettement l’extrait et le partage; compatible SPA; Unhead simplifie la maintenabilité.
  - Con: rendu côté client; SSR reste optimal mais non requis pour nos besoins actuels.
  - Décision: adopter Unhead (@unhead/vue) et rester client‑side; SSR/Nuxt reste une option ultérieure.

### 3. JSON‑LD (BlogPosting)

- Solution: injecter script `application/ld+json` avec headline/description/inLanguage/mainEntityOfPage/image/datePublished/author/publisher, isPartOf (Blog) et keywords (dérivés des catégories).
- Autocritique:
  - Pro: renforce la compréhension sémantique; structure les articles pour rich results.
  - Con: nécessite cohérence des champs et URLs images publiques.
  - Décision: adopté; image servie par l’API via endpoint dédié.

### 4. Sitemap XML (Spring Boot)

- Solution: endpoint `/sitemap.xml` listant les articles (Status=COMPLETED) avec URLs slug, `lastmod` = date, et:
  - alternates hreflang (`xhtml:link`) pour FR/EN/x‑default
  - image sitemap (`image:image/loc`) pointant vers `/api/v1/articles/{id}/og-image`
- Autocritique:
  - Pro: discovery fiable; multi‑langue explicite; image découverte.
  - Con: nécessite cohérence de slug FR/EN; géré par slugification depuis les titres.
  - Décision: conservé et audité régulièrement.

### 5. Robots.txt

- Solution: fichier statique avec `Sitemap: /sitemap.xml` et Disallow pour routes techniques (ex.: `/management/`, `/swagger-ui/`, `/v3/api-docs`, `/api/`, `/admin/`, `/error`, `/h2-console/`).
- Autocritique:
  - Pro: standard; guide les crawlers; simple à maintenir.
  - Con: complément des metas; ne contrôle pas l’indexation fine des pages.
  - Décision: conservé en statique.

### 6. Slugs et schéma d’URL

- Solution: exposer `/v1/articles/:id-:slug/view` en plus de `/v1/articles/:id/view`; slugs FR/EN générés par normalisation depuis les titres localisés.
- Autocritique:
  - Pro: lisible, sémantique, meilleur CTR; pas de stockage ni migration de slug.
  - Con: redirections si le titre change; acceptable avec id invariant.
  - Décision: générer slug à la volée (limite 80 caractères), fallback sur route id seule.

### 7. Hreflang (multi‑langue)

- Solution: exposer `<link rel="alternate" hreflang="fr|en|x-default">` sur la page article, avec URLs slug FR/EN.
- Autocritique:
  - Pro: évite duplicate content; oriente correctement les crawlers.
  - Con: nécessite slugs par langue; déjà gérés.
  - Décision: activé sur détail article et dans le sitemap.

### 8. Performance et Sécurité

- Solution: lazy loading images, DOMPurify, CSP, compression serveur; cache des médias via ETag + Cache‑Control.
- Autocritique:
  - Pro: UX/SEO (Core Web Vitals); sécurité renforcée; crawl plus efficace grâce au cache sur og‑image.
  - Con: gestion de cache invalide si média change; acceptable avec ETag basé sur contenu.
  - Décision: conserver sanitization/lazy/CSP; ETag + max‑age (7j image article, 30j logo).

## Détails d’Implémentation

### Front (Vue 3)

- Head management: Unhead (@unhead/vue), html `lang` dynamique via `useHead` dans `main.ts`.
- Accueil: title + meta description i18n depuis `globalV1.seo.description.*`.
- Article:
  - Description: `descriptionFr/En` sinon extrait markdown décodé; fallback i18n si vide.
  - Canonical: basé sur `router.currentRoute.path` (sans query); alternates hreflang FR/EN/x‑default.
  - OG: title/description/url/type/site_name/locale/image (URL absolue).
  - Twitter: card `summary_large_image`, site, title/description, image, image:alt.
  - JSON‑LD: BlogPosting enrichi (keywords depuis catégories, isPartOf Blog).
- Références:
  - article-details-v1.component.ts: metas article, canonical sans query, hreflang, JSON‑LD enrichi.
  - main.ts: Unhead initialisé et mise à jour dynamique de `html[lang]`.

#### Intégration Unhead (Vue 3)

- Dépendance: `@unhead/vue`.
- Initialisation (main.ts):

```ts
import { createApp } from 'vue';
import { createHead } from '@unhead/vue';
import App from './app.vue';
// ...
const app = createApp(App);
const head = createHead();
app.use(head);
// app.use(router).use(pinia).mount('#app');
```

- Accueil (useHead basique):

```ts
import { computed } from 'vue';
import { useHead } from '@unhead/vue';
import { useI18n } from 'vue-i18n';

const { t: t$ } = useI18n();
const title = computed(() => /* logique WRITING_HASH */);
const description = computed(() => /* i18n base/writing */);

useHead({
  title,
  meta: [{ name: 'description', content: description }],
});
```

- Article (canonical sans query, hreflang, OG/Twitter, JSON‑LD):

```ts
import { computed } from 'vue';
import { useHead } from '@unhead/vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();

const langBase = computed(() => (currentLanguage.value ?? 'fr').toString().split('-')[0].toLowerCase());
const canonicalUrl = computed(() => {
  const origin = window.location.origin;
  const path = router.currentRoute.value?.path ?? route.path;
  return origin + path;
});
const ogImage = computed(() => {
  const origin = window.location.origin;
  const id = articleId.value;
  return id ? `${origin}/api/v1/articles/${id}/og-image` : `${origin}/content/images/logo-app.png`;
});
const slugify = (s: string) =>
  (s ?? '')
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-+|-+$/g, '')
    .substring(0, 80);
const altLinks = computed(() => {
  const origin = window.location.origin;
  const id = articleId.value;
  if (!id) return [];
  const frSlug = slugify(articleTitleFr.value);
  const enSlug = slugify(articleTitleEn.value);
  return [
    { rel: 'alternate', hreflang: 'fr', href: `${origin}/v1/articles/${id}-${frSlug}/view` },
    { rel: 'alternate', hreflang: 'en', href: `${origin}/v1/articles/${id}-${enSlug}/view` },
    { rel: 'alternate', hreflang: 'x-default', href: `${origin}/v1/articles/${id}-${frSlug}/view` },
  ];
});

useHead({
  title: articleTitle,
  link: [{ rel: 'canonical', href: canonicalUrl }, ...altLinks.value],
  meta: [
    { name: 'description', content: articleDescription },
    { property: 'og:title', content: articleTitle },
    { property: 'og:description', content: articleDescription },
    { property: 'og:url', content: canonicalUrl },
    { property: 'og:type', content: 'article' },
    { property: 'og:site_name', content: 'Devalgas.net' },
    { property: 'og:locale', content: langBase },
    { property: 'og:image', content: ogImage },
    { name: 'twitter:card', content: 'summary_large_image' },
    { name: 'twitter:site', content: '@devalgas' },
    { name: 'twitter:title', content: articleTitle },
    { name: 'twitter:description', content: articleDescription },
    { name: 'twitter:image', content: ogImage },
    { name: 'twitter:image:alt', content: articleTitle },
  ],
  script: [
    {
      type: 'application/ld+json',
      children: JSON.stringify({
        '@context': 'https://schema.org',
        '@type': 'BlogPosting',
        headline: articleTitle.value,
        description: articleDescription.value,
        inLanguage: langBase.value,
        mainEntityOfPage: canonicalUrl.value,
        datePublished: articleDateIso.value,
        author: { '@type': 'Person', name: 'Devalgas.net' },
        publisher: { '@type': 'Organization', name: 'Devalgas.net' },
        image: ogImage.value,
        isPartOf: { '@type': 'Blog', name: 'Devalgas.net', url: canonicalUrl.value },
        keywords: articleKeywords.value,
      }),
    },
  ],
});
```

- Variables attendues (snippet Article):
- `articleTitleFr` / `articleTitleEn`: titres localisés
- `articleTitle`: titre selon langue courante
- `articleDescription`: description i18n ou extrait markdown
- `articleId`: identifiant de l’article
- `articleDateIso`: date ISO publiée
- `articleKeywords`: liste dérivée des catégories
- `currentLanguage`: langue réactive (provide/inject)

```ts
import { computed } from 'vue';
const articleTitleFr = computed(() => article.value?.labelFr ?? '');
const articleTitleEn = computed(() => article.value?.labelEn ?? '');
const articleTitle = computed(() => (langBase.value === 'fr' ? articleTitleFr.value : articleTitleEn.value));
const articleDescription = computed(() => description.value);
const articleId = computed(() => article.value?.id);
const articleDateIso = computed(() => (article.value?.date ? new Date(article.value.date).toISOString() : undefined));
const articleKeywords = computed(() =>
  (article.value?.categoryArticles ?? [])
    .map(c => c?.code || c?.label)
    .filter(Boolean)
    .join(', '),
);
```

- Points de cohérence:
  - i18n: valeurs `computed` réactives; Unhead met à jour le `<head>` sur changement.
  - Canonical: s’appuie sur des URLs stables (id/slug).
  - JSON‑LD: structure BlogPosting conforme; dépend de la qualité des données exposées par l’API.

### Back (Spring Boot)

- `/sitemap.xml`: URLs article avec slug; `lastmod` = date; alternates hreflang; image sitemap = `/api/v1/articles/{id}/og-image`.
- Image OG/Twitter: endpoint public `/api/v1/articles/{id}/og-image` (banner prioritaire, sinon badge; fallback logo); cache public via ETag+Cache‑Control.
- Sécurité: GET `/api/v1/articles/**` autorisé; endpoints techniques protégés ou désindexés par robots.txt.
- Robots.txt: fichier statique avec Disallow ciblés et `Sitemap: /sitemap.xml`.

## Tests et Validation

- Front: Vitest pour injection metas/JSON‑LD via Unhead (mock document.head), vérification i18n, canonical sans query et alternates hreflang.
- Back: tests MVC pour `/sitemap.xml` (status 200, structure XML, alternates/image par URL); tests pour `/api/v1/articles/{id}/og-image` (banner/badge/fallback, ETag).
- Manuels: partage OG/Twitter via debugger (Facebook, Twitter), Search Console pour sitemap.

## Plan de Déploiement

- Étape 1: metas dynamiques accueil + article via Unhead, JSON‑LD, image OG publique.
- Étape 2: sitemap.xml enrichi + robots.txt statique.
- Étape 3: slugs FR/EN côté routes; hreflang activé; optimisation continue.
- Monitoring: Search Console, Core Web Vitals, logs de crawl.

## Bonnes pratiques de validation SEO

- Head DOM: vérifier title, meta description, canonical sans query, liens alternates hreflang (fr/en/x‑default), OG (title/description/url/type/site_name/locale/image), Twitter (card/site/title/description/image/alt), présence d’un JSON‑LD valide.
- Validateurs: utiliser Schema.org Markup Validator (BlogPosting), Facebook Sharing Debugger, Twitter Card Validator et LinkedIn Post Inspector pour contrôler rendu et image.
- Endpoint og‑image: contrôler Content‑Type, disponibilité, taille raisonnable; vérifier ETag et Cache‑Control; s’assurer du fallback logo si aucune image d’article.
- Sitemap: vérifier les namespaces (xhtml, image), lastmod, URLs slug, alternates hreflang et image:loc; soumettre dans Search Console et suivre l’état d’indexation.
- Robots.txt: presence de `Sitemap: /sitemap.xml`; Disallow des routes techniques; confirmer qu’aucune page utile n’est bloquée.
- Performance: auditer Lighthouse (LCP/CLS/INP), lazy loading des images, poids des médias; valider mobile‑friendly.
- Monitoring: Search Console (Coverage, Enhancements, Page Indexing), logs de crawl, rapports sociaux (Twitter/Facebook) pour détecter des erreurs récurrentes.

## Erreurs fréquentes et remèdes

- Canonical avec query/hash:
  - Effet: duplication d’URLs et indexation multiple
  - Remède: utiliser `router.currentRoute.path` et générer `canonical = origin + path`
- og:image non exploitable:
  - Effet: aperçu social sans image ou image cassée
  - Causes: Data URI, URL relative, ressource privée
  - Remède: utiliser l’endpoint public `/api/v1/articles/{id}/og-image` (Content‑Type correct, ETag/Cache‑Control), fallback logo
- JSON‑LD invalide/incomplet:
  - Effet: rejet par les validateurs / ignoré par les moteurs
  - Remède: JSON valide, BlogPosting complet (headline/description/inLanguage/mainEntityOfPage/image/datePublished/author/publisher), keywords non vides, image en URL absolue
- hreflang absent ou incohérent:
  - Effet: duplicate content entre FR/EN
  - Remède: liens alternates `fr|en|x-default` côté page et dans le sitemap; slugs FR/EN cohérents
- Sitemap incomplet ou mal formé:
  - Effet: avertissements Search Console, crawl inefficace
  - Remède: namespaces `xhtml` et `image`, `lastmod`, `image:loc` vers og‑image, baseUrl correct, inclure seulement `Status=COMPLETED`
- Robots.txt trop agressif:
  - Effet: pages utiles non crawlées
  - Remède: Disallow uniquement routes techniques; garder `Sitemap: /sitemap.xml`
- Métas en double ou non mises à jour:
  - Effet: head incohérent, interprétation incertaine
  - Remède: Unhead, metas uniques par vue, computed réactifs
- og:url vs canonical divergents:
  - Effet: signaux contradictoires
  - Remède: aligner `og:url` sur la canonical (éviter query si possible)
- Images trop petites ou au mauvais format:
  - Effet: rendu social dégradé
  - Remède: privilégier une bannière ≥ 1200×630 (ratio ~1.91:1); s’assurer que l’endpoint sert l’original avec bon `Content‑Type`

## Autocritique Globale

- Sans SSR, l’exécution JS par les crawlers est généralement suffisante; SSR resterait mieux pour metas “au premier paint”.
- Gestion manuelle des metas est simple mais verbeuse; Unhead (@unhead/vue) améliore la maintenabilité.
- JSON‑LD bienvenu, mais dépend de la qualité des données (titre/description/image/date).
- Sitemap fournit un boost de discovery, slugs un gain de CTR.

## Checklist d’Implémentation (à suivre)

- [ ] Accueil: title + description i18n dynamiques
- [ ] Article: metas dynamiques + canonical + JSON‑LD
- [ ] API image OG/Twitter
- [ ] `/sitemap.xml` Spring Boot + robots.txt
- [ ] Slugs côté modèle et routes
- [ ] Tests (Vitest/MVC) + validation outillage (debuggers/console)

## Checklist avant déploiement (rapide)

- Head DOM: title/description présents; canonical sans query; hreflang fr/en/x‑default; OG/Twitter complets; JSON‑LD valide
- og‑image: endpoint retourne une image publique (Content‑Type correct), ETag/Cache‑Control; fallback logo opérationnel
- Sitemap: accessible, namespaces xhtml/image corrects, `lastmod` cohérent, alternates hreflang et `image:loc` présents
- Robots.txt: contient `Sitemap: /sitemap.xml`; Disallow uniquement routes techniques; aucune page utile bloquée
- Performance: Lighthouse OK (LCP/CLS/INP); images optimisées; lazy loading; mobile‑friendly validé
- Sécurité: CSP appliquée; sanitization DOMPurify pour markdown; GET `/api/v1/articles/**` public; endpoints sensibles protégés
- Search Console: sitemap soumis; couverture sans erreurs majeures; outils sociaux (Facebook/Twitter/LinkedIn) valident l’aperçu

## Critères objectifs Lighthouse (Core Web Vitals)

- LCP (Largest Contentful Paint): ≤ 2,5 s au 75e percentile (mobile)
- CLS (Cumulative Layout Shift): ≤ 0,1
- INP (Interaction to Next Paint): ≤ 200 ms
- Performance (mobile): score ≥ 90
- SEO: score ≥ 90; vérifier metas essentielles (title, description, canonical)
- Accessibilité: score ≥ 90; contrastes, labels, focus visibles
- Bonnes pratiques: score ≥ 95; https, tailles images, évitement vulnérabilités connues

## Mesure Lighthouse (mode opératoire)

- Préconditions: serveur local stable, pages accessibles, images servies correctement (og‑image/public), mesure en navigation anonyme.
- Profil mobile: utiliser preset mobile avec throttling réseau/CPU (RTT 150 ms, 1638 Kbps down/780 Kbps up, CPU ×4).
- Pages à mesurer: accueil `/`, au moins un article FR et EN via route slug `/v1/articles/{id}-{slug}/view`.
- Procédure locale (3 passes → médiane):

```bash
npx lighthouse http://localhost:8080/ \
  --only-categories=performance,seo,accessibility \
  --throttling.rttMs=150 \
  --throttling.throughputKbps=1638 \
  --throttling.requestLatencyMs=150 \
  --throttling.downloadThroughputKbps=1638 \
  --throttling.uploadThroughputKbps=780 \
  --throttling.cpuSlowdownMultiplier=4 \
  --chrome-flags="--incognito --disable-extensions" \
  --output html \
  --output json \
  --output-path ./lighthouse/report-mobile
```

- Interprétation: relever LCP/CLS/INP et les scores “Performance/SEO/Accessibilité/Best Practices”; viser les seuils définis ci‑dessus; retenir la médiane des 3 runs.
- CI (optionnel): utiliser `@lhci/cli` avec `autorun` (mobile par défaut; utiliser `--preset=desktop` pour desktop) et assertions pour garantir les seuils; collecter sur `/` et une page article FR/EN.

#### Script npm local (plus simple)

- Exécuter:

```bash
npm run lighthouse:local
```

- Variante backend (jar sur 8080):

```bash
npm run lighthouse:local:8080
```

- Les rapports sont générés dans `./lighthouse/` et ouverts automatiquement (`--view`).

### Intégration CI minimale (LHCI)

- Fichier de config: `.lighthouseci/config.json` (assertions et preset mobile inclus).
- Script: `npm run lhci:autorun` (ou `npm run lhci:optional` pour ne pas casser la CI).
- URLs mesurées: par défaut `/`; sur la CI, passer des URLs supplémentaires via variable d’environnement:

```bash
LHCI_COLLECT__URL='["http://localhost:8080/","http://localhost:8080/v1/articles/ID-SLUG/view"]' \
  npm run lhci:autorun
```

- Démarrage serveur: lancer l’application avant l’audit (ex.: étape existante de démarrage jar, puis `npm run lhci:autorun`). Conserver le port 8080 ou adapter les URLs.
