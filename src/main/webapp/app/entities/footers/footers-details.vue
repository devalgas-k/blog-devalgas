<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="footers">
        <h2 class="jh-entity-heading" data-cy="footersDetailsHeading">
          <span v-text="t$('devalgasApp.footers.detail.title')"></span> {{ footers.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('devalgasApp.footers.logoFooters')"></span>
          </dt>
          <dd>
            <div v-if="footers.logoFooters">
              <a @click="openFile(footers.logoFootersContentType, footers.logoFooters)">
                <img
                  :src="'data:' + footers.logoFootersContentType + ';base64,' + footers.logoFooters"
                  style="max-width: 100%"
                  alt="footers"
                />
              </a>
              {{ footers.logoFootersContentType }}, {{ byteSize(footers.logoFooters) }}
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="footers.id" :to="{ name: 'FootersEdit', params: { footersId: footers.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./footers-details.component.ts"></script>
