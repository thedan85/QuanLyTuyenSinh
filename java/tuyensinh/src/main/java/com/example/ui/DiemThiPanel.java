package com.example.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.example.dao.DiemThiDAO;
import com.example.dao.ThiSinhDAO;
import com.example.entity.DiemThi;
import com.example.entity.ThiSinh;

public class DiemThiPanel extends JPanel {
    private JTable table;
    private JScrollPane tableScroll;
    private DefaultTableModel tableModel;
    private DiemThiDAO dao;
    private ThiSinhDAO thiSinhDAO;

    // Khai báo gần 20 trường dữ liệu
    private JTextField txtId, txtSbd, txtPhuongThuc;
    private JComboBox<String> cbCccd;
    private JTextField txtTo, txtLi, txtHo, txtSi, txtSu, txtDi, txtVa;
    private JTextField txtN1Thi, txtN1Cc, txtCncn, txtCnnn, txtTi, txtKtpl;
    private JTextField txtNl1, txtNk1, txtNk2;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnImport, btnThongKe; // Thêm btnThongKe
    private final Map<String, String> cccdToSbd = new HashMap<>();

    public DiemThiPanel() {
        dao = new DiemThiDAO();
        thiSinhDAO = new ThiSinhDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- 1. FORM NHẬP LIỆU
        JPanel formPanel = new JPanel(new GridLayout(7, 6, 8, 8));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Khởi tạo và add các trường theo từng cặp (Label + TextField)
        cbCccd = new JComboBox<>();
        txtSbd = new JTextField();
        txtSbd.setEditable(false);
        txtSbd.setBackground(new Color(235, 235, 235));
        txtPhuongThuc = new JTextField();
        txtTo = new JTextField();
        txtLi = new JTextField();
        txtHo = new JTextField();
        txtSi = new JTextField();
        txtSu = new JTextField();
        txtDi = new JTextField();
        txtVa = new JTextField();
        txtN1Thi = new JTextField();
        txtN1Cc = new JTextField();
        txtCncn = new JTextField();
        txtCnnn = new JTextField();
        txtTi = new JTextField();
        txtKtpl = new JTextField();
        txtNl1 = new JTextField();
        txtNk1 = new JTextField();
        txtNk2 = new JTextField();
        txtId = new JTextField();
        txtId.setVisible(false);

        // Dòng 1: Thông tin chung
        formPanel.add(new JLabel("CCCD (*):"));
        formPanel.add(cbCccd);
        formPanel.add(new JLabel("Số báo danh:"));
        formPanel.add(txtSbd);
        formPanel.add(new JLabel("Phương thức:"));
        formPanel.add(txtPhuongThuc);

        // Dòng 2: Môn tự nhiên
        formPanel.add(new JLabel("Toán:"));
        formPanel.add(txtTo);
        formPanel.add(new JLabel("Vật lý:"));
        formPanel.add(txtLi);
        formPanel.add(new JLabel("Hóa học:"));
        formPanel.add(txtHo);

        // Dòng 3: Môn xã hội
        formPanel.add(new JLabel("Sinh học:"));
        formPanel.add(txtSi);
        formPanel.add(new JLabel("Lịch sử:"));
        formPanel.add(txtSu);
        formPanel.add(new JLabel("Địa lý:"));
        formPanel.add(txtDi);

        // Dòng 4: Ngữ văn & Tiếng Anh
        formPanel.add(new JLabel("Ngữ văn:"));
        formPanel.add(txtVa);
        formPanel.add(new JLabel("N1_THI (T.Anh gốc):"));
        formPanel.add(txtN1Thi);
        formPanel.add(new JLabel("N1_CC (T.Anh C/C):"));
        formPanel.add(txtN1Cc);

        // Dòng 5: GDCD & Ngoại ngữ khác
        formPanel.add(new JLabel("CNCN:"));
        formPanel.add(txtCncn);
        formPanel.add(new JLabel("CNNN:"));
        formPanel.add(txtCnnn);
        formPanel.add(new JLabel("Tin học:"));
        formPanel.add(txtTi);

        // Dòng 6: Các môn khác
        formPanel.add(new JLabel("KTPL:"));
        formPanel.add(txtKtpl);
        formPanel.add(new JLabel("NL1 (ĐGNL):"));
        formPanel.add(txtNl1);
        formPanel.add(new JLabel("Năng khiếu 1:"));
        formPanel.add(txtNk1);

        // Dòng 7: NK2 & ID ẩn
        formPanel.add(new JLabel("Năng khiếu 2:"));
        formPanel.add(txtNk2);
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(BorderFactory.createTitledBorder("Bảng Điểm Thí Sinh"));
        formScroll.setViewportBorder(null);
        add(formScroll, BorderLayout.NORTH);

        // --- 2. THANH CÔNG CỤ ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnAdd = new JButton("Thêm mới");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa");
        btnClear = new JButton("Làm mới form");
        btnImport = new JButton("Import CSV");

        btnThongKe = new JButton("📊 Thống kê");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnImport);
        buttonPanel.add(btnThongKe); // Add nút vào panel
        UiButtons.stylePrimary(btnAdd);
        UiButtons.stylePrimary(btnUpdate);
        UiButtons.styleSecondary(btnImport);
        UiButtons.stylePrimary(btnThongKe);
        UiButtons.styleDanger(btnDelete);
        UiButtons.styleSecondary(btnClear);
        UiButtons.equalizeButtonsInContainer(buttonPanel);

