"""
tinh_diem_nguyenvong.py
-----------------------
Tính phương thức (PT1/PT2/PT3) và tổ hợp môn tối ưu cho từng nguyện vọng
dựa trên điểm xét tuyển cao nhất (bao gồm điểm cộng + điểm ưu tiên).

Công thức:
  diem_xettuyen = diem_thxt + min(diem_cc, 3.0) + diem_utxt
  (diem_utxt chưa quy đổi — app Java tự quy đổi theo công thức 22.5)

Output:
  - update_nguyenvong.sql  : UPDATE tt_phuongthuc, tt_thm, diem_thxt vào bảng NV đã có
  - insert_diemcong.sql    : INSERT điểm cộng cho PT+tổ hợp tốt nhất của từng NV

Cách dùng:
  python tinh_diem_nguyenvong.py

File đầu vào (để cùng thư mục hoặc chỉnh DATA_DIR):
  - diem-thi-import.csv
  - Diem_DGNL_VSAT_-_0908_-_DGNL.csv
  - Diem_DGNL_VSAT_-_0908_-_VSAT.csv
  - tohopmon_xlsx_-_Sheet1.csv
  - data_bangquydoi.sql
  - Nguyenvong.xlsx_-_Sheet2.csv
  - Ds_thi_sinh_xlsx_-_Sheet1__1_.csv
  - Ds_quy_doi_tieng_Anh_xlsx_-_import_xettuyen.csv
  - Uu_tien_xet_tuyen_xlsx_-_ds_thi_sinh.csv
"""

import re
import pandas as pd

# ============================================================
# CẤU HÌNH
# ============================================================
DATA_DIR        = "./"
DB_NAME         = "xettuyen2026"
BATCH_SIZE      = 500
OUT_NV          = "update_nguyenvong.sql"    # chỉnh đường dẫn output nếu cần
OUT_DIEMCONG    = "insert_diemcong.sql"      # chỉnh đường dẫn output nếu cần

FILE_DIEM   = DATA_DIR + "diem-thi-import.csv"
FILE_DGNL   = DATA_DIR + "Diem_DGNL_VSAT_-_0908_-_DGNL.csv"
FILE_VSAT   = DATA_DIR + "Diem_DGNL_VSAT_-_0908_-_VSAT.csv"
FILE_TOHOP  = DATA_DIR + "tohopmon_xlsx_-_Sheet1.csv"
FILE_BQD    = DATA_DIR + "data_bangquydoi.sql"
FILE_NV     = DATA_DIR + "Nguyenvong.xlsx_-_Sheet2.csv"
FILE_TS     = DATA_DIR + "Ds_thi_sinh_xlsx_-_Sheet1__1_.csv"
FILE_TA     = DATA_DIR + "Ds_quy_doi_tieng_Anh_xlsx_-_import_xettuyen.csv"
FILE_GIAI   = DATA_DIR + "Uu_tien_xet_tuyen_xlsx_-_ds_thi_sinh.csv"

# Điểm khu vực
KV_DIEM = {'1': 0.75, '2NT': 0.50, '2': 0.25, '3': 0.00}

