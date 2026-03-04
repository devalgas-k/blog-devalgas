<template>
  <div id="app">
    <div id="page-container">
      <div id="content-wrap">
        <ribbon></ribbon>
        <Suspense timeout="0">
          <template #default>
            <div class="suspense-content">
              <div id="app-header">
                <headers-async></headers-async>
              </div>
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
              <div class="row">
                <div class="col-lg-2 mt-2 mt-lg-5">
                  <div class="d-none d-lg-flex" v-if="ADSENSE_ENABLED && consentGiven && adsenseScriptReady && slotLeftValid">
                    <adsense
                      :ad-slot="ADSENSE_SLOT_SIDEBAR_LEFT"
                      format="auto"
                      :responsive="true"
                      style="display: block; width: 100%; min-height: 600px"
                    />
                  </div>
                </div>
                <div class="col-12 col-lg-8">
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
                <div class="col-lg-2 mt-2 mt-lg-5">
                  <div class="d-none d-lg-flex" v-if="ADSENSE_ENABLED && consentGiven && adsenseScriptReady && slotRightValid">
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
          </template>
          <template #fallback>
            <div class="skeleton-container">
              <div class="skeleton-header loading-header">
                <div class="ball">
                  <div class="inner">
                    <div class="line"></div>
                    <div class="li ne line--two"></div>
                    <div class="oval"></div>
                    <div class="oval oval--two"></div>
                  </div>
                </div>
                <div class="shadow"></div>
              </div>
              <div class="row">
                <div class="col-lg-2 mt-2 mt-lg-5"></div>
                <div class="col-12 col-lg-8">
                  <div class="container-fluid">
                    <div class="w-auto px-4 px-lg-0 mx-auto">
                      <div class="skeleton-content"></div>
                      <div class="skeleton-content"></div>
                      <div class="skeleton-content"></div>
                    </div>
                  </div>
                </div>
                <div class="col-lg-2 mt-2 mt-lg-5"></div>
              </div>
            </div>
          </template>
        </Suspense>
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

.skeleton-header {
  height: 56px;
  background: linear-gradient(90deg, rgba(0, 0, 0, 0.08) 25%, rgba(0, 0, 0, 0.15) 37%, rgba(0, 0, 0, 0.08) 63%);
  background-size: 400% 100%;
  animation: shimmer 1.2s infinite;
}
.skeleton-content {
  height: 24px;
  margin: 8px 0;
  background: linear-gradient(90deg, rgba(0, 0, 0, 0.08) 25%, rgba(0, 0, 0, 0.15) 37%, rgba(0, 0, 0, 0.08) 63%);
  background-size: 400% 100%;
  animation: shimmer 1.2s infinite;
}
.loading-header {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 56px;
  overflow: visible;
}
@keyframes shimmer {
  0% {
    background-position: 100% 0;
  }
  100% {
    background-position: 0 0;
  }
}
</style>