        // --- 3. BẢNG DỮ LIỆU ---
        String[] columns = { "ID", "CCCD", "SBD", "PT", "Toán", "Lý", "Hóa", "Sinh", "Sử", "Địa", "Văn", "N1_Thi",
                "N1_CC", "CNCN", "CNNN", "Tin", "KTPL", "NL1", "NK1", "NK2" };
        tableModel = new DefaultTableModel(columns, 0) {
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
                UiTableTheme.applyDataRowAppearance(table, c, row, isSelected);
                return c;
            }
        });

        tableScroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        UiTableColumns.install(table, tableScroll);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(buttonPanel, BorderLayout.NORTH);
        centerPanel.add(tableScroll, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        loadCccdCombo();
        cbCccd.addActionListener(e -> syncSbdWithCccd());
        loadData();
        setupEvents();

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                String selected = getSelectedCccd();
                loadCccdCombo();
                if (!selected.isEmpty()) {
                    selectCccd(selected);
                }
                UiTableColumns.refresh(table);
            }
        });
    }

    private void loadCccdCombo() {
        cbCccd.removeAllItems();
        cbCccd.addItem("-- Chọn CCCD --");
        cccdToSbd.clear();
        List<ThiSinh> list = thiSinhDAO.getAllThiSinh();
        if (list != null) {
            for (ThiSinh ts : list) {
                String cccd = ts.getCccd();
                if (cccd != null && !cccd.trim().isEmpty()) {
                    cbCccd.addItem(cccd);
                    String sbd = ts.getSobaodanh();
                    cccdToSbd.put(cccd, sbd == null ? "" : sbd.trim());
                }
            }
        }
        syncSbdWithCccd();
    }

    private void syncSbdWithCccd() {
        String cccd = getSelectedCccd();
        if (cccd.isEmpty()) {
            txtSbd.setText("");
            return;
        }
        txtSbd.setText(cccdToSbd.getOrDefault(cccd, ""));
    }

    private String getSelectedCccd() {
        Object sel = cbCccd.getSelectedItem();
        if (sel == null) {
            return "";
        }
        String cccd = sel.toString().trim();
        return cccd.startsWith("--") ? "" : cccd;
    }

    private void selectCccd(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            cbCccd.setSelectedIndex(0);
            return;
        }
        boolean found = false;
        for (int i = 0; i < cbCccd.getItemCount(); i++) {
            if (cccd.equals(cbCccd.getItemAt(i))) {
                found = true;
                break;
            }
        }
        if (!found) {
            cbCccd.addItem(cccd);
        }
        cbCccd.setSelectedItem(cccd);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<DiemThi> list = dao.getAll();
        if (list != null) {
            for (DiemThi d : list) {
                tableModel.addRow(new Object[] {
                        d.getIddiemthi(), d.getCccd(), d.getSobaodanh(), d.getdPhuongthuc(),
                        d.getDiemToan(), d.getDiemLy(), d.getDiemHoa(), d.getDiemSinh(),
                        d.getDiemSu(), d.getDiemDia(), d.getDiemVan(), d.getN1Thi(), d.getN1Cc(),
                        d.getCncn(), d.getCnnn(), d.getDiemTin(), d.getKtpl(), d.getNl1(), d.getNk1(), d.getNk2()
                });
            }
        }
        UiTableColumns.refresh(table);
    }

    private Double parseDouble(String str) {
        if (str == null || str.trim().isEmpty())
            return null;
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getSafeString(String[] data, int index) {
        if (index >= data.length)
            return "";
        return data[index].trim();
    }

    private Double getSafeDouble(String[] data, int index) {
        if (index >= data.length)
            return null;
        return parseDouble(data[index]);
    }

    private DiemThi getDataFromForm() throws NumberFormatException {
        DiemThi d = new DiemThi();
        d.setCccd(getSelectedCccd());
        d.setSobaodanh(txtSbd.getText().trim());
        d.setdPhuongthuc(txtPhuongThuc.getText().trim());

        d.setDiemToan(parseDouble(txtTo.getText()));
        d.setDiemLy(parseDouble(txtLi.getText()));
        d.setDiemHoa(parseDouble(txtHo.getText()));
        d.setDiemSinh(parseDouble(txtSi.getText()));
        d.setDiemSu(parseDouble(txtSu.getText()));
        d.setDiemDia(parseDouble(txtDi.getText()));
        d.setDiemVan(parseDouble(txtVa.getText()));
        d.setN1Thi(parseDouble(txtN1Thi.getText()));
        d.setN1Cc(parseDouble(txtN1Cc.getText()));
        d.setCncn(parseDouble(txtCncn.getText()));
        d.setCnnn(parseDouble(txtCnnn.getText()));
        d.setDiemTin(parseDouble(txtTi.getText()));
        d.setKtpl(parseDouble(txtKtpl.getText()));
        d.setNl1(parseDouble(txtNl1.getText()));
        d.setNk1(parseDouble(txtNk1.getText()));
        d.setNk2(parseDouble(txtNk2.getText()));
        return d;
    }

    private void clearForm() {
        txtId.setText("");
        cbCccd.setSelectedIndex(0);
        txtSbd.setText("");
        txtPhuongThuc.setText("");
        txtTo.setText("");
        txtLi.setText("");
        txtHo.setText("");
        txtSi.setText("");
        txtSu.setText("");
        txtDi.setText("");
        txtVa.setText("");
        txtN1Thi.setText("");
        txtN1Cc.setText("");
        txtCncn.setText("");
        txtCnnn.setText("");
        txtTi.setText("");
        txtKtpl.setText("");
        txtNl1.setText("");
        txtNk1.setText("");
        txtNk2.setText("");
        cbCccd.setEnabled(true);
        table.clearSelection();
    }

    private void setupEvents() {
        // Sự kiện mở popup Thống kê
        btnThongKe.addActionListener(e -> showThongKeDialog());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtId.setText(getValue(row, 0));
                selectCccd(getValue(row, 1));
                txtSbd.setText(getValue(row, 2));
                txtPhuongThuc.setText(getValue(row, 3));
                txtTo.setText(getValue(row, 4));
                txtLi.setText(getValue(row, 5));
                txtHo.setText(getValue(row, 6));
                txtSi.setText(getValue(row, 7));
                txtSu.setText(getValue(row, 8));
                txtDi.setText(getValue(row, 9));
                txtVa.setText(getValue(row, 10));
                txtN1Thi.setText(getValue(row, 11));
                txtN1Cc.setText(getValue(row, 12));
                txtCncn.setText(getValue(row, 13));
                txtCnnn.setText(getValue(row, 14));
                txtTi.setText(getValue(row, 15));
                txtKtpl.setText(getValue(row, 16));
                txtNl1.setText(getValue(row, 17));
                txtNk1.setText(getValue(row, 18));
                txtNk2.setText(getValue(row, 19));
                cbCccd.setEnabled(false);
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
                ThiSinh ts = thiSinhDAO.getThiSinhByCccd(cccd);
                String sbd = txtSbd.getText().trim();
                if (ts != null) {
                    String sbdDb = ts.getSobaodanh() == null ? "" : ts.getSobaodanh().trim();
                    if (!sbd.isEmpty() && !sbdDb.isEmpty() && !sbd.equals(sbdDb)) {
                        JOptionPane.showMessageDialog(this, "Số báo danh không khớp với CCCD đã chọn!");
                        return;
                    }
                }
                if (dao.isCccdExists(cccd)) {
                    JOptionPane.showMessageDialog(this, "Thí sinh này đã có điểm trong hệ thống!");
                    return;
                }
                if (dao.addDiem(getDataFromForm())) {
                    loadData();
                    clearForm();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: Điểm phải là số hợp lệ!");
            }
        });

        btnUpdate.addActionListener(e -> {
            if (txtId.getText().isEmpty())
                return;
            if (getSelectedCccd().isEmpty()) {
                JOptionPane.showMessageDialog(this, "CCCD không được để trống!");
                return;
            }
            if (!thiSinhDAO.isCccdExists(getSelectedCccd())) {
                JOptionPane.showMessageDialog(this, "CCCD chưa tồn tại trong bảng thí sinh!");
                return;
            }
            try {
                DiemThi d = getDataFromForm();
                d.setIddiemthi(Integer.parseInt(txtId.getText()));
                if (dao.updateDiem(d)) {
                    loadData();
                    clearForm();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: Điểm phải là số hợp lệ!");
            }
        });

        btnDelete.addActionListener(e -> {
            if (!txtId.getText().isEmpty()
                    && JOptionPane.showConfirmDialog(this, "Xóa toàn bộ điểm của TS này?", "Xác nhận", 0) == 0) {
                if (dao.deleteDiem(Integer.parseInt(txtId.getText()))) {
                    loadData();
                    clearForm();
                }
            }
        });

        btnImport.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                importCSV(fc.getSelectedFile());
        });
    }

    // TÍNH NĂNG MỚI: Popup Thống kê điểm
    private void showThongKeDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thống Kê Phổ Điểm", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Chọn môn / Loại điểm:"));
        String[] subjects = { "Toán", "Vật lý", "Hóa học", "Sinh học", "Lịch sử", "Địa lý", "Ngữ văn",
                "Tiếng Anh (Thi)", "Đánh giá Năng lực (NL1)" };
        JComboBox<String> cbSubjects = new JComboBox<>(subjects);
        topPanel.add(cbSubjects);
        dialog.add(topPanel, BorderLayout.NORTH);

        JTextArea txtResult = new JTextArea();
        txtResult.setEditable(false);
        txtResult.setFont(new Font("Monospaced", Font.BOLD, 14));
        txtResult.setMargin(new Insets(10, 10, 10, 10));
        dialog.add(new JScrollPane(txtResult), BorderLayout.CENTER);

        // Hàm tính toán và hiển thị
        Runnable calculateStats = () -> {
            int selectedIdx = cbSubjects.getSelectedIndex();
            // Map Index của ComboBox với cột trong Bảng hiển thị (tableModel)
            int col = -1;
            boolean isDGNL = false;
            switch (selectedIdx) {
                case 0:
                    col = 4;
                    break; // Toán
                case 1:
                    col = 5;
                    break; // Lý
                case 2:
                    col = 6;
                    break; // Hóa
                case 3:
                    col = 7;
                    break; // Sinh
                case 4:
                    col = 8;
                    break; // Sử
                case 5:
                    col = 9;
                    break; // Địa
                case 6:
                    col = 10;
                    break; // Văn
                case 7:
                    col = 11;
                    break; // N1_Thi
                case 8:
                    col = 17;
                    isDGNL = true;
                    break; // NL1
            }

            int count = 0;
            double sum = 0, max = -1, min = 9999;
            int range1 = 0, range2 = 0, range3 = 0, range4 = 0; // Phổ điểm

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Object val = tableModel.getValueAt(i, col);
                if (val != null && !val.toString().isEmpty()) {
                    try {
                        double score = Double.parseDouble(val.toString());
                        count++;
                        sum += score;
                        if (score > max)
                            max = score;
                        if (score < min)
                            min = score;

                        // Tính toán phổ điểm
                        if (isDGNL) { // ĐGNL thang điểm 1200
                            if (score < 500)
                                range1++;
                            else if (score < 700)
                                range2++;
                            else if (score < 900)
                                range3++;
                            else
                                range4++;
                        } else { // Điểm thi THPT thang điểm 10
                            if (score < 5)
                                range1++;
                            else if (score < 7)
                                range2++;
                            else if (score < 9)
                                range3++;
                            else
                                range4++;
                        }
                    } catch (Exception e) {
                    }
                }
            }

            if (count == 0) {
                txtResult.setText("Không có dữ liệu điểm cho môn này.");
            } else {
                double avg = sum / count;
                StringBuilder sb = new StringBuilder();
                sb.append("=== KẾT QUẢ THỐNG KÊ: ").append(cbSubjects.getSelectedItem()).append(" ===\n\n");
                sb.append(String.format(" 🔹 Tổng số bài thi : %d\n", count));
                sb.append(String.format(" 🔹 Điểm cao nhất   : %.2f\n", max));
                sb.append(String.format(" 🔹 Điểm thấp nhất  : %.2f\n", min));
                sb.append(String.format(" 🔹 Điểm trung bình : %.2f\n\n", avg));

                sb.append("=== PHÂN BỐ PHỔ ĐIỂM ===\n");
                if (isDGNL) {
                    sb.append(String.format(" 📉 Dưới 500 điểm : %d bài thi\n", range1));
                    sb.append(String.format(" 📊 500 - 700     : %d bài thi\n", range2));
                    sb.append(String.format(" 📈 700 - 900     : %d bài thi\n", range3));
                    sb.append(String.format(" 🏆 Trên 900 điểm : %d bài thi\n", range4));
                } else {
                    sb.append(String.format(" 📉 Dưới 5 (Yếu/Kém) : %d bài thi\n", range1));
                    sb.append(String.format(" 📊 Từ 5 - <7 (TB)   : %d bài thi\n", range2));
                    sb.append(String.format(" 📈 Từ 7 - <9 (Khá)  : %d bài thi\n", range3));
                    sb.append(String.format(" 🏆 Từ 9 - 10 (Giỏi) : %d bài thi\n", range4));
                }
                txtResult.setText(sb.toString());
            }
        };

        // Gắn sự kiện: Đổi môn là tự động tính toán lại
        cbSubjects.addActionListener(e -> calculateStats.run());
        calculateStats.run(); // Chạy ngay lần đầu mở lên

        dialog.setVisible(true);
    }

    private String getValue(int row, int col) {
        Object val = table.getValueAt(row, col);
        return val == null ? "" : val.toString();
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

                String cccd = getSafeString(data, 0);
                if (cccd.isEmpty())
                    continue;

                DiemThi d = new DiemThi();
                d.setCccd(cccd);
                d.setSobaodanh(getSafeString(data, 1));
                d.setdPhuongthuc(getSafeString(data, 2));
                d.setDiemToan(getSafeDouble(data, 3));
                d.setDiemLy(getSafeDouble(data, 4));
                d.setDiemHoa(getSafeDouble(data, 5));
                d.setDiemSinh(getSafeDouble(data, 6));
                d.setDiemSu(getSafeDouble(data, 7));
                d.setDiemDia(getSafeDouble(data, 8));
                d.setDiemVan(getSafeDouble(data, 9));
                d.setN1Thi(getSafeDouble(data, 10));
                d.setN1Cc(getSafeDouble(data, 11));
                d.setCncn(getSafeDouble(data, 12));
                d.setCnnn(getSafeDouble(data, 13));
                d.setDiemTin(getSafeDouble(data, 14));
                d.setKtpl(getSafeDouble(data, 15));
                d.setNl1(getSafeDouble(data, 16));
                d.setNk1(getSafeDouble(data, 17));
                d.setNk2(getSafeDouble(data, 18));

                if (!thiSinhDAO.isCccdExists(d.getCccd())) {
                    invalid++;
                    continue;
                }
                ThiSinh ts = thiSinhDAO.getThiSinhByCccd(d.getCccd());
                if (ts != null) {
                    String sbdDb = ts.getSobaodanh() == null ? "" : ts.getSobaodanh().trim();
                    String sbdCsv = d.getSobaodanh() == null ? "" : d.getSobaodanh().trim();
                    if (!sbdCsv.isEmpty() && !sbdDb.isEmpty() && !sbdCsv.equals(sbdDb)) {
                        invalid++;
                        continue;
                    }
                }
                if (!dao.isCccdExists(d.getCccd())) {
                    if (dao.addDiem(d)) {
                        success++;
                    }
                } else {
                    duplicate++;
                }
            }
            JOptionPane.showMessageDialog(this,
                    "Import xong!\nThành công: " + success + "\nTrùng/Bỏ qua: " + duplicate + "\nKhông hợp lệ: " + invalid);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc file CSV: " + ex.getMessage());
        }
    }
}