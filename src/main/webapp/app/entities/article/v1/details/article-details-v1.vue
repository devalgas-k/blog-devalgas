<template>
  <div class="container-fluid">
    <div class="w-100 px-0 mx-auto">
      <div v-show="!isLoading && isFullyReady">
        <article-info-v1 :article="article" class="mt-5" @mounted="onInfoMounted"></article-info-v1>
        <div v-if="isDesktop">
          <p-splitter class="mt-5" style="width: 100%">
            <p-splitter-panel :size="100">
              <div class="editor-content" aria-busy="false">
                <Transition name="fade-fast">
                  <div class="github-markdown-body article-details-markdown" v-html="decodedMarkdownContent.html"></div>
                </Transition>
              </div>
            </p-splitter-panel>
          </p-splitter>
        </div>
        <div v-else class="mt-4">
          <div class="editor-content editor-content--mobile" aria-busy="false">
            <Transition name="fade-fast">
              <div class="github-markdown-body article-details-markdown" v-html="decodedMarkdownContent.html"></div>
            </Transition>
          </div>
        </div>
        <div class="d-none justify-content-end mt-5">
          <router-link :to="{ name: 'Home' }" custom v-slot="{ navigate }">
            <button @click="navigate" id="articles-border-all" class="btn btn-ark">
              <font-awesome-icon icon="border-all"></font-awesome-icon>
              <span v-text="t$('devalgasApp.articleV1.content.explore')"></span>
            </button>
          </router-link>
        </div>
      </div>
      <div v-if="isLoading || !isFullyReady">
        <article-details-skeleton-v1 />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import component from './article-details-v1.component';
export default component;
</script>

<style lang="scss" scoped>
.p-splitter {
  background: var(--dark) !important;
  border: none !important;
  min-height: 900px;
  box-shadow: -1rem 0 3rem #00000067 !important;
}

.editor-content {
  width: 100%;
  margin: 0 auto;
  padding: 1.25rem;
  border-radius: 16px;
  box-shadow: -1rem 0 3rem #00000067;
  transition: 0.2s;
}

.editor-content--mobile {
  margin-top: 0;
}

.fade-fast-enter-active,
.fade-fast-leave-active {
  transition: opacity 0.15s ease;
}

.fade-fast-enter-from,
.fade-fast-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .p-splitter {
    min-height: auto;
    box-shadow: none !important;
  }
}

@media (max-width: 576px) {
  .editor-content,
  .editor-content--mobile {
    padding: 0.5rem;
    border-radius: 10px;
    box-shadow: none;
  }
}
</style>
