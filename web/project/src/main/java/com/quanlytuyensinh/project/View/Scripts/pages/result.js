import { store } from "../core/store.js";
import { buildResultView } from "../logic/result.js";
import { renderComponent } from "../ui/components.js";
import { formatFullName, formatScore } from "../utils/format.js";
import { qs, setText } from "../utils/dom.js";
import { getPriorityPoints } from "../logic/score.js";

export async function initResultPage() {
  const state = store.getState();
  if (!state.session) {
    return;
  }

  const candidate = state.session;
  const summarySlot = qs('[data-slot="result-summary"]');
  const emptySlot = qs('[data-slot="result-empty"]');
  const tableBody = qs("#nvTableBody");

  if (summarySlot) {
    summarySlot.innerHTML = await renderComponent("loading", {
      message: "Đang tải dữ liệu...",
    });
  }
  if (emptySlot) {
    emptySlot.innerHTML = "";
  }
  if (tableBody) {
    tableBody.innerHTML = "";
  }

  let view;
  try {
    view = await buildResultView(candidate.cccd);
  } catch (error) {
    if (summarySlot) {
      summarySlot.innerHTML = await renderComponent("alert", {
        type: "danger",
        title: "Không thể tải dữ liệu",
        message: "Vui lòng thử lại sau hoặc liên hệ phòng đào tạo.",
      });
    }
    return;
  }

  setText(qs('[data-field="fullName"]'), formatFullName(candidate.ho, candidate.ten));
  setText(qs('[data-field="cccd"]'), candidate.cccd || "-");
  setText(qs('[data-field="sbd"]'), candidate.sobaodanh || "-");
  setText(qs('[data-field="dob"]'), candidate.ngaySinh || "-");
  setText(qs('[data-field="email"]'), candidate.email || "-");
  setText(qs('[data-field="noiSinh"]'), candidate.noiSinh || "-");
  setText(qs('[data-field="doiTuong"]'), candidate.doiTuong || "-");
  setText(qs('[data-field="khuVuc"]'), candidate.khuVuc || "-");

  const priorityPoints = getPriorityPoints(candidate.doiTuong, candidate.khuVuc);
  setText(qs('[data-field="diemUuTien"]'), formatScore(priorityPoints, 2));

  if (summarySlot) {
    summarySlot.innerHTML = "";
  }

  if (view.status === "NOT_FOUND") {
    if (summarySlot) {
      summarySlot.innerHTML = await renderComponent("result", {
        tone: "warning",
        status: "Không có dữ liệu",
        title: "Chưa có kết quả",
        message: "Kết quả sẽ được cập nhật sau khi hoàn tất quy trình xét tuyển.",
        major: "-",
        score: "-",
        tohop: "-",
        method: "-",
      });
    }

    if (emptySlot) {
      emptySlot.innerHTML = await renderComponent("empty", {
        icon: "?",
        title: "Không tìm thấy",
        message: "Chưa có nguyện vọng hoặc kết quả chưa được cập nhật.",
        actionHref: "#/tinh-diem",
        actionLabel: "Thử công cụ tính điểm",
      });
    }

    return;
  }

  const topPref = view.preferences[0];
  const admitted = view.admitted;

  if (summarySlot) {
    if (admitted) {
      summarySlot.innerHTML = await renderComponent("result", {
        tone: "success",
        status: "Trúng tuyển",
        title: admitted.majorName,
        message: "Bạn đã được xét trúng tuyển theo nguyện vọng ưu tiên cao nhất.",
        major: admitted.majorName,
        score: formatScore(admitted.diemXetTuyen, 2),
        tohop: admitted.toHopLabel,
        method: admitted.methodLabel,
      });
    } else {
      summarySlot.innerHTML = await renderComponent("result", {
        tone: "warning",
        status: "Không trúng tuyển",
        title: topPref.majorName,
        message: "Điểm hiện tại chưa đạt ngưỡng trúng tuyển.",
        major: topPref.majorName,
        score: formatScore(topPref.diemXetTuyen, 2),
        tohop: topPref.toHopLabel,
        method: topPref.methodLabel,
      });
    }
  }

  if (tableBody) {
    tableBody.innerHTML = view.preferences
      .map((pref) => {
        const rowClass = pref.ketQua === "TRUNG TUYEN" ? "highlight" : "";
        const statusLabel = pref.ketQua === "TRUNG TUYEN" ? "Trúng tuyển" : "Không đạt";
        return `
          <tr class="${rowClass}">
            <td>${pref.thuTuNV}</td>
            <td>${pref.majorName}</td>
            <td>${pref.toHopLabel}</td>
            <td>${pref.methodLabel}</td>
            <td>${formatScore(pref.diemXetTuyen, 2)}</td>
            <td>${statusLabel}</td>
          </tr>
        `;
      })
      .join("");
  }
}
