package com.example.ui;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Objects;

/**
 * Chiều ngang bảng: đo nhu cầu từng cột (header + ô mẫu), nếu tổng vừa viewport thì mỗi cột nhận
 * {@code need[i] + phần thừa chia đều} (tương đương chia đều khi mọi cột “nhỏ”, cột cần rộng hơn
 * trung bình được tăng, các cột khác nhận phần còn lại); nếu tổng vượt viewport thì giữ {@code need[i]}
 * và để {@link JScrollPane} hiển thị scroll ngang.
 */
public final class UiTableColumns {

    private static final String KEY_SCROLL = "UiTableColumns.scroll";
    /** Đặt {@code Boolean.TRUE} trên JTable để tắt tooltip ô. */
    public static final String KEY_NO_CELL_TOOLTIPS = "UiTableColumns.noCellToolTips";
    private static final String KEY_CELL_PAD_SCALE = "UiTableTheme.cellPaddingScale";
    private static final int SAMPLE_ROWS = 50;
    /** Cộng vào độ rộng đo (tương ứng padding trái+phải ô trong {@link UiTableTheme}). */
    private static final int CELL_PAD = 28;
    private static final int MIN_COL = 44;
    /** Trần đo mỗi cột — tránh một ô quá dài chiếm hết layout; khi vượt vẫn có scroll ngang. */
    private static final int MAX_MEASURE = 1200;

    private UiTableColumns() {
    }

    /**
     * {@link JTable#AUTO_RESIZE_OFF}, tooltip ô, đo lại cột khi viewport đổi kích thước; scroll ngang khi cần.
     */
    public static void install(JTable table, JScrollPane scroll) {
        Objects.requireNonNull(table, "table");
        Objects.requireNonNull(scroll, "scroll");
        table.putClientProperty(KEY_SCROLL, scroll);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setReorderingAllowed(true);
        }
        attachCellToolTips(table);
        scroll.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                refresh(table);
            }
        });
        SwingUtilities.invokeLater(() -> refresh(table));
    }

    /**
     * Gọi sau khi nạp dữ liệu (ví dụ {@code setRowCount(0)} rồi addRow) nếu không có sự kiện model.
     */
    public static void refresh(JTable table) {
        JScrollPane scroll = (JScrollPane) table.getClientProperty(KEY_SCROLL);
        if (scroll == null) {
            return;
        }
        int vw = scroll.getViewport().getWidth();
        if (vw <= 0) {
            return;
        }
        int budget = Math.max(0, vw - verticalScrollAllowance(scroll, table) - 2);

        TableColumnModel cm = table.getColumnModel();
        int n = cm.getColumnCount();
        if (n <= 0) {
            return;
        }

        int[] need = new int[n];
        int sumNeed = 0;
        for (int i = 0; i < n; i++) {
            need[i] = measurePreferredColumnWidth(table, i, SAMPLE_ROWS);
            sumNeed += need[i];
        }

        int[] w = new int[n];
        if (sumNeed <= budget) {
            int extra = budget - sumNeed;
            int base = extra / n;
            int rem = extra % n;
            for (int i = 0; i < n; i++) {
                w[i] = need[i] + base + (i < rem ? 1 : 0);
                w[i] = Math.max(MIN_COL, w[i]);
            }
        } else {
            for (int i = 0; i < n; i++) {
                w[i] = Math.max(MIN_COL, need[i]);
            }
        }

        for (int i = 0; i < n; i++) {
            cm.getColumn(i).setPreferredWidth(w[i]);
        }
        table.revalidate();
    }

    /**
     * Trừ chỗ cho thanh cuộn dọc khi nội dung bảng cao hơn viewport (tránh lệch chia ngang).
     */
    private static int verticalScrollAllowance(JScrollPane scroll, JTable table) {
        JScrollBar vsb = scroll.getVerticalScrollBar();
        if (vsb.isVisible()) {
            return vsb.getWidth();
        }
        int vh = scroll.getViewport().getHeight();
        if (vh <= 0) {
            return 0;
        }
        int rowH = table.getRowHeight();
        if (rowH <= 0) {
            rowH = 16;
        }
        int headerH = table.getTableHeader() != null ? table.getTableHeader().getHeight() : 0;
        int contentH = headerH + table.getRowCount() * rowH;
        if (contentH > vh) {
            return Math.max(vsb.getPreferredSize().width, 12);
        }
        return 0;
    }

    private static int measurePreferredColumnWidth(JTable table, int column, int maxRows) {
        int padScale = getCellPadScale(table);
        int pad = CELL_PAD * padScale;
        JTableHeader header = table.getTableHeader();
        TableColumn col = table.getColumnModel().getColumn(column);
        Object h = col.getHeaderValue();
        TableCellRenderer hr = col.getHeaderRenderer();
        if (hr == null && header != null) {
            hr = header.getDefaultRenderer();
        }
        int w = MIN_COL;
        if (hr != null) {
            Component hc = hr.getTableCellRendererComponent(table, h, false, false, -1, column);
            w = Math.max(w, hc.getPreferredSize().width + pad);
        }
        int rows = Math.min(maxRows, table.getRowCount());
        for (int r = 0; r < rows; r++) {
            Object val = table.getValueAt(r, column);
            TableCellRenderer cellR = table.getCellRenderer(r, column);
            Component c = cellR.getTableCellRendererComponent(table, val, false, false, r, column);
            w = Math.max(w, c.getPreferredSize().width + pad);
        }
        return Math.min(MAX_MEASURE, Math.max(MIN_COL, w));
    }

    private static int getCellPadScale(JTable table) {
        Object s = table.getClientProperty(KEY_CELL_PAD_SCALE);
        if (s instanceof Number n) {
            return Math.max(1, n.intValue());
        }
        return 1;
    }

    private static void attachCellToolTips(JTable table) {
        if (Boolean.TRUE.equals(table.getClientProperty(KEY_NO_CELL_TOOLTIPS))) {
            return;
        }
        ToolTipManager.sharedInstance().registerComponent(table);
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                int row = table.rowAtPoint(p);
                int col = table.columnAtPoint(p);
                if (row < 0 || col < 0) {
                    table.setToolTipText(null);
                    return;
                }
                Object v = table.getValueAt(row, col);
                String s = v == null ? "" : v.toString().trim();
                if (s.isEmpty()) {
                    table.setToolTipText(null);
                    return;
                }
                if (!isCellTextTruncated(table, row, col, s)) {
                    table.setToolTipText(null);
                    return;
                }
                table.setToolTipText(s.length() > 80
                        ? "<html><body style='max-width:360px'>" + escapeHtml(s) + "</body></html>"
                        : s);
            }
        });
    }

    /** Chỉ hiện tooltip khi chữ không đủ chỗ trong ô (tránh popup trùng nội dung ô). */
    private static boolean isCellTextTruncated(JTable table, int row, int col, String text) {
        Rectangle rect = table.getCellRect(row, col, false);
        if (rect.width <= 0) {
            return false;
        }
        Component comp = table.prepareRenderer(table.getCellRenderer(row, col), row, col);
        int pad = 8;
        if (comp instanceof JComponent jc) {
            Insets in = jc.getInsets();
            pad = in.left + in.right + 4;
        }
        int avail = rect.width - pad;
        if (avail <= 0) {
            return true;
        }
        FontMetrics fm = comp.getFontMetrics(comp.getFont());
        return fm.stringWidth(text) > avail;
    }

    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br/>");
    }
}
