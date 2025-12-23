<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="devalgasApp.subject.home.createOrEditLabel"
          data-cy="SubjectCreateUpdateHeading"
          v-text="t$('devalgasApp.subject.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="subject.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="subject.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.subject.titleFr')" for="subject-titleFr"></label>
            <input
              type="text"
              class="form-control"
              name="titleFr"
              id="subject-titleFr"
              data-cy="titleFr"
              :class="{ valid: !v$.titleFr.$invalid, invalid: v$.titleFr.$invalid }"
              v-model="v$.titleFr.$model"
              required
            />
            <div v-if="v$.titleFr.$anyDirty && v$.titleFr.$invalid">
              <small class="form-text text-danger" v-for="error of v$.titleFr.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.subject.titleEn')" for="subject-titleEn"></label>
            <input
              type="text"
              class="form-control"
              name="titleEn"
              id="subject-titleEn"
              data-cy="titleEn"
              :class="{ valid: !v$.titleEn.$invalid, invalid: v$.titleEn.$invalid }"
              v-model="v$.titleEn.$model"
              required
            />
            <div v-if="v$.titleEn.$anyDirty && v$.titleEn.$invalid">
              <small class="form-text text-danger" v-for="error of v$.titleEn.$errors" :key="error.$uid">{{ error.$message }}</small>
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
<script lang="ts" src="./subject-update.component.ts"></script>
