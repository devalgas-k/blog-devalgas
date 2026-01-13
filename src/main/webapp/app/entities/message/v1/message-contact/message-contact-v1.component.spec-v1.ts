import { describe, it, expect, vitest } from 'vitest';
import { shallowMount } from '@vue/test-utils';
import { computed } from 'vue';
import flushPromises from 'flush-promises';
import Component from './message-contact-v1.vue';

describe('MessageContactV1 - reCAPTCHA', () => {
  const subject = { id: 1, titleFr: 'Sujet', titleEn: 'Subject' };

  const mountOptions: any = {
    global: {
      stubs: {
        'font-awesome-icon': true,
        'b-form-input': true,
        VueRecaptcha: true,
      },
      provide: {
        messageService: (() => {
          const svc = { create: vitest.fn().mockResolvedValue({ id: 123 }) };
          return () => svc;
        })(),
        alertService: { showSuccessCustom: vitest.fn(), showHttpError: vitest.fn() },
        subjectService: () => ({ retrieve: vitest.fn().mockResolvedValue({ data: [subject] }) }),
        currentLanguage: computed(() => 'fr'),
      },
    },
  };

  it('refuse la sauvegarde sans reCAPTCHA vérifié', async () => {
    const wrapper = shallowMount(Component, mountOptions);
    const comp = wrapper.vm as any;
    comp.v$.name.$model = 'John';
    comp.v$.email.$model = 'john@example.com';
    comp.v$.message.$model = 'Hello';
    comp.v$.subject.$model = subject;
    comp.recaptchaVerified = false;
    const createSpy = comp.messageService().create;
    comp.save();
    await flushPromises();
    expect(comp.recaptchaError).toBe(true);
    expect(createSpy).not.toHaveBeenCalled();
  });
});
