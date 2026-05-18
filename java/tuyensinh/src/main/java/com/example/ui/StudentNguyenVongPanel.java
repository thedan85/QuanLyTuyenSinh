package com.example.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.example.dao.NganhDAO;
import com.example.dao.NguyenVongDAO;
import com.example.dao.ToHopDAO;
import com.example.entity.NguyenVong;
import com.example.entity.ThiSinh;

public class StudentNguyenVongPanel extends JPanel {
    private JTable table;
    private JScrollPane tableScroll;
    private DefaultTableModel tableModel;
    private NguyenVongDAO dao;
    private NganhDAO nganhDAO;
    private ToHopDAO toHopDAO;
    private ThiSinh ts;

    // Chỉ cho phép nhập 4 trường này
    private JTextField txtMaNganh, txtThuTu, txtPhuongThuc, txtToHop;
    private JButton btnAdd, btnDelete, btnRefresh;

    public StudentNguyenVongPanel(ThiSinh ts) {
        this.ts = ts;
        this.dao = new NguyenVongDAO();
        this.nganhDAO = new NganhDAO();
        this.toHopDAO = new ToHopDAO();
        setLayout(new BorderLayout(10, 10));

        // 1. Form nhập liệu tối giản
        JPanel form = new JPanel(new GridLayout(2, 4, 10, 10));
        form.setBorder(new TitledBorder("Đăng ký nguyện vọng mới"));

        txtMaNganh = new JTextField();
        txtThuTu = new JTextField();
        txtPhuongThuc = new JTextField("PT1"); // Mặc định
        txtToHop = new JTextField();

        form.add(new JLabel("Mã Ngành:"));
        form.add(txtMaNganh);
        form.add(new JLabel("Thứ tự NV:"));
        form.add(txtThuTu);
        form.add(new JLabel("Phương thức:"));
        form.add(txtPhuongThuc);
        form.add(new JLabel("Tổ hợp:"));
        form.add(txtToHop);

        // 2. Nút bấm
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAdd = new JButton("Thêm nguyện vọng");
        btnDelete = new JButton("Xóa nguyện vọng đã chọn");
        btnRefresh = new JButton("Làm mới danh sách");

        buttons.add(btnAdd);
        buttons.add(btnDelete);
        buttons.add(btnRefresh);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.NORTH);
        top.add(buttons, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        // 3. Bảng hiển thị
        String[] cols = { "TT NV", "Mã Ngành", "Phương Thức", "Tổ Hợp", "Trạng Thái" };
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        UiTableTheme.apply(table);
        tableScroll = new JScrollPane(table);
        UiTableColumns.install(table, tableScroll);
        add(tableScroll, BorderLayout.CENTER);

        setupEvents();
        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<NguyenVong> list = dao.getAll();
        for (NguyenVong nv : list) {
            if (nv.getTsCccd().equals(ts.getCccd())) {
                tableModel.addRow(new Object[] {
                        nv.getThuTuNV(), nv.getMaNganh(), nv.getPhuongThuc(), nv.getMaToHop(), nv.getKetQua()
                });
            }
        }
        UiTableColumns.refresh(table);
    }

    private void setupEvents() {
        btnRefresh.addActionListener(e -> loadData());

        btnAdd.addActionListener(e -> {
            try {
                String maNganh = txtMaNganh.getText().trim();
                String toHop = txtToHop.getText().trim();
                if (maNganh.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã ngành!");
                    return;
                }
                if (!nganhDAO.isMaNganhExists(maNganh)) {
                    JOptionPane.showMessageDialog(this, "Mã ngành không tồn tại!");
                    return;
                }
                if (!toHop.isEmpty() && !toHopDAO.isMaToHopExists(toHop)) {
                    JOptionPane.showMessageDialog(this, "Tổ hợp môn không tồn tại!");
                    return;
                }

                NguyenVong nv = new NguyenVong();
                nv.setTsCccd(ts.getCccd());
                nv.setMaNganh(maNganh);
                nv.setThuTuNV(Integer.parseInt(txtThuTu.getText().trim()));
                nv.setPhuongThuc(txtPhuongThuc.getText().trim());
                nv.setMaToHop(toHop);
                nv.setKetQua("Chờ xét");
                nv.setNvKeys(ts.getCccd() + "_" + nv.getMaNganh() + "_" + nv.getMaToHop());

                if (dao.isThuTuExists(ts.getCccd(), nv.getThuTuNV())) {
                    JOptionPane.showMessageDialog(this,
                            "Bạn đã sử dụng thứ tự nguyện vọng " + nv.getThuTuNV() + " rồi!");
                    return;
                }

                if (dao.isNganhToHopExists(nv.getTsCccd(), nv.getMaNganh(), nv.getMaToHop())) {
                    JOptionPane.showMessageDialog(this,
                            "Bạn đã đăng ký ngành + tổ hợp này rồi!");
                    return;
                }

                if (dao.add(nv)) {
                    JOptionPane.showMessageDialog(this, "Đã thêm nguyện vọng!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi: Có thể bạn đã đăng ký ngành + tổ hợp này rồi!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng!");
            }
        });

        btnDelete.addActionListener(e -> {
            // Logic xóa nguyện vọng tương tự Admin
        });
    }
}