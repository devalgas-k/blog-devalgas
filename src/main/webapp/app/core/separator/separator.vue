<template>
  <div class="or-spacer">
    <div class="or-spacer__mask"></div>
    <span class="or-spacer__bubble">
      <i class="or-spacer__content">
        <template v-if="hasLabel">{{ t$(labelProp) }}</template>
        <template v-else><font-awesome-icon :icon="iconProp" /></template>
      </i>
    </span>
  </div>
</template>

<script lang="ts" src="./separator.component.ts"></script>

<style lang="scss" scoped>
@use 'sass:math';
.or-spacer {
  width: 100%;
  position: relative;

  &__mask {
    overflow: hidden;
    height: 20px;
    &:after {
      content: '';
      display: block;
      margin: -25px auto 0;
      width: 100%;
      height: 25px;
      border-radius: 125px / 12px;
      box-shadow: 0 0 8px var(--primary);
    }
  }
  &__bubble {
    $size: 50px;
    width: $size;
    height: $size;
    position: absolute;
    bottom: 100%;
    margin-bottom: math.div(-$size, 4);
    left: 50%;
    margin-left: math.div(-$size, 2);
    border-radius: 100%;
    box-shadow: 0 2px 4px var(--primary);
    background: var(--dark);
  }
  &__content {
    $offset: 4px;
    position: absolute;
    top: $offset;
    bottom: $offset;
    left: $offset;
    right: $offset;
    border-radius: 100%;
    border: 1px dashed #fff;

    text-align: center;
    line-height: 40px;
    font-style: normal;
    color: #fff;
  }
}
</style>
