package com.example.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.example.dao.BangQuyDoiDAO;
import com.example.entity.BangQuyDoi;
import com.example.entity.DiemCong;
import com.example.entity.DiemThi;
import com.example.entity.Nganh;
import com.example.entity.NganhToHop;
import com.example.entity.NguyenVong;
import com.example.utils.HibernateUtil;

public class XetTuyenService {

    private final BangQuyDoiDAO bangQuyDoiDAO = new BangQuyDoiDAO();

    /** Chỉ tính lại điểm (THPT / quy đổi ĐGNL-VSAT), không đổi kết quả trúng tuyển. */
    public void tinhDiemChoTatCa() throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<NguyenVong> dsNguyenVong = session.createQuery("FROM NguyenVong", NguyenVong.class).list();
            for (NguyenVong nv : dsNguyenVong) {
                tinhDiemChoNguyenVong(session, nv);
                session.update(nv);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void chayThuatToanXetTuyen() throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            session.createQuery("UPDATE NguyenVong SET ketQua = 'Rớt'").executeUpdate();

            List<NguyenVong> dsNguyenVong = session.createQuery("FROM NguyenVong", NguyenVong.class).list();

            for (NguyenVong nv : dsNguyenVong) {
                tinhDiemChoNguyenVong(session, nv);
                session.update(nv);
            }

            session.flush();
            xetTrungTuyen(session);

            tx.commit();
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    private void tinhDiemChoNguyenVong(Session session, NguyenVong nv) {
        String cccd = nv.getTsCccd();
        String maNganh = nv.getMaNganh();
        String maToHop = nv.getMaToHop();
        String phuongThuc = nv.getPhuongThuc();

        Query<DiemThi> qDiemThi = session.createQuery("FROM DiemThi WHERE cccd = :cccd", DiemThi.class);
        qDiemThi.setParameter("cccd", cccd);
        DiemThi dt = qDiemThi.uniqueResult();

        double diemThxt = tinhDiemThxt(session, dt, maNganh, maToHop, phuongThuc);
        nv.setDiemThxt(round5(diemThxt));

        double diemCongCC = (nv.getDiemCong() != null) ? nv.getDiemCong() : 0.0;
        double diemUtxtGoc = (nv.getDiemUtqd() != null) ? nv.getDiemUtqd() : 0.0;

        String dcKey = cccd + "_" + maNganh + "_" + maToHop + "_" + phuongThuc;
        Query<DiemCong> qDiemCong = session.createQuery("FROM DiemCong WHERE dcKeys = :key", DiemCong.class);
        qDiemCong.setParameter("key", dcKey);
        DiemCong dc = qDiemCong.uniqueResult();

        if (dc != null) {
            diemCongCC = (dc.getDiemCC() != null) ? dc.getDiemCC() : diemCongCC;
            diemUtxtGoc = (dc.getDiemUtxt() != null) ? dc.getDiemUtxt() : diemUtxtGoc;
        }
        if (diemCongCC > 3.0) {
            diemCongCC = 3.0;
        }
        nv.setDiemCong(diemCongCC);

        double tongChuaUT = diemThxt + diemCongCC;
        double diemUtqd = diemUtxtGoc;

        if (tongChuaUT >= 22.5) {
            diemUtqd = ((30.0 - tongChuaUT) / 7.5) * diemUtxtGoc;
        }

        if (diemUtqd < 0)
            diemUtqd = 0.0;
        nv.setDiemUtqd(round5(diemUtqd));

        double diemXetTuyen = tongChuaUT + diemUtqd;
        if (diemXetTuyen > 30.0) {
            diemXetTuyen = 30.0;
        }
        nv.setDiemXetTuyen(round5(diemXetTuyen));
    }

    /**
     * PT trên nguyện vọng: PT1/THPT (gồm NK1–NK6 thang 10), PT2/ĐGNL (NL1), PT3/VSAT (VSAT_*; NK dùng điểm NK thang 10 nếu không có bậc VSAT).
     * Bảng xt_bangquydoi dùng d_phuongthuc = DGNL hoặc VSAT.
     */
    private double tinhDiemThxt(Session session, DiemThi dt,
            String maNganh, String maToHop, String phuongThuc) {
        String loai = resolveLoaiXetTuyen(phuongThuc);

        if ("DGNL".equals(loai)) {
            double nl1 = (dt != null && dt.getNl1() != null) ? dt.getNl1() : 0.0;
            return quyDoiTuBang(session, "DGNL", maToHop, null, nl1);
        }
        if ("VSAT".equals(loai)) {
            return tinhDiemVsat(session, dt, maNganh, maToHop);
        }

        // PT1/THPT: luôn tính lại từ DiemThi + NganhToHop (không giữ diem_thxt cũ trên NV).
        return tinhDiemThpt(session, dt, maNganh, maToHop);
    }

    private double tinhDiemThpt(Session session, DiemThi dt, String maNganh, String maToHop) {
        if (dt == null || maToHop == null || maToHop.isEmpty()) {
            return 0.0;
        }
        String toHopKey = maNganh + "_" + maToHop;
        Query<NganhToHop> qToHop = session.createQuery("FROM NganhToHop WHERE tbKeys = :key", NganhToHop.class);
        qToHop.setParameter("key", toHopKey);
        NganhToHop toHop = qToHop.uniqueResult();

        if (toHop == null || toHop.getThMon1() == null) {
            return 0.0;
        }

        double d1 = getDiemMon(dt, toHop.getThMon1()) * (toHop.getHsMon1() != null ? toHop.getHsMon1() : 1);
        double d2 = getDiemMon(dt, toHop.getThMon2()) * (toHop.getHsMon2() != null ? toHop.getHsMon2() : 1);
        double d3 = getDiemMon(dt, toHop.getThMon3()) * (toHop.getHsMon3() != null ? toHop.getHsMon3() : 1);

        int tongHeSo = (toHop.getHsMon1() != null ? toHop.getHsMon1() : 1)
                + (toHop.getHsMon2() != null ? toHop.getHsMon2() : 1)
                + (toHop.getHsMon3() != null ? toHop.getHsMon3() : 1);

        return ((d1 + d2 + d3) * 30.0 / (tongHeSo * 10.0))
                + (toHop.getDolech() != null ? toHop.getDolech() : 0.0);
    }

    /** PT3: quy đổi từng môn VSAT (thang 150) → THPT (thang 10), cộng THM như PT1. */
    private double tinhDiemVsat(Session session, DiemThi dt, String maNganh, String maToHop) {
        if (dt == null || maToHop == null || maToHop.isEmpty()) {
            return 0.0;
        }
        String toHopKey = maNganh + "_" + maToHop;
        Query<NganhToHop> qToHop = session.createQuery("FROM NganhToHop WHERE tbKeys = :key", NganhToHop.class);
        qToHop.setParameter("key", toHopKey);
        NganhToHop toHop = qToHop.uniqueResult();

        if (toHop == null || toHop.getThMon1() == null) {
            return 0.0;
        }

        double d1 = quyDoiDiemVsatMon(session, dt, maToHop, toHop.getThMon1())
                * (toHop.getHsMon1() != null ? toHop.getHsMon1() : 1);
        double d2 = quyDoiDiemVsatMon(session, dt, maToHop, toHop.getThMon2())
                * (toHop.getHsMon2() != null ? toHop.getHsMon2() : 1);
        double d3 = quyDoiDiemVsatMon(session, dt, maToHop, toHop.getThMon3())
                * (toHop.getHsMon3() != null ? toHop.getHsMon3() : 1);

        int tongHeSo = (toHop.getHsMon1() != null ? toHop.getHsMon1() : 1)
                + (toHop.getHsMon2() != null ? toHop.getHsMon2() : 1)
                + (toHop.getHsMon3() != null ? toHop.getHsMon3() : 1);

        return ((d1 + d2 + d3) * 30.0 / (tongHeSo * 10.0))
                + (toHop.getDolech() != null ? toHop.getDolech() : 0.0);
    }

    private double quyDoiDiemVsatMon(Session session, DiemThi dt, String maToHop, String maMon) {
        if (maMon == null || maMon.trim().isEmpty()) {
            return 0.0;
        }
        String mon = maMon.trim().toUpperCase();
        double goc = getDiemVsatMon(dt, mon);
        double quyDoi = quyDoiTuBang(session, "VSAT", maToHop, mon, goc);
        if (quyDoi > 0) {
            return quyDoi;
        }
        // Không có bậc VSAT cho N1: dùng điểm THPT/chứng chỉ thang 10
        if ("N1".equals(mon)) {
            double n1Thi = dt.getN1Thi() != null ? dt.getN1Thi() : 0.0;
            double n1CcGoc = dt.getN1Cc() != null ? dt.getN1Cc() : 0.0;
            double n1CcQuyDoi = (n1CcGoc > 0 && n1CcGoc <= 9.0) ? quyDoiIelts(n1CcGoc) : n1CcGoc;
            return Math.max(n1Thi, n1CcQuyDoi);
        }
        // NK: không có bảng VSAT cho NK → điểm năng khiếu thang 10 (cùng cột NK1–NK6 như PT1)
        if (isMaMonNk(mon) && dt != null) {
            return getDiemMon(dt, mon);
        }
        return 0.0;
    }

    private double getDiemVsatMon(DiemThi dt, String maMon) {
        if (dt == null || maMon == null) {
            return 0.0;
        }
        Double v;
        switch (maMon.toUpperCase()) {
            case "TO":
                v = dt.getVsatTo();
                break;
            case "LI":
                v = dt.getVsatLi();
                break;
            case "HO":
                v = dt.getVsatHo();
                break;
            case "SI":
                v = dt.getVsatSi();
                break;
            case "SU":
                v = dt.getVsatSu();
                break;
            case "DI":
                v = dt.getVsatDi();
                break;
            case "VA":
                v = dt.getVsatVa();
                break;
            case "N1":
                v = dt.getVsatN1();
                break;
            default:
                return 0.0;
        }
        return (v != null && v > 0) ? v : 0.0;
    }

    /** PT2 hoặc DGNL → ĐGNL; PT3 hoặc VSAT → VSAT; còn lại THPT. */
    public static String resolveLoaiXetTuyen(String phuongThuc) {
        if (phuongThuc == null || phuongThuc.trim().isEmpty()) {
            return "THPT";
        }
        String pt = phuongThuc.trim().toUpperCase();
        if ("PT2".equals(pt) || "DGNL".equals(pt) || pt.contains("DGNL")) {
            return "DGNL";
        }
        if ("PT3".equals(pt) || "VSAT".equals(pt) || pt.contains("VSAT")) {
            return "VSAT";
        }
        return "THPT";
    }

    /**
     * Tra xt_bangquydoi: điểm gốc trong [A,B] → nội suy [C,D].
     * DGNL: C,D thang ~30; VSAT: C,D thang 10. Nhiều bậc khớp: chọn khoảng hẹp nhất.
     */
    private double quyDoiTuBang(Session session, String loaiPt, String maToHop, String maMon, double diemGoc) {
        if (diemGoc <= 0) {
            return 0.0;
        }
        List<BangQuyDoi> bands = bangQuyDoiDAO.findByPhuongThuc(session, loaiPt);
        if (bands == null || bands.isEmpty()) {
            return 0.0;
        }

        BangQuyDoi best = null;
        double bestWidth = Double.MAX_VALUE;
        BangQuyDoi highestBand = null;
        double currentMaxBb = -1.0;

        for (BangQuyDoi b : bands) {
            if (!khopToHop(b.getdTohop(), maToHop))
                continue;
            if (!khopMon(b.getdMon(), maMon, loaiPt))
                continue;

            Double a = b.getdDiema();
            Double bb = b.getdDiemb();
            if (a == null || bb == null || b.getdDiemc() == null || b.getdDiemd() == null) {
                continue;
            }

            if (bb > currentMaxBb) {
                currentMaxBb = bb;
                highestBand = b;
            }

            if (diemGoc >= a && diemGoc <= bb) {
                double width = bb - a;
                if (width < bestWidth) {
                    bestWidth = width;
                    best = b;
                }
            }
        }

        if (best == null && highestBand != null && diemGoc > currentMaxBb) {
            best = highestBand;
            diemGoc = currentMaxBb;
        }

        if (best == null) {
            return 0.0;
        }
        return noiSuy(diemGoc, best.getdDiema(), best.getdDiemb(), best.getdDiemc(), best.getdDiemd());
    }

    private boolean khopToHop(String bandToHop, String nvToHop) {
        if (bandToHop == null || bandToHop.trim().isEmpty()) {
            return true;
        }
        if (nvToHop == null || nvToHop.trim().isEmpty()) {
            return false;
        }
        return bandToHop.trim().equalsIgnoreCase(nvToHop.trim());
    }

    private boolean khopMon(String bandMon, String maMon, String loaiPt) {
        if ("DGNL".equals(loaiPt)) {
            return bandMon == null || bandMon.trim().isEmpty();
        }
        if (maMon == null || maMon.trim().isEmpty()) {
            return false;
        }
        return maMon.trim().equalsIgnoreCase(bandMon != null ? bandMon.trim() : "");
    }

    private double noiSuy(double x, double a, double b, double c, double d) {
        if (b <= a) {
            return c;
        }
        double t = (x - a) / (b - a);
        if (t < 0) {
            t = 0;
        }
        if (t > 1) {
            t = 1;
        }
        return c + t * (d - c);
    }

    private double round5(double v) {
        return Math.round(v * 100000.0) / 100000.0;
    }

    private double quyDoiIelts(double ielts) {
        if (ielts >= 7.0)
            return 10.0;
        if (ielts >= 6.5)
            return 9.5;
        if (ielts >= 6.0)
            return 9.0;
        if (ielts >= 5.5)
            return 8.5;
        if (ielts >= 5.0)
            return 8.0;
        if (ielts >= 4.5)
            return 7.5;
        if (ielts >= 4.0)
            return 7.0;
        return 0.0;
    }

    private double getDiemMon(DiemThi dt, String maMon) {
        if (maMon == null)
            return 0.0;
        switch (maMon.toUpperCase()) {
            case "TO":
                return dt.getDiemToan() != null ? dt.getDiemToan() : 0.0;
            case "LI":
                return dt.getDiemLy() != null ? dt.getDiemLy() : 0.0;
            case "HO":
                return dt.getDiemHoa() != null ? dt.getDiemHoa() : 0.0;
            case "SI":
                return dt.getDiemSinh() != null ? dt.getDiemSinh() : 0.0;
            case "SU":
                return dt.getDiemSu() != null ? dt.getDiemSu() : 0.0;
            case "DI":
                return dt.getDiemDia() != null ? dt.getDiemDia() : 0.0;
            case "VA":
                return dt.getDiemVan() != null ? dt.getDiemVan() : 0.0;
            case "N1":
                double n1Thi = dt.getN1Thi() != null ? dt.getN1Thi() : 0.0;
                double n1CcGoc = dt.getN1Cc() != null ? dt.getN1Cc() : 0.0;
                double n1CcQuyDoi = (n1CcGoc > 0 && n1CcGoc <= 9.0) ? quyDoiIelts(n1CcGoc) : n1CcGoc;
                return Math.max(n1Thi, n1CcQuyDoi);
            case "TI":
                return dt.getDiemTin() != null ? dt.getDiemTin() : 0.0;
            case "KTPL":
                return dt.getKtpl() != null ? dt.getKtpl() : 0.0;
            case "NK1":
                return diemNk(dt.getNk1());
            case "NK2":
                return diemNk(dt.getNk2());
            case "NK3":
                return diemNk(dt.getNk3());
            case "NK4":
                return diemNk(dt.getNk4());
            case "NK5":
                return diemNk(dt.getNk5());
            case "NK6":
                return diemNk(dt.getNk6());
            default:
                return 0.0;
        }
    }

    private static boolean isMaMonNk(String maMon) {
        if (maMon == null) {
            return false;
        }
        switch (maMon.trim().toUpperCase()) {
            case "NK1":
            case "NK2":
            case "NK3":
            case "NK4":
            case "NK5":
            case "NK6":
                return true;
            default:
                return false;
        }
    }

    private static double diemNk(Double v) {
        return (v != null && v > 0) ? v : 0.0;
    }

    /**
     * Xét trúng tuyển theo mô hình Bộ: duyệt theo từng ngành, lọc ảo toàn quốc theo {@code nv_tt}.
     * <ol>
     *   <li>Gom mọi NV vào từng {@code ma_nganh} (không phân biệt NV1/NV2…).</li>
     *   <li>Sắp điểm xét tuyển giảm dần, lấp chỉ tiêu → danh sách đủ điều kiện đỗ (tạm).</li>
     *   <li>Lọc ảo: thí sinh đỗ nhiều ngành chỉ giữ ngành có {@code nv_tt} nhỏ nhất; ngành khác bổ sung người kế tiếp.</li>
     *   <li>Ghi {@code TRÚNG TUYỂN} cho NV được giữ; còn lại {@code Rớt}.</li>
     * </ol>
     */
    private void xetTrungTuyen(Session session) {
        session.createQuery("UPDATE NguyenVong SET ketQua = 'Rớt'").executeUpdate();

        Map<String, Nganh> nganhByMa = loadNganhMap(session);
        List<NguyenVong> allNv = session.createQuery("FROM NguyenVong", NguyenVong.class).list();
        if (allNv == null || allNv.isEmpty()) {
            return;
        }

        Map<String, TrangThaiNganh> trangThaiTheoNganh = taoTrangThaiTheoNganh(allNv, nganhByMa);
        if (trangThaiTheoNganh.isEmpty()) {
            return;
        }

        // Bước 1–3: mỗi ngành lấp danh sách đủ điều kiện đỗ (có thể trùng CCCD giữa các ngành).
        for (TrangThaiNganh tt : trangThaiTheoNganh.values()) {
            boSungDenDuChiTieu(tt, trangThaiTheoNganh, false);
        }

        // Bước 4: lọc ảo toàn quốc + bổ sung chỗ trống cho đến khi ổn định.
        final int maxVongLoc = 500;
        for (int i = 0; i < maxVongLoc; i++) {
            boolean coLoai = locAoToanQuoc(trangThaiTheoNganh);
            boolean coBoSung = false;
            for (TrangThaiNganh tt : trangThaiTheoNganh.values()) {
                int truoc = tt.duDieuKienDo.size();
                boSungDenDuChiTieu(tt, trangThaiTheoNganh, true);
                if (tt.duDieuKienDo.size() != truoc) {
                    coBoSung = true;
                }
            }
            if (!coLoai && !coBoSung) {
                break;
            }
        }

        Set<Integer> idTrung = new HashSet<>();
        Map<String, Double> diemChuanMinByNganh = new HashMap<>();
        for (TrangThaiNganh tt : trangThaiTheoNganh.values()) {
            for (NguyenVong nv : tt.duDieuKienDo) {
                idTrung.add(nv.getIdnv());
                double dx = diemXt(nv);
                Double curMin = diemChuanMinByNganh.get(tt.maNganh);
                if (curMin == null || dx < curMin) {
                    diemChuanMinByNganh.put(tt.maNganh, dx);
                }
            }
        }

        for (NguyenVong nv : allNv) {
            if (idTrung.contains(nv.getIdnv())) {
                nv.setKetQua("TRÚNG TUYỂN");
                session.update(nv);
            }
        }

        for (Map.Entry<String, Double> e : diemChuanMinByNganh.entrySet()) {
            Nganh nganh = nganhByMa.get(e.getKey());
            if (nganh != null) {
                nganh.setnDiemtrungtuyen(round5(e.getValue()));
                session.update(nganh);
            }
        }
    }

    private static Map<String, TrangThaiNganh> taoTrangThaiTheoNganh(List<NguyenVong> allNv,
            Map<String, Nganh> nganhByMa) {
        Map<String, List<NguyenVong>> poolRaw = new HashMap<>();
        for (NguyenVong nv : allNv) {
            if (nv.getMaNganh() == null || nv.getMaNganh().trim().isEmpty()) {
                continue;
            }
            if (nv.getTsCccd() == null || nv.getTsCccd().trim().isEmpty()) {
                continue;
            }
            poolRaw.computeIfAbsent(nv.getMaNganh().trim(), k -> new ArrayList<>()).add(nv);
        }

        Map<String, TrangThaiNganh> result = new HashMap<>();
        for (Map.Entry<String, List<NguyenVong>> e : poolRaw.entrySet()) {
            Nganh nganh = nganhByMa.get(e.getKey());
            if (nganh == null || nganh.getnChitieu() == null || nganh.getnChitieu() <= 0) {
                continue;
            }
            result.put(e.getKey(), new TrangThaiNganh(e.getKey(), nganh, e.getValue()));
        }
        return result;
    }

    /**
     * Lấp chỉ tiêu từ pool đã sort. {@code locAo=true}: không thêm CCCD đã nằm trong danh sách đỗ ngành khác.
     */
    private static void boSungDenDuChiTieu(TrangThaiNganh tt, Map<String, TrangThaiNganh> trangThaiTheoNganh,
            boolean locAo) {
        tt.dongBoDem();
        for (NguyenVong nv : tt.sortedPool) {
            if (!tt.dem.conCho(tt.nganh, resolveLoaiXetTuyen(nv.getPhuongThuc()))) {
                continue;
            }
            if (daCoCccdTrongDanhSach(tt, nv.getTsCccd())) {
                continue;
            }
            if (locAo && daCoCccdTrongNganhKhac(trangThaiTheoNganh, tt.maNganh, nv.getTsCccd())) {
                continue;
            }
            if (!duDiemSan(nv, tt.nganh)) {
                continue;
            }
            String loaiPt = resolveLoaiXetTuyen(nv.getPhuongThuc());
            if (!nganhCoNhanPt(tt.nganh, loaiPt)) {
                continue;
            }
            tt.duDieuKienDo.add(nv);
            tt.dem.tang(loaiPt);
        }
    }

    /** Lọc ảo: CCCD đỗ ≥2 ngành → giữ NV có {@code nv_tt} nhỏ nhất, gỡ khỏi các ngành còn lại. */
    private static boolean locAoToanQuoc(Map<String, TrangThaiNganh> trangThaiTheoNganh) {
        Map<String, List<NguyenVong>> theoCccd = new HashMap<>();
        for (TrangThaiNganh tt : trangThaiTheoNganh.values()) {
            for (NguyenVong nv : tt.duDieuKienDo) {
                theoCccd.computeIfAbsent(nv.getTsCccd(), k -> new ArrayList<>()).add(nv);
            }
        }

        boolean coThayDoi = false;
        for (Map.Entry<String, List<NguyenVong>> e : theoCccd.entrySet()) {
            if (e.getValue().size() < 2) {
                continue;
            }
            NguyenVong giu = chonTheoUuTienNv(e.getValue());
            String maGiu = giu.getMaNganh() != null ? giu.getMaNganh().trim() : "";
            for (NguyenVong nv : e.getValue()) {
                String maNv = nv.getMaNganh() != null ? nv.getMaNganh().trim() : "";
                if (maNv.equals(maGiu) && nv.getIdnv() == giu.getIdnv()) {
                    continue;
                }
                TrangThaiNganh tt = trangThaiTheoNganh.get(maNv);
                if (tt != null && tt.duDieuKienDo.remove(nv)) {
                    coThayDoi = true;
                }
            }
        }
        return coThayDoi;
    }

    /** Ưu tiên {@code nv_tt} nhỏ nhất; hòa thì điểm XT cao hơn. */
    private static NguyenVong chonTheoUuTienNv(List<NguyenVong> danhSach) {
        NguyenVong best = danhSach.get(0);
        for (int i = 1; i < danhSach.size(); i++) {
            NguyenVong cand = danhSach.get(i);
            int ttBest = thuTuNvOrMax(best);
            int ttCand = thuTuNvOrMax(cand);
            if (ttCand < ttBest) {
                best = cand;
            } else if (ttCand == ttBest && diemXt(cand) > diemXt(best)) {
                best = cand;
            }
        }
        return best;
    }

    private static int thuTuNvOrMax(NguyenVong nv) {
        return nv.getThuTuNV() != null && nv.getThuTuNV() > 0 ? nv.getThuTuNV() : Integer.MAX_VALUE;
    }

    private static boolean daCoCccdTrongDanhSach(TrangThaiNganh tt, String cccd) {
        for (NguyenVong nv : tt.duDieuKienDo) {
            if (cccd.equals(nv.getTsCccd())) {
                return true;
            }
        }
        return false;
    }

    private static boolean daCoCccdTrongNganhKhac(Map<String, TrangThaiNganh> trangThaiTheoNganh,
            String maNganhHienTai, String cccd) {
        for (Map.Entry<String, TrangThaiNganh> e : trangThaiTheoNganh.entrySet()) {
            if (e.getKey().equals(maNganhHienTai)) {
                continue;
            }
            if (daCoCccdTrongDanhSach(e.getValue(), cccd)) {
                return true;
            }
        }
        return false;
    }

    private static final class TrangThaiNganh {
        final String maNganh;
        final Nganh nganh;
        final List<NguyenVong> sortedPool;
        final List<NguyenVong> duDieuKienDo = new ArrayList<>();
        ChiTieuBoDem dem = new ChiTieuBoDem();

        TrangThaiNganh(String maNganh, Nganh nganh, List<NguyenVong> pool) {
            this.maNganh = maNganh;
            this.nganh = nganh;
            this.sortedPool = new ArrayList<>(pool);
            sapXepCanhTranh(this.sortedPool);
        }

        void dongBoDem() {
            dem = new ChiTieuBoDem();
            for (NguyenVong nv : duDieuKienDo) {
                dem.tang(resolveLoaiXetTuyen(nv.getPhuongThuc()));
            }
        }
    }

    /** Sắp xếp điểm XT giảm dần; hòa điểm thì CCCD nhỏ hơn. */
    private static void sapXepCanhTranh(List<NguyenVong> candidates) {
        candidates.sort((a, b) -> {
            int byDiem = Double.compare(diemXt(b), diemXt(a));
            if (byDiem != 0) {
                return byDiem;
            }
            String ca = a.getTsCccd() != null ? a.getTsCccd() : "";
            String cb = b.getTsCccd() != null ? b.getTsCccd() : "";
            return ca.compareTo(cb);
        });
    }

    private static double diemXt(NguyenVong nv) {
        return nv.getDiemXetTuyen() != null ? nv.getDiemXetTuyen() : 0.0;
    }

    private static boolean duDiemSan(NguyenVong nv, Nganh nganh) {
        if (nganh.getnDiemsan() == null || nv.getDiemXetTuyen() == null) {
            return false;
        }
        return nv.getDiemXetTuyen() >= nganh.getnDiemsan();
    }

    private Map<String, Nganh> loadNganhMap(Session session) {
        Map<String, Nganh> map = new HashMap<>();
        List<Nganh> list = session.createQuery("FROM Nganh", Nganh.class).list();
        if (list != null) {
            for (Nganh ng : list) {
                if (ng.getManganh() != null) {
                    map.put(ng.getManganh().trim(), ng);
                }
            }
        }
        return map;
    }

    /** n_thpt / n_dgnl / n_vsat = '0' → ngành không nhận PT đó. */
    private static boolean nganhCoNhanPt(Nganh nganh, String loaiPt) {
        String flag;
        if ("DGNL".equals(loaiPt)) {
            flag = nganh.getnDgnl();
        } else if ("VSAT".equals(loaiPt)) {
            flag = nganh.getnVsat();
        } else {
            flag = nganh.getnThpt();
        }
        if (flag == null || flag.trim().isEmpty()) {
            return true;
        }
        return "1".equals(flag.trim());
    }

    /** sl_* = 0 hoặc null → không giới hạn riêng theo PT (chỉ giới hạn n_chitieu). */
    private static int chiTieuPt(Nganh nganh, String loaiPt) {
        if ("DGNL".equals(loaiPt)) {
            return nganh.getSlDgnl() != null ? nganh.getSlDgnl() : 0;
        }
        if ("VSAT".equals(loaiPt)) {
            return nganh.getSlVsat() != null ? nganh.getSlVsat() : 0;
        }
        return parseSlThpt(nganh.getSlThpt());
    }

    private static int parseSlThpt(String slThpt) {
        if (slThpt == null || slThpt.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(slThpt.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** Bộ đếm chỉ tiêu trúng tuyển theo ngành. */
    private static final class ChiTieuBoDem {
        int tong;
        int pt1;
        int pt2;
        int pt3;

        boolean conCho(Nganh nganh, String loaiPt) {
            if (nganh.getnChitieu() == null || nganh.getnChitieu() <= 0) {
                return false;
            }
            if (tong >= nganh.getnChitieu()) {
                return false;
            }
            int slPt = chiTieuPt(nganh, loaiPt);
            if (slPt > 0 && demPt(loaiPt) >= slPt) {
                return false;
            }
            return true;
        }

        void tang(String loaiPt) {
            tong++;
            if ("DGNL".equals(loaiPt)) {
                pt2++;
            } else if ("VSAT".equals(loaiPt)) {
                pt3++;
            } else {
                pt1++;
            }
        }

        int demPt(String loaiPt) {
            if ("DGNL".equals(loaiPt)) {
                return pt2;
            }
            if ("VSAT".equals(loaiPt)) {
                return pt3;
            }
            return pt1;
        }
    }
}
