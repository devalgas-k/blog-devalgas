<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="devalgasApp.message.home.createOrEditLabel"
          data-cy="MessageCreateUpdateHeading"
          v-text="t$('devalgasApp.message.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="message.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="message.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.message.name')" for="message-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="message-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
              required
            />
            <div v-if="v$.name.$anyDirty && v$.name.$invalid">
              <small class="form-text text-danger" v-for="error of v$.name.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.message.email')" for="message-email"></label>
            <input
              type="text"
              class="form-control"
              name="email"
              id="message-email"
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
            <label class="form-control-label" v-text="t$('devalgasApp.message.phone')" for="message-phone"></label>
            <input
              type="text"
              class="form-control"
              name="phone"
              id="message-phone"
              data-cy="phone"
              :class="{ valid: !v$.phone.$invalid, invalid: v$.phone.$invalid }"
              v-model="v$.phone.$model"
            />
            <div v-if="v$.phone.$anyDirty && v$.phone.$invalid">
              <small class="form-text text-danger" v-for="error of v$.phone.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.message.message')" for="message-message"></label>
            <input
              type="text"
              class="form-control"
              name="message"
              id="message-message"
              data-cy="message"
              :class="{ valid: !v$.message.$invalid, invalid: v$.message.$invalid }"
              v-model="v$.message.$model"
              required
            />
            <div v-if="v$.message.$anyDirty && v$.message.$invalid">
              <small class="form-text text-danger" v-for="error of v$.message.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.message.file')" for="message-file"></label>
            <div>
              <div v-if="message.file" class="form-text text-danger clearfix">
                <a class="pull-left" @click="openFile(message.fileContentType, message.file)" v-text="t$('entity.action.open')"></a><br />
                <span class="pull-left">{{ message.fileContentType }}, {{ byteSize(message.file) }}</span>
                <button
                  type="button"
                  @click="
                    message.file = null;
                    message.fileContentType = null;
                  "
                  class="btn btn-secondary btn-xs pull-right"
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                </button>
              </div>
              <label for="file_file" v-text="t$('entity.action.addblob')" class="btn btn-primary pull-right"></label>
              <input
                type="file"
                ref="file_file"
                id="file_file"
                style="display: none"
                data-cy="file"
                @change="setFileData($event, message, 'file', false)"
              />
            </div>
            <input
              type="hidden"
              class="form-control"
              name="file"
              id="message-file"
              data-cy="file"
              :class="{ valid: !v$.file.$invalid, invalid: v$.file.$invalid }"
              v-model="v$.file.$model"
            />
            <input
              type="hidden"
              class="form-control"
              name="fileContentType"
              id="message-fileContentType"
              v-model="message.fileContentType"
            />
            <div v-if="v$.file.$anyDirty && v$.file.$invalid">
              <small class="form-text text-danger" v-for="error of v$.file.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.message.date')" for="message-date"></label>
            <div class="d-flex">
              <input
                id="message-date"
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
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.message.langKey')" for="message-langKey"></label>
            <input
              type="text"
              class="form-control"
              name="langKey"
              id="message-langKey"
              data-cy="langKey"
              :class="{ valid: !v$.langKey.$invalid, invalid: v$.langKey.$invalid }"
              v-model="v$.langKey.$model"
            />
            <div v-if="v$.langKey.$anyDirty && v$.langKey.$invalid">
              <small class="form-text text-danger" v-for="error of v$.langKey.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.message.countryKey')" for="message-countryKey"></label>
            <input
              type="text"
              class="form-control"
              name="countryKey"
              id="message-countryKey"
              data-cy="countryKey"
              :class="{ valid: !v$.countryKey.$invalid, invalid: v$.countryKey.$invalid }"
              v-model="v$.countryKey.$model"
            />
            <div v-if="v$.countryKey.$anyDirty && v$.countryKey.$invalid">
              <small class="form-text text-danger" v-for="error of v$.countryKey.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.message.subject')" for="message-subject"></label>
            <select class="form-control" id="message-subject" data-cy="subject" name="subject" v-model="message.subject" required>
              <option v-if="!message.subject" :value="null" selected></option>
              <option
                :value="message.subject && subjectOption.id === message.subject.id ? message.subject : subjectOption"
                v-for="subjectOption in subjects"
                :key="subjectOption.id"
              >
                {{ subjectOption.titleFr }}
              </option>
            </select>
          </div>
          <div v-if="v$.subject.$anyDirty && v$.subject.$invalid">
            <small class="form-text text-danger" v-for="error of v$.subject.$errors" :key="error.$uid">{{ error.$message }}</small>
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
<script lang="ts" src="./message-update.component.ts"></script>
