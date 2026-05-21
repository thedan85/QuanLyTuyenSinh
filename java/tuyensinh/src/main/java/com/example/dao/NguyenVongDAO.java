package com.example.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.example.dto.DiemXetTuyenRow;
import com.example.dto.ThongKeTrungTuyenRow;
import com.example.dto.TrungTuyenRow;
import com.example.entity.DiemThi;
import com.example.entity.Nganh;
import com.example.entity.NguyenVong;
import com.example.entity.ThiSinh;
import com.example.entity.ToHopMon;
import com.example.service.XetTuyenService;
import com.example.utils.HibernateUtil;

public class NguyenVongDAO {

    public List<NguyenVong> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM NguyenVong ORDER BY tsCccd, thuTuNV", NguyenVong.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean add(NguyenVong nv) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(nv);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            return false;
        }
    }

    public boolean update(NguyenVong nv) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(nv);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            return false;
        }
    }

    public boolean delete(int idnv) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            NguyenVong nv = session.get(NguyenVong.class, idnv);
            if (nv != null) {
                session.delete(nv);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            return false;
        }
    }

    public boolean isKeyExists(String nvKeys) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(n) FROM NguyenVong n WHERE n.nvKeys = :keys";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("keys", nvKeys);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isNganhToHopExists(String cccd, String maNganh, String maToHop) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(n) FROM NguyenVong n WHERE n.tsCccd = :cccd AND n.maNganh = :maNganh AND coalesce(n.maToHop, '') = :maToHop";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("cccd", cccd);
            query.setParameter("maNganh", maNganh);
            query.setParameter("maToHop", maToHop == null ? "" : maToHop);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isNganhToHopExistsExcept(String cccd, String maNganh, String maToHop, int idnv) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(n) FROM NguyenVong n WHERE n.tsCccd = :cccd AND n.maNganh = :maNganh AND coalesce(n.maToHop, '') = :maToHop AND n.idnv <> :id";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("cccd", cccd);
            query.setParameter("maNganh", maNganh);
            query.setParameter("maToHop", maToHop == null ? "" : maToHop);
            query.setParameter("id", idnv);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isKeyExistsExcept(String nvKeys, int idnv) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(n) FROM NguyenVong n WHERE n.nvKeys = :keys AND n.idnv <> :id";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("keys", nvKeys);
            query.setParameter("id", idnv);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            return true;
        }
    }

    public List<Object[]> traCuuTheoCccdHoacSbd(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT ts.cccd, ts.sobaodanh, ts.ho, ts.ten, " +
                    "nv.thuTuNV, nv.maNganh, ng.tennganh, nv.maToHop, " +
                    "nv.diemXetTuyen, nv.ketQua " +
                    "FROM NguyenVong nv, ThiSinh ts, Nganh ng " +
                    "WHERE nv.tsCccd = ts.cccd AND nv.maNganh = ng.manganh " +
                    "AND (ts.cccd = :kw OR ts.sobaodanh = :kw) " +
                    "ORDER BY nv.thuTuNV ASC";
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setParameter("kw", keyword.trim());
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isThuTuExists(String cccd, int thuTu) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(n) FROM NguyenVong n WHERE n.tsCccd = :cccd AND n.thuTuNV = :tt";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("cccd", cccd);
            query.setParameter("tt", thuTu);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isThuTuExistsExcept(String cccd, int thuTu, int idnv) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(n) FROM NguyenVong n WHERE n.tsCccd = :cccd AND n.thuTuNV = :tt AND n.idnv <> :id";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("cccd", cccd);
            query.setParameter("tt", thuTu);
            query.setParameter("id", idnv);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /** Danh sách đầy đủ cho panel Điểm xét tuyển (lọc CCCD/họ tên, có THM cao nhất). */
    public List<DiemXetTuyenRow> buildDiemXetTuyenRows(String keyword) {
        String kw = keyword == null ? "" : keyword.trim();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<NguyenVong> nvs = session
                    .createQuery("FROM NguyenVong nv ORDER BY nv.tsCccd, nv.thuTuNV", NguyenVong.class)
                    .list();
            if (nvs == null || nvs.isEmpty()) {
                return new ArrayList<>();
            }

            Map<String, ThiSinh> thiSinhByCccd = new HashMap<>();
            for (ThiSinh ts : session.createQuery("FROM ThiSinh", ThiSinh.class).list()) {
                if (ts.getCccd() != null) {
                    thiSinhByCccd.put(ts.getCccd(), ts);
                }
            }

            Map<String, DiemThi> diemByCccd = new HashMap<>();
            for (DiemThi dt : session.createQuery("FROM DiemThi", DiemThi.class).list()) {
                if (dt.getCccd() != null) {
                    diemByCccd.put(dt.getCccd(), dt);
                }
            }

            Map<String, ToHopMon> toHopByMa = new HashMap<>();
            for (ToHopMon th : session.createQuery("FROM ToHopMon", ToHopMon.class).list()) {
                if (th.getMatohop() != null) {
                    toHopByMa.put(th.getMatohop(), th);
                }
            }

            Map<String, Double> thmMaxByCccdNganh = new HashMap<>();
            Map<String, String> maToHopAtThmMax = new HashMap<>();
            for (NguyenVong nv : nvs) {
                if (nv.getDiemThxt() == null || nv.getTsCccd() == null || nv.getMaNganh() == null) {
                    continue;
                }
                String key = nv.getTsCccd() + "|" + nv.getMaNganh();
                double thm = nv.getDiemThxt();
                Double current = thmMaxByCccdNganh.get(key);
                if (current == null || thm > current) {
                    thmMaxByCccdNganh.put(key, thm);
                    maToHopAtThmMax.put(key, nv.getMaToHop());
                }
            }

            List<DiemXetTuyenRow> rows = new ArrayList<>();
            for (NguyenVong nv : nvs) {
                ThiSinh ts = thiSinhByCccd.get(nv.getTsCccd());
                if (!matchesKeyword(kw, nv.getTsCccd(), ts)) {
                    continue;
                }
                DiemThi dt = diemByCccd.get(nv.getTsCccd());
                String maxKey = nv.getTsCccd() + "|" + nv.getMaNganh();
                Double thmMax = thmMaxByCccdNganh.get(maxKey);
                String maThmMax = maToHopAtThmMax.get(maxKey);

                DiemXetTuyenRow row = new DiemXetTuyenRow();
                row.setCccd(nv.getTsCccd());
                if (ts != null) {
                    row.setHo(ts.getHo() != null ? ts.getHo() : "");
                    row.setTen(ts.getTen() != null ? ts.getTen() : "");
                } else {
                    row.setHo("");
                    row.setTen("");
                }
                row.setThuTuNv(nv.getThuTuNV());
                row.setMaNganh(nv.getMaNganh());
                row.setPhuongThuc(nv.getPhuongThuc());
                row.setMaToHop(nv.getMaToHop());
                row.setDiemChuaQuyDoi(formatDiemChuaQuyDoi(nv.getPhuongThuc(), dt));
                row.setThm(nv.getDiemThxt());
                row.setThmCaoNhat(thmMax);
                row.setTenToHopThmCaoNhat(formatTenToHopThmCaoNhat(maThmMax, toHopByMa));
                row.setDiemCong(nv.getDiemCong());
                row.setDiemUtqd(nv.getDiemUtqd());
                row.setDiemXetTuyen(nv.getDiemXetTuyen());
                row.setKetQua(nv.getKetQua() == null ? "Chờ xét" : nv.getKetQua());
                rows.add(row);
            }
            return rows;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public long countDiemXetTuyenRows(String keyword) {
        return buildDiemXetTuyenRows(keyword).size();
    }

    /** Danh sách NV trúng tuyển; maNganh null/rỗng = tất cả ngành. */
    public List<TrungTuyenRow> listTrungTuyenByNganh(String maNganh) {
        String ma = maNganh == null ? "" : maNganh.trim();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // LEFT JOIN ThiSinh: vẫn hiện NV trúng khi chưa có bản ghi thí sinh (vd. DB mẫu chỉ có NV/điểm).
            StringBuilder hql = new StringBuilder(
                    "SELECT nv, ts, ng FROM NguyenVong nv " +
                    "LEFT JOIN ThiSinh ts ON nv.tsCccd = ts.cccd " +
                    "JOIN Nganh ng ON nv.maNganh = ng.manganh " +
                    "WHERE nv.ketQua LIKE :trung ");
            if (!ma.isEmpty()) {
                hql.append("AND nv.maNganh = :maNganh ");
            }
            hql.append("ORDER BY nv.maNganh, nv.diemXetTuyen DESC, nv.thuTuNV ASC");

            Query<Object[]> q = session.createQuery(hql.toString(), Object[].class);
            q.setParameter("trung", "%TRÚNG%");
            if (!ma.isEmpty()) {
                q.setParameter("maNganh", ma);
            }

            List<Object[]> raw = q.list();
            List<TrungTuyenRow> rows = new ArrayList<>();
            if (raw == null) {
                return rows;
            }
            for (Object[] o : raw) {
                NguyenVong nv = (NguyenVong) o[0];
                ThiSinh ts = (ThiSinh) o[1];
                Nganh ng = (Nganh) o[2];
                TrungTuyenRow row = new TrungTuyenRow();
                row.setCccd(nv.getTsCccd());
                row.setHo(ts != null && ts.getHo() != null ? ts.getHo() : "");
                row.setTen(ts != null && ts.getTen() != null ? ts.getTen() : "");
                row.setThuTuNv(nv.getThuTuNV());
                row.setMaNganh(nv.getMaNganh());
                row.setTenNganh(ng.getTennganh() != null ? ng.getTennganh() : "");
                row.setPhuongThuc(nv.getPhuongThuc());
                row.setMaToHop(nv.getMaToHop());
                row.setThm(nv.getDiemThxt());
                row.setDiemXetTuyen(nv.getDiemXetTuyen());
                row.setKetQua(nv.getKetQua());
                rows.add(row);
            }
            return rows;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /** Thống kê số trúng theo PT và chỉ tiêu từng ngành. */
    public List<ThongKeTrungTuyenRow> thongKeTrungTuyenTheoNganh() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Nganh> nganhs = session.createQuery("FROM Nganh ng ORDER BY ng.manganh", Nganh.class).list();
            List<NguyenVong> trungList = session.createQuery(
                    "FROM NguyenVong nv WHERE nv.ketQua LIKE :trung ORDER BY nv.maNganh",
                    NguyenVong.class)
                    .setParameter("trung", "%TRÚNG%")
                    .list();

            List<ThongKeTrungTuyenRow> result = new ArrayList<>();
            if (nganhs == null) {
                return result;
            }

            for (Nganh ng : nganhs) {
                if (ng.getManganh() == null) {
                    continue;
                }
                int pt1 = 0, pt2 = 0, pt3 = 0;
                if (trungList != null) {
                    for (NguyenVong nv : trungList) {
                        if (!ng.getManganh().equals(nv.getMaNganh())) {
                            continue;
                        }
                        String loai = XetTuyenService.resolveLoaiXetTuyen(nv.getPhuongThuc());
                        if ("DGNL".equals(loai)) {
                            pt2++;
                        } else if ("VSAT".equals(loai)) {
                            pt3++;
                        } else {
                            pt1++;
                        }
                    }
                }

                ThongKeTrungTuyenRow row = new ThongKeTrungTuyenRow();
                row.setMaNganh(ng.getManganh());
                row.setTenNganh(ng.getTennganh() != null ? ng.getTennganh() : "");
                row.setChiTieu(ng.getnChitieu() != null ? ng.getnChitieu() : 0);
                row.setTrungPt1(pt1);
                row.setTrungPt2(pt2);
                row.setTrungPt3(pt3);
                row.setTongTrung(pt1 + pt2 + pt3);
                row.setSlThptCt(parseSlThpt(ng.getSlThpt()));
                row.setSlDgnlCt(ng.getSlDgnl() != null ? ng.getSlDgnl() : 0);
                row.setSlVsatCt(ng.getSlVsat() != null ? ng.getSlVsat() : 0);
                result.add(row);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
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

    public List<DiemXetTuyenRow> getDiemXetTuyenPage(int page, int pageSize, String keyword) {
        List<DiemXetTuyenRow> all = buildDiemXetTuyenRows(keyword);
        if (all.isEmpty()) {
            return all;
        }
        int from = Math.max(0, (page - 1) * pageSize);
        if (from >= all.size()) {
            return new ArrayList<>();
        }
        int to = Math.min(from + pageSize, all.size());
        return new ArrayList<>(all.subList(from, to));
    }

    private static boolean matchesKeyword(String kw, String cccd, ThiSinh ts) {
        if (kw.isEmpty()) {
            return true;
        }
        if (cccd != null && cccd.contains(kw)) {
            return true;
        }
        if (ts != null) {
            String hoTen = ((ts.getHo() != null ? ts.getHo() : "") + " " + (ts.getTen() != null ? ts.getTen() : ""))
                    .trim();
            return hoTen.toLowerCase().contains(kw.toLowerCase());
        }
        return false;
    }

    private static String formatTenToHopThmCaoNhat(String maToHop, Map<String, ToHopMon> toHopByMa) {
        if (maToHop == null || maToHop.trim().isEmpty()) {
            return "—";
        }
        ToHopMon th = toHopByMa.get(maToHop.trim());
        if (th != null && th.getTentohop() != null && !th.getTentohop().trim().isEmpty()) {
            return th.getTentohop().trim();
        }
        return maToHop.trim();
    }

    private static String formatDiemChuaQuyDoi(String phuongThuc, DiemThi dt) {
        if (dt == null) {
            return "—";
        }
        String loai = XetTuyenService.resolveLoaiXetTuyen(phuongThuc);
        if ("DGNL".equals(loai)) {
            return dt.getNl1() != null ? formatNum(dt.getNl1()) : "—";
        }
        if ("VSAT".equals(loai)) {
            return formatVsatDiemGoc(dt);
        }
        return "—";
    }

    private static String formatVsatDiemGoc(DiemThi dt) {
        StringBuilder sb = new StringBuilder();
        appendVsatPart(sb, "TO", dt.getVsatTo());
        appendVsatPart(sb, "LI", dt.getVsatLi());
        appendVsatPart(sb, "HO", dt.getVsatHo());
        appendVsatPart(sb, "SI", dt.getVsatSi());
        appendVsatPart(sb, "SU", dt.getVsatSu());
        appendVsatPart(sb, "DI", dt.getVsatDi());
        appendVsatPart(sb, "VA", dt.getVsatVa());
        appendVsatPart(sb, "N1", dt.getVsatN1());
        return sb.length() == 0 ? "—" : sb.toString();
    }

    private static void appendVsatPart(StringBuilder sb, String mon, Double diem) {
        if (diem != null && diem > 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(mon).append(":").append(formatNum(diem));
        }
    }

    private static String formatNum(double v) {
        if (v == (long) v) {
            return String.valueOf((long) v);
        }
        return String.format("%.2f", v);
    }
}