# Mapping tên ngành không khớp -> mã ngành
MANUAL_NAME_MAP = {
    'Ngôn ngữ Anh CLC': '7220201CLC',
    'Ngôn ngữ Anh (CLC)': '7220201CLC',
    'Sư phạm Lịch sử-Địa lý': '7140249',
    'Sư phạm Lịch sử - Địa lí': '7140249',
    'Sư phạm Lịch sử-Địa lí': '7140249',
    'Sư phạm Lịch sử- Địa lí': '7140249',
    'Sư phạm Địa lí': '7140219',
    'Tâm lí học': '7310401',
    'Địa lí học': '7310501',
    'GD Chính trị': '7140205',
    'Quản lí GD': '7140114',
    'Sư phạm Vật lí': '7140211',
    'Sư phạm KHTN': '7140247',
    'Sư phạm KHTN (Đào tạo GV THCS) ': '7140247',
    'Toán học Ứng dụng': '7460112',
    'Thông tin Thư viện': '7320201',
    'Sư phạm Hóa học': '7140212',
    'Tài chính Ngân hàng': '7340201',
    'Sư phạmNgữ văn': '7140217',
    'Kỹ thuật Điện tử': '7520207',
    'Sư phạm Sinh': '7140213',
    'Kế Toán học': '7340301',
    'Công nghệ kĩ thuật điện tử viễn thông': '7510302',
    'Kĩ thuật điện tử viễn thông': '7510302',
    'Quản lí giáo dục': '7140114',
    'Quản trị kinh doanh (CLC)': '7340101CLC',
    'Địa lí': '7310501',
    'Quản trị Nhà hàng & DV ăn uống': '7810202',
}

# Map môn VSAT -> tên chuẩn
VSAT_MON_MAP = {
    'TO_VS': 'TO', 'M1': 'TO', 'LI_VS': 'LI', 'M2': 'LI',
    'HO_VS': 'HO', 'M3': 'HO', 'SI_VS': 'SI', 'M4': 'SI',
    'SU_VS': 'SU', 'M6': 'SU', 'DI_VS': 'DI', 'M7': 'DI',
    'VA_VS': 'VA', 'N1_VS': 'N1', 'M8': 'N1',
}

# Map môn tổ hợp -> cột THPT
MON_COL_THPT = {
    'TO': 'TO', 'LI': 'LI', 'HO': 'HO', 'SI': 'SI',
    'SU': 'SU', 'DI': 'DI', 'VA': 'VA', 'N1': 'N1_THI',
    'KTPL': 'KTPL', 'TI': 'TI', 'CNCN': 'CNCN', 'CNNN': 'CNNN',
}

# ============================================================
# LOAD DỮ LIỆU
# ============================================================

def load_tohop(filepath):
    df = pd.read_csv(filepath)
    df['MANGANH'] = df['MANGANH'].astype(str).str.strip()
    df['Độ lệch'] = pd.to_numeric(
        df['Độ lệch'].astype(str).str.replace(',', '.'), errors='coerce').fillna(0)
    result = {}
    for _, row in df.iterrows():
        ng = row['MANGANH']
        s  = str(row['MA_TO_HOP'])
        m  = re.match(r'^([^(]+)', s)
        if not m: continue
        ma   = m.group(1).strip()
        mons = {mon: int(h) for mon, h in re.findall(r'([A-Z0-9]+)-(\d+)', s)}
        result.setdefault(ng, []).append({
            'ma': ma, 'mons': mons, 'mons_set': set(mons.keys()),
            'dolech': float(row['Độ lệch']),
            'is_goc': str(row['Gốc']).strip() == 'Gốc',
        })
    return result


def load_bangquydoi(filepath):
    with open(filepath, encoding='utf-8') as f:
        content = f.read()
    pattern = (r"\(\d+,'([^']+)',([^,]+),([^,]+),"
               r"([\d.]+),([\d.]+),([\d.]+),([\d.]+),'[^']+','[^']+'\)")
    bqd_dgnl, bqd_vsat = {}, {}
    for m in re.finditer(pattern, content):
        loai    = m.group(1)
        matohop = m.group(2).strip().strip("'") if m.group(2).strip() != 'NULL' else None
        mamon   = m.group(3).strip().strip("'") if m.group(3).strip() != 'NULL' else None
        entry   = (float(m.group(4)), float(m.group(5)),
                   float(m.group(6)), float(m.group(7)))
        if loai == 'DGNL' and matohop:
            bqd_dgnl.setdefault(matohop, []).append(entry)
        elif loai == 'VSAT' and mamon:
            bqd_vsat.setdefault(mamon, []).append(entry)
    return bqd_dgnl, bqd_vsat


