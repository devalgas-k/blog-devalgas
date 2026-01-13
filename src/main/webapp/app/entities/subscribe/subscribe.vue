<template>
  <div>
    <h2 id="page-heading" data-cy="SubscribeHeading">
      <span v-text="t$('devalgasApp.subscribe.home.title')" id="subscribe-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('devalgasApp.subscribe.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'SubscribeCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-subscribe"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('devalgasApp.subscribe.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && subscribes && subscribes.length === 0">
      <span v-text="t$('devalgasApp.subscribe.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="subscribes && subscribes.length > 0">
      <table class="table table-striped" aria-describedby="subscribes">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('email')">
              <span v-text="t$('devalgasApp.subscribe.email')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'email'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('langKey')">
              <span v-text="t$('devalgasApp.subscribe.langKey')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'langKey'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('countryKey')">
              <span v-text="t$('devalgasApp.subscribe.countryKey')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'countryKey'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('date')">
              <span v-text="t$('devalgasApp.subscribe.date')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'date'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="subscribe in subscribes" :key="subscribe.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'SubscribeView', params: { subscribeId: subscribe.id } }">{{ subscribe.id }}</router-link>
            </td>
            <td>{{ subscribe.email }}</td>
            <td>{{ subscribe.langKey }}</td>
            <td>{{ subscribe.countryKey }}</td>
            <td>{{ formatDateShort(subscribe.date) || '' }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'SubscribeView', params: { subscribeId: subscribe.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'SubscribeEdit', params: { subscribeId: subscribe.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(subscribe)"
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
        <span id="devalgasApp.subscribe.delete.question" data-cy="subscribeDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-subscribe-heading" v-text="t$('devalgasApp.subscribe.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-subscribe"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeSubscribe()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="subscribes && subscribes.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :items-per-page="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./subscribe.component.ts"></script>
