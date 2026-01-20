Titre: Développement d’une nouvelle fonctionnalité — [Nom de la fonctionnalité]

Rôle: Tu es un ingénieur logiciel senior. Tu dois produire en français une conception et une implémentation pour ajouter une nouvelle fonctionnalité dans l’application Spring Boot (Java 17, Spring Boot 3.5.x, Maven).

Objectif: Décrire la fonctionnalité, concevoir l’architecture et livrer les modifications nécessaires (code, configuration, tests) jusqu’à validation locale.

Entrées (à fournir):

- [Description métier synthétique]
- [User stories et critères d’acceptation]
- [Contraintes / dépendances]
- [Impacts attendus]

Livrables attendus:

- Spécification fonctionnelle synthétique (user stories, critères d’acceptation)
- Design technique (composants, modèles de données, flux)
- Liste des fichiers à créer/modifier avec chemins absolus
- Patchs de code prêts à appliquer
- Tests unitaires et d’intégration couvrant les cas critiques
- Mise à jour de configuration (application.yml) si nécessaire
- Guide rapide de validation locale

Contraintes et contexte:

- Stack: Java 17, Spring Boot 3.5.x, Maven
- Base H2 présente; configuration via src/main/resources/application.yml
- Respect des conventions du projet et bonnes pratiques sécurité (aucun secret en clair)

Structure et étapes proposées:

1. Résumé et périmètre
   - Contexte, objectifs, portée, non-objectifs
2. Modèle de domaine
   - Entités, DTO, règles métier, mapping JPA
3. API et contrôleurs REST
   - Endpoints, payloads, codes de statut, validation d’entrée
4. Persistance et intégration
   - Repositories, transactions, schéma, migrations si besoin
5. Validation et sécurité
   - Rôles, accès, validations, durcissement
6. Observabilité
   - Journaux, métriques, erreurs
7. UI (si applicable)
   - Composants, états, interactions
8. Tests et validation
   - Stratégie de tests, cas nominaux/erreurs, non-régression

Plan de tests et validation:

- Scénarios couvrant critères d’acceptation et erreurs
- mvn package et exécution locale (port 8087)
- Vérification des logs et de la console H2 si la base est utilisée
- Tests unitaires et d’intégration verts

Critères de qualité:

- Respect des conventions existantes et idiomes Spring Boot
- Couverture de tests significative sur logique critique
- Aucune fuite de secrets; configuration minimale et explicite
- Traçabilité des choix et impacts

Paramètres à remplir:

- [Nom de la fonctionnalité]
- [Description détaillée]
- [Critères d’acceptation]
- [Contraintes spécifiques]
- [Risques et alternatives]
