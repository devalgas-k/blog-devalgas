<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="devalgasApp.appInfo.home.createOrEditLabel"
          data-cy="AppInfoCreateUpdateHeading"
          v-text="t$('devalgasApp.appInfo.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="appInfo.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="appInfo.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.appInfo.keyInfo')" for="app-info-keyInfo"></label>
            <input
              type="text"
              class="form-control"
              name="keyInfo"
              id="app-info-keyInfo"
              data-cy="keyInfo"
              :class="{ valid: !v$.keyInfo.$invalid, invalid: v$.keyInfo.$invalid }"
              v-model="v$.keyInfo.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.appInfo.valueInfo')" for="app-info-valueInfo"></label>
            <input
              type="text"
              class="form-control"
              name="valueInfo"
              id="app-info-valueInfo"
              data-cy="valueInfo"
              :class="{ valid: !v$.valueInfo.$invalid, invalid: v$.valueInfo.$invalid }"
              v-model="v$.valueInfo.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.appInfo.headers')" for="app-info-headers"></label>
            <select class="form-control" id="app-info-headers" data-cy="headers" name="headers" v-model="appInfo.headers">
              <option :value="null"></option>
              <option
                :value="appInfo.headers && headersOption.id === appInfo.headers.id ? appInfo.headers : headersOption"
                v-for="headersOption in headers"
                :key="headersOption.id"
              >
                {{ headersOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.appInfo.footers')" for="app-info-footers"></label>
            <select class="form-control" id="app-info-footers" data-cy="footers" name="footers" v-model="appInfo.footers">
              <option :value="null"></option>
              <option
                :value="appInfo.footers && footersOption.id === appInfo.footers.id ? appInfo.footers : footersOption"
                v-for="footersOption in footers"
                :key="footersOption.id"
              >
                {{ footersOption.id }}
              </option>
            </select>
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
<script lang="ts" src="./app-info-update.component.ts"></script>
