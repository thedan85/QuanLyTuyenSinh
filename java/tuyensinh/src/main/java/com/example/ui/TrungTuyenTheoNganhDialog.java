package com.example.ui;

import com.example.dao.NganhDAO;
import com.example.dao.NguyenVongDAO;
import com.example.dto.TrungTuyenRow;
import com.example.entity.Nganh;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Dialog danh sách trúng tuyển theo ngành (mục 6b).
 */
public class TrungTuyenTheoNganhDialog extends JDialog {

    private static final String TAT_CA = "— Tất cả ngành —";

    private static final String[] COLUMNS = {
            "CCCD", "Họ", "Tên", "TT NV", "Mã ngành", "Tên ngành", "PT", "Tổ hợp", "THM", "Điểm XT", "Kết quả"
    };

    private final NganhDAO nganhDAO = new NganhDAO();
    private final NguyenVongDAO nguyenVongDAO = new NguyenVongDAO();
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

    public void refreshTable() {
        tableModel.setRowCount(0);
        String maNganh = resolveMaNganhFromCombo();
        List<TrungTuyenRow> rows = nguyenVongDAO.listTrungTuyenByNganh(maNganh);
        if (rows == null) {
            return;
        }
        for (TrungTuyenRow r : rows) {
            tableModel.addRow(new Object[]{
                    r.getCccd(),
                    r.getHo(),
                    r.getTen(),
                    r.getThuTuNv(),
                    r.getMaNganh(),
                    r.getTenNganh(),
                    r.getPhuongThuc(),
                    r.getMaToHop(),
                    formatNum(r.getThm()),
                    formatNum(r.getDiemXetTuyen()),
                    r.getKetQua()
            });
        }
        UiTableColumns.refresh(table);
    }

    private String resolveMaNganhFromCombo() {
        Object sel = cbNganh.getSelectedItem();
        if (sel == null || TAT_CA.equals(sel.toString())) {
            return null;
        }
        String s = sel.toString().trim();
        int idx = s.indexOf(" - ");
        return idx > 0 ? s.substring(0, idx).trim() : s;
    }

    private static String formatNum(Double v) {
        if (v == null) {
            return "";
        }
        if (v == v.longValue()) {
            return String.valueOf(v.longValue());
        }
        return String.format("%.2f", v);
    }
}
