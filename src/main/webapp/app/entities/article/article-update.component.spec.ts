import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { createTestingPinia } from '@pinia/testing';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import ArticleUpdate from './article-update.vue';
import ArticleService from './article.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import CategoryArticleService from '@/entities/category-article/category-article.service';

type ArticleUpdateComponentType = InstanceType<typeof ArticleUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const articleSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ArticleUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Article Management Update Component', () => {
    let comp: ArticleUpdateComponentType;
    let articleServiceStub: SinonStubbedInstance<ArticleService>;

    beforeEach(() => {
      route = {};
      articleServiceStub = sinon.createStubInstance<ArticleService>(ArticleService);
      articleServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
        plugins: [createTestingPinia({ createSpy: vitest.fn })],
        provide: {
          alertService,
          articleService: () => articleServiceStub,
          categoryArticleService: () =>
            sinon.createStubInstance<CategoryArticleService>(CategoryArticleService, {
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
        const wrapper = shallowMount(ArticleUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(ArticleUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.article = articleSample;
        articleServiceStub.update.resolves(articleSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(articleServiceStub.update.calledWith(articleSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        articleServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ArticleUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.article = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(articleServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        articleServiceStub.find.resolves(articleSample);
        articleServiceStub.retrieve.resolves([articleSample]);

        // WHEN
        route = {
          params: {
            articleId: `${articleSample.id}`,
          },
        };
        const wrapper = shallowMount(ArticleUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.article).toMatchObject(articleSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        articleServiceStub.find.resolves(articleSample);
        const wrapper = shallowMount(ArticleUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
