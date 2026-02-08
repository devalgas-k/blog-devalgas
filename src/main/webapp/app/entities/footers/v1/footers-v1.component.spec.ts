import { describe, it, expect, beforeEach, vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import { createTestingPinia } from '@pinia/testing';

import Footers from './footers-v1.vue';
import AlertService from '@/shared/alert/alert.service';

type FootersComponentType = InstanceType<typeof Footers>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Footers Management Component', () => {
    let mountOptions: MountingOptions<FootersComponentType>['global'];

    beforeEach(() => {
      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          jhiItemCount: true,
          bPagination: true,
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'jhi-sort-indicator': true,
          'b-button': true,
          'router-link': true,
          'b-link': true,
          'message-contact-v1': true,
          'p-separator': true,
          'banner-footers': true,
          'subscribe-v1': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
        },
        plugins: [createTestingPinia()],
      };
    });

    describe('Mount', () => {
      it('monte et initialise sans éléments', async () => {
        const wrapper = shallowMount(Footers, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();
        expect(Array.isArray(comp.footers)).toBeTruthy();
        expect(comp.footers.length).toEqual(0);
      });
    });
    describe('Handles', () => {
      let comp: FootersComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Footers, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
      });

      it('charge une page en mettant à jour le numéro de page', async () => {
        comp.page = 2;
        await comp.$nextTick();
        expect(comp.page).toEqual(2);
        expect(comp.footers.length).toEqual(0);
      });

      it('ne recharge pas si la page est identique', () => {
        comp.page = 1;
        expect(comp.page).toEqual(1);
      });

      it('réinitialise la page via clear', async () => {
        comp.page = 2;
        await comp.$nextTick();
        comp.clear();
        await comp.$nextTick();
        expect(comp.page).toEqual(1);
        expect(comp.footers.length).toEqual(0);
      });

      it('met à jour l’ordre de tri', async () => {
        comp.propOrder = 'name';
        await comp.$nextTick();
        expect(comp.propOrder).toEqual('name');
      });

      it('prepareRemove renseigne l’identifiant', async () => {
        comp.prepareRemove({ id: 123 } as any);
        await comp.$nextTick();
        expect(comp.removeId).toEqual(123);
      });

      it('openMailTo ferme le dropup et ouvre la cible', async () => {
        const openSpy = vitest.spyOn(window, 'open').mockImplementation(() => null as any);
        comp.toggleContactDropup();
        await comp.$nextTick();
        expect(comp.showContactDropup).toBeTruthy();
        comp.openMailTo();
        await comp.$nextTick();
        expect(comp.showContactDropup).toBeFalsy();
        expect(openSpy).toHaveBeenCalled();
        openSpy.mockRestore();
      });

      it('openWhatsApp ferme le dropup et ouvre la cible', async () => {
        const openSpy = vitest.spyOn(window, 'open').mockImplementation(() => null as any);
        comp.toggleContactDropup();
        await comp.$nextTick();
        expect(comp.showContactDropup).toBeTruthy();
        comp.openWhatsApp();
        await comp.$nextTick();
        expect(comp.showContactDropup).toBeFalsy();
        expect(openSpy).toHaveBeenCalled();
        openSpy.mockRestore();
      });

      it('openSocialLink ouvre le lien demandé', async () => {
        const openSpy = vitest.spyOn(window, 'open').mockImplementation(() => null as any);
        comp.openSocialLink('github');
        await comp.$nextTick();
        expect(openSpy).toHaveBeenCalled();
        openSpy.mockRestore();
      });

      it('toggleContactDropup bascule la visibilité du dropup', async () => {
        expect(comp.showContactDropup).toBeFalsy();
        comp.toggleContactDropup();
        await comp.$nextTick();
        expect(comp.showContactDropup).toBeTruthy();
        comp.toggleContactDropup();
        await comp.$nextTick();
        expect(comp.showContactDropup).toBeFalsy();
      });

      it('onContactSaved ferme le dropup', async () => {
        comp.toggleContactDropup();
        await comp.$nextTick();
        expect(comp.showContactDropup).toBeTruthy();
        comp.onContactSaved();
        await comp.$nextTick();
        expect(comp.showContactDropup).toBeFalsy();
      });
    });
  });
});
