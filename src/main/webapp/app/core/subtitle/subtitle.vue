<template>
  <div class="subtitle">
    <div class="content">
      <div class="content__container">
        <p class="content__container__text" v-text="subtitle"></p>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./subtitle.component.ts"></script>

<style lang="scss" scoped>
.content {
  left: 50%;
  transform: translate(-50%, -50%);
  overflow: hidden;

  font-family: 'Lato', sans-serif;
  font-size: 20px;
  line-height: 40px;
  color: #ecf0f1;

  &__container {
    display: flex; // Utilise Flexbox pour aligner les éléments
    align-items: center; // Centre verticalement les éléments
    justify-content: center; // Centre horizontalement les éléments
    font-weight: 600;
    overflow: hidden;
    height: 40px;
    padding: 0 30px;
    position: relative;
    z-index: 10;

    &:before {
      content: '[';
      left: 0;
    }

    &:after {
      content: ']';
      position: absolute;
      right: 0;
    }

    &:after,
    &:before {
      position: absolute;
      top: 0;

      color: var(--primary);
      font-size: 30px;
      line-height: 40px;

      -webkit-animation-name: opacity;
      -webkit-animation-duration: 2s;
      -webkit-animation-iteration-count: infinite;
      animation-name: opacity;
      animation-duration: 2s;
      animation-iteration-count: infinite;
    }

    &__text {
      display: inline;
      float: left;
      margin: 0;
      white-space: nowrap;
    }
  }
}

@-webkit-keyframes opacity {
  0%,
  100% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
}
@-o-keyframes opacity {
  0%,
  100% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
}
@-moz-keyframes opacity {
  0%,
  100% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
}
@keyframes opacity {
  0%,
  100% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
}
</style>
