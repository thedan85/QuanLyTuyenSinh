package com.example.ui;

import com.example.dao.ThiSinhDAO;
import com.example.entity.ThiSinh;

import javax.swing.*;
import java.awt.*;

public class EditThiSinhDialog extends JDialog {
    private boolean isUpdated = false;

    // Khai báo các trường nhập liệu
    private JTextField txtCccd, txtSbd, txtHo, txtTen, txtNgaySinh;
    private JTextField txtDienThoai, txtPassword, txtGioiTinh, txtEmail;
    private JTextField txtNoiSinh, txtDoiTuong, txtKhuVuc;
    
    private JButton btnSave, btnCancel;

    public EditThiSinhDialog(JFrame parent, ThiSinhDAO dao, ThiSinh ts) {
        super(parent, "Sửa thông tin Thí sinh", true);
        setSize(450, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- 1. FORM NHẬP LIỆU ---
        JPanel formPanel = new JPanel(new GridLayout(12, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        formPanel.add(new JLabel("CCCD (Không được sửa):"));
        txtCccd = new JTextField(ts.getCccd());
        txtCccd.setEditable(false);
        txtCccd.setBackground(new Color(235, 235, 235));
        formPanel.add(txtCccd);

        formPanel.add(new JLabel("Số báo danh:"));
        txtSbd = new JTextField(ts.getSobaodanh());
        formPanel.add(txtSbd);

        formPanel.add(new JLabel("Họ:"));
        txtHo = new JTextField(ts.getHo());
        formPanel.add(txtHo);

        formPanel.add(new JLabel("Tên:"));
        txtTen = new JTextField(ts.getTen());
        formPanel.add(txtTen);

        formPanel.add(new JLabel("Ngày sinh:"));
        txtNgaySinh = new JTextField(ts.getNgaySinh());
        formPanel.add(txtNgaySinh);

        formPanel.add(new JLabel("Điện thoại:"));
        txtDienThoai = new JTextField(ts.getDienThoai());
        formPanel.add(txtDienThoai);

        // --- Ô MẬT KHẨU MỚI THÊM VÀO ---
        formPanel.add(new JLabel("Mật khẩu đăng nhập:"));
        txtPassword = new JTextField(ts.getPassword());
        txtPassword.setForeground(Color.RED); // In đỏ cho dễ chú ý
        formPanel.add(txtPassword);

        formPanel.add(new JLabel("Giới tính:"));
        txtGioiTinh = new JTextField(ts.getGioiTinh());
        formPanel.add(txtGioiTinh);

        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField(ts.getEmail());
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Nơi sinh:"));
        txtNoiSinh = new JTextField(ts.getNoiSinh());
        formPanel.add(txtNoiSinh);

        formPanel.add(new JLabel("Đối tượng:"));
        txtDoiTuong = new JTextField(ts.getDoiTuong());
        formPanel.add(txtDoiTuong);

        formPanel.add(new JLabel("Khu vực:"));
        txtKhuVuc = new JTextField(ts.getKhuVuc());
        formPanel.add(txtKhuVuc);

        add(formPanel, BorderLayout.CENTER);

        // --- 2. NÚT BẤM ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Lưu thay đổi");
        btnCancel = new JButton("Hủy");

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- 3. SỰ KIỆN ---
        btnCancel.addActionListener(e -> dispose());

        btnSave.addActionListener(e -> {
            // Cập nhật lại dữ liệu vào object ts
            ts.setSobaodanh(txtSbd.getText().trim());
            ts.setHo(txtHo.getText().trim());
            ts.setTen(txtTen.getText().trim());
            ts.setNgaySinh(txtNgaySinh.getText().trim());
            ts.setDienThoai(txtDienThoai.getText().trim());
            ts.setPassword(txtPassword.getText().trim()); // Lưu lại mật khẩu
            ts.setGioiTinh(txtGioiTinh.getText().trim());
            ts.setEmail(txtEmail.getText().trim());
            ts.setNoiSinh(txtNoiSinh.getText().trim());
            ts.setDoiTuong(txtDoiTuong.getText().trim());
            ts.setKhuVuc(txtKhuVuc.getText().trim());

            if (dao.updateThiSinh(ts)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                isUpdated = true;
                dispose(); // Đóng popup
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại, vui lòng kiểm tra lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Dùng để panel cha biết có cần tải lại bảng hay không
    public boolean isUpdated() {
        return isUpdated;
    }
}