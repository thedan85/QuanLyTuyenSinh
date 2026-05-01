import { majors, toHopMon } from "../data/mock-data.js";
import {
  PRIORITY_GROUPS,
  REGIONS,
  SUBJECT_LABELS,
  SUBJECT_LIST,
  SCORE_LIMITS,
} from "../data/constants.js";
import { calculateDgnlResult, calculateThptResults } from "../logic/score.js";
import { formatScore } from "../utils/format.js";
import { qs, qsa, setText } from "../utils/dom.js";
import { renderComponent } from "../ui/components.js";

const toHopMap = new Map(toHopMon.map((item) => [item.maToHop, item]));

export async function initCalculatorPage() {
  const dgnlMajor = qs("#dgnlMajor");
  const thptMajor = qs("#thptMajor");
  const dgnlGroup = qs("#dgnlGroup");
  const dgnlRegion = qs("#dgnlRegion");
  const thptGroup = qs("#thptGroup");
  const thptRegion = qs("#thptRegion");
  const thptBonusSubject = qs("#thptBonusSubject");

  const modalRoot = document.getElementById("app-modal-root");
  if (modalRoot) {
    modalRoot.innerHTML = await renderComponent("modal", {
      id: "priorityModal",
      title: "Công thức ưu tiên",
      body:
        "<p>Nếu tổng điểm (chưa ưu tiên) >= 22.5, điểm ưu tiên sẽ giảm theo công thức:</p>" +
        "<p><strong>Ưu tiên điều chỉnh = ((30 - tổng) / 7.5) * ưu tiên gốc</strong></p>" +
        "<p>Nếu tổng < 22.5, điểm ưu tiên không thay đổi.</p>",
    });
  }

  populateSelect(dgnlMajor, majors, (item) => `${item.maNganh} - ${item.tenNganh}`);
  populateSelect(thptMajor, majors, (item) => `${item.maNganh} - ${item.tenNganh}`);
  populateSelect(dgnlGroup, PRIORITY_GROUPS, (item) => item.label);
  populateSelect(thptGroup, PRIORITY_GROUPS, (item) => item.label);
  populateSelect(dgnlRegion, REGIONS, (item) => item.label);
  populateSelect(thptRegion, REGIONS, (item) => item.label);
  populateSelect(thptBonusSubject, buildBonusSubjects(), (item) => item.label);

  bindDgnlEvents();
  bindThptEvents();
  updateDgnl();
  updateThpt();
}

function populateSelect(select, items, labelBuilder) {
  if (!select) {
    return;
  }
  const options = ["<option value=\"\">Chọn</option>"];
  items.forEach((item) => {
    const label = labelBuilder(item);
    const value = item.value ?? item.maNganh ?? item.code ?? "";
    options.push(`<option value=\"${value}\">${label}</option>`);
  });
  select.innerHTML = options.join("");
}

function buildBonusSubjects() {
  return [
    { value: "", label: "Không" },
    ...SUBJECT_LIST.map((code) => ({
      value: code,
      label: SUBJECT_LABELS[code] || code,
    })),
  ];
}

function bindDgnlEvents() {
  const form = qs("#dgnlForm");
  if (!form) {
    return;
  }
  form.addEventListener("input", updateDgnl);
  form.addEventListener("change", updateDgnl);
}

function bindThptEvents() {
  const form = qs("#thptForm");
  if (!form) {
    return;
  }
  form.addEventListener("input", updateThpt);
  form.addEventListener("change", updateThpt);
}

function readNumber(value) {
  if (value === "" || value === null || value === undefined) {
    return null;
  }
  const parsed = Number(value);
  return Number.isNaN(parsed) ? null : parsed;
}

function updateDgnl() {
  const rawScore = readNumber(qs("#dgnlScore")?.value);
  const majorCode = qs("#dgnlMajor")?.value || "";
  const groupValue = qs("#dgnlGroup")?.value || "";
  const regionValue = qs("#dgnlRegion")?.value || "";
  const bonusPoints = readNumber(qs("#dgnlBonus")?.value) || 0;

  const result = calculateDgnlResult({
    rawScore,
    majorCode,
    groupValue,
    regionValue,
    bonusPoints,
  });

  if (!result) {
    setText(qs('[data-field="dgnlConverted"]'), "-");
    setText(qs('[data-field="dgnlToHop"]'), "-");
    setText(qs('[data-field="dgnlPriority"]'), "-");
    setText(qs('[data-field="dgnlFinal"]'), "-");
    setText(qs('[data-field="dgnlVsDiemSan"]'), "-");
    setText(qs('[data-field="dgnlVsDiemTrungTuyen"]'), "-");
    setText(qs('[data-field="dgnlNote"]'), "Nhập thông tin để xem kết quả.");
    return;
  }

  setText(qs('[data-field="dgnlConverted"]'), formatScore(result.convertedScore, 2));
  setText(qs('[data-field="dgnlToHop"]'), result.toHop || "-");
  setText(qs('[data-field="dgnlPriority"]'), formatScore(result.utAdjusted, 2));
  setText(qs('[data-field="dgnlFinal"]'), formatScore(result.finalScore, 2));

  const passSan = result.finalScore >= result.diemSan;
  setText(
    qs('[data-field="dgnlVsDiemSan"]'),
    passSan ? `Đạt (>= ${formatScore(result.diemSan, 2)})` : `Không đạt (< ${formatScore(result.diemSan, 2)})`
  );

  if (result.diemTrungTuyen && result.diemTrungTuyen > 0) {
    const passTrungTuyen = result.finalScore >= result.diemTrungTuyen;
    setText(
      qs('[data-field="dgnlVsDiemTrungTuyen"]'),
      passTrungTuyen
        ? `Đạt (>= ${formatScore(result.diemTrungTuyen, 2)})`
        : `Không đạt (< ${formatScore(result.diemTrungTuyen, 2)})`
    );
  } else {
    setText(qs('[data-field="dgnlVsDiemTrungTuyen"]'), "Chưa công bố");
  }

  setText(qs('[data-field="dgnlNote"]'), "Điểm quy đổi đã bao gồm điểm cộng và ưu tiên điều chỉnh.");
}

