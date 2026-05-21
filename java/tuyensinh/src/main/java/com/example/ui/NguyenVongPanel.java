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
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.example.entity.Nganh;
import com.example.entity.NguyenVong;
import com.example.service.XetTuyenService;

public class NguyenVongPanel extends JPanel implements RefreshablePanel {
    private JTable table;
    private JScrollPane tableScroll;
    private DefaultTableModel tableModel;
    private NguyenVongDAO dao;
    private ThiSinhDAO thiSinhDAO;
    private NganhDAO nganhDAO;
    @SuppressWarnings("unused")
    private JTextField txtIdnv, txtNvTt;
    private JComboBox<String> cbCccd, cbMaNganh;
    private JComboBox<NvSwapOption> cbHoanDoi;

    private final Map<String, String> tenNganhByMa = new HashMap<>();

    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnImport;
    private JButton btnRunAlgo, btnDanhSachTrung, btnThongKeTrung;

    /** Mục combo hoán đổi TT: "2 - Sư phạm Toán" */
    private static final class NvSwapOption {
        final int idnv;
        final String label;

        NvSwapOption(int idnv, String label) {
            this.idnv = idnv;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public NguyenVongPanel() {
        dao = new NguyenVongDAO();
        thiSinhDAO = new ThiSinhDAO();
        nganhDAO = new NganhDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        txtIdnv = new JTextField();
        txtIdnv.setEditable(false);
        cbCccd = new JComboBox<>();
        cbMaNganh = new JComboBox<>();
        txtNvTt = new JTextField();
        txtNvTt.setEditable(false);
        cbHoanDoi = new JComboBox<>();
        cbHoanDoi.setPrototypeDisplayValue(new NvSwapOption(0, "9 - Tên ngành mẫu dài để hiển thị"));
        cbCccd.setPrototypeDisplayValue("001204000001");
        cbMaNganh.setPrototypeDisplayValue("7480201 - Công nghệ thông tin");

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Nhập / hoán đổi Nguyện Vọng (CCCD + Ngành; TT tự tăng khi thêm)"),
                new EmptyBorder(12, 14, 12, 14)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormField(formPanel, gbc, 0, 0, "ID NV:", txtIdnv);
        addFormField(formPanel, gbc, 0, 1, "CCCD (*):", cbCccd);
        addFormField(formPanel, gbc, 0, 2, "Mã Ngành (*):", cbMaNganh);
        addFormField(formPanel, gbc, 1, 0, "Thứ tự NV:", txtNvTt);
        addFormField(formPanel, gbc, 1, 1, "Hoán đổi với:", cbHoanDoi);

        add(formPanel, BorderLayout.NORTH);

        JPanel crudBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        btnAdd = new JButton("Thêm mới");
        btnEdit = new JButton("Cập nhật TT");
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

        String[] cols = { "ID", "CCCD", "Mã Ngành", "Thứ tự", "Điểm THXT", "Điểm UTQD", "Cộng", "Tổng Điểm",
                "Kết quả", "Keys", "PT", "Tổ hợp" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        UiTableTheme.apply(table);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                UiTableTheme.applyColumnAlignment(table, c, column);
                UiTableTheme.applyDataRowAppearance(table, c, row, isSelected);
                if (!isSelected) {
                    String colName = table.getColumnName(column);
                    if (colName != null && colName.toLowerCase().contains("kết quả") && value != null) {
                        if (value.toString().contains("TRÚNG TUYỂN")) {
                            c.setForeground(new Color(0, 150, 0));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else if (value.toString().contains("Rớt")) {
                            c.setForeground(Color.RED);
                            c.setFont(c.getFont().deriveFont(Font.PLAIN));
                        } else {
                            c.setForeground(Color.BLACK);
                            c.setFont(c.getFont().deriveFont(Font.PLAIN));
                        }
                    } else {
                        c.setForeground(Color.BLACK);
                        c.setFont(c.getFont().deriveFont(Font.PLAIN));
                    }
                }
                return c;
            }
        });

        tableScroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
                loadComboBoxData();
                if (selCccd != null) {
                    cbCccd.setSelectedItem(selCccd);
                }
                if (selNganh != null) {
                    cbMaNganh.setSelectedItem(selNganh);
                }
                refreshThuTuSuggest();
                refreshHoanDoiCombo();
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

    @Override
    public void refreshData() {
        Object selCccd = cbCccd.getSelectedItem();
        Object selNganh = cbMaNganh.getSelectedItem();
        loadComboBoxData();
        if (selCccd != null) {
            cbCccd.setSelectedItem(selCccd);
        }
        if (selNganh != null) {
            cbMaNganh.setSelectedItem(selNganh);
        }
        refreshThuTuSuggest();
        refreshHoanDoiCombo();
        loadData();
    }

    private void loadComboBoxData() {
        tenNganhByMa.clear();

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
                String ma = n.getManganh();
                String ten = n.getTennganh() != null ? n.getTennganh() : ma;
                tenNganhByMa.put(ma, ten);
                cbMaNganh.addItem(ma + " - " + ten);
            }
        }
    }

