package com.example.ui;

import com.example.dao.UserDAO;
import com.example.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private UserDAO userDAO;

    public UserPanel() {
        userDAO = new UserDAO();
        setLayout(new BorderLayout());

        // 1. Tạo thanh công cụ (Toolbar) chứa các nút
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnAdd = new JButton("Thêm User");
        btnAdd.addActionListener(e -> {
            // Lấy frame cha để gắn dialog vào
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            AddUserDialog dialog = new AddUserDialog(parentFrame, userDAO);
            dialog.setVisible(true); // Mở cửa sổ

            // Code sẽ dừng ở đây cho đến khi dialog bị tắt
            // Kiểm tra xem nếu thêm thành công thì load lại bảng
            if (dialog.isAdded()) {
                loadDataToTable();
            }
        });

        JButton btnEdit = new JButton("Đổi Quyền / Trạng thái");
        btnEdit.addActionListener(e -> {
            // 1. Lấy dòng đang được chọn trên bảng
            int selectedRow = table.getSelectedRow();
            
            // Nếu chưa chọn dòng nào
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản từ bảng để sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Lấy ID của user từ cột đầu tiên (cột số 0)
            int userId = (int) table.getValueAt(selectedRow, 0);
            
            // 3. Truy vấn User từ database để đảm bảo dữ liệu mới nhất
            User selectedUser = userDAO.getUserById(userId);

            if (selectedUser != null) {
                // 4. Mở hộp thoại sửa
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                EditUserDialog dialog = new EditUserDialog(parentFrame, userDAO, selectedUser);
                dialog.setVisible(true);

                // 5. Nếu cập nhật thành công, tải lại dữ liệu lên bảng
                if (dialog.isUpdated()) {
                    loadDataToTable();
                }
            }
        });

        JButton btnResetPass = new JButton("Đổi Mật Khẩu");
        // Sự kiện cho nút Đổi Mật Khẩu
        btnResetPass.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản từ bảng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int userId = (int) table.getValueAt(selectedRow, 0);
            User selectedUser = userDAO.getUserById(userId);

            if (selectedUser != null) {
                // Hiển thị hộp thoại nhập mật khẩu mới
                String newPassword = JOptionPane.showInputDialog(this, 
                        "Nhập mật khẩu mới cho tài khoản [" + selectedUser.getUsername() + "]:", 
                        "Đổi Mật Khẩu", 
                        JOptionPane.QUESTION_MESSAGE);

                // Nếu người dùng nhập pass và bấm OK (không bấm Cancel)
                if (newPassword != null && !newPassword.trim().isEmpty()) {
                    selectedUser.setPassword(newPassword.trim()); // Cập nhật pass mới
                    
                    if (userDAO.updateUser(selectedUser)) {
                        JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi lưu vào database!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JButton btnRefresh = new JButton("Làm mới");

        topPanel.add(btnAdd);
        topPanel.add(btnEdit);
        topPanel.add(btnResetPass);
        topPanel.add(btnRefresh);
        add(topPanel, BorderLayout.NORTH);

        // 2. Tạo bảng hiển thị dữ liệu
        String[] columnNames = {"ID", "Tên đăng nhập", "Mật khẩu", "Quyền", "Trạng thái hoạt động"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép sửa trực tiếp trên ô
            }
        };
        table = new JTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Tải dữ liệu lên bảng
        loadDataToTable();

        // 4. Gắn sự kiện cho nút Làm mới (Các nút khác ta sẽ xử lý logic sau)
        btnRefresh.addActionListener(e -> loadDataToTable());
    }

    // Hàm đổ dữ liệu từ DB vào JTable
    private void loadDataToTable() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        List<User> users = userDAO.getAllUsers();
        if (users != null) {
            for (User u : users) {
                Object[] row = {
                    u.getId(),
                    u.getUsername(),
                    u.getPassword(),
                    u.getRole(),
                    u.isIsActive() ? "Đang hoạt động" : "Bị khóa"
                };
                tableModel.addRow(row);
            }
        }
    }
}