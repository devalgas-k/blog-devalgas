import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import ArticleService from './article.service';
import { DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { Article } from '@/shared/model/article.model';

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
  describe('Article Service', () => {
    let service: ArticleService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new ArticleService();
      currentDate = new Date();
      elemDefault = new Article(
        123,
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'image/png',
        'AAAAAAA',
        'image/png',
        'AAAAAAA',
        'COMPLETED',
        currentDate,
        'image/png',
        'AAAAAAA',
        'image/png',
        'AAAAAAA',
        0,
        0,
        false,
        false,
      );
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = { date: dayjs(currentDate).format(DATE_TIME_FORMAT), ...elemDefault };
        axiosStub.get.resolves({ data: returnedFromService });

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

      it('should create a Article', async () => {
        const returnedFromService = { id: 123, date: dayjs(currentDate).format(DATE_TIME_FORMAT), ...elemDefault };
        const expected = { date: currentDate, ...returnedFromService };

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a Article', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a Article', async () => {
        const returnedFromService = {
          labelEn: 'BBBBBB',
          labelFr: 'BBBBBB',
          descriptionFr: 'BBBBBB',
          descriptionEn: 'BBBBBB',
          markdownFr: 'BBBBBB',
          markdownEn: 'BBBBBB',
          status: 'BBBBBB',
          date: dayjs(currentDate).format(DATE_TIME_FORMAT),
          badge: 'BBBBBB',
          banner: 'BBBBBB',
          views: 1,
          stars: 1,
          display: true,
          newsletter: true,
          ...elemDefault,
        };

        const expected = { date: currentDate, ...returnedFromService };
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a Article', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a Article', async () => {
        const patchObject = {
          labelFr: 'BBBBBB',
          descriptionFr: 'BBBBBB',
          descriptionEn: 'BBBBBB',
          markdownFr: 'BBBBBB',
          markdownEn: 'BBBBBB',
          status: 'BBBBBB',
          badge: 'BBBBBB',
          views: 1,
          stars: 1,
          display: true,
          newsletter: true,
          ...new Article(),
        };
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = { date: currentDate, ...returnedFromService };
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a Article', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of Article', async () => {
        const returnedFromService = {
          labelEn: 'BBBBBB',
          labelFr: 'BBBBBB',
          descriptionFr: 'BBBBBB',
          descriptionEn: 'BBBBBB',
          markdownFr: 'BBBBBB',
          markdownEn: 'BBBBBB',
          status: 'BBBBBB',
          date: dayjs(currentDate).format(DATE_TIME_FORMAT),
          badge: 'BBBBBB',
          banner: 'BBBBBB',
          views: 1,
          stars: 1,
          display: true,
          newsletter: true,
          ...elemDefault,
        };
        const expected = { date: currentDate, ...returnedFromService };
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of Article', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a Article', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a Article', async () => {
        axiosStub.delete.rejects(error);

        return service
          .delete(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });
    });
  });
});
