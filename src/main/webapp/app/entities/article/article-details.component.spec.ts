import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ArticleDetails from './article-details.vue';
import ArticleService from './article.service';
import AlertService from '@/shared/alert/alert.service';

type ArticleDetailsComponentType = InstanceType<typeof ArticleDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const articleSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Article Management Detail Component', () => {
    let articleServiceStub: SinonStubbedInstance<ArticleService>;
    let mountOptions: MountingOptions<ArticleDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      articleServiceStub = sinon.createStubInstance<ArticleService>(ArticleService);

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
          articleService: () => articleServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        articleServiceStub.find.resolves(articleSample);
        route = {
          params: {
            articleId: `${123}`,
          },
        };
        const wrapper = shallowMount(ArticleDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.article).toMatchObject(articleSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        articleServiceStub.find.resolves(articleSample);
        const wrapper = shallowMount(ArticleDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
