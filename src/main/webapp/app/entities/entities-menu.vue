<template>
  <div>
    <b-dropdown-item to="/headers">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.headers')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/footers">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.footers')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/app-info">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.appInfo')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/category-article">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.categoryArticle')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/article">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.article')"></span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>
