<template>
  <!-- TODO https://bootstrapbrain.com/component/bootstrap-signup-form-design/ -->

  <div class="footer-v1 mt-2">
    <div class="container">
      <div class="footer-v1__content py-4">
        <div class="row">
          <p-separator class="col-12" icon="envelopes-bulk"></p-separator>
          <subscribe-v1 class="mt-3"></subscribe-v1>
        </div>
      </div>
    </div>
    <footer class="footer-v1__section">
      <banner-footers></banner-footers>

      <div class="footer-v1__copyright">
        <div class="container">
          <div class="row justify-content-between align-items-center">
            <div class="col-xl-6 col-lg-6 text-center text-lg-left">
              <div class="footer-v1__copyright-text">
                <p><span v-html="t$('globalV1.footer.copyright')"></span> <a href="#">Devalgas</a></p>
              </div>
            </div>
            <div class="col-xl-6 col-lg-6 d-none d-lg-block text-right">
              <div class="footer-v1__menu">
                <ul>
                  <li>
                    <b-link :to="{ path: '/', hash: writingHash }"
                      ><font-awesome-icon icon="newspaper" /> <span v-text="t$('globalV1.headers.blog')"></span
                    ></b-link>
                  </li>
                  <li class="d-none"><a href="#">Terms</a></li>
                  <li><a href="#" class="d-none">Privacy</a></li>
                  <li><a href="#" class="d-none">Policy</a></li>
                  <li class="footer-v1__contact" ref="contactRef">
                    <b-link href="#" @click.prevent="toggleContactDropup">
                      <span>
                        <font-awesome-icon icon="inbox"></font-awesome-icon>
                        <span v-text="t$('globalV1.headers.contactUs')"></span>
                      </span>
                    </b-link>
                    <div v-if="showContactDropup" class="footer-v1__dropup-menu">
                      <div class="d-flex justify-content-between">
                        <button type="button" class="btn btn-white btn-sm ml-2" @click="openWhatsApp">
                          <svg
                            xmlns="http://www.w3.org/2000/svg"
                            class="mt-n1"
                            width="21"
                            height="21"
                            fill="var(--white)"
                            viewBox="0 0 256 256"
                          >
                            <path
                              d="M187.3,159.06A36.09,36.09,0,0,1,152,188a84.09,84.09,0,0,1-84-84A36.09,36.09,0,0,1,96.94,68.7,12,12,0,0,1,110,75.1l11.48,23a12,12,0,0,1-.75,12l-8.52,12.78a44.56,44.56,0,0,0,20.91,20.91l12.78-8.52a12,12,0,0,1,12-.75l23,11.48A12,12,0,0,1,187.3,159.06ZM236,128A108,108,0,0,1,78.77,224.15L46.34,235A20,20,0,0,1,21,209.66l10.81-32.43A108,108,0,1,1,236,128Zm-24,0A84,84,0,1,0,55.27,170.06a12,12,0,0,1,1,9.81l-9.93,29.79,29.79-9.93a12.1,12.1,0,0,1,3.8-.62,12,12,0,0,1,6,1.62A84,84,0,0,0,212,128Z"
                            ></path>
                          </svg>
                          Whatsapp
                        </button>
                        <button type="button" class="btn btn-white btn-sm mr-5" @click="openMailTo">
                          <svg
                            xmlns="http://www.w3.org/2000/svg"
                            class="mt-n1"
                            width="21"
                            height="21"
                            fill="var(--white)"
                            viewBox="0 0 256 256"
                          >
                            <path
                              d="M124,128a36,36,0,1,0-36,36A36,36,0,0,0,124,128Zm-48,0a12,12,0,1,1,12,12A12,12,0,0,1,76,128Zm148-28H212V32a12,12,0,0,0-12-12H104A12,12,0,0,0,92,32V56H36A20,20,0,0,0,16,76V180a20,20,0,0,0,20,20H68v16a20,20,0,0,0,20,20H216a20,20,0,0,0,20-20V112A12,12,0,0,0,224,100Zm-52.45,68L212,136.54v62.92ZM116,44h72v80.8l-28,21.78V76a20,20,0,0,0-20-20H116ZM40,80h96v96H40ZM92,200h48a20,20,0,0,0,18.28-11.92L189,212H92Z"
                            ></path>
                          </svg>
                          Mail
                        </button>
                      </div>
                      <p-separator class="mt-3" :label="separatorLabel"></p-separator>
                      <message-contact-v1 @saved="onContactSaved"></message-contact-v1>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script lang="ts" src="./footers-v1.component.ts"></script>

<style lang="scss" scoped>
.footer-v1 {
  ul {
    margin: 0;
    padding: 0;
  }

  &__section {
    position: relative;
  }

  &__content {
    position: relative;
    z-index: 2;
  }

  &__copyright {
    background-color: rgba(0, 0, 0, 0.2);
    padding: 10px 0;
    position: relative;
    z-index: 2;
  }

  &__copyright-text {
    p {
      margin: 0;
      font-size: 14px;
      color: var(--light);
    }
    p a {
      color: var(--primary);
    }
  }

  &__menu {
    li {
      display: inline-block;
      margin-left: 20px;
    }
    li:hover a {
      color: var(--primary);
    }
    li a {
      font-size: 14px;
      color: #878787;
    }
  }

  &__contact {
    position: relative;
  }

  &__dropup-menu {
    position: absolute;
    bottom: calc(100% + 8px);
    left: 50%;
    right: auto;
    min-width: 420px;
    background: var(--dark);
    border-radius: 4px;
    z-index: 10;
    padding: 12px;
    color: var(--light);
    text-align: left;
    transform: translateX(-50%);
  }

  &__dropup-menu :deep(.pull-right) {
    float: none;
  }
}
</style>
