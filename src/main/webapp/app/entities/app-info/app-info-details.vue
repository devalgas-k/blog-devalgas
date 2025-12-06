<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="appInfo">
        <h2 class="jh-entity-heading" data-cy="appInfoDetailsHeading">
          <span v-text="t$('devalgasApp.appInfo.detail.title')"></span> {{ appInfo.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('devalgasApp.appInfo.keyInfo')"></span>
          </dt>
          <dd>
            <span>{{ appInfo.keyInfo }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.appInfo.valueInfo')"></span>
          </dt>
          <dd>
            <span>{{ appInfo.valueInfo }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.appInfo.headers')"></span>
          </dt>
          <dd>
            <div v-if="appInfo.headers">
              <router-link :to="{ name: 'HeadersView', params: { headersId: appInfo.headers.id } }">{{ appInfo.headers.id }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.appInfo.footers')"></span>
          </dt>
          <dd>
            <div v-if="appInfo.footers">
              <router-link :to="{ name: 'FootersView', params: { footersId: appInfo.footers.id } }">{{ appInfo.footers.id }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="appInfo.id" :to="{ name: 'AppInfoEdit', params: { appInfoId: appInfo.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./app-info-details.component.ts"></script>
