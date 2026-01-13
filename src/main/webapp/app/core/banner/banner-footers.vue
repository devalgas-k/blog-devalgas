<template>
  <footer class="footer-test" :style="styleVars">
    <div v-if="showParallax" class="footer-test__parralax">
      <div class="footer-test__parralax-trees"></div>
      <div class="footer-test__parralax-moto"></div>
      <div class="footer-test__parralax-secondplan"></div>
      <div class="footer-test__parralax-premierplan"></div>
      <div class="footer-test__parralax-voiture"></div>
    </div>
  </footer>
</template>

<script lang="ts" src="./banner-footers.component.ts"></script>

<style lang="scss" scoped>
@import url('https://fonts.googleapis.com/css2?family=Open+Sans:wght@600&display=swap');
html,
body {
  height: 100%;
}
body {
  font-family: 'Open Sans', sans-serif;
  display: flex;
  flex-direction: column;
}

// Footer variables
$footerBg: 'rgba(0, 0, 0, 0.5)';
$footerText: #fff;

.footer-test {
  position: relative;
  padding: 0.1rem 0;
  color: $footerText;
  background: $footerBg;
  margin-top: auto;
  &__columns {
    display: flex;
    justify-content: space-between;
  }
  &__col {
    &-title {
      font-size: 1.6rem;
      margin-bottom: 2rem;
      text-transform: uppercase;
      display: flex;
      align-items: center;
      gap: 1rem;
    }
  }
  a {
    display: flex;
    align-items: center;
    color: $footerText;
    text-decoration: none;
    gap: 1rem;
  }
  &__nav-list {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }
  &__copyrights {
    padding-top: 3rem;
    margin-top: 3rem;
    border-top: 1px solid rgba($footerText, 0.2);
    p {
      display: flex;
      gap: 0.5rem;
    }
  }
  @keyframes parralax {
    0% {
      background-position: 260px;
    }
    100% {
      background-position: -10000vw;
    }
  }
  @keyframes moto {
    0% {
      transform: translateY(0) rotate(0);
    }
    5% {
      transform: translateY(0) rotate(-5deg);
    }
    25% {
      transform: translateY(60px) rotate(-20deg);
    }
    49% {
      transform: translateY(0) rotate(-1deg);
    }
    51% {
      transform: translateY(0) rotate(1deg);
    }
    75% {
      transform: translateY(60px) rotate(20deg);
    }
    80% {
      transform: translateY(60px) rotate(0deg);
    }
    98% {
      transform: translateY(0) rotate(0);
    }
  }
  @keyframes voiture {
    0% {
      transform: rotate(0);
    }
    25% {
      transform: rotate(-5deg);
    }
    50% {
      transform: rotate(0);
    }
    75% {
      transform: rotate(5deg);
    }
    100% {
      transform: rotate(0);
    }
  }
  &__parralax {
    position: absolute;
    left: 0;
    top: -300px;
    height: 300px;
    width: 100%;
    overflow: hidden;
    &-premierplan {
      background-position: 60px;
    }
    &-trees,
    &-premierplan,
    &-secondplan {
      position: absolute;
      inset: 0;
      background-repeat: repeat-x;
      background-position-y: 100% !important;
      animation: parralax var(--parallax-duration, 600s) linear infinite;
    }
    &-moto {
      position: absolute;
      bottom: 80px;
      left: 50%;
      margin-left: -250px;
      height: 200px;
      width: 150px;
      // background: url('../../../content/images/moto-net.gif');
      background-repeat: no-repeat;
      transform-origin: 50% 80%;
      animation: moto var(--moto-duration, 5s) linear infinite;
    }
    &-voiture {
      position: absolute;
      bottom: 10px;
      left: 50%;
      margin-left: 250px;
      height: 114px;
      width: 206px;
      background: var(--voiture-image, url('../../../content/images/voiture-fumee.gif'));
      background-repeat: no-repeat;
      animation: voiture var(--voiture-duration, 1s) linear infinite;
    }
    &-trees {
      background-image: var(--trees-image, url('../../../content/images/arbres.png'));
      bottom: -60px;
      animation-duration: var(--trees-duration, 1000s);
    }
    &-premierplan {
      background-image: var(--premierplan-image, url('../../../content/images/premierplanv3.png'));
      animation-duration: var(--premierplan-duration, 500s);
      //background-position-y: 27px !important;
    }
    &-secondplan {
      background-image: var(--secondplan-image, url('../../../content/images/second-plan.png'));
      animation-duration: var(--secondplan-duration, 600s);
    }
  }
}
</style>
