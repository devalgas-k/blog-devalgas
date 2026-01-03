import { describe, it, expect, vitest } from 'vitest';
import { shallowMount } from '@vue/test-utils';
import { computed } from 'vue';
import flushPromises from 'flush-promises';
import Component from './subscribe-v1.vue';

describe('SubscribeV1', () => {
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

  it("réinitialise le formulaire après succès et affiche l'état initial", async () => {
    const wrapper = shallowMount(Component, mountOptions);
    const comp = wrapper.vm as any;
    comp.v$.subscribe.email.$model = 'john@example.com';
    comp.recaptchaVerified = true;
    const subscribeSpy = comp.subscribeService.processSubscribe;
    const toastSpy = comp.alertService.showSuccessCustom;
    comp.subscribeEmail();
    await flushPromises();
    expect(subscribeSpy).toHaveBeenCalled();
    expect(comp.success).toBe(false);
    expect(comp.v$.$invalid).toBe(true);
    expect(comp.recaptchaVerified).toBe(false);
    expect(toastSpy).toHaveBeenCalled();
  });
});
