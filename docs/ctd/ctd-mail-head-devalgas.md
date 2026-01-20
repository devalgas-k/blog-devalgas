Titre: Mise à jour / refactorisation du header des e-mails (head-devalgas)

Rôle: Tu es un architecte logiciel senior. Tu dois produire en français une mise à jour maîtrisée d’une CTD existante, avec traçabilité, compatibilité et validation de non-régression.

Objectif: Extraire l’en-tête visuel commun des gabarits e‑mail dans un fragment Thymeleaf dédié (head-devalgas.html) inspiré de app/core/title/title.vue, puis remplacer les contenus des blocs logo-container des templates e‑mail pour réutiliser ce fragment. Garantir la compatibilité des styles existants et la non‑régression.

Entrées:

- CTD source: conception-technique-mail-v1.md (v1)
- Motif: factorisation et cohérence visuelle (réduction duplication, maintenance simplifiée)
- Périmètre: templates e‑mail sous src/main/resources/templates/mail; front reference app/core/title/title.vue
- Volumétrie/SLA: inchangés; rendu côté serveur Thymeleaf
- Risques/dépendances: limites CSS des clients e‑mail (animations/SCSS non supportés), i18n de la ligne de fonction

Livrables:

- Fragment Thymeleaf head-devalgas.html
- Mise à jour des templates e‑mail pour utiliser th:replace
- CTD avec spécifications et plan de tests

1. Contexte et périmètre

- Actuel: plusieurs templates e‑mail dupliquent un header (logo, titre, sous‑titre).
- Attendu: créer un fragment réutilisable head-devalgas.html et remplacer les contenus internes des logo-container par th:replace.
- Non‑objectifs: introduire animations avancées ou SCSS; modifier styles globaux au-delà du header; transformer les e‑mails legacy (activation/reset/creation) qui n’ont pas de header.

2. Synthèse existante

- Templates e‑mail: contactMessageEmail.html, contactMessageAdminEmail.html, subscribeEmail.html (et activation/reset/creation sans header).
- Style local: classes .logo-container, .logo-title, .position-text, etc., avec CSS inline dans chaque template.
- Référence front: app/core/title/title.vue affiche “[ Devalgas ]” et une sous‑ligne fonction.

3. Objectifs et non‑régression

- Unifier l’en‑tête e‑mail sans changer le rendu final perceptible.
- Conserver les classes .logo-title et .position-text pour compatibilité CSS existante.
- Réutiliser la clé i18n email.subscribe.position pour la ligne de fonction.
- Aucun impact sur sujets, corps et signatures; aucun changement sur SMTP/Failover.

4. Décisions techniques

- Créer un fragment Thymeleaf: src/main/resources/templates/mail/head-devalgas.html avec th:fragment="logoHeader".
- Adapter le contenu à la réalité des clients e‑mail (CSS limité): utiliser le marquage simple (h1 + span + p), éviter animations/SCSS.
- Dans chaque template avec header, remplacer le contenu interne du bloc .logo-container par th:replace du fragment (en conservant le div wrapper et son style local).

5. Spécifications détaillées

- Fragment:
  - Fichier: src/main/resources/templates/mail/head-devalgas.html
  - Contenu:
    ```
    <div xmlns:th="http://www.thymeleaf.org" th:fragment="logoHeader">
      <h1 class="logo-title">[ Devalgas ]<span></span></h1>
      <p class="position-text" th:text="#{email.subscribe.position}">Software Engineer</p>
    </div>
    ```
- Templates à modifier:
  - contactMessageEmail.html:
    - Remplacer le contenu interne de .logo-container par:
      `<div th:replace="mail/head-devalgas :: logoHeader"></div>`
  - contactMessageAdminEmail.html:
    - Même remplacement.
  - subscribeEmail.html:
    - Même remplacement.
- Templates sans header (activationEmail, creationEmail, passwordResetEmail): inchangés.

6. Impacts sécurité, performance, observabilité

- Sécurité: aucun secret; pas de fuite.
- Performance: neutre; rendu Thymeleaf identique.
- Observabilité: neutre; logs inchangés.

7. Migration et rollback

- Changement atomique côté templates.
- Rollback: restaurer les contenus précédents dans .logo-container si nécessaire.

8. Tests et validation

- Tests backend: `npm run backend:unit:test` pour valider compilation et IT.
- Validation visuelle locale: envoyer des e‑mails (manual/integration) et vérifier rendu.

9. Changelog CTD

- Ajout du fragment head-devalgas.html.
- Remplacement des contenus .logo-container par th:replace du fragment.
- Aucun changement sur autres parties des e‑mails.

10. Références

- Front: [title.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/core/title/title.vue)
- Templates: [contactMessageEmail.html](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/templates/mail/contactMessageEmail.html), [contactMessageAdminEmail.html](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/templates/mail/contactMessageAdminEmail.html), [subscribeEmail.html](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/templates/mail/subscribeEmail.html)
