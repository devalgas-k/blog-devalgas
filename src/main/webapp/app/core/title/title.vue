<template>
  <div class="container-title py-0">
    <div class="box container-title__box">
      <div class="title container-title__title">
        <span class="block container-title__title-block"></span>
        <h1 class="container-title__heading">[ Devalgas ]<span></span></h1>
      </div>

      <div class="role container-title__role">
        <div class="block container-title__role-block"></div>
        <p v-text="t$('globalV1.headers.subTitle')"></p>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./title.component.ts"></script>

<style lang="scss" scoped>
@import url('https://fonts.googleapis.com/css?family=Lato:300,400|Poppins:300,400,800&display=swap');

* {
  margin: 0;
  padding: 0;
}

.container-title {
  .box,
  &__box {
    width: 250px;
    position: relative;
    display: flex;
    justify-content: center;
    flex-direction: column;
  }

  .title,
  &__title {
    width: 75%;
    position: relative;
    display: flex;
    align-items: center;
    height: 50px;
    margin-left: 15px;

    .block,
    &-block {
      width: 0%;
      height: inherit;
      background: linear-gradient(to left, var(--primary), var(--dark));
      position: absolute;
      animation: blockSweep 2s cubic-bezier(0.74, 0.06, 0.4, 0.92) forwards;
      display: flex;
    }

    h1,
    .container-title__heading {
      font-family: 'Poppins';
      color: #fff;
      font-size: 29px;
      animation: mainFadeIn 2s forwards;
      animation-delay: 1.6s;
      opacity: 0;
      display: flex;
      align-items: baseline;
      position: relative;

      span {
        width: 0px;
        height: 0px;
        border-radius: 50%;
        background: var(--dark);
        animation: popIn 0.8s cubic-bezier(0.74, 0.06, 0.4, 0.92) forwards;
        animation-delay: 2s;
        margin-left: 5px;
        margin-top: -10px;
        position: absolute;
        bottom: 13px;
        right: -12px;
      }
    }
  }

  .role,
  &__role {
    width: 100%;
    position: relative;
    display: flex;
    align-items: center;
    height: 20px;
    margin-top: -10px;

    .block,
    &-block {
      width: 0%;
      height: inherit;
      background: var(--dark);
      position: absolute;
      animation: blockSweep 2s cubic-bezier(0.74, 0.06, 0.4, 0.92) forwards;
      animation-delay: 2s;
      display: flex;
    }

    p {
      animation: secFadeIn 2s forwards;
      animation-delay: 3.2s;
      opacity: 0;
      font-weight: 800;
      font-family: 'Lato';
      font-size: 14px;
      text-transform: uppercase;
      letter-spacing: 5px;
      color: var(--white);
      margin-top: 10px;
    }
  }
}

@keyframes blockSweep {
  0% {
    width: 0%;
    left: 0;
  }
  50% {
    width: 100%;
    left: 0;
  }
  100% {
    width: 0;
    left: 100%;
  }
}

@keyframes mainFadeIn {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}

@keyframes popIn {
  0% {
    width: 0px;
    height: 0px;
    background: linear-gradient(to left, var(--primary), var(--dark));
    border: 0px solid #ddd;
    opacity: 0;
  }
  50% {
    width: 10px;
    height: 10px;
    background: var(--dark);
    opacity: 1;
    bottom: 45px;
  }
  65% {
    width: 7px;
    height: 7px;
    bottom: 0px;
    width: 15px;
  }
  80% {
    width: 10px;
    height: 10px;
    bottom: 20px;
  }
  100% {
    width: 10px;
    height: 10px;
    background: var(--primary);
    box-shadow: 0 0 3px 3px var(--dark);
    border: 0px solid #222;
    bottom: 5px;
  }
}

@keyframes secFadeIn {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 0.5;
  }
}

@media screen and (max-width: 400px) {
  .container-title {
    .title,
    &__title {
      margin-left: 11px;
    }
  }
}
</style>
