import { loadHtmlPartial } from './dom.js';
import * as api from './core/api.js';

(async () => {
  // load header partial and inject into DOM
  const hdr = await loadHtmlPartial('Components/site-header.html', '');
  const root = document.getElementById('header-root');
  if (root) root.innerHTML = hdr;

  // wire header auth UI
  const studentChip = document.querySelector('.student-chip');
  const btnOpenLogin = document.getElementById('btn-open-login');
  const btnLogout = document.getElementById('btn-logout');

  const updateHeader = () => {
    const student = api.getLoggedStudent();
    if (student) {
      if (studentChip) {
        studentChip.classList.remove('is-hidden');
        studentChip.textContent = `Xin chào, ${student.fullName || student.cccd}`;
      }
      if (btnLogout) btnLogout.classList.remove('is-hidden');
      if (btnOpenLogin) btnOpenLogin.classList.add('is-hidden');
    } else {
      if (studentChip) studentChip.style.display = 'none';
      if (btnLogout) btnLogout.style.display = 'none';
      if (btnOpenLogin) btnOpenLogin.classList.remove('is-hidden');
    }
  };

  if (btnLogout) {
    btnLogout.addEventListener('click', async () => {
      await api.logoutStudent();
      window.dispatchEvent(new CustomEvent('auth:changed'));
      location.hash = '#/login';
    });
  }

  window.addEventListener('auth:changed', updateHeader);
  updateHeader();
})();
