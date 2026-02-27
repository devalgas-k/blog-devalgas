<template>
  <!--  https://github.com/mazipan/vue-google-adsense-->
  <div class="adsense adsense__wrapper" :key="adKey">
    <ins
      ref="insEl"
      class="adsbygoogle"
      :style="insStyle"
      :data-ad-client="dataAdClient"
      :data-ad-slot="dataAdSlot || undefined"
      :data-ad-format="dataAdFormat"
      :data-ad-layout="adLayout || undefined"
      :data-ad-layout-key="adLayoutKeyAttr || undefined"
      :data-adtest="dataAdTest"
      :data-full-width-responsive="dataFullWidthResponsive"
    ></ins>
  </div>
</template>

<script lang="ts" src="./adsense.component.ts"></script>

<style lang="scss" scoped>
.adsense {
  &__wrapper {
    min-height: 120px;
  }
}
</style>
