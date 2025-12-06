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

describe('AppInfo e2e test', () => {
  const appInfoPageUrl = '/app-info';
  const appInfoPageUrlPattern = new RegExp('/app-info(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const appInfoSample = {};

  let appInfo;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/app-infos+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/app-infos').as('postEntityRequest');
    cy.intercept('DELETE', '/api/app-infos/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (appInfo) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/app-infos/${appInfo.id}`,
      }).then(() => {
        appInfo = undefined;
      });
    }
  });

  it('AppInfos menu should load AppInfos page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('app-info');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('AppInfo').should('exist');
    cy.url().should('match', appInfoPageUrlPattern);
  });

  describe('AppInfo page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(appInfoPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create AppInfo page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/app-info/new$'));
        cy.getEntityCreateUpdateHeading('AppInfo');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', appInfoPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/app-infos',
          body: appInfoSample,
        }).then(({ body }) => {
          appInfo = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/app-infos+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/app-infos?page=0&size=20>; rel="last",<http://localhost/api/app-infos?page=0&size=20>; rel="first"',
              },
              body: [appInfo],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(appInfoPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details AppInfo page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('appInfo');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', appInfoPageUrlPattern);
      });

      it('edit button click should load edit AppInfo page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AppInfo');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', appInfoPageUrlPattern);
      });

      it('edit button click should load edit AppInfo page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AppInfo');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', appInfoPageUrlPattern);
      });

      it('last delete button click should delete instance of AppInfo', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('appInfo').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', appInfoPageUrlPattern);

        appInfo = undefined;
      });
    });
  });

  describe('new AppInfo page', () => {
    beforeEach(() => {
      cy.visit(`${appInfoPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('AppInfo');
    });

    it('should create an instance of AppInfo', () => {
      cy.get(`[data-cy="keyInfo"]`).type('assez');
      cy.get(`[data-cy="keyInfo"]`).should('have.value', 'assez');

      cy.get(`[data-cy="valueInfo"]`).type('de sorte que');
      cy.get(`[data-cy="valueInfo"]`).should('have.value', 'de sorte que');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        appInfo = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', appInfoPageUrlPattern);
    });
  });
});
