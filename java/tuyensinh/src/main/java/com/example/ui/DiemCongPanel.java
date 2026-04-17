package com.example.ui;

import com.example.dao.DiemCongDAO;
import com.example.dao.NganhDAO;
import com.example.dao.ToHopDAO;
import com.example.entity.DiemCong;
import com.example.entity.Nganh;
import com.example.entity.ToHopMon;

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

public class DiemCongPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private DiemCongDAO dao;
    private NganhDAO nganhDAO;
    private ToHopDAO toHopDAO;

    private JTextField txtId, txtCccd, txtPhuongThuc;
    private JComboBox<String> cbMaNganh, cbMaToHop; // Chuyển sang JComboBox
    private JTextField txtDiemCC, txtDiemUtxt, txtDiemTong, txtGhiChu;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnImport;

    public DiemCongPanel() {
        dao = new DiemCongDAO();
        nganhDAO = new NganhDAO();
        toHopDAO = new ToHopDAO();
        
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- 1. FORM NHẬP LIỆU ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createEtchedBorder(), " Cấu hình Điểm Cộng / Ưu Tiên "),
                new EmptyBorder(10, 10, 10, 10)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dòng 1
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("CCCD:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtCccd = new JTextField();
        formPanel.add(txtCccd, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Mã ngành:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        cbMaNganh = new JComboBox<>();
        formPanel.add(cbMaNganh, gbc);

        // Dòng 2
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Mã tổ hợp:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cbMaToHop = new JComboBox<>();
        formPanel.add(cbMaToHop, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Phương thức:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        txtPhuongThuc = new JTextField();
        formPanel.add(txtPhuongThuc, gbc);

        // Dòng 3
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Điểm C/C (Ngoại ngữ):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtDiemCC = new JTextField();
        formPanel.add(txtDiemCC, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Điểm ƯTXT (Giải/Đ.tượng):"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        txtDiemUtxt = new JTextField();
        formPanel.add(txtDiemUtxt, gbc);

        // Dòng 4
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(new JLabel("Tổng Điểm Cộng:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtDiemTong = new JTextField();
        txtDiemTong.setToolTipText("Hệ thống sẽ tự tính nếu để trống");
        txtDiemTong.setEditable(false);
        txtDiemTong.setBackground(new Color(235, 235, 235));
        formPanel.add(txtDiemTong, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        txtGhiChu = new JTextField();
        formPanel.add(txtGhiChu, gbc);

        txtId = new JTextField();
        txtId.setVisible(false);
        add(formPanel, BorderLayout.NORTH);

        // --- 2. THANH CÔNG CỤ ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnAdd = new JButton("Thêm mới");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa");
        btnClear = new JButton("Làm mới");
        btnImport = new JButton("Import CSV");

        buttonPanel.add(btnAdd); buttonPanel.add(btnUpdate); buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear); buttonPanel.add(btnImport);

        // --- 3. BẢNG DỮ LIỆU ---
        String[] columns = { "ID", "CCCD", "Ngành", "Tổ Hợp", "Phương Thức", "Điểm CC", "Điểm ƯTXT", "Tổng Cộng", "Ghi Chú" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                return c;
            }
        });

        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.add(buttonPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        loadComboBoxData(); // Load dữ liệu cho ComboBox
        loadData();
        setupEvents();

        // TỰ ĐỘNG ĐỒNG BỘ COMBOBOX KHI CHUYỂN TAB
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                Object selNganh = cbMaNganh.getSelectedItem();
                Object selToHop = cbMaToHop.getSelectedItem();
                loadComboBoxData();
                if (selNganh != null) cbMaNganh.setSelectedItem(selNganh);
                if (selToHop != null) cbMaToHop.setSelectedItem(selToHop);
            }
        });
    }

    private void loadComboBoxData() {
        // Load Ngành
        cbMaNganh.removeAllItems();
        cbMaNganh.addItem("-- Không chọn / Tất cả --");
        List<Nganh> listNganh = nganhDAO.getAllNganh();
        if (listNganh != null) {
            for (Nganh n : listNganh) cbMaNganh.addItem(n.getManganh());
        }

        // Load Tổ hợp
        cbMaToHop.removeAllItems();
        cbMaToHop.addItem("-- Không chọn / Tất cả --");
        List<ToHopMon> listToHop = toHopDAO.getAllToHop();
        if (listToHop != null) {
            for (ToHopMon th : listToHop) cbMaToHop.addItem(th.getMatohop());
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<DiemCong> list = dao.getAll();
        if (list != null) {
            for (DiemCong d : list) {
                tableModel.addRow(new Object[] {
                        d.getIddiemcong(), d.getTsCccd(), d.getManganh(), d.getMatohop(),
                        d.getPhuongthuc(), d.getDiemCC(), d.getDiemUtxt(), d.getDiemTong(), d.getGhichu()
                });
            }
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtCccd.setText("");
        cbMaNganh.setSelectedIndex(0);
        cbMaToHop.setSelectedIndex(0);
        txtPhuongThuc.setText("");
        txtDiemCC.setText("");
        txtDiemUtxt.setText("");
        txtDiemTong.setText("");
        txtGhiChu.setText("");
        
        txtCccd.setEditable(true);
        cbMaNganh.setEnabled(true);
        cbMaToHop.setEnabled(true);
        txtPhuongThuc.setEditable(true);
        table.clearSelection();
    }

    private DiemCong getDataFromForm() {
        DiemCong d = new DiemCong();
        d.setTsCccd(txtCccd.getText().trim());
        
        // Đọc giá trị từ ComboBox
        String selectedNganh = (String) cbMaNganh.getSelectedItem();
        d.setManganh((selectedNganh == null || selectedNganh.startsWith("--")) ? "" : selectedNganh);
        
        String selectedToHop = (String) cbMaToHop.getSelectedItem();
        d.setMatohop((selectedToHop == null || selectedToHop.startsWith("--")) ? "" : selectedToHop);
        
        d.setPhuongthuc(txtPhuongThuc.getText().trim());

        String sCC = txtDiemCC.getText().trim();
        String sUT = txtDiemUtxt.getText().trim();
        Double dCC = sCC.isEmpty() ? null : Double.parseDouble(sCC);
        Double dUT = sUT.isEmpty() ? null : Double.parseDouble(sUT);

        d.setDiemCC(dCC);
        d.setDiemUtxt(dUT);

        double total = (dCC != null ? dCC : 0) + (dUT != null ? dUT : 0);
        d.setDiemTong(total);

        d.setGhichu(txtGhiChu.getText().trim());
        d.setDcKeys(d.getTsCccd() + "_" + d.getManganh() + "_" + d.getMatohop() + "_" + d.getPhuongthuc());

        return d;
    }

    private void setupEvents() {
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtId.setText(table.getValueAt(row, 0).toString());
                txtCccd.setText(table.getValueAt(row, 1) != null ? table.getValueAt(row, 1).toString() : "");
                
                String nganh = table.getValueAt(row, 2) != null ? table.getValueAt(row, 2).toString() : "";
                if (nganh.isEmpty()) cbMaNganh.setSelectedIndex(0); else cbMaNganh.setSelectedItem(nganh);
                
                String tohop = table.getValueAt(row, 3) != null ? table.getValueAt(row, 3).toString() : "";
                if (tohop.isEmpty()) cbMaToHop.setSelectedIndex(0); else cbMaToHop.setSelectedItem(tohop);
                
                txtPhuongThuc.setText(table.getValueAt(row, 4) != null ? table.getValueAt(row, 4).toString() : "");
                txtDiemCC.setText(table.getValueAt(row, 5) != null ? table.getValueAt(row, 5).toString() : "");
                txtDiemUtxt.setText(table.getValueAt(row, 6) != null ? table.getValueAt(row, 6).toString() : "");
                txtDiemTong.setText(table.getValueAt(row, 7) != null ? table.getValueAt(row, 7).toString() : "");
                txtGhiChu.setText(table.getValueAt(row, 8) != null ? table.getValueAt(row, 8).toString() : "");

                // Khóa các trường tạo nên Key khi Edit
                txtCccd.setEditable(false);
                cbMaNganh.setEnabled(false);
                cbMaToHop.setEnabled(false);
                txtPhuongThuc.setEditable(false);
            }
        });

        btnClear.addActionListener(e -> clearForm());

        btnAdd.addActionListener(e -> {
            if (txtCccd.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "CCCD không được để trống!");
                return;
            }
            try {
                DiemCong d = getDataFromForm();
                if (dao.isKeyExists(d.getDcKeys())) {
                    JOptionPane.showMessageDialog(this, "Điểm cộng cho khóa này đã tồn tại!");
                    return;
                }
                if (dao.addDiemCong(d)) {
                    loadData(); clearForm();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Điểm phải là số!");
            }
        });

        btnUpdate.addActionListener(e -> {
            if (txtId.getText().isEmpty()) return;
            try {
                DiemCong d = getDataFromForm();
                d.setIddiemcong(Integer.parseInt(txtId.getText()));
                if (dao.updateDiemCong(d)) {
                    loadData(); clearForm();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Điểm phải là số!");
            }
        });

        btnDelete.addActionListener(e -> {
            if (!txtId.getText().isEmpty() && JOptionPane.showConfirmDialog(this, "Xóa cấu hình này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == 0) {
                if (dao.deleteDiemCong(Integer.parseInt(txtId.getText()))) {
                    loadData(); clearForm();
                }
            }
        });

        btnImport.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) importCSV(fc.getSelectedFile());
        });

        // Lắng nghe thay đổi tính tổng tự động
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
            String line; boolean first = true;
            int success = 0, duplicate = 0;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                String[] data = line.split(",", -1);
                if (data.length >= 4) {
                    DiemCong d = new DiemCong();
                    d.setTsCccd(data[0].trim());
                    d.setManganh(data[1].trim());
                    d.setMatohop(data[2].trim());
                    d.setPhuongthuc(data[3].trim());

                    Double dcc = data.length > 4 && !data[4].trim().isEmpty() ? Double.parseDouble(data[4].trim()) : 0.0;
                    Double dut = data.length > 5 && !data[5].trim().isEmpty() ? Double.parseDouble(data[5].trim()) : 0.0;

                    d.setDiemCC(dcc == 0.0 ? null : dcc);
                    d.setDiemUtxt(dut == 0.0 ? null : dut);
                    if (data.length > 6 && !data[6].trim().isEmpty()) {
                        d.setDiemTong(Double.parseDouble(data[6].trim()));
                    } else {
                        d.setDiemTong(dcc + dut);
                    }
                    if (data.length > 7) d.setGhichu(data[7].trim());
                    
                    d.setDcKeys(d.getTsCccd() + "_" + d.getManganh() + "_" + d.getMatohop() + "_" + d.getPhuongthuc());

                    if (!dao.isKeyExists(d.getDcKeys())) {
                        if (dao.addDiemCong(d)) success++;
                    } else duplicate++;
                }
            }
            JOptionPane.showMessageDialog(this, "Import xong!\nThành công: " + success + "\nBỏ qua (trùng lặp): " + duplicate);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc file CSV! Đảm bảo các cột Điểm là số.");
        }
    }

    private void tuDongTinhTong() {
        try {
            double dcc = txtDiemCC.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtDiemCC.getText().trim());
            double dut = txtDiemUtxt.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtDiemUtxt.getText().trim());
            txtDiemTong.setText(String.valueOf(dcc + dut));
        } catch (NumberFormatException e) {}
    }
}