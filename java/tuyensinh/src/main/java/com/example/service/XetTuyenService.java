package com.example.service;

import com.example.dao.BangQuyDoiDAO;
import com.example.entity.*;
import com.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        nv.setDiemXetTuyen(round5(diemXetTuyen));
    }

    /**
     * PT trên nguyện vọng: PT1/THPT, PT2/ĐGNL (NL1), PT3/VSAT (VSAT_* từng môn).
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
            double n1Cc = dt.getN1Cc() != null ? dt.getN1Cc() : 0.0;
            return Math.max(n1Thi, n1Cc);
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

        for (BangQuyDoi b : bands) {
            if (!khopToHop(b.getdTohop(), maToHop)) {
                continue;
            }
            if (!khopMon(b.getdMon(), maMon, loaiPt)) {
                continue;
            }
            Double a = b.getdDiema();
            Double bb = b.getdDiemb();
            Double c = b.getdDiemc();
            Double d = b.getdDiemd();
            if (a == null || bb == null || c == null || d == null) {
                continue;
            }
            if (diemGoc < a || diemGoc > bb) {
                continue;
            }
            double width = bb - a;
            if (width < bestWidth) {
                bestWidth = width;
                best = b;
            }
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
                double n1Cc = dt.getN1Cc() != null ? dt.getN1Cc() : 0.0;
                return Math.max(n1Thi, n1Cc);
            case "TI":
                return dt.getDiemTin() != null ? dt.getDiemTin() : 0.0;
            case "KTPL":
                return dt.getKtpl() != null ? dt.getKtpl() : 0.0;
            default:
                return 0.0;
        }
    }

    /**
     * Xét trúng tuyển theo vòng NV (mô hình A): vòng 1 = mọi NV1, vòng 2 = NV2 (thí sinh chưa trúng), ...
     * Trong mỗi ngành + vòng: điểm cao trước; tôn chỉ tiêu tổng + chỉ tiêu PT (sl_*).
     */
    private void xetTrungTuyen(Session session) {
        session.createQuery("UPDATE NguyenVong SET ketQua = 'Rớt'").executeUpdate();

        Map<String, Nganh> nganhByMa = loadNganhMap(session);
        List<NguyenVong> allNv = session.createQuery("FROM NguyenVong", NguyenVong.class).list();
        if (allNv == null || allNv.isEmpty()) {
            return;
        }

        int maxThuTu = 0;
        for (NguyenVong nv : allNv) {
            if (nv.getThuTuNV() != null && nv.getThuTuNV() > maxThuTu) {
                maxThuTu = nv.getThuTuNV();
            }
        }
        if (maxThuTu <= 0) {
            maxThuTu = 3;
        }

        Set<String> cccdDaTrung = new HashSet<>();
        Map<String, ChiTieuBoDem> boDemByNganh = new HashMap<>();
        Map<String, Double> diemChuanMinByNganh = new HashMap<>();

        for (int vong = 1; vong <= maxThuTu; vong++) {
            Map<String, List<NguyenVong>> theoNganh = new HashMap<>();
            for (NguyenVong nv : allNv) {
                if (nv.getThuTuNV() == null || nv.getThuTuNV() != vong) {
                    continue;
                }
                if (nv.getTsCccd() == null || cccdDaTrung.contains(nv.getTsCccd())) {
                    continue;
                }
                if (nv.getMaNganh() == null || nv.getMaNganh().trim().isEmpty()) {
                    continue;
                }
                theoNganh.computeIfAbsent(nv.getMaNganh().trim(), k -> new ArrayList<>()).add(nv);
            }

            for (Map.Entry<String, List<NguyenVong>> entry : theoNganh.entrySet()) {
                String maNganh = entry.getKey();
                Nganh nganh = nganhByMa.get(maNganh);
                if (nganh == null || nganh.getnChitieu() == null || nganh.getnChitieu() <= 0) {
                    continue;
                }

                List<NguyenVong> candidates = entry.getValue();
                candidates.sort((a, b) -> Double.compare(diemXt(b), diemXt(a)));

                ChiTieuBoDem dem = boDemByNganh.computeIfAbsent(maNganh, k -> new ChiTieuBoDem());

                for (NguyenVong nv : candidates) {
                    if (cccdDaTrung.contains(nv.getTsCccd())) {
                        continue;
                    }
                    if (!duDiemSan(nv, nganh)) {
                        continue;
                    }

                    String loaiPt = resolveLoaiXetTuyen(nv.getPhuongThuc());
                    if (!nganhCoNhanPt(nganh, loaiPt)) {
                        continue;
                    }
                    if (!dem.conCho(nganh, loaiPt)) {
                        continue;
                    }

                    nv.setKetQua("TRÚNG TUYỂN");
                    session.update(nv);
                    cccdDaTrung.add(nv.getTsCccd());
                    dem.tang(loaiPt);

                    double dx = diemXt(nv);
                    Double curMin = diemChuanMinByNganh.get(maNganh);
                    if (curMin == null || dx < curMin) {
                        diemChuanMinByNganh.put(maNganh, dx);
                    }
                }
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
