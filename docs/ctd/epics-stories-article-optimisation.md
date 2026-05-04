# Epics et User Stories : Optimisation du Chargement Article Details V1

Ce document détaille le découpage en Epics et User Stories pour l'implémentation de l'optimisation du chargement du composant `ArticleDetailsV1`, basé sur la CTD de Winston.

## Epic 1 : Amélioration de la Perception Visuelle (Zéro CLS & Skeleton)

**Objectif** : Supprimer l'effet de saut de mise en page et le loader "tout ou rien" pour une transition fluide.

### Story 1.1 : Création du composant ArticleDetailsSkeletonV1

- **Description** : En tant qu'utilisateur, je veux voir une structure simplifiée de l'article pendant le chargement pour ne pas ressentir d'attente brutale.
- **Critères d'acceptation** :
  - Créer `ArticleDetailsSkeletonV1.vue`.
  - Le skeleton doit reproduire le layout du `p-splitter` (deux colonnes sur desktop, une sur mobile).
  - Inclure des placeholders animés (shimmer effect) pour le titre, la description et le corps de l'article.
  - Utiliser des dimensions CSS fixes pour correspondre au contenu final (min-height).

### Story 1.2 : Intégration du Skeleton dans ArticleDetailsV1

- **Description** : En tant que développeur, je veux remplacer le `app-loader` global par le nouveau skeleton.
- **Critères d'acceptation** :
  - Remplacer `<template v-else><app-loader /></template>` par le skeleton.
  - S'assurer que le skeleton est visible tant que les données ne sont pas prêtes.

## Epic 2 : Coordination et Fluidité du Rendu

**Objectif** : Synchroniser l'affichage du contenu pour éviter l'effet de "pop-in" des sous-composants.

### Story 2.1 : Coordination du montage des composants

- **Description** : En tant qu'utilisateur, je veux que tout l'article s'affiche en une seule fois pour éviter les clignotements.
- **Critères d'acceptation** :
  - Ajouter un événement `mounted` (ou un état de chargement) dans `ArticleInfoV1`.
  - Dans `ArticleDetailsV1`, utiliser un état `isFullyReady` qui attend la fin de `retrieveArticle` ET le montage de `ArticleInfoV1`.
  - N'afficher le contenu final que lorsque `isFullyReady` est vrai.

### Story 2.2 : Optimisation de la transition "fade"

- **Description** : En tant qu'utilisateur, je veux une transition rapide entre le skeleton et le contenu.
- **Critères d'acceptation** :
  - Réduire la durée de la transition `fade` à 150ms.
  - S'assurer qu'il n'y a pas de superposition visuelle entre le skeleton et le contenu pendant la transition.

## Epic 3 : Optimisation des Performances Techniques (Thread & Ressources)

**Objectif** : Réduire le temps de blocage du thread principal (TBT) et accélérer le premier rendu (LCP).

### Story 3.1 : Préchargement des ressources critiques

- **Description** : En tant que développeur, je veux utiliser `useHead` pour prioriser le chargement des polices et des données.
- **Critères d'acceptation** :
  - Ajouter `<link rel="preload">` pour la police Inter.
  - Explorer la possibilité de pré-fetcher les données de l'article lors du survol des liens (si applicable).

### Story 3.2 : Optimisation du décodage Markdown

- **Description** : En tant qu'utilisateur, je veux que le texte s'affiche sans bloquer mon navigateur, même pour de longs articles.
- **Critères d'acceptation** :
  - Mémoïser le résultat du décodage Markdown pour éviter les re-calculs inutiles.
  - (Optionnel) Déporter le décodage dans un Web Worker si la taille des articles le justifie.

---

## Priorisation du Backlog (Sprint Plan)

1.  **Priorité Haute (Sprint 1)** :

    - Story 1.1 : Création du composant `ArticleDetailsSkeletonV1`.
    - Story 1.2 : Intégration du Skeleton dans `ArticleDetailsV1`.
    - Story 2.1 : Coordination du montage des composants (fondamental pour l'effet "seamless").

2.  **Priorité Moyenne (Sprint 2)** :

    - Story 2.2 : Optimisation de la transition `fade`.
    - Story 3.1 : Préchargement des ressources critiques.

3.  **Priorité Basse / Optimisation continue** :
    - Story 3.2 : Optimisation du décodage Markdown.
