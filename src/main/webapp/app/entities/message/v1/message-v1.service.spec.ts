import { describe, it, expect, beforeEach } from 'vitest';
import axios from 'axios';
import sinon from 'sinon';
import MessageV1Service from './message-v1.service';
import { type IMessage } from '@/shared/model/message.model';

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
  describe('Message Service V1', () => {
    let service: MessageV1Service;
    let elemDefault: IMessage;

    beforeEach(() => {
      service = new MessageV1Service();
      elemDefault = {
        id: 123,
        email: 'a@a.com',
        subject: 'AAAAAAA',
        content: 'BBBBBBB',
        date: new Date(),
      } as unknown as IMessage;
    });

    it('should create a Message', async () => {
      const returned = { id: 123, ...elemDefault };
      axiosStub.post.resolves({ data: returned });
      return service.create(elemDefault).then(res => {
        expect(res).toMatchObject(returned);
      });
    });

    it('should not create a Message', async () => {
      axiosStub.post.rejects(error);
      return service
        .create(elemDefault)
        .then()
        .catch(err => {
          expect(err).toMatchObject(error);
        });
    });
  });
});
