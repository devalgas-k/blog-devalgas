<template>
  <div>
    <div class="subscribe mx-auto">
      <div class="subscribe__icon">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="92"
          height="92"
          :fill="errorEmailExists || emailFormatError || recaptchaError ? 'var(--warning)' : 'var(--primary)'"
          viewBox="0 0 256 256"
        >
          <path
            d="M104,152a8,8,0,0,1-8,8H56a8,8,0,0,1,0-16H96A8,8,0,0,1,104,152Zm136-36v60a16,16,0,0,1-16,16H136v32a8,8,0,0,1-16,0V192H32a16,16,0,0,1-16-16V116A60.07,60.07,0,0,1,76,56h76V24a8,8,0,0,1,8-8h32a8,8,0,0,1,0,16H168V56h12A60.07,60.07,0,0,1,240,116ZM120,176V116a44,44,0,0,0-88,0v60Zm104-60a44.05,44.05,0,0,0-44-44H168v72a8,8,0,0,1-16,0V72H116.75A59.86,59.86,0,0,1,136,116v60h88Z"
          ></path>
        </svg>
      </div>
      <h5 class="subscribe__heading mt-n5 text-white" v-text="t$('devalgasApp.subscribe.title')"></h5>
      <div class="footer-text mt-5">
        <h6
          class="alert alert-primary"
          v-if="!success && !emailFormatError && !recaptchaError && !errorEmailExists"
          v-text="t$('devalgasApp.subscribe.subTitle')"
        ></h6>
        <h6
          class="alert alert-warning"
          role="alert"
          v-if="recaptchaError && !errorEmailExists"
          v-text="t$('devalgasApp.subscribe.messages.error.recaptchaRequired')"
        ></h6>
        <h6 class="alert alert-primary" role="alert" v-if="success" v-html="t$('devalgasApp.subscribe.messages.success.validEmail')"></h6>
        <h6
          class="alert alert-warning"
          role="alert"
          v-if="errorEmailExists"
          v-html="t$('devalgasApp.subscribe.messages.error.emailexists')"
        ></h6>
        <h6
          class="alert alert-warning"
          role="alert"
          v-if="emailFormatError"
          v-text="t$('devalgasApp.subscribe.messages.error.invalidEmail')"
        ></h6>
      </div>
      <div class="d-flex justify-content-center mt-5">
        <VueRecaptcha
          :sitekey="siteKey"
          :load-recaptcha-script="true"
          @verify="handleSuccess"
          @error="handleError"
          @expired="handleError"
          v-if="!success && v$?.subscribe?.email?.$invalid === false"
        ></VueRecaptcha>
      </div>

      <div class="email-box" v-if="!success">
        <form id="subscribe-email-form" name="subscribeEmailForm" @submit.prevent="subscribeEmail()" no-validate>
          <div class="form-group">
            <b-input-group class="d-flex justify-content-between">
              <b-form-input
                class="form-control email-box__input"
                v-model="v$.subscribe.email.$model"
                :placeholder="t$('devalgasApp.subscribe.placeholder')"
                type="email"
                :class="{ 'is-invalid': emailFormatError }"
              ></b-form-input>
              <b-input-group-append>
                <b-button
                  class="btn btn-primary mx-1"
                  :class="{ 'btn-warning': errorEmailExists || emailFormatError }"
                  size="xs"
                  text="Button"
                  type="submit"
                  ><font-awesome-icon icon="paper-plane"
                /></b-button>
              </b-input-group-append>
            </b-input-group>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./subscribe-v1.component.ts"></script>

<style lang="scss" scoped>
// Variables
$white: #fff;
$black: #fff;
$text-color: #444;
$border-radius: 4px;
$brand-color: #073642;
$accent-color: #b58900;
$mobile-width: 700px;
$container-width: 50rem;
$mobile-container-width: 20rem;

.is-invalid {
  border-color: none !important;
}

.subscribe {
  background: $brand-color;
  color: $white;
  width: 100%;
  max-width: $container-width;
  margin: 0 auto;
  border-radius: $border-radius;
  text-align: center;
  padding: 10rem 4rem 4rem;
  position: relative;

  &__icon {
    position: absolute;
    top: -2rem;
    left: 50%;
    transform: translateX(-50%);
    background: $brand-color;
    padding: 2.5rem 8rem 0;
    border-radius: 50%;
    color: $accent-color;
  }

  &__heading {
    text-transform: uppercase;
  }

  p {
    font-size: 1.5rem;
  }

  @media only screen and (max-width: $mobile-width) {
    padding: 6rem 1.5rem 2rem;

    &__icon {
      transform: translateX(-50%);
      padding: 2rem 5rem 0;
    }
  }
}

.email-box {
  $box-width: 40rem;
  $mobile-box-width: 22rem;

  position: static;
  width: 100%;
  max-width: $box-width;
  margin: 0 auto;
  padding: 0 1rem;
  margin-top: 2rem;

  .input {
    color: lighten($black, 20%);
    display: block;
    width: 100%;
    height: 3.5rem;
    border-radius: $border-radius;
    border: 1px solid $accent-color;
    font-size: 1rem;
    padding: 1rem;

    &:hover,
    &:focus {
      border-color: darken($accent-color, 10%);
    }
  }

  .btn {
    box-shadow: none;
    background: var(--primary);

    &:hover,
    &:focus {
      background: darken($accent-color, 10%);
    }
  }

  @media only screen and (max-width: $mobile-width) {
    max-width: $mobile-box-width;
    padding: 0 0.5rem;
    margin-top: 1.5rem;
  }
}
</style>
