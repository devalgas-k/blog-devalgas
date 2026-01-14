# Prompt d’intervention — v1

## Contexte et rôle

- Agir en développeur senior full‑stack (Java, Spring Boot, CSS, Vue.js) et expert d pour une maintenance évolutive et corrective.
- Prioriser simplicité, robustesse, lisibilité et efficacité.

## Portée v1 (fichiers concernés)

- src/main/webapp/app/core/title/title.vue
- src/main/webapp/content/css/loading.css
- src/main/webapp/index.html

## Tâche prioritaire

## Contraintes et interdits

- Ne pas supprimer les commentaires existants.
- Aucune régression fonctionnelle ou amélioration de performance.
- Couverture par tests unitaires uniquement pour les fichiers contenant “v1”. Ne pas modifier les tests qui n’ont pas “v1” dans leur nom. Créer les tests manquants en ajoutant “v1” dans leur nom.
- Après analyse, définir une stratégie inspirée des bonnes pratiques avant toute modification.
- Lorsque possible, réaliser une autocritique des changements (pertinence, performance, efficacité).

## Plan d’action recommandé

1. Analyse ciblée: comprendre l’usage des éléments listés
2. Stratégie: choisir la solution parmis plussieurs solutions la plus simple et efficace; formaliser brièvement le plan
3. Implémentation frontend

- le css de <span></span> de '<h1 class="container-title__heading">[ Devalgas ]<span></span></h1>' dans title.vue doit etre
  le app-loading le remplacer dans loading.css, index.html notamment dans la classe css app-loading
