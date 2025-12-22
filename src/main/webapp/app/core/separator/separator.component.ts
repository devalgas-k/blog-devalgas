import { defineComponent } from 'vue';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Separator',
  components: {},
  props: {
    icon: { type: String, default: '' },
  },
  setup(props) {
    return {
      iconProp: props.icon,
    };
  },
});
