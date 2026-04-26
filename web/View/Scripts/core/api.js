// Thin API layer - uses in-memory mock state for now
import { findStudentByCredentials, listPreferencesByStudent, listMajors } from './state.js';

function wait(ms = 180) {
  return new Promise((res) => setTimeout(res, ms));
}

export async function loginStudent({ cccd, password }) {
  await wait(240);
  const student = findStudentByCredentials(cccd, password);
  if (!student) throw new Error('CCCD hoặc mật khẩu không đúng.');
  sessionStorage.setItem('ts_session_cccd', student.cccd);
  return student;
}

export async function logoutStudent() {
  await wait(80);
  sessionStorage.removeItem('ts_session_cccd');
}

export function getLoggedStudent() {
  const cccd = sessionStorage.getItem('ts_session_cccd');
  if (!cccd) return null;
  return findStudentByCredentials(cccd);
}

export async function fetchQuickLookup(keyword, password) {
  await wait(200);
  const student = findStudentByCredentials(keyword, password);
  if (!student) return null;
  const prefs = listPreferencesByStudent(student.cccd);
  return { student, preferences: prefs };
}

export async function fetchStudentPreferences(studentCccd) {
  await wait(130);
  return listPreferencesByStudent(studentCccd);
}

export async function fetchMajors() {
  await wait(120);
  return listMajors();
}
