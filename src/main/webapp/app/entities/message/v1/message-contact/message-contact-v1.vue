<template>
  <div class="message-contact-v1 message-contact-v1__container">
    <form class="creative-form message-contact-v1__form" name="editForm" novalidate @submit.prevent="save()">
      <h3 class="d-none form-title message-contact-v1__title">
        <label class="form-control-label" v-text="t$('globalV1.headers.contactUs')" for="message-subject"></label>
      </h3>
      <div class="mb-1 mt-3">
        <div class="form-group message-contact-v1__group">
          <b-form-input
            size="xs"
            type="text"
            class="form-control"
            :placeholder="t$('devalgasApp.messageContactV1.name')"
            name="name"
            id="message-name"
            data-cy="name"
            :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
            v-model="v$.name.$model"
            required
          />
          <div v-if="v$.name.$anyDirty && v$.name.$invalid">
            <small class="form-text text-danger nowrap" v-for="error of v$.name.$errors" :key="error.$uid">{{ t$(error.$message) }}</small>
          </div>
        </div>
      </div>
      <div class="mb-1">
        <div class="form-group message-contact-v1__group">
          <b-form-input
            type="text"
            class="form-control"
            name="email"
            id="message-email"
            data-cy="email"
            :class="{ valid: !v$.email.$invalid, invalid: v$.email.$invalid }"
            v-model="v$.email.$model"
            :placeholder="t$('devalgasApp.messageContactV1.mail')"
            required
          />
          <div v-if="v$.email.$anyDirty && v$.email.$invalid">
            <small class="form-text text-danger nowrap" v-for="error of v$.email.$errors" :key="error.$uid">{{ t$(error.$message) }}</small>
          </div>
        </div>
      </div>
      <div class="mb-1">
        <div class="row">
          <div class="col-9">
            <div class="form-group message-contact-v1__group">
              <label class="form-control-label" v-text="t$('devalgasApp.messageContactV1.subject')" for="message-subject"></label>
              <select
                class="form-control"
                id="message-subject"
                data-cy="subject"
                name="subject"
                :class="{ valid: !v$.subject.$invalid, invalid: v$.subject.$invalid }"
                v-model="v$.subject.$model"
                required
              >
                <option v-if="!message.subject" :value="null" selected></option>
                <option
                  :value="message.subject && subjectOption.id === message.subject.id ? message.subject : subjectOption"
                  v-for="subjectOption in filteredSubjects"
                  :key="subjectOption.id"
                >
                  {{ (currentLanguage ?? 'fr').split('-')[0].toLowerCase() === 'fr' ? subjectOption.titleFr : subjectOption.titleEn }}
                </option>
              </select>
              <div v-if="v$.subject.$anyDirty && v$.subject.$invalid">
                <small class="form-text text-danger nowrap" v-for="error of v$.subject.$errors" :key="error.$uid">
                  {{ t$(error.$message) }}
                </small>
              </div>
            </div>
          </div>
          <div class="col-3 px-0">
            <div class="form-group message-contact-v1__group">
              <label class="form-control-label" v-text="t$('devalgasApp.messageContactV1.file')" for="message-file"></label>
              <div>
                <label for="file_file" class="btn btn-dark pull-right"><font-awesome-icon icon="file-upload"></font-awesome-icon></label>
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
            </div>
          </div>
        </div>
      </div>
      <div v-if="v$.file.$anyDirty && v$.file.$invalid" class="form-text clearfix mb-1">
        <small class="text-danger nowrap" v-for="error of v$.file.$errors" :key="error.$uid">
          {{ t$(error.$message) }}
        </small>
      </div>
      <div v-if="message.file" class="d-flex justify-content-between form-text clearfix mb-1">
        <a class="pull-left text-danger" @click="openFile(message.fileContentType, message.file)" v-text="t$('entity.action.open')"></a
        ><br />
        <span class="pull-left text-primary nowrap ml-auto"
          >{{ mimeSubtype(message.fileContentType) }}, {{ byteSizeMb(message.file) }}</span
        >
        <a class="text-danger" @click.prevent.stop="clearFile($event)">
          <font-awesome-icon icon="times"></font-awesome-icon>
        </a>
      </div>
      <div class="mb-1">
        <div class="form-group message-contact-v1__group">
          <textarea
            type="text"
            class="form-control"
            name="message"
            id="message-message"
            data-cy="message"
            :class="{ valid: !v$.message.$invalid, invalid: v$.message.$invalid }"
            v-model="v$.message.$model"
            required
            rows="4"
            :placeholder="t$('devalgasApp.messageContactV1.message')"
            maxlength="256"
          />
          <div class="d-flex justify-content-between">
            <div v-if="v$.message.$anyDirty && v$.message.$invalid">
              <small class="form-text text-danger" v-for="error of v$.message.$errors" :key="error.$uid">{{ t$(error.$message) }}</small>
            </div>
            <small class="form-text text-muted ml-auto"> {{ (v$.message.$model || '').length }}/256 </small>
          </div>
        </div>
      </div>
      <div class="d-flex justify-content-center">
        <VueRecaptcha
          class="my-2"
          :sitekey="siteKey"
          :load-recaptcha-script="true"
          @verify="handleSuccess"
          @error="handleError"
          @expired="handleError"
          v-if="showRecaptcha"
        ></VueRecaptcha>
      </div>
      <h6
        class="alert alert-danger text-dark font-weight-bold"
        role="alert"
        v-if="recaptchaError"
        v-text="t$('devalgasApp.messageContactV1.messages.error.recaptchaRequired')"
      ></h6>
      <div class="d-flex justify-content-between">
        <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-dark nowrap" @click="clearInputs()">
          <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('globalV1.actions.reset')"></span>
        </button>
        <button type="submit" id="save-entity" data-cy="entityCreateSaveButton" :disabled="v$.$invalid || isSaving" class="btn btn-primary">
          <font-awesome-icon icon="paper-plane"></font-awesome-icon>&nbsp;<span v-text="t$('globalV1.actions.sent')"></span>
        </button>
      </div>
      <div class="d-flex justify-content-center mt-4">
        <div class="loader headers-v1__loader message-contact-v1__loader"></div>
      </div>
    </form>
  </div>
</template>

<script lang="ts" src="./message-contact-v1.component.ts"></script>

<style lang="scss" scoped>
.message-contact-v1 {
  &__container {
    margin: 1rem auto;
    min-width: 300px;
    position: relative;
  }

  &__title {
    font-weight: 600;
    margin-bottom: 1.5rem;
    font-size: 1.5rem;
  }

  &__group {
    margin-bottom: 0;
    min-width: 150px;
  }

  &__loader {
    position: relative;
    width: 300px;
    height: 3px;
    background: linear-gradient(to right, transparent, var(--primary), transparent);
    overflow: hidden;

    &::after {
      content: '';
      position: absolute;
      translate: -200px 0;
      width: 150px;
      height: 100%;
      background: linear-gradient(to right, transparent, var(--primary), transparent);
      animation: slide 1s infinite;
    }
  }
}

@keyframes slide {
  100% {
    translate: 300px 0;
  }
}
</style>
