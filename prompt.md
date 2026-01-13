# Prompt d’intervention — v1

## Contexte et rôle

- Agir en développeur senior full‑stack (Java, Spring Boot, CSS, Vue.js) pour une maintenance évolutive et corrective.
- Prioriser simplicité, robustesse, lisibilité et efficacité.

## Portée v1 (fichiers concernés)

- L’API getArticleV1: src/main/java/com/devalgas/blog/web/rest/v1/ArticleResourceV1.java
- Vue article-details-v1.component.ts: src/main/webapp/app/entities/article/v1/details/article-details-v1.component.ts
- Service front article.service-v1.ts: src/main/webapp/app/entities/article/v1/article.service-v1.ts
- Modèle article.model.ts: src/main/webapp/app/shared/model/article.model.ts

## Tâche prioritaire

## Contraintes et interdits

- Ne pas supprimer les commentaires existants.
- Aucune régression fonctionnelle ou amélioration de performance.
- Ne modifier que les fichiers “v1”; exceptions autorisées: fichiers de configuration de cache et de sécurité.
- Couverture par tests unitaires uniquement pour les fichiers contenant “v1”. Ne pas modifier les tests qui n’ont pas “v1” dans leur nom. Créer les tests manquants en ajoutant “v1” dans leur nom.
- Après analyse, définir une stratégie inspirée des bonnes pratiques avant toute modification.
- Lorsque possible, réaliser une autocritique des changements (pertinence, performance, efficacité).

## Plan d’action recommandé

1. Analyse ciblée: comprendre l’usage des éléments listés dans la Portée v1 et cartographier les dépendances backend/frontend.
2. Stratégie: choisir la solution parmis plussieurs solutions la plus simple et efficace; formaliser brièvement le plan
3. Implémentation backend: améliorer le temps de réponse de l'API getArticleV1 à 1s max optimisant le changement des bob markdown, ne pas utiliser lang et l’ETag
4. Implémentation frontend: garantir compatibilité des types et de la vue; éviter les modifications hors v1.
5. Tests v1: créer/ajuster uniquement les tests v1; couvrir projection, repository, service et affichage minimal de la vue.
6. Validation: vérifier compilation, tests, comportement de la vue et performance des requêtes.
7. Autocritique et simplification: réduire la complexité et le nombre d’instructions si possible.

## Critères d’acceptation

- Aucun fichier hors v1 modifié (hors config cache/sécurité).
- Tests v1 passent; aucun test non v1 modifié.
- Pas de régression; code conforme aux styles du projet.

## Format de réponse attendu

- Résumé de l’intention et du scope.
- Plan d’action succinct.
- Changements par fichier (liste et justification).
- Extraits clés de code nécessaires.
- Validation (tests, build, vérifications effectuées).
- Points ouverts ou risques éventuels.
- Temps de réponse attendu de l'API getAllArticles: 1s max.

### Exemple de reponse attendu de l'API
