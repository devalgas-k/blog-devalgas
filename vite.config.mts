import { URL, fileURLToPath } from 'node:url';
import { defineConfig, normalizePath, loadEnv } from 'vite';

import vue from '@vitejs/plugin-vue';
import { viteStaticCopy } from 'vite-plugin-static-copy';

const { getAbsoluteFSPath } = await import('swagger-ui-dist');
const swaggerUiPath = getAbsoluteFSPath();

// eslint-disable-next-line prefer-const
let config = defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');

  return {
    plugins: [
      vue(),
      viteStaticCopy({
        targets: [
          {
            src: [
              `${normalizePath(swaggerUiPath)}/*.{js,css,html,png}`,
              `!${normalizePath(swaggerUiPath)}/**/index.html`,
              normalizePath(fileURLToPath(new URL('./dist/axios.min.js', import.meta.resolve('axios/package.json')))),
              normalizePath(fileURLToPath(new URL('./src/main/webapp/swagger-ui/index.html', import.meta.url))),
            ],
            dest: 'swagger-ui',
          },
        ],
      }),
    ],
    root: fileURLToPath(new URL('./src/main/webapp/', import.meta.url)),
    publicDir: fileURLToPath(new URL('./target/classes/static/public', import.meta.url)),
    cacheDir: fileURLToPath(new URL('./target/.vite-cache', import.meta.url)),
    build: {
      emptyOutDir: true,
      outDir: fileURLToPath(new URL('./target/classes/static/', import.meta.url)),
      rollupOptions: {
        input: {
          app: fileURLToPath(new URL('./src/main/webapp/index.html', import.meta.url)),
        },
      },
    },
    resolve: {
      alias: {
        vue: '@vue/compat/dist/vue.esm-bundler.js',
        '@': fileURLToPath(new URL('./src/main/webapp/app/', import.meta.url)),
        '@content': fileURLToPath(new URL('./src/main/webapp/content/', import.meta.url)),
      },
    },
    define: {
      I18N_HASH: '"generated_hash"',
      SERVER_API_URL: '"/"',
      APP_VERSION: `"${env.APP_VERSION ? env.APP_VERSION : 'DEV'}"`,
      RECAPTCHA_SITE_KEY: `"${env.RECAPTCHA_SITE_KEY ? env.RECAPTCHA_SITE_KEY : '6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI'}"`,
      ADSENSE_CLIENT: `"${env.ADSENSE_CLIENT ? env.ADSENSE_CLIENT : 'ca-pub-6181972205565553'}"`,
      ADSENSE_SLOT: `"${env.ADSENSE_SLOT ? env.ADSENSE_SLOT : '4433984685'}"`,
      IMAGE_BASE_PATH: `"${env.IMAGE_BASE_PATH ? env.IMAGE_BASE_PATH : '/content/images'}"`,
      WRITING_HASH: `"${env.WRITING_HASH ? env.WRITING_HASH : '#writing'}"`,
      LINKEDIN_URL: `"${env.LINKEDIN_URL ? env.LINKEDIN_URL : 'https://www.linkedin.com/in/devalgas-kamga/'}"`,
      TWITTER_URL: `"${env.TWITTER_URL ? env.TWITTER_URL : 'https://x.com/devalgas1/'}"`,
      GITHUB_URL: `"${env.GITHUB_URL ? env.GITHUB_URL : 'https://github.com/devalgas-k/'}"`,
      MEDIUM_URL: `"${env.MEDIUM_URL ? env.MEDIUM_URL : 'https://medium.com/@kamgadevalgas'}"`,
      MAIL_TO: `"${env.MAIL_TO ? env.MAIL_TO : 'mailto:kamgadevalga@icloud.com'}"`,
      PHONE_URL: `"${env.PHONE_URL ? env.PHONE_URL : 'tel:+23055040199'}"`,
      WHATSAPP_URL: `"${env.WHATSAPP_URL ? env.WHATSAPP_URL : 'https://wa.me/237699520388'}"`,
    },
    server: {
      host: true,
      port: 9000,
      proxy: Object.fromEntries(
        ['/api', '/management', '/v3/api-docs', '/websocket'].map(res => [
          res,
          {
            target: 'http://localhost:8080',
            ws: res === '/websocket',
          },
        ]),
      ),
    },
  };
});

// jhipster-needle-add-vite-config - JHipster will add custom config

export default config;
