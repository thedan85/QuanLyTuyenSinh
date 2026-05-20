package com.example.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.example.dao.DiemCongDAO;
import com.example.dao.NganhDAO;
import com.example.dao.ThiSinhDAO;
import com.example.dao.ToHopDAO;
import com.example.entity.DiemCong;
import com.example.entity.Nganh;
import com.example.entity.ToHopMon;

public class DiemCongPanel extends JPanel implements RefreshablePanel {
    private JTable table;
    private JScrollPane tableScroll;
    private DefaultTableModel tableModel;
    private DiemCongDAO dao;
    private NganhDAO nganhDAO;
    private ToHopDAO toHopDAO;
    private ThiSinhDAO thiSinhDAO;

    private JTextField txtId;
    private JComboBox<String> cbCccd, cbPhuongThuc;
    private JComboBox<String> cbMaNganh, cbMaToHop;
    private JTextField txtDiemCC, txtDiemUtxt, txtDiemTong, txtGhiChu;
    private JTextField txtSearch;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnImport, btnSearch;

    public DiemCongPanel() {
        dao = new DiemCongDAO();
        nganhDAO = new NganhDAO();
        toHopDAO = new ToHopDAO();
        thiSinhDAO = new ThiSinhDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- 1. FORM NHẬP LIỆU ---
        JPanel formPanel = new JPanel(new GridLayout(4, 4, 10, 10));
        formPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        formPanel.add(new JLabel("Thí sinh (CCCD) (*):"));
        cbCccd = new JComboBox<>();
        formPanel.add(cbCccd);

        formPanel.add(new JLabel("Nguyện vọng (Mã ngành):"));
        cbMaNganh = new JComboBox<>();
        formPanel.add(cbMaNganh);

        formPanel.add(new JLabel("Tổ hợp môn:"));
        cbMaToHop = new JComboBox<>();
        formPanel.add(cbMaToHop);

        formPanel.add(new JLabel("Phương thức (*):"));
        cbPhuongThuc = PhuongThucOptions.newCombo();
        formPanel.add(cbPhuongThuc);

        formPanel.add(new JLabel("Điểm cộng Tiếng Anh:"));
        txtDiemCC = new JTextField();
        formPanel.add(txtDiemCC);

        formPanel.add(new JLabel("Điểm cộng Học Sinh Giỏi:"));
        txtDiemUtxt = new JTextField();
        formPanel.add(txtDiemUtxt);

        formPanel.add(new JLabel("Tổng điểm cộng:"));
        txtDiemTong = new JTextField();
        txtDiemTong.setEditable(false);
        txtDiemTong.setBackground(new Color(235, 235, 235));
        formPanel.add(txtDiemTong);

        formPanel.add(new JLabel("Ghi chú:"));
        txtGhiChu = new JTextField();
        formPanel.add(txtGhiChu);

        txtId = new JTextField();
        txtId.setVisible(false);

        // --- 2. THANH TÌM KIẾM ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 14));
        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 18));
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập CCCD để lọc...");
        Border fieldLine = BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(fieldLine, new EmptyBorder(11, 15, 11, 15)));
        btnSearch = new JButton("Tìm kiếm");
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        UiButtons.stylePrimary(btnSearch);

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(BorderFactory.createTitledBorder("Cấu hình Điểm Cộng"));
        formScroll.setViewportBorder(null);
        formScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        formScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(formScroll, BorderLayout.CENTER);
        northPanel.add(searchPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // --- 3. THANH CÔNG CỤ ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnAdd = new JButton("Thêm mới");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa");
        btnClear = new JButton("Làm mới Form");
        btnImport = new JButton("Import CSV");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnImport);
        UiButtons.stylePrimary(btnAdd);
        UiButtons.stylePrimary(btnUpdate);
        UiButtons.styleSecondary(btnImport);
        UiButtons.styleDanger(btnDelete);
        UiButtons.styleSecondary(btnClear);
        UiButtons.equalizeButtonsInContainer(buttonPanel);

        // --- 4. BẢNG DỮ LIỆU ---
        String[] columns = {
                "ID", "Thí sinh (CCCD)", "Nguyện vọng", "Tổ hợp môn",
                "Điểm cộng Tiếng Anh", "Điểm cộng HSG", "Tổng điểm cộng", "Phương thức", "Ghi chú"
        };
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

        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.add(buttonPanel, BorderLayout.NORTH);
        centerPanel.add(tableScroll, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        loadComboBoxData();
        loadData("");
        setupEvents();

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

    private void loadComboBoxData() {
        loadCccdCombo();

        cbMaNganh.removeAllItems();
        cbMaNganh.addItem("-- Không chọn / Tất cả --");
        List<Nganh> listNganh = nganhDAO.getAllNganh();
        if (listNganh != null) {
            for (Nganh n : listNganh) {
                cbMaNganh.addItem(n.getManganh());
            }
        }

        cbMaToHop.removeAllItems();
        cbMaToHop.addItem("-- Không chọn / Tất cả --");
        List<ToHopMon> listToHop = toHopDAO.getAllToHop();
        if (listToHop != null) {
            for (ToHopMon th : listToHop) {
                cbMaToHop.addItem(th.getMatohop());
            }
        }
    }

    private void loadCccdCombo() {
        cbCccd.removeAllItems();
        cbCccd.addItem("-- Chọn CCCD --");
        List<com.example.entity.ThiSinh> list = thiSinhDAO.getAllThiSinh();
        if (list != null) {
            for (com.example.entity.ThiSinh ts : list) {
                String cccd = ts.getCccd();
                if (cccd != null && !cccd.trim().isEmpty()) {
                    cbCccd.addItem(cccd);
                }
            }
        }
    }

    private String getSelectedCccd() {
        Object sel = cbCccd.getSelectedItem();
        if (sel == null) {
            return "";
        }
        String cccd = sel.toString().trim();
        return cccd.startsWith("--") ? "" : cccd;
    }

    private void loadData(String keyword) {
        tableModel.setRowCount(0);
        List<DiemCong> list;
        if (keyword == null || keyword.trim().isEmpty()) {
            list = dao.getAll();
        } else {
            list = dao.searchByCccd(keyword.trim());
        }

        if (list != null) {
            for (DiemCong d : list) {
                tableModel.addRow(new Object[]{
                        d.getIddiemcong(), d.getTsCccd(), d.getManganh(), d.getMatohop(),
                        d.getDiemCC(), d.getDiemUtxt(), d.getDiemTong(),
                        d.getPhuongthuc(), d.getGhichu()
                });
            }
        }
        UiTableColumns.refresh(table);
    }

    @Override
    public void refreshData() {
        Object selCccd = cbCccd.getSelectedItem();
        Object selNganh = cbMaNganh.getSelectedItem();
        Object selToHop = cbMaToHop.getSelectedItem();
        loadComboBoxData();
        if (selCccd != null) cbCccd.setSelectedItem(selCccd);
        if (selNganh != null) cbMaNganh.setSelectedItem(selNganh);
        if (selToHop != null) cbMaToHop.setSelectedItem(selToHop);
        loadData(txtSearch.getText());
    }

    private void clearForm() {
        txtId.setText("");
        cbCccd.setSelectedIndex(0);
        cbMaNganh.setSelectedIndex(0);
        cbMaToHop.setSelectedIndex(0);
        PhuongThucOptions.select(cbPhuongThuc, PhuongThucOptions.PT1);
        txtDiemCC.setText("");
        txtDiemUtxt.setText("");
        txtDiemTong.setText("");
        txtGhiChu.setText("");
        cbCccd.setEnabled(true);
        cbMaNganh.setEnabled(true);
        cbMaToHop.setEnabled(true);
        cbPhuongThuc.setEnabled(true);
        table.clearSelection();
    }

    private DiemCong getDataFromForm() throws NumberFormatException {
        DiemCong d = new DiemCong();
        d.setTsCccd(getSelectedCccd());

        String selectedNganh = (String) cbMaNganh.getSelectedItem();
        d.setManganh((selectedNganh == null || selectedNganh.startsWith("--")) ? "" : selectedNganh);

        String selectedToHop = (String) cbMaToHop.getSelectedItem();
        d.setMatohop((selectedToHop == null || selectedToHop.startsWith("--")) ? "" : selectedToHop);

        d.setPhuongthuc(PhuongThucOptions.getCode(cbPhuongThuc));

        String sCC = txtDiemCC.getText().trim();
        String sUT = txtDiemUtxt.getText().trim();
        Double dCC = sCC.isEmpty() ? null : Double.parseDouble(sCC);
        Double dUT = sUT.isEmpty() ? null : Double.parseDouble(sUT);

        d.setDiemCC(dCC);
        d.setDiemUtxt(dUT);
        d.setDiemTong((dCC != null ? dCC : 0) + (dUT != null ? dUT : 0));
        d.setGhichu(txtGhiChu.getText().trim());
        d.setDcKeys(d.getTsCccd() + "_" + d.getManganh() + "_" + d.getMatohop() + "_" + d.getPhuongthuc());
        return d;
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

    private void setupEvents() {
        btnSearch.addActionListener(e -> loadData(txtSearch.getText()));

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtId.setText(table.getValueAt(row, 0).toString());
                selectComboItem(cbCccd, table.getValueAt(row, 1) != null ? table.getValueAt(row, 1).toString() : "");
                selectComboItem(cbMaNganh, table.getValueAt(row, 2) != null ? table.getValueAt(row, 2).toString() : "");
                selectComboItem(cbMaToHop, table.getValueAt(row, 3) != null ? table.getValueAt(row, 3).toString() : "");
                txtDiemCC.setText(table.getValueAt(row, 4) != null ? table.getValueAt(row, 4).toString() : "");
                txtDiemUtxt.setText(table.getValueAt(row, 5) != null ? table.getValueAt(row, 5).toString() : "");
                txtDiemTong.setText(table.getValueAt(row, 6) != null ? table.getValueAt(row, 6).toString() : "");
                PhuongThucOptions.select(cbPhuongThuc,
                        table.getValueAt(row, 7) != null ? table.getValueAt(row, 7).toString() : "");
                txtGhiChu.setText(table.getValueAt(row, 8) != null ? table.getValueAt(row, 8).toString() : "");

                cbCccd.setEnabled(false);
                cbMaNganh.setEnabled(false);
                cbMaToHop.setEnabled(false);
                cbPhuongThuc.setEnabled(false);
            }
        });

        btnClear.addActionListener(e -> clearForm());

        btnAdd.addActionListener(e -> {
            String cccd = getSelectedCccd();
            if (cccd.isEmpty()) {
                JOptionPane.showMessageDialog(this, "CCCD không được để trống!");
                return;
            }
            try {
                if (!thiSinhDAO.isCccdExists(cccd)) {
                    JOptionPane.showMessageDialog(this, "CCCD chưa tồn tại trong bảng thí sinh!");
                    return;
                }
                DiemCong d = getDataFromForm();
                if (dao.isKeyExists(d.getDcKeys())) {
                    JOptionPane.showMessageDialog(this, "Điểm cộng cho khóa này đã tồn tại!");
                    return;
                }
                if (dao.addDiemCong(d)) {
                    JOptionPane.showMessageDialog(this, "Thêm thành công!");
                    loadData(txtSearch.getText());
                    clearForm();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Điểm phải là số!");
            }
        });

        btnUpdate.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                return;
            }
            try {
                DiemCong d = getDataFromForm();
                d.setIddiemcong(Integer.parseInt(txtId.getText()));
                if (dao.updateDiemCong(d)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadData(txtSearch.getText());
                    clearForm();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Điểm phải là số!");
            }
        });

        btnDelete.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Xóa cấu hình này?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (dao.deleteDiemCong(Integer.parseInt(txtId.getText()))) {
                    loadData(txtSearch.getText());
                    clearForm();
                }
            }
        });

        btnImport.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                importCSV(fc.getSelectedFile());
            }
        });

        javax.swing.event.DocumentListener docListener = new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { tuDongTinhTong(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { tuDongTinhTong(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { tuDongTinhTong(); }
        };
        txtDiemCC.getDocument().addDocumentListener(docListener);
        txtDiemUtxt.getDocument().addDocumentListener(docListener);
    }

    private void importCSV(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean first = true;
            int success = 0, duplicate = 0, invalid = 0;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                String[] data = line.split(",", -1);
                if (data.length >= 4) {
                    DiemCong d = new DiemCong();
                    d.setTsCccd(data[0].trim());
                    d.setManganh(data[1].trim());
                    d.setMatohop(data[2].trim());
                    d.setPhuongthuc(PhuongThucOptions.toCode(data[3]));

                    if (d.getTsCccd().isEmpty() || !thiSinhDAO.isCccdExists(d.getTsCccd())) {
                        invalid++;
                        continue;
                    }
                    if (!d.getManganh().isEmpty() && !nganhDAO.isMaNganhExists(d.getManganh())) {
                        invalid++;
                        continue;
                    }
                    if (!d.getMatohop().isEmpty() && !toHopDAO.isMaToHopExists(d.getMatohop())) {
                        invalid++;
                        continue;
                    }

                    Double dcc = data.length > 4 && !data[4].trim().isEmpty() ? Double.parseDouble(data[4].trim()) : 0.0;
                    Double dut = data.length > 5 && !data[5].trim().isEmpty() ? Double.parseDouble(data[5].trim()) : 0.0;

                    d.setDiemCC(dcc == 0.0 ? null : dcc);
                    d.setDiemUtxt(dut == 0.0 ? null : dut);
                    if (data.length > 6 && !data[6].trim().isEmpty()) {
                        d.setDiemTong(Double.parseDouble(data[6].trim()));
                    } else {
                        d.setDiemTong(dcc + dut);
                    }
                    if (data.length > 7) {
                        d.setGhichu(data[7].trim());
                    }
                    d.setDcKeys(d.getTsCccd() + "_" + d.getManganh() + "_" + d.getMatohop() + "_" + d.getPhuongthuc());

                    if (!dao.isKeyExists(d.getDcKeys())) {
                        if (dao.addDiemCong(d)) {
                            success++;
                        }
                    } else {
                        duplicate++;
                    }
                }
            }
            JOptionPane.showMessageDialog(this,
                    "Import xong!\nThành công: " + success + "\nBỏ qua (trùng): " + duplicate + "\nKhông hợp lệ: " + invalid);
            loadData("");
            txtSearch.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc file CSV! Đảm bảo các cột Điểm là số.");
        }
    }

    private void tuDongTinhTong() {
        try {
            double dcc = txtDiemCC.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtDiemCC.getText().trim());
            double dut = txtDiemUtxt.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtDiemUtxt.getText().trim());
            txtDiemTong.setText(String.valueOf(dcc + dut));
        } catch (NumberFormatException ignored) {
        }
    }
}
