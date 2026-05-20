package com.example.ui;

import com.example.dao.ToHopDAO;
import com.example.entity.ToHopMon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ToHopPanel extends JPanel implements RefreshablePanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private ToHopDAO toHopDAO;

    // Các trường nhập liệu
    private JTextField txtId, txtMaToHop, txtMon1, txtMon2, txtMon3, txtTenToHop;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnImport;

    public ToHopPanel() {
        toHopDAO = new ToHopDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- PHẦN 1: FORM NHẬP LIỆU (Phía trên) ---
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        formPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        formPanel.add(new JLabel("Mã tổ hợp (VD: A00):"));
        txtMaToHop = new JTextField();
        formPanel.add(txtMaToHop);

        formPanel.add(new JLabel("Tên tổ hợp (VD: Toán, Lý, Hóa):"));
        txtTenToHop = new JTextField();
        formPanel.add(txtTenToHop);

        formPanel.add(new JLabel("Môn 1 (Mã môn):"));
        txtMon1 = new JTextField();
        formPanel.add(txtMon1);

        formPanel.add(new JLabel("Môn 2 (Mã môn):"));
        txtMon2 = new JTextField();
        formPanel.add(txtMon2);

        formPanel.add(new JLabel("Môn 3 (Mã môn):"));
        txtMon3 = new JTextField();
        formPanel.add(txtMon3);

        // ID ẩn dùng để lưu tạm khi chọn sửa
        txtId = new JTextField();
        txtId.setVisible(false); 

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(BorderFactory.createTitledBorder("Thông tin Tổ hợp môn"));
        formScroll.setViewportBorder(null);
        formScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        formScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        add(formScroll, BorderLayout.NORTH);

        // --- PHẦN 2: THANH CÔNG CỤ (Ở giữa) ---
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

        // --- PHẦN 3: BẢNG DỮ LIỆU (Phía dưới) ---
        String[] columns = {"ID", "Mã Tổ Hợp", "Môn 1", "Môn 2", "Môn 3", "Tên Tổ Hợp"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        UiTableTheme.apply(table);
        JScrollPane scrollPane = new JScrollPane(table);
        UiTableColumns.install(table, scrollPane);

        // Gom nút và bảng vào phần Center
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.add(buttonPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // --- GẮN SỰ KIỆN ---
        loadData();
        setupEvents();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<ToHopMon> list = toHopDAO.getAllToHop();
        if (list != null) {
            for (ToHopMon t : list) {
                tableModel.addRow(new Object[]{
                    t.getIdtohop(), t.getMatohop(), t.getMon1(), t.getMon2(), t.getMon3(), t.getTentohop()
                });
            }
        }
        UiTableColumns.refresh(table);
    }

    @Override
    public void refreshData() {
        loadData();
    }

    private void clearForm() {
        txtId.setText("");
        txtMaToHop.setText("");
        txtMaToHop.setEditable(true); // Cho phép nhập lại mã
        txtMon1.setText("");
        txtMon2.setText("");
        txtMon3.setText("");
        txtTenToHop.setText("");
        table.clearSelection();
    }

    private void setupEvents() {
        // Click vào bảng để đổ dữ liệu lên Form
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtId.setText(table.getValueAt(row, 0).toString());
                txtMaToHop.setText(table.getValueAt(row, 1).toString());
                txtMaToHop.setEditable(false); // Không cho sửa mã khi đang Edit
                txtMon1.setText(table.getValueAt(row, 2).toString());
                txtMon2.setText(table.getValueAt(row, 3).toString());
                txtMon3.setText(table.getValueAt(row, 4).toString());
                txtTenToHop.setText(table.getValueAt(row, 5).toString());
            }
        });

        btnClear.addActionListener(e -> clearForm());

        btnAdd.addActionListener(e -> {
            if (txtMaToHop.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã tổ hợp!");
                return;
            }
            if (toHopDAO.isMaToHopExists(txtMaToHop.getText().trim())) {
                JOptionPane.showMessageDialog(this, "Mã tổ hợp đã tồn tại!");
                return;
            }
            ToHopMon t = new ToHopMon();
            t.setMatohop(txtMaToHop.getText().trim());
            t.setMon1(txtMon1.getText().trim());
            t.setMon2(txtMon2.getText().trim());
            t.setMon3(txtMon3.getText().trim());
            t.setTentohop(txtTenToHop.getText().trim());

            if (toHopDAO.addToHop(t)) {
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
                loadData();
                clearForm();
            }
        });

        btnUpdate.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một tổ hợp dưới bảng để sửa!");
                return;
            }
            ToHopMon t = new ToHopMon();
            t.setIdtohop(Integer.parseInt(txtId.getText()));
            t.setMatohop(txtMaToHop.getText().trim()); // Gốc
            t.setMon1(txtMon1.getText().trim());
            t.setMon2(txtMon2.getText().trim());
            t.setMon3(txtMon3.getText().trim());
            t.setTentohop(txtTenToHop.getText().trim());

            if (toHopDAO.updateToHop(t)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadData();
                clearForm();
            }
        });

        btnDelete.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một tổ hợp để xóa!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa tổ hợp này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (toHopDAO.deleteToHop(Integer.parseInt(txtId.getText()))) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    loadData();
                    clearForm();
                }
            }
        });

        // Sự kiện cho nút Import CSV
        btnImport.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn file CSV để import");
            
            // Lọc chỉ cho phép chọn file .csv
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));

            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                importDataFromCSV(file);
            }
        });
    }

    private void importDataFromCSV(java.io.File file) {
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            int successCount = 0;
            int duplicateCount = 0;

            // Đọc từng dòng cho đến hết file
            while ((line = br.readLine()) != null) {
                // Bỏ qua dòng tiêu đề đầu tiên
                if (isFirstLine) { 
                    isFirstLine = false; 
                    continue; 
                } 

                // Tách dữ liệu bằng dấu phẩy (Cấu trúc file CSV: matohop,mon1,mon2,mon3,tentohop)
                String[] data = line.split(",");
                
                if (data.length >= 5) {
                    ToHopMon t = new ToHopMon();
                    t.setMatohop(data[0].trim());
                    t.setMon1(data[1].trim());
                    t.setMon2(data[2].trim());
                    t.setMon3(data[3].trim());
                    t.setTentohop(data[4].trim());

                    // Kiểm tra xem mã tổ hợp đã tồn tại chưa để tránh lỗi trùng lặp
                    if (!toHopDAO.isMaToHopExists(t.getMatohop())) {
                        if (toHopDAO.addToHop(t)) {
                            successCount++;
                        }
                    } else {
                        duplicateCount++; // Đếm số dòng bị trùng
                    }
                }
            }
            
            JOptionPane.showMessageDialog(this, 
                "Import hoàn tất!\n- Thành công: " + successCount + " dòng.\n- Trùng lặp (bỏ qua): " + duplicateCount + " dòng.", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            
            loadData(); // Tải lại bảng sau khi import xong

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi đọc file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

}