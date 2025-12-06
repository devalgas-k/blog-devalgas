import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import HeadersUpdate from './headers-update.vue';
import HeadersService from './headers.service';
import AlertService from '@/shared/alert/alert.service';

type HeadersUpdateComponentType = InstanceType<typeof HeadersUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const headersSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<HeadersUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Headers Management Update Component', () => {
    let comp: HeadersUpdateComponentType;
    let headersServiceStub: SinonStubbedInstance<HeadersService>;

    beforeEach(() => {
      route = {};
      headersServiceStub = sinon.createStubInstance<HeadersService>(HeadersService);
      headersServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          headersService: () => headersServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(HeadersUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.headers = headersSample;
        headersServiceStub.update.resolves(headersSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(headersServiceStub.update.calledWith(headersSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        headersServiceStub.create.resolves(entity);
        const wrapper = shallowMount(HeadersUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.headers = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(headersServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        headersServiceStub.find.resolves(headersSample);
        headersServiceStub.retrieve.resolves([headersSample]);

        // WHEN
        route = {
          params: {
            headersId: `${headersSample.id}`,
          },
        };
        const wrapper = shallowMount(HeadersUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.headers).toMatchObject(headersSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        headersServiceStub.find.resolves(headersSample);
        const wrapper = shallowMount(HeadersUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
