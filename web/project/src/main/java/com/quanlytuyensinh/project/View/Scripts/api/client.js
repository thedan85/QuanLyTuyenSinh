const API_BASE = "/api";

async function requestJson(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      Accept: "application/json",
      ...(options.headers || {}),
    },
    ...options,
  });

  if (response.status === 404) {
    return null;
  }

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || `Request failed: ${response.status}`);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

async function requestList(path) {
  const data = await requestJson(path);
  return Array.isArray(data) ? data : [];
}

export const api = {
  getThiSinhByCccd(cccd) {
    return requestJson(`/thi-sinh/cccd/${encodeURIComponent(cccd)}`);
  },
  getNganhs() {
    return requestList("/nganh");
  },
  getNganhByMa(maNganh) {
    return requestJson(`/nganh/ma/${encodeURIComponent(maNganh)}`);
  },
  getNguyenVongByCccd(cccd) {
    return requestList(`/nguyen-vong/cccd/${encodeURIComponent(cccd)}`);
  },
  getToHopMon() {
    return requestList("/to-hop");
  },
  getNganhToHopByMaNganh(maNganh) {
    return requestList(`/nganh-tohop/ma-nganh/${encodeURIComponent(maNganh)}`);
  },
  getBangQuyDoiByToHop(toHop) {
    return requestList(`/bang-quy-doi/to-hop/${encodeURIComponent(toHop)}`);
  },
  getBangQuyDoiByPhuongThuc(phuongThuc, toHop) {
    return requestList(
      `/bang-quy-doi/phuong-thuc/${encodeURIComponent(phuongThuc)}/to-hop/${encodeURIComponent(toHop)}`
    );
  },
};
