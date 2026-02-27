# Conception Technique Détaillée — Gestion des espaces vides AdSense (no‑fill) pour TOP et FOOTER

## 1. Résumé exécutif

- Problème: les emplacements TOP et FOOTER réservent une hauteur fixe et créent des « trous » visibles quand aucune annonce n’est servie (no‑fill).
- Objectif: supprimer ou masquer ces espaces, sans dégrader l’expérience (CLS/LCP) ni violer les politiques AdSense.
- Portée: front Vue 3 (Vite), intégration AdSense existante par composants; emplacements concernés dans [app.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/app.vue).

## 2. Contexte technique

- Intégration actuelle:
  - Script AdSense injecté une seule fois via Unhead dans [main.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/main.ts).
  - Gating: `ADSENSE_ENABLED && consentGiven && adsenseScriptReady && slotXValid`.
  - Réservation de hauteur pour limiter le CLS: TOP ≈ 90–120px, FOOTER ≈ 280px.
  - Composant: [adsense.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/core/adsense/adsense.vue) et logique dans [adsense.component.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/core/adsense/adsense.component.ts).
- Limitation: AdSense ne fournit pas d’événement « rempli/non rempli » standard; la détection se fait par heuristiques (taille du `<ins>` rendue, mutation DOM, délais).

## 3. Solutions

### Solution A — Repli automatique du conteneur en cas de no‑fill (collapse)

- Description: après un délai court, si aucune annonce n’est visible (hauteur du `<ins>` à 0), réduire la hauteur réservée du conteneur à 0 et masquer l’élément.
- Implémentation (extrait à intégrer dans [adsense.component.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/core/adsense/adsense.component.ts)):

```ts
mounted() {
  const el = (this.$refs as any)?.insEl as HTMLElement | null;
  const w = window as any;
  const collapseCheck = () => {
    const h = el?.offsetHeight ?? 0;
    if (h === 0) {
      this.$emit('no-fill');
      if (this.collapseIfNoFill) {
        const wrapperEl = (el?.closest('.adsense__wrapper') as HTMLElement) || (this.$el as HTMLElement | null);
        if (wrapperEl) {
          wrapperEl.style.minHeight = '0';
          wrapperEl.style.height = '0';
          wrapperEl.style.display = 'none';
        }
      }
    }
  };
  const ensureReady = () => {
    if (w.adsbygoogle && typeof w.adsbygoogle.push === 'function') {
      w.adsbygoogle.push({});
      setTimeout(collapseCheck, 1500);
    } else {
      setTimeout(ensureReady, 300);
    }
  };
  ensureReady();
}
```

- Avantages:
  - Supprime l’espace vide de façon autonome.
  - Simple à intégrer; pas de dépendance serveur.
- Inconvénients:
  - Peut provoquer du CLS si la réduction intervient après le premier rendu.
  - Nécessite un calibrage du délai (trop court: faux positifs; trop long: trou visible).
- Atténuations:
  - Appliquer une transition visuelle discrète (opacity) plutôt qu’une animation de hauteur.
  - Effectuer le collapse sur FOOTER en priorité (impact visuel moindre qu’au TOP).

### Solution B — Fallback de contenu interne (remplacement visuel)

- Description: si no‑fill détecté, rendre un contenu maison (CTA, liens internes, « À la une », séparateur) à la place de l’annonce.
- Implémentation:
  - Wrapper autour du composant `<adsense>` dans [app.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/app.vue): si le `<ins>` reste vide (événement `no-fill`), rendre un fallback neutre (ex.: `<hr>` discret) sans introduire de CLS.
  - Exemple (TOP):
    ```vue
    <div v-if="ADSENSE_ENABLED && consentGiven && adsenseScriptReady && slotTopValid && !topNoFill" class="d-none d-lg-flex">
      <adsense
        :ad-slot="ADSENSE_SLOT_TOP"
        format="auto"
        :responsive="true"
        style="display:block;width:100%;min-height:90px"
        @no-fill="onTopNoFill"
      />
    </div>
    <div v-if="ADSENSE_ENABLED && consentGiven && adsenseScriptReady && slotTopValid && topNoFill" class="d-none d-lg-flex" style="width:100%">
      <div style="display:block;width:100%;min-height:90px">
        <hr style="opacity:0.35;border-color:var(--dark)" />
      </div>
    </div>
    ```
- Avantages:
  - Évite totalement l’espace vide sans réduire la hauteur réservée; CLS quasi nul.
  - Transforme l’emplacement en valeur utile (navigation, promotion).
- Inconvénients:
  - Doit respecter les politiques AdSense (pas de contenu trompeur/adjacent).
  - Conception UI requise; risque de distration si contenu non pertinent.
- Recommandations:
  - TOP: privilégier un bandeau fin et neutre (ex.: séparateur visuel `<hr>` discret).
  - FOOTER: bloc « À la une » ou newsletter pour valoriser l’espace.

