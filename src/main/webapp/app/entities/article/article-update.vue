<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="devalgasApp.article.home.createOrEditLabel"
          data-cy="ArticleCreateUpdateHeading"
          v-text="t$('devalgasApp.article.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="article.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="article.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.article.labelEn')" for="article-labelEn"></label>
            <input
              type="text"
              class="form-control"
              name="labelEn"
              id="article-labelEn"
              data-cy="labelEn"
              :class="{ valid: !v$.labelEn.$invalid, invalid: v$.labelEn.$invalid }"
              v-model="v$.labelEn.$model"
              required
            />
            <div v-if="v$.labelEn.$anyDirty && v$.labelEn.$invalid">
              <small class="form-text text-danger" v-for="error of v$.labelEn.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.article.labelFr')" for="article-labelFr"></label>
            <input
              type="text"
              class="form-control"
              name="labelFr"
              id="article-labelFr"
              data-cy="labelFr"
              :class="{ valid: !v$.labelFr.$invalid, invalid: v$.labelFr.$invalid }"
              v-model="v$.labelFr.$model"
              required
            />
            <div v-if="v$.labelFr.$anyDirty && v$.labelFr.$invalid">
              <small class="form-text text-danger" v-for="error of v$.labelFr.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.article.descriptionFr')" for="article-descriptionFr"></label>
            <input
              type="text"
              class="form-control"
              name="descriptionFr"
              id="article-descriptionFr"
              data-cy="descriptionFr"
              :class="{ valid: !v$.descriptionFr.$invalid, invalid: v$.descriptionFr.$invalid }"
              v-model="v$.descriptionFr.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.article.descriptionEn')" for="article-descriptionEn"></label>
            <input
              type="text"
              class="form-control"
              name="descriptionEn"
              id="article-descriptionEn"
              data-cy="descriptionEn"
              :class="{ valid: !v$.descriptionEn.$invalid, invalid: v$.descriptionEn.$invalid }"
              v-model="v$.descriptionEn.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.article.markdownFr')" for="article-markdownFr"></label>
            <div>
              <div v-if="article.markdownFr" class="form-text text-danger clearfix">
                <a
                  class="pull-left"
                  @click="openFile(article.markdownFrContentType, article.markdownFr)"
                  v-text="t$('entity.action.open')"
                ></a
                ><br />
                <span class="pull-left">{{ article.markdownFrContentType }}, {{ byteSize(article.markdownFr) }}</span>
                <button
                  type="button"
                  @click="
                    article.markdownFr = null;
                    article.markdownFrContentType = null;
                  "
                  class="btn btn-secondary btn-xs pull-right"
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                </button>
              </div>
              <label for="file_markdownFr" v-text="t$('entity.action.addblob')" class="btn btn-primary pull-right"></label>
              <input
                type="file"
                ref="file_markdownFr"
                id="file_markdownFr"
                style="display: none"
                data-cy="markdownFr"
                @change="setFileData($event, article, 'markdownFr', false)"
              />
            </div>
            <input
              type="hidden"
              class="form-control"
              name="markdownFr"
              id="article-markdownFr"
              data-cy="markdownFr"
              :class="{ valid: !v$.markdownFr.$invalid, invalid: v$.markdownFr.$invalid }"
              v-model="v$.markdownFr.$model"
            />
            <input
              type="hidden"
              class="form-control"
              name="markdownFrContentType"
              id="article-markdownFrContentType"
              v-model="article.markdownFrContentType"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.article.markdownEn')" for="article-markdownEn"></label>
            <div>
              <div v-if="article.markdownEn" class="form-text text-danger clearfix">
                <a
                  class="pull-left"
                  @click="openFile(article.markdownEnContentType, article.markdownEn)"
                  v-text="t$('entity.action.open')"
                ></a
                ><br />
                <span class="pull-left">{{ article.markdownEnContentType }}, {{ byteSize(article.markdownEn) }}</span>
                <button
                  type="button"
                  @click="
                    article.markdownEn = null;
                    article.markdownEnContentType = null;
                  "
                  class="btn btn-secondary btn-xs pull-right"
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                </button>
              </div>
              <label for="file_markdownEn" v-text="t$('entity.action.addblob')" class="btn btn-primary pull-right"></label>
              <input
                type="file"
                ref="file_markdownEn"
                id="file_markdownEn"
                style="display: none"
                data-cy="markdownEn"
                @change="setFileData($event, article, 'markdownEn', false)"
              />
            </div>
            <input
              type="hidden"
              class="form-control"
              name="markdownEn"
              id="article-markdownEn"
              data-cy="markdownEn"
              :class="{ valid: !v$.markdownEn.$invalid, invalid: v$.markdownEn.$invalid }"
              v-model="v$.markdownEn.$model"
            />
            <input
              type="hidden"
              class="form-control"
              name="markdownEnContentType"
              id="article-markdownEnContentType"
              v-model="article.markdownEnContentType"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.article.status')" for="article-status"></label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="article-status"
              data-cy="status"
              required
            >
              <option v-for="status in statusValues" :key="status" :value="status" :label="t$('devalgasApp.Status.' + status)">
                {{ status }}
              </option>
            </select>
            <div v-if="v$.status.$anyDirty && v$.status.$invalid">
              <small class="form-text text-danger" v-for="error of v$.status.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.article.date')" for="article-date"></label>
            <div class="d-flex">
              <input
                id="article-date"
                data-cy="date"
                type="datetime-local"
                class="form-control"
                name="date"
                :class="{ valid: !v$.date.$invalid, invalid: v$.date.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.date.$model)"
                @change="updateZonedDateTimeField('date', $event)"
              />
            </div>
            <div v-if="v$.date.$anyDirty && v$.date.$invalid">
              <small class="form-text text-danger" v-for="error of v$.date.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.article.badge')" for="article-badge"></label>
            <div>
              <img
                :src="'data:' + article.badgeContentType + ';base64,' + article.badge"
                style="max-height: 100px"
                v-if="article.badge"
                alt="article"
              />
              <div v-if="article.badge" class="form-text text-danger clearfix">
                <span class="pull-left">{{ article.badgeContentType }}, {{ byteSize(article.badge) }}</span>
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
                @change="setFileData($event, article, 'badge', true)"
                accept="image/*"
              />
            </div>
            <input
              type="hidden"
              class="form-control"
              name="badge"
              id="article-badge"
              data-cy="badge"
              :class="{ valid: !v$.badge.$invalid, invalid: v$.badge.$invalid }"
              v-model="v$.badge.$model"
            />
            <input
              type="hidden"
              class="form-control"
              name="badgeContentType"
              id="article-badgeContentType"
              v-model="article.badgeContentType"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.article.banner')" for="article-banner"></label>
            <div>
              <img
                :src="'data:' + article.bannerContentType + ';base64,' + article.banner"
                style="max-height: 100px"
                v-if="article.banner"
                alt="article"
              />
              <div v-if="article.banner" class="form-text text-danger clearfix">
                <span class="pull-left">{{ article.bannerContentType }}, {{ byteSize(article.banner) }}</span>
                <button
                  type="button"
                  @click="clearInputImage('banner', 'bannerContentType', 'file_banner')"
                  class="btn btn-secondary btn-xs pull-right"
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                </button>
              </div>
              <label for="file_banner" v-text="t$('entity.action.addimage')" class="btn btn-primary pull-right"></label>
              <input
                type="file"
                ref="file_banner"
                id="file_banner"
                style="display: none"
                data-cy="banner"
                @change="setFileData($event, article, 'banner', true)"
                accept="image/*"
              />
            </div>
            <input
              type="hidden"
              class="form-control"
              name="banner"
              id="article-banner"
              data-cy="banner"
              :class="{ valid: !v$.banner.$invalid, invalid: v$.banner.$invalid }"
              v-model="v$.banner.$model"
            />
            <input
              type="hidden"
              class="form-control"
              name="bannerContentType"
              id="article-bannerContentType"
              v-model="article.bannerContentType"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.article.views')" for="article-views"></label>
            <input
              type="number"
              class="form-control"
              name="views"
              id="article-views"
              data-cy="views"
              :class="{ valid: !v$.views.$invalid, invalid: v$.views.$invalid }"
              v-model.number="v$.views.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.article.stars')" for="article-stars"></label>
            <input
              type="number"
              class="form-control"
              name="stars"
              id="article-stars"
              data-cy="stars"
              :class="{ valid: !v$.stars.$invalid, invalid: v$.stars.$invalid }"
              v-model.number="v$.stars.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.article.display')" for="article-display"></label>
            <input
              type="checkbox"
              class="form-check"
              name="display"
              id="article-display"
              data-cy="display"
              :class="{ valid: !v$.display.$invalid, invalid: v$.display.$invalid }"
              v-model="v$.display.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('devalgasApp.article.newsletter')" for="article-newsletter"></label>
            <input
              type="checkbox"
              class="form-check"
              name="newsletter"
              id="article-newsletter"
              data-cy="newsletter"
              :class="{ valid: !v$.newsletter.$invalid, invalid: v$.newsletter.$invalid }"
              v-model="v$.newsletter.$model"
            />
          </div>
          <div class="form-group">
            <label v-text="t$('devalgasApp.article.categoryArticle')" for="article-categoryArticle"></label>
            <select
              class="form-control"
              id="article-categoryArticles"
              data-cy="categoryArticle"
              multiple
              name="categoryArticle"
              v-if="article.categoryArticles !== undefined"
              v-model="article.categoryArticles"
            >
              <option
                :value="getSelected(article.categoryArticles, categoryArticleOption, 'id')"
                v-for="categoryArticleOption in categoryArticles"
                :key="categoryArticleOption.id"
              >
                {{ categoryArticleOption.label }}
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
<script lang="ts" src="./article-update.component.ts"></script>
