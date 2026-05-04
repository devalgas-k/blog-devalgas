# Conception Technique Détaillée : Optimisation du Chargement Article Details V1

Ce document présente trois approches pour optimiser le chargement du composant `article-details-v1.vue`, avec pour objectif une expérience utilisateur fluide ("seamless") où le chargement n'est pas ressenti.

## 1. Analyse des goulots d'étranglement actuels

- **Décodage Markdown Client-Side** : Le contenu Markdown est stocké en Base64 dans l'objet `article` et décodé via une propriété calculée (`decodedMarkdownContent`). Pour des articles longs, cela bloque le thread principal lors de l'affichage initial (TBT).
- **Composants Asynchrones Non Coordonnés** : `ArticleInfoV1` et `Adsense` sont chargés via `defineAsyncComponent`. Le `AppLoader` principal disparaît dès que le HTML du Markdown est présent, mais `ArticleInfoV1` peut encore afficher son propre skeleton interne, créant un effet de "pop-in" désagréable.
- **Poids de PrimeVue Splitter** : Le composant `p-splitter` est puissant mais lourd. Son initialisation peut causer un léger délai de rendu.
- **Absence de Priorisation des Ressources** : Les polices (Inter) et les icônes FontAwesome ne sont pas préchargées, ce qui peut provoquer des flashs de texte non stylisé (FOIT) ou des décalages de mise en page (CLS).
- **Gestion du Loader "Tout ou Rien"** : L'utilisation de `v-if="decodedMarkdownContent.html"` vs `v-else="app-loader"` crée une transition brutale.

---

## 2. Propositions de solutions

### Proposition A : Skeleton-First & Hydratation Coordonnée (Approche UX)

L'idée est de remplacer le loader global par un skeleton complet qui reproduit exactement la structure finale, et de ne l'enlever que lorsque TOUS les composants critiques sont prêts.

- **Implémentation** :
  - Créer un composant `ArticleDetailsSkeletonV1` qui imite le `p-splitter` et le bloc info.
  - Utiliser un état `isFullyReady` dans le setup.
  - Coordonner la fin du chargement : `isFullyReady = true` uniquement quand `retrieveArticle` est fini ET que `ArticleInfoV1` a émis un événement `mounted`.
- **Gain** : Suppression du sentiment de "chargement" car la structure est déjà là. CLS proche de 0.

### Proposition B : Offloading & Priorisation (Approche Performance brute)

Se concentrer sur la réduction du temps de blocage du thread principal (TBT) et l'accélération du premier rendu (LCP).

- **Implémentation** :
  - **Web Worker** : Déplacer le décodage Base64 et le parsing Markdown dans un Web Worker.
  - **Unhead Preload** : Utiliser `useHead` pour injecter des balises `<link rel="preload">` pour l'API de l'article dès que le routeur change (pré-fetch).
  - **Optimisation CSS** : Remplacer `p-splitter` par un layout CSS Grid/Flex simple sur mobile pour éviter la surcharge JS.
- **Gain** : Réduction drastique du TBT. L'article s'affiche instantanément dès que les données arrivent.

### Proposition C : App Shell & Transition Fluide (Approche "App-like")

Transformer la transition entre la liste et le détail en une expérience continue.

- **Implémentation** :
  - **Shared Element Transition** : Utiliser l'API View Transitions (si compatible) pour animer le passage de la carte home vers le détail.
  - **State Machine** : Gérer les états `IDLE` -> `LOADING_DATA` -> `RENDERING` -> `READY`.
  - **Placeholder CSS** : Utiliser des dégradés animés (shimmer effect) sur les conteneurs réels au lieu d'un composant skeleton séparé.
- **Gain** : Perception de vitesse augmentée par l'animation. Expérience premium.

---

## 3. Impacts SEO et Accessibilité

- **SEO** : Les balises JSON-LD et metas injectées par `useHead` doivent rester prioritaires. L'utilisation de skeletons ne doit pas masquer le contenu aux bots (ce qui est déjà géré par le rendu côté serveur ou l'hydratation).
- **Accessibilité** :
  - Conserver `aria-busy="true"` sur le conteneur pendant le chargement.
  - S'assurer que le focus n'est pas perdu lors du passage du skeleton au contenu réel.
  - Le texte alternatif pour les images et icônes doit être chargé en priorité.

---

## 4. Plan d'implémentation (Solution recommandée : Mix A + B)

### Étape 1 : Préparation du Skeleton (Zéro CLS)

1.  Extraire le layout du `p-splitter` dans un composant de skeleton.
2.  Fixer les dimensions minimales en CSS pour éviter tout saut de mise en page.

### Étape 2 : Coordination du chargement

1.  Ajouter un événement `@mounted` sur `ArticleInfoV1`.
2.  Dans `article-details-v1.component.ts`, utiliser `Promise.all` pour attendre les données et le montage des sous-composants.

### Étape 3 : Optimisation des ressources

1.  Ajouter des `link rel="preload"` via `useHead` pour la police Inter et les fichiers JSON de traduction.
2.  Optimiser le décodage Markdown (mémoïsation ou worker).

### Étape 4 : Raffinement de la transition

1.  Ajuster la transition `fade` pour qu'elle soit plus courte (150ms au lieu de 300ms) afin de paraître plus réactive.
2.  Supprimer le délai artificiel `makeLoader` en production.
