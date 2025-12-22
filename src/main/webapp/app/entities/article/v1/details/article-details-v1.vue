<template>
  <div class="d-none row ad-top">
    <div class="col-12">
      <adsense client="ca-pub-6181972205565553" ad-slot="4433984685" format="auto" />
    </div>
  </div>

  <article-info-v1 :article="article" class="mt-5"></article-info-v1>
  <p-splitter class="mt-5" style="width: 100%">
    <p-splitter-panel :size="100">
      <div class="editor-content" :aria-busy="!decodedMarkdownContent.html">
        <Transition name="fade">
          <div v-if="decodedMarkdownContent.html" v-html="decodedMarkdownContent.html"></div>
        </Transition>
        <template v-if="!decodedMarkdownContent.html">
          <p-skeleton width="70%" height="28px" class="mb-2" />
          <p-skeleton width="100%" height="16px" class="mb-2" />
          <p-skeleton width="100%" height="16px" class="mb-2" />
          <p-skeleton width="60%" height="16px" />
        </template>
      </div>
    </p-splitter-panel>
  </p-splitter>
  <!--  TODO lors du clique dois arriver #articles-->
  <div class="d-flex justify-content-end mt-5">
    <router-link :to="{ name: 'Home' }" custom v-slot="{ navigate }">
      <button @click="navigate" id="articles-border-all" class="btn btn-primary">
        <font-awesome-icon icon="border-all"></font-awesome-icon>
        <span v-text="t$('devalgasApp.articleV1.content.explore')"></span>
      </button>
    </router-link>
  </div>
</template>
<script lang="ts" src="./article-details-v1.component.ts"></script>

<style lang="scss" scoped>
.p-splitter {
  background: var(--dark) !important;
  border: 1 solid var(--primary) !important;
  min-height: 900px;
}
.ad-top {
  min-height: 120px;
}
.github-markdown-body {
  color: var(--white) !important;
  table tr {
    color: #444d56 !important;
  }
}
code {
  color: var(--blue) !important;
  font-weight: bold !important;
}
.editor-content {
  width: 100%;
  max-width: 880px;
  margin: 0 auto;
  padding: 1.25rem;
  border-radius: 16px;
  box-shadow: -1rem 0 3rem #00000067;
  transition: 0.2s;

  &.fade-enter-active,
  &.fade-leave-active {
    transition: opacity 0.2s ease-in-out;
  }
  &.fade-enter-from,
  &.fade-leave-to {
    opacity: 0;
  }

  :deep(img),
  :deep(video),
  :deep(canvas) {
    display: block;
    max-width: 100% !important;
    height: auto !important;
    margin: 1rem auto;
  }
  :deep(iframe) {
    display: block;
    max-width: 100% !important;
    width: 100% !important;
    margin: 1rem auto;
  }
  :deep(pre) {
    overflow-x: auto;
  }
  :deep(table) {
    display: block;
    width: 100%;
    overflow-x: auto;
  }
}
</style>