def load_nguyenvong(filepath):
    df = pd.read_csv(filepath, skiprows=4, header=None)
    df.columns = ['STT','cccd','thu_tu_nv','ma_truong',
                  'ten_truong','manganh','ten_nganh','tuyen_thang']
    df = df[df['cccd'].notna() & (df['cccd'] != 'CCCD')].copy()
    df['manganh']    = df['manganh'].astype(str).str.strip()
    df['thu_tu_nv']  = pd.to_numeric(df['thu_tu_nv'], errors='coerce').fillna(1).astype(int)
    return df


def load_thisinh_kvdt(filepath):
    df = pd.read_csv(filepath, low_memory=False)
    kv = dict(zip(df['CCCD'].astype(str), df['KVƯT'].astype(str)))
    dt = dict(zip(df['CCCD'].astype(str), df['ĐTƯT'].astype(str)))
    return kv, dt


def load_tiengAnh(filepath):
    df = pd.read_csv(filepath)
    df['Điểm cộng'] = pd.to_numeric(
        df['Điểm cộng'].astype(str).str.replace(',', '.'), errors='coerce').fillna(0)
    return df.groupby('CCCD')['Điểm cộng'].max().to_dict()


def load_giai(filepath):
    df = pd.read_csv(filepath)
    for col in ['Điểm cộng cho môn đạt giải', 'Điểm cộng cho THXT ko có môn đạt giải']:
        df[col] = pd.to_numeric(
            df[col].astype(str).str.replace(',', '.'), errors='coerce').fillna(0)
    result = {}
    for _, row in df.iterrows():
        cccd = str(row['CCCD']).strip()
        result.setdefault(cccd, []).append({
            'mon'       : str(row['Mã môn']).strip(),
            'diem_co'   : float(row['Điểm cộng cho môn đạt giải']),
            'diem_khong': float(row['Điểm cộng cho THXT ko có môn đạt giải']),
        })
    return result


# ============================================================
# HELPER TÍNH ĐIỂM
# ============================================================

def quy_doi(bang, diem_goc):
    if diem_goc <= 0: return 0.0
    for tu, den, qdTu, qdDen in sorted(bang, key=lambda x: -x[0]):
        if tu <= diem_goc <= den:
            if den == tu: return qdTu
            return qdTu + (qdDen - qdTu) * (diem_goc - tu) / (den - tu)
    max_row = max(bang, key=lambda x: x[1])
    return max_row[3] if diem_goc > max_row[1] else 0.0


def round5(v):
    return round(round(v * 20) / 20, 4)


def tinh_thpt(dt, th):
    tong_hs, tong_diem = sum(th['mons'].values()), 0.0
    for mon, hs in th['mons'].items():
        if mon == 'N1':
            d = max(float(dt.get('N1_THI') or 0), float(dt.get('N1_CC') or 0))
        else:
            col = MON_COL_THPT.get(mon)
            if not col: return 0.0
            d = float(dt.get(col) or 0)
        if d == 0: return 0.0
        tong_diem += d * hs
    return (tong_diem * 30.0 / (tong_hs * 10.0)) + th['dolech']


def tinh_vsat(vsat_mons, th, bqd_vsat):
    tong_hs, tong_diem = sum(th['mons'].values()), 0.0
    for mon, hs in th['mons'].items():
        d_vsat = vsat_mons.get(mon, 0)
        if d_vsat <= 0 or mon not in bqd_vsat: return 0.0
        d = quy_doi(bqd_vsat[mon], d_vsat)
        if d <= 0: return 0.0
        tong_diem += d * hs
    return (tong_diem * 30.0 / (tong_hs * 10.0)) + th['dolech']


def tinh_dgnl(nl1, ma_tohop_goc, bqd_dgnl):
    if nl1 <= 0 or ma_tohop_goc not in bqd_dgnl: return 0.0
    return quy_doi(bqd_dgnl[ma_tohop_goc], nl1)


