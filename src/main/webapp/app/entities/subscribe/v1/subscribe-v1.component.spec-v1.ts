import { describe, it, expect, vitest } from 'vitest';
import { shallowMount } from '@vue/test-utils';
import { computed } from 'vue';
import flushPromises from 'flush-promises';
import Component from './subscribe-v1.vue';

describe('SubscribeV1 - reCAPTCHA', () => {
  const mountOptions: any = {
    global: {
      stubs: {
        'font-awesome-icon': true,
        VueRecaptcha: true,
        'social-media': true,
      },
      provide: {
        subscribeService: { processSubscribe: vitest.fn().mockResolvedValue({}) },
        alertService: { showSuccessCustom: vitest.fn(), showHttpError: vitest.fn() },
        currentLanguage: computed(() => 'fr'),
      },
    },
  };

  it('refuse la soumission sans reCAPTCHA vérifié', async () => {
    const wrapper = shallowMount(Component, mountOptions);
    const comp = wrapper.vm as any;
    comp.v$.subscribe.email.$model = 'john@example.com';
    comp.recaptchaVerified = false;
    const subscribeSpy = comp.subscribeService.processSubscribe;
    comp.subscribeEmail();
    await flushPromises();
    expect(comp.recaptchaError).toBe(true);
    expect(subscribeSpy).not.toHaveBeenCalled();
  });
});
