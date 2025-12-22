import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('CategoryArticle e2e test', () => {
  const categoryArticlePageUrl = '/category-article';
  const categoryArticlePageUrlPattern = new RegExp('/category-article(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const categoryArticleSample = { label: 'Fy%[L', code: 'BR' };

  let categoryArticle;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/category-articles+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/category-articles').as('postEntityRequest');
    cy.intercept('DELETE', '/api/category-articles/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (categoryArticle) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/category-articles/${categoryArticle.id}`,
      }).then(() => {
        categoryArticle = undefined;
      });
    }
  });

  it('CategoryArticles menu should load CategoryArticles page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('category-article');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CategoryArticle').should('exist');
    cy.url().should('match', categoryArticlePageUrlPattern);
  });

  describe('CategoryArticle page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(categoryArticlePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CategoryArticle page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/category-article/new$'));
        cy.getEntityCreateUpdateHeading('CategoryArticle');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', categoryArticlePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/category-articles',
          body: categoryArticleSample,
        }).then(({ body }) => {
          categoryArticle = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/category-articles+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/category-articles?page=0&size=20>; rel="last",<http://localhost/api/category-articles?page=0&size=20>; rel="first"',
              },
              body: [categoryArticle],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(categoryArticlePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details CategoryArticle page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('categoryArticle');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', categoryArticlePageUrlPattern);
      });

      it('edit button click should load edit CategoryArticle page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CategoryArticle');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', categoryArticlePageUrlPattern);
      });

      it('edit button click should load edit CategoryArticle page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CategoryArticle');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', categoryArticlePageUrlPattern);
      });

      it('last delete button click should delete instance of CategoryArticle', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('categoryArticle').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', categoryArticlePageUrlPattern);

        categoryArticle = undefined;
      });
    });
  });

  describe('new CategoryArticle page', () => {
    beforeEach(() => {
      cy.visit(`${categoryArticlePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('CategoryArticle');
    });

    it('should create an instance of CategoryArticle', () => {
      cy.get(`[data-cy="label"]`).type('DvW/.');
      cy.get(`[data-cy="label"]`).should('have.value', 'DvW/.');

      cy.get(`[data-cy="code"]`).type('JM');
      cy.get(`[data-cy="code"]`).should('have.value', 'JM');

      cy.setFieldImageAsBytesOfEntity('badge', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="descriptionFr"]`).type('moyennant bang');
      cy.get(`[data-cy="descriptionFr"]`).should('have.value', 'moyennant bang');

      cy.get(`[data-cy="descriptionEn"]`).type('au-dessus');
      cy.get(`[data-cy="descriptionEn"]`).should('have.value', 'au-dessus');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        categoryArticle = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', categoryArticlePageUrlPattern);
    });
  });
});
