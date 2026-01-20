# Rétro‑conception technique — Changement de thème (Solar/Slate)

## Architecture

- Vue 3 (compat) + Pinia + Bootstrap 4 + Bootswatch 4.
- Deux thèmes compilés et « scoppés » dans un unique `vendor.scss` via des classes de portée appliquées sur `html`:
  - `:root.theme-light` → Bootswatch Solar.
  - `:root.theme-dark` → Bootswatch Slate.

## Points d’entrée

- Injection des APIs thème au bootstrap de l’app:
  - `currentTheme` (computed) et `changeTheme` (action) fournis au scope d’injection.
  - Application immédiate du thème à l’initialisation avec ajout de classe sur `document.documentElement`.
  - Référence: `src/main/webapp/app/main.ts:72` (applyTheme), `src/main/webapp/app/main.ts:78` (initialisation), `src/main/webapp/app/main.ts:98` (provide currentTheme), `src/main/webapp/app/main.ts:101` (provide changeTheme).

## Flux d’exécution

- Démarrage:
  - Lecture du stockage: `localStorage.getItem('currentTheme')`.
  - Si absent, le thème par défaut est `light` (Solar).
  - Application de `theme-light` ou `theme-dark` sur `html` via `applyTheme`.
- Interaction utilisateur:
  - Dans `headers-v1.vue`, le menu « thèmes » affiche `Solar`/`Slate` et appelle `changeTheme(key)`.
  - `changeTheme` met à jour `localStorage`, la valeur réactive, puis ré-applique la classe.

## Persistance

- Clé: `currentTheme` (valeurs `light` ou `dark`).
- Persistance locale uniquement (navigateur), aucune propagation serveur.
- Référence: `src/main/webapp/app/main.ts:77` (lecture), `src/main/webapp/app/main.ts:82` (écriture).

## UI — Intégration

- Menu des thèmes:
  - Référence: `src/main/webapp/app/entities/headers/v1/headers-v1.vue:96` (dropdown), `src/main/webapp/app/entities/headers/v1/headers-v1.vue:102` (items), `src/main/webapp/app/entities/headers/v1/headers-v1.vue:104` (action), `src/main/webapp/app/entities/headers/v1/headers-v1.vue:105` (état actif).
- Composant `headers-v1`:
  - Consomme `currentTheme` et `changeTheme` via `inject`.
  - Référence: `src/main/webapp/app/entities/headers/v1/headers-v1.component.ts:24` (inject currentTheme), `src/main/webapp/app/entities/headers/v1/headers-v1.component.ts:33` (inject changeTheme), `src/main/webapp/app/entities/headers/v1/headers-v1.component.ts:41` (isActiveTheme).

## Styles — Scoping des thèmes

- Compilation des deux thèmes dans `vendor.scss` avec scoping par classe:
  - `:root.theme-light { @import 'bootswatch/dist/solar/...'; @import 'bootstrap/scss/bootstrap'; @import 'bootstrap-vue/src/index.scss'; }`
  - `:root.theme-dark { @import 'bootswatch/dist/slate/...'; @import 'bootstrap/scss/bootstrap'; @import 'bootstrap-vue/src/index.scss'; }`
- Avantages:
  - Switch runtime sans recharger de CSS.
  - Cohérence Bootstrap/Bootswatch.
- Considérations:
  - Bundle CSS plus volumineux (deux thèmes empaquetés).
  - Toute règle importée est préfixée par la classe de portée.
- Référence: `src/main/webapp/content/scss/vendor.scss:8` (Solar), `src/main/webapp/content/scss/vendor.scss:18` (Slate).

## Libellés des thèmes

- Source des libellés pour le menu:
  - `light: 'Solar'`, `dark: 'Slate'`.
  - Référence: `src/main/webapp/app/shared/config/themes.ts:1`.

## Paramétrage par défaut

- Thème par défaut: `Solar` (clé `light`).
- Référence: `src/main/webapp/app/main.ts:78`.

## Tests

- Spécifique à `headers-v1`:
  - Vérifie noms de thèmes et état actif.
  - Vérifie l’appel de `changeTheme`.
  - Référence: `src/main/webapp/app/entities/headers/v1/headers-v1.component.spec.ts`.

