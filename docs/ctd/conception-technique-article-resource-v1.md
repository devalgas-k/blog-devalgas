# Conception Technique — API V1 ArticleResourceV1

## Objectif

- Exposer des endpoints “lightweight” pour la consultation des articles en versionnée V1.
- Réduire la taille des réponses via des projections JPA.
- Optimiser les performances par la mise en cache côté service.
- Conserver les en-têtes de pagination standard JHipster.

## Endpoints

- GET /api/v1/articles/summary

  - Renvoie une liste paginée de résumés d’articles (projection basique).
  - Implémentation: [ArticleResourceV1.java:L77-L91](src/main/java/com/devalgas/blog/web/rest/v1/ArticleResourceV1.java#L77-L91)
  - Service: [ArticleServiceV1Impl.java](src/main/java/com/devalgas/blog/service/impl/v1/ArticleServiceV1Impl.java)
  - Repository: [ArticleRepositoryV1.java:L38-L49](src/main/java/com/devalgas/blog/repository/v1/ArticleRepositoryV1.java#L38-L49)
  - Projections: [ArticleSummaryProjectionV1](src/main/java/com/devalgas/blog/repository/v1/projection/ArticleSummaryProjectionV1.java)
  - En-têtes: X-Total-Count, Link (générés par PaginationUtil)
  - Statut: 200

- GET /api/v1/articles/summary/{id}
  - Renvoie le détail “basique” d’un article par son id (projection).
  - Implémentation: [ArticleResourceV1.java:L93-L99](src/main/java/com/devalgas/blog/web/rest/v1/ArticleResourceV1.java#L93-L99)
  - Service: [ArticleServiceV1Impl.java](src/main/java/com/devalgas/blog/service/impl/v1/ArticleServiceV1Impl.java)
  - Repository: [ArticleRepositoryV1.java:L51-L57](src/main/java/com/devalgas/blog/repository/v1/ArticleRepositoryV1.java#L51-L57)
  - Projection: [ArticleDetailsBasicProjectionV1.java](src/main/java/com/devalgas/blog/repository/v1/projection/ArticleDetailsBasicProjectionV1.java)
  - Statuts: 200 si trouvé, 404 si absent (ResponseUtil.wrapOrNotFound)
  - Paramètre optionnel: `lang=fr|en` pour ne renvoyer que le markdown ciblé; si absent ou autre valeur, renvoie les deux flux (FR/EN).
  - Exemples:
    - `/api/v1/articles/summary/1?lang=fr`
    - `/api/v1/articles/summary/1?lang=en`
    - `/api/v1/articles/summary/1` (comportement existant: FR + EN)

## Modèle de données exposé (Projections)

- Résumé (summary):
  - id, labelFr, labelEn, date
  - Source: [ArticleRepositoryV1.java:L45-L49](src/main/java/com/devalgas/blog/repository/v1/ArticleRepositoryV1.java#L45-L49)
- Détail basique (details):
  - id, labelFr, labelEn, markdownFr, markdownFrContentType, markdownEn, markdownEnContentType, date
  - Source: [ArticleDetailsBasicProjectionV1.java](src/main/java/com/devalgas/blog/repository/v1/projection/ArticleDetailsBasicProjectionV1.java)

### Requêtes par langue (optimisation)

- Détail FR uniquement: [ArticleRepositoryV1.java:L59-L66](src/main/java/com/devalgas/blog/repository/v1/ArticleRepositoryV1.java#L59-L66)
- Détail EN uniquement: [ArticleRepositoryV1.java:L68-L75](src/main/java/com/devalgas/blog/repository/v1/ArticleRepositoryV1.java#L68-L75)
- Sans `lang`: requête d’origine renvoyant FR+EN: [ArticleRepositoryV1.java:L51-L57](src/main/java/com/devalgas/blog/repository/v1/ArticleRepositoryV1.java#L51-L57)

## Pagination et En-têtes

- Liste summary:
  - Pagination Spring (Pageable)
  - En-têtes générés: X-Total-Count, Link
  - Source: [ArticleResourceV1.java:L84-L91](src/main/java/com/devalgas/blog/web/rest/v1/ArticleResourceV1.java#L84-L91)

## Caching

- Caches Ehcache (JSR-107):
  - articlesSummaryV1: liste paginée des résumés
  - articleDetailsV1: détail basique par id
  - Configuration: [CacheConfiguration.java:L45-L68](src/main/java/com/devalgas/blog/config/CacheConfiguration.java#L45-L68)
- Clé de cache:
  - summary: `pageNumber-size-sort-count()` (compte total pour éviter incohérences)
  - details: `id-lang` (avec `lang='all'` quand aucun paramètre n’est fourni), pour distinguer FR/EN et le cas “complet”.
  - Source: [ArticleServiceV1Impl.java:L44-L56](src/main/java/com/devalgas/blog/service/impl/v1/ArticleServiceV1Impl.java#L44-L56)
- TTL:
  - Défini via JHipsterProperties (par défaut 3600s), ajustable par profil.
- Désactivation en test:
  - Cache désactivé dynamiquement en profils “test”, “testdev”, “testprod”.
- Invalidation:
  - À chaque save/update/partialUpdate/delete: éviction complète des caches V1.
  - Source: [ArticleServiceImpl.java:L35-L55](src/main/java/com/devalgas/blog/service/impl/ArticleServiceImpl.java#L35-L55) et [ArticleServiceImpl.java:L87-L93](src/main/java/com/devalgas/blog/service/impl/ArticleServiceImpl.java#L87-L93)

## Transactions

- Les méthodes V1 côté service sont annotées `@Transactional(readOnly = true)` pour réduire le coût des transactions lors des lectures.
- Source: [ArticleServiceV1Impl.java](src/main/java/com/devalgas/blog/service/impl/v1/ArticleServiceV1Impl.java)

## Erreurs et Codes

- 200: réponse OK
- 404: article non trouvé (endpoint détail)
- Génération: ResponseUtil.wrapOrNotFound (V1 détail)

## Sécurité

- Par défaut, endpoints V1 accessibles publiquement (aucune restriction spécifique dans SecurityConfiguration sur /api/v1/articles).
- Ajustable via SecurityConfiguration si besoin.

## Performance et Considérations

- Projections JPA pour limiter la taille des payloads
- Cache Ehcache pour réduire la latence en lecture
- Invalidation agressive lors des mutations pour éviter stale data
- Pagination standard pour contrôler le volume transféré

## Exemples de Réponse

### GET /api/v1/articles/summary

```json
[
  { "id": 1, "labelFr": "Titre FR", "labelEn": "Title EN", "date": "1970-01-01T00:00:00Z" },
  { "id": 2, "labelFr": "Autre FR", "labelEn": "Other EN", "date": "1970-01-02T00:00:00Z" }
]
```

### GET /api/v1/articles/summary/123

```json
{
  "id": 123,
  "labelFr": "Titre FR",
  "labelEn": "Title EN",
  "markdownFrContentType": "text/markdown",
  "markdownFr": "BASE64...",
  "markdownEnContentType": "text/markdown",
  "markdownEn": "BASE64...",
  "date": "1970-01-01T00:00:00Z"
}
```

### GET /api/v1/articles/summary/123?lang=fr

```json
{
  "id": 123,
  "labelFr": "Titre FR",
  "markdownFrContentType": "text/markdown",
  "markdownFr": "BASE64...",
  "date": "1970-01-01T00:00:00Z"
}
```

### GET /api/v1/articles/summary/123?lang=en

```json
{
  "id": 123,
  "labelEn": "Title EN",
  "markdownEnContentType": "text/markdown",
  "markdownEn": "BASE64...",
  "date": "1970-01-01T00:00:00Z"
}
```
