<template>
  <div class="about" id="about">
    <picture class="about__picture" :style="{ '--matrix-url': `url('${imageBasePath}/matrix.png')` }">
      <img
        v-if="showBackgroundImage"
        class="about__image"
        :src="backgroundImageSrc"
        alt="background"
        width="960"
        height="960"
        decoding="async"
        fetchpriority="high"
      />
    </picture>
    <figure class="about__figure">
      <figcaption class="about__caption">
        <div class="d-flex justify-content-center">
          <p-input-group style="height: 55px; width: 40vw; min-width: 280px !important">
            <p-auto-complete
              icon="pi pi-search"
              severity="contrast"
              type="search"
              :model-value="selectedArticle"
              @update:model-value="selectedArticle = $event"
              :suggestions="filteredArticles"
              :input-style="{ width: 40 + 'vw', background: 'rgba(0, 0, 0, 0.5)' }"
              :select-on-focus="true"
              @complete="search"
              option-label="label"
              option-group-label="label"
              option-group-children="items"
              :placeholder="t$('globalV1.about.placeholder')"
              :append-to="'body'"
              :pt="{
                panel: {
                  style: {
                    width: 40 + 'vw',
                    maxHeight: panelMaxHeight,
                    overflowY: 'auto',
                    zIndex: 9999,
                    backgroundColor: 'var(--dark)',
                    border: '1px solid var(--dark)',
                    color: 'var(--white)',
                  },
                },
                items: { style: { backgroundColor: 'var(--dark)' } },
                itemGroup: { style: { backgroundColor: 'var(--dark)', color: 'var(--white)' } },
                item: {
                  style: {
                    backgroundColor: 'var(--dark)',
                    color: 'var(--white)',
                    margin: '0',
                    padding: '0.5rem 1rem',
                  },
                },
              }"
              @blur="onAutoBlur"
              @focus="onAutoFocus"
              @hide="onPanelHide"
              @item-select="onItemSelect"
            >
              <template #header>
                <div></div>
              </template>
              <template #optiongroup="slotProps">
                <div class="d-none">
                  <p-avatar
                    :image="'data:' + slotProps.item.badgeContentType + ';base64,' + slotProps.item.badge"
                    class="ml-3 mr-2"
                    shape="circle"
                  />
                  <div class="mt-1">
                    <span>{{ slotProps.item.label }}</span>
                  </div>
                </div>
              </template>
              <template #item="slotProps">
                <router-link
                  :to="{
                    name: 'ArticleDetailsViewV1Slug',
                    params: { articleId: slotProps.item?.id, slug: slugForArticle(slotProps.item) },
                  }"
                  custom
                  v-slot="{ navigate }"
                >
                  <div @click="navigate">
                    <article-info-v1 :article="slotProps.item" class="w-100" />
                  </div>
                </router-link>
              </template>
              <template #empty>
                <div class="text-center p-3">
                  {{ t$('globalV1.about.no-results-found') }}
                </div>
              </template>
              <template #footer>
                <div></div>
              </template>
            </p-auto-complete>
          </p-input-group>
        </div>
      </figcaption>
    </figure>
  </div>
</template>

<script lang="ts" src="./article-search-v1.component.ts"></script>

<style lang="scss" scoped>
.ml-g {
  margin-left: 4.3rem !important;
}

.about {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  height: 65vh;

  &__picture {
    position: absolute;
    width: 100%;
    height: 100%;
    overflow: hidden;
    z-index: 0;

    &::before {
      position: absolute;
      top: 0;
      left: 0;
      width: 25%;
      height: 25%;
      background-color: transparent;
      content: '';
      animation: a 20s ease infinite;
    }

    &::after {
      position: absolute;
      top: 0;
      left: 0;
      width: 25%;
      height: 25%;
      background-image: var(--matrix-url, url('/content/images/matrix.png'));
      background-repeat: no-repeat;
      background-position: center center;
      content: '';
      animation: b 10s ease infinite;
    }
  }

  &__image {
    width: 160%;
    height: 100%;
    object-fit: contain;
    border-radius: 30%;
    aspect-ratio: 1 / 1;
  }

  :deep(.p-autocomplete) {
    & .p-autocomplete-input {
      &:focus,
      &:focus-visible {
        background-color: var(--write);
        border: 1px solid var(--write);
        color: var(--white);
      }
    }
  }

  :deep(.p-autocomplete-panel) {
    background-color: var(--dark);
    border: 1px solid var(--dark);
    min-width: 400px;
    color: var(--white);

    & .p-autocomplete-items {
      background-color: var(--dark);
      & .p-autocomplete-item-group {
        background-color: var(--dark);
        color: var(--white);
      }
      & .p-autocomplete-item {
        background-color: var(--dark);
        color: var(--white);
        margin: 0;
        padding: 0.5rem 1rem;
        &:hover,
        &.p-highlight {
          background-color: var(--dark);
          color: var(--white);
        }
      }
    }
  }
}

