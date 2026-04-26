export function getRoutePath(hash, routes) {
  if (routes[hash]) return routes[hash];
  // fallback: try matching prefixes
  for (const r in routes) {
    if (hash.startsWith(r)) return routes[r];
  }
  return routes[''];
}

export function matchRoute(hash, pattern){
  // simple placeholder for future route params
  return hash === pattern;
}