    private String getSelectedCccd() {
        return getSelectedValue(cbCccd);
    }

    private String parseMaNganhFromCombo() {
        String raw = getSelectedValue(cbMaNganh);
        if (raw.isEmpty()) {
            return "";
        }
        int sep = raw.indexOf(" - ");
        return sep > 0 ? raw.substring(0, sep).trim() : raw.trim();
    }

    private String getSelectedValue(JComboBox<String> combo) {
        Object sel = combo.getSelectedItem();
        if (sel == null) {
            return "";
        }
        String value = sel.toString().trim();
        return value.startsWith("--") ? "" : value;
    }

    private void selectCccdItem(String cccd) {
        if (cccd == null || cccd.isEmpty()) {
            cbCccd.setSelectedIndex(0);
            return;
        }
        for (int i = 0; i < cbCccd.getItemCount(); i++) {
            if (cccd.equals(cbCccd.getItemAt(i))) {
                cbCccd.setSelectedIndex(i);
                return;
            }
        }
        cbCccd.addItem(cccd);
        cbCccd.setSelectedItem(cccd);
    }

    private void selectComboItem(JComboBox<String> combo, String maNganh) {
        if (maNganh == null || maNganh.isEmpty()) {
            combo.setSelectedIndex(0);
            return;
        }
        String ten = tenNganhByMa.getOrDefault(maNganh, maNganh);
        String display = maNganh + " - " + ten;
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (display.equals(combo.getItemAt(i)) || maNganh.equals(combo.getItemAt(i))) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        combo.addItem(display);
        combo.setSelectedItem(display);
    }

    private void refreshThuTuSuggest() {
        String cccd = getSelectedCccd();
        if (cccd.isEmpty()) {
            txtNvTt.setText("");
            return;
        }
        if (txtIdnv.getText().isEmpty()) {
            txtNvTt.setText(String.valueOf(dao.suggestNextThuTu(cccd)));
        }
    }

    private void refreshHoanDoiCombo() {
        cbHoanDoi.removeAllItems();
        cbHoanDoi.addItem(null);
        String cccd = getSelectedCccd();
        if (cccd.isEmpty()) {
            return;
        }
        List<NguyenVong> list = dao.findByCccd(cccd);
        for (NguyenVong nv : list) {
            String ten = tenNganhByMa.getOrDefault(nv.getMaNganh(), nv.getMaNganh());
            cbHoanDoi.addItem(new NvSwapOption(nv.getIdnv(), nv.getThuTuNV() + " - " + ten));
        }
    }

    private void clearForm() {
        txtIdnv.setText("");
        cbCccd.setSelectedIndex(0);
        cbMaNganh.setSelectedIndex(0);
        txtNvTt.setText("");
        cbHoanDoi.removeAllItems();
        cbCccd.setEnabled(true);
        cbMaNganh.setEnabled(true);
        table.clearSelection();
    }

