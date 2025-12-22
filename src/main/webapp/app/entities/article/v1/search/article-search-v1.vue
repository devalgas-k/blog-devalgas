<template>
  <div class="about" id="about">
    <picture class="">
      <img :src="backgroundImageSrc" alt="background" />
    </picture>
    <figure>
      <figcaption>
        <div class="d-flex justify-content-center">
          <p-input-group style="height: 55px; width: 40vw; min-width: 250px !important">
            <p-auto-complete
              icon="pi pi-search"
              severity="contrast"
              type="search"
              :model-value="selectedArticle"
              @update:model-value="selectedArticle = $event"
              :suggestions="filteredArticles"
              :input-style="{ width: 35 + 'vw', background: 'rgba(0, 0, 0, 0.5)', border: '2px solid' }"
              :select-on-focus="true"
              @complete="search"
              option-label="label"
              option-group-label="label"
              option-group-children="items"
              :placeholder="t$('globalV1.about.placeholder')"
            >
              <template #header>
                <div></div>
              </template>
              <template #optiongroup="slotProps">
                <div class="row country-item">
                  <p-avatar
                    :image="'data:' + slotProps.item.badgeContentType + ';base64,' + slotProps.item.badge"
                    class="ml-3 mr-2"
                    shape="circle"
                  />
                  <div class="mt-1">{{ slotProps.item.label }}</div>
                </div>
              </template>
              <template #item="slotProps">
                <router-link
                  v-if="viewArticle"
                  :to="{ name: 'ArticleDetailsViewV1', params: { articleId: selectedArticle?.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <div @click="navigate">{{ slotProps.item.label }}</div>
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
  <!--TODO  <div class="row">
      <ul class="list-group offset-1 offset-lg-4 mt-lg-n5 mt-3 mb-5 list-group-horizontal">
        <li class="list-group-item">
          <a href="https://github.com/devalgas-k/" target="_blank" rel="noopener noreferrer" v-text="t$('home.link.twitter')"></a>
        </li>
        <li class="list-group-item">
          <a
            href="https://www.linkedin.com/in/devalgas-kamga/"
            target="_blank"
            rel="noopener noreferrer"
            v-text="t$('home.link.linkedin')"
          ></a>
        </li>
        <li class="list-group-item">
          <a href="https://x.com/devalgas1/" target="_blank" rel="noopener noreferrer" v-text="t$('home.link.github')"></a>
        </li>
      </ul>
    </div>-->
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
}

picture {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

picture img {
  width: 160%;
  height: 100%;
  object-fit: contain;
  border-radius: 30%;
}

picture::before {
  position: absolute;
  top: 0;
  left: 0;
  width: 25%;
  height: 25%;
  background-color: transparent;
  content: '';
  animation: a 20s ease infinite;
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

picture::after {
  position: absolute;
  top: 0;
  left: 0;
  width: 25%;
  height: 25%;
  background: url('/content/images/matrix.png') no-repeat center center;
  content: '';
  animation: b 10s ease infinite;
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

figure {
  width: 130%;
  max-width: 950px;
  border: 2px solid rgba(0, 0, 0, 0.4);
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: space-between;
  background-color: rgba(0, 0, 0, 0.3);
  z-index: 1;
  animation: c 10s linear infinite;
  position: relative;
}

@keyframes c {
  from {
    backdrop-filter: blur(2vmin) hue-rotate(0deg);
  }

  to {
    backdrop-filter: blur(2vmin) hue-rotate(360deg);
  }
}

figure::after {
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

h2 {
  font-family: 'Major Mono Display', monospace;
  line-height: 1.5;
  text-wrap: balance;
  color: white;
  margin-bottom: 20px;
}

figcaption {
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

  figure {
    max-width: 90%;
    position: relative;
    margin: 20px;
  }
}

@media (max-width: 600px) {
  h2 {
    font-size: 32px;
  }

  figcaption {
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
</style>
