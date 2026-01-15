import { defineComponent, computed } from 'vue';
import { useI18n } from 'vue-i18n';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'DPBanner',
  components: {},
  props: {
    imageBasePath: { type: String, default: IMAGE_BASE_PATH },
    showParallax: { type: Boolean, default: true },
    parallaxDuration: { type: String, default: '600s' },
    motoDuration: { type: String, default: '5s' },
    voitureDuration: { type: String, default: '1s' },
    treesDuration: { type: String, default: '1000s' },
    premierplanDuration: { type: String, default: '500s' },
    secondplanDuration: { type: String, default: '600s' },
  },
  setup(props) {
    const imageBasePath = computed(() => props.imageBasePath || ((import.meta as any).env?.VITE_IMAGE_BASE_PATH ?? '/content/images'));
    const styleVars = computed(() => ({
      '--trees-image': `url('${imageBasePath.value}/arbres.png')`,
      '--premierplan-image': `url('${imageBasePath.value}/premierplanv3.png')`,
      '--secondplan-image': `url('${imageBasePath.value}/second-plan.png')`,
      '--voiture-image': `url('${imageBasePath.value}/voiture-fumee.gif')`,
      '--parallax-duration': props.parallaxDuration,
      '--moto-duration': props.motoDuration,
      '--voiture-duration': props.voitureDuration,
      '--trees-duration': props.treesDuration,
      '--premierplan-duration': props.premierplanDuration,
      '--secondplan-duration': props.secondplanDuration,
    }));
    return {
      t$: useI18n().t,
      styleVars,
    };
  },
});
