import { describe, it, expect, vi } from 'vitest';
import { mount } from '@vue/test-utils';
import Adsense from './adsense.vue';

describe('Adsense component', () => {
  it('rend un <ins class="adsbygoogle"> et pousse adsbygoogle', async () => {
    const w = globalThis as any;
    w.adsbygoogle = {
      push: vi.fn(),
    };
    const wrapper = mount(Adsense, {
      props: {
        client: 'ca-pub-1234567890',
        adSlot: '9876543210',
        format: 'auto',
        responsive: true,
        style: 'display:block;width:100%;min-height:120px',
        test: true,
        npa: false,
      },
    });
    expect(wrapper.find('ins.adsbygoogle').exists()).toBe(true);
    expect(w.adsbygoogle.push).toHaveBeenCalledTimes(1);
  });

  it('active NPA quand npa=true', async () => {
    const w = globalThis as any;
    w.adsbygoogle = {
      push: vi.fn(),
    };
    const wrapper = mount(Adsense, {
      props: {
        client: 'ca-pub-1234567890',
        adSlot: '9876543210',
        npa: true,
      },
    });
    expect(wrapper.find('ins.adsbygoogle').exists()).toBe(true);
    expect(w.adsbygoogle.requestNonPersonalizedAds).toBe(1);
    expect(w.adsbygoogle.push).toHaveBeenCalledTimes(1);
  });
});