function updateThpt() {
  const majorCode = qs("#thptMajor")?.value || "";
  const groupValue = qs("#thptGroup")?.value || "";
  const regionValue = qs("#thptRegion")?.value || "";
  const bonusPoints = readNumber(qs("#thptBonus")?.value) || 0;

  const mode = qs('input[name="thptMode"]:checked')?.value || "THPT";
  const scaleDivider = mode === "VSAT" ? SCORE_LIMITS.VSAT_MAX / SCORE_LIMITS.THPT_MAX : 1;

  const modeNote = qs("#thptNote");
  if (modeNote) {
    modeNote.textContent =
      mode === "VSAT"
        ? "Chế độ VSAT: hệ thống quy đổi về thang 10."
        : "Chế độ THPT: nhập điểm theo thang 10.";
  }

  const subjectScores = collectSubjectScores(scaleDivider);

  const results = calculateThptResults({
    subjectScores,
    majorCode,
    groupValue,
    regionValue,
    bonusPoints,
  });

  updateConvertedSummary(mode, subjectScores, scaleDivider);
  renderThptTable(results);
}

function collectSubjectScores(scaleDivider) {
  const scores = {};
  SUBJECT_LIST.forEach((code) => {
    scores[code] = null;
  });

  const rawScores = {
    TO: readNumber(qs("#scoreTo")?.value),
    LI: readNumber(qs("#scoreLi")?.value),
    HO: readNumber(qs("#scoreHo")?.value),
    SI: readNumber(qs("#scoreSi")?.value),
    SU: readNumber(qs("#scoreSu")?.value),
    DI: readNumber(qs("#scoreDi")?.value),
    VA: readNumber(qs("#scoreVa")?.value),
  };

  const anhThi = readNumber(qs("#scoreAnh")?.value);
  const anhQuyDoi = readNumber(qs("#scoreAnhQD")?.value);
  const n1Raw = Math.max(anhThi || 0, anhQuyDoi || 0);
  const hasN1 = anhThi !== null || anhQuyDoi !== null;

  Object.entries(rawScores).forEach(([code, value]) => {
    if (value === null || value === undefined) {
      scores[code] = null;
      return;
    }
    scores[code] = value / scaleDivider;
  });

  if (hasN1) {
    scores.N1 = n1Raw / scaleDivider;
  }

  return scores;
}

function updateConvertedSummary(mode, subjectScores, scaleDivider) {
  const summary = qs("#thptConvertedSummary");
  if (!summary) {
    return;
  }

  if (mode !== "VSAT") {
    summary.textContent = "Nhập điểm theo thang 10 cho từng môn.";
    return;
  }

  const pieces = SUBJECT_LIST.filter((code) => subjectScores[code] !== null).map((code) => {
    const label = SUBJECT_LABELS[code] || code;
    return `${label}: ${formatScore(subjectScores[code], 2)}`;
  });

  if (pieces.length === 0) {
    summary.textContent = "Nhập điểm VSAT để hệ thống quy đổi về thang 10.";
    return;
  }

  summary.textContent = `Quy đổi thang 10: ${pieces.join(", ")}`;
}

function renderThptTable(results) {
  const tableBody = qs("#thptTableBody");
  if (!tableBody) {
    return;
  }

  if (results.length === 0) {
    tableBody.innerHTML = "<tr><td colspan=\"5\">Chọn ngành để xem kết quả.</td></tr>";
    return;
  }

  tableBody.innerHTML = results
    .map((item) => {
      const tohop = toHopMap.get(item.combo.maToHop);
      const tohopLabel = tohop ? tohop.tenToHop : item.combo.maToHop;
      const baseScore = item.baseScore !== null ? formatScore(item.baseScore, 2) : "-";
      const utScore = item.utAdjusted !== null ? formatScore(item.utAdjusted, 2) : "-";
      const finalScore = item.finalScore !== null ? formatScore(item.finalScore, 2) : "-";
      const passSan =
        item.finalScore !== null && item.finalScore >= item.diemSan
          ? "Đạt"
          : "Không đạt";
      const rowClass = item.isComplete ? "" : "muted";

      return `
        <tr class="${rowClass}">
          <td>${item.combo.maToHop} - ${tohopLabel}</td>
          <td>${baseScore}</td>
          <td>${utScore}</td>
          <td>${finalScore}</td>
          <td>${item.isComplete ? passSan : "Chưa đủ dữ liệu"}</td>
        </tr>
      `;
    })
    .join("");
}
