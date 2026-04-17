package com.example.dao;

import com.example.entity.ThiSinh;
import com.example.entity.User;
import com.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.Transaction;
import java.util.List;

public class UserDAO {
    // Hàm kiểm tra đăng nhập
    public Object authenticateUser(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // 1. Kiểm tra trong bảng xt_users trước (Admin/Mod)
            User admin = session.createQuery("FROM User WHERE username = :u AND password = :p", User.class)
                    .setParameter("u", username).setParameter("p", password).uniqueResult();
            if (admin != null)
                return admin;

            // 2. Nếu không có, kiểm tra trong bảng xt_thisinhxettuyen25 (Thí sinh)
            ThiSinh ts = session.createQuery("FROM ThiSinh WHERE cccd = :u AND password = :p", ThiSinh.class)
                    .setParameter("u", username).setParameter("p", password).uniqueResult();
            return ts;
        } catch (Exception e) {
            return null;
        }
    }

    // Hàm lấy tất cả người dùng (dành cho admin)
    public List<User> getAllUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // HQL lấy tất cả user
            return session.createQuery("FROM User", User.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Kiểm tra xem tên đăng nhập đã tồn tại chưa
    public boolean isUsernameExists(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(u) FROM User u WHERE u.username = :username";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("username", username);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Nếu lỗi cứ coi như đã tồn tại cho an toàn
        }
    }

    // Thêm người dùng mới
    public boolean addUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(user); // Lệnh lưu của Hibernate
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback(); // Lỗi thì hoàn tác
            e.printStackTrace();
            return false;
        }
    }

    // Lấy thông tin user theo ID
    public User getUserById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id); // Hibernate hỗ trợ sẵn hàm get theo Khóa chính
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Cập nhật thông tin người dùng
    public boolean updateUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(user); // Lệnh update của Hibernate
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }
}