package com.example.ui;

import com.example.dao.NganhDAO;
import com.example.entity.Nganh;
import com.example.dao.ToHopDAO;
import com.example.entity.ToHopMon;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class NganhPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private NganhDAO nganhDAO;

    private JTextField txtId, txtMaNganh, txtTenNganh, txtChiTieu, txtDiemSan;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnImport;
    private JComboBox<String> cbTohopGoc;

    public NganhPanel() {
        nganhDAO = new NganhDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- FORM NHẬP LIỆU ---
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Ngành xét tuyển"));

        formPanel.add(new JLabel("Mã ngành:"));
        txtMaNganh = new JTextField();
        formPanel.add(txtMaNganh);

        formPanel.add(new JLabel("Tên ngành:"));
        txtTenNganh = new JTextField();
        formPanel.add(txtTenNganh);

        formPanel.add(new JLabel("Chỉ tiêu:"));
        txtChiTieu = new JTextField();
        formPanel.add(txtChiTieu);

        formPanel.add(new JLabel("Điểm sàn:"));
        txtDiemSan = new JTextField();
        formPanel.add(txtDiemSan);

        formPanel.add(new JLabel("Tổ hợp gốc:"));
        cbTohopGoc = new JComboBox<>();
        loadToHopToCombo();
        formPanel.add(cbTohopGoc);

        txtId = new JTextField();
        txtId.setVisible(false);

        add(formPanel, BorderLayout.NORTH);

        // --- THANH CÔNG CỤ ---
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

        // --- BẢNG DỮ LIỆU ---
        String[] columns = {
                "ID", "Mã Ngành", "Tên Ngành", "Chỉ Tiêu", "Điểm Sàn", "Tổ Hợp Gốc",
                "Điểm Trúng Tuyển", "Tuyển Thẳng", "ĐGNL", "THPT", "VSAT",
                "SL_XTT", "SL_DGNL", "SL_VSAT", "SL_THPT"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.add(buttonPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        loadData();
        setupEvents();

        // Lắng nghe sự kiện mỗi khi Panel này được hiển thị (người dùng chuyển sang tab này)
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                // Tạm thời lưu lại tổ hợp đang chọn (để không bị mất khi load lại)
                Object selected = cbTohopGoc.getSelectedItem();

                // Nạp lại danh sách từ DB
                loadToHopToCombo();

                // Trả lại giá trị đang chọn cũ (nếu có)
                if (selected != null) {
                    cbTohopGoc.setSelectedItem(selected);
                }
            }
        });
    }

    private void loadToHopToCombo() {
        cbTohopGoc.removeAllItems();
        cbTohopGoc.addItem("-- Chọn tổ hợp --");
        List<ToHopMon> list = new ToHopDAO().getAllToHop();
        if (list != null) {
            for (ToHopMon th : list) {
                cbTohopGoc.addItem(th.getMatohop());
            }
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Nganh> list = nganhDAO.getAllNganh();
        if (list != null) {
            for (Nganh n : list) {
                tableModel.addRow(new Object[] {
                        n.getIdnganh(),
                        n.getManganh(),
                        n.getTennganh(),
                        n.getnChitieu(),
                        n.getnDiemsan(),
                        n.getnTohopgoc(),
                        n.getnDiemtrungtuyen(),
                        n.getnTuyenthang(),
                        n.getnDgnl(),
                        n.getnThpt(),
                        n.getnVsat(),
                        n.getSlXtt(),
                        n.getSlDgnl(),
                        n.getSlVsat(),
                        n.getSlThpt()
                });
            }
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtMaNganh.setText("");
        txtMaNganh.setEditable(true);
        txtTenNganh.setText("");
        txtChiTieu.setText("");
        txtDiemSan.setText("");
        cbTohopGoc.setSelectedIndex(0);
        table.clearSelection();
    }

    private void setupEvents() {
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtId.setText(table.getValueAt(row, 0).toString());
                txtMaNganh.setText(table.getValueAt(row, 1).toString());
                txtMaNganh.setEditable(false);
                txtTenNganh.setText(table.getValueAt(row, 2).toString());
                txtChiTieu.setText(table.getValueAt(row, 3).toString());
                txtDiemSan.setText(table.getValueAt(row, 4) != null ? table.getValueAt(row, 4).toString() : "");
                cbTohopGoc.setSelectedItem(table.getValueAt(row, 5) != null ? table.getValueAt(row, 5).toString() : "");
            }
        });

        btnClear.addActionListener(e -> clearForm());

        btnAdd.addActionListener(e -> {
            if (txtMaNganh.getText().isEmpty() || txtTenNganh.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã ngành và Tên ngành!");
                return;
            }
            if (nganhDAO.isMaNganhExists(txtMaNganh.getText().trim())) {
                JOptionPane.showMessageDialog(this, "Mã ngành đã tồn tại!");
                return;
            }
            try {
                Nganh n = new Nganh();
                n.setManganh(txtMaNganh.getText().trim());
                n.setTennganh(txtTenNganh.getText().trim());
                n.setnChitieu(txtChiTieu.getText().isEmpty() ? 0 : Integer.parseInt(txtChiTieu.getText().trim()));
                n.setnDiemsan(txtDiemSan.getText().isEmpty() ? null : Double.parseDouble(txtDiemSan.getText().trim()));
                String selected = (String) cbTohopGoc.getSelectedItem();
                if (selected.equals("-- Chọn tổ hợp --")) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn tổ hợp gốc!");
                    return;
                }
                n.setnTohopgoc(selected);

                if (nganhDAO.addNganh(n)) {
                    JOptionPane.showMessageDialog(this, "Thêm thành công!");
                    loadData();
                    clearForm();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Chỉ tiêu và Điểm sàn phải là số!", "Lỗi định dạng",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        btnUpdate.addActionListener(e -> {
            // (Logic tương tự Thêm mới nhưng gán thêm Id và dùng lệnh updateNganh)
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn Ngành để sửa!");
                return;
            }
            try {
                Nganh n = new Nganh();
                n.setIdnganh(Integer.parseInt(txtId.getText()));
                n.setManganh(txtMaNganh.getText().trim());
                n.setTennganh(txtTenNganh.getText().trim());
                n.setnChitieu(txtChiTieu.getText().isEmpty() ? 0 : Integer.parseInt(txtChiTieu.getText().trim()));
                n.setnDiemsan(txtDiemSan.getText().isEmpty() ? null : Double.parseDouble(txtDiemSan.getText().trim()));
                String selected = (String) cbTohopGoc.getSelectedItem();
                if (selected.equals("-- Chọn tổ hợp --")) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn tổ hợp gốc!");
                    return;
                }
                n.setnTohopgoc(selected);

                if (nganhDAO.updateNganh(n)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadData();
                    clearForm();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Chỉ tiêu và Điểm sàn phải là số!");
            }
        });

        btnDelete.addActionListener(e -> {
            if (txtId.getText().isEmpty())
                return;
            if (JOptionPane.showConfirmDialog(this, "Xóa ngành này?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (nganhDAO.deleteNganh(Integer.parseInt(txtId.getText()))) {
                    loadData();
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
    }

    private String getSafeString(String[] data, int index) {
        return (index < data.length && data[index] != null) ? data[index].trim() : null;
    }

    private Integer getSafeInt(String[] data, int index) {
        try {
            String val = getSafeString(data, index);
            return (val == null || val.isEmpty()) ? null : Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double getSafeDouble(String[] data, int index) {
        try {
            String val = getSafeString(data, index);
            return (val == null || val.isEmpty()) ? null : Double.parseDouble(val);
        } catch (NumberFormatException e) {
            return null;
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

                String maNganh = getSafeString(data, 0);
                if (maNganh == null || maNganh.isEmpty())
                    continue;

                Nganh n = new Nganh();
                n.setManganh(maNganh);
                n.setTennganh(getSafeString(data, 1));
                n.setnChitieu(getSafeInt(data, 2));
                n.setnDiemsan(getSafeDouble(data, 3));
                n.setnTohopgoc(getSafeString(data, 4));
                n.setnDiemtrungtuyen(getSafeDouble(data, 5));
                n.setnTuyenthang(getSafeString(data, 6));
                n.setnDgnl(getSafeString(data, 7));
                n.setnThpt(getSafeString(data, 8));
                n.setnVsat(getSafeString(data, 9));
                n.setSlXtt(getSafeInt(data, 10));
                n.setSlDgnl(getSafeInt(data, 11));
                n.setSlVsat(getSafeInt(data, 12));
                n.setSlThpt(getSafeString(data, 13));

                if (!nganhDAO.isMaNganhExists(n.getManganh())) {
                    if (nganhDAO.addNganh(n))
                        success++;
                } else
                    duplicate++;
            }
            JOptionPane.showMessageDialog(this,
                    "Import xong!\nThành công: " + success + "\nBỏ qua (trùng): " + duplicate);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc file CSV!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}