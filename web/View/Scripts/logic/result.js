import { majors, toHopMon, nguyenVong } from "../data/mock-data.js";
import { METHOD_LABELS } from "../data/constants.js";

const majorMap = new Map(majors.map((major) => [major.maNganh, major]));
const toHopMap = new Map(toHopMon.map((tohop) => [tohop.maToHop, tohop]));

export function buildResultView(cccd) {
  const preferences = nguyenVong
    .filter((nv) => nv.tsCccd === cccd)
    .sort((a, b) => a.thuTuNV - b.thuTuNV)
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

  const admitted = preferences.find((pref) => pref.ketQua === "TRUNG TUYEN") || null;

  return {
    status: admitted ? "ADMITTED" : "NOT_ADMITTED",
    admitted,
    preferences,
  };
}
