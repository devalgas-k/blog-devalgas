import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import CategoryArticle from './category-article.vue';
import CategoryArticleService from './category-article.service';
import AlertService from '@/shared/alert/alert.service';

type CategoryArticleComponentType = InstanceType<typeof CategoryArticle>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('CategoryArticle Management Component', () => {
    let categoryArticleServiceStub: SinonStubbedInstance<CategoryArticleService>;
    let mountOptions: MountingOptions<CategoryArticleComponentType>['global'];

    beforeEach(() => {
      categoryArticleServiceStub = sinon.createStubInstance<CategoryArticleService>(CategoryArticleService);
      categoryArticleServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          jhiItemCount: true,
          bPagination: true,
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'jhi-sort-indicator': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          categoryArticleService: () => categoryArticleServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        categoryArticleServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(CategoryArticle, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(categoryArticleServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.categoryArticles[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(CategoryArticle, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(categoryArticleServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: CategoryArticleComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(CategoryArticle, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        categoryArticleServiceStub.retrieve.reset();
        categoryArticleServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        categoryArticleServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(categoryArticleServiceStub.retrieve.called).toBeTruthy();
        expect(comp.categoryArticles[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(categoryArticleServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        categoryArticleServiceStub.retrieve.reset();
        categoryArticleServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(categoryArticleServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.categoryArticles[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(categoryArticleServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        categoryArticleServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeCategoryArticle();
        await comp.$nextTick(); // clear components

        // THEN
        expect(categoryArticleServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(categoryArticleServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
