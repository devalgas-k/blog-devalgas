<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="headers">
        <h2 class="jh-entity-heading" data-cy="headersDetailsHeading">
          <span v-text="t$('devalgasApp.headers.detail.title')"></span> {{ headers.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('devalgasApp.headers.logoHeaders')"></span>
          </dt>
          <dd>
            <div v-if="headers.logoHeaders">
              <a @click="openFile(headers.logoHeadersContentType, headers.logoHeaders)">
                <img
                  :src="'data:' + headers.logoHeadersContentType + ';base64,' + headers.logoHeaders"
                  style="max-width: 100%"
                  alt="headers"
                />
              </a>
              {{ headers.logoHeadersContentType }}, {{ byteSize(headers.logoHeaders) }}
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="headers.id" :to="{ name: 'HeadersEdit', params: { headersId: headers.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./headers-details.component.ts"></script>
