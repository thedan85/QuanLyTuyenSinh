package com.example.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.example.dao.NganhDAO;
import com.example.dao.ToHopDAO;
import com.example.entity.Nganh;
import com.example.entity.ToHopMon;

public class NganhPanel extends JPanel {
    private JTable table;
    private JScrollPane tableScroll;
    private DefaultTableModel tableModel;
    private NganhDAO nganhDAO;

    private JTextField txtId, txtMaNganh, txtTenNganh, txtChiTieu, txtDiemSan, txtDiemTrungTuyen, txtTuyenThang,
            txtDgnl, txtThpt, txtVsat, txtSlXtt, txtSlDgnl, txtSlVsat, txtSlThpt;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnImport;
    private JComboBox<String> cbTohopGoc;

    public NganhPanel() {
        nganhDAO = new NganhDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- FORM NHẬP LIỆU (3 cột × label + ô, giống Bảng Điểm Thí Sinh) ---
        JPanel formPanel = new JPanel(new GridLayout(5, 6, 8, 8));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtMaNganh = new JTextField();
        txtTenNganh = new JTextField();
        cbTohopGoc = new JComboBox<>();
        loadToHopToCombo();
        txtChiTieu = new JTextField();
        txtDiemSan = new JTextField();
        txtDiemTrungTuyen = new JTextField();
        txtTuyenThang = new JTextField();
        txtDgnl = new JTextField();
        txtThpt = new JTextField();
        txtVsat = new JTextField();
        txtSlXtt = new JTextField();
        txtSlDgnl = new JTextField();
        txtSlVsat = new JTextField();
        txtSlThpt = new JTextField();
        txtId = new JTextField();
        txtId.setVisible(false);

        // Dòng 1: Định danh ngành
        formPanel.add(new JLabel("Mã ngành (*):"));
        formPanel.add(txtMaNganh);
        formPanel.add(new JLabel("Tên ngành (*):"));
        formPanel.add(txtTenNganh);
        formPanel.add(new JLabel("Tổ hợp gốc:"));
        formPanel.add(cbTohopGoc);

        // Dòng 2: Chỉ tiêu & điểm
        formPanel.add(new JLabel("Chỉ tiêu:"));
        formPanel.add(txtChiTieu);
        formPanel.add(new JLabel("Điểm sàn:"));
        formPanel.add(txtDiemSan);
        formPanel.add(new JLabel("Điểm trúng tuyển:"));
        formPanel.add(txtDiemTrungTuyen);

        // Dòng 3: Phương thức xét tuyển (Y/N)
        formPanel.add(new JLabel("Tuyển thẳng (Y/N):"));
        formPanel.add(txtTuyenThang);
        formPanel.add(new JLabel("ĐGNL (Y/N):"));
        formPanel.add(txtDgnl);
        formPanel.add(new JLabel("THPT (Y/N):"));
        formPanel.add(txtThpt);

        // Dòng 4: VSAT & số lượng (1)
        formPanel.add(new JLabel("VSAT (Y/N):"));
        formPanel.add(txtVsat);
        formPanel.add(new JLabel("SL tuyển thẳng:"));
        formPanel.add(txtSlXtt);
        formPanel.add(new JLabel("SL ĐGNL:"));
        formPanel.add(txtSlDgnl);

        // Dòng 5: Số lượng (2)
        formPanel.add(new JLabel("SL VSAT:"));
        formPanel.add(txtSlVsat);
        formPanel.add(new JLabel("SL THPT:"));
        formPanel.add(txtSlThpt);
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(BorderFactory.createTitledBorder("Thông tin Ngành xét tuyển"));
        formScroll.setViewportBorder(null);
        add(formScroll, BorderLayout.NORTH);

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
        UiButtons.stylePrimary(btnAdd);
        UiButtons.stylePrimary(btnUpdate);
        UiButtons.styleSecondary(btnImport);
        UiButtons.styleDanger(btnDelete);
        UiButtons.styleSecondary(btnClear);
        UiButtons.equalizeButtonsInContainer(buttonPanel);

        // --- BẢNG DỮ LIỆU ---
        String[] columns = {
                "ID", "Mã Ngành", "Tên Ngành", "Tổ Hợp Gốc", "Chỉ Tiêu",
                "Điểm Sàn", "Điểm Trúng Tuyển", "Tuyển Thẳng", "ĐGNL", "THPT", "VSAT",
                "SL XTT", "SL ĐGNL", "SL VSAT", "SL THPT", "Số lượng Đăng ký"
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

        loadData();
        setupEvents();

        // Lắng nghe sự kiện mỗi khi Panel này được hiển thị
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                Object selected = cbTohopGoc.getSelectedItem();
                loadToHopToCombo();
                if (selected != null) {
                    cbTohopGoc.setSelectedItem(selected);
                }
                UiTableColumns.refresh(table);
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
        List<Object[]> list = nganhDAO.getNganhWithRegistryCount();
        if (list != null) {
            for (Object[] row : list) {
                Nganh n = (Nganh) row[0];
                Long countRegistry = (Long) row[1];
                tableModel.addRow(new Object[] {
                        n.getIdnganh(), // 0
                        n.getManganh(), // 1
                        n.getTennganh(), // 2
                        n.getnTohopgoc(), // 3
                        n.getnChitieu(), // 4
                        n.getnDiemsan(), // 5
                        n.getnDiemtrungtuyen(), // 6
                        n.getnTuyenthang(), // 7
                        n.getnDgnl(), // 8
                        n.getnThpt(), // 9
                        n.getnVsat(), // 10
                        n.getSlXtt(), // 11
                        n.getSlDgnl(), // 12
                        n.getSlVsat(), // 13
                        n.getSlThpt(), // 14
                        countRegistry // 15
                });
            }
        }
        UiTableColumns.refresh(table);
    }

    private void clearForm() {
        txtId.setText("");
        txtMaNganh.setText("");
        txtMaNganh.setEditable(true);
        txtTenNganh.setText("");
        txtChiTieu.setText("");
        txtDiemSan.setText("");
        cbTohopGoc.setSelectedIndex(0);
        txtDiemTrungTuyen.setText("");
        txtTuyenThang.setText("");
        txtDgnl.setText("");
        txtThpt.setText("");
        txtVsat.setText("");
        txtSlXtt.setText("");
        txtSlDgnl.setText("");
        txtSlVsat.setText("");
        txtSlThpt.setText("");
        table.clearSelection();
    }

    private String getValueOrEmpty(int row, int col) {
        Object val = table.getValueAt(row, col);
        return (val == null) ? "" : val.toString();
    }

    private Integer parseIntOrNull(String text) {
        if (text == null) return null;
        String value = text.trim();
        if (value.isEmpty()) return null;
        return Integer.parseInt(value);
    }

    private Double parseDoubleOrNull(String text) {
        if (text == null) return null;
        String value = text.trim();
        if (value.isEmpty()) return null;
        value = value.replace(",", ".");
        return Double.parseDouble(value);
    }

    private String parseIntStringOrNull(String text) {
        if (text == null) return null;
        String value = text.trim();
        if (value.isEmpty()) return null;
        return String.valueOf(Integer.parseInt(value));
    }

    private void setupEvents() {
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtId.setText(getValueOrEmpty(row, 0));
                txtMaNganh.setText(getValueOrEmpty(row, 1));
                txtMaNganh.setEditable(false);
                txtTenNganh.setText(getValueOrEmpty(row, 2));
                cbTohopGoc.setSelectedItem(getValueOrEmpty(row, 3));
                txtChiTieu.setText(getValueOrEmpty(row, 4));
                txtDiemSan.setText(getValueOrEmpty(row, 5));
                txtDiemTrungTuyen.setText(getValueOrEmpty(row, 6));
                txtTuyenThang.setText(getValueOrEmpty(row, 7));
                txtDgnl.setText(getValueOrEmpty(row, 8));
                txtThpt.setText(getValueOrEmpty(row, 9));
                txtVsat.setText(getValueOrEmpty(row, 10));
                txtSlXtt.setText(getValueOrEmpty(row, 11));
                txtSlDgnl.setText(getValueOrEmpty(row, 12));
                txtSlVsat.setText(getValueOrEmpty(row, 13));
                txtSlThpt.setText(getValueOrEmpty(row, 14));
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
                n.setnChitieu(parseIntOrNull(txtChiTieu.getText()));
                n.setnDiemsan(parseDoubleOrNull(txtDiemSan.getText()));
                String selected = (String) cbTohopGoc.getSelectedItem();
                if (selected != null && selected.equals("-- Chọn tổ hợp --")) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn tổ hợp gốc!");
                    return;
                }
                n.setnTohopgoc(selected);

                // Lưu thêm các trường còn lại khi thêm mới
                n.setnDiemtrungtuyen(parseDoubleOrNull(txtDiemTrungTuyen.getText()));
                n.setnTuyenthang(txtTuyenThang.getText().trim());
                n.setnDgnl(txtDgnl.getText().trim());
                n.setnThpt(txtThpt.getText().trim());
                n.setnVsat(txtVsat.getText().trim());
                
                n.setSlXtt(parseIntOrNull(txtSlXtt.getText()));
                n.setSlDgnl(parseIntOrNull(txtSlDgnl.getText()));
                n.setSlVsat(parseIntOrNull(txtSlVsat.getText()));
                n.setSlThpt(parseIntStringOrNull(txtSlThpt.getText()));

                if (nganhDAO.addNganh(n)) {
                    JOptionPane.showMessageDialog(this, "Thêm thành công!");
                    loadData();
                    clearForm();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Các ô Chỉ tiêu, Điểm và SL phải là số!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnUpdate.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn Ngành để sửa!");
                return;
            }
            try {
                Nganh n = new Nganh();
                n.setIdnganh(Integer.parseInt(txtId.getText()));
                n.setManganh(txtMaNganh.getText().trim());
                n.setTennganh(txtTenNganh.getText().trim());
                n.setnChitieu(parseIntOrNull(txtChiTieu.getText()));
                n.setnDiemsan(parseDoubleOrNull(txtDiemSan.getText()));
                n.setnTohopgoc((String) cbTohopGoc.getSelectedItem());

                n.setnDiemtrungtuyen(parseDoubleOrNull(txtDiemTrungTuyen.getText()));
                n.setnTuyenthang(txtTuyenThang.getText().trim());
                n.setnDgnl(txtDgnl.getText().trim());
                n.setnThpt(txtThpt.getText().trim());
                n.setnVsat(txtVsat.getText().trim());
                
                // ĐÃ FIX: Lưu đầy đủ các ô số lượng khi bấm cập nhật
                n.setSlXtt(parseIntOrNull(txtSlXtt.getText()));
                n.setSlDgnl(parseIntOrNull(txtSlDgnl.getText()));
                n.setSlVsat(parseIntOrNull(txtSlVsat.getText()));
                n.setSlThpt(parseIntStringOrNull(txtSlThpt.getText()));

                if (nganhDAO.updateNganh(n)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadData();
                    clearForm();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng kiểm tra định dạng số ở các ô số lượng/điểm/chỉ tiêu!");
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