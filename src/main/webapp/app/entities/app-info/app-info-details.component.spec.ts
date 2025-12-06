import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import AppInfoDetails from './app-info-details.vue';
import AppInfoService from './app-info.service';
import AlertService from '@/shared/alert/alert.service';

type AppInfoDetailsComponentType = InstanceType<typeof AppInfoDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const appInfoSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('AppInfo Management Detail Component', () => {
    let appInfoServiceStub: SinonStubbedInstance<AppInfoService>;
    let mountOptions: MountingOptions<AppInfoDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      appInfoServiceStub = sinon.createStubInstance<AppInfoService>(AppInfoService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          appInfoService: () => appInfoServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        appInfoServiceStub.find.resolves(appInfoSample);
        route = {
          params: {
            appInfoId: `${123}`,
          },
        };
        const wrapper = shallowMount(AppInfoDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.appInfo).toMatchObject(appInfoSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        appInfoServiceStub.find.resolves(appInfoSample);
        const wrapper = shallowMount(AppInfoDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
