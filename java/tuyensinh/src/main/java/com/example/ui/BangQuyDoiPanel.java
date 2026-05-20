package com.example.ui;

import com.example.dao.BangQuyDoiDAO;
import com.example.dao.ToHopDAO; // Import thêm DAO này
import com.example.entity.BangQuyDoi;
import com.example.entity.ToHopMon; // Import thêm Entity này

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class BangQuyDoiPanel extends JPanel implements RefreshablePanel {
    private JTable table;
    private JScrollPane tableScroll;
    private DefaultTableModel tableModel;
    private BangQuyDoiDAO dao;
    private ToHopDAO toHopDAO; // Khai báo thêm DAO

    private JTextField txtId, txtMaQuyDoi, txtPhuongThuc, txtMon;
    private JComboBox<String> cbToHop; // Chuyển từ JTextField sang JComboBox
    private JTextField txtDiemA, txtDiemB, txtDiemC, txtDiemD, txtPhanVi;
    private JTextField txtSearch; 
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnImport, btnSearch;

    public BangQuyDoiPanel() {
        dao = new BangQuyDoiDAO();
        toHopDAO = new ToHopDAO(); // Khởi tạo
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- 1. FORM NHẬP LIỆU ---
        JPanel formPanel = new JPanel(new GridLayout(5, 4, 10, 10));
        formPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        formPanel.add(new JLabel("Mã quy đổi (*):"));
        txtMaQuyDoi = new JTextField();
        formPanel.add(txtMaQuyDoi);

        formPanel.add(new JLabel("Phương thức (VD: DGNL):"));
        txtPhuongThuc = new JTextField();
        formPanel.add(txtPhuongThuc);

        formPanel.add(new JLabel("Tổ hợp:"));
        cbToHop = new JComboBox<>(); // Sử dụng ComboBox
        formPanel.add(cbToHop);

        formPanel.add(new JLabel("Môn:"));
        txtMon = new JTextField();
        formPanel.add(txtMon);

        formPanel.add(new JLabel("Điểm A (Mốc min):"));
        txtDiemA = new JTextField();
        formPanel.add(txtDiemA);

        formPanel.add(new JLabel("Điểm B (Mốc max):"));
        txtDiemB = new JTextField();
        formPanel.add(txtDiemB);

        formPanel.add(new JLabel("Điểm C (Quy đổi min):"));
        txtDiemC = new JTextField();
        formPanel.add(txtDiemC);

        formPanel.add(new JLabel("Điểm D (Quy đổi max):"));
        txtDiemD = new JTextField();
        formPanel.add(txtDiemD);

        formPanel.add(new JLabel("Phân vị:"));
        txtPhanVi = new JTextField();
        formPanel.add(txtPhanVi);

        txtId = new JTextField();
        txtId.setVisible(false);

        // --- 2. THANH TÌM KIẾM ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 14));
        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 18));
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập Mã/PT/Tổ hợp/Môn...");
        Border fieldLine = BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(fieldLine, new EmptyBorder(11, 15, 11, 15)));
        btnSearch = new JButton("Tìm kiếm");
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        UiButtons.stylePrimary(btnSearch);

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(BorderFactory.createTitledBorder("Cấu hình Bảng Quy Đổi"));
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

        buttonPanel.add(btnAdd); buttonPanel.add(btnUpdate); buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear); buttonPanel.add(btnImport);
        UiButtons.stylePrimary(btnAdd);
        UiButtons.stylePrimary(btnUpdate);
        UiButtons.styleSecondary(btnImport);
        UiButtons.styleDanger(btnDelete);
        UiButtons.styleSecondary(btnClear);
        UiButtons.equalizeButtonsInContainer(buttonPanel);

        // --- 4. BẢNG DỮ LIỆU ---
        String[] columns = {"ID", "Mã QĐ", "PT", "Tổ Hợp", "Môn", "Điểm A", "Điểm B", "Điểm C", "Điểm D", "Phân vị"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        UiTableTheme.apply(table);
        tableScroll = new JScrollPane(table);
        UiTableColumns.install(table, tableScroll);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.add(buttonPanel, BorderLayout.NORTH);
        centerPanel.add(tableScroll, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        loadToHopToCombo(); // Tải dữ liệu vào ComboBox
        loadData(""); 
        setupEvents();

        // TỰ ĐỘNG ĐỒNG BỘ COMBOBOX KHI CHUYỂN TAB
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                Object selected = cbToHop.getSelectedItem();
                loadToHopToCombo();
                if (selected != null) cbToHop.setSelectedItem(selected);
                UiTableColumns.refresh(table);
            }
        });
    }

    private void loadToHopToCombo() {
        cbToHop.removeAllItems();
        cbToHop.addItem("-- Không có / Trống --"); // Cho phép để trống (ví dụ: điểm IELTS)
        List<ToHopMon> list = toHopDAO.getAllToHop();
        if (list != null) {
            for (ToHopMon th : list) {
                cbToHop.addItem(th.getMatohop());
            }
        }
    }

    private void loadData(String keyword) {
        tableModel.setRowCount(0);
        List<BangQuyDoi> list;
        if (keyword == null || keyword.trim().isEmpty()) {
            list = dao.getAll();
        } else {
            list = dao.searchBangQuyDoi(keyword.trim());
        }
        
        if (list != null) {
            for (BangQuyDoi b : list) {
                tableModel.addRow(new Object[]{
                    b.getIdqd(), b.getdMaquydoi(), b.getdPhuongthuc(), b.getdTohop(), b.getdMon(),
                    b.getdDiema(), b.getdDiemb(), b.getdDiemc(), b.getdDiemd(), b.getdPhanvi()
                });
            }
        }
        UiTableColumns.refresh(table);
    }

    @Override
    public void refreshData() {
        Object selected = cbToHop.getSelectedItem();
        loadToHopToCombo();
        if (selected != null) {
            cbToHop.setSelectedItem(selected);
        }
        loadData(txtSearch.getText());
    }

    private void clearForm() {
        txtId.setText(""); txtMaQuyDoi.setText(""); txtPhuongThuc.setText("");
        cbToHop.setSelectedIndex(0); // Reset ComboBox
        txtMon.setText(""); txtPhanVi.setText("");
        txtDiemA.setText(""); txtDiemB.setText(""); txtDiemC.setText(""); txtDiemD.setText("");
        txtMaQuyDoi.setEditable(true);
        table.clearSelection();
    }

    private BangQuyDoi getDataFromForm() throws NumberFormatException {
        BangQuyDoi b = new BangQuyDoi();
        b.setdMaquydoi(txtMaQuyDoi.getText().trim());
        b.setdPhuongthuc(txtPhuongThuc.getText().trim());
        
        // Xử lý giá trị từ ComboBox
        String selectedToHop = (String) cbToHop.getSelectedItem();
        if (selectedToHop == null || selectedToHop.startsWith("--")) {
            b.setdTohop(""); // Lưu rỗng nếu chọn "Không có"
        } else {
            b.setdTohop(selectedToHop);
        }

        b.setdMon(txtMon.getText().trim());
        b.setdPhanvi(txtPhanVi.getText().trim());
        
        b.setdDiema(txtDiemA.getText().trim().isEmpty() ? null : Double.parseDouble(txtDiemA.getText().trim()));
        b.setdDiemb(txtDiemB.getText().trim().isEmpty() ? null : Double.parseDouble(txtDiemB.getText().trim()));
        b.setdDiemc(txtDiemC.getText().trim().isEmpty() ? null : Double.parseDouble(txtDiemC.getText().trim()));
        b.setdDiemd(txtDiemD.getText().trim().isEmpty() ? null : Double.parseDouble(txtDiemD.getText().trim()));
        return b;
    }

    private void setupEvents() {
        btnSearch.addActionListener(e -> loadData(txtSearch.getText()));

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtId.setText(table.getValueAt(row, 0).toString());
                txtMaQuyDoi.setText(table.getValueAt(row, 1) != null ? table.getValueAt(row, 1).toString() : "");
                txtPhuongThuc.setText(table.getValueAt(row, 2) != null ? table.getValueAt(row, 2).toString() : "");
                
                // Đổ dữ liệu lên ComboBox Tổ Hợp
                String th = table.getValueAt(row, 3) != null ? table.getValueAt(row, 3).toString() : "";
                if (th.isEmpty()) {
                    cbToHop.setSelectedIndex(0); // Chọn dòng "-- Không có --"
                } else {
                    cbToHop.setSelectedItem(th);
                }

                txtMon.setText(table.getValueAt(row, 4) != null ? table.getValueAt(row, 4).toString() : "");
                txtDiemA.setText(table.getValueAt(row, 5) != null ? table.getValueAt(row, 5).toString() : "");
                txtDiemB.setText(table.getValueAt(row, 6) != null ? table.getValueAt(row, 6).toString() : "");
                txtDiemC.setText(table.getValueAt(row, 7) != null ? table.getValueAt(row, 7).toString() : "");
                txtDiemD.setText(table.getValueAt(row, 8) != null ? table.getValueAt(row, 8).toString() : "");
                txtPhanVi.setText(table.getValueAt(row, 9) != null ? table.getValueAt(row, 9).toString() : "");
                txtMaQuyDoi.setEditable(false);
            }
        });

        btnClear.addActionListener(e -> clearForm());

        btnAdd.addActionListener(e -> {
            if (txtMaQuyDoi.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã quy đổi không được để trống!");
                return;
            }
            try {
                if (dao.isMaQuyDoiExists(txtMaQuyDoi.getText().trim())) {
                    JOptionPane.showMessageDialog(this, "Mã quy đổi này đã tồn tại!");
                    return;
                }
                if (dao.addBangQuyDoi(getDataFromForm())) {
                    JOptionPane.showMessageDialog(this, "Thêm thành công!");
                    loadData(txtSearch.getText()); clearForm();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Các trường Điểm phải là số thực (VD: 8.5)!");
            }
        });

        btnUpdate.addActionListener(e -> {
            if (txtId.getText().isEmpty()) return;
            try {
                BangQuyDoi b = getDataFromForm();
                b.setIdqd(Integer.parseInt(txtId.getText()));
                if (dao.updateBangQuyDoi(b)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadData(txtSearch.getText()); clearForm();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Các trường Điểm phải là số thực!");
            }
        });

        btnDelete.addActionListener(e -> {
            if (txtId.getText().isEmpty()) return;
            if (JOptionPane.showConfirmDialog(this, "Xóa cấu hình này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (dao.deleteBangQuyDoi(Integer.parseInt(txtId.getText()))) {
                    loadData(txtSearch.getText()); clearForm();
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
    }

    private void importCSV(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line; boolean first = true;
            int success = 0, duplicate = 0;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                String[] data = line.split(",", -1);
                if (data.length >= 8) {
                    BangQuyDoi b = new BangQuyDoi();
                    b.setdPhuongthuc(data[0].trim());
                    b.setdTohop(data[1].trim());
                    b.setdMon(data[2].trim());
                    b.setdDiema(data[3].trim().isEmpty() ? null : Double.parseDouble(data[3].trim()));
                    b.setdDiemb(data[4].trim().isEmpty() ? null : Double.parseDouble(data[4].trim()));
                    b.setdDiemc(data[5].trim().isEmpty() ? null : Double.parseDouble(data[5].trim()));
                    b.setdDiemd(data[6].trim().isEmpty() ? null : Double.parseDouble(data[6].trim()));
                    b.setdMaquydoi(data[7].trim());
                    if (data.length >= 9) b.setdPhanvi(data[8].trim());

                    if (!dao.isMaQuyDoiExists(b.getdMaquydoi())) {
                        if (dao.addBangQuyDoi(b)) success++;
                    } else duplicate++;
                }
            }
            JOptionPane.showMessageDialog(this, "Import xong!\nThành công: " + success + "\nBỏ qua (trùng Mã QĐ): " + duplicate);
            loadData(""); 
            txtSearch.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc file CSV! Đảm bảo các cột Điểm là số.");
        }
    }
}