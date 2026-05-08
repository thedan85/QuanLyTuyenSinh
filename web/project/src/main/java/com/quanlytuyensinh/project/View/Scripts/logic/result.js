import { api } from "../api/client.js";
import { METHOD_LABELS } from "../data/constants.js";
import { getMajors, getToHopMap } from "../data/reference.js";

function normalizeText(value) {
  if (!value) {
    return "";
  }
  return value
    .toString()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toUpperCase();
}

export function isAdmitted(ketQua) {
  const normalized = normalizeText(ketQua);
  return normalized.includes("TRUNG") || normalized.includes("DAU");
}

export async function buildResultView(cccd) {
  const [majors, toHopMap, preferencesRaw] = await Promise.all([
    getMajors(),
    getToHopMap(),
    api.getNguyenVongByCccd(cccd),
  ]);

  const majorMap = new Map(majors.map((major) => [major.maNganh, major]));
  const preferences = (preferencesRaw || [])
    .slice()
    .sort((a, b) => (a.thuTuNV || 0) - (b.thuTuNV || 0))
    .map((nv) => {
      const major = majorMap.get(nv.maNganh);
      const tohop = toHopMap.get(nv.maToHop);
      return {
        ...nv,
        majorName: major ? major.tenNganh : nv.maNganh,
        toHopLabel: tohop ? tohop.tenToHop : nv.maToHop,
        methodLabel: METHOD_LABELS[nv.phuongThuc] || nv.phuongThuc,
      };
    });

  if (preferences.length === 0) {
    return {
      status: "NOT_FOUND",
      admitted: null,
      preferences: [],
    };
  }

  const admitted = preferences.find((pref) => isAdmitted(pref.ketQua)) || null;

  return {
    status: admitted ? "ADMITTED" : "NOT_ADMITTED",
    admitted,
    preferences,
  };
}
