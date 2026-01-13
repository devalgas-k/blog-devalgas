import axios from 'axios';

import buildPaginationQueryOpts from '@/shared/sort/sorts';

const baseApiUrl = 'api/v1/subjects';

/**
 * Service client V1 pour les sujets.
 * Fournit des méthodes pour récupérer la liste paginée des sujets.
 */
export default class SubjectV1Service {
  /**
   * Récupère la liste paginée des sujets.
   *
   * @param paginationQuery paramètres de pagination/tri (size, page, sort)
   * @returns une promesse résolue avec la réponse axios (données + en-têtes)
   */
  public retrieve(paginationQuery?: any): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .get(`${baseApiUrl}?${buildPaginationQueryOpts(paginationQuery)}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }
}
