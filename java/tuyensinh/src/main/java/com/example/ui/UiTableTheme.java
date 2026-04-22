package com.example.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/** Bảng: kẻ ngang, zebra, hover dòng, padding ô; header đồng bộ. */
public final class UiTableTheme {

    private static final Color GRID = new Color(237, 242, 247);
    private static final Color HEADER_BG = new Color(248, 250, 252);
    private static final float ADMIN_TABLE_FONT = 13f;
    private static final float ADMIN_HEADER_FONT = 13f;

    /** Dòng chẵn (0-based: 0,2,4…). */
    private static final Color STRIPE_EVEN = Color.WHITE;
    /** Dòng lẻ. */
    private static final Color STRIPE_ODD = new Color(247, 250, 252);
    /** Hover — nhạt theo tông accent. */
    private static final Color ROW_HOVER = new Color(224, 242, 240);

    public static final String HOVER_ROW_KEY = "UiTableTheme.hoverRow";
    /** Tùy chọn theo bảng: hệ số nhân padding ô dữ liệu (1 = mặc định). */
    public static final String CELL_PADDING_SCALE_KEY = "UiTableTheme.cellPaddingScale";
    private static final String HOVER_ATTACHED = "UiTableTheme.hoverAttached";

    /** Padding nội dung ô (trên, trái, dưới, phải). */
    public static final int CELL_PAD_TOP = 10;
    public static final int CELL_PAD_LEFT = 14;
    public static final int CELL_PAD_BOTTOM = 10;
    public static final int CELL_PAD_RIGHT = 14;

    /** Padding tiêu đề cột (header). */
    private static final int HEADER_PAD_TOP = 12;
    private static final int HEADER_PAD_LEFT = 16;
    private static final int HEADER_PAD_BOTTOM = 12;
    private static final int HEADER_PAD_RIGHT = 16;

    /** Chữ ô dữ liệu bình thường — luôn đặt lại để renderer dùng chung không sót màu chữ ô đã chọn. */
    private static final Color DATA_FG = new Color(30, 41, 59);

    private UiTableTheme() {
    }

    public static void apply(JTable table) {
        table.setShowGrid(true);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setGridColor(GRID);
        int scale = getCellPaddingScale(table);
        int minRow = Math.max(36, 16 + (CELL_PAD_TOP + CELL_PAD_BOTTOM) * scale);
        table.setRowHeight(Math.max(table.getRowHeight(), minRow));
        table.setFillsViewportHeight(true);

        table.setFont(table.getFont().deriveFont(ADMIN_TABLE_FONT));
        table.setForeground(DATA_FG);
        table.setSelectionForeground(DATA_FG);
        table.setSelectionBackground(new Color(167, 243, 208));

        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setBackground(HEADER_BG);
            header.setOpaque(true);
            header.setFont(header.getFont().deriveFont(Font.BOLD, ADMIN_HEADER_FONT));
            installPaddedHeaderRenderer(header);
        }

