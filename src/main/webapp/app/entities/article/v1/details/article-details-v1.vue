<template>
  <div class="d-none row ad-top">
    <div class="col-12">
      <adsense :client="adsenseClient" :ad-slot="adsenseSlot" format="auto" />
    </div>
  </div>

  <article-info-v1 :article="article" class="mt-5"></article-info-v1>
  <p-splitter class="mt-5" style="width: 100%">
    <p-splitter-panel :size="100">
      <div class="editor-content" :aria-busy="!decodedMarkdownContent.html">
        <Transition name="fade">
          <div v-if="decodedMarkdownContent.html" class="github-markdown-body" v-html="decodedMarkdownContent.html"></div>
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
  <div class="d-none justify-content-end mt-5">
    <router-link :to="{ name: 'Home' }" custom v-slot="{ navigate }">
      <button @click="navigate" id="articles-border-all" class="btn btn-ark">
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
  border: none !important;
  min-height: 900px;
  box-shadow: -1rem 0 3rem #00000067 !important;
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
  /*
  max-width: 880px;
  */
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

  :deep(h1) {
    margin: 1.4rem 0 0.9rem;
    font-size: clamp(1.4rem, 4.8vw, 1.9rem);
    border-bottom: 1px solid rgba(255, 255, 255, 0.12);
    padding-bottom: 0.35rem;
  }
  :deep(h2) {
    margin: 1.2rem 0 0.7rem;
    font-size: clamp(1.2rem, 3.8vw, 1.5rem);
    border-bottom: 1px solid rgba(255, 255, 255, 0.12);
    padding-bottom: 0.25rem;
  }
  :deep(h3) {
    margin: 1rem 0 0.5rem;
    font-size: clamp(1.05rem, 3.2vw, 1.25rem);
  }
  :deep(p) {
    margin: 0.9rem 0;
    line-height: 1.75;
  }
  :deep(a) {
    color: var(--primary);
    text-decoration: underline;
    overflow-wrap: anywhere;
  }
  :deep(a:hover) {
    text-decoration: none;
  }
  :deep(ul),
  :deep(ol) {
    margin: 0.9rem 0;
    padding-left: 1.5em;
  }
  :deep(li) {
    margin: 0.25rem 0;
  }
  :deep(blockquote) {
    margin: 1rem 0;
    padding: 0.5rem 0.9rem;
    color: rgba(255, 255, 255, 0.85);
    border-left: 4px solid var(--primary);
    background: rgba(255, 255, 255, 0.04);
    border-radius: 8px;
  }
  :deep(hr) {
    border: 0;
    border-top: 1px solid rgba(255, 255, 255, 0.12);
    margin: 1.4rem 0;
  }
  :deep(code) {
    color: var(--blue);
    background: rgba(255, 255, 255, 0.06);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 6px;
    padding: 0.12rem 0.3rem;
    font-weight: 600;
    font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
    font-size: 0.95em;
    overflow-wrap: anywhere;
  }
  :deep(pre) {
    -webkit-overflow-scrolling: touch;
  }
  :deep(pre code) {
    background: transparent;
    border: 0;
    padding: 0;
    color: var(--white);
    font-size: 0.95em;
  }
  :deep(table) {
    -webkit-overflow-scrolling: touch;
  }
  :deep(th),
  :deep(td) {
    border: 1px solid rgba(255, 255, 255, 0.12);
    padding: 0.55rem 0.75rem;
    vertical-align: top;
  }
  :deep(th) {
    background: rgba(255, 255, 255, 0.06);
    font-weight: 700;
  }
  :deep(tr:nth-child(2n)) {
    background: rgba(255, 255, 255, 0.03);
  }

  @media (max-width: 768px) {
    max-width: 100%;
    padding: 1rem;
  }
  @media (max-width: 576px) {
    padding: 0.75rem;
    box-shadow: none;
    border-radius: 12px;
    :deep(h1) {
      font-size: clamp(1.3rem, 6vw, 1.7rem);
    }
    :deep(h2) {
      font-size: clamp(1.15rem, 5vw, 1.45rem);
    }
    :deep(h3) {
      font-size: clamp(1rem, 4.5vw, 1.2rem);
    }
    :deep(ul),
    :deep(ol) {
      padding-left: 1.25em;
    }
    :deep(pre) {
      padding: 0.75rem 0.9rem;
    }
    :deep(code) {
      font-size: 0.9em;
    }
    :deep(th),
    :deep(td) {
      padding: 0.45rem 0.6rem;
    }
    :deep(img),
    :deep(video),
    :deep(canvas),
    :deep(iframe) {
      margin: 0.75rem auto;
      border-radius: 10px;
    }
  }

  :deep(.github-markdown-body) {
    line-height: 1.75;
    font-size: clamp(0.95rem, 1.1vw, 1rem);
    word-wrap: break-word;
  }
  :deep(.github-markdown-body h1) {
    margin: 1.6rem 0 1rem;
    font-size: clamp(1.6rem, 4.5vw, 2rem);
    border-bottom: 1px solid rgba(255, 255, 255, 0.12);
    padding-bottom: 0.4rem;
  }
  :deep(.github-markdown-body h2) {
    margin: 1.3rem 0 0.75rem;
    font-size: clamp(1.3rem, 3.5vw, 1.6rem);
    border-bottom: 1px solid rgba(255, 255, 255, 0.12);
    padding-bottom: 0.3rem;
  }
  :deep(.github-markdown-body h3) {
    margin: 1.1rem 0 0.5rem;
    font-size: clamp(1.15rem, 2.8vw, 1.3rem);
  }
  :deep(.github-markdown-body h4),
  :deep(.github-markdown-body h5),
  :deep(.github-markdown-body h6) {
    margin: 0.75rem 0 0.5rem;
    font-size: 1.1rem;
  }
  :deep(.github-markdown-body p) {
    margin: 1rem 0;
  }
  :deep(.github-markdown-body a) {
    color: var(--primary);
    text-decoration: underline;
    overflow-wrap: anywhere;
  }
  :deep(.github-markdown-body a:hover) {
    text-decoration: none;
  }
  :deep(.github-markdown-body ul),
  :deep(.github-markdown-body ol) {
    margin: 1rem 0;
    padding-left: 2em;
  }
  :deep(.github-markdown-body li) {
    margin: 0.25rem 0;
  }
  :deep(.github-markdown-body blockquote) {
    margin: 1rem 0;
    padding: 0.5rem 1rem;
    color: rgba(255, 255, 255, 0.85);
    border-left: 4px solid var(--primary);
    background: rgba(255, 255, 255, 0.04);
    border-radius: 8px;
  }
  :deep(.github-markdown-body hr) {
    border: 0;
    border-top: 1px solid rgba(255, 255, 255, 0.12);
    margin: 1.5rem 0;
  }
  :deep(.github-markdown-body code) {
    color: var(--blue);
    background: rgba(255, 255, 255, 0.06);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 6px;
    padding: 0.15rem 0.35rem;
    font-weight: 600;
    font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
    font-size: 0.95em;
    overflow-wrap: anywhere;
  }
  :deep(.github-markdown-body pre) {
    background: rgba(255, 255, 255, 0.06);
    border: 1px solid rgba(255, 255, 255, 0.12);
    border-radius: 8px;
    padding: 1rem 1.25rem;
    overflow-x: auto;
    margin: 1rem 0;
  }
  :deep(.github-markdown-body pre code) {
    background: transparent;
    border: 0;
    padding: 0;
    color: var(--white);
    font-size: 0.95em;
  }
  :deep(.github-markdown-body .katex) {
    color: var(--white);
  }
  :deep(.github-markdown-body .katex-display) {
    overflow-x: auto;
  }
  :deep(.github-markdown-body table) {
    width: 100%;
    border-collapse: collapse;
    margin: 1rem 0;
    display: block;
    overflow-x: auto;
  }
  :deep(.github-markdown-body th),
  :deep(.github-markdown-body td) {
    border: 1px solid rgba(255, 255, 255, 0.12);
    padding: 0.55rem 0.75rem;
    vertical-align: top;
  }
  :deep(.github-markdown-body th) {
    background: rgba(255, 255, 255, 0.06);
    font-weight: 700;
  }
  :deep(.github-markdown-body tr:nth-child(2n)) {
    background: rgba(255, 255, 255, 0.03);
  }
  :deep(.github-markdown-body caption) {
    padding: 0.5rem 0;
    font-size: 0.95em;
    color: rgba(255, 255, 255, 0.8);
  }
  :deep(.github-markdown-body kbd) {
    background: rgba(255, 255, 255, 0.08);
    border: 1px solid rgba(255, 255, 255, 0.12);
    border-radius: 6px;
    padding: 0.1rem 0.3rem;
    font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
    font-size: 0.9em;
  }
  :deep(.github-markdown-body details) {
    margin: 1rem 0;
    padding: 0.5rem 0.75rem;
    border: 1px solid rgba(255, 255, 255, 0.12);
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.04);
  }
  :deep(.github-markdown-body summary) {
    cursor: pointer;
    font-weight: 600;
  }

  @media (max-width: 768px) {
    padding: 1rem;
    border-radius: 14px;
  }
  @media (max-width: 576px) {
    padding: 0.75rem;
    box-shadow: none;
    border-radius: 12px;
    :deep(.github-markdown-body h1) {
      font-size: clamp(1.4rem, 6vw, 1.8rem);
    }
    :deep(.github-markdown-body h2) {
      font-size: clamp(1.2rem, 5vw, 1.5rem);
    }
    :deep(.github-markdown-body h3) {
      font-size: clamp(1.05rem, 4.5vw, 1.2rem);
    }
    :deep(.github-markdown-body ul),
    :deep(.github-markdown-body ol) {
      padding-left: 1.25em;
    }
    :deep(.github-markdown-body pre) {
      padding: 0.75rem 0.9rem;
    }
    :deep(.github-markdown-body code) {
      font-size: 0.9em;
    }
    :deep(.github-markdown-body th),
    :deep(.github-markdown-body td) {
      padding: 0.45rem 0.6rem;
    }
    :deep(img),
    :deep(video),
    :deep(canvas),
    :deep(iframe) {
      margin: 0.75rem auto;
      border-radius: 10px;
    }
  }
}
</style>
