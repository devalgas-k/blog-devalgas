# Conception Technique Détaillée — Validation AdSense et intégration (client: `ca-pub-8340083616743463`)

## Résumé exécutif

- Objectif: faire valider le site par AdSense et intégrer les annonces de façon fiable, tout en conservant le contrôle par emplacements et un CLS minimal.
- Contexte: SPA Vue 3 (Vite) + Spring Boot, gestion du `<head>` via Unhead. Le robot AdSense peut ne pas exécuter JavaScript; ce qu’il “voit” est l’HTML initial servi par le serveur.
- Décision cible: rendre visible au robot la meta `google-adsense-account` et garantir la cohérence d’`ads.txt` avec l’ID éditeur `ca-pub-8340083616743463` (`pub-8340083616743463`).

## Contraintes et périmètre

- Stack: Vue 3 + Vite, Spring Boot; Unhead pour le head.
- Fichiers clés: [index.html](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/index.html), [main.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/main.ts), [vite.config.mts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/vite.config.mts), [application.yml](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/config/application.yml), `ads.txt` en prod [static](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/static/ads.txt).
- Politique CSP: déjà permissive pour AdSense (scripts/images/frames/connect).
- Gating d’annonces: conservé (consentement/script prêt/slots valides).

## Problème

- La meta `google-adsense-account` est injectée dynamiquement via Unhead ([main.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/main.ts#L219-L220)). Si le robot n’exécute pas JavaScript, il ne la voit pas et la validation échoue.
- Des fichiers `ads.txt` multiples existent; ils doivent converger vers `pub-8340083616743463`.

## Solutions proposées

### Solution A — Meta statique dans `index.html` (front)

- Description: ajouter statiquement `<meta name="google-adsense-account" content="ca-pub-8340083616743463" />` dans l’HTML initial.
- Implémentation:
  - Éditer [index.html](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/index.html) et ajouter la meta dans `<head>`.
  - Conserver l’injection du script AdSense via Unhead (une seule fois) dans [main.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/main.ts#L212-L218).
- Avantages:
  - Visible sans JS; répond aux attentes du robot.
  - Simple et immédiat; aucun impact serveur.
- Inconvénients:
  - Valeur “durcie” dans le front; changement d’ID nécessite une édition et rebuild.
  - Risque de doublon si aussi injecté via Unhead; garder la meta uniquement côté statique.

### Solution B — Injection au build via Vite (transformIndexHtml)

- Description: injecter la meta au build (et en dev) via un plugin Vite, avec valeur pilotée par `ADSENSE_CLIENT` (`loadEnv`).
- Implémentation (extrait):

```ts
// Dans vite.config.mts
import type { Plugin } from 'vite';
const adsenseMetaPlugin = (client: string): Plugin => ({
  name: 'adsense-meta',
  transformIndexHtml(html) {
    return html.replace('</head>', `  <meta name="google-adsense-account" content="${client}" />\n</head>`);
  },
});
// ...
const env = loadEnv(mode, process.cwd(), '');
return {
  plugins: [
    vue(),
    adsenseMetaPlugin(env.ADSENSE_CLIENT || 'ca-pub-8340083616743463'),
    // autres plugins...
  ],
  define: {
    ADSENSE_CLIENT: `"${env.ADSENSE_CLIENT ? env.ADSENSE_CLIENT : 'ca-pub-8340083616743463'}"`,
    // ...
  },
};
```

- Avantages:
  - Meta présente dans l’HTML initial sans dépendre de JS.
  - Pilotage par environnement; pas de “durcissement” manuel.
- Inconvénients:
  - Ajoute une transformation HTML au build; attention aux conflits si d’autres plugins réécrivent `<head>`.
  - En dev, la valeur dépend du `.env`; s’assurer qu’elle est correctement définie.

### Solution C — Injection serveur de la meta (Spring Boot)

- Description: ajouter la meta côté serveur en post‑traitement de la ressource `index.html` (pas de SSR complet, seulement un “patch”).
- Implémentation (extrait simple):

```java
// Filtre qui capte "/" et réécrit la réponse HTML pour injecter la meta.
@Component
public class AdsenseMetaFilter implements Filter {

  private static final String META = "<meta name=\"google-adsense-account\" content=\"ca-pub-8340083616743463\" />";

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    CharResponseWrapper wrapped = new CharResponseWrapper((HttpServletResponse) res);
    chain.doFilter(req, wrapped);
    String content = wrapped.toString();
    if (content.contains("</head>")) {
      content = content.replace("</head>", META + "\n</head>");
      res.setContentLength(content.getBytes(res.getCharacterEncoding()).length);
      res.getWriter().write(content);
    } else {
      res.getWriter().write(content);
    }
  }
}

```

- Avantages:
  - Garantit la présence de la meta dans l’HTML servi, même si le front change.
  - Peut lire `ADSENSE_CLIENT` via variables d’environnement Spring pour éviter la valeur durcie.
- Inconvénients:
  - Complexité et maintenance (réécriture de réponse); attention à la compression et au cache.
  - Risque de régression performance; fragile si la structure de l’HTML change.

### Solution D — Validation alternative côté AdSense (DNS/HTML file, si proposé)

- Description: utiliser une méthode alternative de validation (DNS TXT ou fichier HTML dédié) proposée dans l’interface AdSense.
- Implémentation:
  - Suivre l’instruction AdSense: ajouter un enregistrement DNS TXT ou déposer un fichier de validation accessible publiquement (ex.: `https://devalgas.net/<fichier-validation>.html` servi par Spring statique).
- Avantages:
  - Aucun impact sur le head de l’application; rapide si DNS accessible.
- Inconvénients:
  - Toutes les régions AdSense ne proposent pas systématiquement l’alternative; dépend de l’UI du compte.
  - Ne résout pas la visibilité de la meta si AdSense l’exige explicitement pour l’intégration par ad unit.

### Solution E — Basculer vers Auto Ads (option)

- Description: utiliser Auto Ads (script unique) à la place d’“ad units” par emplacement.
- Implémentation:
  - Insérer le script Auto Ads en `index.html` ou via Unhead; laisser AdSense choisir les emplacements.
- Avantages:
  - Simplicité; validation souvent plus directe.
- Inconvénients:
  - Moins de contrôle sur l’UX et le CLS; densité d’annonces moins maîtrisée.
  - Contredit le choix V1 “par emplacement”; nécessite une politique de gating différente.

## Recommandation

- Choix appliqué: Solution A (meta statique dans `index.html`) pour garantir la visibilité sans JS et éviter les doublons avec Unhead.
- Aligner le client par défaut: `ADSENSE_CLIENT = ca-pub-8340083616743463` côté `vite.config.mts` (surchargé par env en CI/CD).
- Unifier `ads.txt` avec `pub-8340083616743463` et supprimer les duplicats (garder uniquement [static/ads.txt](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/static/ads.txt)).
- Conserver l’injection du script via Unhead et le gating actuel des composants (CLS minimal, NPA si consentement absent).

## Impacts & fichiers

- Front:
  - [index.html](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/index.html): meta statique (Solution A) ou laissée au plugin (Solution B).
  - [vite.config.mts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/vite.config.mts): `define.ADSENSE_CLIENT="ca-pub-8340083616743463"` par défaut, surchargé par env en CI/CD; slots par défaut mis à jour:
    - TOP: `1159284671`, SIDEBAR_LEFT: `6647773869`, SIDEBAR_RIGHT: `2280794651`, FOOTER: `6028467975`.
  - [main.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/main.ts): injection script AdSense via Unhead (inchangé).
- Back:
  - [application.yml](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/config/application.yml): CSP déjà compatible; rien à modifier.
  - Filtre Java (Solution C) si retenue; sinon, aucun impact serveur.
- Public:
  - `ads.txt`: conserver un seul fichier en prod [static/ads.txt](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/static/ads.txt) avec `google.com, pub-8340083616743463, DIRECT, f08c47fec0942fa0`.

## Tests & validation

- Techniques:
  - `curl -I https://devalgas.net/` puis vérifier que l’HTML initial contient `<meta name="google-adsense-account" content="ca-pub-8340083616743463">`.
  - `curl https://devalgas.net/ads.txt` doit retourner `pub-8340083616743463`.
  - Audit Lighthouse: CLS faible (grâce aux hauteurs réservées dans [app.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/app.vue) et au fallback neutre sur TOP en cas de no‑fill).
- Unitaires (front):
  - Vérifier la présence des `<ins.adsbygoogle>` quand `ADSENSE_ENABLED=true`, consentement vrai et slots valides; vérifier l’événement `no-fill` et le collapse au FOOTER.
  - Vérifier l’absence d’annonces si consentement/script faux (tests existants).
- Manuels:
  - Rejouer la validation AdSense; surveiller la console AdSense et les no‑fill.

## Annexes

- Intégration existante: [ctd-google-ads-v1.md](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/docs/ctd/ctd-google-ads-v1.md)
- Composants: [adsense.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/core/adsense/adsense.vue), [adsense.component.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/core/adsense/adsense.component.ts)
- Head: [main.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/main.ts)
- Config: [vite.config.mts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/vite.config.mts)
