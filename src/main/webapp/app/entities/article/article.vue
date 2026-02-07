<template>
  <div>
    <h2 id="page-heading" data-cy="ArticleHeading">
      <span v-text="t$('devalgasApp.article.home.title')" id="article-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('devalgasApp.article.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'ArticleCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-article"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('devalgasApp.article.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && articles && articles.length === 0">
      <span v-text="t$('devalgasApp.article.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="articles && articles.length > 0">
      <table class="table table-striped" aria-describedby="articles">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('labelEn')">
              <span v-text="t$('devalgasApp.article.labelEn')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'labelEn'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('labelFr')">
              <span v-text="t$('devalgasApp.article.labelFr')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'labelFr'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('descriptionFr')">
              <span v-text="t$('devalgasApp.article.descriptionFr')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'descriptionFr'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('descriptionEn')">
              <span v-text="t$('devalgasApp.article.descriptionEn')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'descriptionEn'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('markdownFr')">
              <span v-text="t$('devalgasApp.article.markdownFr')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'markdownFr'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('markdownEn')">
              <span v-text="t$('devalgasApp.article.markdownEn')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'markdownEn'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('status')">
              <span v-text="t$('devalgasApp.article.status')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'status'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('date')">
              <span v-text="t$('devalgasApp.article.date')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'date'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('badge')">
              <span v-text="t$('devalgasApp.article.badge')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'badge'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('banner')">
              <span v-text="t$('devalgasApp.article.banner')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'banner'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('views')">
              <span v-text="t$('devalgasApp.article.views')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'views'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('stars')">
              <span v-text="t$('devalgasApp.article.stars')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'stars'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('display')">
              <span v-text="t$('devalgasApp.article.display')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'display'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="article in articles" :key="article.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ArticleView', params: { articleId: article.id } }">{{ article.id }}</router-link>
            </td>
            <td>{{ article.labelEn }}</td>
            <td>{{ article.labelFr }}</td>
            <td>{{ article.descriptionFr }}</td>
            <td>{{ article.descriptionEn }}</td>
            <td>
              <a
                v-if="article.markdownFr"
                @click="openFile(article.markdownFrContentType, article.markdownFr)"
                v-text="t$('entity.action.open')"
              ></a>
              <span v-if="article.markdownFr">{{ article.markdownFrContentType }}, {{ byteSize(article.markdownFr) }}</span>
            </td>
            <td>
              <a
                v-if="article.markdownEn"
                @click="openFile(article.markdownEnContentType, article.markdownEn)"
                v-text="t$('entity.action.open')"
              ></a>
              <span v-if="article.markdownEn">{{ article.markdownEnContentType }}, {{ byteSize(article.markdownEn) }}</span>
            </td>
            <td v-text="t$('devalgasApp.Status.' + article.status)"></td>
            <td>{{ formatDateShort(article.date) || '' }}</td>
            <td>
              <a v-if="article.badge" @click="openFile(article.badgeContentType, article.badge)">
                <img :src="'data:' + article.badgeContentType + ';base64,' + article.badge" style="max-height: 30px" alt="article" />
              </a>
              <span v-if="article.badge">{{ article.badgeContentType }}, {{ byteSize(article.badge) }}</span>
            </td>
            <td>
              <a v-if="article.banner" @click="openFile(article.bannerContentType, article.banner)">
                <img :src="'data:' + article.bannerContentType + ';base64,' + article.banner" style="max-height: 30px" alt="article" />
              </a>
              <span v-if="article.banner">{{ article.bannerContentType }}, {{ byteSize(article.banner) }}</span>
            </td>
            <td>{{ article.views }}</td>
            <td>{{ article.stars }}</td>
            <td>{{ article.display }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'ArticleView', params: { articleId: article.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'ArticleEdit', params: { articleId: article.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(article)"
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
        <span id="devalgasApp.article.delete.question" data-cy="articleDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-article-heading" v-text="t$('devalgasApp.article.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-article"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeArticle()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="articles && articles.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./article.component.ts"></script>
