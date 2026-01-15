# Prompt d’intervention — v1

## Contexte et rôle

- Agir en architecte logiciel et développeur senior full‑stack (Java, Spring Boot, CSS, Vue.js) et expert d pour une maintenance évolutive et corrective.
- Prioriser simplicité, robustesse, lisibilité et efficacité.

## Portée v1 (fichiers concernés)

- src/main/java/com/devalgas/blog/service/impl/v1/MailServiceImplV1.java
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
2. Faire une conception technique detaillee \*.md dans le 'docs' pour intergrer un stmp pour l'envoi des mails avec
   avec SMTP2GO et gmail. Dans la conception touver des stategie sur comment les deux SMTP2GO et gmail peuvent communiquer
   ensemble SMTP2GO etant le favori. La conception doit pouvoir se limiter au plan gratuit
