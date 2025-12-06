import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import HeadersDetails from './headers-details.vue';
import HeadersService from './headers.service';
import AlertService from '@/shared/alert/alert.service';

type HeadersDetailsComponentType = InstanceType<typeof HeadersDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const headersSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Headers Management Detail Component', () => {
    let headersServiceStub: SinonStubbedInstance<HeadersService>;
    let mountOptions: MountingOptions<HeadersDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      headersServiceStub = sinon.createStubInstance<HeadersService>(HeadersService);

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
          headersService: () => headersServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        headersServiceStub.find.resolves(headersSample);
        route = {
          params: {
            headersId: `${123}`,
          },
        };
        const wrapper = shallowMount(HeadersDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.headers).toMatchObject(headersSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        headersServiceStub.find.resolves(headersSample);
        const wrapper = shallowMount(HeadersDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
