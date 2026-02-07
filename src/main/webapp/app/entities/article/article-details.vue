<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="article">
        <h2 class="jh-entity-heading" data-cy="articleDetailsHeading">
          <span v-text="t$('devalgasApp.article.detail.title')"></span> {{ article.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('devalgasApp.article.labelEn')"></span>
          </dt>
          <dd>
            <span>{{ article.labelEn }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.article.labelFr')"></span>
          </dt>
          <dd>
            <span>{{ article.labelFr }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.article.descriptionFr')"></span>
          </dt>
          <dd>
            <span>{{ article.descriptionFr }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.article.descriptionEn')"></span>
          </dt>
          <dd>
            <span>{{ article.descriptionEn }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.article.markdownFr')"></span>
          </dt>
          <dd>
            <div v-if="article.markdownFr">
              <a @click="openFile(article.markdownFrContentType, article.markdownFr)" v-text="t$('entity.action.open')"></a>
              {{ article.markdownFrContentType }}, {{ byteSize(article.markdownFr) }}
            </div>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.article.markdownEn')"></span>
          </dt>
          <dd>
            <div v-if="article.markdownEn">
              <a @click="openFile(article.markdownEnContentType, article.markdownEn)" v-text="t$('entity.action.open')"></a>
              {{ article.markdownEnContentType }}, {{ byteSize(article.markdownEn) }}
            </div>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.article.status')"></span>
          </dt>
          <dd>
            <span v-text="t$('devalgasApp.Status.' + article.status)"></span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.article.date')"></span>
          </dt>
          <dd>
            <span v-if="article.date">{{ formatDateLong(article.date) }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.article.badge')"></span>
          </dt>
          <dd>
            <div v-if="article.badge">
              <a @click="openFile(article.badgeContentType, article.badge)">
                <img :src="'data:' + article.badgeContentType + ';base64,' + article.badge" style="max-width: 100%" alt="article" />
              </a>
              {{ article.badgeContentType }}, {{ byteSize(article.badge) }}
            </div>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.article.banner')"></span>
          </dt>
          <dd>
            <div v-if="article.banner">
              <a @click="openFile(article.bannerContentType, article.banner)">
                <img :src="'data:' + article.bannerContentType + ';base64,' + article.banner" style="max-width: 100%" alt="article" />
              </a>
              {{ article.bannerContentType }}, {{ byteSize(article.banner) }}
            </div>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.article.views')"></span>
          </dt>
          <dd>
            <span>{{ article.views }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.article.stars')"></span>
          </dt>
          <dd>
            <span>{{ article.stars }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.article.display')"></span>
          </dt>
          <dd>
            <span>{{ article.display }}</span>
          </dd>
          <dt>
            <span v-text="t$('devalgasApp.article.categoryArticle')"></span>
          </dt>
          <dd>
            <span v-for="(categoryArticle, i) in article.categoryArticles" :key="categoryArticle.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'CategoryArticleView', params: { categoryArticleId: categoryArticle.id } }">{{
                categoryArticle.label
              }}</router-link>
            </span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="article.id" :to="{ name: 'ArticleEdit', params: { articleId: article.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./article-details.component.ts"></script>
