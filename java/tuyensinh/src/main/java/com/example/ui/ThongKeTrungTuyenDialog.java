package com.example.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Dialog thống kê số trúng tuyển theo PT / ngành (mục 6c) — UI; dữ liệu nối DAO sau.
 */
public class ThongKeTrungTuyenDialog extends JDialog {

    private static final String[] COLUMNS = {
            "Mã ngành", "Tên ngành", "Chỉ tiêu",
            "Trúng PT1", "Trúng PT2", "Trúng PT3", "Tổng trúng",
            "SL THPT (CT)", "SL ĐGNL (CT)", "SL VSAT (CT)"
    };

    private JButton btnRefresh;
    private DefaultTableModel tableModel;
    private JTable table;
    private JScrollPane tableScroll;

    public ThongKeTrungTuyenDialog(Window owner) {
        super(owner, "Thống kê trúng tuyển theo ngành", ModalityType.APPLICATION_MODAL);
        setSize(900, 480);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        btnRefresh = new JButton("Làm mới");
        topPanel.add(btnRefresh);
        UiButtons.stylePrimary(btnRefresh);
        UiButtons.equalizeHeightsOnly(btnRefresh);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        UiTableTheme.apply(table);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                UiTableTheme.applyColumnAlignment(tbl, c, column);
                UiTableTheme.applyDataRowAppearance(tbl, c, row, isSelected);
                return c;
            }
        });
        tableScroll = new JScrollPane(table);
        UiTableColumns.install(table, tableScroll);
        add(tableScroll, BorderLayout.CENTER);

        JButton btnClose = new JButton("Đóng");
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.add(btnClose);
        UiButtons.styleSecondary(btnClose);
        add(southPanel, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> refreshTable());
        btnClose.addActionListener(e -> dispose());

        refreshTable();
    }

    public static void showDialog(Component parent) {
        Window owner = parent != null ? SwingUtilities.getWindowAncestor(parent) : null;
        ThongKeTrungTuyenDialog dialog = new ThongKeTrungTuyenDialog(owner);
        dialog.setVisible(true);
    }

    /** UI placeholder — nối NguyenVongDAO sau. */
    public void refreshTable() {
        tableModel.setRowCount(0);
    }
}
