import { defineComponent } from 'vue';
import { useI18n } from 'vue-i18n';

/**
 * Composant d’affichage du titre principal de l’application.
 * Fournit les fonctions d’internationalisation au template.
 */
export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Title',
  components: {},
  setup() {
    return {
      t$: useI18n().t,
    };
  },
});