## Alternatives et trade‑offs

- Switch par `<link rel="stylesheet">`:
  - Pros: bundle initial plus léger, chargement conditionnel.
  - Cons: flash de styles lors du switch, gestion de cache, complexité du loader.
- Store dédié au thème (Pinia):
  - Pros: centralise l’état et réutilisable cross‑composants.
  - Cons: apporte une dépendance de plus (non nécessaire ici grâce à l’injection).

## Extension — Ajouter un thème

- Ajouter libellé dans `src/main/webapp/app/shared/config/themes.ts`.
- Étendre `vendor.scss` avec un bloc scoppé supplémentaire.
- Adapter `applyTheme` (ajout de la classe correspondante).
- Aucun changement dans `headers-v1.vue` si la clé/valeur suit le même modèle.

## Sécurité / Performance

- Pas de données sensibles manipulées (localStorage simple).
- Surveillez la taille CSS; un code split manuel peut être envisagé si le bundle dépasse vos contraintes.

## Résumé

- Le mécanisme repose sur:
  - Injection de `currentTheme`/`changeTheme`.
  - Persistance locale du choix.
  - Scoping CSS pour charger Solar/Slate et basculer à la volée.
  - Solar est le thème par défaut.

## Pseudo‑code

### Bootstrap thème global

```pseudo
function initThemeAndProvide():
  theme = localStorage.get('currentTheme')
  if theme is null:
    theme = 'light'  // par défaut: Solar

  applyTheme(theme)

  provide('currentTheme', computed(() => theme))
  provide('changeTheme', async (newTheme) => {
    if theme != newTheme:
      theme = newTheme
      localStorage.set('currentTheme', newTheme)
      applyTheme(newTheme)
  })

// Réf: src/main/webapp/app/main.ts:72,78,98,101
```

### Application du thème

```pseudo
function applyTheme(theme):
  root = document.documentElement
  root.classList.remove('theme-light', 'theme-dark')
  if theme == 'dark':
    root.classList.add('theme-dark')
  else:
    root.classList.add('theme-light')

// Réf: src/main/webapp/app/main.ts:72
```

### Interaction UI (headers‑v1)

```pseudo
component HeadersV1.setup():
  currentTheme = inject('currentTheme')
  changeTheme = inject('changeTheme')

  function isActiveTheme(key):
    return key == currentTheme.value

  onDropdownItemClick(key):
    await changeTheme(key)

// Réf: src/main/webapp/app/entities/headers/v1/headers-v1.component.ts:24,33,41
// Réf: src/main/webapp/app/entities/headers/v1/headers-v1.vue:96,102,104,105
```

### Scoping CSS des thèmes

```pseudo
:root.theme-light {
  import bootswatch solar variables
  import bootstrap
  import bootstrap-vue
  import bootswatch solar theme
}

:root.theme-dark {
  import bootswatch slate variables
  import bootstrap
  import bootstrap-vue
  import bootswatch slate theme
}

// Réf: src/main/webapp/content/scss/vendor.scss:8,18
```

### Libellés des thèmes

```pseudo
themes = {
  light: { name: 'Solar' },
  dark: { name: 'Slate' },
}

// Réf: src/main/webapp/app/shared/config/themes.ts:1
```

### Extension: ajouter un nouveau thème

```pseudo
function addTheme(key, label, bootswatchVariablesPath, bootswatchThemePath):
  themes[key] = { name: label }
  vendor.scss add block :root.theme-<key> with imports
  applyTheme support class 'theme-<key>'

// Impacts: themes.ts, vendor.scss, applyTheme()
```

### Tests (extraits)

```pseudo
test 'libellés des thèmes présents':
  mount HeadersV1 with providers
  expect(comp.themes.light.name == 'Solar')
  expect(comp.themes.dark.name == 'Slate')

test 'changeTheme est appelé':
  provide changeTheme stub
  await comp.changeTheme('light')
  expect(changeTheme).toHaveBeenCalledWith('light')

// Réf: src/main/webapp/app/entities/headers/v1/headers-v1.component.spec.ts
```
