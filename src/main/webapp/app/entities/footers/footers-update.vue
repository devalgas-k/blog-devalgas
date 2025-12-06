<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="devalgasApp.footers.home.createOrEditLabel"
          data-cy="FootersCreateUpdateHeading"
          v-text="t$('devalgasApp.footers.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="footers.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="footers.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.footers.logoFooters')" for="footers-logoFooters"></label>
            <div>
              <img
                :src="'data:' + footers.logoFootersContentType + ';base64,' + footers.logoFooters"
                style="max-height: 100px"
                v-if="footers.logoFooters"
                alt="footers"
              />
              <div v-if="footers.logoFooters" class="form-text text-danger clearfix">
                <span class="pull-left">{{ footers.logoFootersContentType }}, {{ byteSize(footers.logoFooters) }}</span>
                <button
                  type="button"
                  @click="clearInputImage('logoFooters', 'logoFootersContentType', 'file_logoFooters')"
                  class="btn btn-secondary btn-xs pull-right"
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                </button>
              </div>
              <label for="file_logoFooters" v-text="t$('entity.action.addimage')" class="btn btn-primary pull-right"></label>
              <input
                type="file"
                ref="file_logoFooters"
                id="file_logoFooters"
                style="display: none"
                data-cy="logoFooters"
                @change="setFileData($event, footers, 'logoFooters', true)"
                accept="image/*"
              />
            </div>
            <input
              type="hidden"
              class="form-control"
              name="logoFooters"
              id="footers-logoFooters"
              data-cy="logoFooters"
              :class="{ valid: !v$.logoFooters.$invalid, invalid: v$.logoFooters.$invalid }"
              v-model="v$.logoFooters.$model"
            />
            <input
              type="hidden"
              class="form-control"
              name="logoFootersContentType"
              id="footers-logoFootersContentType"
              v-model="footers.logoFootersContentType"
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
<script lang="ts" src="./footers-update.component.ts"></script>
