import { loadTemplate } from "./template.js";
import { renderComponent } from "./components.js";
import { store } from "../core/store.js";
import { navigate } from "../core/router.js";

function setTitle(title) {
  if (title) {
    document.title = `${title} | Tuyển sinh 2026`;
  }
}

async function renderNav() {
  const header = document.getElementById("app-header");
  header.innerHTML = await renderComponent("nav");

  const state = store.getState();
  const navUser = document.getElementById("navUser");
  if (navUser && state.session) {
    navUser.textContent = `${state.session.ho} ${state.session.ten} - ${state.session.cccd}`;
  }

  const logoutBtn = document.getElementById("btnLogout");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", () => {
      store.setState({ session: null });
      navigate("#/dang-nhap");
    });
  }
}

export async function renderView(route) {
  const main = document.getElementById("app-main");
  const header = document.getElementById("app-header");

  if (route.showNav) {
    await renderNav();
  } else {
    header.innerHTML = "";
  }

  main.classList.add("view-enter");
  main.innerHTML = await loadTemplate(route.template);

  window.requestAnimationFrame(() => {
    main.classList.add("view-enter-active");
  });

  window.setTimeout(() => {
    main.classList.remove("view-enter", "view-enter-active");
  }, 350);

  setTitle(route.title);

  if (route.controller) {
    await route.controller();
  }
}
