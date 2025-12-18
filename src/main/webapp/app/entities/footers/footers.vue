<template>
  <div>
    <h2 id="page-heading" data-cy="FootersHeading">
      <span v-text="t$('devalgasApp.footers.home.title')" id="footers-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('devalgasApp.footers.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'FootersCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-footers"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('devalgasApp.footers.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && footers && footers.length === 0">
      <span v-text="t$('devalgasApp.footers.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="footers && footers.length > 0">
      <table class="table table-striped" aria-describedby="footers">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('logoFooters')">
              <span v-text="t$('devalgasApp.footers.logoFooters')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'logoFooters'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="footers in footers" :key="footers.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'FootersView', params: { footersId: footers.id } }">{{ footers.id }}</router-link>
            </td>
            <td>
              <a v-if="footers.logoFooters" @click="openFile(footers.logoFootersContentType, footers.logoFooters)">
                <img
                  :src="'data:' + footers.logoFootersContentType + ';base64,' + footers.logoFooters"
                  style="max-height: 30px"
                  alt="footers"
                />
              </a>
              <span v-if="footers.logoFooters">{{ footers.logoFootersContentType }}, {{ byteSize(footers.logoFooters) }}</span>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'FootersView', params: { footersId: footers.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'FootersEdit', params: { footersId: footers.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(footers)"
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
        <span id="devalgasApp.footers.delete.question" data-cy="footersDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-footers-heading" v-text="t$('devalgasApp.footers.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-footers"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeFooters()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="footers && footers.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :items-per-page="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./footers.component.ts"></script>
