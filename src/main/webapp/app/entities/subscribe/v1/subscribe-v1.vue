<template>
  <div id="newsletter" class="newsletter w-100">
    <div class="subscribe ml-4">
      <div class="row">
        <div class="col-sm-12">
          <div class="content">
            <h3 v-if="!success" class="text-white" v-text="t$('devalgasApp.subscribeV1.title')"></h3>
            <h6 v-if="!success" class="content-tuned"></h6>
            <h6
              class="alert alert-primary text-dark font-weight-bold"
              v-if="!success && !emailFormatError && !recaptchaError && !errorEmailExists"
              v-text="t$('devalgasApp.subscribeV1.subTitle')"
            ></h6>
            <h6
              class="alert alert-danger text-dark font-weight-bold"
              role="alert"
              v-if="recaptchaError && !errorEmailExists"
              v-text="t$('devalgasApp.subscribeV1.messages.error.recaptchaRequired')"
            ></h6>
            <h6
              class="alert alert-danger text-dark font-weight-bold"
              role="alert"
              v-if="errorEmailExists"
              v-html="t$('devalgasApp.subscribeV1.messages.error.emailexists')"
            ></h6>
            <h6
              class="alert alert-danger text-dark font-weight-bold"
              role="alert"
              v-if="emailFormatError"
              v-text="t$('devalgasApp.subscribeV1.messages.error.invalidEmail')"
            ></h6>
            <div class="d-flex justify-content-center my-3">
              <!--              https://www.google.com/recaptcha/admin/create-->
              <VueRecaptcha
                :sitekey="siteKey"
                :load-recaptcha-script="true"
                @verify="handleSuccess"
                @error="handleError"
                @expired="handleError"
                v-if="!success && showRecaptcha"
              ></VueRecaptcha>
            </div>
            <div class="input-group" v-if="!success">
              <input
                type="email"
                class="form-control"
                :placeholder="t$('devalgasApp.subscribeV1.placeholder')"
                v-model="v$.subscribe.email.$model"
                :class="{ 'is-invalid': emailFormatError }"
                @keydown.enter.prevent="subscribeEmail()"
              />
              <span class="input-group-btn"></span>
              <button class="btn" type="submit" @click="subscribeEmail()"><font-awesome-icon icon="paper-plane" /></button>
            </div>
            <!--            TODO unsubscribe-->
            <div class="text-center d-none">
              <a class="unsubscribe-link" href="#">Would you like to unsubscribe?</a>
            </div>

            <div class="text-center mt-5">
              <social-media></social-media>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./subscribe-v1.component.ts"></script>

<style lang="scss" scoped>
.newsletter {
  background: rgba(0, 0, 0, 0.3) !important;
  padding: 40px 40px;
  z-index: 5;

  .content {
    margin: 0 auto;
    text-align: center;
    position: relative;
    z-index: 2;
  }

  h2 {
    color: var(--white);
    margin-bottom: 6px;
  }

  .content-tuned {
    font-size: 0.8rem;
    margin-bottom: 34px !important;
    color: var(--white);
  }

  .form-control {
    height: 45px !important;
    border-color: var(--white);
    border-radius: 2px 0px 0px 2px;
    /*
    background: rgba(0, 0, 0, 0.3) !important;
    */
    color: var(--dark) !important;

    &:focus {
      color: #495057;
      border: 2px solid var(--dark);
      border-color: var(--primary);
      outline: 0;
      box-shadow: 0 0 0 0rem rgba(0, 123, 255, 0.25) !important;
    }
  }

  .btn {
    border-radius: 0;
    color: var(--white) !important;
    font-weight: 900;

    &:hover {
      background: var(--primary);
    }
  }

  .unsubscribe-link {
    text-decoration: none !important;
    font-size: 13px;
    color: var(--white);

    &:hover {
      text-decoration: none !important;
      color: var(--white);
    }
  }

  :deep(.dp-social-media) {
    justify-content: center !important;
  }
}
@media (max-width: 576px) {
  .newsletter {
    background: transparent !important;
  }
}
</style>
