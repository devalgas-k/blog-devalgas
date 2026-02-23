import { defineStore } from 'pinia';

export interface ContentUpdateState {
  articlesChanged: boolean;
}

export const useContentUpdateStore = defineStore('contentUpdateStore', {
  state: (): ContentUpdateState => ({
    articlesChanged: false,
  }),
  actions: {
    markArticlesChanged() {
      this.articlesChanged = true;
    },
    resetArticlesChanged() {
      this.articlesChanged = false;
    },
  },
});
