import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import CategoryArticleUpdate from './category-article-update.vue';
import CategoryArticleService from './category-article.service';
import AlertService from '@/shared/alert/alert.service';

import ArticleService from '@/entities/article/article.service';

type CategoryArticleUpdateComponentType = InstanceType<typeof CategoryArticleUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const categoryArticleSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<CategoryArticleUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('CategoryArticle Management Update Component', () => {
    let comp: CategoryArticleUpdateComponentType;
    let categoryArticleServiceStub: SinonStubbedInstance<CategoryArticleService>;

    beforeEach(() => {
      route = {};
      categoryArticleServiceStub = sinon.createStubInstance<CategoryArticleService>(CategoryArticleService);
      categoryArticleServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          categoryArticleService: () => categoryArticleServiceStub,
          articleService: () =>
            sinon.createStubInstance<ArticleService>(ArticleService, {
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
        const wrapper = shallowMount(CategoryArticleUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.categoryArticle = categoryArticleSample;
        categoryArticleServiceStub.update.resolves(categoryArticleSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(categoryArticleServiceStub.update.calledWith(categoryArticleSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        categoryArticleServiceStub.create.resolves(entity);
        const wrapper = shallowMount(CategoryArticleUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.categoryArticle = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(categoryArticleServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        categoryArticleServiceStub.find.resolves(categoryArticleSample);
        categoryArticleServiceStub.retrieve.resolves([categoryArticleSample]);

        // WHEN
        route = {
          params: {
            categoryArticleId: `${categoryArticleSample.id}`,
          },
        };
        const wrapper = shallowMount(CategoryArticleUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.categoryArticle).toMatchObject(categoryArticleSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        categoryArticleServiceStub.find.resolves(categoryArticleSample);
        const wrapper = shallowMount(CategoryArticleUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
