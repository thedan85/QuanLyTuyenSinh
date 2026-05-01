import { loadTemplate, renderTemplate } from "./template.js";

const componentPaths = {
  nav: "./Components/nav.html",
  alert: "./Components/alert.html",
  empty: "./Components/empty-state.html",
  loading: "./Components/loading.html",
  result: "./Components/result-card.html",
  modal: "./Components/modal.html",
};

export async function renderComponent(name, data = {}) {
  const path = componentPaths[name];
  if (!path) {
    throw new Error(`Unknown component: ${name}`);
  }
  const template = await loadTemplate(path);
  return renderTemplate(template, data);
}

export async function mountComponent(name, container, data = {}) {
  if (!container) {
    return;
  }
  container.innerHTML = await renderComponent(name, data);
}
