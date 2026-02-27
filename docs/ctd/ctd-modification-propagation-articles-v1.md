Titre: Mise à jour / Propagation des mises à jour Article vers API v1

Rôle: Tu es un architecte logiciel senior. Tu dois produire en français une mise à jour maîtrisée d’une CTD existante, avec traçabilité, compatibilité et validation de non‑régression.

Objectif: Garantir que les mises à jour via PUT /api/articles/{id} sont immédiatement prises en compte par les APIs v1:

- GET /api/v1/category-articles/summary
- GET /api/v1/articles/summary/{id}

Entrées:

- Source: [prompt.md](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/prompt.md)
- Contrôleurs v1: [ArticleResourceV1.java](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/web/rest/v1/ArticleResourceV1.java), [CategoryArticleResourceV1.java](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/web/rest/v1/CategoryArticleResourceV1.java)
- Contrôleur legacy write: [ArticleResource.java](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/web/rest/ArticleResource.java)
- Services v1: [ArticleServiceImplV1.java](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/service/impl/v1/ArticleServiceImplV1.java), [CategoryArticleServiceImplV1.java](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/service/impl/v1/CategoryArticleServiceImplV1.java)
- Repositories v1: [ArticleHomeRepositoryV1.java](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/repository/v1/ArticleHomeRepositoryV1.java), [ArticleDetailRepositoryV1.java](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/repository/v1/ArticleDetailRepositoryV1.java)
- Domaines v1 (lecture, même table “article”): [ArticleHomeV1.java](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/domain/v1/ArticleHomeV1.java), [ArticleDetailV1.java](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/domain/v1/ArticleDetailV1.java)
- Caches: [CacheConfiguration.java](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/config/CacheConfiguration.java), TTL Ehcache dans [application-local.yml](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/config/application-local.yml#L94-L99), [application-dev.yml](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/config/application-dev.yml#L50-L57), [application-prod.yml](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/config/application-prod.yml#L97-L103)

1. Contexte et périmètre

- Actuel: les endpoints v1 lisent via des projections ArticleHomeV1/ArticleDetailV1 mappées sur la même table “article” que l’entité write Article. Le cache L2 (Ehcache JSR‑107) est activé sur ces entités v1.
- Problème: après PUT /api/articles/{id}, les lectures v1 peuvent retourner des données périmées si les caches L2 des entités v1 ne sont pas évincés, car l’écriture s’opère via l’entité Article (classe différente).
- Non‑objectifs: refonte du modèle de données, changement de base de données, modification des endpoints front.

2. Synthèse de la CTD existante

- Lecture v1:
  - Liste: ArticleServiceV1.findAllArticlesHome() → [ArticleHomeRepositoryV1.findByStatusAndDisplayWithEagerRelationships](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/repository/v1/ArticleHomeRepositoryV1.java#L32-L36)
  - Détail: ArticleServiceV1.findOneArticleDetails() → [ArticleDetailRepositoryV1.findOneWithEagerRelationships](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/repository/v1/ArticleDetailRepositoryV1.java#L16-L20)
- Écriture: POST/PUT/PATCH/DELETE via [ArticleResource](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/web/rest/ArticleResource.java#L84-L105)
- Cache: Ehcache configuré avec TTL par profil; caches par classe JPA (ArticleHomeV1, ArticleDetailV1, CategoryArticleHomeV1).

3. Objectifs de modification et critères de non‑régression

- Après tout write sur /api/articles, les lectures v1 reflètent immédiatement la dernière version des champs (label, description, markdown\*, status, date, display, categories).
- Contrats d’API inchangés; pagination/tri identiques; associations ManyToMany cohérentes.
- Pas de fuite de secrets; performances préservées (impacts mesurés).

4. Décisions techniques et architecture cible
   Plusieurs solutions sont possibles; choisir selon environnement/contraintes.

Solution A — Éviction croisée des caches L2 après write (recommandée)

- Principe: au niveau du service d’écriture, évincer explicitement les régions de cache des entités v1 mappées sur la même table “article” (ArticleHomeV1, ArticleDetailV1) et des catégories si nécessaire.
- Bénéfices: propagation immédiate, faible risque, code localisé; lecture v1 garde son eager fetch performant.
- Points d’attention: bien cibler les classes v1 (cache par classe); ne pas évincer globalement sans nécessité.
- Patch proposé (indicatif):

```
// ArticleServiceImplV1: ajouter une méthode evictV1Caches(id) et l’appeler après save/update/patch/delete
// Eviction via entityManager.getEntityManagerFactory().getCache().evict(...)
// + cacheManager.getCache(region).clear() sur:
//  - com.devalgas.blog.domain.v1.ArticleHomeV1
//  - com.devalgas.blog.domain.v1.ArticleHomeV1.categoryArticles
//  - com.devalgas.blog.domain.v1.ArticleDetailV1
//  - com.devalgas.blog.domain.v1.ArticleDetailV1.categoryArticles
//  - com.devalgas.blog.domain.v1.CategoryArticleHomeV1
//  - com.devalgas.blog.domain.v1.CategoryArticleHomeV1.articles
```

Solution B — Ajustement de la configuration Ehcache (TTL faible ou désactivation ciblée)

- Principe: réduire le TTL des caches en dev/local pour rapprocher l’état des lectures v1, ou désactiver le cache L2 pour les entités v1 en retirant @Cache sur les classes v1.
- Bénéfices: aucune logique d’éviction; simplicité en dev/local.
- Points d’attention: impact performance en prod; désactiver sélectivement plutôt que globalement.
- Blocs de configuration (exacts, profils dev/local):

```
jhipster:
  cache:
    ehcache:
      time-to-live-seconds: 1
      max-entries: 100
```

- Variante prod prudente:

```
jhipster:
  cache:
    ehcache:
      time-to-live-seconds: 60
      max-entries: 1000
```

Solution C — Lecture native sans L2 pour la liste

- Principe: utiliser la variante native existante [findByStatusAndDisplayNativeWithEagerRelationships](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/repository/v1/ArticleHomeRepositoryV1.java#L38-L47) côté service pour la liste, ce qui contourne les artefacts de cache L2 sur certaines projections.
- Bénéfices: correction rapide sur la liste; pas de changement de contrat.
- Points d’attention: cohérence avec le détail; garder le tri et la pagination.
- Patch proposé (indicatif):

```
// ArticleServiceImplV1.findAllArticlesHome(...)
// Remplacer findByStatusAndDisplayWithEagerRelationships(...) par findByStatusAndDisplayNativeWithEagerRelationships(...)
```

Solution D — Vue matérialisée dédiée aux lectures v1 (option architecturale)

- Principe: créer une vue matérialisée pour les projections v1 (home/detail) rafraîchie après write, réduisant le couplage multi‑classe sur la même table.
- Bénéfices: isolation forte lecture/écriture, performance de lecture stable.
- Points d’attention: migration DB, complexité; non nécessaire si A/B/C suffisent.

5. Spécifications détaillées mises à jour

- API inchangées.
- Écriture: ajouter éviction croisée (Solution A) dans le service write.
- Lecture liste: option C bascule sur méthode native.
- Configuration: option B ajuste TTL par profil.

Blocs de configuration exacts:
application-local.yml:

```
jhipster:
  cache:
    ehcache:
      time-to-live-seconds: 1
      max-entries: 100
```

application-dev.yml:

```
jhipster:
  cache:
    ehcache:
      time-to-live-seconds: 3600
      max-entries: 100
```

application-prod.yml:

```
jhipster:
  cache:
    ehcache:
      time-to-live-seconds: 60
      max-entries: 1000
```

6. Impacts sécurité, performance et observabilité

- Sécurité: aucune fuite; endpoints inchangés; pas d’exposition de secrets.
- Performance: Solution A a un coût d’éviction faible; Solution B peut dégrader légèrement le hit‑rate; Solution C conserve eager et charge DB native.
- Observabilité: tracer les writes et les évictions; conserver les logs existants; métriques inchangées.

7. Plan de migration et rollback

- Dev/local: appliquer Solution B (TTL faible) pour valider rapidement.
- Prod: privilégier Solution A; rollback en retirant l’éviction si side‑effects inattendus.
- Option C: bascule contrôlée sur native si nécessaire; rollback trivial.

8. Plan de tests et validation

- Scénario:
  - Étape 1: PUT /api/articles/{id} avec modifications sur labelFr/labelEn/description* et markdown*.
  - Étape 2: GET /api/v1/articles/summary/{id} → vérifier les champs modifiés.
  - Étape 3: GET /api/v1/category-articles/summary → vérifier que l’article modifié apparaît avec les nouvelles valeurs.
- Tests backend locaux:
  - `npm run backend:unit:test`
- Validation manuelle:
  - Lancer l’app; exécuter les requêtes; observer qu’aucune valeur périmée ne subsiste.

9. Changelog CTD

- Ajout de la propagation via éviction croisée (Solution A).
- Alternatives de configuration des caches (Solution B).
- Option de lecture native pour la liste (Solution C).
- Option architecturale via vue matérialisée (Solution D).

10. Fichiers à modifier (selon la solution retenue)

- Article write: [ArticleServiceImplV1.java](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/service/impl/v1/ArticleServiceImplV1.java)
- Lecture liste: [ArticleHomeRepositoryV1.java](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/repository/v1/ArticleHomeRepositoryV1.java)
- Configuration TTL: [application-local.yml](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/config/application-local.yml), [application-dev.yml](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/config/application-dev.yml), [application-prod.yml](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/config/application-prod.yml)
