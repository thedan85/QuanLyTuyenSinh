package com.example.service;

import com.example.entity.*;
import com.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class XetTuyenService {

    public void chayThuatToanXetTuyen() throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            // 1. Đặt lại toàn bộ kết quả thành "Rớt" trước khi chạy vòng lặp mới
            session.createQuery("UPDATE NguyenVong SET ketQua = 'Rớt'").executeUpdate();

            // 2. Lấy toàn bộ danh sách nguyện vọng
            List<NguyenVong> dsNguyenVong = session.createQuery("FROM NguyenVong", NguyenVong.class).list();

            // 3. Tính điểm cho từng nguyện vọng
            for (NguyenVong nv : dsNguyenVong) {
                tinhDiemChoNguyenVong(session, nv);
                session.update(nv); // Lưu điểm vừa tính vào DB
            }

            // Xả dữ liệu tính điểm xuống DB trước khi xét chỉ tiêu
            session.flush();

            // 4. Xét đậu / trượt dựa trên chỉ tiêu của Ngành (Thuật toán lọc)
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

        // --- BƯỚC 1: LẤY ĐIỂM TỔ HỢP GỐC (THXT) ---
        // CỰC KỲ QUAN TRỌNG: Ưu tiên dùng điểm đã import từ CSV, chỉ tính lại nếu điểm
        // = 0
        double diemThxt = (nv.getDiemThxt() != null) ? nv.getDiemThxt() : 0.0;

        if (diemThxt == 0.0) {
            Query<DiemThi> qDiemThi = session.createQuery("FROM DiemThi WHERE cccd = :cccd", DiemThi.class);
            qDiemThi.setParameter("cccd", cccd);
            DiemThi dt = qDiemThi.uniqueResult();

            if (dt != null) {
                String toHopKey = maNganh + "_" + maToHop;
                Query<NganhToHop> qToHop = session.createQuery("FROM NganhToHop WHERE tbKeys = :key", NganhToHop.class);
                qToHop.setParameter("key", toHopKey);
                NganhToHop toHop = qToHop.uniqueResult();

                if (toHop != null && toHop.getThMon1() != null) {
                    double d1 = getDiemMon(dt, toHop.getThMon1()) * (toHop.getHsMon1() != null ? toHop.getHsMon1() : 1);
                    double d2 = getDiemMon(dt, toHop.getThMon2()) * (toHop.getHsMon2() != null ? toHop.getHsMon2() : 1);
                    double d3 = getDiemMon(dt, toHop.getThMon3()) * (toHop.getHsMon3() != null ? toHop.getHsMon3() : 1);

                    int tongHeSo = (toHop.getHsMon1() != null ? toHop.getHsMon1() : 1) +
                            (toHop.getHsMon2() != null ? toHop.getHsMon2() : 1) +
                            (toHop.getHsMon3() != null ? toHop.getHsMon3() : 1);

                    diemThxt = ((d1 + d2 + d3) * 30.0 / (tongHeSo * 10.0))
                            + (toHop.getDolech() != null ? toHop.getDolech() : 0.0);
                }
            }
        }
        // Làm tròn 5 chữ số thập phân theo database
        nv.setDiemThxt(Math.round(diemThxt * 100000.0) / 100000.0);

        // --- BƯỚC 2: LẤY ĐIỂM CỘNG VÀ ƯU TIÊN ---
        double diemCongCC = (nv.getDiemCong() != null) ? nv.getDiemCong() : 0.0;
        double diemUtxtGoc = (nv.getDiemUtqd() != null) ? nv.getDiemUtqd() : 0.0;

        // Tra cứu trong bảng xt_diemcongxetuyen xem có điểm ưu tiên không
        String dcKey = cccd + "_" + maNganh + "_" + maToHop + "_" + phuongThuc;
        Query<DiemCong> qDiemCong = session.createQuery("FROM DiemCong WHERE dcKeys = :key", DiemCong.class);
        qDiemCong.setParameter("key", dcKey);
        DiemCong dc = qDiemCong.uniqueResult();

        if (dc != null) {
            diemCongCC = (dc.getDiemCC() != null) ? dc.getDiemCC() : diemCongCC;
            diemUtxtGoc = (dc.getDiemUtxt() != null) ? dc.getDiemUtxt() : diemUtxtGoc;
        }
        nv.setDiemCong(diemCongCC);

        // --- BƯỚC 3: ÁP DỤNG CÔNG THỨC GIẢM ĐIỂM ƯU TIÊN ---
        double tongChuaUT = diemThxt + diemCongCC;
        double diemUtqd = diemUtxtGoc;

        // Nếu tổng điểm >= 22.5, điểm ưu tiên bị giảm dần theo công thức Bộ GD&ĐT
        if (tongChuaUT >= 22.5) {
            diemUtqd = ((30.0 - tongChuaUT) / 7.5) * diemUtxtGoc;
        }

        if (diemUtqd < 0)
            diemUtqd = 0.0;
        nv.setDiemUtqd(Math.round(diemUtqd * 100000.0) / 100000.0);

        // --- BƯỚC 4: TÍNH TỔNG ĐIỂM XÉT TUYỂN CUỐI CÙNG ---
        double diemXetTuyen = tongChuaUT + diemUtqd;
        nv.setDiemXetTuyen(Math.round(diemXetTuyen * 100000.0) / 100000.0);
    }

    // Hàm quy đổi mã môn sang cột điểm trong DB
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
        // 1. Reset tất cả về Rớt
        session.createQuery("UPDATE NguyenVong SET ketQua = 'Rớt'").executeUpdate();

        // 2. Lấy danh sách tất cả các nguyện vọng có điểm >= điểm sàn của ngành đó
        // Sắp xếp quan trọng: ĐIỂM GIẢM DẦN, nếu bằng điểm thì ai NV nhỏ hơn (ưu tiên
        // hơn) đứng trước
        String hql = "SELECT nv FROM NguyenVong nv, Nganh ng " +
                "WHERE nv.maNganh = ng.manganh AND nv.diemXetTuyen >= ng.nDiemsan " +
                "ORDER BY nv.diemXetTuyen DESC, nv.thuTuNV ASC";
        List<NguyenVong> allWaiting = session.createQuery(hql, NguyenVong.class).list();

        // 3. Tạo một bản ghi nhớ số lượng đã tuyển của mỗi ngành
        java.util.Map<String, Integer> countMap = new java.util.HashMap<>();

        for (NguyenVong nv : allWaiting) {
            String maNganh = nv.getMaNganh();

            // Lấy chỉ tiêu của ngành này
            Nganh ng = session.get(Nganh.class,
                    session.createQuery("SELECT idnganh FROM Nganh WHERE manganh = :m", Integer.class)
                            .setParameter("m", maNganh).uniqueResult());
            int chiTieu = ng.getnChitieu();
            int daTuyen = countMap.getOrDefault(maNganh, 0);

            // KIỂM TRA:
            // 1. Ngành còn chỉ tiêu không?
            // 2. Thí sinh này đã trúng tuyển nguyện vọng nào cao hơn (số TT nhỏ hơn) chưa?
            if (daTuyen < chiTieu) {
                String checkDaDo = "SELECT count(n) FROM NguyenVong n WHERE tsCccd = :cccd AND ketQua = 'TRÚNG TUYỂN'";
                long soNvDaDo = session.createQuery(checkDaDo, Long.class)
                        .setParameter("cccd", nv.getTsCccd()).uniqueResult();

                if (soNvDaDo == 0) {
                    nv.setKetQua("TRÚNG TUYỂN");
                    session.update(nv);
                    countMap.put(maNganh, daTuyen + 1);

                    // Cập nhật điểm chuẩn cho ngành (người cuối cùng vào là điểm chuẩn)
                    ng.setnDiemtrungtuyen(nv.getDiemXetTuyen());
                    session.update(ng);
                }
            }
        }
    }
}