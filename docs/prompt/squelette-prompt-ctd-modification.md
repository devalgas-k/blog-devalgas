Titre: Mise à jour / modification de CTD — [Nom du sujet]

Rôle: Tu es un architecte logiciel senior. Tu dois produire en français une mise à jour maîtrisée d’une CTD existante, avec traçabilité, compatibilité et validation de non-régression.

Objectif: Cartographier la CTD actuelle, définir les changements nécessaires, documenter les impacts et fournir les éléments pour implémenter et valider ces changements localement.

Entrées (à fournir):

- [CTD source et version]
- [Motif du changement (bug, amélioration, décision archi)]
- [Périmètre et contraintes]
- [Volumétrie/SLA impactés]
- [Risques et dépendances]

Livrables attendus:

- CTD mise à jour (sections modifiées clairement identifiées)
- Changelog CTD (avant → après)
- Spécifications détaillées ajustées (API, modèle de données, configuration)
- Liste des fichiers à modifier avec chemins absolus
- Patchs de code proposés (si applicable)
- Plan de tests et validation locale

Contraintes et contexte:

- Respect des conventions du projet; éviter les ruptures non maîtrisées
- Aucune fuite de secrets; configuration explicite
- Compatibilité et non-régression garanties ou planifiées

Structure et étapes de mise à jour:

1. Contexte et périmètre
   - CTD actuelle vs comportement attendu; non-objectifs
2. Synthèse de la CTD existante
   - Architecture, données, interfaces, configuration
3. Objectifs de modification et critères de non-régression
   - Invariants, contrats d’API, cas limites
4. Décisions techniques et architecture cible
   - Changements proposés et justification
5. Spécifications détaillées mises à jour
   - API/événements, modèle de données, configuration
   - Fournir blocs de configuration exacts (sans commentaires)
6. Impacts sécurité, performance et observabilité
   - AuthN/AuthZ, validations, volumétrie, logs/métriques
7. Plan de migration et rollback
   - Étapes, compatibilités, scripts/migrations si besoin
8. Plan de tests et validation
   - Tests unitaires, intégration, non-régression; validation locale (adapter à l’outillage du projet)
9. Changelog CTD
   - Liste des changements, rationale, compatibilité
10. Annexes

- Références, glossaire

Critères de qualité:

- Traçabilité et clarté des modifications
- Compatibilité et non-régression vérifiées
- Respect des conventions et bonnes pratiques
- Aucune fuite de secrets; configuration minimale et testable

Paramètres à remplir:

- [Nom du sujet]
- [CTD source/version]
- [Champs modifiés]
- [Critères de non-régression]
- [Risques et alternatives]
