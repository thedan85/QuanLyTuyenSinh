package com.example.dao;

import com.example.entity.ToHopMon;
import com.example.dao.ToHopDAO;
import com.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ToHopDAO {

    // 1. Lấy danh sách tất cả tổ hợp môn
    public List<ToHopMon> getAllToHop() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM ToHopMon", ToHopMon.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 2. Kiểm tra mã tổ hợp đã tồn tại chưa
    public boolean isMaToHopExists(String matohop) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(t) FROM ToHopMon t WHERE t.matohop = :matohop";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("matohop", matohop);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    // 3. Thêm mới
    public boolean addToHop(ToHopMon tohop) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(tohop);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // 4. Cập nhật
    public boolean updateToHop(ToHopMon tohop) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(tohop);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // 5. Xóa (Sử dụng ID)
    public boolean deleteToHop(int idtohop) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            ToHopMon tohop = session.get(ToHopMon.class, idtohop);
            if (tohop != null) {
                session.delete(tohop);
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