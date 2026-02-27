import { computed, defineComponent } from 'vue';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Adsense',
  emits: ['no-fill'],
  props: {
    client: { type: String, default: ADSENSE_CLIENT },
    adSlot: { type: String, default: ADSENSE_SLOT },
    format: { type: String, default: 'auto' },
    responsive: { type: Boolean, default: true },
    test: { type: Boolean, default: import.meta.env.DEV },
    npa: { type: Boolean, default: import.meta.env.DEV },
    layout: { type: String, default: '' },
    adLayoutKey: { type: String, default: '' },
    style: { type: String, default: 'display:block;width:100%;min-height:120px' },
    collapseIfNoFill: { type: Boolean, default: false },
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
    const el = (this.$refs as any)?.insEl as HTMLElement | null;
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
    const push = () => {
      if (w.adsbygoogle && typeof w.adsbygoogle.push === 'function') {
        if (this.isNpa) {
          w.adsbygoogle.requestNonPersonalizedAds = 1;
        }
        w.adsbygoogle.push({});
        setTimeout(collapseCheck, 1500);
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
