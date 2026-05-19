package com.example.ui;

import com.example.dao.NguyenVongDAO;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
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
    private JScrollPane tableScroll;
    private DefaultTableModel tableModel;
    private NguyenVongDAO dao;

    public static JLabel buildSearchTitleLabel() {
        JLabel lblTitle = new JLabel("CỔNG TRA CỨU KẾT QUẢ TUYỂN SINH 2026", SwingConstants.CENTER);
        Font base = UIManager.getFont("Label.font");
        if (base == null) {
            base = new Font("Segoe UI", Font.BOLD, 30);
        }
        lblTitle.setFont(base.deriveFont(Font.BOLD, 31f));
        lblTitle.setForeground(new Color(241, 245, 249));
        return lblTitle;
    }

    public TraCuuPanel() {
        dao = new NguyenVongDAO();
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- 1. HEADER & THANH TÌM KIẾM ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 14));
        txtSearch = new JTextField(15);
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 18));
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập CCCD hoặc Số Báo Danh...");
        Border fieldLine = BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(fieldLine, new EmptyBorder(11, 15, 11, 15)));
        btnTraCuu = new JButton("🔍 Tra cứu");
        UiButtons.stylePrimary(btnTraCuu);
        btnTraCuu.setFont(btnTraCuu.getFont().deriveFont(Font.BOLD, 16f));

        searchPanel.add(txtSearch);
        searchPanel.add(btnTraCuu);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        // --- 2. THÔNG TIN THÍ SINH ---
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 24, 8));
        TitledBorder infoBorder = new TitledBorder(" Thông tin Thí sinh ");
        infoBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 16));
        infoBorder.setTitleColor(new Color(71, 85, 105));
        infoPanel.setBorder(new CompoundBorder(infoBorder, new EmptyBorder(10, 14, 10, 14)));
        lblHoTen = new JLabel("Họ tên: ");
        lblCccd = new JLabel("CCCD: ");
        lblSbd = new JLabel("SBD: ");
        Font infoFont = new Font("Segoe UI", Font.PLAIN, 18);
        Color infoColor = new Color(30, 41, 59);
        lblHoTen.setFont(infoFont);
        lblCccd.setFont(infoFont);
        lblSbd.setFont(infoFont);
        lblHoTen.setForeground(infoColor);
        lblCccd.setForeground(infoColor);
        lblSbd.setForeground(infoColor);
        infoPanel.add(lblHoTen); infoPanel.add(lblCccd); infoPanel.add(lblSbd);
        topPanel.add(infoPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // --- 3. BẢNG KẾT QUẢ ---
        String[] cols = {"Nguyện Vọng", "Mã Ngành", "Tên Ngành", "Tổ Hợp", "Điểm Xét Tuyển", "Trạng Thái Kết Quả"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        // Trang tra cứu: padding ô dữ liệu lớn hơn mặc định.
        table.putClientProperty(UiTableTheme.CELL_PADDING_SCALE_KEY, 2);
        UiTableTheme.apply(table);
        centerAllColumnsAndHighlightResult(table);

        tableScroll = new JScrollPane(table);
        UiTableColumns.install(table, tableScroll);
        add(tableScroll, BorderLayout.CENTER);

        // --- 4. SỰ KIỆN NÚT TRA CỨU ---
        btnTraCuu.addActionListener(e -> thucHienTraCuu());
        // Cho phép ấn Enter ở ô text để tra cứu luôn
        txtSearch.addActionListener(e -> thucHienTraCuu());
    }

    private String getEffectiveSearchKeyword() {
        return txtSearch.getText().trim();
    }

    private void thucHienTraCuu() {
        String keyword = getEffectiveSearchKeyword();
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
        UiTableColumns.refresh(table);
    }

    private static void centerAllColumnsAndHighlightResult(JTable table) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                        boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                    UiTableTheme.applyColumnAlignment(t, c, column);
                    UiTableTheme.applyDataRowAppearance(t, c, row, isSelected);
                    String colName = t.getColumnName(column);
                    if (!isSelected && colName != null && colName.toLowerCase().contains("kết quả") && value != null) {
                        String s = value.toString().toUpperCase();
                        if (s.contains("TRÚNG TUYỂN")) {
                            c.setForeground(new Color(22, 163, 74));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else if (s.contains("RỚT") || s.contains("ROT")) {
                            c.setForeground(new Color(234, 88, 12));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        }
                    }
                    return c;
                }
            });
        }
    }
}