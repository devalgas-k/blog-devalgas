<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="categoryArticle">
        <h2 class="jh-entity-heading" data-cy="categoryArticleDetailsHeading">
          <span v-text="t$('devalgasApp.categoryArticle.detail.title')"></span> {{ categoryArticle.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('devalgasApp.categoryArticle.label')"></span>
          </dt>
          <dd>
            <span>{{ categoryArticle.label }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.categoryArticle.code')"></span>
          </dt>
          <dd>
            <span>{{ categoryArticle.code }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.categoryArticle.badge')"></span>
          </dt>
          <dd>
            <div v-if="categoryArticle.badge">
              <a @click="openFile(categoryArticle.badgeContentType, categoryArticle.badge)">
                <img
                  :src="'data:' + categoryArticle.badgeContentType + ';base64,' + categoryArticle.badge"
                  style="max-width: 100%"
                  alt="categoryArticle"
                />
              </a>
              {{ categoryArticle.badgeContentType }}, {{ byteSize(categoryArticle.badge) }}
            </div>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.categoryArticle.descriptionFr')"></span>
          </dt>
          <dd>
            <span>{{ categoryArticle.descriptionFr }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.categoryArticle.descriptionEn')"></span>
          </dt>
          <dd>
            <span>{{ categoryArticle.descriptionEn }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.categoryArticle.article')"></span>
          </dt>
          <dd>
            <span v-for="(article, i) in categoryArticle.articles" :key="article.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'ArticleView', params: { articleId: article.id } }">{{ article.id }}</router-link>
            </span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="categoryArticle.id"
          :to="{ name: 'CategoryArticleEdit', params: { categoryArticleId: categoryArticle.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./category-article-details.component.ts"></script>
