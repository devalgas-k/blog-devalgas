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

describe('Footers e2e test', () => {
  const footersPageUrl = '/footers';
  const footersPageUrlPattern = new RegExp('/footers(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const footersSample = {};

  let footers;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/footers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/footers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/footers/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (footers) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/footers/${footers.id}`,
      }).then(() => {
        footers = undefined;
      });
    }
  });

  it('Footers menu should load Footers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('footers');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Footers').should('exist');
    cy.url().should('match', footersPageUrlPattern);
  });

  describe('Footers page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(footersPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Footers page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/footers/new$'));
        cy.getEntityCreateUpdateHeading('Footers');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', footersPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/footers',
          body: footersSample,
        }).then(({ body }) => {
          footers = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/footers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/footers?page=0&size=20>; rel="last",<http://localhost/api/footers?page=0&size=20>; rel="first"',
              },
              body: [footers],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(footersPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Footers page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('footers');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', footersPageUrlPattern);
      });

      it('edit button click should load edit Footers page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Footers');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', footersPageUrlPattern);
      });

      it('edit button click should load edit Footers page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Footers');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', footersPageUrlPattern);
      });

      it('last delete button click should delete instance of Footers', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('footers').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', footersPageUrlPattern);

        footers = undefined;
      });
    });
  });

  describe('new Footers page', () => {
    beforeEach(() => {
      cy.visit(`${footersPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Footers');
    });

    it('should create an instance of Footers', () => {
      cy.setFieldImageAsBytesOfEntity('logoFooters', 'integration-test.png', 'image/png');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        footers = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', footersPageUrlPattern);
    });
  });
});
