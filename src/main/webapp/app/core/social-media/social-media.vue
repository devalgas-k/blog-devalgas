<template>
  <div class="role"><p v-text="t$('core.socialMedia.role')"></p></div>
  <ul class="dp-social-media">
    <li v-for="link in socialLinks" :key="link.name">
      <a :class="link.name" :href="link.url" target="_blank" rel="noopener noreferrer" :aria-label="link.ariaLabel">
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <i v-if="link.icon" :class="link.icon" aria-hidden="true"></i>
        <svg v-else width="24" height="24" viewBox="0 0 48 48" aria-hidden="true">
          <circle cx="24" cy="24" r="22" fill="currentColor" opacity="0.1" />
          <path
            d="M14 34 V14 L24 28 L34 14 V34"
            stroke="currentColor"
            stroke-width="3"
            fill="none"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </a>
    </li>
  </ul>
</template>

<script lang="ts" src="./social-media.component.ts"></script>

<style lang="scss" scoped>
.role {
  display: none;
}
.dp-social-media {
  margin: 0;
  padding: 0;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  isolation: isolate;

  li {
    list-style: none;

    a {
      display: block;
      position: relative;
      width: 56px;
      height: 50px;
      line-height: 56px;
      font-size: 10px;
      font-weight: normal;
      text-align: center;
      text-decoration: none;
      margin: 0 10px;
      transition: 0.5s;

      span {
        position: absolute;
        transition: transform 0.5s;

        &:nth-child(1),
        &:nth-child(3) {
          width: 100%;
          height: 3px;
          background: #fff;
        }
        &:nth-child(1) {
          top: 0;
          left: 0;
          transform-origin: right;
        }
        &:nth-child(3) {
          bottom: 0;
          left: 0;
          transform-origin: left;
        }
        &:nth-child(2),
        &:nth-child(4) {
          width: 3px;
          height: 100%;
          background: #fff;
        }
        &:nth-child(2) {
          top: 0;
          left: 0;
          transform: scale(0);
          transform-origin: bottom;
        }
        &:nth-child(4) {
          top: 0;
          right: 0;
          transform: scale(0);
          transform-origin: top;
        }
      }

      &:hover {
        span:nth-child(1) {
          transform: scaleX(0);
          transform-origin: left;
          transition: transform 0.5s;
        }
        span:nth-child(3) {
          transform: scaleX(0);
          transform-origin: right;
          transition: transform 0.5s;
        }
        span:nth-child(2) {
          transform: scale(1);
          transform-origin: top;
          transition: transform 0.5s;
        }
        span:nth-child(4) {
          transform: scale(1);
          transform-origin: bottom;
          transition: transform 0.5s;
        }
      }

      &.facebook:hover {
        color: #3b5998;
        span {
          background: #3b5998;
        }
      }
      &.twitter:hover {
        color: #1da1f2;
        span {
          background: #1da1f2;
        }
      }
      &.github:hover {
        color: #0969da;
        span {
          background: #181717;
        }
      }
      &.medium:hover {
        color: #00ab6c;
        span {
          background: #00ab6c;
        }
      }
      &.linkedin:hover {
        color: #0a66c2;
        span {
          background: #0a66c2;
        }
      }
    }
  }
}
</style>
