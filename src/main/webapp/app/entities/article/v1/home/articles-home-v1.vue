<template>
  <!-- TODO filter https://freefrontend.com/vue-filter-sort/ https://codepen.io/udyux/pen/EwwPgr https://codepen.io/kristen17/pen/PoemNzM https://freefrontend.com/css-filter-sort/-->
  <div id="writing" class="writing" :aria-busy="isFetching">
    <Transition name="fade">
      <div v-if="isFetching" class="table-responsive">
        <table class="table" aria-describedby="articles">
          <tbody>
            <tr v-for="i in 5" :key="i">
              <td>
                <div class="writing__link">
                  <article-info :article="undefined"></article-info>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-else>
        <div class="alert alert-warning" v-if="articles && articles.length === 0">
          <span v-text="t$('devalgasApp.article.home.notFound')"></span>
        </div>
        <div class="table-responsive" v-if="articles && articles.length > 0">
          <table class="table" aria-describedby="articles">
            <tbody>
              <tr v-for="article in articles" :key="article.id" data-cy="entityTable">
                <td>
                  <router-link
                    :to="{ name: 'ArticleDetailsViewV1Slug', params: { articleId: article?.id, slug: slugForArticle(article) } }"
                    custom
                    v-slot="{ navigate }"
                  >
                    <div @click="navigate" class="clickable writing__link">
                      <article-info :article="article"></article-info>
                    </div>
                  </router-link>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </Transition>

    <div v-show="articles && articles.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :items-per-page="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>

  <div class="skills-section writing__skills-section d-none">
    <div class="skills-container writing__skills-container">
      <div class="tag-list writing__tag-list">
        <div class="loop-slider writing__loop-slider" style="--duration: 15951ms; --direction: normal">
          <div class="inner writing__loop-inner">
            <div class="tag writing__tag"><span>#</span> Java</div>
            <div class="tag writing__tag"><span>#</span> Spring Boot</div>
            <div class="tag writing__tag"><span>#</span> Spring Security</div>
            <div class="tag writing__tag"><span>#</span> JPA</div>
            <div class="tag writing__tag"><span>#</span> Hibernate</div>
            <!-- duplicated content -->
            <div class="tag writing__tag"><span>#</span> Docker</div>
            <div class="tag writing__tag"><span>#</span> Kubernetes</div>
            <div class="tag writing__tag"><span>#</span> Jenkins</div>
            <div class="tag writing__tag"><span>#</span> Maven</div>
            <div class="tag writing__tag"><span>#</span> Git</div>
          </div>
        </div>
        <div class="loop-slider writing__loop-slider" style="--duration: 19260ms; --direction: reverse">
          <div class="inner writing__loop-inner">
            <div class="tag writing__tag"><span>#</span> Docker</div>
            <div class="tag writing__tag"><span>#</span> Kubernetes</div>
            <div class="tag writing__tag"><span>#</span> Jenkins</div>
            <div class="tag writing__tag"><span>#</span> Maven</div>
            <div class="tag writing__tag"><span>#</span> Git</div>
            <!-- duplicated content -->
            <div class="tag writing__tag"><span>#</span> Java</div>
            <div class="tag writing__tag"><span>#</span> Spring Boot</div>
            <div class="tag writing__tag"><span>#</span> Spring Security</div>
            <div class="tag writing__tag"><span>#</span> JPA</div>
            <div class="tag writing__tag"><span>#</span> Hibernate</div>
          </div>
        </div>
        <div class="d-none loop-slider writing__loop-slider" style="--duration: 10449ms; --direction: normal">
          <div class="inner writing__loop-inner">
            <div class="tag writing__tag"><span>#</span> HTML5</div>
            <div class="tag writing__tag"><span>#</span> CSS3</div>
            <div class="tag writing__tag"><span>#</span> SASS</div>
            <div class="tag writing__tag"><span>#</span> Bootstrap</div>
            <div class="tag writing__tag"><span>#</span> Responsive Design</div>
            <!-- duplicated content -->
            <div class="tag writing__tag"><span>#</span> HTML5</div>
            <div class="tag writing__tag"><span>#</span> CSS3</div>
            <div class="tag writing__tag"><span>#</span> SASS</div>
            <div class="tag writing__tag"><span>#</span> Bootstrap</div>
            <div class="tag writing__tag"><span>#</span> Responsive Design</div>
          </div>
        </div>
        <div class="fade writing__fade"></div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./articles-home.component-v1.ts"></script>
<style lang="scss" scoped>
.writing {
  & .table td {
    width: 100vw !important;
    border-top: 0 !important;
  }

  &__skills-section {
    font-family: 'Montserrat', sans-serif;
  }

  &__skills-container {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
  }

  &__tag-list {
    width: 100vw;
    display: flex;
    flex-shrink: 0;
    flex-direction: column;
    gap: 1rem 0;
    position: relative;
    overflow: hidden;
  }

  &__loop-slider {
    & > .writing__loop-inner {
      display: flex;
      width: fit-content;
      animation-name: loop;
      animation-timing-function: linear;
      animation-iteration-count: infinite;
      animation-direction: var(--direction);
      animation-duration: var(--duration);
    }
  }

  &__tag {
    display: flex;
    white-space: nowrap;
    align-items: center;
    gap: 0 0.2rem;
    color: #e2e8f0;
    font-size: 0.9rem;
    background-color: var(--dark);
    border-bottom-color: #20c997;
    border-radius: 0.4rem;
    padding: 0.7rem 1rem;
    margin-right: 1rem; // Must used margin-right instead of gap for the loop to be smooth
    box-shadow:
      0 0.1rem 0.2rem rgb(0 0 0 / 20%),
      0 0.1rem 0.5rem rgb(0 0 0 / 30%),
      0 0.2rem 1.5rem rgb(0 0 0 / 40%);

    span {
      font-size: 1.2rem;
      color: #64748b;
    }
  }

  &__fade {
    pointer-events: none;
    background: linear-gradient(90deg, #1e293b, transparent 30%, transparent 70%, #1e293b);
    position: absolute;
    inset: 0;
  }

  @keyframes loop {
    0% {
      transform: translateX(0);
    }
    100% {
      transform: translateX(-50%);
    }
  }

  &__link,
  .clickable {
    cursor: pointer;
  }
}

.p-autocomplete-input .p-inputtext .p-component .p-variant-filled {
  border: 1px solid darken(#b58900, 10%) !important;
}
</style>
