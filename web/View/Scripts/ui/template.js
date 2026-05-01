const templateCache = new Map();

export async function loadTemplate(path) {
  if (templateCache.has(path)) {
    return templateCache.get(path);
  }

  const response = await fetch(path, { cache: "no-store" });
  if (!response.ok) {
    throw new Error(`Template not found: ${path}`);
  }

  const html = await response.text();
  templateCache.set(path, html);
  return html;
}

export function renderTemplate(template, data = {}) {
  return template.replace(/{{(\w+)}}/g, (_match, key) => {
    const value = data[key];
    return value === undefined || value === null ? "" : String(value);
  });
}