@keyframes a {
  0%,
  100% {
    width: 25%;
    height: 25%;
    left: 0;
    top: 0;
    backdrop-filter: blur(2vmin) hue-rotate(0deg);
  }

  12.5% {
    width: 100%;
    left: unset;
    backdrop-filter: blur(0vmin) hue-rotate(150deg);
  }

  25% {
    width: 25%;
    height: 25%;
    backdrop-filter: blur(2vmin) hue-rotate(300deg);
  }

  37.5% {
    top: unset;
    height: 100%;
    backdrop-filter: blur(0vmin) hue-rotate(150deg);
  }

  50% {
    width: 25%;
    height: 25%;
    backdrop-filter: blur(2vmin) hue-rotate(0deg);
  }

  62.5% {
    width: 100%;
    height: 100%;
    backdrop-filter: blur(0vmin) hue-rotate(150deg);
  }

  75% {
    width: 25%;
    height: 25%;
    backdrop-filter: blur(2vmin) hue-rotate(300deg);
  }

  87.5% {
    height: 100%;
    backdrop-filter: blur(0vmin) hue-rotate(150deg);
  }
}

@keyframes b {
  0%,
  100% {
    width: 25%;
    height: 25%;
    left: 0;
    top: 0;
    backdrop-filter: blur(2vmin) hue-rotate(0deg);
  }

  12.5% {
    height: 100%;
    top: unset;
    backdrop-filter: blur(0vmin) hue-rotate(150deg);
  }

  25% {
    width: 25%;
    height: 25%;
    backdrop-filter: blur(2vmin) hue-rotate(300deg);
  }

  37.5% {
    width: 100%;
    backdrop-filter: blur(0vmin) hue-rotate(150deg);
  }

  50% {
    width: 25%;
    height: 25%;
    backdrop-filter: blur(2vmin) hue-rotate(0deg);
  }

  62.5% {
    height: 100%;
    backdrop-filter: blur(0vmin) hue-rotate(150deg);
  }

  75% {
    width: 25%;
    height: 25%;
    backdrop-filter: blur(2vmin) hue-rotate(300deg);
  }

  87.5% {
    width: 100%;
    left: 0;
    backdrop-filter: blur(0vmin) hue-rotate(150deg);
  }
}

.about {
  &__figure {
    width: 130%;
    max-width: 950px;
    min-width: 380px;
    border: 2px solid rgba(0, 0, 0, 0.4);
    padding: 20px 10px;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    justify-content: space-between;
    background-color: rgba(0, 0, 0, 0.3);
    z-index: 1;
    animation: c 10s linear infinite;
    position: relative;
  }
}

@keyframes c {
  from {
    backdrop-filter: blur(2vmin) hue-rotate(0deg);
  }

  to {
    backdrop-filter: blur(2vmin) hue-rotate(360deg);
  }
}

.about__figure::after {
  position: absolute;
  bottom: -10px;
  right: -10px;
  width: 60px;
  height: 60px;
  background-image: linear-gradient(320deg, #ff20fb, #f424ff, #d42dff, #b139ff, #9742ff, #8d46ff);
  content: '';
  z-index: -1;
  animation: d 5s linear infinite;
}

@keyframes d {
  from {
    filter: hue-rotate(0deg) brightness(1.5);
    transform: rotateZ(0deg);
    border-radius: 0;
  }

  50% {
    border-radius: 49%;
    filter: hue-rotate(180deg) brightness(1.5);
  }

  to {
    filter: hue-rotate(360deg) brightness(1.5);
    transform: rotateZ(360deg);
    border-radius: 0;
  }
}

.about h2 {
  font-family: 'Major Mono Display', monospace;
  line-height: 1.5;
  text-wrap: balance;
  color: white;
  margin-bottom: 20px;
}

.about__caption {
  font-family: 'Figtree', sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 2;
  text-wrap: pretty;
  padding-left: 5px;
  border-left: 1px solid var(--dark);
  border-right: 1px solid var(--dark);
  color: #bbb;
  width: 100%;
}

@media (max-width: 780px) {
  .about {
    flex-direction: column;
    height: auto;
  }

  .about__figure {
    max-width: 90%;
    position: relative;
    margin: 20px;
  }
}

@media (max-width: 600px) {
  .about h2 {
    font-size: 32px;
  }

  .about__caption {
    font-size: 16px;
  }
}

.w-120 {
  width: 120%;
}

.list-group-horizontal > li {
  margin-top: 5px;
  padding-top: 0.5rem;
  padding-bottom: 0.5rem;
  box-shadow: 2px 2px 0 var(--primary);
}
.category-labels {
  color: #fff !important;
  border: 1px solid #fff;
  padding: 0 4px;
  border-radius: 4px;
  display: inline-block;
  line-height: 1.4;
}
</style>
