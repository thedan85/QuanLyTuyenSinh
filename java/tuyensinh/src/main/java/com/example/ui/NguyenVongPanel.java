package com.example.ui;

import com.example.dao.NguyenVongDAO;
import com.example.entity.NguyenVong;
import com.example.service.XetTuyenService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class NguyenVongPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private NguyenVongDAO dao;

    // CHỈ GIỮ LẠI CÁC TRƯỜNG NHẬP LIỆU CƠ BẢN
    private JTextField txtIdnv, txtNnCccd, txtNvManganh, txtNvTt;
    private JTextField txtTtPhuongthuc, txtTtThm;

    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnImport, btnRunAlgo;

    public NguyenVongPanel() {
        dao = new NguyenVongDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- 1. FORM NHẬP LIỆU THU GỌN (GridLayout 2 hàng x 6 cột) ---
        JPanel formPanel = new JPanel(new GridLayout(2, 6, 10, 10));
        formPanel.setBorder(new TitledBorder(" Nhập thông tin Nguyện Vọng cơ bản "));

        txtIdnv = new JTextField(); txtIdnv.setEditable(false);
        txtNnCccd = new JTextField(); 
        txtNvManganh = new JTextField(); 
        txtNvTt = new JTextField();
        txtTtPhuongthuc = new JTextField(); 
        txtTtThm = new JTextField();

        // Hàng 1
        formPanel.add(new JLabel("ID NV:")); formPanel.add(txtIdnv);
        formPanel.add(new JLabel("CCCD (*):")); formPanel.add(txtNnCccd);
        formPanel.add(new JLabel("Mã Ngành (*):")); formPanel.add(txtNvManganh);

        // Hàng 2
        formPanel.add(new JLabel("Thứ tự NV (*):")); formPanel.add(txtNvTt);
        formPanel.add(new JLabel("Phương thức (*):")); formPanel.add(txtTtPhuongthuc);
        formPanel.add(new JLabel("Tổ hợp:")); formPanel.add(txtTtThm);

        add(formPanel, BorderLayout.NORTH);

        // --- 2. THANH CÔNG CỤ ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdd = new JButton("Thêm mới");
        btnEdit = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới form");
        btnImport = new JButton("Import CSV");
        
        btnRunAlgo = new JButton("🚀 CHẠY XÉT TUYỂN");
        // Thay vì set Background, ta chỉ cần set màu chữ nổi bật
        btnRunAlgo.setForeground(new Color(220, 53, 69)); // Màu đỏ
        btnRunAlgo.setFont(new Font("SansSerif", Font.BOLD, 14));

        btnPanel.add(btnAdd); btnPanel.add(btnEdit); btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh); btnPanel.add(btnImport); 
        btnPanel.add(new JLabel(" | "));
        btnPanel.add(btnRunAlgo);

        // --- 3. BẢNG DỮ LIỆU (VẪN GIỮ ĐỦ 12 CỘT ĐỂ XEM KẾT QUẢ) ---
        String[] cols = { "ID", "CCCD", "Mã Ngành", "Thứ tự", "Điểm THXT", "Điểm UTQD", "Cộng", "Tổng Điểm", "Kết quả", "Keys", "PT", "Tổ hợp" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
        table.setRowHeight(25);

        // Thiết lập độ rộng cột
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // CCCD
        table.getColumnModel().getColumn(8).setPreferredWidth(120); // Kết quả
        table.getColumnModel().getColumn(9).setPreferredWidth(150); // Keys

        // Tô màu Trúng Tuyển / Rớt
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                    if (column == 8 && value != null) {
                        if (value.toString().contains("TRÚNG TUYỂN")) {
                            c.setForeground(new Color(0, 150, 0)); c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else if (value.toString().contains("Rớt")) {
                            c.setForeground(Color.RED); c.setFont(c.getFont().deriveFont(Font.PLAIN));
                        } else { c.setForeground(Color.BLACK); c.setFont(c.getFont().deriveFont(Font.PLAIN)); }
                    } else { c.setForeground(Color.BLACK); c.setFont(c.getFont().deriveFont(Font.PLAIN)); }
                }
                return c;
            }
        });

        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.add(btnPanel, BorderLayout.NORTH);
        centerContainer.add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
        add(centerContainer, BorderLayout.CENTER);

        setupEvents();
        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<NguyenVong> list = dao.getAll();
        if (list != null) {
            for (NguyenVong n : list) {
                tableModel.addRow(new Object[] {
                    n.getIdnv(), n.getTsCccd(), n.getMaNganh(), n.getThuTuNV(),
                    n.getDiemThxt(), n.getDiemUtqd(), n.getDiemCong(), n.getDiemXetTuyen(),
                    n.getKetQua() == null ? "Chờ xét" : n.getKetQua(), 
                    n.getNvKeys(), n.getPhuongThuc(), n.getMaToHop()
                });
            }
        }
    }

    private void clearForm() {
        txtIdnv.setText(""); txtNnCccd.setText(""); txtNvManganh.setText("");
        txtNvTt.setText(""); txtTtPhuongthuc.setText(""); txtTtThm.setText("");
        txtNnCccd.setEditable(true); txtNvManganh.setEditable(true); txtTtPhuongthuc.setEditable(true);
        table.clearSelection();
    }

    private void setupEvents() {
        btnRefresh.addActionListener(e -> { loadData(); clearForm(); });

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                // Chỉ lấy các dữ liệu cơ bản đắp lên Form
                txtIdnv.setText(table.getValueAt(row, 0).toString());
                txtNnCccd.setText(table.getValueAt(row, 1).toString());
                txtNvManganh.setText(table.getValueAt(row, 2).toString());
                txtNvTt.setText(table.getValueAt(row, 3).toString());
                txtTtPhuongthuc.setText(table.getValueAt(row, 10).toString()); // Lưu ý index cột PT là 10
                txtTtThm.setText(table.getValueAt(row, 11) != null ? table.getValueAt(row, 11).toString() : ""); // Tố hợp index 11
                
                // Khóa bộ 3 Key không cho sửa
                txtNnCccd.setEditable(false); txtNvManganh.setEditable(false); txtTtPhuongthuc.setEditable(false);
            }
        });

        btnAdd.addActionListener(e -> {
            NguyenVong nv = getDataFromForm();
            if (nv == null) return;

            if (dao.isKeyExists(nv.getNvKeys())) {
                JOptionPane.showMessageDialog(this, "Thí sinh này đã đăng ký nguyện vọng ngành và phương thức này rồi!");
                return;
            }

            if (dao.add(nv)) {
                JOptionPane.showMessageDialog(this, "Thêm nguyện vọng thô thành công. Vui lòng bấm 'Chạy Xét Tuyển' để tính điểm!");
                loadData(); clearForm();
            }
        });

        btnEdit.addActionListener(e -> {
            if (txtIdnv.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Chọn nguyện vọng để sửa!"); return;
            }
            NguyenVong nv = getDataFromForm();
            if (nv != null && dao.update(nv)) { loadData(); clearForm(); }
        });

        btnDelete.addActionListener(e -> {
            if (txtIdnv.getText().isEmpty()) return;
            if (JOptionPane.showConfirmDialog(this, "Xóa nguyện vọng này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == 0) {
                if (dao.delete(Integer.parseInt(txtIdnv.getText()))) { loadData(); clearForm(); }
            }
        });

        btnImport.addActionListener(e -> {
            // Mở hộp thoại chọn file và Import (Logic import đã tối giản ở phần dưới)
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                importCSV(fc.getSelectedFile());
            }
        });

        // TÍCH HỢP GỌI SERVICE Ở ĐÂY
        btnRunAlgo.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Hệ thống sẽ móc nối điểm thi, điểm cộng và tự động tính toán trúng tuyển cho TẤT CẢ nguyện vọng.\nTiếp tục?", 
                "Xác nhận Xét Tuyển", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    XetTuyenService service = new XetTuyenService();
                    service.chayThuatToanXetTuyen();
                    loadData();
                    JOptionPane.showMessageDialog(this, "✅ Đã chạy thuật toán xong! Xem cột Kết quả.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi tính toán: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });
    }

    private NguyenVong getDataFromForm() {
        try {
            if (txtNnCccd.getText().isEmpty() || txtNvManganh.getText().isEmpty() || txtTtPhuongthuc.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ CCCD, Mã Ngành, Phương thức!");
                return null;
            }

            NguyenVong nv = new NguyenVong();
            if (!txtIdnv.getText().isEmpty()) nv.setIdnv(Integer.parseInt(txtIdnv.getText()));

            // Chỉ lấy 5 thông tin cơ bản
            nv.setTsCccd(txtNnCccd.getText().trim());
            nv.setMaNganh(txtNvManganh.getText().trim());
            nv.setThuTuNV(Integer.parseInt(txtNvTt.getText().trim()));
            nv.setPhuongThuc(txtTtPhuongthuc.getText().trim());
            nv.setMaToHop(txtTtThm.getText().trim());

            // CÁC CỘT CÒN LẠI TỰ GÁN VỀ 0 HOẶC TRỐNG ĐỂ CHỜ THUẬT TOÁN TÍNH
            nv.setDiemThxt(0.0);
            nv.setDiemUtqd(0.0);
            nv.setDiemCong(0.0);
            nv.setDiemXetTuyen(0.0);
            nv.setKetQua("Chờ xét");

            nv.setNvKeys(nv.getTsCccd() + "_" + nv.getMaNganh() + "_" + nv.getPhuongThuc());
            return nv;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Thứ tự NV phải là số nguyên!");
            return null;
        }
    }

    // Hàm Import CSV rút gọn (Chỉ đọc 5 cột cơ bản)
    private void importCSV(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line; boolean first = true;
            int success = 0, duplicate = 0;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                String[] data = line.split(",", -1);
                
                if (data.length >= 5) { 
                    NguyenVong nv = new NguyenVong();
                    nv.setTsCccd(data[0].trim());
                    nv.setMaNganh(data[1].trim());
                    nv.setThuTuNV(data[2].trim().isEmpty() ? 99 : Integer.parseInt(data[2].trim()));
                    nv.setPhuongThuc(data[3].trim());
                    nv.setMaToHop(data[4].trim());

                    // Các cột điểm gán mặc định
                    nv.setDiemThxt(0.0); nv.setDiemUtqd(0.0); nv.setDiemCong(0.0); nv.setDiemXetTuyen(0.0);
                    nv.setKetQua("Chờ xét");
                    nv.setNvKeys(nv.getTsCccd() + "_" + nv.getMaNganh() + "_" + nv.getPhuongThuc());

                    if (!dao.isKeyExists(nv.getNvKeys())) {
                        if (dao.add(nv)) success++;
                    } else duplicate++;
                }
            }
            JOptionPane.showMessageDialog(this, "Import xong!\nThành công: " + success + "\nBỏ qua trùng: " + duplicate);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi CSV! Yêu cầu file có 5 cột: CCCD, Ngành, TT, PT, Tổ hợp");
        }
    }
}