import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import FootersUpdate from './footers-update.vue';
import FootersService from './footers.service';
import AlertService from '@/shared/alert/alert.service';

type FootersUpdateComponentType = InstanceType<typeof FootersUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const footersSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<FootersUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Footers Management Update Component', () => {
    let comp: FootersUpdateComponentType;
    let footersServiceStub: SinonStubbedInstance<FootersService>;

    beforeEach(() => {
      route = {};
      footersServiceStub = sinon.createStubInstance<FootersService>(FootersService);
      footersServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          footersService: () => footersServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(FootersUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.footers = footersSample;
        footersServiceStub.update.resolves(footersSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(footersServiceStub.update.calledWith(footersSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        footersServiceStub.create.resolves(entity);
        const wrapper = shallowMount(FootersUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.footers = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(footersServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        footersServiceStub.find.resolves(footersSample);
        footersServiceStub.retrieve.resolves([footersSample]);

        // WHEN
        route = {
          params: {
            footersId: `${footersSample.id}`,
          },
        };
        const wrapper = shallowMount(FootersUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.footers).toMatchObject(footersSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        footersServiceStub.find.resolves(footersSample);
        const wrapper = shallowMount(FootersUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
