import { describe, it, expect, beforeEach } from 'vitest';
import axios from 'axios';
import sinon from 'sinon';
import SubjectV1Service from './subject-v1.service';

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
  describe('Subject Service V1', () => {
    let service: SubjectV1Service;

    beforeEach(() => {
      service = new SubjectV1Service();
    });

    it('should retrieve list', async () => {
      const response = { headers: { 'x-total-count': '1' }, data: [{ id: 1, label: 'A' }] };
      axiosStub.get.resolves(response);
      return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
        expect(res).toBe(response);
        expect(res.data).toContainEqual({ id: 1, label: 'A' });
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
  });
});