def tinh_diem_cc(cccd, mons_set, ta_by_cccd, giai_by_cccd):
    """Tính diemCC = TA + giải thưởng (chưa cap, app tự cap 3.0)"""
    diem_ta   = float(ta_by_cccd.get(cccd, 0) or 0)
    diem_giai = 0.0
    for giai in giai_by_cccd.get(cccd, []):
        if giai['mon'] in mons_set:
            diem_giai += giai['diem_co']
        else:
            diem_giai += giai['diem_khong']
    return diem_ta + diem_giai


def tinh_diem_utxt(cccd, kv_by_cccd, dt_by_cccd):
    """Tính diemUtxt = KV + ĐT (gốc, app tự quy đổi)"""
    kv      = str(kv_by_cccd.get(cccd, '3')).strip()
    dt      = str(dt_by_cccd.get(cccd, '')).strip()
    diem_kv = KV_DIEM.get(kv, 0.0)
    if pd.isna(dt) or dt in ('nan', ''):
        diem_dt = 0.0
    elif dt == '01':
        diem_dt = 2.0
    elif dt in ('06a', '07a'):
        diem_dt = 1.0
    else:
        diem_dt = 0.0
    return diem_kv + diem_dt, kv, dt


def ghichu_str(diem_ta, diem_giai, kv, diem_kv, dt, diem_dt):
    parts = []
    if diem_ta   > 0: parts.append(f"TA:{diem_ta}")
    if diem_giai > 0: parts.append(f"Giải:{diem_giai}")
    if diem_kv   > 0: parts.append(f"KV{kv}:{diem_kv}")
    if diem_dt   > 0: parts.append(f"ĐT{dt}:{diem_dt}")
    return ', '.join(parts) or 'Không có điểm cộng'


# ============================================================
# MAIN
# ============================================================

