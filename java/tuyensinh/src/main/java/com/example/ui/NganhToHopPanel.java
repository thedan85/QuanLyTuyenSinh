package com.example.ui;

import com.example.dao.NganhDAO;
import com.example.dao.NganhToHopDAO;
import com.example.dao.ToHopDAO;
import com.example.entity.Nganh;
import com.example.entity.NganhToHop;
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

public class NganhToHopPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private NganhToHopDAO dao;
    private NganhDAO nganhDAO;
    private ToHopDAO toHopDAO;

    private JTextField txtId;
    private JComboBox<String> cbMaNganh, cbMaToHop; // Đổi thành JComboBox
    private JTextField txtMon1, txtHs1, txtMon2, txtHs2, txtMon3, txtHs3;
    private JTextField txtDolech;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnImport;

    public NganhToHopPanel() {
        dao = new NganhToHopDAO();
        nganhDAO = new NganhDAO();
        toHopDAO = new ToHopDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- 1. FORM NHẬP LIỆU ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(" Ghép nối Ngành - Tổ hợp môn "), new EmptyBorder(5, 5, 5, 5)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbMaNganh = new JComboBox<>();
        cbMaToHop = new JComboBox<>();
        
        txtMon1 = new JTextField(); txtMon1.setEditable(false); // Khóa ô môn học
        txtHs1 = new JTextField("1");
        txtMon2 = new JTextField(); txtMon2.setEditable(false);
        txtHs2 = new JTextField("1");
        txtMon3 = new JTextField(); txtMon3.setEditable(false);
        txtHs3 = new JTextField("1");
        txtDolech = new JTextField("0.0");
        txtId = new JTextField();
        txtId.setVisible(false);

        // Dòng 1
        gbc.gridy = 0; gbc.gridx = 0;
        formPanel.add(new JLabel("Mã ngành (*):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(cbMaNganh, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Mã tổ hợp (*):"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        formPanel.add(cbMaToHop, gbc);

        // Dòng 2
        gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("Môn 1 (Mã - Hệ số):"), gbc);
        JPanel pMon1 = new JPanel(new GridLayout(1, 2, 5, 0));
        pMon1.add(txtMon1); pMon1.add(txtHs1);
        gbc.gridx = 1; formPanel.add(pMon1, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Môn 2 (Mã - Hệ số):"), gbc);
        JPanel pMon2 = new JPanel(new GridLayout(1, 2, 5, 0));
        pMon2.add(txtMon2); pMon2.add(txtHs2);
        gbc.gridx = 3; formPanel.add(pMon2, gbc);

        // Dòng 3
        gbc.gridy = 2; gbc.gridx = 0;
        formPanel.add(new JLabel("Môn 3 (Mã - Hệ số):"), gbc);
        JPanel pMon3 = new JPanel(new GridLayout(1, 2, 5, 0));
        pMon3.add(txtMon3); pMon3.add(txtHs3);
        gbc.gridx = 1; formPanel.add(pMon3, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Độ lệch điểm:"), gbc);
        gbc.gridx = 3; formPanel.add(txtDolech, gbc);

        add(formPanel, BorderLayout.NORTH);

        // --- 2. THANH CÔNG CỤ ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnAdd = new JButton("Thêm mới");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa");
        btnClear = new JButton("Làm mới Form");
        btnImport = new JButton("Import CSV");
        buttonPanel.add(btnAdd); buttonPanel.add(btnUpdate); buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear); buttonPanel.add(btnImport);

        // --- 3. BẢNG DỮ LIỆU ---
        String[] columns = { "ID", "Ngành", "Tổ Hợp", "M1", "H1", "M2", "H2", "M3", "H3",
                "N1", "TO", "LI", "HO", "SI", "VA", "SU", "DI", "TI", "KHAC", "KTPL", "Độ lệch" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < columns.length; i++) {
            int width = (i == 1 || i == 2) ? 80 : 40;
            if (i == columns.length - 1) width = 60;
            table.getColumnModel().getColumn(i).setPreferredWidth(width);
        }
        table.setRowHeight(25);

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
        centerPanel.add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        loadComboBoxData();
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
        cbMaNganh.addItem("-- Chọn Ngành --");
        List<Nganh> listNganh = nganhDAO.getAllNganh();
        if (listNganh != null) {
            for (Nganh n : listNganh) cbMaNganh.addItem(n.getManganh());
        }

        // Load Tổ hợp
        cbMaToHop.removeAllItems();
        cbMaToHop.addItem("-- Chọn Tổ hợp --");
        List<ToHopMon> listToHop = toHopDAO.getAllToHop();
        if (listToHop != null) {
            for (ToHopMon th : listToHop) cbMaToHop.addItem(th.getMatohop());
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<NganhToHop> list = dao.getAll();
        if (list != null) {
            for (NganhToHop n : list) {
                tableModel.addRow(new Object[] {
                        n.getId(), n.getManganh(), n.getMatohop(),
                        n.getThMon1(), n.getHsMon1(), n.getThMon2(), n.getHsMon2(), n.getThMon3(), n.getHsMon3(),
                        n.getN1(), n.getTo(), n.getLi(), n.getHo(), n.getSi(), n.getVa(),
                        n.getSu(), n.getDi(), n.getTi(), n.getKhac(), n.getKtpl(), n.getDolech()
                });
            }
        }
    }

    private void clearForm() {
        txtId.setText("");
        cbMaNganh.setSelectedIndex(0);
        cbMaToHop.setSelectedIndex(0);
        txtMon1.setText(""); txtHs1.setText("1");
        txtMon2.setText(""); txtHs2.setText("1");
        txtMon3.setText(""); txtHs3.setText("1");
        txtDolech.setText("0.0");
        
        cbMaNganh.setEnabled(true);
        cbMaToHop.setEnabled(true);
        table.clearSelection();
    }

    private NganhToHop getDataFromForm() throws Exception {
        String nganh = (String) cbMaNganh.getSelectedItem();
        String tohop = (String) cbMaToHop.getSelectedItem();
        
        if (nganh == null || nganh.startsWith("--") || tohop == null || tohop.startsWith("--")) {
            throw new Exception("Vui lòng chọn Mã ngành và Mã tổ hợp hợp lệ!");
        }

        NganhToHop n = new NganhToHop();
        n.setManganh(nganh);
        n.setMatohop(tohop);
        n.setThMon1(txtMon1.getText().trim());
        n.setHsMon1(Integer.parseInt(txtHs1.getText().trim().isEmpty() ? "1" : txtHs1.getText().trim()));
        n.setThMon2(txtMon2.getText().trim());
        n.setHsMon2(Integer.parseInt(txtHs2.getText().trim().isEmpty() ? "1" : txtHs2.getText().trim()));
        n.setThMon3(txtMon3.getText().trim());
        n.setHsMon3(Integer.parseInt(txtHs3.getText().trim().isEmpty() ? "1" : txtHs3.getText().trim()));

        // Các cờ môn học để mặc định 0 theo format gốc
        n.setN1(0); n.setTo(0); n.setLi(0); n.setHo(0); n.setSi(0); n.setVa(0);
        n.setSu(0); n.setDi(0); n.setTi(0); n.setKhac(0); n.setKtpl(0);
        n.setDolech(Double.parseDouble(txtDolech.getText().trim().isEmpty() ? "0.0" : txtDolech.getText().trim()));
        n.setTbKeys(n.getManganh() + "_" + n.getMatohop());
        return n;
    }

    private void setupEvents() {
        // SỰ KIỆN: KHI CHỌN TỔ HỢP, TỰ ĐỘNG ĐIỀN 3 MÔN HỌC
        cbMaToHop.addActionListener(e -> {
            String selectedToHop = (String) cbMaToHop.getSelectedItem();
            if (selectedToHop != null && !selectedToHop.startsWith("--")) {
                List<ToHopMon> list = toHopDAO.getAllToHop();
                for (ToHopMon th : list) {
                    if (th.getMatohop().equals(selectedToHop)) {
                        txtMon1.setText(th.getMon1());
                        txtMon2.setText(th.getMon2());
                        txtMon3.setText(th.getMon3());
                        break;
                    }
                }
            } else {
                txtMon1.setText(""); txtMon2.setText(""); txtMon3.setText("");
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtId.setText(getValue(row, 0));
                cbMaNganh.setSelectedItem(getValue(row, 1));
                cbMaToHop.setSelectedItem(getValue(row, 2));
                
                // Môn học sẽ tự nhảy do sự kiện cbMaToHop, ta chỉ cần gán lại hệ số
                txtHs1.setText(getValue(row, 4));
                txtHs2.setText(getValue(row, 6));
                txtHs3.setText(getValue(row, 8));
                txtDolech.setText(getValue(row, 20)); 
                
                // Khóa ComboBox khi đang ở chế độ Sửa
                cbMaNganh.setEnabled(false);
                cbMaToHop.setEnabled(false);
            }
        });

        btnClear.addActionListener(e -> clearForm());

        btnAdd.addActionListener(e -> {
            try {
                NganhToHop n = getDataFromForm();
                if (dao.isMappingExists(n.getTbKeys())) {
                    JOptionPane.showMessageDialog(this, "Ngành này đã có Tổ hợp này rồi!");
                    return;
                }
                if (dao.addMapping(n)) {
                    JOptionPane.showMessageDialog(this, "Thêm thành công!");
                    loadData(); clearForm();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Hệ số phải là số nguyên!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        btnUpdate.addActionListener(e -> {
            if (txtId.getText().isEmpty()) return;
            try {
                NganhToHop n = getDataFromForm();
                n.setId(Integer.parseInt(txtId.getText()));
                if (dao.updateMapping(n)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadData(); clearForm();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Hệ số phải là số nguyên!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        btnDelete.addActionListener(e -> {
            if (txtId.getText().isEmpty()) return;
            if (JOptionPane.showConfirmDialog(this, "Xóa liên kết này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (dao.deleteMapping(Integer.parseInt(txtId.getText()))) {
                    loadData(); clearForm();
                }
            }
        });

        btnImport.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) importCSV(fc.getSelectedFile());
        });
    }

    private String getValue(int row, int col) {
        Object val = table.getValueAt(row, col);
        return val == null ? "" : val.toString();
    }

    // Các Helper An Toàn
    private String getSafeString(String[] data, int index) {
        return (index < data.length && data[index] != null) ? data[index].trim() : null;
    }

    private Integer getSafeInt(String[] data, int index) {
        try {
            String val = getSafeString(data, index);
            return (val == null || val.isEmpty()) ? 0 : Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Double getSafeDouble(String[] data, int index) {
        try {
            String val = getSafeString(data, index);
            return (val == null || val.isEmpty()) ? 0.0 : Double.parseDouble(val);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void importCSV(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean first = true;
            int success = 0, duplicate = 0;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                String[] data = line.split(",", -1);

                String nganh = getSafeString(data, 0);
                String tohop = getSafeString(data, 1);
                if (nganh == null || nganh.isEmpty() || tohop == null || tohop.isEmpty()) continue;

                NganhToHop n = new NganhToHop();
                n.setManganh(nganh);
                n.setMatohop(tohop);
                n.setTbKeys(nganh + "_" + tohop);

                n.setThMon1(getSafeString(data, 2)); n.setHsMon1(getSafeInt(data, 3));
                n.setThMon2(getSafeString(data, 4)); n.setHsMon2(getSafeInt(data, 5));
                n.setThMon3(getSafeString(data, 6)); n.setHsMon3(getSafeInt(data, 7));

                n.setN1(getSafeInt(data, 8)); n.setTo(getSafeInt(data, 9));
                n.setLi(getSafeInt(data, 10)); n.setHo(getSafeInt(data, 11));
                n.setSi(getSafeInt(data, 12)); n.setVa(getSafeInt(data, 13));
                n.setSu(getSafeInt(data, 14)); n.setDi(getSafeInt(data, 15));
                n.setTi(getSafeInt(data, 16)); n.setKhac(getSafeInt(data, 17));
                n.setKtpl(getSafeInt(data, 18));
                n.setDolech(getSafeDouble(data, 19));

                if (!dao.isMappingExists(n.getTbKeys())) {
                    if (dao.addMapping(n)) success++;
                } else duplicate++;
            }
            JOptionPane.showMessageDialog(this, "Import xong!\nThành công: " + success + "\nBỏ qua (Trùng): " + duplicate);
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi đọc file CSV: " + ex.getMessage());
        }
    }
}