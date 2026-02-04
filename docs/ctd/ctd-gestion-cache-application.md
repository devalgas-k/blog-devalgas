CTD – Gestion du cache (explication simple)

- Idée générale

  - Le cache, c’est comme une petite armoire près de nous. Plutôt que d’aller à la cave (la base de données) à chaque fois, on regarde d’abord dans l’armoire.
  - Si ce qu’on a dans l’armoire est trop vieux ou a changé, on jette et on retourne à la cave chercher du frais.

- Types de cache dans l’application

  - Cache “mémoire” pour les entités (Hibernate L2 + Ehcache)
    - Il garde en mémoire des objets “Article”, “CategoryArticle”, “User”, “Headers”, “Footers”, “AppInfo”, et leurs listes.
    - On a aussi la même chose pour les versions V1: “ArticleHomeV1”, “ArticleDetailV1”, “CategoryArticleHomeV1”, et leurs listes.
    - Où c’est configuré: [CacheConfiguration.java](src/main/java/com/devalgas/blog/config/CacheConfiguration.java)
    - Exemples d’annotations:
      - [Article.java](src/main/java/com/devalgas/blog/domain/Article.java#L20-L21)
      - [CategoryArticle.java](src/main/java/com/devalgas/blog/domain/CategoryArticle.java#L18-L19)
      - [ArticleHomeV1.java](src/main/java/com/devalgas/blog/domain/v1/ArticleHomeV1.java)
      - [ArticleDetailV1.java](src/main/java/com/devalgas/blog/domain/v1/ArticleDetailV1.java)
      - [CategoryArticleHomeV1.java](src/main/java/com/devalgas/blog/domain/v1/CategoryArticleHomeV1.java)
  - Petit cache pour les utilisateurs (Spring @Cacheable)
    - Permet de retrouver rapidement un utilisateur par login ou email.
    - Où c’est: [UserRepository.java](src/main/java/com/devalgas/blog/repository/UserRepository.java)
  - Cache HTTP pour les fichiers statiques (images, JS, CSS) en production
    - Le navigateur les garde très longtemps pour accélerer le site.
    - Où c’est: [StaticResourcesWebConfiguration.java](src/main/java/com/devalgas/blog/config/StaticResourcesWebConfiguration.java)

- Les réglages importants

  - Durée de vie en mémoire (TTL) du cache L2: 3600 secondes (1 heure).
  - Taille max:
    - Dev: 100 objets par “tiroir” de cache.
    - Prod: 1000 objets par “tiroir”.
  - Où c’est:
    - [application-dev.yml](src/main/resources/config/application-dev.yml#L52-L80) et [application-prod.yml](src/main/resources/config/application-prod.yml#L90-L105)
  - Cache de requêtes (query cache) désactivé: on n’enregistre pas les “résultats de requêtes”, seulement les objets eux-mêmes.
    - Où c’est: [application.yml](src/main/resources/config/application.yml#L121-L129)

- Quand l’armoire se vide (invalidation)

  - Si on change un objet (enregistrer, modifier, supprimer), Hibernate jette la version en mémoire et ira relire en base la prochaine fois.
  - Après un nouveau déploiement, on change les “étiquettes de clé” du cache. Résultat: les anciennes cases ne sont plus consultées, on repart propre.
  - Au bout de 1 heure (TTL), ce qui est en mémoire est considéré trop vieux et jeté.

- Ce qui n’est pas mis en cache

  - Les réponses des API dynamiques ne sont pas “stockées” par le serveur.
  - Les “requêtes” elles-mêmes ne sont pas gardées (query cache off).
  - Les fichiers statiques, eux, sont mis en cache par le navigateur (HTTP), pas par la base.

- Petit dessin du trajet

  - Demande API → Service → Hibernate
    - D’abord: “Regardons dans l’armoire (cache L2)”
    - Si trouvé: “Servez-vous !”
    - Sinon: “Allons à la cave (base de données), puis mettons un double dans l’armoire”

- Attention utile (pour éviter les surprises)

  - “Article” et ses versions V1 (“ArticleHomeV1”, “ArticleDetailV1”) pointent tous vers la même table en base, mais chaque type a son propre tiroir de cache.
  - Si on modifie via “Article”, on jette ce qui concerne “Article” dans son tiroir. Les tiroirs V1 sont séparés: on les recharge au besoin (les services lisent proprement).
  - Les listes ManyToMany (articles ↔ catégories) sont pratiques à garder, mais elles peuvent prendre de la place et changer souvent. Quand ça change, Hibernate revalide, mais on évite d’en dépendre pour fabriquer les réponses d’API: on relit ce qu’il faut quand on construit la réponse.

- En résumé
  - On garde en mémoire ce qu’on lit souvent pour aller plus vite.
  - Si ça change ou si c’est trop vieux, on jette et on relit.
  - Les V1 ont aussi leurs tiroirs pour rester rapides, mais on continue à charger comme il faut dans les services pour éviter les incohérences.
