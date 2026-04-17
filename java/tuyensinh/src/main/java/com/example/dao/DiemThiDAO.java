package com.example.dao;

import com.example.entity.DiemThi;
import com.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class DiemThiDAO {

    public List<DiemThi> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM DiemThi ORDER BY cccd", DiemThi.class).list();
        } catch (Exception e) { e.printStackTrace(); return null; }
    }

    // Kiểm tra xem thí sinh này đã có dòng điểm nào trong DB chưa
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

    public boolean addDiem(DiemThi d) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(d);
            tx.commit();
            return true;
        } catch (Exception e) { 
            if (tx != null) tx.rollback(); 
            e.printStackTrace();
            return false; 
        }
    }

    public boolean updateDiem(DiemThi d) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(d);
            tx.commit();
            return true;
        } catch (Exception e) { 
            if (tx != null) tx.rollback(); 
            e.printStackTrace();
            return false; 
        }
    }

    public boolean deleteDiem(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            DiemThi d = session.get(DiemThi.class, id);
            if (d != null) { session.delete(d); tx.commit(); return true; }
            return false;
        } catch (Exception e) { 
            if (tx != null) tx.rollback(); 
            e.printStackTrace();
            return false; 
        }
    }
}