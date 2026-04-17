package com.example.ui;

import com.example.dao.UserDAO;
import com.example.entity.User;

import javax.swing.*;
import java.awt.*;

public class AddUserDialog extends JDialog {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;
    private boolean isAdded = false; // Cờ kiểm tra xem có thêm thành công không

    public AddUserDialog(JFrame parent, UserDAO userDAO) {
        super(parent, "Thêm Người Dùng Mới", true); // true = Modal (bắt buộc thao tác xong mới quay lại màn hình chính được)
        setSize(300, 200);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("Tên đăng nhập:"));
        txtUsername = new JTextField();
        panel.add(txtUsername);

        panel.add(new JLabel("Mật khẩu:"));
        txtPassword = new JPasswordField();
        panel.add(txtPassword);

        panel.add(new JLabel("Quyền:"));
        String[] roles = {"user", "admin"};
        cbRole = new JComboBox<>(roles);
        panel.add(cbRole);

        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");

        panel.add(btnSave);
        panel.add(btnCancel);
        add(panel);

        // Sự kiện nút Hủy
        btnCancel.addActionListener(e -> dispose());

        // Sự kiện nút Lưu
        btnSave.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            String role = (String) cbRole.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (userDAO.isUsernameExists(username)) {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tạo đối tượng User mới
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setRole(role);
            newUser.setIsActive(true); // Mặc định kích hoạt

            // Lưu vào DB
            if (userDAO.addUser(newUser)) {
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
                isAdded = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu vào cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public boolean isAdded() {
        return isAdded;
    }
}