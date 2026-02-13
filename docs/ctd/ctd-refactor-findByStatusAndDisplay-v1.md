CTD – Refactor findByStatusAndDisplay (V1)

- Objectif

  - Renommer la méthode native actuelle et introduire une méthode dérivée sans @Query.
  - Mapper la colonne display dans le modèle V1 (entité + DTO) et adapter le mapping.
  - Ajouter un index composite (status, display, date) pour accélérer tri/filtrage.
  - Vérifier l’alignement avec le cache L2.

- Modifications

  - Modèle V1
    - ArticleHomeV1: ajout du champ Boolean display.
    - ArticleHomeV1DTO: ajout du champ Boolean display.
  - Mapper V1
    - ArticleHomeV1Mapper: mapping implicite de display par MapStruct.
  - Repository V1
    - ArticleHomeRepositoryV1:
      - Méthode native renommée: findByStatusAndDisplayNative(String status, Boolean display, Pageable).
      - Nouvelle méthode dérivée typée: findByStatusAndDisplay(Status status, Boolean display, Pageable).
      - Méthodes WithEagerRelationships correspondantes.
  - Service V1
    - ArticleServiceImplV1: utilisation de la méthode typée avec Status (tri par date DESC via Pageable).
  - Liquibase
    - Nouveau changeset: 20260209123500_added_index_article_status_display_date.xml (index: status, display, date).
    - Inclus dans master.xml.

- Cache L2 (Hibernate + Ehcache)

  - L2 activé, query cache désactivé.
  - Caches déclarés pour ArticleHomeV1 et collections CategoryArticleHomeV1.
  - Les requêtes paginées ne sont pas mises en cache côté requêtes; les entités chargées sont mises en L2.
  - L’ajout du champ display est pris en compte dans l’état d’entité en cache; aucune configuration supplémentaire.

- Vérification
  - Build/test backend: npm run backend:unit:test.
  - Liquibase: exécution du changeset d’index en environnement cible.
