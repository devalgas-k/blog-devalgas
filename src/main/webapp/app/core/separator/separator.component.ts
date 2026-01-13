import { defineComponent, computed } from 'vue';
import { useI18n } from 'vue-i18n';

/**
 * Composant séparateur visuel avec icône et libellé optionnels.
 * Fournit t$ pour i18n et expose les props au template.
 */
export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Separator',
  components: {},
  props: {
    icon: { type: String, default: '' },
    label: { type: String, default: '' },
  },
  setup(props) {
    return {
      t$: useI18n().t,
      iconProp: props.icon,
      labelProp: props.label,
      hasLabel: computed(() => !!props.label && props.label.trim().length > 0),
    };
  },
});
