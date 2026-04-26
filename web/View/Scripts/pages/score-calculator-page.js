import { qs } from '../dom.js';
import { fetchMajors } from '../core/api.js';

function calcDgnl(score1200){
  // simple linear scaling to 30
  return (Number(score1200) / 1200) * 30;
}

export async function initScoreCalculator(){
  const root = document.getElementById('app');
  if(!root) return;
  const tabBtns = root.querySelectorAll('.tab-btn');
  tabBtns.forEach(b=>b.addEventListener('click', ()=>{
    tabBtns.forEach(x=>x.classList.remove('is-active'));
    b.classList.add('is-active');
    const target = b.dataset.target;
    root.querySelectorAll('.tab-panel').forEach(p=>p.classList.remove('is-active'));
    root.querySelector('#tab-'+target).classList.add('is-active');
  }));

  // load majors
  const majors = await fetchMajors();
  const dsel = root.querySelector('#dgnl-major');
  const vsel = root.querySelector('#vsat-major');
  majors.forEach(m=>{const o=document.createElement('option');o.value=m.code;o.textContent=m.name;dsel.append(o)});
  majors.forEach(m=>{const o=document.createElement('option');o.value=m.code;o.textContent=m.name;vsel.append(o)});

  root.querySelector('#form-dgnl').addEventListener('submit', (ev)=>{
    ev.preventDefault();
    const s = root.querySelector('#dgnl-score').value;
    const converted = calcDgnl(s);
    root.querySelector('#dgnl-result').innerHTML=`<div class="card"><p>Điểm quy đổi (thang 30): <strong>${converted.toFixed(2)}</strong></p></div>`;
  });

  root.querySelector('#form-vsat').addEventListener('submit', (ev)=>{
    ev.preventDefault();
    const m = Number(root.querySelector('#vsat-math').value||0);
    const p = Number(root.querySelector('#vsat-phys').value||0);
    const c = Number(root.querySelector('#vsat-chem').value||0);
    // simple avg to 10 scale
    const avg150 = (m+p+c)/3;
    const scaled = (avg150 / 150) * 10;
    root.querySelector('#vsat-result').innerHTML=`<div class="card"><p>Điểm tính xét (thang 10): <strong>${scaled.toFixed(2)}</strong></p></div>`;
  });
}

window.addEventListener('spa:navigated', ()=>{
  if(location.hash === '#/calculator') initScoreCalculator();
});
