import { initRouter } from "./core/router.js";
import { store } from "./core/store.js";

document.addEventListener("DOMContentLoaded", () => {
  const state = store.getState();
  document.body.dataset.theme = state.theme || "sky";
  initRouter();
});
