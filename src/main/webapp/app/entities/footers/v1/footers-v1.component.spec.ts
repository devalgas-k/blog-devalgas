import { describe, it, expect, beforeEach, vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { createTestingPinia } from '@pinia/testing';

import Footers from './footers-v1.vue';
import FootersService from '../footers.service';
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
    let footersServiceStub: SinonStubbedInstance<FootersService>;
    let mountOptions: MountingOptions<FootersComponentType>['global'];

    beforeEach(() => {
      footersServiceStub = sinon.createStubInstance<FootersService>(FootersService);
      footersServiceStub.retrieve.resolves({ headers: {} });

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
          footersService: () => footersServiceStub,
        },
        plugins: [createTestingPinia()],
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        footersServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Footers, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(footersServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.footers[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(Footers, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(footersServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: FootersComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Footers, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        footersServiceStub.retrieve.reset();
        footersServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        footersServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(footersServiceStub.retrieve.called).toBeTruthy();
        expect(comp.footers[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(footersServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        footersServiceStub.retrieve.reset();
        footersServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(footersServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.footers[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(footersServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        footersServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeFooters();
        await comp.$nextTick(); // clear components

        // THEN
        expect(footersServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(footersServiceStub.retrieve.callCount).toEqual(1);
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
