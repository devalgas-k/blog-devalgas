import { describe, it, expect, beforeEach } from 'vitest';
import axios from 'axios';
import sinon from 'sinon';
import CategoryArticleServiceV1 from './category-article-v1.service';
import { type ICategoryArticle } from '@/shared/model/category-article.model';

const error = {
  response: {
    status: null,
    data: {
      type: null,
    },
  },
};

const axiosStub = {
  get: sinon.stub(axios, 'get'),
  post: sinon.stub(axios, 'post'),
  put: sinon.stub(axios, 'put'),
  patch: sinon.stub(axios, 'patch'),
  delete: sinon.stub(axios, 'delete'),
};

describe('Service Tests', () => {
  describe('CategoryArticle Service V1', () => {
    let service: CategoryArticleServiceV1;
    let elemDefault: ICategoryArticle;

    beforeEach(() => {
      service = new CategoryArticleServiceV1();
      elemDefault = {
        id: 123,
        label: 'AAAAAAA',
        badgeContentType: 'image/png',
        badge: 'AAAAAAA',
      } as unknown as ICategoryArticle;
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        axiosStub.get.resolves({ data: elemDefault });
        return service.find(123).then(res => {
          expect(res).toMatchObject(elemDefault);
        });
      });

      it('should not find an element', async () => {
        axiosStub.get.rejects(error);
        return service
          .find(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should retrieve home summary', async () => {
        const response = { headers: { 'x-total-count': '1' }, data: [elemDefault] };
        axiosStub.get.resolves(response);
        return service.retrieveHome({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toBe(response);
          expect(res.data).toContainEqual(elemDefault);
        });
      });

      it('should retrieve list', async () => {
        const response = { headers: { 'x-total-count': '1' }, data: [elemDefault] };
        axiosStub.get.resolves(response);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toBe(response);
          expect(res.data).toContainEqual(elemDefault);
        });
      });

      it('should not retrieve list', async () => {
        axiosStub.get.rejects(error);
        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete an element', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete an element', async () => {
        axiosStub.delete.rejects(error);
        return service
          .delete(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should create a CategoryArticle', async () => {
        const returned = { id: 123, ...elemDefault };
        axiosStub.post.resolves({ data: returned });
        return service.create(elemDefault).then(res => {
          expect(res).toMatchObject(returned);
        });
      });

      it('should not create a CategoryArticle', async () => {
        axiosStub.post.rejects(error);
        return service
          .create(elemDefault)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a CategoryArticle', async () => {
        const returned = { id: 123, label: 'BBBBBB', ...elemDefault };
        axiosStub.put.resolves({ data: returned });
        return service.update(returned as unknown as ICategoryArticle).then(res => {
          expect(res).toMatchObject(returned);
        });
      });

      it('should not update a CategoryArticle', async () => {
        axiosStub.put.rejects(error);
        return service
          .update(elemDefault)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a CategoryArticle', async () => {
        const patchObject = { id: 123, label: 'BBBBBB' } as unknown as ICategoryArticle;
        const returned = { ...elemDefault, ...patchObject };
        axiosStub.patch.resolves({ data: returned });
        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(returned);
        });
      });

      it('should not partial update a CategoryArticle', async () => {
        axiosStub.patch.rejects(error);
        return service
          .partialUpdate(elemDefault)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });
    });
  });
});
