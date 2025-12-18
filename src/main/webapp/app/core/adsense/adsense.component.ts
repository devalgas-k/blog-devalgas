import { computed, defineComponent } from 'vue';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Adsense',
  props: {
    client: { type: String, default: 'ca-pub-6181972205565553' },
    adSlot: { type: String, default: '4433984685' },
    format: { type: String, default: 'auto' },
    responsive: { type: Boolean, default: true },
    test: { type: Boolean, default: import.meta.env.DEV },
    npa: { type: Boolean, default: import.meta.env.DEV },
    layout: { type: String, default: '' },
    adLayoutKey: { type: String, default: '' },
    style: { type: String, default: 'display:block;width:100%;min-height:120px' },
  },
  setup(props) {
    const adTest = computed(() => (props.test ? 'on' : ''));
    const adKey = computed(() => `${props.client}-${props.adSlot}-${adTest.value}-${props.format}-${props.layout}-${props.adLayoutKey}`);
    return {
      dataAdClient: props.client,
      dataAdSlot: props.adSlot,
      dataAdFormat: props.format,
      dataFullWidthResponsive: props.responsive,
      dataAdTest: adTest,
      isNpa: props.npa,
      adLayout: props.layout,
      adLayoutKeyAttr: props.adLayoutKey,
      insStyle: props.style,
      adKey,
    };
  },
  mounted() {
    const w = window as any;
    const push = () => {
      if (w.adsbygoogle && typeof w.adsbygoogle.push === 'function') {
        if (this.isNpa) {
          w.adsbygoogle.requestNonPersonalizedAds = 1;
        }
        w.adsbygoogle.push({});
      }
    };
    const wait = () => {
      if (w.adsbygoogle && typeof w.adsbygoogle.push === 'function') {
        push();
      } else {
        setTimeout(wait, 300);
      }
    };
    wait();
  },
});
