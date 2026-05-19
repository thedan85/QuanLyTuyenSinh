package com.example.dao;

import com.example.entity.BangQuyDoi;
import com.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class BangQuyDoiDAO {

    public List<BangQuyDoi> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM BangQuyDoi", BangQuyDoi.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Lấy các bậc quy đổi theo phương thức (DGNL / VSAT) — dùng chung session với XetTuyenService. */
    public List<BangQuyDoi> findByPhuongThuc(org.hibernate.Session session, String phuongThuc) {
        return session.createQuery(
                "FROM BangQuyDoi b WHERE UPPER(TRIM(b.dPhuongthuc)) = :pt",
                BangQuyDoi.class)
                .setParameter("pt", phuongThuc.trim().toUpperCase())
                .list();
    }

    public boolean isMaQuyDoiExists(String maQuyDoi) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(b) FROM BangQuyDoi b WHERE b.dMaquydoi = :maQuyDoi";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("maQuyDoi", maQuyDoi);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean addBangQuyDoi(BangQuyDoi bqd) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(bqd);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBangQuyDoi(BangQuyDoi bqd) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(bqd);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBangQuyDoi(int idqd) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            BangQuyDoi bqd = session.get(BangQuyDoi.class, idqd);
            if (bqd != null) {
                session.delete(bqd);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Hàm tìm kiếm bảng quy đổi
    public List<BangQuyDoi> searchBangQuyDoi(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Tìm kiếm tương đối (LIKE) trên 4 cột quan trọng
            String hql = "FROM BangQuyDoi b WHERE b.dMaquydoi LIKE :kw OR b.dPhuongthuc LIKE :kw OR b.dTohop LIKE :kw OR b.dMon LIKE :kw";
            Query<BangQuyDoi> query = session.createQuery(hql, BangQuyDoi.class);
            query.setParameter("kw", "%" + keyword + "%");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}