def main():
    print("=== TINH DIEM NGUYEN VONG (có điểm cộng + ưu tiên) ===")
    print("Đang load dữ liệu...")

    tohop_by_nganh      = load_tohop(FILE_TOHOP)
    bqd_dgnl, bqd_vsat = load_bangquydoi(FILE_BQD)
    kv_by_cccd, dt_by_cccd = load_thisinh_kvdt(FILE_TS)
    ta_by_cccd          = load_tiengAnh(FILE_TA)
    giai_by_cccd        = load_giai(FILE_GIAI)
    df_nv               = load_nguyenvong(FILE_NV)

    df_diem = pd.read_csv(FILE_DIEM, low_memory=False)
    diem_by_cccd = {row['cccd']: row for _, row in df_diem.iterrows()}

    df_dgnl_raw = pd.read_csv(FILE_DGNL)
    df_dgnl_raw['DIEM'] = df_dgnl_raw['DIEM'].astype(str).str.replace(',', '.').astype(float)
    dgnl_best = df_dgnl_raw.groupby('CMND')['DIEM'].max().to_dict()

    df_vsat_raw = pd.read_csv(FILE_VSAT)
    df_vsat_raw['DIEM'] = df_vsat_raw['DIEM'].astype(str).str.replace(',', '.').astype(float)
    df_vsat_raw['mon'] = df_vsat_raw['MAMONTHI'].map(VSAT_MON_MAP)
    df_vsat_raw = df_vsat_raw[df_vsat_raw['mon'].notna()]
    vsat_by_cccd = {}
    for cccd, grp in df_vsat_raw.groupby('CMND'):
        vsat_by_cccd[cccd] = grp.groupby('mon')['DIEM'].max().to_dict()

    print(f"  NV={len(df_nv)} | DGNL={len(dgnl_best)} | VSAT={len(vsat_by_cccd)}")
    print(f"  TS có TA={len(ta_by_cccd)} | TS có giải={len(giai_by_cccd)}")
    print("Đang tính điểm...")

    nv_results   = []   # cho UPDATE nguyện vọng
    dc_results   = []   # cho INSERT điểm cộng
    pt_count     = {'PT1': 0, 'PT2': 0, 'PT3': 0}
    skip         = 0

    for _, nv_row in df_nv.iterrows():
        cccd    = str(nv_row['cccd']).strip()
        manganh = str(nv_row['manganh']).strip()
        thu_tu  = int(nv_row['thu_tu_nv'])

        if manganh not in tohop_by_nganh:
            skip += 1
            continue

        tohops    = tohop_by_nganh[manganh]
        tohop_goc = next((t for t in tohops if t['is_goc']), tohops[0])
        dt_row    = diem_by_cccd.get(cccd)
        nl1       = dgnl_best.get(cccd, 0) or 0
        vsat_mons = vsat_by_cccd.get(cccd, {})

        # Điểm ưu tiên (không đổi theo tổ hợp)
        diem_utxt, kv, dt = tinh_diem_utxt(cccd, kv_by_cccd, dt_by_cccd)

        best_total  = -1.0
        best_thxt   = 0.0
        best_cc     = 0.0
        best_pt     = 'PT1'
        best_tohop  = tohop_goc['ma']
        best_ghichu = ''

        # --- PT1: THPT — loop tất cả tổ hợp ---
        if dt_row is not None:
            for th in tohops:
                d_thxt = tinh_thpt(dt_row, th)
                if d_thxt <= 0: continue
                d_cc    = tinh_diem_cc(cccd, th['mons_set'], ta_by_cccd, giai_by_cccd)
                d_total = d_thxt + min(d_cc, 3.0) + diem_utxt
                if d_total > best_total:
                    best_total  = d_total
                    best_thxt   = d_thxt
                    best_cc     = d_cc
                    best_pt     = 'PT1'
                    best_tohop  = th['ma']

        # --- PT2: ĐGNL — tổ hợp gốc ---
        d_dgnl = tinh_dgnl(nl1, tohop_goc['ma'], bqd_dgnl)
        if d_dgnl > 0:
            d_cc    = tinh_diem_cc(cccd, tohop_goc['mons_set'], ta_by_cccd, giai_by_cccd)
            d_total = d_dgnl + min(d_cc, 3.0) + diem_utxt
            if d_total > best_total:
                best_total  = d_total
                best_thxt   = d_dgnl
                best_cc     = d_cc
                best_pt     = 'PT2'
                best_tohop  = tohop_goc['ma']

        # --- PT3: VSAT — loop tất cả tổ hợp ---
        if vsat_mons:
            for th in tohops:
                d_vsat = tinh_vsat(vsat_mons, th, bqd_vsat)
                if d_vsat <= 0: continue
                d_cc    = tinh_diem_cc(cccd, th['mons_set'], ta_by_cccd, giai_by_cccd)
                d_total = d_vsat + min(d_cc, 3.0) + diem_utxt
                if d_total > best_total:
                    best_total  = d_total
                    best_thxt   = d_vsat
                    best_cc     = d_cc
                    best_pt     = 'PT3'
                    best_tohop  = th['ma']

        pt_count[best_pt] += 1
        nv_keys = f"{cccd}_{manganh}_{best_tohop}_{best_pt}"

        # Lấy môn set của tổ hợp tốt nhất để tính ghichu
        best_th     = next((t for t in tohops if t['ma'] == best_tohop), tohop_goc)
        diem_ta     = float(ta_by_cccd.get(cccd, 0) or 0)
        diem_giai   = best_cc - diem_ta
        diem_kv     = KV_DIEM.get(kv, 0.0)
        diem_dt_val = diem_utxt - diem_kv
        best_ghichu = ghichu_str(diem_ta, diem_giai, kv, diem_kv, dt, diem_dt_val)

        # UPDATE NV
        nv_results.append((
            cccd, manganh, thu_tu, best_pt, best_tohop,
            round5(max(best_thxt, 0)), nv_keys
        ))

        # INSERT diemcong (chỉ nếu có điểm cộng)
        diem_tong = best_cc + diem_utxt
        if best_cc > 0 or diem_utxt > 0:
            dc_keys = nv_keys
            dc_results.append((
                cccd, manganh, best_tohop, best_pt,
                round5(best_cc), round5(diem_utxt), round5(diem_tong),
                best_ghichu, dc_keys
            ))

    print(f"Xong! {len(nv_results)} NV | Bỏ qua: {skip}")
    print(f"  PT1={pt_count['PT1']} | PT2={pt_count['PT2']} | PT3={pt_count['PT3']}")
    print(f"  Bản ghi điểm cộng: {len(dc_results)}")

    # ============================================================
    # OUTPUT 1: UPDATE nguyện vọng
    # ============================================================
    print(f"Đang ghi {OUT_NV}...")
    lines = [
        "-- UPDATE tt_phuongthuc + tt_thm + diem_thxt vào xt_nguyenvongxettuyen",
        f"-- Tổng: {len(nv_results)} NV | PT1={pt_count['PT1']} PT2={pt_count['PT2']} PT3={pt_count['PT3']}",
        "-- Điểm đã bao gồm điểm cộng khi so sánh để chọn PT + tổ hợp tốt nhất",
        "", f"USE {DB_NAME};", "",
    ]
    for cccd, manganh, thu_tu, pt, tohop, diem_thxt, nv_keys in nv_results:
        lines.append(
            f"UPDATE `xt_nguyenvongxettuyen` "
            f"SET `tt_phuongthuc`='{pt}', `tt_thm`='{tohop}', "
            f"`diem_thxt`={diem_thxt}, `nv_keys`='{nv_keys}' "
            f"WHERE `nn_cccd`='{cccd}' AND `nv_manganh`='{manganh}' AND `nv_tt`={thu_tu};"
        )
    with open(OUT_NV, 'w', encoding='utf-8') as f:
        f.write("\n".join(lines))

    # ============================================================
    # OUTPUT 2: INSERT điểm cộng
    # ============================================================
    print(f"Đang ghi {OUT_DIEMCONG}...")
    COLS = ("(`ts_cccd`, `manganh`, `matohop`, `phuongthuc`, "
            "`diemCC`, `diemUtxt`, `diemTong`, `ghichu`, `dc_keys`)")
    lines2 = [
        "-- INSERT điểm cộng vào xt_diemcongxettuyen",
        f"-- Tổng: {len(dc_results)} bản ghi (chỉ NV có điểm cộng > 0)",
        "-- diemCC = TA + giải (app tự cap 3.0)",
        "-- diemUtxt = KV + ĐT (app tự quy đổi nếu tổng >= 22.5)",
        "", f"USE {DB_NAME};", "",
    ]
    for i in range(0, len(dc_results), BATCH_SIZE):
        batch = dc_results[i:i + BATCH_SIZE]
        lines2.append(f"INSERT INTO `xt_diemcongxettuyen` {COLS} VALUES")
        rows = []
        for cccd, ma, tohop, pt, dcc, dut, dtong, ghichu, keys in batch:
            g = ghichu.replace("'", "''")
            rows.append(
                f"('{cccd}', '{ma}', '{tohop}', '{pt}', "
                f"{dcc}, {dut}, {dtong}, '{g}', '{keys}')"
            )
        lines2.append(",\n".join(rows) + ";")
        lines2.append("")

    with open(OUT_DIEMCONG, 'w', encoding='utf-8') as f:
        f.write("\n".join(lines2))

    import os
    print(f"\nHoàn thành!")
    print(f"  {OUT_NV}: {os.path.getsize(OUT_NV):,} bytes")
    print(f"  {OUT_DIEMCONG}: {os.path.getsize(OUT_DIEMCONG):,} bytes")


if __name__ == "__main__":
    main()
