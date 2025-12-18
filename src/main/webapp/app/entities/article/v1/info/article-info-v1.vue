<template>
  <div class="article-info">
    <h1 class="article-info__title">{{ label }}</h1>
    <div class="article-info__body">
      <div class="article-info__header">
        <dl class="article-info__meta">
          <div class="article-info__row">
            <dt class="article-info__value" v-if="publishedDate">{{ publishedDate }}</dt>
            <dd class="article-info__label" v-if="publishedDate" v-text="t$('devalgasApp.articleV1.info.published')"></dd>
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

      <div class="article-info__tags">
        <a
          v-for="cat in article && article.categoryArticles ? article.categoryArticles : []"
          :key="cat.id ?? cat.label"
          href="#"
          v-text="cat.label"
        ></a>
      </div>
    </div>
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
  box-shadow: -1rem 0 3rem #00000067;
  transition: 0.2s;
  font-family: 'Inter', sans-serif;

  &:hover {
    transform: translateY(-0.4rem);
  }

  &__title {
    display: block;
    width: 100%;
    margin: 0;
    font-size: 2rem;
    line-height: 1.2;
    color: var(--white);
  }

  &__body {
    display: flex;
    flex-direction: row;
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
    color: var(--white);
  }

  &__label {
    line-height: 1rem;
    color: var(--light);
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
      color: #fff;
      text-transform: uppercase;
      font-size: 0.66rem;
      border: 2px solid var(--light);
      border-radius: 2rem;
      padding: 0.2rem 0.85rem 0.25rem;
      position: relative;

      &:hover {
        background: linear-gradient(90deg, #fff, #e52e71);
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
</style>
