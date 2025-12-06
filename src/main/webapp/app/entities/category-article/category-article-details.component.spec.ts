import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import CategoryArticleDetails from './category-article-details.vue';
import CategoryArticleService from './category-article.service';
import AlertService from '@/shared/alert/alert.service';

type CategoryArticleDetailsComponentType = InstanceType<typeof CategoryArticleDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const categoryArticleSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('CategoryArticle Management Detail Component', () => {
    let categoryArticleServiceStub: SinonStubbedInstance<CategoryArticleService>;
    let mountOptions: MountingOptions<CategoryArticleDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      categoryArticleServiceStub = sinon.createStubInstance<CategoryArticleService>(CategoryArticleService);

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
          categoryArticleService: () => categoryArticleServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        categoryArticleServiceStub.find.resolves(categoryArticleSample);
        route = {
          params: {
            categoryArticleId: `${123}`,
          },
        };
        const wrapper = shallowMount(CategoryArticleDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.categoryArticle).toMatchObject(categoryArticleSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        categoryArticleServiceStub.find.resolves(categoryArticleSample);
        const wrapper = shallowMount(CategoryArticleDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
