# Conception Technique Détaillée (CTD) — Intégration Google AdSense (V1)

## 1. Résumé exécutif

- Contexte: monétiser des zones clés du site via Google AdSense sur une SPA Vue 3.
- Objectifs: injecter le script AdSense une seule fois, remplacer les placeholders « google ad » par des composants configurés, garantir UX responsive et CLS minimal, préparer la configurabilité par environnement, définir tests et validations.
- Portée: intégration manuelle AdSense dans les emplacements présents dans [app.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/app.vue). Pas d’Auto Ads.
- Non‑objectifs: gestion de réseau Ad Manager, optimisation d’inventaire publicitaire avancée, SSR.

## 2. Architecture & choix techniques

- Composants: composant interne [Adsense](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/core/adsense/adsense.vue) + [adsense.component.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/core/adsense/adsense.component.ts) pour rendre `<ins.adsbygoogle>` et déclencher `adsbygoogle.push`.
- Head management: injection unique du script AdSense via Unhead (`useHead`) dans [main.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/main.ts) avec `crossorigin: 'anonymous'`.
- Configurabilité: constants Vite `define` exposées globalement (client, flag d’activation et slots par emplacement) déclarées dans [declarations.d.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/declarations.d.ts).
- Layout & rendu: mapping des formats par emplacement; utilisation de `format="auto"` et `:responsive="true"` pour adapter aux conteneurs Bootstrap; réservation de hauteur pour minimiser le CLS.
- Décisions: manuel par emplacement (pas Auto Ads) pour contrôler densité, formats et UX; un seul chargement du script; préserver la stabilité en navigation SPA (clé stable sur le composant).
- Garde‑fous de rendu: ne pas rendre d’annonce si `ADSENSE_ENABLED` est faux, si le consentement est absent, si le script est indisponible (`adsenseScriptReady`), ou si le slot de l’emplacement n’est pas valide (`slotTopValid/slotLeftValid/slotRightValid/slotFooterValid`, exclus les placeholders commençant par `000000`); replier le conteneur en cas de no‑fill.

## 3. Modèle de domaine et données

- Front uniquement. Pas de modèle métier. Données: variables d’environnement lues par Vite pour `ADSENSE_CLIENT` et quatre slots par emplacement (TOP, SIDEBAR_LEFT, SIDEBAR_RIGHT, FOOTER).

## 4. Interfaces & contrats

- Contrat du composant Adsense:
  - Props: `client: string`, `adSlot: string`, `format: string`, `responsive: boolean`, `style: string`, `npa: boolean`, `test: boolean`, `layout: string`, `adLayoutKey: string`.
  - Comportement: réserve la hauteur via `style`, rend `<ins class="adsbygoogle">`, déclenche `adsbygoogle.push({})` en `mounted`, option NPA si consentement absent, `data-adtest="on"` en dev.

## 5. Spécification de configuration

- Variables d’environnement (exemples à définir côté CI/CD ou `.env`):
  - `ADSENSE_CLIENT`: ID éditeur (ex. `ca-pub-6181972205565553`).
  - `ADSENSE_SLOT_TOP`, `ADSENSE_SLOT_SIDEBAR_LEFT`, `ADSENSE_SLOT_SIDEBAR_RIGHT`, `ADSENSE_SLOT_FOOTER`: ad unit IDs réels par emplacement.
  - `ADSENSE_ENABLED`: activer/désactiver globalement l’affichage des annonces (`true` par défaut).
- Vite `define` dans [vite.config.mts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/vite.config.mts):

```ts
define: {
  ADSENSE_CLIENT: `"${env.ADSENSE_CLIENT ? env.ADSENSE_CLIENT : 'ca-pub-6181972205565553'}"`,
  // Normalisation booléenne avec défaut true
  // const ADSENSE_ENABLED_VAL = env.ADSENSE_ENABLED ? env.ADSENSE_ENABLED === 'true' : true;
  ADSENSE_ENABLED: ADSENSE_ENABLED_VAL,
  ADSENSE_SLOT_TOP: `"${env.ADSENSE_SLOT_TOP ? env.ADSENSE_SLOT_TOP : '0000000001'}"`,
  ADSENSE_SLOT_SIDEBAR_LEFT: `"${env.ADSENSE_SLOT_SIDEBAR_LEFT ? env.ADSENSE_SLOT_SIDEBAR_LEFT : '0000000002'}"`,
  ADSENSE_SLOT_SIDEBAR_RIGHT: `"${env.ADSENSE_SLOT_SIDEBAR_RIGHT ? env.ADSENSE_SLOT_SIDEBAR_RIGHT : '0000000003'}"`,
  ADSENSE_SLOT_FOOTER: `"${env.ADSENSE_SLOT_FOOTER ? env.ADSENSE_SLOT_FOOTER : '0000000004'}"`,
}
```

