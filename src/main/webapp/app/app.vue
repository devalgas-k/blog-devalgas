<template>
  <div id="app">
    <div id="page-container">
      <div id="content-wrap">
        <ribbon></ribbon>
        <div id="app-header">
          <headers-v1></headers-v1>
        </div>

        <div class="container">
          <div class="w-75 mx-auto">
            <router-view></router-view>
          </div>
          <b-modal id="login-page" v-model="loginModalOpen" hide-footer lazy>
            <template #modal-title>
              <span v-if="i18nReady" data-cy="loginTitle" id="login-title" v-text="t$('login.title')"></span>
            </template>
            <login-form></login-form>
          </b-modal>
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
