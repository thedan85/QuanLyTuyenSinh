package com.example.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.example.entity.ThiSinh;
import com.example.utils.HibernateUtil;

public class ThiSinhDAO {

    // Lấy danh sách có Tìm kiếm và Phân trang (20 dòng/trang)
    public List<ThiSinh> getThiSinhs(int page, int pageSize, String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM ThiSinh t WHERE t.cccd LIKE :kw OR CONCAT(t.ho, ' ', t.ten) LIKE :kw ORDER BY t.idthisinh";
            Query<ThiSinh> query = session.createQuery(hql, ThiSinh.class);
            query.setParameter("kw", "%" + keyword + "%");
            
            // Xử lý phân trang
            query.setFirstResult((page - 1) * pageSize); // Vị trí bắt đầu
            query.setMaxResults(pageSize); // Số lượng lấy
            
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Đếm tổng số thí sinh để tính tổng số trang
    public long getTotalThiSinhs(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(t) FROM ThiSinh t WHERE t.cccd LIKE :kw OR CONCAT(t.ho, ' ', t.ten) LIKE :kw";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("kw", "%" + keyword + "%");
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<ThiSinh> getAllThiSinh() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM ThiSinh ORDER BY cccd", ThiSinh.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ThiSinh getThiSinhByCccd(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM ThiSinh t WHERE t.cccd = :cccd";
            Query<ThiSinh> query = session.createQuery(hql, ThiSinh.class);
            query.setParameter("cccd", cccd);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ThiSinh getThiSinhBySbd(String sbd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM ThiSinh t WHERE t.sobaodanh = :sbd";
            Query<ThiSinh> query = session.createQuery(hql, ThiSinh.class);
            query.setParameter("sbd", sbd);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isCccdExists(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(t) FROM ThiSinh t WHERE t.cccd = :cccd";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("cccd", cccd);
            return query.uniqueResult() > 0;
        } catch (Exception e) { return true; }
    }

    public boolean addThiSinh(ThiSinh ts) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(ts);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return false;
        }
    }

    public boolean updateThiSinh(ThiSinh ts) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(ts);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return false;
        }
    }

    // Lấy thông tin thí sinh theo ID
    public ThiSinh getThiSinhById(int idthisinh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(ThiSinh.class, idthisinh);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Thống kê số lượng thí sinh theo khu vực hoặc đối tượng
    public List<Object[]> getThongKe(String columnName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // columnName sẽ truyền vào là "khuVuc" hoặc "doiTuong"
            String hql = "SELECT t." + columnName + ", COUNT(t) FROM ThiSinh t GROUP BY t." + columnName;
            return session.createQuery(hql, Object[].class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}