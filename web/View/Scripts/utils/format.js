export function formatFullName(ho, ten) {
  return [ho, ten].filter(Boolean).join(" ").trim() || "-";
}

export function normalizeDobToKey(ngaySinh) {
  if (!ngaySinh) {
    return "";
  }
  const parts = ngaySinh.split("/");
  if (parts.length !== 3) {
    return "";
  }
  const [day, month, year] = parts;
  return `${day}${month}${year}`;
}

export function formatScore(value, digits = 2) {
  if (value === null || value === undefined || Number.isNaN(value)) {
    return "-";
  }
  return Number(value).toFixed(digits);
}

export function formatNumber(value, digits = 0) {
  if (value === null || value === undefined || Number.isNaN(value)) {
    return "-";
  }
  return Number(value).toFixed(digits);
}