    private void setupEvents() {
        btnRefresh.addActionListener(e -> {
            loadData();
            clearForm();
        });

        cbCccd.addActionListener(e -> {
            refreshThuTuSuggest();
            refreshHoanDoiCombo();
        });

        cbMaNganh.addActionListener(e -> {
            String cccd = getSelectedCccd();
            String ma = parseMaNganhFromCombo();
            if (!cccd.isEmpty() && !ma.isEmpty() && txtIdnv.getText().isEmpty()
                    && dao.isCccdNganhExists(cccd, ma)) {
                JOptionPane.showMessageDialog(this,
                        "Thí sinh đã đăng ký ngành " + ma + " rồi!",
                        "Trùng ngành",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtIdnv.setText(table.getValueAt(row, 0).toString());
                selectCccdItem(table.getValueAt(row, 1).toString());
                selectComboItem(cbMaNganh, table.getValueAt(row, 2).toString());
                txtNvTt.setText(table.getValueAt(row, 3).toString());
                cbCccd.setEnabled(false);
                cbMaNganh.setEnabled(false);
                refreshHoanDoiCombo();
            }
        });

        btnAdd.addActionListener(e -> {
            NguyenVong nv = buildRawNguyenVongForAdd();
            if (nv == null) {
                return;
            }
            if (dao.isCccdNganhExists(nv.getTsCccd(), nv.getMaNganh())) {
                JOptionPane.showMessageDialog(this, "Thí sinh đã đăng ký ngành này rồi!");
                return;
            }
            if (dao.add(nv)) {
                JOptionPane.showMessageDialog(this,
                        "Đã thêm NV thứ tự " + nv.getThuTuNV()
                                + ". Bấm Tính lại điểm (tab Điểm XT) hoặc CHẠY XÉT TUYỂN.");
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Không thêm được (trùng khóa hoặc lỗi DB).");
            }
        });

        btnEdit.addActionListener(e -> hoanDoiThuTu());

        btnDelete.addActionListener(e -> {
            if (txtIdnv.getText().isEmpty()) {
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Xóa nguyện vọng này?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (dao.delete(Integer.parseInt(txtIdnv.getText()))) {
                    loadData();
                    clearForm();
                }
            }
        });

        btnImport.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Chọn file CSV nguyện vọng (3 cột)");
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                importCSV(fc.getSelectedFile());
            }
        });

        btnDanhSachTrung.addActionListener(e -> TrungTuyenTheoNganhDialog.showDialog(this));
        btnThongKeTrung.addActionListener(e -> ThongKeTrungTuyenDialog.showDialog(this));

        btnRunAlgo.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Tối ưu PT/tổ hợp, tính lại điểm và xét trúng/rớt cho tất cả nguyện vọng. Tiếp tục?",
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
                    JOptionPane.showMessageDialog(this, "Lỗi tính toán: " + ex.getMessage(), "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });
    }

    private void hoanDoiThuTu() {
        if (txtIdnv.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Chọn một dòng trên bảng (nguyện vọng nguồn), rồi chọn mục hoán đổi và bấm Cập nhật TT.");
            return;
        }
        NvSwapOption target = (NvSwapOption) cbHoanDoi.getSelectedItem();
        if (target == null) {
            JOptionPane.showMessageDialog(this, "Chọn nguyện vọng đích trong ô \"Hoán đổi với\".");
            return;
        }
        int idNguon = Integer.parseInt(txtIdnv.getText().trim());
        if (idNguon == target.idnv) {
            JOptionPane.showMessageDialog(this, "Chọn hai nguyện vọng khác nhau để hoán đổi.");
            return;
        }
        if (dao.swapThuTu(idNguon, target.idnv)) {
            JOptionPane.showMessageDialog(this, "Đã hoán đổi thứ tự nguyện vọng.");
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Không hoán đổi được (kiểm tra cùng CCCD / DB).");
        }
    }

    private NguyenVong buildRawNguyenVongForAdd() {
        String cccd = getSelectedCccd();
        String maNganh = parseMaNganhFromCombo();
        if (cccd.isEmpty() || maNganh.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn CCCD và Mã ngành!");
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

        int tt = dao.suggestNextThuTu(cccd);
        txtNvTt.setText(String.valueOf(tt));

        NguyenVong nv = new NguyenVong();
        nv.setTsCccd(cccd);
        nv.setMaNganh(maNganh);
        nv.setThuTuNV(tt);
        nv.setPhuongThuc(null);
        nv.setMaToHop(null);
        nv.setDiemThxt(null);
        nv.setDiemUtqd(null);
        nv.setDiemCong(null);
        nv.setDiemXetTuyen(null);
        nv.setKetQua("Chờ xét");
        nv.setNvKeys(cccd + "_" + maNganh + "_" + tt);
        return nv;
    }

    /**
     * Import NV thô (Hybrid): 3 cột CSV — CCCD, Mã ngành, Thứ tự NV.
     * PT/tổ hợp/điểm để NULL; sau đó dùng Tính lại điểm hoặc CHẠY XÉT TUYỂN.
     */
    private void importCSV(File file) {
        int success = 0, duplicate = 0, invalid = 0;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            boolean skippedHeader = false;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] data = line.split(",", -1);
                if (data.length < 3) {
                    invalid++;
                    continue;
                }
                if (!skippedHeader && isImportHeaderRow(data)) {
                    skippedHeader = true;
                    continue;
                }
                skippedHeader = true;

                String cccd = stripBom(data[0].trim());
                String maNganh = data[1].trim();
                String ttRaw = data[2].trim();

                if (cccd.isEmpty() || maNganh.isEmpty() || ttRaw.isEmpty()) {
                    invalid++;
                    continue;
                }

                int thuTu;
                try {
                    thuTu = Integer.parseInt(ttRaw);
                    if (thuTu < 1) {
                        invalid++;
                        continue;
                    }
                } catch (NumberFormatException e) {
                    invalid++;
                    continue;
                }

                if (!thiSinhDAO.isCccdExists(cccd)) {
                    invalid++;
                    continue;
                }
                if (!nganhDAO.isMaNganhExists(maNganh)) {
                    invalid++;
                    continue;
                }
                if (dao.isCccdNganhExists(cccd, maNganh)) {
                    duplicate++;
                    continue;
                }
                if (dao.isThuTuExists(cccd, thuTu)) {
                    duplicate++;
                    continue;
                }

                NguyenVong nv = new NguyenVong();
                nv.setTsCccd(cccd);
                nv.setMaNganh(maNganh);
                nv.setThuTuNV(thuTu);
                nv.setPhuongThuc(null);
                nv.setMaToHop(null);
                nv.setDiemThxt(null);
                nv.setDiemUtqd(null);
                nv.setDiemCong(null);
                nv.setDiemXetTuyen(null);
                nv.setKetQua("Chờ xét");
                nv.setNvKeys(cccd + "_" + maNganh + "_" + thuTu);

                if (dao.add(nv)) {
                    success++;
                } else {
                    duplicate++;
                }
            }
            JOptionPane.showMessageDialog(this,
                    "Import xong!\nThành công: " + success + "\nBỏ qua trùng: " + duplicate + "\nKhông hợp lệ: "
                            + invalid
                            + "\n\nFile CSV: 3 cột (CCCD, Mã ngành, Thứ tự NV). Excel → Lưu dạng CSV UTF-8.",
                    "Import nguyện vọng",
                    JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi đọc file!\nCần CSV UTF-8, 3 cột: CCCD, Mã ngành, Thứ tự NV (dòng 1 có thể là tiêu đề).",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static boolean isImportHeaderRow(String[] data) {
        String c0 = stripBom(data[0].trim().toLowerCase());
        return c0.contains("cccd") || c0.equals("stt") || c0.contains("mã ngành") || c0.contains("ma nganh");
    }

    private static String stripBom(String s) {
        if (s != null && s.startsWith("\uFEFF")) {
            return s.substring(1);
        }
        return s;
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
