// Minimal SPA loader + simple hash router
import { loadHtmlPartial } from './dom.js';

import { matchRoute, getRoutePath } from './route.js';
import { getLoggedStudent } from './core/api.js';

const routes = {
  '': 'Pages/login.html',
  '#/login': 'Pages/login.html',
  '#/quick-lookup': 'Pages/quick-lookup.html',
  '#/calculator': 'Pages/score-calculator.html'
};

async function render() {
  const root = document.getElementById('app');
  if (!root) return;
  const key = location.hash || '';
  const isLoginPage = key === '' || key === '#/login';
  const loggedIn = !!getLoggedStudent();
  // If not logged in and not on login page, redirect to login
  if (!loggedIn && !isLoginPage) {
    location.hash = '#/login';
    return;
  }
  // If logged in and on login page, redirect to main page
  if (loggedIn && isLoginPage) {
    location.hash = '#/quick-lookup';
    return;
  }
  const path = getRoutePath(key, routes);
  const html = await loadHtmlPartial(path, '<section class="page"><div class="container"><p>Không tìm thấy trang</p></div></section>');
  root.innerHTML = html;
  // dispatch event for page scripts
  window.dispatchEvent(new CustomEvent('spa:navigated'));
}

window.addEventListener('hashchange', render);
window.addEventListener('load', render);

// Login page: show/hide password logic
window.addEventListener('spa:navigated', () => {
  // Show/hide password logic
  const pwdInput = document.getElementById('login-password');
  const toggle = document.getElementById('show-password-toggle');
  if (pwdInput && toggle) {
    toggle.addEventListener('change', () => {
      pwdInput.type = toggle.checked ? 'text' : 'password';
    });
  }

  // Hiện/ẩn nav theo trạng thái đăng nhập
  const navLookup = document.getElementById('nav-lookup');
  const navCalculator = document.getElementById('nav-calculator');
  const loggedIn = !!(typeof getLoggedStudent === 'function' ? getLoggedStudent() : null);
  if (navLookup) navLookup.style.display = loggedIn ? '' : 'none';
  if (navCalculator) navCalculator.style.display = loggedIn ? '' : 'none';
});

export { render };
