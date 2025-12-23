<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="subject">
        <h2 class="jh-entity-heading" data-cy="subjectDetailsHeading">
          <span v-text="t$('devalgasApp.subject.detail.title')"></span> {{ subject.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('devalgasApp.subject.titleFr')"></span>
          </dt>
          <dd>
            <span>{{ subject.titleFr }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.subject.titleEn')"></span>
          </dt>
          <dd>
            <span>{{ subject.titleEn }}</span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="subject.id" :to="{ name: 'SubjectEdit', params: { subjectId: subject.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./subject-details.component.ts"></script>
