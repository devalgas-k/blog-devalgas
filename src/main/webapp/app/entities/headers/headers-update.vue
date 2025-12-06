<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="devalgasApp.headers.home.createOrEditLabel"
          data-cy="HeadersCreateUpdateHeading"
          v-text="t$('devalgasApp.headers.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="headers.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="headers.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.headers.logoHeaders')" for="headers-logoHeaders"></label>
            <div>
              <img
                :src="'data:' + headers.logoHeadersContentType + ';base64,' + headers.logoHeaders"
                style="max-height: 100px"
                v-if="headers.logoHeaders"
                alt="headers"
              />
              <div v-if="headers.logoHeaders" class="form-text text-danger clearfix">
                <span class="pull-left">{{ headers.logoHeadersContentType }}, {{ byteSize(headers.logoHeaders) }}</span>
                <button
                  type="button"
                  @click="clearInputImage('logoHeaders', 'logoHeadersContentType', 'file_logoHeaders')"
                  class="btn btn-secondary btn-xs pull-right"
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                </button>
              </div>
              <label for="file_logoHeaders" v-text="t$('entity.action.addimage')" class="btn btn-primary pull-right"></label>
              <input
                type="file"
                ref="file_logoHeaders"
                id="file_logoHeaders"
                style="display: none"
                data-cy="logoHeaders"
                @change="setFileData($event, headers, 'logoHeaders', true)"
                accept="image/*"
              />
            </div>
            <input
              type="hidden"
              class="form-control"
              name="logoHeaders"
              id="headers-logoHeaders"
              data-cy="logoHeaders"
              :class="{ valid: !v$.logoHeaders.$invalid, invalid: v$.logoHeaders.$invalid }"
              v-model="v$.logoHeaders.$model"
            />
            <input
              type="hidden"
              class="form-control"
              name="logoHeadersContentType"
              id="headers-logoHeadersContentType"
              v-model="headers.logoHeadersContentType"
            />
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" @click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./headers-update.component.ts"></script>
