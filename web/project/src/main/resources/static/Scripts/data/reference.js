import { api } from "../api/client.js";

let majorsCache = null;
let majorsMapCache = null;
let toHopMapCache = null;
const nganhToHopCache = new Map();
const bangQuyDoiCache = new Map();
const bangQuyDoiPhuongThucCache = new Map();

function normalizeMajor(item) {
  return {
    maNganh: item.manganh ?? item.maNganh ?? "",
    tenNganh: item.tennganh ?? item.tenNganh ?? "",
    toHopGoc: item.nTohopgoc ?? item.toHopGoc ?? "",
    chiTieu: item.nChitieu ?? item.chiTieu ?? 0,
    diemSan: item.nDiemsan ?? item.diemSan ?? 0,
    diemTrungTuyen: item.nDiemtrungtuyen ?? item.diemTrungTuyen ?? 0,
  };
}

function normalizeToHopMon(item) {
  return {
    maToHop: item.matohop ?? item.maToHop ?? "",
    mon1: item.mon1 ?? "",
    mon2: item.mon2 ?? "",
    mon3: item.mon3 ?? "",
    tenToHop: item.tentohop ?? item.tenToHop ?? item.matohop ?? "",
  };
}

export async function getMajors() {
  if (!majorsCache) {
    const data = await api.getNganhs();
    majorsCache = data.map(normalizeMajor);
  }
  return majorsCache;
}

export async function getMajorMap() {
  if (!majorsMapCache) {
    const majors = await getMajors();
    majorsMapCache = new Map(majors.map((major) => [major.maNganh, major]));
  }
  return majorsMapCache;
}

export async function getToHopMap() {
  if (!toHopMapCache) {
    const data = await api.getToHopMon();
    const normalized = data.map(normalizeToHopMon);
    toHopMapCache = new Map(normalized.map((item) => [item.maToHop, item]));
  }
  return toHopMapCache;
}

export async function getNganhToHopByMaNganh(maNganh) {
  if (!maNganh) {
    return [];
  }
  if (nganhToHopCache.has(maNganh)) {
    return nganhToHopCache.get(maNganh);
  }
  const data = await api.getNganhToHopByMaNganh(maNganh);
  nganhToHopCache.set(maNganh, data);
  return data;
}

export async function getBangQuyDoiByToHop(toHop) {
  if (!toHop) {
    return [];
  }
  if (bangQuyDoiCache.has(toHop)) {
    return bangQuyDoiCache.get(toHop);
  }
  const data = await api.getBangQuyDoiByToHop(toHop);
  bangQuyDoiCache.set(toHop, data);
  return data;
}

export async function getBangQuyDoiByPhuongThuc(phuongThuc, toHop) {
  if (!phuongThuc || !toHop) {
    return [];
  }
  const key = `${phuongThuc}::${toHop}`;
  if (bangQuyDoiPhuongThucCache.has(key)) {
    return bangQuyDoiPhuongThucCache.get(key);
  }
  const data = await api.getBangQuyDoiByPhuongThuc(phuongThuc, toHop);
  bangQuyDoiPhuongThucCache.set(key, data);
  return data;
}

export function clearReferenceCache() {
  majorsCache = null;
  majorsMapCache = null;
  toHopMapCache = null;
  nganhToHopCache.clear();
  bangQuyDoiCache.clear();
  bangQuyDoiPhuongThucCache.clear();
}
