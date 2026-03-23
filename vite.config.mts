import { URL, fileURLToPath } from 'node:url';
import { defineConfig, normalizePath, loadEnv } from 'vite';

import vue from '@vitejs/plugin-vue';
import { viteStaticCopy } from 'vite-plugin-static-copy';
import type { Plugin } from 'vite';

const { getAbsoluteFSPath } = await import('swagger-ui-dist');
const swaggerUiPath = getAbsoluteFSPath();

// eslint-disable-next-line prefer-const
let config = defineConfig(({ mode }) => {
  const envFile = loadEnv(mode, process.cwd(), '');
  const env: Record<string, string | undefined> = { ...(process.env as Record<string, string | undefined>), ...envFile };

  const crittersInlineCss = (): Plugin => ({
    name: 'critters-inline-css',
    enforce: 'post',
    apply: 'build',
    async transformIndexHtml(html) {
      if (mode === 'development') return html;
      const { default: Critters } = await import('critters');
      const critters = new Critters({
        path: fileURLToPath(new URL('./target/classes/static/', import.meta.url)),
        publicPath: '/',
        preload: 'swap',
        noscriptFallback: true,
        inlineFonts: true,
        pruneSource: true,
        compress: true,
      });
      return await critters.process(html);
    },
  });

  const ADSENSE_ENABLED_VAL = env.ADSENSE_ENABLED ? ['1', 'true', 'yes', 'on'].includes(String(env.ADSENSE_ENABLED).toLowerCase()) : true;
  try {
    // eslint-disable-next-line no-console
    console.log(`[vite] ADSENSE_ENABLED="${env.ADSENSE_ENABLED}" -> ${ADSENSE_ENABLED_VAL}`);
  } catch {}

  return {
    plugins: [
      vue(),
      ...(mode !== 'development'
        ? [
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
                {
                  src: [
                    normalizePath(fileURLToPath(new URL('./src/main/webapp/content/images/*.{png,jpg,jpeg,gif,svg,ico}', import.meta.url))),
                  ],
                  dest: 'content/images',
                },
              ],
            }),
            crittersInlineCss(),
          ]
        : []),
    ],
    root: fileURLToPath(new URL('./src/main/webapp/', import.meta.url)),
    publicDir: mode === 'development' ? undefined : fileURLToPath(new URL('./target/classes/static/public', import.meta.url)),
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
      RECAPTCHA_SITE_KEY: `"${env.RECAPTCHA_SITE_KEY ? env.RECAPTCHA_SITE_KEY : '6LewAUwsAAAAAOVXC6a37SgGw4TOQa4T9JUo6wcK'}"`,
      ADSENSE_CLIENT: `"${env.ADSENSE_CLIENT ? env.ADSENSE_CLIENT : 'ca-pub-8340083616743463'}"`,
      ADSENSE_SLOT: `"${env.ADSENSE_SLOT ? env.ADSENSE_SLOT : '4433984685'}"`,
      ADSENSE_ENABLED: ADSENSE_ENABLED_VAL,
      ADSENSE_SLOT_TOP: `"${env.ADSENSE_SLOT_TOP ? env.ADSENSE_SLOT_TOP : '1159284671'}"`,
      ADSENSE_SLOT_SIDEBAR_LEFT: `"${env.ADSENSE_SLOT_SIDEBAR_LEFT ? env.ADSENSE_SLOT_SIDEBAR_LEFT : '6647773869'}"`,
      ADSENSE_SLOT_SIDEBAR_RIGHT: `"${env.ADSENSE_SLOT_SIDEBAR_RIGHT ? env.ADSENSE_SLOT_SIDEBAR_RIGHT : '2280794651'}"`,
      ADSENSE_SLOT_FOOTER: `"${env.ADSENSE_SLOT_FOOTER ? env.ADSENSE_SLOT_FOOTER : '6028467975'}"`,
      IMAGE_BASE_PATH: `"${env.IMAGE_BASE_PATH ? env.IMAGE_BASE_PATH : '/content/images'}"`,
      WRITING_HASH: `"${env.WRITING_HASH ? env.WRITING_HASH : '#writing'}"`,
      LINKEDIN_URL: `"${env.LINKEDIN_URL ? env.LINKEDIN_URL : 'https://www.linkedin.com/in/devalgas-kamga/'}"`,
      TWITTER_URL: `"${env.TWITTER_URL ? env.TWITTER_URL : 'https://x.com/devalgas1/'}"`,
      GITHUB_URL: `"${env.GITHUB_URL ? env.GITHUB_URL : 'https://github.com/devalgas-k/'}"`,
      MEDIUM_URL: `"${env.MEDIUM_URL ? env.MEDIUM_URL : 'https://medium.com/@kamgadevalgas'}"`,
      MAIL_TO: `"${env.MAIL_TO ? env.MAIL_TO : 'mailto:contact@devalgas.net'}"`,
      PHONE_URL: `"${env.PHONE_URL ? env.PHONE_URL : 'tel:+23055040199'}"`,
      WHATSAPP_URL: `"${env.WHATSAPP_URL ? env.WHATSAPP_URL : 'https://wa.me/23055040199'}"`,
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
