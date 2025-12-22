<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="subscribe">
        <h2 class="jh-entity-heading" data-cy="subscribeDetailsHeading">
          <span v-text="t$('devalgasApp.subscribe.detail.title')"></span> {{ subscribe.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('devalgasApp.subscribe.email')"></span>
          </dt>
          <dd>
            <span>{{ subscribe.email }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.subscribe.langKey')"></span>
          </dt>
          <dd>
            <span>{{ subscribe.langKey }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.subscribe.countryKey')"></span>
          </dt>
          <dd>
            <span>{{ subscribe.countryKey }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.subscribe.date')"></span>
          </dt>
          <dd>
            <span v-if="subscribe.date">{{ formatDateLong(subscribe.date) }}</span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="subscribe.id"
          :to="{ name: 'SubscribeEdit', params: { subscribeId: subscribe.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./subscribe-details.component.ts"></script>
