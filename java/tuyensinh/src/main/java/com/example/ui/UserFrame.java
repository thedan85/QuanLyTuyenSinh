package com.example.ui;

import com.example.entity.ThiSinh;
import javax.swing.*;
import java.awt.*;

public class UserFrame extends JFrame {
    // private ThiSinh currentTs;

    public UserFrame(ThiSinh ts) {
        // this.currentTs = ts;
        setTitle("Cổng thông tin Thí sinh - SGU");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Thiết lập Layout chính cho Frame
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        
        // Tab 1: Tra cứu công khai (như cũ)
        tabs.addTab("Xem Kết Quả Chung", new TraCuuPanel());

        // Tab 2: Quản lý nguyện vọng cá nhân
        tabs.addTab("Đăng Ký Nguyện Vọng", new StudentNguyenVongPanel(ts));

        // Đặt Tabs vào giữa màn hình
        add(tabs, BorderLayout.CENTER);

        // --- PHẦN SOUTH: PANEL ĐĂNG XUẤT (Giống hệt AdminFrame) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Căn lề cho đẹp
        
        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setForeground(Color.RED); // Chữ màu đỏ nổi bật
        
        bottomPanel.add(btnLogout);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- SỰ KIỆN ĐĂNG XUẤT ---
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn đăng xuất?", 
                "Xác nhận", 
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose(); // Đóng cửa sổ hiện tại
                // Mở lại form Login
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            }
        });
    }
}