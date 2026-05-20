import { SCORE_LIMITS, PRIORITY_GROUPS, REGIONS } from "../data/constants.js";

const groupMap = new Map(PRIORITY_GROUPS.map((group) => [group.value, group]));
const regionMap = new Map(REGIONS.map((region) => [region.value, region]));

const ROUND_DIGITS = 2;

function round(value, digits = ROUND_DIGITS) {
  if (value === null || value === undefined || Number.isNaN(value)) {
    return null;
  }
  const factor = 10 ** digits;
  return Math.round(value * factor) / factor;
}

export function getPriorityPoints(groupValue, regionValue) {
  const group = groupMap.get(groupValue);
  const region = regionMap.get(regionValue);
  const groupPoints = group ? group.points : 0;
  const regionPoints = region ? region.points : 0;
  return round(groupPoints + regionPoints, 2) || 0;
}

export function adjustPriorityPoints(baseScore, utGoc) {
  if (baseScore >= SCORE_LIMITS.UT_THRESHOLD) {
    const adjusted =
      ((SCORE_LIMITS.BASE_MAX - baseScore) / SCORE_LIMITS.UT_DIVISOR) * utGoc;
    return round(Math.max(0, adjusted), 2) || 0;
  }
  return round(utGoc, 2) || 0;
}

export function convertDgnlScore(rawScore, toHop, ranges = []) {
  if (rawScore === null || rawScore === undefined || Number.isNaN(rawScore)) {
    return null;
  }

  const match = ranges.find(
    (row) =>
      row.phuongThuc === "DGNL" &&
      row.toHop === toHop &&
      rawScore >= row.diemA &&
      rawScore <= row.diemB
  );

  if (match) {
    const span = match.diemB - match.diemA;
    const ratio = span > 0 ? (rawScore - match.diemA) / span : 0;
    const converted = match.diemC + ratio * (match.diemD - match.diemC);
    return round(converted, 2);
  }

  const fallback = (rawScore / SCORE_LIMITS.DGNL_MAX) * SCORE_LIMITS.BASE_MAX;
  return round(fallback, 2);
}

export function convertVsatScore(rawScore, subject, toHop, ranges = []) {
  if (rawScore === null || rawScore === undefined || Number.isNaN(rawScore)) {
    return null;
  }

  const subjectKey = subject ? subject.trim().toUpperCase() : "";
  if (!subjectKey) {
    return null;
  }

  const toHopKey = toHop ? toHop.trim().toUpperCase() : "";
  let best = null;
  let bestWidth = Number.POSITIVE_INFINITY;

  ranges.forEach((row) => {
    const phuongThuc = row.phuongThuc ? row.phuongThuc.trim().toUpperCase() : "";
    if (phuongThuc && phuongThuc !== "VSAT") {
      return;
    }

    const rowToHop = row.toHop ? row.toHop.trim().toUpperCase() : "";
    if (rowToHop && rowToHop !== toHopKey) {
      return;
    }

    const rowMon = row.mon ? row.mon.trim().toUpperCase() : "";
    if (!rowMon || rowMon !== subjectKey) {
      return;
    }

    const { diemA, diemB, diemC, diemD } = row;
    if (
      diemA === null ||
      diemA === undefined ||
      diemB === null ||
      diemB === undefined ||
      diemC === null ||
      diemC === undefined ||
      diemD === null ||
      diemD === undefined
    ) {
      return;
    }
    if (rawScore < diemA || rawScore > diemB) {
      return;
    }

    const width = diemB - diemA;
    if (width < bestWidth) {
      bestWidth = width;
      best = row;
    }
  });

  if (!best) {
    return null;
  }

  const span = best.diemB - best.diemA;
  const ratio = span > 0 ? (rawScore - best.diemA) / span : 0;
  const clamped = Math.min(1, Math.max(0, ratio));
  const converted = best.diemC + clamped * (best.diemD - best.diemC);
  return round(converted, 2);
}

export function calculateDgnlResult({
  rawScore,
  major,
  groupValue,
  regionValue,
  bonusPoints,
  bangQuyDoi,
}) {
  if (!major || rawScore === null || rawScore === undefined || Number.isNaN(rawScore)) {
    return null;
  }

  const toHop = major.toHopGoc || "";
  const convertedScore = convertDgnlScore(rawScore, toHop, bangQuyDoi || []);
  const baseScore = round((convertedScore || 0) + (bonusPoints || 0), 2) || 0;
  const utGoc = getPriorityPoints(groupValue, regionValue);
  const utAdjusted = adjustPriorityPoints(baseScore, utGoc);
  const finalScore = round(baseScore + utAdjusted, 2) || 0;

  return {
    convertedScore,
    toHop,
    baseScore,
    utGoc,
    utAdjusted,
    finalScore,
    diemSan: major.diemSan,
    diemTrungTuyen: major.diemTrungTuyen,
  };
}

export function calculateThptResults({
  subjectScores,
  rawSubjectScores,
  major,
  groupValue,
  regionValue,
  bonusPoints,
  combos,
  mode,
  bangQuyDoiByToHop,
}) {
  if (!major || !Array.isArray(combos) || combos.length === 0) {
    return [];
  }

  const utGoc = getPriorityPoints(groupValue, regionValue);
  const scoreSource = mode === "VSAT" ? rawSubjectScores || {} : subjectScores || {};

  return combos.map((combo) => {
    const d1Raw = scoreSource[combo.thMon1];
    const d2Raw = scoreSource[combo.thMon2];
    const d3Raw = scoreSource[combo.thMon3];

    const ranges =
      mode === "VSAT" && bangQuyDoiByToHop ? bangQuyDoiByToHop.get(combo.maToHop) || [] : [];

    const d1 = mode === "VSAT" ? convertVsatScore(d1Raw, combo.thMon1, combo.maToHop, ranges) : d1Raw;
    const d2 = mode === "VSAT" ? convertVsatScore(d2Raw, combo.thMon2, combo.maToHop, ranges) : d2Raw;
    const d3 = mode === "VSAT" ? convertVsatScore(d3Raw, combo.thMon3, combo.maToHop, ranges) : d3Raw;

    const hasAll = [d1, d2, d3].every((value) => value !== null && value !== undefined);

    if (!hasAll) {
      return {
        combo,
        baseScore: null,
        utAdjusted: null,
        finalScore: null,
        isComplete: false,
        diemSan: major.diemSan,
      };
    }

    const hs1 = combo.hsMon1 || 1;
    const hs2 = combo.hsMon2 || 1;
    const hs3 = combo.hsMon3 || 1;
    const heSoTotal = hs1 + hs2 + hs3;

    const sumWeighted = d1 * hs1 + d2 * hs2 + d3 * hs3;
    const baseScore =
      (sumWeighted * SCORE_LIMITS.BASE_MAX) / (heSoTotal * SCORE_LIMITS.THPT_MAX) +
      (combo.dolech || 0);

    const baseWithBonus = baseScore + (bonusPoints || 0);
    const utAdjusted = adjustPriorityPoints(baseWithBonus, utGoc);
    const finalScore = baseWithBonus + utAdjusted;

    return {
      combo,
      baseScore: round(baseScore, 2),
      utAdjusted,
      finalScore: round(finalScore, 2),
      isComplete: true,
      diemSan: major.diemSan,
    };
  });
}
