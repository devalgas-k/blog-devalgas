import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import AppInfoUpdate from './app-info-update.vue';
import AppInfoService from './app-info.service';
import AlertService from '@/shared/alert/alert.service';

import HeadersService from '@/entities/headers/headers.service';
import FootersService from '@/entities/footers/footers.service';

type AppInfoUpdateComponentType = InstanceType<typeof AppInfoUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const appInfoSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<AppInfoUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('AppInfo Management Update Component', () => {
    let comp: AppInfoUpdateComponentType;
    let appInfoServiceStub: SinonStubbedInstance<AppInfoService>;

    beforeEach(() => {
      route = {};
      appInfoServiceStub = sinon.createStubInstance<AppInfoService>(AppInfoService);
      appInfoServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          appInfoService: () => appInfoServiceStub,
          headersService: () =>
            sinon.createStubInstance<HeadersService>(HeadersService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          footersService: () =>
            sinon.createStubInstance<FootersService>(FootersService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(AppInfoUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.appInfo = appInfoSample;
        appInfoServiceStub.update.resolves(appInfoSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(appInfoServiceStub.update.calledWith(appInfoSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        appInfoServiceStub.create.resolves(entity);
        const wrapper = shallowMount(AppInfoUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.appInfo = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(appInfoServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        appInfoServiceStub.find.resolves(appInfoSample);
        appInfoServiceStub.retrieve.resolves([appInfoSample]);

        // WHEN
        route = {
          params: {
            appInfoId: `${appInfoSample.id}`,
          },
        };
        const wrapper = shallowMount(AppInfoUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.appInfo).toMatchObject(appInfoSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        appInfoServiceStub.find.resolves(appInfoSample);
        const wrapper = shallowMount(AppInfoUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
