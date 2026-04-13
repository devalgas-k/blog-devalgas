<template>
  <div id="app">
    <div id="page-container">
      <div id="content-wrap">
        <ribbon></ribbon>
        <Suspense timeout="0">
          <template #default>
            <div v-if="shellReady" class="suspense-content">
              <div id="app-header">
                <headers-async></headers-async>
              </div>
              <div class="row">
                <div class="col-12 col-lg-12">
                  <div class="container">
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
                </div>
              </div>
              <div id="footer">
                <footers-v1></footers-v1>
                <ScrollTop />
              </div>
            </div>
            <div v-else class="loading-center">
              <div class="ball">
                <div class="inner">
                  <div class="line"></div>
                  <div class="line line--two"></div>
                  <div class="oval"></div>
                  <div class="oval oval--two"></div>
                </div>
              </div>
              <div class="shadow"></div>
            </div>
          </template>
          <template #fallback>
            <div class="loading-center">
              <div class="ball">
                <div class="inner">
                  <div class="line"></div>
                  <div class="line line--two"></div>
                  <div class="oval"></div>
                  <div class="oval oval--two"></div>
                </div>
              </div>
              <div class="shadow"></div>
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
</style>
