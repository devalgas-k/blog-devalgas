import { defineComponent } from 'vue';
import { useI18n } from 'vue-i18n';

/**
 * Composant V1 de bloc de contact.
 * Fournit uniquement les fonctions d’internationalisation pour le template.
 */
export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MessageContactV1',
  components: {},
  setup() {
    return {
      t$: useI18n().t,
    };
  },
});
