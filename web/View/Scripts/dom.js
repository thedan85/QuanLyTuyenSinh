export async function loadHtmlPartial(relativePath, fallback = ''){
  try{
    const resp = await fetch(relativePath, {cache:'no-store'});
    if(!resp.ok) throw new Error('Not found');
    return await resp.text();
  }catch(e){console.error(e);return fallback}
}

export function qs(root, sel){return root.querySelector(sel)}
export function qsa(root, sel){return Array.from(root.querySelectorAll(sel))}
export function escapeHtml(s){return String(s).replaceAll('&','&amp;').replaceAll('<','&lt;').replaceAll('>','&gt;')}
