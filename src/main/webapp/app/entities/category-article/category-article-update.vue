<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="devalgasApp.categoryArticle.home.createOrEditLabel"
          data-cy="CategoryArticleCreateUpdateHeading"
          v-text="t$('devalgasApp.categoryArticle.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="categoryArticle.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="categoryArticle.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.categoryArticle.label')" for="category-article-label"></label>
            <input
              type="text"
              class="form-control"
              name="label"
              id="category-article-label"
              data-cy="label"
              :class="{ valid: !v$.label.$invalid, invalid: v$.label.$invalid }"
              v-model="v$.label.$model"
              required
            />
            <div v-if="v$.label.$anyDirty && v$.label.$invalid">
              <small class="form-text text-danger" v-for="error of v$.label.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.categoryArticle.code')" for="category-article-code"></label>
            <input
              type="text"
              class="form-control"
              name="code"
              id="category-article-code"
              data-cy="code"
              :class="{ valid: !v$.code.$invalid, invalid: v$.code.$invalid }"
              v-model="v$.code.$model"
              required
            />
            <div v-if="v$.code.$anyDirty && v$.code.$invalid">
              <small class="form-text text-danger" v-for="error of v$.code.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.categoryArticle.badge')" for="category-article-badge"></label>
            <div>
              <img
                :src="'data:' + categoryArticle.badgeContentType + ';base64,' + categoryArticle.badge"
                style="max-height: 100px"
                v-if="categoryArticle.badge"
                alt="categoryArticle"
              />
              <div v-if="categoryArticle.badge" class="form-text text-danger clearfix">
                <span class="pull-left">{{ categoryArticle.badgeContentType }}, {{ byteSize(categoryArticle.badge) }}</span>
                <button
                  type="button"
                  @click="clearInputImage('badge', 'badgeContentType', 'file_badge')"
                  class="btn btn-secondary btn-xs pull-right"
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                </button>
              </div>
              <label for="file_badge" v-text="t$('entity.action.addimage')" class="btn btn-primary pull-right"></label>
              <input
                type="file"
                ref="file_badge"
                id="file_badge"
                style="display: none"
                data-cy="badge"
                @change="setFileData($event, categoryArticle, 'badge', true)"
                accept="image/*"
              />
            </div>
            <input
              type="hidden"
              class="form-control"
              name="badge"
              id="category-article-badge"
              data-cy="badge"
              :class="{ valid: !v$.badge.$invalid, invalid: v$.badge.$invalid }"
              v-model="v$.badge.$model"
            />
            <input
              type="hidden"
              class="form-control"
              name="badgeContentType"
              id="category-article-badgeContentType"
              v-model="categoryArticle.badgeContentType"
            />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('devalgasApp.categoryArticle.descriptionFr')"
              for="category-article-descriptionFr"
            ></label>
            <input
              type="text"
              class="form-control"
              name="descriptionFr"
              id="category-article-descriptionFr"
              data-cy="descriptionFr"
              :class="{ valid: !v$.descriptionFr.$invalid, invalid: v$.descriptionFr.$invalid }"
              v-model="v$.descriptionFr.$model"
            />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('devalgasApp.categoryArticle.descriptionEn')"
              for="category-article-descriptionEn"
            ></label>
            <input
              type="text"
              class="form-control"
              name="descriptionEn"
              id="category-article-descriptionEn"
              data-cy="descriptionEn"
              :class="{ valid: !v$.descriptionEn.$invalid, invalid: v$.descriptionEn.$invalid }"
              v-model="v$.descriptionEn.$model"
            />
          </div>
          <div class="form-group">
            <label v-text="t$('devalgasApp.categoryArticle.article')" for="category-article-article"></label>
            <select
              class="form-control"
              id="category-article-articles"
              data-cy="article"
              multiple
              name="article"
              v-if="categoryArticle.articles !== undefined"
              v-model="categoryArticle.articles"
            >
              <option
                :value="getSelected(categoryArticle.articles, articleOption, 'id')"
                v-for="articleOption in articles"
                :key="articleOption.id"
              >
                {{ articleOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" @click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./category-article-update.component.ts"></script>
