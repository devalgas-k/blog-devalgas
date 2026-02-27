Titre: Conception Technique Détaillée (CTD) — Intégration Google Ads (AdSense) dans app.vue (V1)

Rôle: Tu es un architecte logiciel senior. Produis en français une CTD exhaustive pour intégrer des publicités Google AdSense dans l’application Vue 3, en remplaçant tous les placeholders « google ad » de [app.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/app.vue). La CTD doit déterminer les formats d’annonces optimaux par emplacement, décrire l’intégration Vue conforme aux bonnes pratiques (performance, UX, conformité), et livrer les spécifications, patchs et validations nécessaires. Ne pas implémenter; livrer uniquement la CTD et les blocs/configs prêts à l’emploi.

Objectif: Définir l’architecture, les choix techniques, les formats, les spécifications détaillées et les impacts pour:

- Injecter le script AdSense une seule fois, de façon déclarative
- Remplacer chaque placeholder par un composant d’annonce configuré par emplacement
- Minimiser le CLS et préserver l’UX responsive
- Empêcher le rendu des blocs d’annonce si aucune pub ne doit être servie (consentement absent, script indisponible, slots non configurés) et replier automatiquement le conteneur en cas de no‑fill
- Préparer la configuration (env/define) pour des slots par emplacement
- Décrire le plan de tests (unitaires et validations manuelles)

Entrées (à fournir et à intégrer dans la CTD):

- Contexte et objectifs métier: monétiser via Google Ads des zones clés du site
- Hypothèses et périmètre: intégration AdSense manuelle (pas Auto Ads), placements présents dans app.vue
- Contraintes et dépendances: Vue 3.5.x, Vite, Bootstrap 4, Unhead pour le head; composant interne Adsense
- Volumétrie et SLA attendus: trafic web grand public; latence d’affichage d’annonce non bloquante
- Risques pressentis: CLS/UX, non‑conformité consentement, script non chargé, slots invalides

Livrables attendus:

- CTD complète au format Markdown dans docs/ctd suivant le squelette
- Spécifications détaillées (configuration, intégration script, placements, styles)
- Liste des fichiers à créer/modifier avec chemins absolus
- Patchs de code proposés (app.vue, main.ts, vite.config.mts, declarations.d.ts)
- Plan de tests et validation locale

Contraintes et contexte (adapter depuis le projet):

- Stack: Vue 3.5.13, Vite, Bootstrap‑Vue 2.x, Pinia, Unhead (@unhead/vue)
- Fichiers et composants existants:
  - [app.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/app.vue) — placeholders « google ad »
  - [adsense.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/core/adsense/adsense.vue) et [adsense.component.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/core/adsense/adsense.component.ts)
  - [main.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/main.ts) — Unhead initialisé
  - [vite.config.mts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/vite.config.mts) — define ADSENSE_CLIENT/ADSENSE_SLOT
  - [declarations.d.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/declarations.d.ts) — constantes globales
- Politique de secrets: aucune fuite; utiliser variables d’environnement

Formats cibles (guides et tailles de référence):

- Bannière horizontale (après navbar, desktop):
  - 970×90 (Large Leaderboard), alternative 728×90 (Leaderboard), option 970×250 (Billboard)
- Sidebar (colonne droite/gauche):
  - 300×250 (Medium Rectangle), 300×600 (Half Page), 160×600 (Wide Skyscraper)
- Fin d’article (avant footer):
  - 336×280 (Large Rectangle) ou 300×250 (Medium Rectangle)
- Documentation officielle: Google Ads — Tailles d’annonces display https://support.google.com/google-ads/answer/1722096
- Mobile (optionnel):
  - 320×100 (Large Mobile Banner) prioritaire; fallback 320×50 (Mobile Banner)

Structure et contenu requis de la CTD (squelette à respecter):

1. Résumé exécutif
   - Contexte, objectifs, portée, non‑objectifs
2. Architecture & choix techniques
   - Composants, patterns (script head via Unhead, composants Vue par emplacement), justification des décisions
3. Modèle de domaine et données
   - N/A (front uniquement); variables d’environnement et constantes JS
4. Interfaces & contrats
   - Contrat composant Adsense (props: adSlot, format, responsive, style, npa, test)
5. Spécification de configuration
   - Propriétés applicatives exactes (define Vite) et variables d’environnement pour chaque slot
   - Blocs prêts à l’emploi (sans commentaires)
   - Inclure un flag d’activation globale (ex. ADSENSE_ENABLED) et le gating de consentement; les composants ne doivent pas rendre `<ins.adsbygoogle>` lorsque ces garde‑fous sont faux
6. Sécurité & conformité
   - Consentement utilisateur (CMP/TCF v2) et NPA; pas de PII; script crossOrigin
   - Respect des politiques AdSense: densité d’annonces raisonnable, annonces clairement distinguées du contenu, pas de clic induit, pas d’injection dans des éléments interactifs
   - Vérifier la cohérence de `ads.txt` avec l’ID éditeur et le domaine
