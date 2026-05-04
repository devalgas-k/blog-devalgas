<template>
  <div class="article-skeleton mt-5">
    <!-- Skeleton Info Bloc -->
    <div class="skeleton-info mb-5">
      <p-skeleton width="70%" height="2.5rem" class="mb-3" />
      <p-skeleton width="90%" height="1.2rem" class="mb-2" />
      <div class="skeleton-meta mt-4">
        <p-skeleton width="150px" height="1rem" />
      </div>
    </div>

    <!-- Skeleton Body (Splitter imitation) -->
    <div class="skeleton-body">
      <div class="skeleton-content-box">
        <p-skeleton width="100%" height="20px" class="mb-2" />
        <p-skeleton width="95%" height="20px" class="mb-2" />
        <p-skeleton width="100%" height="20px" class="mb-2" />
        <p-skeleton width="40%" height="20px" class="mb-5" />

        <p-skeleton width="100%" height="20px" class="mb-2" />
        <p-skeleton width="90%" height="20px" class="mb-2" />
        <p-skeleton width="95%" height="20px" class="mb-2" />
        <p-skeleton width="60%" height="20px" class="mb-5" />

        <p-skeleton width="100%" height="400px" />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import Skeleton from 'primevue/skeleton';

export default defineComponent({
  name: 'ArticleDetailsSkeletonV1',
  components: {
    'p-skeleton': Skeleton,
  },
});
</script>

<style lang="scss" scoped>
.article-skeleton {
  width: 100%;
}

.skeleton-info {
  padding: 1rem;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.05);
  box-shadow: 0 0.2rem 1.5rem rgb(0 0 0 / 40%);
  min-height: 140px;
}

.skeleton-content-box {
  width: 100%;
  margin: 0 auto;
  padding: 1.25rem;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.03);
  box-shadow: -1rem 0 3rem #00000067;
  min-height: 600px;
}

@media (max-width: 576px) {
  .skeleton-content-box {
    padding: 0.5rem;
    border-radius: 10px;
    box-shadow: none;
  }
}
</style>
