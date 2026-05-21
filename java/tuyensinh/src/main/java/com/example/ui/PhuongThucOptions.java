package com.example.ui;

import javax.swing.JComboBox;

/**
 * Phương thức xét tuyển trên nguyện vọng / điểm cộng: PT1, PT2, PT3.
 */
public final class PhuongThucOptions {

    public static final String PT1 = "PT1";
    public static final String PT2 = "PT2";
    public static final String PT3 = "PT3";

    private static final String[] CODES = { PT1, PT2, PT3 };
    private static final String[] LABELS = {
            "PT1 - THPT (điểm THPT)",
            "PT2 - ĐGNL (NL1)",
            "PT3 - V-SAT (VSAT_*)"
    };

    private PhuongThucOptions() {
    }

    public static JComboBox<String> newCombo() {
        JComboBox<String> cb = new JComboBox<>(LABELS);
        cb.setEditable(false);
        return cb;
    }

    /** Lấy mã PT1/PT2/PT3 từ combo (nhãn hiển thị hoặc mã). */
    public static String getCode(JComboBox<String> combo) {
        Object sel = combo == null ? null : combo.getSelectedItem();
        if (sel == null) {
            return "";
        }
        return toCode(sel.toString());
    }

    /** Chọn item trong combo theo giá trị lưu DB (PT1, PT2, PT3 hoặc DGNL/VSAT). */
    public static void select(JComboBox<String> combo, String stored) {
        if (combo == null) {
            return;
        }
        resetItems(combo);
        String code = toCode(stored);
        for (int i = 0; i < CODES.length; i++) {
            if (CODES[i].equals(code)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        if (stored != null && !stored.trim().isEmpty()) {
            combo.addItem(stored.trim() + " (không chuẩn)");
            combo.setSelectedIndex(combo.getItemCount() - 1);
        } else {
            combo.setSelectedIndex(0);
        }
    }

    /** Chuẩn hóa nhập tay, CSV, DB → PT1 / PT2 / PT3. */
    public static String toCode(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return "";
        }
        String s = raw.trim().toUpperCase();
        if (s.startsWith("PT1") || s.contains("THPT")) {
            return PT1;
        }
        if ("PT2".equals(s) || "DGNL".equals(s) || s.contains("DGNL") || s.startsWith("PT2")) {
            return PT2;
        }
        if ("PT3".equals(s) || "VSAT".equals(s) || s.contains("VSAT") || s.startsWith("PT3")) {
            return PT3;
        }
        if (PT1.equals(s) || PT2.equals(s) || PT3.equals(s)) {
            return s;
        }
        return raw.trim();
    }

    private static void resetItems(JComboBox<String> combo) {
        combo.removeAllItems();
        for (String label : LABELS) {
            combo.addItem(label);
        }
    }
}
