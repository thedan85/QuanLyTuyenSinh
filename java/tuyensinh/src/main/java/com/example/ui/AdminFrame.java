package com.example.ui;

import com.example.entity.User;
import javax.swing.*;
import java.awt.*;

public class AdminFrame extends JFrame {
    private User loggedInUser;

    public AdminFrame(User user) {
        this.loggedInUser = user;

        setTitle("Hệ Thống Quản Lý Tuyển Sinh - Xin chào: " + loggedInUser.getUsername());
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // --- PHÂN QUYỀN HIỂN THỊ TAB ---
        if ("admin".equals(loggedInUser.getRole())) {
            // Chỉ Admin mới được quản lý tài khoản nhân viên
            tabbedPane.addTab("Quản lý Người dùng", new UserPanel());
            
            // Chỉ Admin mới được quyền chạy thuật toán Xét tuyển chốt danh sách
            tabbedPane.addTab("Xét tuyển & Nguyện vọng", new NguyenVongPanel()); 
        }

        // Các tab dùng chung cho cả Admin và User (Giáo vụ)
        tabbedPane.addTab("Quản lý Tổ hợp môn", new ToHopPanel());
        tabbedPane.addTab("Quản lý Ngành", new NganhPanel());
        tabbedPane.addTab("Quản lý Ngành - Tổ hợp", new NganhToHopPanel());
        tabbedPane.addTab("Quản lý Thí sinh", new ThiSinhPanel());
        tabbedPane.addTab("Quản lý Bảng quy đổi", new BangQuyDoiPanel());
        tabbedPane.addTab("Quản lý Điểm thi", new DiemThiPanel());
        tabbedPane.addTab("Quản lý Điểm cộng", new DiemCongPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // --- PHẦN SOUTH: PANEL ĐĂNG XUẤT ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); 
        
        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setForeground(Color.RED); 
        
        bottomPanel.add(btnLogout);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- SỰ KIỆN ĐĂNG XUẤT ---
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn đăng xuất?", 
                "Xác nhận", 
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            }
        });
    }
}