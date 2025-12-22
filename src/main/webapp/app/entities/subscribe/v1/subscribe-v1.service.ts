import axios from 'axios';

import buildPaginationQueryOpts from '@/shared/sort/sorts';

import { type ISubscribe } from '@/shared/model/subscribe.model';

const baseApiUrl = 'api/v1/subscribes';

export default class SubscribeV1Service {
  public processSubscribe(entity: ISubscribe): Promise<ISubscribe> {
    return new Promise<ISubscribe>((resolve, reject) => {
      axios
        .post(`${baseApiUrl}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }
}
