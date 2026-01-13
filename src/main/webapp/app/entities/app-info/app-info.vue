<template>
  <div>
    <h2 id="page-heading" data-cy="AppInfoHeading">
      <span v-text="t$('devalgasApp.appInfo.home.title')" id="app-info-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('devalgasApp.appInfo.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'AppInfoCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-app-info"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('devalgasApp.appInfo.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && appInfos && appInfos.length === 0">
      <span v-text="t$('devalgasApp.appInfo.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="appInfos && appInfos.length > 0">
      <table class="table table-striped" aria-describedby="appInfos">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('keyInfo')">
              <span v-text="t$('devalgasApp.appInfo.keyInfo')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'keyInfo'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('valueInfo')">
              <span v-text="t$('devalgasApp.appInfo.valueInfo')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'valueInfo'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('headers.id')">
              <span v-text="t$('devalgasApp.appInfo.headers')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'headers.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('footers.id')">
              <span v-text="t$('devalgasApp.appInfo.footers')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'footers.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="appInfo in appInfos" :key="appInfo.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'AppInfoView', params: { appInfoId: appInfo.id } }">{{ appInfo.id }}</router-link>
            </td>
            <td>{{ appInfo.keyInfo }}</td>
            <td>{{ appInfo.valueInfo }}</td>
            <td>
              <div v-if="appInfo.headers">
                <router-link :to="{ name: 'HeadersView', params: { headersId: appInfo.headers.id } }">{{ appInfo.headers.id }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="appInfo.footers">
                <router-link :to="{ name: 'FootersView', params: { footersId: appInfo.footers.id } }">{{ appInfo.footers.id }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'AppInfoView', params: { appInfoId: appInfo.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'AppInfoEdit', params: { appInfoId: appInfo.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(appInfo)"
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
        <span id="devalgasApp.appInfo.delete.question" data-cy="appInfoDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-appInfo-heading" v-text="t$('devalgasApp.appInfo.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-appInfo"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeAppInfo()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="appInfos && appInfos.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :items-per-page="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./app-info.component.ts"></script>
