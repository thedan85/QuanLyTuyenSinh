package com.example.ui;

import com.example.dao.UserDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        JButton btnTraCuuNhanh = new JButton("Tra cứu nhanh");
        btnTraCuuNhanh.setForeground(new Color(0, 102, 204));

        setTitle("Hệ Thống Xét Tuyển");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Tạo các thành phần giao diện
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Tài khoản:"));
        txtUsername = new JTextField();
        panel.add(txtUsername);

        panel.add(new JLabel("Mật khẩu:"));
        txtPassword = new JPasswordField();
        panel.add(txtPassword);

        panel.add(new JLabel("")); // Ô trống để căn chỉnh
        btnLogin = new JButton("Đăng nhập");
        panel.add(btnLogin);

        panel.add(new JLabel("")); // Ô trống
        panel.add(btnTraCuuNhanh);

        // Thêm panel vào khung
        add(panel);

        // Bắt sự kiện khi click nút Đăng nhập
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Sự kiện nút Tra cứu nhanh
        btnTraCuuNhanh.addActionListener(e -> {
            JFrame searchFrame = new JFrame("Tra Cứu Kết Quả Tuyển Sinh");
            searchFrame.setSize(800, 500);
            searchFrame.setLocationRelativeTo(null);
            searchFrame.add(new TraCuuPanel()); // Sử dụng lại Panel tra cứu bạn đã làm
            searchFrame.setVisible(true);
        });
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tài khoản và mật khẩu!");
            return;
        }

        // Gọi hàm trả về Object (có thể là User hoặc ThiSinh)
        Object result = userDAO.authenticateUser(username, password);

        if (result != null) {
            this.dispose(); // Đóng form login

            if (result instanceof com.example.entity.User) {
                // TRƯỜNG HỢP 1: LÀ ADMIN
                com.example.entity.User admin = (com.example.entity.User) result;
                JOptionPane.showMessageDialog(null, "Đăng nhập ADMIN thành công!");
                new AdminFrame(admin).setVisible(true);
            } 
            else if (result instanceof com.example.entity.ThiSinh) {
                // TRƯỜNG HỢP 2: LÀ THÍ SINH
                com.example.entity.ThiSinh ts = (com.example.entity.ThiSinh) result;
                JOptionPane.showMessageDialog(null, "Chào thí sinh: " + ts.getHo() + " " + ts.getTen());
                new UserFrame(ts).setVisible(true); // Mở giao diện dành riêng cho thí sinh
            }
        } else {
            JOptionPane.showMessageDialog(this, "Tài khoản hoặc mật khẩu không chính xác!");
        }
    }
}