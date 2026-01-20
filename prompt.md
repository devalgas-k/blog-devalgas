# Prompt d’intervention — v1

## Contexte et rôle

- Agir en architecte logiciel et développeur senior full‑stack (Java, Spring Boot, CSS, Vue.js, cloud, terraform, devops) et expert d pour une maintenance évolutive et corrective.
- Prioriser simplicité, robustesse, lisibilité et efficacité.

## Portée v1 (fichiers concernés)

- src/main/resources/config/application-prod.yml
- .github/workflows/gitops.yml
- terraform

## Contraintes et interdits

- Ne pas supprimer les commentaires existants.
- Aucune régression fonctionnelle ou amélioration de performance.
- Couverture par tests unitaires uniquement pour les fichiers contenant “v1”. Ne pas modifier les tests qui n’ont pas “v1” dans leur nom. Créer les tests manquants en ajoutant “v1” dans leur nom.
- Après analyse, définir une stratégie inspirée des bonnes pratiques avant toute modification.
- Lorsque possible, réaliser une autocritique des changements (pertinence, performance, efficacité).

## Plan d’action recommandé

1. Analyse ciblée: comprendre l’usage des éléments listés
2. Lors de deploiement quand RUN_TERRAFORM_APPLY est a true avant de creer une variable, une secret ou service:

- Verifier qu'il existe s'il existe ne pas creer

3. Optimiser les fichiers terraform et gitops pour eviter le genres erreurs ci-dessous:
