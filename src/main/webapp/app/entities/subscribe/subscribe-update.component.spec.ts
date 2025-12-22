import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import SubscribeUpdate from './subscribe-update.vue';
import SubscribeService from './subscribe.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type SubscribeUpdateComponentType = InstanceType<typeof SubscribeUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const subscribeSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<SubscribeUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Subscribe Management Update Component', () => {
    let comp: SubscribeUpdateComponentType;
    let subscribeServiceStub: SinonStubbedInstance<SubscribeService>;

    beforeEach(() => {
      route = {};
      subscribeServiceStub = sinon.createStubInstance<SubscribeService>(SubscribeService);
      subscribeServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          subscribeService: () => subscribeServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(SubscribeUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(SubscribeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.subscribe = subscribeSample;
        subscribeServiceStub.update.resolves(subscribeSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(subscribeServiceStub.update.calledWith(subscribeSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        subscribeServiceStub.create.resolves(entity);
        const wrapper = shallowMount(SubscribeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.subscribe = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(subscribeServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        subscribeServiceStub.find.resolves(subscribeSample);
        subscribeServiceStub.retrieve.resolves([subscribeSample]);

        // WHEN
        route = {
          params: {
            subscribeId: `${subscribeSample.id}`,
          },
        };
        const wrapper = shallowMount(SubscribeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.subscribe).toMatchObject(subscribeSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        subscribeServiceStub.find.resolves(subscribeSample);
        const wrapper = shallowMount(SubscribeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
