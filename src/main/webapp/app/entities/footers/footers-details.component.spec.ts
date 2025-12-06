import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import FootersDetails from './footers-details.vue';
import FootersService from './footers.service';
import AlertService from '@/shared/alert/alert.service';

type FootersDetailsComponentType = InstanceType<typeof FootersDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const footersSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Footers Management Detail Component', () => {
    let footersServiceStub: SinonStubbedInstance<FootersService>;
    let mountOptions: MountingOptions<FootersDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      footersServiceStub = sinon.createStubInstance<FootersService>(FootersService);

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
          footersService: () => footersServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        footersServiceStub.find.resolves(footersSample);
        route = {
          params: {
            footersId: `${123}`,
          },
        };
        const wrapper = shallowMount(FootersDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.footers).toMatchObject(footersSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        footersServiceStub.find.resolves(footersSample);
        const wrapper = shallowMount(FootersDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
