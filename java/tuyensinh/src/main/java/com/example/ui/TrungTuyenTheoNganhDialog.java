package com.example.ui;

import com.example.dao.NganhDAO;
import com.example.entity.Nganh;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Dialog danh sách trúng tuyển theo ngành (mục 6b) — UI; dữ liệu nối DAO sau.
 */
public class TrungTuyenTheoNganhDialog extends JDialog {

    private static final String TAT_CA = "— Tất cả ngành —";

    private static final String[] COLUMNS = {
            "CCCD", "Họ", "Tên", "TT NV", "Mã ngành", "Tên ngành", "PT", "Tổ hợp", "THM", "Điểm XT", "Kết quả"
    };

    private final NganhDAO nganhDAO = new NganhDAO();
    private JComboBox<String> cbNganh;
    private JButton btnRefresh;
    private DefaultTableModel tableModel;
    private JTable table;
    private JScrollPane tableScroll;

    public TrungTuyenTheoNganhDialog(Window owner) {
        super(owner, "Danh sách trúng tuyển", ModalityType.APPLICATION_MODAL);
        setSize(960, 520);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topPanel.add(new JLabel("Ngành:"));
        cbNganh = new JComboBox<>();
        cbNganh.setPreferredSize(new Dimension(320, 36));
        topPanel.add(cbNganh);
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
                int colKq = tbl.getColumnModel().getColumnIndex("Kết quả");
                if (!isSelected && column == colKq && value != null
                        && value.toString().contains("TRÚNG TUYỂN")) {
                    c.setForeground(new Color(0, 150, 0));
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                }
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

        loadNganhCombo();
        btnRefresh.addActionListener(e -> refreshTable());
        cbNganh.addActionListener(e -> refreshTable());
        btnClose.addActionListener(e -> dispose());

        refreshTable();
    }

    public static void showDialog(Component parent) {
        Window owner = parent != null ? SwingUtilities.getWindowAncestor(parent) : null;
        TrungTuyenTheoNganhDialog dialog = new TrungTuyenTheoNganhDialog(owner);
        dialog.setVisible(true);
    }

    private void loadNganhCombo() {
        cbNganh.removeAllItems();
        cbNganh.addItem(TAT_CA);
        List<Nganh> list = nganhDAO.getAllNganh();
        if (list != null) {
            for (Nganh n : list) {
                if (n.getManganh() != null) {
                    String ten = n.getTennganh() != null ? n.getTennganh() : "";
                    cbNganh.addItem(n.getManganh() + " - " + ten);
                }
            }
        }
    }

    /** UI placeholder — nối NguyenVongDAO sau. */
    public void refreshTable() {
        tableModel.setRowCount(0);
    }
}
