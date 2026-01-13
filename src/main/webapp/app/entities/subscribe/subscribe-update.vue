<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="devalgasApp.subscribe.home.createOrEditLabel"
          data-cy="SubscribeCreateUpdateHeading"
          v-text="t$('devalgasApp.subscribe.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="subscribe.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="subscribe.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.subscribe.email')" for="subscribe-email"></label>
            <input
              type="text"
              class="form-control"
              name="email"
              id="subscribe-email"
              data-cy="email"
              :class="{ valid: !v$.email.$invalid, invalid: v$.email.$invalid }"
              v-model="v$.email.$model"
              required
            />
            <div v-if="v$.email.$anyDirty && v$.email.$invalid">
              <small class="form-text text-danger" v-for="error of v$.email.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.subscribe.langKey')" for="subscribe-langKey"></label>
            <input
              type="text"
              class="form-control"
              name="langKey"
              id="subscribe-langKey"
              data-cy="langKey"
              :class="{ valid: !v$.langKey.$invalid, invalid: v$.langKey.$invalid }"
              v-model="v$.langKey.$model"
            />
            <div v-if="v$.langKey.$anyDirty && v$.langKey.$invalid">
              <small class="form-text text-danger" v-for="error of v$.langKey.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.subscribe.countryKey')" for="subscribe-countryKey"></label>
            <input
              type="text"
              class="form-control"
              name="countryKey"
              id="subscribe-countryKey"
              data-cy="countryKey"
              :class="{ valid: !v$.countryKey.$invalid, invalid: v$.countryKey.$invalid }"
              v-model="v$.countryKey.$model"
            />
            <div v-if="v$.countryKey.$anyDirty && v$.countryKey.$invalid">
              <small class="form-text text-danger" v-for="error of v$.countryKey.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.subscribe.date')" for="subscribe-date"></label>
            <div class="d-flex">
              <input
                id="subscribe-date"
                data-cy="date"
                type="datetime-local"
                class="form-control"
                name="date"
                :class="{ valid: !v$.date.$invalid, invalid: v$.date.$invalid }"
                :value="convertDateTimeFromServer(v$.date.$model)"
                @change="updateZonedDateTimeField('date', $event)"
              />
            </div>
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
<script lang="ts" src="./subscribe-update.component.ts"></script>
