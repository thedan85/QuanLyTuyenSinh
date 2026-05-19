package com.example.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.example.dto.DiemXetTuyenRow;
import com.example.entity.DiemThi;
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
            if (dt.getNk1() != null && dt.getNk1() > 0) {
                return formatNum(dt.getNk1());
            }
            if (dt.getNk2() != null && dt.getNk2() > 0) {
                return formatNum(dt.getNk2());
            }
            return "—";
        }
        return "—";
    }

    private static String formatNum(double v) {
        if (v == (long) v) {
            return String.valueOf((long) v);
        }
        return String.format("%.2f", v);
    }
}