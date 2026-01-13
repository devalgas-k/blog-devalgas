<template>
  <div class="row">
    <article-search class="col-12 mt-4"></article-search>
    <articles-home class="col-12 mt-1"></articles-home>
  </div>
  <div class="home row d-none">
    <div class="col-md-3">
      <span class="hipster img-fluid rounded"></span>
    </div>
    <div class="col-md-9">
      <h1 class="display-4 home__title" v-text="t$('home.title')"></h1>
      <p class="lead home__subtitle" v-text="t$('home.subtitle')"></p>

      <div>
        <div class="alert alert-success" v-if="authenticated">
          <span v-if="username" v-text="t$('home.logged.message', { username })"></span>
        </div>

        <div class="alert alert-warning" v-if="!authenticated">
          <span v-text="t$('global.messages.info.authenticated.prefix')"></span>
          <a class="alert-link" @click="showLogin()" v-text="t$('global.messages.info.authenticated.link')"></a
          ><span v-html="t$('global.messages.info.authenticated.suffix')"></span>
        </div>
        <div class="alert alert-warning" v-if="!authenticated">
          <span v-text="t$('global.messages.info.register.noaccount')"></span>&nbsp;
          <router-link class="alert-link" to="/register" v-text="t$('global.messages.info.register.link')"></router-link>
        </div>
      </div>

      <p v-text="t$('home.question')"></p>

      <ul class="home__links">
        <li class="home__link-item">
          <a
            class="home__link"
            href="https://www.jhipster.tech/"
            target="_blank"
            rel="noopener noreferrer"
            v-text="t$('home.link.homepage')"
          ></a>
        </li>
        <li>
          <a
            href="https://stackoverflow.com/tags/jhipster/info"
            target="_blank"
            rel="noopener noreferrer"
            v-text="t$('home.link.stackoverflow')"
          ></a>
        </li>
        <li>
          <a
            href="https://github.com/jhipster/generator-jhipster/issues?state=open"
            target="_blank"
            rel="noopener noreferrer"
            v-text="t$('home.link.bugtracker')"
          ></a>
        </li>
        <li>
          <a
            href="https://gitter.im/jhipster/generator-jhipster"
            target="_blank"
            rel="noopener noreferrer"
            v-text="t$('home.link.chat')"
          ></a>
        </li>
        <li>
          <a href="https://twitter.com/jhipster" target="_blank" rel="noopener noreferrer" v-text="t$('home.link.follow')"></a>
        </li>
      </ul>

      <p>
        <span v-text="t$('home.like')"></span>
        <a href="https://github.com/jhipster/generator-jhipster" target="_blank" rel="noopener noreferrer" v-text="t$('home.github')"></a>!
      </p>
    </div>
  </div>
</template>

<script lang="ts" src="./home.component.ts"></script>
<style lang="scss" scoped>
.home {
  &__title {
    margin-bottom: 0.5rem;
  }
  &__subtitle {
    margin-bottom: 1rem;
    color: var(--secondary);
  }
  & .alert {
    margin-top: 0.5rem;
  }
  &__links {
    list-style: none;
    padding-left: 0;
    margin-top: 1rem;
  }
  &__link-item {
    margin-bottom: 0.25rem;
  }
  &__link {
    text-decoration: underline;
  }
}
</style>
