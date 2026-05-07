import { routes } from "./routes.js";
import { store } from "./store.js";
import { renderView } from "../ui/view.js";

const routeMap = new Map(routes.map((route) => [route.path, route]));

function resolveRoute(hash) {
  if (!hash || hash === "#") {
    return routeMap.get("#/dang-nhap");
  }
  return routeMap.get(hash) || routeMap.get("#/dang-nhap");
}

async function handleRoute() {
  const route = resolveRoute(window.location.hash);
  const state = store.getState();

  if (route.requiresAuth && !state.session) {
    navigate("#/dang-nhap");
    return;
  }

  store.setState({ route: route.path });
  await renderView(route);
}

export function navigate(path) {
  if (window.location.hash !== path) {
    window.location.hash = path;
    return;
  }
  handleRoute();
}

export function initRouter() {
  window.addEventListener("hashchange", handleRoute);
  window.addEventListener("load", handleRoute);

  if (!window.location.hash) {
    navigate("#/dang-nhap");
  }
}