- Déclarations TypeScript dans [declarations.d.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/declarations.d.ts):

```ts
declare const ADSENSE_ENABLED: boolean;
declare const ADSENSE_SLOT_TOP: string;
declare const ADSENSE_SLOT_SIDEBAR_LEFT: string;
declare const ADSENSE_SLOT_SIDEBAR_RIGHT: string;
declare const ADSENSE_SLOT_FOOTER: string;
```

## 6. Sécurité & conformité

- Consentement utilisateur: intégrer/valider CMP TCF v2 avant affichage; activer NPA si consentement absent.
- Données: aucune PII collectée par l’intégration; respecter les politiques AdSense sur densité et emplacement; bannir rendus dans des conteneurs invisibles.
- Script: charger avec `crossorigin: 'anonymous'`; une seule injection globale.
- `ads.txt`: vérifier cohérence avec l’éditeur et le domaine public.

## 7. Performance & UX

- CLS minimal: réserver hauteur par emplacement (top 120px, sidebars 600px ou 250px, footer 280px).
- Responsive: formats auto, `width:100%`, conteneurs Bootstrap `d-none d-lg-flex` pour cibler desktop si nécessaire.
- SPA: clé stable sur le composant pour éviter remount; ne re‑pusher que si slot/format changent; possibilité d’IntersectionObserver pour différer `push` à l’entrée dans le viewport.
- Mobile (optionnel): ajouter une bannière dédiée 320×100 (fallback 320×50) et réserver 100px de hauteur; afficher seulement avec consentement.
- Visibilité & gating: rendre l’annonce uniquement si visible et consentement acquis; conditionner le rendu à `ADSENSE_ENABLED && consentGiven && adsenseScriptReady && slotXValid` pour éviter `push` sur un conteneur caché, un script indisponible ou un slot placeholder.
- Repli automatique: si aucune annonce n’est servie après un délai raisonnable (no‑fill), réduire la hauteur du conteneur à 0 et ne pas afficher de placeholder intrusif.

## 8. Observabilité

- Logs de chargement en dev: surveiller erreurs console AdSense et la présence de `adsbygoogle.push`.
- Audit Lighthouse: contrôler CLS, éviter reflows/repositionnements.
- Suivi de remplissage: observer le taux de fill et erreurs réseau AdSense.

## 9. Plan de tests et validation

- Unitaires (Vitest):
  - Scénario activé: monter [app.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/app.vue) avec `ADSENSE_ENABLED=true` et consentement vrai; vérifier la présence de 4 instances `<ins class="adsbygoogle">`.
  - Scénario désactivé/consentement absent: monter avec `ADSENSE_ENABLED=false` ou consentement faux; vérifier qu’il n’y a aucune instance `<ins class="adsbygoogle">`.
  - Scénario no‑fill simulé: espionner le composant pour forcer une hauteur de `<ins>` à 0 et vérifier le repli du conteneur (hauteur réduite à 0 ou non rendu).
  - Vérifier l’injection du script AdSense dans le head via Unhead.
  - Vérifier que les styles réservent la hauteur (90/600/250/280).
- Manuels:
  - Démarrer en dev et vérifier l’absence d’erreurs console liées à adsbygoogle.
  - Auditer Lighthouse (CLS) et vérifier que les conteneurs évitent les sauts de mise en page.
  - Valider le remplissage des annonces une fois les slots AdSense réels configurés.

## 10. Risques, limites et alternatives

- Non‑remplissage: slots non avalisés ou inventaire insuffisant; prévoir fallback visuel avec placeholder neutre réservé.
- Formats non adaptés: ajuster largeur des sidebars si besoin pour 300px; sinon privilégier 160×600.
- Consentement: bloquer rendu sans consentement; activer NPA.
- Alternatives: Auto Ads pour simplicité; Ad Manager pour contrôle avancé (hors portée V1).

## 11. Annexes

- Références:
  - Composant: [adsense.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/core/adsense/adsense.vue), [adsense.component.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/core/adsense/adsense.component.ts)
  - Entrée: [main.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/main.ts)
  - Config: [vite.config.mts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/vite.config.mts)
  - Constantes: [declarations.d.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/declarations.d.ts)
  - Tailles d’annonces display: https://support.google.com/google-ads/answer/1722096

