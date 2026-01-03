import { describe, it, expect, beforeEach } from 'vitest';
import axios from 'axios';
import sinon from 'sinon';
import SubscribeV1Service from './subscribe-v1.service';
import { type ISubscribe } from '@/shared/model/subscribe.model';

const error = {
  response: {
    status: null,
    data: {
      type: null,
    },
  },
};

const axiosStub = {
  post: sinon.stub(axios, 'post'),
};

describe('Service Tests', () => {
  describe('Subscribe Service V1', () => {
    let service: SubscribeV1Service;
    let elemDefault: ISubscribe;

    beforeEach(() => {
      service = new SubscribeV1Service();
      elemDefault = {
        id: 123,
        email: 'a@a.com',
        consent: true,
        date: new Date(),
      } as unknown as ISubscribe;
    });

    it('should process subscribe', async () => {
      const returned = { id: 123, ...elemDefault };
      axiosStub.post.resolves({ data: returned });
      return service.processSubscribe(elemDefault).then(res => {
        expect(res).toMatchObject(returned);
      });
    });

    it('should not process subscribe', async () => {
      axiosStub.post.rejects(error);
      return service
        .processSubscribe(elemDefault)
        .then()
        .catch(err => {
          expect(err).toMatchObject(error);
        });
    });
  });
});
