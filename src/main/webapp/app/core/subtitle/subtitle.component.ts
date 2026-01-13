import { defineComponent, computed } from 'vue';

/**
 * Composant d’affichage d’un sous-titre.
 * Accepte une prop title et l’expose au template.
 */
export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Subtitle',
  components: {},
  props: {
    title: { type: String, default: '' },
  },
  setup(props) {
    return {
      subtitle: computed(() => props.title),
    };
  },
});
