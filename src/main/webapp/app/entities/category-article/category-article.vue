<template>
  <div>
    <h2 id="page-heading" data-cy="CategoryArticleHeading">
      <span v-text="t$('devalgasApp.categoryArticle.home.title')" id="category-article-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('devalgasApp.categoryArticle.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'CategoryArticleCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-category-article"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('devalgasApp.categoryArticle.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && categoryArticles && categoryArticles.length === 0">
      <span v-text="t$('devalgasApp.categoryArticle.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="categoryArticles && categoryArticles.length > 0">
      <table class="table table-striped" aria-describedby="categoryArticles">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('label')">
              <span v-text="t$('devalgasApp.categoryArticle.label')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'label'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('code')">
              <span v-text="t$('devalgasApp.categoryArticle.code')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'code'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('badge')">
              <span v-text="t$('devalgasApp.categoryArticle.badge')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'badge'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('descriptionFr')">
              <span v-text="t$('devalgasApp.categoryArticle.descriptionFr')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'descriptionFr'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('descriptionEn')">
              <span v-text="t$('devalgasApp.categoryArticle.descriptionEn')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'descriptionEn'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="categoryArticle in categoryArticles" :key="categoryArticle.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'CategoryArticleView', params: { categoryArticleId: categoryArticle.id } }">{{
                categoryArticle.id
              }}</router-link>
            </td>
            <td>{{ categoryArticle.label }}</td>
            <td>{{ categoryArticle.code }}</td>
            <td>
              <a v-if="categoryArticle.badge" @click="openFile(categoryArticle.badgeContentType, categoryArticle.badge)">
                <img
                  :src="'data:' + categoryArticle.badgeContentType + ';base64,' + categoryArticle.badge"
                  style="max-height: 30px"
                  alt="categoryArticle"
                />
              </a>
              <span v-if="categoryArticle.badge">{{ categoryArticle.badgeContentType }}, {{ byteSize(categoryArticle.badge) }}</span>
            </td>
            <td>{{ categoryArticle.descriptionFr }}</td>
            <td>{{ categoryArticle.descriptionEn }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'CategoryArticleView', params: { categoryArticleId: categoryArticle.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'CategoryArticleEdit', params: { categoryArticleId: categoryArticle.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(categoryArticle)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span
          id="devalgasApp.categoryArticle.delete.question"
          data-cy="categoryArticleDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-categoryArticle-heading" v-text="t$('devalgasApp.categoryArticle.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-categoryArticle"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeCategoryArticle()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="categoryArticles && categoryArticles.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./category-article.component.ts"></script>
