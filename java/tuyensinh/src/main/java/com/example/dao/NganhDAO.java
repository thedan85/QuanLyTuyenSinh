package com.example.dao;

import com.example.entity.Nganh;
import com.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class NganhDAO {

    public List<Nganh> getAllNganh() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Nganh", Nganh.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isMaNganhExists(String manganh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(n) FROM Nganh n WHERE n.manganh = :manganh";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("manganh", manganh);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public Nganh findByMaNganh(String manganh) {
        if (manganh == null || manganh.trim().isEmpty()) {
            return null;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Nganh> q = session.createQuery("FROM Nganh n WHERE n.manganh = :ma", Nganh.class);
            q.setParameter("ma", manganh.trim());
            return q.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addNganh(Nganh nganh) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(nganh);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateNganh(Nganh nganh) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(nganh);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteNganh(int idnganh) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Nganh nganh = session.get(Nganh.class, idnganh);
            if (nganh != null) {
                session.delete(nganh);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Lấy danh sách ngành kèm số lượng thí sinh đăng ký nguyện vọng
    public List<Object[]> getNganhWithRegistryCount() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Query bốc dữ liệu từ bảng Ngành và đếm số lượng bản ghi tương ứng bên bảng
            // Nguyện vọng
            String hql = "SELECT n, (SELECT COUNT(nv) FROM NguyenVong nv WHERE nv.maNganh = n.manganh) " +
                    "FROM Nganh n";
            return session.createQuery(hql, Object[].class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}