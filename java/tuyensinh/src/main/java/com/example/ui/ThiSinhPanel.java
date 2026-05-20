package com.example.ui;

import com.example.dao.ThiSinhDAO;
import com.example.entity.ThiSinh;
//import com.oracle.graal.vector.nodes.consumer.ac;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ThiSinhPanel extends JPanel {
    private JTable table;
    private JScrollPane tableScroll;
    private DefaultTableModel tableModel;
    private ThiSinhDAO dao;

    private JTextField txtSearch;
    private JButton btnSearch, btnImport, btnEdit, btnThongKe, btnDetail;
    private JButton btnPrev, btnNext;
    private JLabel lblPageInfo;

    private int currentPage = 1;
    private final int PAGE_SIZE = 20; // 20 row/page theo đúng yêu cầu
    private int totalPages = 1;
    private String currentKeyword = "";

    public ThiSinhPanel() {
        dao = new ThiSinhDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- PHẦN TOP: TÌM KIẾM & CHỨC NĂNG ---
        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 8));
        txtSearch = new JTextField(18);
        txtSearch.setFont(txtSearch.getFont().deriveFont(Font.PLAIN, 18f));
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập CCCD hoặc họ tên...");
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(11, 15, 11, 15)));
        btnSearch = new JButton("Tìm kiếm");
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        UiButtons.stylePrimary(btnSearch);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnThongKe = new JButton("Thống kê");
        btnDetail = new JButton("Xem chi tiết");

        btnEdit = new JButton("Sửa");
        btnImport = new JButton("Import CSV");

        actionPanel.add(btnEdit);
        actionPanel.add(btnImport);
        actionPanel.add(btnThongKe);
        actionPanel.add(btnDetail);

        UiButtons.stylePrimary(btnEdit);
        UiButtons.styleSecondary(btnImport);
        UiButtons.equalizeButtonsInContainer(actionPanel);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // --- PHẦN CENTER: BẢNG DỮ LIỆU ---
        // Đã THÊM CỘT "Mật khẩu" VÀO ĐÂY
        String[] columns = { "ID", "CCCD", "SBD", "Họ", "Tên", "Ngày sinh", "SĐT", "Mật khẩu", "Giới tính", "Email", "Nơi Sinh", "Đối tượng", "Khu Vực" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        UiTableTheme.apply(table);
        tableScroll = new JScrollPane(table);
        UiTableColumns.install(table, tableScroll);
        add(tableScroll, BorderLayout.CENTER);

        // --- PHẦN BOTTOM: PHÂN TRANG ---
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPrev = new JButton("<< Trước");
        btnNext = new JButton("Sau >>");
        lblPageInfo = new JLabel("Trang 1 / 1");

        paginationPanel.add(btnPrev);
        paginationPanel.add(lblPageInfo);
        paginationPanel.add(btnNext);
        UiButtons.styleSecondary(btnPrev);
        UiButtons.styleSecondary(btnNext);
        UiButtons.equalizeButtonSizes(btnPrev, btnNext);
        add(paginationPanel, BorderLayout.SOUTH);

        // --- SỰ KIỆN ---
        setupEvents();
        loadData(); // Load lần đầu
    }

    private void loadData() {
        // Cập nhật tổng số trang
        long totalRecords = dao.getTotalThiSinhs(currentKeyword);
        totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;

        if (currentPage > totalPages) currentPage = totalPages;

        lblPageInfo.setText("Trang " + currentPage + " / " + totalPages);
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);

        // Đổ dữ liệu vào bảng
        tableModel.setRowCount(0);
        List<ThiSinh> list = dao.getThiSinhs(currentPage, PAGE_SIZE, currentKeyword);
        if (list != null) {
            for (ThiSinh t : list) {
                // Đã THÊM t.getPassword() VÀO MẢNG DỮ LIỆU ĐỂ HIỆN LÊN BẢNG
                tableModel.addRow(new Object[] {
                        t.getIdthisinh(), t.getCccd(), t.getSobaodanh(), t.getHo(), t.getTen(),
                        t.getNgaySinh(), t.getDienThoai(), t.getPassword(), t.getGioiTinh(), t.getEmail(), t.getNoiSinh(),
                        t.getDoiTuong(), t.getKhuVuc()
                });
            }
        }
        UiTableColumns.refresh(table);
    }

    private void setupEvents() {
        btnSearch.addActionListener(e -> {
            currentKeyword = txtSearch.getText().trim();
            currentPage = 1;
            loadData();
        });

        btnPrev.addActionListener(e -> {
            if (currentPage > 1) { currentPage--; loadData(); }
        });
        btnNext.addActionListener(e -> {
            if (currentPage < totalPages) { currentPage++; loadData(); }
        });

        btnImport.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                importCSVAsync(fc.getSelectedFile());
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 thí sinh để sửa!");
                return;
            }

            int id = (int) table.getValueAt(row, 0);
            ThiSinh ts = dao.getThiSinhById(id);

            if (ts != null) {
                JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
                EditThiSinhDialog dialog = new EditThiSinhDialog(parent, dao, ts);
                dialog.setVisible(true);

                if (dialog.isUpdated()) {
                    loadData();
                }
            }
        });
        
        btnDetail.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 thí sinh trên bảng để xem chi tiết!");
                return;
            }
            int id = (int) table.getValueAt(row, 0);
            ThiSinh ts = dao.getThiSinhById(id);
            if (ts != null) {
                JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
                new ThiSinhDetailDialog(parent, ts).setVisible(true);
            }
        });

        btnThongKe.addActionListener(e -> {
            long total = dao.getTotalThiSinhs(""); // Lấy tổng thí sinh
            List<Object[]> statKhuVuc = dao.getThongKe("khuVuc");
            List<Object[]> statDoiTuong = dao.getThongKe("doiTuong");

            StringBuilder sb = new StringBuilder();
            sb.append("=== THỐNG KÊ HỒ SƠ THÍ SINH ===\n\n");
            sb.append("Tổng số thí sinh trong hệ thống: ").append(total).append("\n\n");
            
            sb.append("--- Phân bố theo KHU VỰC ---\n");
            if (statKhuVuc != null) {
                for (Object[] row : statKhuVuc) {
                    sb.append("- Khu vực ").append(row[0] != null ? row[0] : "Trống").append(": ").append(row[1]).append(" thí sinh\n");
                }
            }

            sb.append("\n--- Phân bố theo ĐỐI TƯỢNG ƯU TIÊN ---\n");
            if (statDoiTuong != null) {
                for (Object[] row : statDoiTuong) {
                    sb.append("- Đối tượng ").append(row[0] != null ? row[0] : "Trống").append(": ").append(row[1]).append(" thí sinh\n");
                }
            }

            // Hiển thị ra hộp thoại
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.BOLD, 14));
            textArea.setBackground(new Color(245, 245, 245));
            textArea.setMargin(new Insets(10, 10, 10, 10));
            
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Bảng Thống Kê", JOptionPane.INFORMATION_MESSAGE);
        });

    }

    private String getSafeString(List<String> data, int index) {
        return (index < data.size() && data.get(index) != null) ? data.get(index).trim() : "";
    }

    private static class ImportResult {
        private final int success;
        private final int duplicate;
        private final int invalid;

        private ImportResult(int success, int duplicate, int invalid) {
            this.success = success;
            this.duplicate = duplicate;
            this.invalid = invalid;
        }
    }

    private List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        fields.add(current.toString().trim());
        return fields;
    }

    private void importCSVAsync(File file) {
        btnImport.setEnabled(false);
        SwingWorker<ImportResult, Void> worker = new SwingWorker<>() {
            @Override
            protected ImportResult doInBackground() throws Exception {
                return importCSV(file);
            }

            @Override
            protected void done() {
                btnImport.setEnabled(true);
                try {
                    ImportResult result = get();
                    JOptionPane.showMessageDialog(ThiSinhPanel.this,
                            "Import xong!\nThành công: " + result.success +
                                    "\nBỏ qua (trùng CCCD): " + result.duplicate +
                                    "\nLỗi/thiếu dữ liệu: " + result.invalid);
                    loadData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ThiSinhPanel.this, "Lỗi đọc file CSV! Định dạng không hợp lệ.");
                    ex.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    private ImportResult importCSV(File file) throws Exception {
        int success = 0;
        int duplicate = 0;
        int invalid = 0;
        long currentTime = System.currentTimeMillis();
        Set<String> existingCccds = dao.getAllCccdSet();
        if (existingCccds == null) {
            existingCccds = new HashSet<>();
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                if (line.trim().isEmpty()) { continue; }

                List<String> data = parseCsvLine(line);
                String cccd = getSafeString(data, 0);
                if (cccd.startsWith("\uFEFF")) {
                    cccd = cccd.substring(1);
                }
                if (cccd.isEmpty()) { invalid++; continue; }

                if (existingCccds.contains(cccd)) {
                    duplicate++;
                    continue;
                }

                ThiSinh ts = new ThiSinh();
                ts.setCccd(cccd);
                ts.setSobaodanh(getSafeString(data, 1));
                ts.setHo(getSafeString(data, 2));
                ts.setTen(getSafeString(data, 3));
                ts.setNgaySinh(getSafeString(data, 4));
                ts.setDienThoai(getSafeString(data, 5));

                // MẬT KHẨU: Nếu file CSV không có cột mật khẩu hoặc để trống, set mặc định là 123456
                String pass = getSafeString(data, 6);
                ts.setPassword(pass.isEmpty() ? "123456" : pass);

                ts.setGioiTinh(getSafeString(data, 7));
                ts.setEmail(getSafeString(data, 8));
                ts.setNoiSinh(getSafeString(data, 9));

                String dateStr = getSafeString(data, 10);
                try {
                    ts.setUpdatedAt(dateStr.isEmpty() ? new java.sql.Date(currentTime) : java.sql.Date.valueOf(dateStr));
                } catch (Exception e) {
                    ts.setUpdatedAt(new java.sql.Date(currentTime));
                }

                ts.setDoiTuong(getSafeString(data, 11));
                ts.setKhuVuc(getSafeString(data, 12));

                if (dao.addThiSinh(ts)) {
                    success++;
                    existingCccds.add(cccd);
                } else {
                    invalid++;
                }
            }
        }

        return new ImportResult(success, duplicate, invalid);
    }
}