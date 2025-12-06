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

describe('Headers e2e test', () => {
  const headersPageUrl = '/headers';
  const headersPageUrlPattern = new RegExp('/headers(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const headersSample = {};

  let headers;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/headers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/headers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/headers/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (headers) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/headers/${headers.id}`,
      }).then(() => {
        headers = undefined;
      });
    }
  });

  it('Headers menu should load Headers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('headers');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Headers').should('exist');
    cy.url().should('match', headersPageUrlPattern);
  });

  describe('Headers page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(headersPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Headers page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/headers/new$'));
        cy.getEntityCreateUpdateHeading('Headers');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', headersPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/headers',
          body: headersSample,
        }).then(({ body }) => {
          headers = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/headers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/headers?page=0&size=20>; rel="last",<http://localhost/api/headers?page=0&size=20>; rel="first"',
              },
              body: [headers],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(headersPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Headers page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('headers');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', headersPageUrlPattern);
      });

      it('edit button click should load edit Headers page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Headers');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', headersPageUrlPattern);
      });

      it('edit button click should load edit Headers page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Headers');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', headersPageUrlPattern);
      });

      it('last delete button click should delete instance of Headers', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('headers').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', headersPageUrlPattern);

        headers = undefined;
      });
    });
  });

  describe('new Headers page', () => {
    beforeEach(() => {
      cy.visit(`${headersPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Headers');
    });

    it('should create an instance of Headers', () => {
      cy.setFieldImageAsBytesOfEntity('logoHeaders', 'integration-test.png', 'image/png');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        headers = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', headersPageUrlPattern);
    });
  });
});
