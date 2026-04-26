import { loginStudent, getLoggedStudent, logoutStudent } from '../core/api.js';

export function initLoginPage(){
  const root = document.getElementById('app');
  if(!root) return;
  const form = root.querySelector('#student-login-form');
  const feedback = root.querySelector('#login-feedback');
  const btnClear = root.querySelector('#btn-login-clear');

  form.addEventListener('submit', async (ev)=>{
    ev.preventDefault();
    const cccd = form.querySelector('#login-cccd').value.trim();
    const password = form.querySelector('#login-password').value.trim();
    try{
      await loginStudent({cccd,password});
      feedback.textContent = 'Đăng nhập thành công.';
      // sync header
      window.dispatchEvent(new CustomEvent('auth:changed'));
      location.hash = '#/quick-lookup';
    }catch(err){
      feedback.textContent = err.message || 'Lỗi đăng nhập';
    }
  });

  if(btnClear) btnClear.addEventListener('click', ()=>{form.reset();feedback.textContent='';});
}

window.addEventListener('spa:navigated', ()=>{
  if(location.hash === '' || location.hash === '#/login') initLoginPage();
});