### Solution C — Réservation conditionnelle (lazy height)

- Description: ne pas réserver de hauteur au premier rendu; n’allouer la hauteur qu’une fois le script prêt et l’`<ins>` inséré, sinon rester à 0.
- Implémentation:
  - Contrôler `style` du wrapper: `min-height:0` tant que `adsenseScriptReady` n’est pas vrai; puis passer à la hauteur cible.
- Avantages:
  - Supprime les trous quand aucune annonce n’est prévue/servie.
- Inconvénients:
  - Introduit du CLS quand l’annonce arrive (la hauteur augmente).
  - Peut impacter le LCP si TOP s’affiche après.
- Usage conseillé:
  - Éviter au TOP (impact LCP/CLS). Acceptable sur FOOTER (moins critique).

### Solution D — Auto Ads (option)

- Description: laisser AdSense placer automatiquement les annonces, souvent avec des taux de remplissage plus élevés.
- Avantages:
  - Réduit la probabilité de no‑fill aux emplacements contrôlés par AdSense.
- Inconvénients:
  - Moins de contrôle sur l’UX/CLS et l’emplacement exact.
  - Contredit le choix V1 « par emplacement » (voir [ctd-google-ads-v1.md](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/docs/ctd/ctd-google-ads-v1.md)).

### Solution E — Toggles par emplacement (pilotage env/CI)

- Description: ajouter des flags `ADSENSE_SLOT_TOP_ENABLED`/`ADSENSE_SLOT_FOOTER_ENABLED` pour masquer certains emplacements lorsque les taux de remplissage sont faibles (pilotage par CI/CD).
- Avantages:
  - Contrôle simple, réversible; supprime les trous pour les périodes à faible inventaire.
- Inconvénients:
  - Opex manuel; pas granulaire au runtime.
  - Ne résout pas les périodes intermittentes de no‑fill.

## 4. Comparaison rapide

- CLS:
  - A (collapse): possible si tardif; à minimiser par délai court et FOOTER d’abord.
  - B (fallback): le meilleur pour le CLS (pas de suppression de hauteur).
  - C (lazy): CLS à l’arrivée de l’annonce; éviter au TOP.
  - D (auto ads): dépend du placement; résultats variables.
  - E (toggles): aucun CLS si désactivé; mais perte d’inventaire.
- Effort:
  - A: faible à moyen (détection+collapse).
  - B: moyen (design+composant fallback).
  - C: faible (gating d’allocation hauteur).
  - D: faible (configuration), mais impact UX.
  - E: faible (variables d’env+conditions).
- Conformité:
  - Respecter les politiques AdSense; éviter contenu trompeur adjacent à un emplacement d’annonce.

## 5. Recommandation

- TOP:
  - Privilégier B (fallback neutre type separator) pour préserver le layout sans trou ni CLS.
  - En alternative, A (collapse) avec délai très court (<1.5s) si le fallback n’est pas souhaité, en surveillant le CLS.
- FOOTER:
  - A (collapse) recommandé: l’impact visuel est limité et le gain est immédiat.
  - B (fallback « À la une »/newsletter) si vous préférez valoriser l’espace.
- Options complémentaires:
  - E (toggles) pour désactiver ponctuellement des emplacements en période de faible inventaire.

## 6. Impacts & fichiers

- [app.vue](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/app.vue): wrapper autour TOP/FOOTER pour fallback ou collapse.
- [adsense.component.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/core/adsense/adsense.component.ts): heuristique de no‑fill (mesure `offsetHeight`, collapse).
- [main.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/main.ts): inchangé (script injecté via Unhead, gating ok).
- Variables (option E): `ADSENSE_SLOT_TOP_ENABLED`, `ADSENSE_SLOT_FOOTER_ENABLED` à ajouter dans [vite.config.mts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/vite.config.mts) et [declarations.d.ts](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/webapp/app/declarations.d.ts).

## 7. Plan de tests

- Unitaires (Vitest):
  - Simuler no‑fill: forcer `offsetHeight=0` sur `<ins>` et vérifier:
    - A: le wrapper passe à `display:none` et `min-height:0`.
    - B: rendu du composant fallback (separator/CTA).
    - C: allocation de hauteur conditionnelle au moment où `adsenseScriptReady` passe à vrai.
  - Vérifier absence d’erreurs console liées à `adsbygoogle.push`.
- Manuels:
  - Auditer Lighthouse: surveiller CLS sur pages avec TOP/FOOTER.
  - Tester en prod avec et sans remplissage; observer le comportement visuel.

## 8. Annexes

- Intégration existante: [ctd-google-ads-v1.md](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/docs/ctd/ctd-google-ads-v1.md)
- Validation: [ctd-google-adsense-validation-options.md](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/docs/ctd/ctd-google-adsense-validation-options.md)
