import axios from 'axios';

import buildPaginationQueryOpts from '@/shared/sort/sorts';

import { type ICategoryArticle } from '@/shared/model/category-article.model';

const baseApiUrl = 'api/v1/category-articles';

/**
 * Service client V1 pour les catégories d'articles.
 * Fournit des méthodes CRUD et des récupérations optimisées.
 */
export default class CategoryArticleServiceV1 {
  /**
   * Récupère une catégorie d'article par identifiant.
   *
   * @param id identifiant de la catégorie
   * @returns une promesse résolue avec la catégorie
   */
  public find(id: number): Promise<ICategoryArticle> {
    return new Promise<ICategoryArticle>((resolve, reject) => {
      axios
        .get(`${baseApiUrl}/${id}`)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  /**
   * Récupère le résumé optimisé pour la page d'accueil.
   *
   * @param paginationQuery paramètres de pagination/tri (size, page, sort)
   * @returns une promesse résolue avec la réponse axios (données + en-têtes)
   */
  public retrieveHome(paginationQuery?: any): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .get(baseApiUrl + `/summary?${buildPaginationQueryOpts(paginationQuery)}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  /**
   * Récupère la liste paginée des catégories d'articles.
   *
   * @param paginationQuery paramètres de pagination/tri (size, page, sort)
   * @returns une promesse résolue avec la réponse axios (données + en-têtes)
   */
  public retrieve(paginationQuery?: any): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .get(baseApiUrl + `?${buildPaginationQueryOpts(paginationQuery)}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  /**
   * Supprime une catégorie d'article.
   *
   * @param id identifiant de la catégorie
   * @returns une promesse résolue avec la réponse axios
   */
  public delete(id: number): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .delete(`${baseApiUrl}/${id}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  /**
   * Crée une nouvelle catégorie d'article.
   *
   * @param entity données de la catégorie à créer
   * @returns une promesse résolue avec la catégorie persistée
   */
  public create(entity: ICategoryArticle): Promise<ICategoryArticle> {
    return new Promise<ICategoryArticle>((resolve, reject) => {
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

  /**
   * Met à jour une catégorie d'article existante.
   *
   * @param entity données de la catégorie à mettre à jour
   * @returns une promesse résolue avec la catégorie mise à jour
   */
  public update(entity: ICategoryArticle): Promise<ICategoryArticle> {
    return new Promise<ICategoryArticle>((resolve, reject) => {
      axios
        .put(`${baseApiUrl}/${entity.id}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  /**
   * Met à jour partiellement une catégorie d'article.
   *
   * @param entity données de la catégorie avec champs à mettre à jour
   * @returns une promesse résolue avec la catégorie mise à jour
   */
  public partialUpdate(entity: ICategoryArticle): Promise<ICategoryArticle> {
    return new Promise<ICategoryArticle>((resolve, reject) => {
      axios
        .patch(`${baseApiUrl}/${entity.id}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }
}
