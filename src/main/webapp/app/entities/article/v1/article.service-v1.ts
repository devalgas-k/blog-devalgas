import axios from 'axios';

import buildPaginationQueryOpts from '@/shared/sort/sorts';

import { type IArticle } from '@/shared/model/article.model';

const baseApiUrl = 'api/v1/articles';

/**
 * Service client V1 pour les articles.
 * Fournit des méthodes pour récupérer les résumés et les détails optimisés.
 */
export default class ArticleServiceV1 {
  /**
   * Récupère les détails optimisés d'un article par identifiant.
   *
   * @param id identifiant de l'article
   * @returns une promesse résolue avec l'article optimisé
   */
  public find(id: number): Promise<IArticle> {
    return new Promise<IArticle>((resolve, reject) => {
      axios
        .get(`${baseApiUrl}/summary/${id}`)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  /**
   * Récupère la liste paginée des articles (projection summary).
   *
   * @param paginationQuery paramètres de pagination/tri (size, page, sort)
   * @returns une promesse résolue avec la réponse axios (données + en-têtes)
   */
  public retrieve(paginationQuery?: any): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .get(`${baseApiUrl}/summary?${buildPaginationQueryOpts(paginationQuery)}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }
}