---

### Spécifications et patchs proposés

#### Injection du script AdSense (main.ts)

- Fichier: [main.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/main.ts)
- Bloc prêt à l’emploi:

```ts
useHead({
  script: [
    {
      src: `https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=${ADSENSE_CLIENT}`,
      async: true,
      crossorigin: 'anonymous',
    },
  ],
});
// Détection de la disponibilité du script et provisioning du gating
const consentRef = ref(true);
const adsenseReady = ref(false);
const ensureAdsenseReady = () => {
  const w = window as any;
  if (w.adsbygoogle && typeof w.adsbygoogle.push === 'function') {
    adsenseReady.value = true;
  } else {
    setTimeout(ensureAdsenseReady, 300);
  }
};
ensureAdsenseReady();
provide(
  'consentGiven',
  computed(() => consentRef.value),
);
provide(
  'adsenseScriptReady',
  computed(() => adsenseReady.value),
);
```

#### Définition des constantes (vite.config.mts)

- Fichier: [vite.config.mts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/vite.config.mts)
- Bloc prêt à l’emploi:

```ts
define: {
  ADSENSE_CLIENT: `"${env.ADSENSE_CLIENT ? env.ADSENSE_CLIENT : 'ca-pub-6181972205565553'}"`,
  // ADSENSE_ENABLED normalisé (string env -> boolean)
  // const ADSENSE_ENABLED_VAL = env.ADSENSE_ENABLED ? env.ADSENSE_ENABLED === 'true' : true;
  ADSENSE_ENABLED: ADSENSE_ENABLED_VAL,
  ADSENSE_SLOT_TOP: `"${env.ADSENSE_SLOT_TOP ? env.ADSENSE_SLOT_TOP : '0000000001'}"`,
  ADSENSE_SLOT_SIDEBAR_LEFT: `"${env.ADSENSE_SLOT_SIDEBAR_LEFT ? env.ADSENSE_SLOT_SIDEBAR_LEFT : '0000000002'}"`,
  ADSENSE_SLOT_SIDEBAR_RIGHT: `"${env.ADSENSE_SLOT_SIDEBAR_RIGHT ? env.ADSENSE_SLOT_SIDEBAR_RIGHT : '0000000003'}"`,
  ADSENSE_SLOT_FOOTER: `"${env.ADSENSE_SLOT_FOOTER ? env.ADSENSE_SLOT_FOOTER : '0000000004'}"`,
}
```

#### Déclarations TypeScript (declarations.d.ts)

- Fichier: [declarations.d.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/declarations.d.ts)
- Bloc prêt à l’emploi:

```ts
declare const ADSENSE_ENABLED: boolean;
declare const ADSENSE_SLOT_TOP: string;
declare const ADSENSE_SLOT_SIDEBAR_LEFT: string;
declare const ADSENSE_SLOT_SIDEBAR_RIGHT: string;
declare const ADSENSE_SLOT_FOOTER: string;
```

#### Remplacement des placeholders dans app.vue

- Fichier: [app.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/app.vue)
- Contexte: layout desktop `col-lg-2 | col-12 col-lg-8 | col-lg-2`.
- Emplacements et blocs:

1. Après la navbar (desktop, dans `col-lg-8`, réserver 90px)

```vue
<div v-if="ADSENSE_ENABLED && consentGiven && adsenseScriptReady && slotTopValid" class="d-none d-lg-flex">
  <adsense :ad-slot="ADSENSE_SLOT_TOP" format="auto" :responsive="true" style="display:block;width:100%;min-height:90px" />
</div>
```

2. Sidebar gauche (desktop, réserver 600px)

```vue
<div v-if="ADSENSE_ENABLED && consentGiven && adsenseScriptReady && slotLeftValid" class="d-none d-lg-flex">
  <adsense :ad-slot="ADSENSE_SLOT_SIDEBAR_LEFT" format="auto" :responsive="true" style="display:block;width:100%;min-height:600px" />
</div>
```

3. Sidebar droite (desktop, réserver 250px ou 600px selon format)

```vue
<div v-if="ADSENSE_ENABLED && consentGiven && adsenseScriptReady && slotRightValid" class="d-none d-lg-flex">
  <adsense :ad-slot="ADSENSE_SLOT_SIDEBAR_RIGHT" format="auto" :responsive="true" style="display:block;width:100%;min-height:250px" />
</div>
```

