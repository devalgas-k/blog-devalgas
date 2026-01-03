import { describe, it, expect, beforeEach } from 'vitest';
import axios from 'axios';
import sinon from 'sinon';
import ArticleServiceV1 from './article.service-v1';
import { type IArticle } from '@/shared/model/article.model';

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
};

describe('Service Tests', () => {
  describe('Article Service V1', () => {
    let service: ArticleServiceV1;
    let elemDefault: IArticle;

    beforeEach(() => {
      service = new ArticleServiceV1();
      elemDefault = {
        id: 123,
        labelEn: 'AAAAAAA',
        labelFr: 'AAAAAAA',
        descriptionFr: 'AAAAAAA',
        descriptionEn: 'AAAAAAA',
        markdownFr: 'AAAAAAA',
        markdownEn: 'AAAAAAA',
        status: 'COMPLETED',
        date: new Date(),
        badgeContentType: 'image/png',
        badge: 'AAAAAAA',
        bannerContentType: 'image/png',
        banner: 'AAAAAAA',
        views: 0,
        stars: 0,
      } as unknown as IArticle;
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

      it('should retrieve summary', async () => {
        const response = { headers: { 'x-total-count': '1' }, data: [elemDefault] };
        axiosStub.get.resolves(response);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toBe(response);
          expect(res.data).toContainEqual(elemDefault);
        });
      });

      it('should not retrieve summary', async () => {
        axiosStub.get.rejects(error);
        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });
    });
  });
});
