export const SCORE_LIMITS = {
  DGNL_MAX: 1200,
  VSAT_MAX: 150,
  THPT_MAX: 10,
  BASE_MAX: 30,
  UT_THRESHOLD: 22.5,
  UT_DIVISOR: 7.5,
};

export const METHOD_LABELS = {
  PT1: "THPT",
  PT2: "DGNL",
  PT3: "VSAT",
};

export const PRIORITY_GROUPS = [
  { value: "", label: "Không", points: 0 },
  { value: "01", label: "Đối tượng 01 (+2.0)", points: 2.0 },
  { value: "02", label: "Đối tượng 02 (+1.5)", points: 1.5 },
  { value: "03", label: "Đối tượng 03 (+1.0)", points: 1.0 },
  { value: "04", label: "Đối tượng 04 (+0.5)", points: 0.5 },
];

export const REGIONS = [
  { value: "", label: "Không", points: 0 },
  { value: "KV1", label: "KV1 (+0.75)", points: 0.75 },
  { value: "KV2-NT", label: "KV2-NT (+0.5)", points: 0.5 },
  { value: "KV2", label: "KV2 (+0.25)", points: 0.25 },
  { value: "KV3", label: "KV3 (+0)", points: 0 },
];

export const SUBJECT_LABELS = {
  TO: "Toán",
  LI: "Lý",
  HO: "Hóa",
  SI: "Sinh",
  SU: "Sử",
  DI: "Địa",
  VA: "Văn",
  N1: "Anh",
  TI: "Tin",
  KTPL: "KTPL",
};

export const SUBJECT_LIST = [
  "TO",
  "LI",
  "HO",
  "SI",
  "SU",
  "DI",
  "VA",
  "N1",
  "TI",
  "KTPL",
];
