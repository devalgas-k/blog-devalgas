import axios from 'axios';

import { type IMessage } from '@/shared/model/message.model';

const baseApiUrl = 'api/v1/messages';

export default class MessageV1Service {
  public create(entity: IMessage): Promise<IMessage> {
    return new Promise<IMessage>((resolve, reject) => {
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
