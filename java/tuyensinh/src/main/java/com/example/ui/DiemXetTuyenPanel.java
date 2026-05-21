package com.example.ui;

import com.example.dao.NguyenVongDAO;
import com.example.dto.DiemXetTuyenRow;
import com.example.service.XetTuyenService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel xem điểm xét tuyển theo nguyện vọng (style đồng bộ ThiSinhPanel).
 */
public class DiemXetTuyenPanel extends JPanel implements RefreshablePanel {
    private final NguyenVongDAO dao = new NguyenVongDAO();

    private JTable table;
    private JScrollPane tableScroll;
    private DefaultTableModel tableModel;

    private JTextField txtSearch;
    private JButton btnSearch, btnTinhLai, btnRefresh;
    private JButton btnPrev, btnNext;
    private JLabel lblPageInfo;

    private int currentPage = 1;
    private final int pageSize = 20;
    private int totalPages = 1;
    private String currentKeyword = "";

    private static final int COL_KET_QUA = 14;

    private static final String[] COLUMNS = {
            "CCCD", "Họ", "Tên", "TT NV", "Mã ngành", "PT", "Tổ hợp",
            "Điểm chưa quy đổi", "THM", "THM cao nhất", "Tổ hợp THM cao nhất",
            "Cộng", "Ưu tiên", "Điểm XT", "Kết quả"
    };

    public DiemXetTuyenPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 8));
        txtSearch = new JTextField(18);
        txtSearch.setFont(txtSearch.getFont().deriveFont(Font.PLAIN, 18f));
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập CCCD...");
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(11, 15, 11, 15)));
        btnSearch = new JButton("Tìm kiếm");
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        UiButtons.stylePrimary(btnSearch);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnTinhLai = new JButton("Tính lại điểm");
        btnRefresh = new JButton("Làm mới");
        actionPanel.add(btnTinhLai);
        actionPanel.add(btnRefresh);
        UiButtons.stylePrimary(btnTinhLai);
        UiButtons.styleSecondary(btnRefresh);
        UiButtons.equalizeButtonsInContainer(actionPanel);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.EAST);
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
                if (!isSelected && column == COL_KET_QUA && value != null) {
                    String s = value.toString();
                    if (s.contains("TRÚNG TUYỂN")) {
                        c.setForeground(new Color(0, 150, 0));
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else if (s.contains("Rớt")) {
                        c.setForeground(Color.RED);
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });
        tableScroll = new JScrollPane(table);
        UiTableColumns.install(table, tableScroll);
        add(tableScroll, BorderLayout.CENTER);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPrev = new JButton("<< Trước");
        btnNext = new JButton("Sau >>");
        lblPageInfo = new JLabel("Trang 1 / 1");
        paginationPanel.add(btnPrev);
        paginationPanel.add(lblPageInfo);
        paginationPanel.add(btnNext);
        UiButtons.styleSecondary(btnPrev);
        UiButtons.styleSecondary(btnNext);
        UiButtons.equalizeButtonSizes(btnPrev, btnNext);
        add(paginationPanel, BorderLayout.SOUTH);

        setupEvents();
        loadData();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadData();
                UiTableColumns.refresh(table);
            }
        });
    }

    private void setupEvents() {
        btnSearch.addActionListener(e -> {
            currentKeyword = txtSearch.getText().trim();
            currentPage = 1;
            loadData();
        });

        btnRefresh.addActionListener(e -> loadData());

        btnPrev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadData();
            }
        });

        btnNext.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadData();
            }
        });

        btnTinhLai.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Tối ưu PT + tổ hợp (theo ngành), rồi tính THM, điểm cộng, ưu tiên và điểm xét tuyển\n"
                            + "cho TẤT CẢ nguyện vọng. Không đổi kết quả TRÚNG/Rớt.\nTiếp tục?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                new XetTuyenService().tinhDiemChoTatCa();
                loadData();
                JOptionPane.showMessageDialog(this, "Đã tính lại điểm xong.", "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            } finally {
                setCursor(Cursor.getDefaultCursor());
            }
        });
    }

    private void loadData() {
        List<DiemXetTuyenRow> all = dao.buildDiemXetTuyenRows(currentKeyword);
        int total = all.size();
        totalPages = (int) Math.ceil((double) total / pageSize);
        if (totalPages < 1) {
            totalPages = 1;
        }
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        lblPageInfo.setText("Trang " + currentPage + " / " + totalPages);
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);

        int from = (currentPage - 1) * pageSize;
        int to = Math.min(from + pageSize, total);

        tableModel.setRowCount(0);
        for (int i = from; i < to; i++) {
            DiemXetTuyenRow r = all.get(i);
                tableModel.addRow(new Object[] {
                        r.getCccd(),
                        r.getHo(),
                        r.getTen(),
                        r.getThuTuNv(),
                        r.getMaNganh(),
                        r.getPhuongThuc(),
                        r.getMaToHop(),
                        r.getDiemChuaQuyDoi(),
                        fmt(r.getThm()),
                        fmt(r.getThmCaoNhat()),
                        r.getTenToHopThmCaoNhat(),
                        fmt(r.getDiemCong()),
                        fmt(r.getDiemUtqd()),
                        fmt(r.getDiemXetTuyen()),
                        r.getKetQua()
                });
        }
        UiTableColumns.refresh(table);
    }

    @Override
    public void refreshData() {
        loadData();
    }

    private static String fmt(Double v) {
        if (v == null) {
            return "";
        }
        return String.format("%.5f", v).replaceAll("0+$", "").replaceAll("\\.$", "");
    }
}
