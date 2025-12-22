<template>
  <!-- TODO filter https://freefrontend.com/vue-filter-sort/ https://codepen.io/udyux/pen/EwwPgr https://codepen.io/kristen17/pen/PoemNzM https://freefrontend.com/css-filter-sort/-->
  <div>
    <div class="alert alert-warning" v-if="!isFetching && articles && articles.length === 0">
      <span v-text="t$('devalgasApp.article.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="articles && articles.length > 0">
      <table class="table" aria-describedby="articles">
        <tbody>
          <tr v-for="article in articles" :key="article.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ArticleDetailsViewV1', params: { articleId: article?.id } }" custom v-slot="slot">
                <div class="clickable" @click="slot && slot.navigate ? slot.navigate() : null">
                  <article-info :article="article"></article-info>
                </div>
              </router-link>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-show="articles && articles.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :items-per-page="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>

  <div class="skills-section d-none">
    <div class="skills-container">
      <div class="tag-list">
        <div class="loop-slider" style="--duration: 15951ms; --direction: normal">
          <div class="inner">
            <div class="tag"><span>#</span> Java</div>
            <div class="tag"><span>#</span> Spring Boot</div>
            <div class="tag"><span>#</span> Spring Security</div>
            <div class="tag"><span>#</span> JPA</div>
            <div class="tag"><span>#</span> Hibernate</div>
            <!-- duplicated content -->
            <div class="tag"><span>#</span> Docker</div>
            <div class="tag"><span>#</span> Kubernetes</div>
            <div class="tag"><span>#</span> Jenkins</div>
            <div class="tag"><span>#</span> Maven</div>
            <div class="tag"><span>#</span> Git</div>
          </div>
        </div>
        <div class="loop-slider" style="--duration: 19260ms; --direction: reverse">
          <div class="inner">
            <div class="tag"><span>#</span> Docker</div>
            <div class="tag"><span>#</span> Kubernetes</div>
            <div class="tag"><span>#</span> Jenkins</div>
            <div class="tag"><span>#</span> Maven</div>
            <div class="tag"><span>#</span> Git</div>
            <!-- duplicated content -->
            <div class="tag"><span>#</span> Java</div>
            <div class="tag"><span>#</span> Spring Boot</div>
            <div class="tag"><span>#</span> Spring Security</div>
            <div class="tag"><span>#</span> JPA</div>
            <div class="tag"><span>#</span> Hibernate</div>
          </div>
        </div>
        <div class="d-none loop-slider" style="--duration: 10449ms; --direction: normal">
          <div class="inner">
            <div class="tag"><span>#</span> HTML5</div>
            <div class="tag"><span>#</span> CSS3</div>
            <div class="tag"><span>#</span> SASS</div>
            <div class="tag"><span>#</span> Bootstrap</div>
            <div class="tag"><span>#</span> Responsive Design</div>
            <!-- duplicated content -->
            <div class="tag"><span>#</span> HTML5</div>
            <div class="tag"><span>#</span> CSS3</div>
            <div class="tag"><span>#</span> SASS</div>
            <div class="tag"><span>#</span> Bootstrap</div>
            <div class="tag"><span>#</span> Responsive Design</div>
          </div>
        </div>
        <div class="fade"></div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./articles-home.component-v1.ts"></script>
<style lang="scss" scoped>
.table td {
  width: 100vw !important;
  border-top: 0 !important;
}

.skills-section {
  font-family: 'Montserrat', sans-serif;
}

.skills-container {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}

.skills-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 1rem;
  text-align: center;

  h1 {
    font-weight: 600;
    font-size: 2rem;
    margin-bottom: 0.5rem;

    @media (min-width: 768px) {
      font-size: 3rem;
    }
  }

  p {
    color: #94a3b8;
    margin-bottom: 0.5rem;
  }

  a {
    color: #7393c1;
  }
}

.tag-list {
  width: 100vw;
  display: flex;
  flex-shrink: 0;
  flex-direction: column;
  gap: 1rem 0;
  position: relative;
  overflow: hidden;
}

.loop-slider {
  .inner {
    display: flex;
    width: fit-content;
    animation-name: loop;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
    animation-direction: var(--direction);
    animation-duration: var(--duration);
  }
}

.tag {
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

.fade {
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

.p-autocomplete-input .p-inputtext .p-component .p-variant-filled {
  border: 1px solid darken(#b58900, 10%) !important;
}

.clickable {
  cursor: pointer;
}
</style>
