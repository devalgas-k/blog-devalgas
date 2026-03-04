<template>
  <div id="app">
    <div id="page-container">
      <div id="content-wrap">
        <ribbon></ribbon>
        <div id="app-header">
          <headers-v1></headers-v1>
        </div>

        <div class="row">
          <div class="col-lg-2 mt-15rem">
            <div v-if="ADSENSE_ENABLED && consentGiven && adsenseScriptReady && slotLeftValid" class="d-none d-lg-flex">
              <adsense
                :ad-slot="ADSENSE_SLOT_SIDEBAR_LEFT"
                format="auto"
                :responsive="true"
                style="display: block; width: 100%; min-height: 600px"
              />
            </div>
          </div>
          <div class="col-12 col-lg-8">
            <div class="row d-none">
              <div class="col-12">
                <div v-if="ADSENSE_ENABLED && consentGiven && adsenseScriptReady && slotTopValid && !topNoFill" class="d-none d-lg-flex">
                  <adsense
                    ref="adsenseTop"
                    :ad-slot="ADSENSE_SLOT_TOP"
                    format="auto"
                    :responsive="true"
                    style="display: block; width: 100%; min-height: 90px"
                    @no-fill="onTopNoFill"
                  />
                </div>
                <div
                  v-if="ADSENSE_ENABLED && consentGiven && adsenseScriptReady && slotTopValid && topNoFill"
                  class="d-none d-lg-flex"
                  style="width: 100%"
                >
                  <div style="display: block; width: 100%; min-height: 90px">
                    <hr style="opacity: 0.35; border-color: var(--dark)" />
                  </div>
                </div>
              </div>
            </div>
            <div class="container-fluid">
              <div class="w-auto px-4 px-lg-0 mx-auto">
                <router-view v-slot="{ Component, route }">
                  <keep-alive>
                    <component :is="Component" v-if="route.meta && route.meta.keepAlive" />
                  </keep-alive>
                  <component :is="Component" v-if="!route.meta || !route.meta.keepAlive" />
                </router-view>
              </div>
              <b-modal id="login-page" v-model="loginModalOpen" hide-footer lazy>
                <template #modal-title>
                  <span v-if="i18nReady" data-cy="loginTitle" id="login-title" v-text="t$('login.title')"></span>
                </template>
                <login-form></login-form>
              </b-modal>
            </div>
            <div class="row d-none">
              <div class="col-12">
                <div v-if="ADSENSE_ENABLED && consentGiven && adsenseScriptReady && slotFooterValid" class="d-none d-lg-flex">
                  <adsense
                    :ad-slot="ADSENSE_SLOT_FOOTER"
                    format="auto"
                    :responsive="true"
                    style="display: block; width: 100%; min-height: 280px"
                    :collapse-if-no-fill="true"
                  />
                </div>
              </div>
            </div>
          </div>
          <div class="col-lg-2 mt-15rem">
            <div v-if="ADSENSE_ENABLED && consentGiven && adsenseScriptReady && slotRightValid" class="d-none d-lg-flex">
              <adsense
                :ad-slot="ADSENSE_SLOT_SIDEBAR_RIGHT"
                format="auto"
                :responsive="true"
                style="display: block; width: 100%; min-height: 250px"
              />
            </div>
          </div>
        </div>
        <div id="footer">
          <footers-v1></footers-v1>
          <ScrollTop />
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./app.component.ts"></script>

<style>
#page-container {
  position: relative;
  min-height: 100vh;
}

#content-wrap {
  padding-bottom: 2.5rem;
  /* Footer height */
}

#footer {
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 2.5rem;
}

.mt-15rem {
  margin-top: 15rem !important;
}

@media (max-width: 576px) {
  .container > .w-75.mx-auto {
    width: 100% !important;
  }
}

@media (max-width: 576px) {
  .container,
  .container-fluid,
  .container-xl,
  .container-lg,
  .container-md,
  .container-sm {
    padding-right: 0 !important;
    padding-left: 0 !important;
  }
}
</style>
