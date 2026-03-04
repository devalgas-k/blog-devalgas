import { defineStore } from 'pinia';

export interface RenderGateState {
  i18nReady: boolean;
  routeReady: boolean;
  dataReady: boolean;
}

export const useRenderGateStore = defineStore('renderGateStore', {
  state: (): RenderGateState => ({
    i18nReady: false,
    routeReady: false,
    dataReady: false,
  }),
  getters: {
    ready: s => s.i18nReady && s.routeReady && s.dataReady,
  },
  actions: {
    markI18nReady() {
      this.i18nReady = true;
    },
    markRouteReady() {
      this.routeReady = true;
    },
    markDataReady() {
      this.dataReady = true;
    },
    reset() {
      this.i18nReady = false;
      this.routeReady = false;
      this.dataReady = false;
    },
  },
});
