import axios from 'axios';

import { type ISubscribe } from '@/shared/model/subscribe.model';

const baseApiUrl = 'api/v1/subscribes';

/**
 * Service client V1 pour les abonnements (newsletter).
 * Fournit des méthodes pour enregistrer un abonné.
 */
export default class SubscribeV1Service {
  /**
   * Enregistre un nouvel abonné au blog.
   *
   * @param entity données de l'abonné à enregistrer
   * @returns une promesse résolue avec l'abonné persisté
   */
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
