import { qs } from '../dom.js';
import { fetchStudentPreferences, fetchQuickLookup, getLoggedStudent } from '../core/api.js';

function renderResult(root, data){
  const area = root.querySelector('#lookup-result');
  // No student found
  if(!data){
    area.innerHTML = `
      <div class="card quick-result">
        <h3>Kết quả</h3>
        <ul class="result-list">
          <li class="result-item not-found">Không tìm thấy</li>
        </ul>
      </div>`;
    return;
  }

  const prefs = data.preferences || [];
  // Separate admitted / not admitted
  const admitted = prefs.filter(p => String(p.result||'').toLowerCase().includes('trúng'));
  const notAdmitted = prefs.filter(p => !String(p.result||'').toLowerCase().includes('trúng'));

  // Build HTML
  let html = `<div class="card quick-result"><h3>Kết quả</h3><div class="result-block"><strong>Tìm thấy:</strong>`;
  if(prefs.length === 0){
    html += `<p class="muted">Không có nguyện vọng nào được lưu.</p>`;
  } else {
    if(admitted.length){
      html += `<div class="result-sub"><strong>Trúng tuyển</strong><ul class="result-sublist">`;
      html += admitted.map(p => `<li><strong>${p.major}</strong> — điểm: ${p.diem_xt ?? '-'} — tổ hợp: ${p.tohop ?? '-'} — phương thức: ${p.phuongthuc ?? '-'}</li>`).join('');
      html += `</ul></div>`;
    }
    if(notAdmitted.length){
      html += `<div class="result-sub"><strong>Không trúng tuyển</strong><ul class="result-sublist">`;
      html += notAdmitted.map(p => `<li>${p.major} — ${p.result ?? ''}</li>`).join('');
      html += `</ul></div>`;
    }
  }
  html += `</div></div>`;
  area.innerHTML = html;
}

export async function initQuickLookup(){
  const root = document.getElementById('app');
  if(!root) return;
  const form = root.querySelector('#lookup-form');
  const btnClear = root.querySelector('#btn-clear');

  // If logged in, auto-fetch preferences and show result
  try{
    const logged = getLoggedStudent();
    if(logged){
      const prefs = await fetchStudentPreferences(logged.cccd);
      renderResult(root, { student: logged, preferences: prefs });
    }
  }catch(e){
    console.error(e);
  }

  form.addEventListener('submit', async (ev)=>{
    ev.preventDefault();
    const keyword = form.querySelector('[name=keyword]').value.trim();
    const password = form.querySelector('[name=password]').value.trim();
    try{
      const data = await fetchQuickLookup(keyword, password);
      renderResult(root, data);
    }catch(err){
      renderResult(root, null);
    }
  });
  btnClear.addEventListener('click', ()=>{form.reset(); root.querySelector('#lookup-result').innerHTML='';});
}

// Auto-init on navigation
window.addEventListener('spa:navigated', ()=>{
  if(location.hash === '' || location.hash === '#/quick-lookup'){
    initQuickLookup();
  }
});
