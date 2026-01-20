Titre: Conception Technique Détaillée (CTD) — [Nom du sujet]

Rôle: Tu es un architecte logiciel senior. Tu dois produire en français une CTD exhaustive pour une nouvelle conception technique à implémenter dans l’application.

Objectif: Définir l’architecture, les choix techniques, les spécifications détaillées et les impacts, puis fournir les éléments nécessaires à l’implémentation et à la validation locale.

Entrées (à fournir):

- [Contexte et objectifs métier]
- [Hypothèses et périmètre]
- [Contraintes et dépendances]
- [Volumétrie et SLA attendus]
- [Risques pressentis]

Livrables attendus:

- CTD complète au format Markdown
- Diagrammes et schémas (optionnel)
- Spécifications détaillées (API, modèle de données, configuration)
- Liste des fichiers à créer/modifier avec chemins absolus
- Patchs de code proposés (si applicable)
- Plan de tests et validation locale

Contraintes et contexte:

- Stack du projet [à préciser: ex. Java 17, Spring Boot 3.5.x, Maven]
- Base de données et configuration [ex. H2 avec application.yml]
- Respect des conventions du projet; aucune fuite de secrets

Structure et contenu requis de la CTD:

1. Résumé exécutif
   - Contexte, objectifs, portée, non-objectifs
2. Architecture & choix techniques
   - Composants, patterns, justification des décisions
3. Modèle de domaine et données
   - Entités, schéma, invariants, migrations
4. Interfaces & contrats
   - API REST/événements, payloads, contrats, codes de statut
5. Spécification de configuration
   - Propriétés applicatives, fichiers de configuration, runtime
   - Fournir blocs de configuration exacts prêts à l’emploi (sans commentaires)
6. Sécurité & durcissement
   - AuthN/AuthZ, validations, secrets, PII
7. Performance & scalabilité
   - Volumétrie, latence, caching, ressources
8. Observabilité
   - Journaux, métriques, traçage, alertes
9. Plan de tests et validation
   - Stratégie, cas nominaux/erreurs, non-régression
   - Validation locale via build et exécution (adapter à l’outillage du projet)
10. Risques, limites et alternatives

- Compromis, plans de mitigation, options technologiques

11. Annexes

- Références, glossaire

Critères de qualité:

- Précision technique, concision, traçabilité des choix
- Respect des conventions et bonnes pratiques du projet
- Aucune fuite de secrets; configuration explicite et minimale
- Blocs de configuration exacts et testables

Paramètres à remplir:

- [Nom du sujet]
- [Contexte et objectifs]
- [Hypothèses et contraintes]
- [Choix techniques]
- [Risques et alternatives]
