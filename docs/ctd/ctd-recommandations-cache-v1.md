CTD – Recommandations de cache pour les API V1

- Résumé exécutif

  - Ne pas ajouter de cache service pour /api/v1/articles/summary (ArticleHomeV1DTO).
  - Ne pas ajouter de cache service pour /api/v1/articles/{id} (ArticleDetailV1DTO), sauf besoin mesuré très fort.
  - Ajouter un cache service pour /api/v1/category-articles/summary (CategoryArticleHomeV1DTO), avec TTL court (30–120s) et éviction explicite sur écritures.

- Périmètre

  - Endpoints concernés:
    - Articles summary: src/main/java/com/devalgas/blog/web/rest/v1/ArticleResourceV1.java
    - Articles detail: service V1: src/main/java/com/devalgas/blog/service/impl/v1/ArticleServiceImplV1.java
    - Categories summary: src/main/java/com/devalgas/blog/web/rest/v1/CategoryArticleResourceV1.java et src/main/java/com/devalgas/blog/service/impl/v1/CategoryArticleServiceImplV1.java
  - DTOs:
    - ArticleHomeV1DTO: src/main/java/com/devalgas/blog/service/dto/v1/ArticleHomeV1DTO.java
    - ArticleDetailV1DTO: src/main/java/com/devalgas/blog/service/dto/v1/ArticleDetailV1DTO.java
    - CategoryArticleHomeV1DTO: src/main/java/com/devalgas/blog/service/dto/v1/CategoryArticleHomeV1DTO.java

- État actuel

  - Cache L2 Hibernate/Ehcache actif pour entités legacy et V1, y compris leurs collections ManyToMany:
    - Config: src/main/java/com/devalgas/blog/config/CacheConfiguration.java
    - Entités: src/main/java/com/devalgas/blog/domain/Article.java, src/main/java/com/devalgas/blog/domain/CategoryArticle.java
    - Entités V1: src/main/java/com/devalgas/blog/domain/v1/ArticleHomeV1.java, src/main/java/com/devalgas/blog/domain/v1/ArticleDetailV1.java, src/main/java/com/devalgas/blog/domain/v1/CategoryArticleHomeV1.java
  - Requêtes optimisées par fetch join et mappers dédiés:
    - Article summary: src/main/java/com/devalgas/blog/service/impl/v1/ArticleServiceImplV1.java
    - Categories summary: src/main/java/com/devalgas/blog/repository/v1/CategoryArticleHomeRepositoryWithBagRelationshipsV1Impl.java
  - TTL Ehcache 1h, max-entries 100 (dev) / 1000 (prod):
    - src/main/resources/config/application-dev.yml, src/main/resources/config/application-prod.yml

- Recommandations par endpoint

  - Articles summary (Page<ArticleHomeV1DTO>):
    - Conserver sans cache service: la pagination/sorting crée de nombreuses combinaisons et rend l’invalidation coûteuse.
    - Continuer à utiliser fetch join via repository V1 pour éviter N+1.
    - Option front: activer ETag/Last-Modified côté API pour limiter les relectures côté client si inchangé.
  - Article detail (Optional<ArticleDetailV1DTO>):
    - L2 couvre déjà souvent la lecture par id.
    - N’ajouter @Cacheable que si trafic très élevé mesuré, avec TTL court (30–120s) et éviction sur save/update/delete d’article.
  - Categories summary (List<CategoryArticleHomeV1DTO> ou Page unpaged):
    - Ajouter @Cacheable au service CategoryArticleServiceImplV1 avec clé constante (ex: "'all'").
    - Créer une région Ehcache dédiée avec TTL court (30–120s) pour éviter stale longs.
    - Éviction explicite sur écritures de CategoryArticle et Article (relations modifiées).

- Mise en œuvre technique (proposée)

  - Cache service pour Categories summary:
    - Service: src/main/java/com/devalgas/blog/service/impl/v1/CategoryArticleServiceImplV1.java
      - Annoter findAllCategoriesArticleHome(Pageable unpaged) avec @Cacheable(cacheNames = "v1:categories:summary", key = "'all'")
    - Évictions:
      - Sur CategoryArticleService (legacy): save/update/partialUpdate/delete → @CacheEvict(cacheNames = "v1:categories:summary", allEntries = true)
        - Fichier: src/main/java/com/devalgas/blog/service/impl/CategoryArticleServiceImpl.java
      - Sur ArticleService (legacy/V1): save/update/delete qui touchent les associations → @CacheEvict(cacheNames = "v1:categories:summary", allEntries = true)
        - Fichier V1: src/main/java/com/devalgas/blog/service/impl/v1/ArticleServiceImplV1.java
    - Région Ehcache:
      - Ajouter createCache(cm, "v1:categories:summary") dans src/main/java/com/devalgas/blog/config/CacheConfiguration.java
      - Option: définir une configuration Eh107 spécifique (TTL court) si souhaité.
  - Optionnel: Cache service pour Article detail:
    - @Cacheable(cacheNames = "v1:articles:detail", key = "#id") dans src/main/java/com/devalgas/blog/service/impl/v1/ArticleServiceImplV1.java
    - Éviction sur save/update/delete d’article: @CacheEvict(cacheNames = "v1:articles:detail", key = "#articleDTO.id") ou allEntries=true selon granularité.
    - À n’activer que si bénéfice confirmé par métriques.

- Stratégie d’invalidation

  - Éviction proactive après toute écriture susceptible d’impacter la vue:
    - Articles: modif des catégories, statut, date, labels → éviction categories summary, et détail si activé.
    - Catégories: modif label/code/associations → éviction categories summary.
  - Ne pas utiliser TTL longs pour ces caches service; préférer 30–120s pour limiter l’obsolescence.

- Tests et validation

  - Tests d’intégration:
    - Après écriture (article/catégorie), vérifier que le cache est vidé: premier GET reconstruit les DTO, suivants servis depuis cache.
    - Vérifier que la pagination de articles summary n’est pas mise en cache service (comportement inchangé).
  - Observabilité:
    - Activer /management/caches et analyser hit/miss.
    - Mettre des métriques sur la latence des endpoints et taux de lecture BD.

- Risques et arbitrages

  - Multiplication des régions de cache augmente la complexité d’invalidation; limiter aux endpoints “read-mostly”.
  - Les entités V1 mappent la même table que legacy; les caches L2 restent par type d’entité. Les caches service sur DTO garantissent mieux la cohérence per-endpoint que de forcer davantage L2.

- Plan d’action
  - Étape 1: ajouter le cache service “v1:categories:summary” + évictions associées.
  - Étape 2: mesurer latence et hit/miss; ajuster TTL (30–120s).
  - Étape 3 (option): activer “v1:articles:detail” si trafic très élevé, sinon rester sans cache service.
