import { SCORE_LIMITS, PRIORITY_GROUPS, REGIONS } from "../data/constants.js";
import { majors, nganhToHop, bangQuyDoi } from "../data/mock-data.js";

const groupMap = new Map(PRIORITY_GROUPS.map((group) => [group.value, group]));
const regionMap = new Map(REGIONS.map((region) => [region.value, region]));
const majorMap = new Map(majors.map((major) => [major.maNganh, major]));

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

export function convertDgnlScore(rawScore, toHop) {
  if (rawScore === null || rawScore === undefined || Number.isNaN(rawScore)) {
    return null;
  }

  const ranges = bangQuyDoi.filter(
    (row) => row.phuongThuc === "DGNL" && row.toHop === toHop
  );

  const match = ranges.find((row) => rawScore >= row.diemA && rawScore <= row.diemB);

  if (match) {
    const span = match.diemB - match.diemA;
    const ratio = span > 0 ? (rawScore - match.diemA) / span : 0;
    const converted = match.diemC + ratio * (match.diemD - match.diemC);
    return round(converted, 2);
  }

  const fallback = (rawScore / SCORE_LIMITS.DGNL_MAX) * SCORE_LIMITS.BASE_MAX;
  return round(fallback, 2);
}

export function calculateDgnlResult({
  rawScore,
  majorCode,
  groupValue,
  regionValue,
  bonusPoints,
}) {
  const major = majorMap.get(majorCode);
  if (!major || rawScore === null || rawScore === undefined || Number.isNaN(rawScore)) {
    return null;
  }

  const convertedScore = convertDgnlScore(rawScore, major.toHopGoc);
  const baseScore = round((convertedScore || 0) + (bonusPoints || 0), 2) || 0;
  const utGoc = getPriorityPoints(groupValue, regionValue);
  const utAdjusted = adjustPriorityPoints(baseScore, utGoc);
  const finalScore = round(baseScore + utAdjusted, 2) || 0;

  return {
    convertedScore,
    toHop: major.toHopGoc,
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
  majorCode,
  groupValue,
  regionValue,
  bonusPoints,
}) {
  const major = majorMap.get(majorCode);
  if (!major) {
    return [];
  }

  const combos = nganhToHop.filter((combo) => combo.maNganh === majorCode);
  const utGoc = getPriorityPoints(groupValue, regionValue);

  return combos.map((combo) => {
    const d1 = subjectScores[combo.thMon1];
    const d2 = subjectScores[combo.thMon2];
    const d3 = subjectScores[combo.thMon3];

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
