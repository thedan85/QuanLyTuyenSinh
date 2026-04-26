// Mock state for UI development (sample accounts, majors, nguyện vọng)
const DEFAULT_STATE = {
  students: [
    {
      cccd: '001204000001',
      sbd: 'SGU001',
      fullName: 'Nguyễn Văn A',
      ngay_sinh: '01/02/2004',
      dien_thoai: '0912345678',
      noi_sinh: 'Hà Nội',
      email: 'a.nguyen@example.com',
      ghi_chu: 'Tài khoản demo',
      password: '123456'
    },
    {
      cccd: '001204000002',
      sbd: 'SGU002',
      fullName: 'Trần Thị B',
      ngay_sinh: '15/05/2003',
      dien_thoai: '0987654321',
      noi_sinh: 'Hải Phòng',
      email: 'b.tran@example.com',
      ghi_chu: 'Ưu tiên khu vực',
      password: '15052003'
    },
    {
      cccd: '001204000003',
      sbd: 'SGU003',
      fullName: 'Lê Văn C',
      ngay_sinh: '20-08-2003',
      dien_thoai: '0900111222',
      noi_sinh: 'Đà Nẵng',
      email: 'c.le@example.com',
      ghi_chu: '',
      password: '20082003'
    }
  ],

  majors: [
    { code: '7480201', name: 'Công nghệ thông tin' },
    { code: '7340101', name: 'Quản trị kinh doanh' },
    { code: '7520101', name: 'Khoa học máy tính' }
  ],

  // preferences (nguyện vọng) per student
  preferences: [
    // student A
    {
      id: 1,
      cccd: '001204000001',
      order: 1,
      code: '7480201',
      major: 'Công nghệ thông tin',
      tohop: 'A01',
      phuongthuc: 'PT1',
      diem_xt: 26.0,
      result: 'Trúng tuyển'
    },
    {
      id: 2,
      cccd: '001204000001',
      order: 2,
      code: '7340101',
      major: 'Quản trị kinh doanh',
      tohop: 'A01',
      phuongthuc: 'PT1',
      diem_xt: 24.5,
      result: 'Rớt'
    },
    // student B
    {
      id: 3,
      cccd: '001204000002',
      order: 1,
      code: '7480201',
      major: 'Công nghệ thông tin',
      tohop: 'A01',
      phuongthuc: 'PT1',
      diem_xt: 24.0,
      result: 'Rớt'
    },
    {
      id: 4,
      cccd: '001204000002',
      order: 2,
      code: '7520101',
      major: 'Khoa học máy tính',
      tohop: 'A01',
      phuongthuc: 'PT1',
      diem_xt: 25.5,
      result: 'Chờ xét'
    }
    // student C has no preferences (Chưa xét tuyển)
  ]
};

function toDdMmYyyy(s) {
  if (!s) return '';
  s = String(s).trim();
  if (/^\d{8}$/.test(s)) return s;
  const parts = s.split(/[\/\.\-]/);
  if (parts.length === 3) {
    if (parts[0].length === 4) {
      // yyyy-mm-dd
      const y = parts[0], m = parts[1].padStart(2, '0'), d = parts[2].padStart(2, '0');
      return d + m + y;
    } else {
      const d = parts[0].padStart(2, '0'), m = parts[1].padStart(2, '0'), y = parts[2];
      return d + m + y;
    }
  }
  const digits = s.replace(/\D/g, '');
  return digits;
}

export function findStudentByCredentials(keyword, password) {
  if (!keyword) return null;
  const s = DEFAULT_STATE.students.find((x) => x.cccd === keyword || x.sbd === keyword);
  if (password !== undefined && password !== null && password !== '') {
    if (!s) return null;
    const pwd = String(s.password || '');
    const fromDate = toDdMmYyyy(s.ngay_sinh || '');
    if (password === pwd || password === fromDate) return s;
    return null;
  }
  return s || null;
}

export function listPreferencesByStudent(cccd) {
  return DEFAULT_STATE.preferences.filter((p) => p.cccd === cccd).sort((a, b) => a.order - b.order);
}

export function listMajors() {
  return DEFAULT_STATE.majors.slice();
}

// utility to add a preference (for UI testing)
export function addPreference(pref) {
  const id = DEFAULT_STATE.preferences.reduce((m, p) => Math.max(m, p.id || 0), 0) + 1;
  const item = Object.assign({ id }, pref);
  DEFAULT_STATE.preferences.push(item);
  return item;
}

export function clearState() {
  // for tests: keep the default as-is
}

