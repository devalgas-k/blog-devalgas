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
    const styleVars = computed(() => ({
      '--trees-image': `url('${props.imageBasePath}/arbres.png')`,
      '--premierplan-image': `url('${props.imageBasePath}/premierplanv3.png')`,
      '--secondplan-image': `url('${props.imageBasePath}/second-plan.png')`,
      '--voiture-image': `url('${props.imageBasePath}/voiture-fumee.gif')`,
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