4. Bandeau avant footer (desktop, réserver 280px)

```vue
<div v-if="ADSENSE_ENABLED && consentGiven && adsenseScriptReady && slotFooterValid" class="d-none d-lg-flex">
  <adsense :ad-slot="ADSENSE_SLOT_FOOTER" format="auto" :responsive="true" style="display:block;width:100%;min-height:280px" />
</div>
```

Variant avec gating visibilité + consentement (desktop uniquement) et NPA

```vue
<div v-if="ADSENSE_ENABLED && isDesktop && consentGranted">
  <adsense :ad-slot="ADSENSE_SLOT_TOP" format="auto" :responsive="true" style="display:block;width:100%;min-height:120px" :npa="!consentGranted" />
</div>
```

#### Extensions recommandées (optionnelles)

- Mobile: ajouter une bannière dédiée 320×100 (fallback 320×50) en haut/bas d’écran; réserver `min-height:100px`; afficher uniquement avec consentement.
- In‑article/multiplex: en fin d’article si disponible côté compte.
- `ads.txt`: vérifier les fichiers `src/main/webapp/ads.txt` et `src/main/resources/static/ads.txt` avec `google.com, pub-6181972205565553, DIRECT, f08c47fec0942fa0`.

### CI/CD & provisioning

- Workflow: export des variables ADSENSE\_\* lors du build frontend pour que Vite les prenne en compte dans [gitops.yml](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/.github/workflows/gitops.yml#L590-L598).
- Terraform (optionnel): gestion des variables GitHub Actions via provider `integrations/github` et création des variables `ADSENSE_ENABLED`, `ADSENSE_CLIENT`, `ADSENSE_SLOT_*` pour assurer un provisioning idempotent.

### Liste des fichiers à créer/modifier

- Modifier: [src/main/webapp/app/main.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/main.ts)
- Modifier: [src/main/webapp/app/app.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/app.vue)
- Modifier: [vite.config.mts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/vite.config.mts)
- Modifier: [src/main/webapp/app/declarations.d.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/declarations.d.ts)

---

### Ajustements pour réduire les risques (optionnels)

#### Borne d’attente du script dans adsense.component.ts

```ts
mounted() {
  const w = window as any;
  let attempts = 0;
  const maxAttempts = 40;
  const push = () => {
    if (w.adsbygoogle && typeof w.adsbygoogle.push === 'function') {
      if (this.isNpa) {
        w.adsbygoogle.requestNonPersonalizedAds = 1;
      }
      w.adsbygoogle.push({});
    }
  };
  const wait = () => {
    if (w.adsbygoogle && typeof w.adsbygoogle.push === 'function') {
      push();
    } else if (attempts++ < maxAttempts) {
      setTimeout(wait, 300);
    }
  };
  wait();
}
```

#### Déclenchement au viewport via IntersectionObserver

```ts
mounted() {
  const el = this.$el.querySelector('ins.adsbygoogle') as HTMLElement;
  const w = window as any;
  const push = () => {
    if (w.adsbygoogle && typeof w.adsbygoogle.push === 'function') {
      if (this.isNpa) {
        w.adsbygoogle.requestNonPersonalizedAds = 1;
      }
      w.adsbygoogle.push({});
    }
  };
  const io = new IntersectionObserver(entries => {
    for (const e of entries) {
      if (e.isIntersecting) {
        push();
        io.disconnect();
        break;
      }
    }
  }, { rootMargin: '0px', threshold: 0.1 });
  io.observe(el);
}
```

#### CMP/TCF v2: interface de consentement

```ts
const consentGranted = computed(() => ConsentStore().granted);
```

```vue
<adsense :ad-slot="ADSENSE_SLOT_TOP" :npa="!consentGranted" />
```

#### Rétrocompatibilité avec ADSENSE_SLOT existant

- Conserver l’usage de `ADSENSE_SLOT` pour les pages article; les nouveaux slots (`ADSENSE_SLOT_TOP`, `SIDEBAR_LEFT`, `SIDEBAR_RIGHT`, `FOOTER`) s’appliquent au layout global.
- Migration ultérieure possible vers un naming unifié si souhaité.

#### Placement de l’injection Unhead

- Insérer `useHead` du script AdSense dans le `setup()` de la racine [main.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/main.ts) pour garantir une injection unique.

#### CSP

- La politique CSP existante autorise les domaines nécessaires (script/img/frame/connect) pour AdSense [application.yml](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/config/application.yml#L192-L200).