7. Performance & UX
   - CLS minimal (réserver la hauteur), responsive, push asynchrone, éviter reflows et repositionnements
   - Un seul chargement du script; éviter de recréer les annonces à chaque navigation (clé stable, pas de remount inutile)
   - Compatibilité mobile: masquer si non prévu, ou ajouter des formats dédiés 320×100/320×50
   - Repli automatique: si aucune annonce n’est servie après un délai raisonnable, réduire la hauteur du conteneur à 0 et ne pas afficher de placeholder intrusif
8. Observabilité
   - Journaux de chargement script (optionnel), validation Lighthouse
   - Surveiller le taux de remplissage et les erreurs console AdSense; remonter les cas de `adsbygoogle.push` non exécuté
9. Plan de tests et validation
   - Unitaires Vitest pour présence des <ins.adsbygoogle> et injection du script head; validations manuelles
10. Risques, limites et alternatives

- Non‑remplissage, formats non adaptés, alternative Auto Ads/Ad Manager

11. Annexes

- Références de code et chemins de fichiers

Spécifications à intégrer (exacts et contextualisés):

- Injection du script AdSense via Unhead (dans main.ts), une seule fois:
  - Fichier: [main.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/main.ts)
  - Bloc à fournir dans la CTD:
    ```
    useHead({
      script: [
        {
          src: `https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=${ADSENSE_CLIENT}`,
          async: true,
          crossOrigin: 'anonymous',
        },
      ],
    });
    ```
- Déclaration des slots par emplacement via define Vite:
  - Fichier: [vite.config.mts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/vite.config.mts)
  - Bloc à fournir dans la CTD (valeurs par défaut à surcharger par env):
    ```
    define: {
      ADSENSE_CLIENT: `"${env.ADSENSE_CLIENT ? env.ADSENSE_CLIENT : 'ca-pub-6181972205565553'}"`,
      ADSENSE_ENABLED: ${env.ADSENSE_ENABLED ?? true},
      ADSENSE_SLOT_TOP: `"${env.ADSENSE_SLOT_TOP ? env.ADSENSE_SLOT_TOP : '0000000001'}"`,
      ADSENSE_SLOT_SIDEBAR_LEFT: `"${env.ADSENSE_SLOT_SIDEBAR_LEFT ? env.ADSENSE_SLOT_SIDEBAR_LEFT : '0000000002'}"`,
      ADSENSE_SLOT_SIDEBAR_RIGHT: `"${env.ADSENSE_SLOT_SIDEBAR_RIGHT ? env.ADSENSE_SLOT_SIDEBAR_RIGHT : '0000000003'}"`,
      ADSENSE_SLOT_FOOTER: `"${env.ADSENSE_SLOT_FOOTER ? env.ADSENSE_SLOT_FOOTER : '0000000004'}"`,
    }
    ```
- Déclarations TypeScript des nouvelles constantes:
  - Fichier: [declarations.d.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/declarations.d.ts)
  - Bloc à fournir:
    ```
    declare const ADSENSE_SLOT_TOP: string;
    declare const ADSENSE_SLOT_SIDEBAR_LEFT: string;
    declare const ADSENSE_SLOT_SIDEBAR_RIGHT: string;
    declare const ADSENSE_SLOT_FOOTER: string;
    ```
- Remplacement des placeholders dans app.vue par le composant Adsense configuré:
  - Fichier: [app.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/app.vue)
  - Contexte de grille actuel:
    - Layout desktop: `col-lg-2 | col-12 col-lg-8 | col-lg-2`
    - Impact: la zone centrale en `col-lg-8` limite la largeur disponible; privilégier 728×90 pour le top si la bannière reste dans cette colonne; 970×90 possible uniquement si la bannière occupe `col-12` pleine largeur.
  - Emplacements et formats:
    - Après la navbar (desktop, dans `col-lg-8`):
      - 728×90 prioritaire; 970×90 uniquement si passage en `col-12` pleine largeur; style réservé min‑height: 90px
      - Bloc à fournir:
        ```
        <div v-if="ADSENSE_ENABLED && consentGiven" class="d-none d-lg-flex">
          <adsense :ad-slot="ADSENSE_SLOT_TOP" format="auto" :responsive="true" style="display:block;width:100%;min-height:90px" />
        </div>
        ```
    - Sidebar gauche (desktop, en `col-lg-2`):
      - 160×600 prioritaire (colonne étroite); si la grille est élargie à ≥300 px (ex. `col-lg-3`), 300×600 prioritaire; fallback 300×250; style réservé min‑height: 600px
      - Bloc à fournir:
        ```
        <div v-if="ADSENSE_ENABLED && consentGiven" class="d-none d-lg-flex">
          <adsense :ad-slot="ADSENSE_SLOT_SIDEBAR_LEFT" format="auto" :responsive="true" style="display:block;width:100%;min-height:600px" />
        </div>
        ```
    - Sidebar droite (desktop, en `col-lg-2`):
      - 160×600 prioritaire en colonne étroite; 300×250 si la largeur de sidebar atteint ≥300 px; style réservé min‑height: 250px (adapter à 600 px si 160×600)
      - Bloc à fournir:
        ```
        <div v-if="ADSENSE_ENABLED && consentGiven" class="d-none d-lg-flex">
          <adsense :ad-slot="ADSENSE_SLOT_SIDEBAR_RIGHT" format="auto" :responsive="true" style="display:block;width:100%;min-height:250px" />
        </div>
        ```
    - Bandeau avant footer (desktop):
      - 336×280 prioritaire; fallback 300×250; style réservé min‑height: 280px
      - Bloc à fournir:
        ```
        <div v-if="ADSENSE_ENABLED && consentGiven" class="d-none d-lg-flex">
          <adsense :ad-slot="ADSENSE_SLOT_FOOTER" format="auto" :responsive="true" style="display:block;width:100%;min-height:280px" />
        </div>
        ```
