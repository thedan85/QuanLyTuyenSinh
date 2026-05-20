

package com.example.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.example.entity.DiemThi;
import com.example.utils.HibernateUtil;

public class DiemThiDAO {

    // Các hàm chỉ đọc (SELECT) không cần Transaction nên dùng try-with-resources vẫn an toàn
    public List<DiemThi> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM DiemThi ORDER BY cccd", DiemThi.class).list();
        } catch (Exception e) { 
            e.printStackTrace(); 
            return null; 
        }
    }

    public boolean isCccdExists(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(d) FROM DiemThi d WHERE d.cccd = :cccd";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("cccd", cccd);
            return query.uniqueResult() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
            return true; 
        }
    }

    // Đã sửa thành try-catch-finally truyền thống để an toàn khi rollback
    public boolean addDiem(DiemThi d) {
        Transaction tx = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            tx = session.beginTransaction();
            session.save(d);
            tx.commit();
            return true;
        } catch (Exception e) { 
            if (tx != null && tx.isActive()) {
                tx.rollback(); 
            }
            System.err.println("LỖI KHI THÊM ĐIỂM: ");
            e.printStackTrace(); // In ra lỗi thật sự để dễ debug
            return false; 
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public boolean updateDiem(DiemThi d) {
        Transaction tx = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            tx = session.beginTransaction();
            session.update(d);
            tx.commit();
            return true;
        } catch (Exception e) { 
            if (tx != null && tx.isActive()) {
                tx.rollback(); 
            }
            System.err.println("LỖI KHI CẬP NHẬT ĐIỂM: ");
            e.printStackTrace();
            return false; 
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public boolean deleteDiem(int id) {
        Transaction tx = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            tx = session.beginTransaction();
            DiemThi d = session.get(DiemThi.class, id);
            if (d != null) { 
                session.delete(d); 
                tx.commit(); 
                return true; 
            }
            return false;
        } catch (Exception e) { 
            if (tx != null && tx.isActive()) {
                tx.rollback(); 
            }
            System.err.println("LỖI KHI XÓA ĐIỂM: ");
            e.printStackTrace();
            return false; 
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<DiemThi> getDiemByCccd(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM DiemThi WHERE cccd = :cccd";
            return session.createQuery(hql, DiemThi.class)
                          .setParameter("cccd", cccd).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}