<template>
  <div class="article-info" :aria-busy="!label">
    <Transition name="fade">
      <div v-if="label">
        <h5 class="article-info__title">{{ label }}</h5>
        <h6 v-if="description" class="article-info__description">{{ description }}</h6>
        <div class="article-info__body">
          <div class="article-info__header">
            <dl class="article-info__meta">
              <div class="article-info__row">
                <dt class="article-info__value" v-if="publishedDate">
                  <font-awesome-icon icon="calendar-days" color="var(--primary)" class="mt-n1" /> {{ publishedDate }}
                </dt>
              </div>
              <div class="article-info__row d-none">
                <dt class="article-info__value">3 minute</dt>
                <dd class="article-info__label" v-text="t$('devalgasApp.articleV1.info.readingTime')"></dd>
              </div>
            </dl>
          </div>

          <div class="article-info__stats d-none">
            <div class="article-info__row">
              <dt class="article-info__value" v-if="publishedDate">{{ article.views }}</dt>
              <dd class="article-info__label" v-if="publishedDate"><font-awesome-icon icon="eye"></font-awesome-icon></dd>
            </div>
            <div class="article-info__row">
              <dt class="article-info__value" v-if="publishedDate">{{ article.stars }}</dt>
              <dd class="article-info__label" v-if="publishedDate"><font-awesome-icon icon="thumbs-up"></font-awesome-icon></dd>
            </div>
          </div>

          <div class="article-info__share"></div>

          <div class="article-info__tags flex-wrap">
            <!--        TODO Defini chaque categorie-->
            <a
              v-for="cat in article && article.categoryArticles ? article.categoryArticles : []"
              :key="cat.id ?? cat.label"
              href="#"
              v-text="cat.label"
            ></a>
          </div>
        </div>
      </div>
    </Transition>
    <template v-if="!label">
      <p-skeleton width="70%" height="28px" class="mb-2" />
      <p-skeleton width="100%" height="16px" class="mb-2" />
      <p-skeleton width="60%" height="16px" />
    </template>
  </div>
</template>

<script lang="ts" src="./article-info-v1.component.ts"></script>

<style lang="scss" scoped>
.article-info {
  display: flex;
  position: relative;
  flex-direction: column;
  align-items: stretch;
  min-width: 250px;
  padding: 1rem;
  border-radius: 16px;
  /*box-shadow: -1rem 0 3rem #00000067;*/
  box-shadow:
    0 0.1rem 0.2rem rgb(0 0 0 / 20%),
    0 0.1rem 0.5rem rgb(0 0 0 / 30%),
    0 0.2rem 1.5rem rgb(0 0 0 / 40%);
  transition: 0.2s;
  font-family: 'Inter', sans-serif;

  &:hover {
    transform: translateY(-0.4rem);
  }

  &__title {
    display: -webkit-box;
    width: 100%;
    max-width: 100%;
    margin: 0;
    font-size: clamp(1rem, 2.5vw, 2rem);
    line-height: 1.25rem;
    color: var(--white);
    text-transform: capitalize !important;
    font-weight: bolder !important;
    white-space: normal;
    word-break: break-word;
    overflow-wrap: anywhere;
    hyphens: auto;
    overflow: hidden;
    text-overflow: ellipsis;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }
  &__description {
    display: -webkit-box;
    width: 100%;
    max-width: 100%;
    margin: 0.25rem 0 0.5rem;
    font-size: clamp(0.9rem, 2vw, 1.1rem);
    line-height: 1.2rem;
    color: var(--light);
    white-space: normal;
    word-break: break-word;
    overflow-wrap: anywhere;
    hyphens: auto;
    overflow: hidden;
    text-overflow: ellipsis;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }

  &__body {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    gap: 12px;
  }

  &__header {
    margin-top: 10px;
  }

  &__meta {
    margin-top: 0;
    display: flex;
    gap: 1rem;
  }

  &__row {
    font-size: 0.75rem;
    display: flex;
    flex-direction: column-reverse;
  }

  &__value {
    line-height: 1.25rem;
    font-weight: 600;
    color: var(--light);
  }

  &__label {
    line-height: 1rem;
    color: var(--white);
  }

  &__stats {
    display: flex;
    gap: 16px;
  }

  &__share {
  }

  &__tags {
    display: flex;
    align-items: center;
    gap: 8px;
    margin: 0;
    padding: 0;
    line-height: 1.5;
    a {
      font-style: normal;
      font-weight: 700;
      color: var(--light);
      text-transform: uppercase;
      font-size: 0.66rem;
      border: 2px solid var(--primary);
      border-radius: 2rem;
      padding: 0.2rem 0.85rem 0.25rem;
      position: relative;

      &:hover {
        background: linear-gradient(90deg, var(--light), var(--warning));
        text-shadow: none;
        -webkit-text-fill-color: transparent;
        -webkit-background-clip: text;
        -webkit-box-decoration-break: clone;
        box-decoration-break: clone;
        background-clip: text;
        border-color: white;
      }
    }
  }

  @media (max-width: 765px) {
    &__title {
      font-size: 1rem;
    }

    &__body {
      flex-direction: column;
      align-items: flex-start;
      justify-content: flex-start;
      width: 100%;
    }

    &__header,
    &__stats,
    &__tags {
      width: 100%;
    }

    &__tags {
      flex-wrap: wrap;
    }
  }
}

button {
  position: relative;
  height: 30px;
  padding: 0 20px;
  border: 2px solid var(--white);
  background: var(--body-bg);
  user-select: none;
  white-space: nowrap;
  transition: all 0.05s linear;
  font-family: inherit;
}

button:before,
button:after {
  content: '';
  position: absolute;
  background: var(--body-bg);
  transition: all 0.2s linear;
}

button:before {
  width: calc(100% + 6px);
  height: calc(100% - 16px);
  top: 8px;
  left: -3px;
}

button:after {
  width: calc(100% - 16px);
  height: calc(100% + 6px);
  top: -3px;
  left: 8px;
}

button:hover {
  cursor: crosshair;
}

button:active {
  transform: scale(0.95);
}

button:hover:before {
  height: calc(100% - 32px);
  top: 16px;
}

button:hover:after {
  width: calc(100% - 32px);
  left: 16px;
}

button span {
  font-size: 15px;
  z-index: 3;
  position: relative;
  font-weight: 600;
}
</style>
