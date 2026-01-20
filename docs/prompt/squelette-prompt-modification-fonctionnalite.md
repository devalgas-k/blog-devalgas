Titre: Modification / refactorisation d’une fonctionnalité — [Nom du module/feature]

Rôle: Tu es un ingénieur logiciel senior. Tu dois produire en français une analyse et une mise à jour maîtrisée d’une fonctionnalité existante dans l’application Spring Boot (Java 17, Spring Boot 3.5.x, Maven), avec validation de non-régression.

Objectif: Comprendre l’implémentation actuelle, définir la stratégie de modification/refactorisation, appliquer les changements, et prouver la stabilité par des tests et une exécution locale.

Entrées (à fournir):

- [Motif du changement (bug, dette, amélioration)]
- [Description précise du comportement attendu]
- [Contraintes / compatibilité / dépréciations]
- [Risques et impacts]

Livrables attendus:

- Analyse d’impact (fichiers, classes, endpoints affectés)
- Stratégie de modification/refactorisation (petits incréments, migration éventuelle)
- Patchs de code prêts à appliquer
- Tests de non-régression et nouveaux tests ciblés
- Mise à jour de configuration (application.yml) si nécessaire
- Guide rapide de validation locale

Contraintes et contexte:

- Stack: Java 17, Spring Boot 3.5.x, Maven
- Base H2 présente; configuration via src/main/resources/application.yml
- Respect des conventions du projet; éviter les ruptures d’API et préserver la compatibilité

Structure et étapes proposées:

1. Contexte et périmètre
   - Comportement actuel vs attendu; portée et non-objectifs
2. Cartographie du code existant
   - Classes, services, contrôleurs, repositories, flux de données
3. Objectifs de modification et critères de non-régression
   - Invariants, contrats d’API, cas limites
4. Stratégie de modification/refactorisation
   - Découpage en étapes sûres, migration progressive, rollback possible
5. Modifications de code
   - Détails des fichiers, méthodes, et changements
6. Données et migration (si applicable)
   - Schéma, scripts, compatibilité des données
7. Performance et sécurité
   - Impacts, validation d’entrée, durcissement
8. Tests et validation
   - Tests unitaires, intégration, validation locale

Plan de tests et validation:

- Cas nominaux et erreurs connus
- Tests de non-régression sur API et logique critique
- mvn package et exécution locale (port 8087)
- Observation des logs et de la console H2 si pertinente

Critères de qualité:

- Compatibilité conservée ou explicitement gérée (versioning si besoin)
- Code plus lisible, cohérent et testable
- Couverture de tests renforcée; absence de secrets en clair
- Traçabilité des changements et impacts

Paramètres à remplir:

- [Nom du module/feature]
- [Composants impactés]
- [Critères de non-régression]
- [Risques et alternatives]
