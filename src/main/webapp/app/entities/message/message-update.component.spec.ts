import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import MessageUpdate from './message-update.vue';
import MessageService from './message.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import SubjectService from '@/entities/subject/subject.service';

type MessageUpdateComponentType = InstanceType<typeof MessageUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const messageSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<MessageUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Message Management Update Component', () => {
    let comp: MessageUpdateComponentType;
    let messageServiceStub: SinonStubbedInstance<MessageService>;

    beforeEach(() => {
      route = {};
      messageServiceStub = sinon.createStubInstance<MessageService>(MessageService);
      messageServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          messageService: () => messageServiceStub,
          subjectService: () =>
            sinon.createStubInstance<SubjectService>(SubjectService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(MessageUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(MessageUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.message = messageSample;
        messageServiceStub.update.resolves(messageSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(messageServiceStub.update.calledWith(messageSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        messageServiceStub.create.resolves(entity);
        const wrapper = shallowMount(MessageUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.message = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(messageServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        messageServiceStub.find.resolves(messageSample);
        messageServiceStub.retrieve.resolves([messageSample]);

        // WHEN
        route = {
          params: {
            messageId: `${messageSample.id}`,
          },
        };
        const wrapper = shallowMount(MessageUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.message).toMatchObject(messageSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        messageServiceStub.find.resolves(messageSample);
        const wrapper = shallowMount(MessageUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
