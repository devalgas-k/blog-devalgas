<template>
  <div>
    <h2 id="page-heading" data-cy="SubjectHeading">
      <span v-text="t$('devalgasApp.subject.home.title')" id="subject-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('devalgasApp.subject.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'SubjectCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-subject"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('devalgasApp.subject.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && subjects && subjects.length === 0">
      <span v-text="t$('devalgasApp.subject.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="subjects && subjects.length > 0">
      <table class="table table-striped" aria-describedby="subjects">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('titleFr')">
              <span v-text="t$('devalgasApp.subject.titleFr')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'titleFr'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('titleEn')">
              <span v-text="t$('devalgasApp.subject.titleEn')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'titleEn'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="subject in subjects" :key="subject.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'SubjectView', params: { subjectId: subject.id } }">{{ subject.id }}</router-link>
            </td>
            <td>{{ subject.titleFr }}</td>
            <td>{{ subject.titleEn }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'SubjectView', params: { subjectId: subject.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'SubjectEdit', params: { subjectId: subject.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(subject)"
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
        <span id="devalgasApp.subject.delete.question" data-cy="subjectDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-subject-heading" v-text="t$('devalgasApp.subject.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-subject"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeSubject()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="subjects && subjects.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./subject.component.ts"></script>
