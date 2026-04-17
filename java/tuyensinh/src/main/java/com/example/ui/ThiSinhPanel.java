package com.example.ui;

import com.example.dao.ThiSinhDAO;
import com.example.entity.ThiSinh;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class ThiSinhPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private ThiSinhDAO dao;

    private JTextField txtSearch;
    private JButton btnSearch, btnImport, btnEdit;
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

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm CCCD hoặc Họ tên:"));
        txtSearch = new JTextField(20);
        btnSearch = new JButton("Tìm kiếm");
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnEdit = new JButton("Sửa thông tin");
        btnImport = new JButton("Import CSV");
        actionPanel.add(btnEdit);
        actionPanel.add(btnImport);

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
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- PHẦN BOTTOM: PHÂN TRANG ---
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPrev = new JButton("<< Trước");
        btnNext = new JButton("Sau >>");
        lblPageInfo = new JLabel("Trang 1 / 1");

        paginationPanel.add(btnPrev);
        paginationPanel.add(lblPageInfo);
        paginationPanel.add(btnNext);
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
                importCSV(fc.getSelectedFile());
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
    }

    private String getSafeString(String[] data, int index) {
        return (index < data.length && data[index] != null) ? data[index].trim() : "";
    }

    private void importCSV(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line; boolean first = true;
            int success = 0, duplicate = 0;
            long currentTime = System.currentTimeMillis();

            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                String[] data = line.split(",", -1);
                
                String cccd = getSafeString(data, 0);
                if (cccd.isEmpty()) continue;

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

                if (!dao.isCccdExists(ts.getCccd())) {
                    if (dao.addThiSinh(ts)) success++;
                } else {
                    duplicate++;
                }
            }
            JOptionPane.showMessageDialog(this, "Import xong!\nThành công: " + success + "\nBỏ qua (trùng CCCD): " + duplicate);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc file CSV! Định dạng không hợp lệ.");
            ex.printStackTrace();
        }
    }
}