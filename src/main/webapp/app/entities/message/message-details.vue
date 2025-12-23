<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="message">
        <h2 class="jh-entity-heading" data-cy="messageDetailsHeading">
          <span v-text="t$('devalgasApp.message.detail.title')"></span> {{ message.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('devalgasApp.message.name')"></span>
          </dt>
          <dd>
            <span>{{ message.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.message.email')"></span>
          </dt>
          <dd>
            <span>{{ message.email }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.message.phone')"></span>
          </dt>
          <dd>
            <span>{{ message.phone }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.message.message')"></span>
          </dt>
          <dd>
            <span>{{ message.message }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.message.file')"></span>
          </dt>
          <dd>
            <div v-if="message.file">
              <a @click="openFile(message.fileContentType, message.file)" v-text="t$('entity.action.open')"></a>
              {{ message.fileContentType }}, {{ byteSize(message.file) }}
            </div>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.message.date')"></span>
          </dt>
          <dd>
            <span v-if="message.date">{{ formatDateLong(message.date) }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.message.langKey')"></span>
          </dt>
          <dd>
            <span>{{ message.langKey }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.message.countryKey')"></span>
          </dt>
          <dd>
            <span>{{ message.countryKey }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.message.subject')"></span>
          </dt>
          <dd>
            <div v-if="message.subject">
              <router-link :to="{ name: 'SubjectView', params: { subjectId: message.subject.id } }">{{
                message.subject.titleFr
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="message.id" :to="{ name: 'MessageEdit', params: { messageId: message.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./message-details.component.ts"></script>
