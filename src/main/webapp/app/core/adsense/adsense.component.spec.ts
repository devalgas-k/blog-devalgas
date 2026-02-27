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

  it('émet no-fill et collapse le wrapper quand collapseIfNoFill=true', async () => {
    vi.useFakeTimers();
    const w = globalThis as any;
    w.adsbygoogle = { push: vi.fn() };
    const wrapper = mount(Adsense, {
      props: {
        client: 'ca-pub-1234567890',
        adSlot: '9876543210',
        collapseIfNoFill: true,
        style: 'display:block;width:100%;min-height:90px',
      },
    });
    expect(wrapper.find('ins.adsbygoogle').exists()).toBe(true);
    vi.advanceTimersByTime(1600);
    await wrapper.vm.$nextTick();
    expect(wrapper.emitted('no-fill')).toBeTruthy();
    const rootEl = wrapper.find('.adsense__wrapper').element as any;
    expect(rootEl.style.display).toBe('none');
    vi.useRealTimers();
  });

  it('émet no-fill sans collapse quand collapseIfNoFill=false (fallback parent)', async () => {
    vi.useFakeTimers();
    const w = globalThis as any;
    w.adsbygoogle = { push: vi.fn() };
    const wrapper = mount(Adsense, {
      props: {
        client: 'ca-pub-1234567890',
        adSlot: '9876543210',
        collapseIfNoFill: false,
        style: 'display:block;width:100%;min-height:90px',
      },
    });
    vi.advanceTimersByTime(1600);
    await wrapper.vm.$nextTick();
    expect(wrapper.emitted('no-fill')).toBeTruthy();
    const rootEl = wrapper.find('.adsense__wrapper').element as any;
    expect(rootEl.style.display).not.toBe('none');
    vi.useRealTimers();
  });
});
