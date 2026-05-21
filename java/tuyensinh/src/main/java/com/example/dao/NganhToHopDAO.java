package com.example.dao;

import com.example.entity.NganhToHop;
import com.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class NganhToHopDAO {

    public List<NganhToHop> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM NganhToHop", NganhToHop.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<NganhToHop> findByMaNganh(String maNganh) {
        if (maNganh == null || maNganh.trim().isEmpty()) {
            return List.of();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<NganhToHop> q = session.createQuery(
                    "FROM NganhToHop n WHERE n.manganh = :ma ORDER BY n.matohop", NganhToHop.class);
            q.setParameter("ma", maNganh.trim());
            return q.list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // Kiểm tra xem Ngành này đã có Tổ hợp này chưa
    public boolean isMappingExists(String tbKeys) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(n) FROM NganhToHop n WHERE n.tbKeys = :tbKeys";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("tbKeys", tbKeys);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public boolean addMapping(NganhToHop nth) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(nth);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMapping(NganhToHop nth) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(nth);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMapping(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            NganhToHop nth = session.get(NganhToHop.class, id);
            if (nth != null) {
                session.delete(nth);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }
}