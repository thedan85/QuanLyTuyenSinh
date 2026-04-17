package com.example.ui;

import com.example.dao.UserDAO;
import com.example.entity.User;

import javax.swing.*;
import java.awt.*;

public class EditUserDialog extends JDialog {
    private JComboBox<String> cbRole;
    private JCheckBox chkActive;
    private boolean isUpdated = false;

    public EditUserDialog(JFrame parent, UserDAO userDAO, User currentUser) {
        super(parent, "Sửa Thông Tin Người Dùng", true);
        setSize(300, 200);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Tên đăng nhập (Chỉ hiển thị, không cho sửa)
        panel.add(new JLabel("Tên đăng nhập:"));
        JTextField txtUsername = new JTextField(currentUser.getUsername());
        txtUsername.setEditable(false); 
        panel.add(txtUsername);

        // Quyền
        panel.add(new JLabel("Quyền:"));
        String[] roles = {"user", "admin"};
        cbRole = new JComboBox<>(roles);
        cbRole.setSelectedItem(currentUser.getRole()); // Set giá trị hiện tại
        panel.add(cbRole);

        // Trạng thái hoạt động
        panel.add(new JLabel("Trạng thái:"));
        chkActive = new JCheckBox("Đang hoạt động");
        chkActive.setSelected(currentUser.isIsActive()); // Set giá trị hiện tại
        panel.add(chkActive);

        // Nút bấm
        JButton btnSave = new JButton("Cập nhật");
        JButton btnCancel = new JButton("Hủy");
        panel.add(btnSave);
        panel.add(btnCancel);

        add(panel);

        // Sự kiện nút Hủy
        btnCancel.addActionListener(e -> dispose());

        // Sự kiện nút Cập nhật
        btnSave.addActionListener(e -> {
            // Cập nhật dữ liệu mới vào object currentUser
            currentUser.setRole((String) cbRole.getSelectedItem());
            currentUser.setIsActive(chkActive.isSelected());

            // Lưu xuống database
            if (userDAO.updateUser(currentUser)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                isUpdated = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public boolean isUpdated() {
        return isUpdated;
    }
}