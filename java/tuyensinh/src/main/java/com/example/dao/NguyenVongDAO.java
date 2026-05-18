package com.example.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.example.entity.NguyenVong;
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
}