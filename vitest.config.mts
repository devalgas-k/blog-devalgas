import { fileURLToPath } from 'node:url';
import { defineConfig } from 'vitest/config';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      vue: 'vue',
      '@': fileURLToPath(new URL('./src/main/webapp/app/', import.meta.url)),
      '@content': fileURLToPath(new URL('./src/main/webapp/content/', import.meta.url)),
    },
  },
  define: {
    I18N_HASH: '"generated_hash"',
    SERVER_API_URL: '"/"',
    APP_VERSION: '"TEST"',
    RECAPTCHA_SITE_KEY: '"6LewAUwsAAAAAOVXC6a37SgGw4TOQa4T9JUo6wcK"',
    ADSENSE_CLIENT: '""',
    ADSENSE_SLOT: '""',
    IMAGE_BASE_PATH: '"/content/images"',
    WRITING_HASH: '"#writing"',
    LINKEDIN_URL: '"https://www.linkedin.com/in/devalgas-kamga/"',
    TWITTER_URL: '"https://x.com/devalgas1/"',
    GITHUB_URL: '"https://github.com/devalgas-k/"',
    MEDIUM_URL: '"https://medium.com/@kamgadevalgas"',
    MAIL_TO: '""',
    PHONE_URL: '""',
    WHATSAPP_URL: '""',
  },
  test: {
    globals: true,
    environment: 'happy-dom',
    setupFiles: [fileURLToPath(new URL('./src/main/webapp/app/test-setup.ts', import.meta.url))],
    reporters: ['default', 'vitest-sonar-reporter'],
    outputFile: {
      'vitest-sonar-reporter': fileURLToPath(new URL('./target/test-results/TESTS-results-vitest.xml', import.meta.url)),
    },
    coverage: {
      provider: 'v8',
      reportsDirectory: fileURLToPath(new URL('./target/vite-coverage', import.meta.url)),
      include: ['src/main/webapp/app/**/v1/**/*.{ts,vue}'],
      exclude: ['target/**', 'src/main/webapp/content/**', 'src/main/webapp/swagger-ui/**', '**/*.spec.ts', '**/*.spec-v1.ts'],
      thresholds: {
        statements: 84,
        branches: 72,
        lines: 84,
      },
    },
  },
});
