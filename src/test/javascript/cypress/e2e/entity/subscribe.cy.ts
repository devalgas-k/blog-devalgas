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

describe('Subscribe e2e test', () => {
  const subscribePageUrl = '/subscribe';
  const subscribePageUrlPattern = new RegExp('/subscribe(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const subscribeSample = { email: 'Y\\}HHx@mB7wP.F' };

  let subscribe;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/subscribes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/subscribes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/subscribes/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (subscribe) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/subscribes/${subscribe.id}`,
      }).then(() => {
        subscribe = undefined;
      });
    }
  });

  it('Subscribes menu should load Subscribes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('subscribe');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Subscribe').should('exist');
    cy.url().should('match', subscribePageUrlPattern);
  });

  describe('Subscribe page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(subscribePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Subscribe page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/subscribe/new$'));
        cy.getEntityCreateUpdateHeading('Subscribe');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', subscribePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/subscribes',
          body: subscribeSample,
        }).then(({ body }) => {
          subscribe = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/subscribes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/subscribes?page=0&size=20>; rel="last",<http://localhost/api/subscribes?page=0&size=20>; rel="first"',
              },
              body: [subscribe],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(subscribePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Subscribe page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('subscribe');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', subscribePageUrlPattern);
      });

      it('edit button click should load edit Subscribe page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Subscribe');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', subscribePageUrlPattern);
      });

      it('edit button click should load edit Subscribe page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Subscribe');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', subscribePageUrlPattern);
      });

      it('last delete button click should delete instance of Subscribe', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('subscribe').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', subscribePageUrlPattern);

        subscribe = undefined;
      });
    });
  });

  describe('new Subscribe page', () => {
    beforeEach(() => {
      cy.visit(`${subscribePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Subscribe');
    });

    it('should create an instance of Subscribe', () => {
      cy.get(`[data-cy="email"]`).type("p;1[d+@2kR-6.'Wj?");
      cy.get(`[data-cy="email"]`).should('have.value', "p;1[d+@2kR-6.'Wj?");

      cy.get(`[data-cy="langKey"]`).type('av');
      cy.get(`[data-cy="langKey"]`).should('have.value', 'av');

      cy.get(`[data-cy="countryKey"]`).type('supposer de manière à ce que quasiment');
      cy.get(`[data-cy="countryKey"]`).should('have.value', 'supposer de manière à ce que quasiment');

      cy.get(`[data-cy="date"]`).type('2025-12-21T14:39');
      cy.get(`[data-cy="date"]`).blur();
      cy.get(`[data-cy="date"]`).should('have.value', '2025-12-21T14:39');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        subscribe = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', subscribePageUrlPattern);
    });
  });
});
