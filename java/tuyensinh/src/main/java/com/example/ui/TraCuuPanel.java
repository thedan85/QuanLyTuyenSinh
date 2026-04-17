package com.example.ui;

import com.example.dao.NguyenVongDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TraCuuPanel extends JPanel {
    private JTextField txtSearch;
    private JButton btnTraCuu;
    private JLabel lblHoTen, lblCccd, lblSbd;
    private JTable table;
    private DefaultTableModel tableModel;
    private NguyenVongDAO dao;

    public TraCuuPanel() {
        dao = new NguyenVongDAO();
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- 1. HEADER & THANH TÌM KIẾM ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        
        JLabel lblTitle = new JLabel("CỔNG TRA CỨU KẾT QUẢ TUYỂN SINH 2026", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 102, 204));
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.add(new JLabel("Nhập CCCD hoặc Số Báo Danh:"));
        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnTraCuu = new JButton("🔍 Tra cứu");
        btnTraCuu.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnTraCuu.setBackground(new Color(0, 153, 51));
      
        
        searchPanel.add(txtSearch);
        searchPanel.add(btnTraCuu);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        // --- 2. THÔNG TIN THÍ SINH ---
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        infoPanel.setBorder(new TitledBorder(" Thông tin Thí sinh "));
        lblHoTen = new JLabel("Họ tên: ");
        lblCccd = new JLabel("CCCD: ");
        lblSbd = new JLabel("SBD: ");
        lblHoTen.setFont(new Font("SansSerif", Font.BOLD, 14));
        infoPanel.add(lblHoTen); infoPanel.add(lblCccd); infoPanel.add(lblSbd);
        topPanel.add(infoPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // --- 3. BẢNG KẾT QUẢ ---
        String[] cols = {"Nguyện Vọng", "Mã Ngành", "Tên Ngành", "Tổ Hợp", "Điểm Xét Tuyển", "Trạng Thái Kết Quả"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));

        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(250); // Tên ngành dài hơn
        
        // Căn giữa cột Nguyện vọng và Tổ hợp
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        // Tô màu kết quả
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 5 && value != null) {
                    if (value.toString().contains("TRÚNG TUYỂN")) {
                        c.setForeground(new Color(0, 150, 0)); c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else if (value.toString().contains("Rớt")) {
                        c.setForeground(Color.RED);
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- 4. SỰ KIỆN NÚT TRA CỨU ---
        btnTraCuu.addActionListener(e -> thucHienTraCuu());
        // Cho phép ấn Enter ở ô text để tra cứu luôn
        txtSearch.addActionListener(e -> thucHienTraCuu());
    }

    private void thucHienTraCuu() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập CCCD hoặc Số báo danh!");
            return;
        }

        List<Object[]> results = dao.traCuuTheoCccdHoacSbd(keyword);
        tableModel.setRowCount(0); // Xóa dữ liệu cũ trên bảng

        if (results == null || results.isEmpty()) {
            lblHoTen.setText("Họ tên: KHÔNG TÌM THẤY");
            lblCccd.setText("CCCD: -");
            lblSbd.setText("SBD: -");
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu xét tuyển cho từ khóa: " + keyword);
        } else {
            // Lấy thông tin thí sinh từ dòng đầu tiên
            Object[] firstRow = results.get(0);
            lblCccd.setText("CCCD: " + firstRow[0]);
            lblSbd.setText("SBD: " + (firstRow[1] != null ? firstRow[1] : ""));
            lblHoTen.setText("Họ tên: " + firstRow[2] + " " + firstRow[3]);

            // Đổ danh sách nguyện vọng vào bảng
            for (Object[] row : results) {
                tableModel.addRow(new Object[]{
                    "NV " + row[4], // Thứ tự NV
                    row[5],         // Mã ngành
                    row[6],         // Tên ngành
                    row[7],         // Tổ hợp
                    row[8],         // Điểm xét tuyển
                    row[9]          // Kết quả
                });
            }
        }
    }
}