- Bonnes pratiques d’intégration Vue:
  - Réserver la hauteur via style pour éviter le CLS
  - Utiliser `format="auto"` et `:responsive="true"` pour adapter aux conteneurs Bootstrap
  - Charger le script via Unhead, une seule fois; chaque composant déclenche `adsbygoogle.push({})`
  - Activer `data-adtest="on"` en dev (déjà géré par le composant)
  - Gérer NPA via la prop `npa` si consentement absent
  - Fournir des ad unit IDs distincts (`data-ad-slot`) par emplacement; ne pas réutiliser un même slot sur plusieurs zones simultanément
  - Ne pas rendre les annonces dans des conteneurs cachés ou avec `display:none`; s’assurer que l’élément est visible avant `push`
  - En navigation interne (SPA), éviter le “re-push” agressif; ne relancer que si la clé change (slot ou format)
  - Option avancée: déclencher `push` via IntersectionObserver lorsque l’annonce entre dans le viewport
  - Rendu conditionnel: utiliser `v-if="ADSENSE_ENABLED && consentGiven && scriptReady"` pour ne jamais afficher le bloc si la pub n’est pas autorisée; prévoir un mécanisme de repli qui retire le conteneur si la hauteur de `<ins>` reste 0 après un délai (no‑fill)

Extensions recommandées (optionnelles):

- Mobile:
  - Ajouter une bannière mobile dédiée (320×100 prioritaire; fallback 320×50) en haut ou bas d’écran; respecter l’UX (pas d’overlay intrusif)
  - Afficher les annonces mobiles seulement avec consentement; réserver min‑height: 100 px
- Multiplex/In‑article:
  - En fin d’article, envisager un format “multiplex” ou “in‑article” si disponible côté compte; bons CTR et intégration au flux de lecture
- ads.txt:
  - Confirmer que `src/main/webapp/ads.txt` et `src/main/resources/static/ads.txt` sont alignés avec l’ID éditeur: `google.com, pub-6181972205565553, DIRECT, f08c47fec0942fa0`

Liste des fichiers à créer/modifier:

- Modifier: [src/main/webapp/app/main.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/main.ts)
- Modifier: [src/main/webapp/app/app.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/app.vue)
- Modifier: [vite.config.mts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/vite.config.mts)
- Modifier: [src/main/webapp/app/declarations.d.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/declarations.d.ts)

Plan de tests et validation:

- Unitaires (Vitest):
  - Scénario activé: monter app.vue avec `ADSENSE_ENABLED=true` et consentement vrai; vérifier la présence de 4 instances `<ins class="adsbygoogle">`
  - Scénario désactivé/consentement absent: monter app.vue avec `ADSENSE_ENABLED=false` ou consentement faux; vérifier qu’il n’y a aucune instance `<ins class="adsbygoogle">`
  - Scénario no‑fill simulé: espionner le composant pour forcer une hauteur de `<ins>` à 0 et vérifier le repli du conteneur (hauteur réduite à 0 ou non rendu)
  - Vérifier l’injection du script AdSense dans le head via Unhead
  - Vérifier que les styles réservent la hauteur (90/600/250/280)
- Manuels:
  - Démarrer en dev et vérifier l’absence d’erreurs console liées à adsbygoogle
  - Auditer Lighthouse (CLS) et vérifier que les conteneurs évitent les sauts de mise en page
  - Valider le remplissage des annonces une fois les slots AdSense réels configurés

Critères d’acceptation:

- Script AdSense injecté une seule fois au head
- Placeholders remplacés par Adsense avec slots dédiés
- Aucun CLS notable; pas d’erreurs console
- Aucun bloc d’annonce vide: en absence de pub (no‑fill ou gating faux), le conteneur n’apparaît pas et/ou est replié sans gêner l’UX
- Configurabilité via variables d’environnement

Références de code:

- Composant Adsense: [adsense.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/core/adsense/adsense.vue), [adsense.component.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/core/adsense/adsense.component.ts)
- Entrée app: [main.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/main.ts), [index.html](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/index.html)
- Configuration Vite: [vite.config.mts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/vite.config.mts)
- Constantes TS: [declarations.d.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/declarations.d.ts)
