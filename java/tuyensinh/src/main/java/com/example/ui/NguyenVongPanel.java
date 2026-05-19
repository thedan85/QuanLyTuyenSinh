package com.example.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.example.dao.NganhDAO;
import com.example.dao.NguyenVongDAO;
import com.example.dao.ThiSinhDAO;
import com.example.dao.ToHopDAO;
import com.example.entity.Nganh;
import com.example.entity.NguyenVong;
import com.example.entity.ToHopMon;
import com.example.service.XetTuyenService;

public class NguyenVongPanel extends JPanel {
    private JTable table;
    private JScrollPane tableScroll;
    private DefaultTableModel tableModel;
    private NguyenVongDAO dao;
    private ThiSinhDAO thiSinhDAO;
    private NganhDAO nganhDAO;
    private ToHopDAO toHopDAO;

    // CHỈ GIỮ LẠI CÁC TRƯỜNG NHẬP LIỆU CƠ BẢN
    private JTextField txtIdnv, txtNvTt;
    private JComboBox<String> cbPhuongThuc;
    private JComboBox<String> cbCccd, cbMaNganh, cbMaToHop;

    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnImport;
    private JButton btnRunAlgo, btnDanhSachTrung, btnThongKeTrung;

    public NguyenVongPanel() {
        dao = new NguyenVongDAO();
        thiSinhDAO = new ThiSinhDAO();
        nganhDAO = new NganhDAO();
        toHopDAO = new ToHopDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- 1. FORM NHẬP LIỆU ---
        txtIdnv = new JTextField();
        txtIdnv.setEditable(false);
        cbCccd = new JComboBox<>();
        cbMaNganh = new JComboBox<>();
        cbMaToHop = new JComboBox<>();
        txtNvTt = new JTextField();
        cbPhuongThuc = PhuongThucOptions.newCombo();
        cbPhuongThuc.setPrototypeDisplayValue("PT3 - V-SAT (VSAT_*)");
        cbCccd.setPrototypeDisplayValue("001204000001 - Nguyễn Văn A");
        cbMaNganh.setPrototypeDisplayValue("7480201 - Công nghệ thông tin");
        cbMaToHop.setPrototypeDisplayValue("-- Chọn tổ hợp --");

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Nhập thông tin Nguyện Vọng cơ bản"),
                new EmptyBorder(12, 14, 12, 14)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormField(formPanel, gbc, 0, 0, "ID NV:", txtIdnv);
        addFormField(formPanel, gbc, 0, 1, "CCCD (*):", cbCccd);
        addFormField(formPanel, gbc, 0, 2, "Mã Ngành (*):", cbMaNganh);
        addFormField(formPanel, gbc, 1, 0, "Thứ tự NV (*):", txtNvTt);
        addFormField(formPanel, gbc, 1, 1, "Phương thức (*):", cbPhuongThuc);
        addFormField(formPanel, gbc, 1, 2, "Tổ hợp:", cbMaToHop);

        add(formPanel, BorderLayout.NORTH);

        // --- 2. THANH CÔNG CỤ (2 hàng: CRUD + Xét tuyển) ---
        JPanel crudBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        btnAdd = new JButton("Thêm mới");
        btnEdit = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới form");
        btnImport = new JButton("Import CSV");
        crudBar.add(btnAdd);
        crudBar.add(btnEdit);
        crudBar.add(btnDelete);
        crudBar.add(btnRefresh);
        crudBar.add(btnImport);
        UiButtons.stylePrimary(btnAdd);
        UiButtons.stylePrimary(btnEdit);
        UiButtons.styleSecondary(btnImport);
        UiButtons.styleDanger(btnDelete);
        UiButtons.styleSecondary(btnRefresh);
        UiButtons.equalizeHeightsOnly(btnAdd, btnEdit, btnDelete, btnRefresh, btnImport);

        JPanel xetBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        xetBar.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                "Thao tác xét tuyển",
                TitledBorder.LEFT,
                TitledBorder.TOP));
        btnRunAlgo = new JButton("🚀 CHẠY XÉT TUYỂN");
        btnDanhSachTrung = new JButton("Danh sách trúng tuyển");
        btnThongKeTrung = new JButton("Thống kê trúng tuyển");
        xetBar.add(btnRunAlgo);
        xetBar.add(btnDanhSachTrung);
        xetBar.add(btnThongKeTrung);
        UiButtons.stylePrimary(btnRunAlgo);
        UiButtons.styleSecondary(btnDanhSachTrung);
        UiButtons.styleSecondary(btnThongKeTrung);
        UiButtons.equalizeHeightsOnly(btnRunAlgo, btnDanhSachTrung, btnThongKeTrung);

        JPanel toolNorth = new JPanel();
        toolNorth.setLayout(new BoxLayout(toolNorth, BoxLayout.Y_AXIS));
        toolNorth.add(crudBar);
        toolNorth.add(xetBar);

        // --- 3. BẢNG DỮ LIỆU (VẪN GIỮ ĐỦ 12 CỘT ĐỂ XEM KẾT QUẢ) ---
        String[] cols = { "ID", "CCCD", "Mã Ngành", "Thứ tự", "Điểm THXT", "Điểm UTQD", "Cộng", "Tổng Điểm", "Kết quả", "Keys", "PT", "Tổ hợp" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        UiTableTheme.apply(table);

        // Tô màu Trúng Tuyển / Rớt (nền zebra/hover do UiTableTheme)
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                UiTableTheme.applyColumnAlignment(table, c, column);
                UiTableTheme.applyDataRowAppearance(table, c, row, isSelected);
                if (!isSelected) {
                    String colName = table.getColumnName(column);
                    if (colName != null && colName.toLowerCase().contains("kết quả") && value != null) {
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

        tableScroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        UiTableColumns.install(table, tableScroll);

        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.add(toolNorth, BorderLayout.NORTH);
        centerContainer.add(tableScroll, BorderLayout.CENTER);
        add(centerContainer, BorderLayout.CENTER);

        loadComboBoxData();
        setupEvents();
        loadData();

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                Object selCccd = cbCccd.getSelectedItem();
                Object selNganh = cbMaNganh.getSelectedItem();
                Object selToHop = cbMaToHop.getSelectedItem();
                loadComboBoxData();
                if (selCccd != null) cbCccd.setSelectedItem(selCccd);
                if (selNganh != null) cbMaNganh.setSelectedItem(selNganh);
                if (selToHop != null) cbMaToHop.setSelectedItem(selToHop);
                UiTableColumns.refresh(table);
            }
        });
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
        UiTableColumns.refresh(table);
    }

    private void loadComboBoxData() {
        cbCccd.removeAllItems();
        cbCccd.addItem("-- Chọn CCCD --");
        List<com.example.entity.ThiSinh> listThiSinh = thiSinhDAO.getAllThiSinh();
        if (listThiSinh != null) {
            for (com.example.entity.ThiSinh ts : listThiSinh) {
                String cccd = ts.getCccd();
                if (cccd != null && !cccd.trim().isEmpty()) {
                    cbCccd.addItem(cccd);
                }
            }
        }

        cbMaNganh.removeAllItems();
        cbMaNganh.addItem("-- Chọn Ngành --");
        List<Nganh> listNganh = nganhDAO.getAllNganh();
        if (listNganh != null) {
            for (Nganh n : listNganh) {
                cbMaNganh.addItem(n.getManganh());
            }
        }

        cbMaToHop.removeAllItems();
        cbMaToHop.addItem("-- Chọn Tổ hợp --");
        List<ToHopMon> listToHop = toHopDAO.getAllToHop();
        if (listToHop != null) {
            for (ToHopMon th : listToHop) {
                cbMaToHop.addItem(th.getMatohop());
            }
        }
    }

    private String getSelectedValue(JComboBox<String> combo) {
        Object sel = combo.getSelectedItem();
        if (sel == null) {
            return "";
        }
        String value = sel.toString().trim();
        return value.startsWith("--") ? "" : value;
    }

    private void selectComboItem(JComboBox<String> combo, String value) {
        if (value == null || value.isEmpty()) {
            combo.setSelectedIndex(0);
            return;
        }
        boolean found = false;
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (value.equals(combo.getItemAt(i))) {
                found = true;
                break;
            }
        }
        if (!found) {
            combo.addItem(value);
        }
        combo.setSelectedItem(value);
    }

    private void clearForm() {
        txtIdnv.setText("");
        cbCccd.setSelectedIndex(0);
        cbMaNganh.setSelectedIndex(0);
        cbMaToHop.setSelectedIndex(0);
        txtNvTt.setText("");
        PhuongThucOptions.select(cbPhuongThuc, PhuongThucOptions.PT1);
        cbCccd.setEnabled(true);
        cbMaNganh.setEnabled(true);
        cbPhuongThuc.setEnabled(true);
        table.clearSelection();
    }

    private void setupEvents() {
        btnRefresh.addActionListener(e -> { loadData(); clearForm(); });

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                // Chỉ lấy các dữ liệu cơ bản đắp lên Form
                txtIdnv.setText(table.getValueAt(row, 0).toString());
                selectComboItem(cbCccd, table.getValueAt(row, 1).toString());
                selectComboItem(cbMaNganh, table.getValueAt(row, 2).toString());
                txtNvTt.setText(table.getValueAt(row, 3).toString());
                PhuongThucOptions.select(cbPhuongThuc,
                        table.getValueAt(row, 10) != null ? table.getValueAt(row, 10).toString() : "");
                selectComboItem(cbMaToHop, table.getValueAt(row, 11) != null ? table.getValueAt(row, 11).toString() : "");
                
                cbCccd.setEnabled(false);
                cbMaNganh.setEnabled(false);
                cbPhuongThuc.setEnabled(false);
            }
        });

        btnAdd.addActionListener(e -> {
            NguyenVong nv = getDataFromForm();
            if (nv == null) return;

            if (dao.isThuTuExists(nv.getTsCccd(), nv.getThuTuNV())) {
                JOptionPane.showMessageDialog(this, "Thí sinh này đã dùng thứ tự nguyện vọng này rồi!");
                return;
            }

            if (dao.isNganhToHopExists(nv.getTsCccd(), nv.getMaNganh(), nv.getMaToHop())) {
                JOptionPane.showMessageDialog(this, "Thí sinh này đã đăng ký ngành + tổ hợp này rồi!");
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
            if (nv == null) return;

            if (dao.isThuTuExistsExcept(nv.getTsCccd(), nv.getThuTuNV(), nv.getIdnv())) {
                JOptionPane.showMessageDialog(this, "Thứ tự nguyện vọng bị trùng trong cùng thí sinh!");
                return;
            }

            if (dao.isNganhToHopExistsExcept(nv.getTsCccd(), nv.getMaNganh(), nv.getMaToHop(), nv.getIdnv())) {
                JOptionPane.showMessageDialog(this, "Thí sinh này đã đăng ký ngành + tổ hợp này rồi!");
                return;
            }

            if (dao.update(nv)) { loadData(); clearForm(); }
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

        btnDanhSachTrung.addActionListener(e -> TrungTuyenTheoNganhDialog.showDialog(this));
        btnThongKeTrung.addActionListener(e -> ThongKeTrungTuyenDialog.showDialog(this));

        btnRunAlgo.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Tính lại điểm và xét trúng/rớt cho tất cả nguyện vọng. Tiếp tục?",
                "Xác nhận Xét Tuyển",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
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
            String cccd = getSelectedValue(cbCccd);
            String maNganh = getSelectedValue(cbMaNganh);
            String maToHop = getSelectedValue(cbMaToHop);

            String phuongThuc = PhuongThucOptions.getCode(cbPhuongThuc);
            if (cccd.isEmpty() || maNganh.isEmpty() || phuongThuc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ CCCD, Mã Ngành, Phương thức!");
                return null;
            }

            if (!thiSinhDAO.isCccdExists(cccd)) {
                JOptionPane.showMessageDialog(this, "CCCD chưa tồn tại trong bảng thí sinh!");
                return null;
            }
            if (!nganhDAO.isMaNganhExists(maNganh)) {
                JOptionPane.showMessageDialog(this, "Mã ngành không tồn tại!");
                return null;
            }
            if (!maToHop.isEmpty() && !toHopDAO.isMaToHopExists(maToHop)) {
                JOptionPane.showMessageDialog(this, "Tổ hợp môn không tồn tại!");
                return null;
            }

            NguyenVong nv = new NguyenVong();
            if (!txtIdnv.getText().isEmpty()) nv.setIdnv(Integer.parseInt(txtIdnv.getText()));

            // Chỉ lấy 5 thông tin cơ bản
            nv.setTsCccd(cccd);
            nv.setMaNganh(maNganh);
            nv.setThuTuNV(Integer.parseInt(txtNvTt.getText().trim()));
            nv.setPhuongThuc(phuongThuc);
            nv.setMaToHop(maToHop);

            // CÁC CỘT CÒN LẠI TỰ GÁN VỀ 0 HOẶC TRỐNG ĐỂ CHỜ THUẬT TOÁN TÍNH
            nv.setDiemThxt(0.0);
            nv.setDiemUtqd(0.0);
            nv.setDiemCong(0.0);
            nv.setDiemXetTuyen(0.0);
            nv.setKetQua("Chờ xét");

            nv.setNvKeys(nv.getTsCccd() + "_" + nv.getMaNganh() + "_" + nv.getMaToHop());
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
            int success = 0, duplicate = 0, invalid = 0;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                String[] data = line.split(",", -1);
                
                if (data.length >= 5) { 
                    NguyenVong nv = new NguyenVong();
                    nv.setTsCccd(data[0].trim());
                    nv.setMaNganh(data[1].trim());
                    nv.setThuTuNV(data[2].trim().isEmpty() ? 99 : Integer.parseInt(data[2].trim()));
                    nv.setPhuongThuc(PhuongThucOptions.toCode(data[3]));
                    nv.setMaToHop(data[4].trim());

                    if (nv.getTsCccd().isEmpty() || nv.getMaNganh().isEmpty() || nv.getPhuongThuc().isEmpty()) {
                        invalid++;
                        continue;
                    }
                    if (!thiSinhDAO.isCccdExists(nv.getTsCccd())) {
                        invalid++;
                        continue;
                    }
                    if (!nganhDAO.isMaNganhExists(nv.getMaNganh())) {
                        invalid++;
                        continue;
                    }
                    if (!nv.getMaToHop().isEmpty() && !toHopDAO.isMaToHopExists(nv.getMaToHop())) {
                        invalid++;
                        continue;
                    }
                    if (dao.isThuTuExists(nv.getTsCccd(), nv.getThuTuNV())) {
                        duplicate++;
                        continue;
                    }

                    // Các cột điểm gán mặc định
                    nv.setDiemThxt(0.0); nv.setDiemUtqd(0.0); nv.setDiemCong(0.0); nv.setDiemXetTuyen(0.0);
                    nv.setKetQua("Chờ xét");
                    nv.setNvKeys(nv.getTsCccd() + "_" + nv.getMaNganh() + "_" + nv.getMaToHop());

                    if (!dao.isNganhToHopExists(nv.getTsCccd(), nv.getMaNganh(), nv.getMaToHop())) {
                        if (dao.add(nv)) success++;
                    } else duplicate++;
                }
            }
            JOptionPane.showMessageDialog(this, "Import xong!\nThành công: " + success + "\nBỏ qua trùng: " + duplicate + "\nKhông hợp lệ: " + invalid);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi CSV! Yêu cầu file có 5 cột: CCCD, Ngành, TT, PT, Tổ hợp");
        }
    }

    private static void addFormField(JPanel form, GridBagConstraints gbc, int row, int col,
            String label, Component field) {
        gbc.gridy = row;
        gbc.gridx = col * 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        form.add(new JLabel(label), gbc);

        gbc.gridx = col * 2 + 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(field, gbc);
    }
}