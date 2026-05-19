package com.example.service;

import com.example.dao.BangQuyDoiDAO;
import com.example.entity.*;
import com.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

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

        double diemThxt = tinhDiemThxt(session, nv, dt, maNganh, maToHop, phuongThuc);
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
     * PT trên nguyện vọng: PT1/THPT, PT2/ĐGNL (NL1), PT3/VSAT (NK1/NK2).
     * Bảng xt_bangquydoi dùng d_phuongthuc = DGNL hoặc VSAT.
     */
    private double tinhDiemThxt(Session session, NguyenVong nv, DiemThi dt,
            String maNganh, String maToHop, String phuongThuc) {
        String loai = resolveLoaiXetTuyen(phuongThuc);

        if ("DGNL".equals(loai)) {
            double nl1 = (dt != null && dt.getNl1() != null) ? dt.getNl1() : 0.0;
            return quyDoiTuBang(session, "DGNL", maToHop, nl1);
        }
        if ("VSAT".equals(loai)) {
            return quyDoiTuBang(session, "VSAT", maToHop, layDiemVsatGoc(dt));
        }

        double daCo = (nv.getDiemThxt() != null) ? nv.getDiemThxt() : 0.0;
        if (daCo != 0.0) {
            return daCo;
        }
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

    private double layDiemVsatGoc(DiemThi dt) {
        if (dt == null) {
            return 0.0;
        }
        Double nk1 = dt.getNk1();
        Double nk2 = dt.getNk2();
        if (nk1 != null && nk1 > 0) {
            return nk1;
        }
        if (nk2 != null && nk2 > 0) {
            return nk2;
        }
        if (nk1 != null && nk2 != null) {
            return Math.max(nk1, nk2);
        }
        return 0.0;
    }

    /**
     * Tra xt_bangquydoi: điểm gốc trong [A,B] → nội suy thang 30 trong [C,D].
     * Nhiều bậc khớp: chọn khoảng hẹp nhất (B-A nhỏ nhất).
     */
    private double quyDoiTuBang(Session session, String loaiPt, String maToHop, double diemGoc) {
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

    private void xetTrungTuyen(Session session) {
        session.createQuery("UPDATE NguyenVong SET ketQua = 'Rớt'").executeUpdate();

        String hql = "SELECT nv FROM NguyenVong nv, Nganh ng " +
                "WHERE nv.maNganh = ng.manganh AND nv.diemXetTuyen >= ng.nDiemsan " +
                "ORDER BY nv.diemXetTuyen DESC, nv.thuTuNV ASC";
        List<NguyenVong> allWaiting = session.createQuery(hql, NguyenVong.class).list();

        java.util.Map<String, Integer> countMap = new java.util.HashMap<>();

        for (NguyenVong nv : allWaiting) {
            String maNganh = nv.getMaNganh();

            Nganh ng = session.get(Nganh.class,
                    session.createQuery("SELECT idnganh FROM Nganh WHERE manganh = :m", Integer.class)
                            .setParameter("m", maNganh).uniqueResult());
            int chiTieu = ng.getnChitieu();
            int daTuyen = countMap.getOrDefault(maNganh, 0);

            if (daTuyen < chiTieu) {
                String checkDaDo = "SELECT count(n) FROM NguyenVong n WHERE tsCccd = :cccd AND ketQua = 'TRÚNG TUYỂN'";
                long soNvDaDo = session.createQuery(checkDaDo, Long.class)
                        .setParameter("cccd", nv.getTsCccd()).uniqueResult();

                if (soNvDaDo == 0) {
                    nv.setKetQua("TRÚNG TUYỂN");
                    session.update(nv);
                    countMap.put(maNganh, daTuyen + 1);

                    ng.setnDiemtrungtuyen(nv.getDiemXetTuyen());
                    session.update(ng);
                }
            }
        }
    }
}
