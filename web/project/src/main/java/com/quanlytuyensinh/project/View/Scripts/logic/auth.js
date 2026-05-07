import { api } from "../api/client.js";
import { normalizeDobToKey } from "../utils/format.js";

export async function authenticate(cccd, dobInput) {
  const candidate = await api.getThiSinhByCccd(cccd);
  if (!candidate) {
    return null;
  }

  const dobKey = normalizeDobToKey(candidate.ngaySinh);
  if (dobKey !== dobInput) {
    return null;
  }

  return candidate;
}
