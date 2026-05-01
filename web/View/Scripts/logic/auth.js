import { candidates } from "../data/mock-data.js";
import { normalizeDobToKey } from "../utils/format.js";

export function authenticate(cccd, dobInput) {
  const candidate = candidates.find((item) => item.cccd === cccd);
  if (!candidate) {
    return null;
  }

  const dobKey = normalizeDobToKey(candidate.ngaySinh);
  if (dobKey !== dobInput) {
    return null;
  }

  return candidate;
}