        installDefaultStripedRenderers(table);
        centerIdColumns(table);
        attachRowHover(table);
    }

    /**
     * Áp dụng padding, nền zebra / hover / chọn — gọi từ renderer tùy chỉnh sau {@code super} (và sau khi chỉnh màu chữ nếu cần).
     */
    public static void applyDataRowAppearance(JTable table, Component c, int row, boolean isSelected) {
        if (!(c instanceof JComponent jc)) {
            return;
        }
        int scale = getCellPaddingScale(table);
        jc.setBorder(new EmptyBorder(
                CELL_PAD_TOP * scale,
                CELL_PAD_LEFT * scale,
                CELL_PAD_BOTTOM * scale,
                CELL_PAD_RIGHT * scale));
        jc.setOpaque(true);
        if (isSelected) {
            Color bg = table.getSelectionBackground();
            Color fg = table.getSelectionForeground();
            jc.setBackground(bg != null ? bg : UIManager.getColor("Table.selectionBackground"));
            if (c instanceof JLabel jl) {
                jl.setForeground(fg != null ? fg : UIManager.getColor("Table.selectionForeground"));
            } else {
                jc.setForeground(fg != null ? fg : UIManager.getColor("Table.selectionForeground"));
            }
            return;
        }
        int hoverRow = readHoverRow(table);
        if (row >= 0 && row == hoverRow) {
            jc.setBackground(ROW_HOVER);
        } else if ((row & 1) == 0) {
            jc.setBackground(STRIPE_EVEN);
        } else {
            jc.setBackground(STRIPE_ODD);
        }
        Color normalFg = table.getForeground();
        if (normalFg == null) {
            normalFg = UIManager.getColor("Table.foreground");
        }
        if (normalFg == null) {
            normalFg = DATA_FG;
        }
        jc.setForeground(normalFg);
    }

    private static int getCellPaddingScale(JTable table) {
        Object v = table.getClientProperty(CELL_PADDING_SCALE_KEY);
        if (v instanceof Number n) {
            return Math.max(1, n.intValue());
        }
        return 1;
    }

    private static void installPaddedHeaderRenderer(JTableHeader header) {
        TableCellRenderer base = unwrapPaddedHeader(header.getDefaultRenderer());
        header.setDefaultRenderer(new PaddedHeaderRenderer(base));
    }

    private static TableCellRenderer unwrapPaddedHeader(TableCellRenderer r) {
        return r instanceof PaddedHeaderRenderer p ? p.delegate : r;
    }

    private static final class PaddedHeaderRenderer implements TableCellRenderer {
        /** Không {@code private} để lớp ngoài unwrap khi gọi {@link #apply} lặp. */
        final TableCellRenderer delegate;

        PaddedHeaderRenderer(TableCellRenderer delegate) {
            this.delegate = delegate;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component c = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (c instanceof JComponent jc) {
                jc.setBorder(new EmptyBorder(HEADER_PAD_TOP, HEADER_PAD_LEFT, HEADER_PAD_BOTTOM, HEADER_PAD_RIGHT));
            }
            return c;
        }
    }

    private static int readHoverRow(JTable table) {
        Object hr = table.getClientProperty(HOVER_ROW_KEY);
        return hr instanceof Integer ? (Integer) hr : -1;
    }

    private static void installDefaultStripedRenderers(JTable table) {
        TableCellRenderer r = new StripedHoverRenderer();
        table.setDefaultRenderer(Object.class, r);
        table.setDefaultRenderer(String.class, r);
        table.setDefaultRenderer(Number.class, r);
        table.setDefaultRenderer(Integer.class, r);
        table.setDefaultRenderer(Long.class, r);
        table.setDefaultRenderer(Short.class, r);
        table.setDefaultRenderer(Byte.class, r);
        table.setDefaultRenderer(Double.class, r);
        table.setDefaultRenderer(Float.class, r);
        table.setDefaultRenderer(Boolean.class, r);
    }

    private static void attachRowHover(JTable table) {
        if (Boolean.TRUE.equals(table.getClientProperty(HOVER_ATTACHED))) {
            return;
        }
        table.putClientProperty(HOVER_ATTACHED, Boolean.TRUE);
        table.putClientProperty(HOVER_ROW_KEY, -1);

        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int r = table.rowAtPoint(e.getPoint());
                int old = readHoverRow(table);
                if (r != old) {
                    table.putClientProperty(HOVER_ROW_KEY, r);
                    table.repaint();
                }
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                table.putClientProperty(HOVER_ROW_KEY, -1);
                table.repaint();
            }
        });
    }

    /** Căn giữa mọi cột có tiêu đề "ID" (không phân biệt hoa thường). */
    public static void centerIdColumns(JTable table) {
        if (table == null) {
            return;
        }
        for (int i = 0; i < table.getColumnCount(); i++) {
            String name = table.getColumnName(i);
            if (name == null || !name.trim().equalsIgnoreCase("ID")) {
                continue;
            }
            final int col = i;
            table.getColumnModel().getColumn(col).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                        boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                    if (c instanceof JLabel jl) {
                        jl.setHorizontalAlignment(SwingConstants.CENTER);
                    }
                    applyDataRowAppearance(t, c, row, isSelected);
                    return c;
                }
            });
        }
    }

    private static final class StripedHoverRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            applyDataRowAppearance(table, c, row, isSelected);
            return c;
        }
    }
}
