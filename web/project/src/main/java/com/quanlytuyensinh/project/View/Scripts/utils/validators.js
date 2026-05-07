export function validateCccd(value) {
  if (!value) {
    return { valid: false, message: "Vui lòng nhập CCCD." };
  }
  if (!/^\d{12}$/.test(value)) {
    return { valid: false, message: "CCCD phải 12 chữ số." };
  }
  return { valid: true };
}

export function validateDob(value) {
  if (!value) {
    return { valid: false, message: "Vui lòng nhập ngày sinh." };
  }
  if (!/^\d{8}$/.test(value)) {
    return { valid: false, message: "Ngày sinh phải đúng định dạng ddmmyyyy." };
  }

  const day = Number(value.slice(0, 2));
  const month = Number(value.slice(2, 4));
  const year = Number(value.slice(4, 8));
  const date = new Date(year, month - 1, day);

  const isValidDate =
    date.getFullYear() === year &&
    date.getMonth() === month - 1 &&
    date.getDate() === day;

  if (!isValidDate) {
    return { valid: false, message: "Ngày sinh không hợp lệ." };
  }

  return { valid: true };
}
