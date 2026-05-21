package com.example.report;

import com.example.dto.ThongKeTrungTuyenRow;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Xuất PDF biểu mẫu tuyển sinh (Times New Roman, header/footer chuẩn). */
public final class PdfTableExporter {

    private static final DateTimeFormatter NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String TEN_TRUONG = "TRƯỜNG ĐẠI HỌC SÀI GÒN";
    private static final String KY_TUYEN_SINH_PHU = "KỲ TUYỂN SINH ĐẠI HỌC NĂM 2026";

    private PdfTableExporter() {
    }

    /** PDF danh sách trúng tuyển — bảng đúng như trên dialog. */
    public static boolean exportDanhSachTrungTuyen(Component parent, DefaultTableModel model,
            String phamViNganh) {
        if (model == null || model.getRowCount() == 0) {
            showEmpty(parent);
            return false;
        }
        return savePdf(parent, "danh_sach_trung_tuyen.pdf", doc -> {
            PdfFonts f = PdfFonts.load();
            addLetterhead(doc, f, "DANH SÁCH TRÚNG TUYỂN", KY_TUYEN_SINH_PHU, phamViNganh);

            int cols = model.getColumnCount();
            PdfPTable table = new PdfPTable(cols);
            table.setWidthPercentage(100f);
            table.setSpacingBefore(8f);
            setWidthsAuto(table, cols);

            for (int c = 0; c < cols; c++) {
                table.addCell(headerCell(model.getColumnName(c), f));
            }
            for (int r = 0; r < model.getRowCount(); r++) {
                for (int c = 0; c < cols; c++) {
                    Object v = model.getValueAt(r, c);
                    boolean center = c >= 3;
                    table.addCell(dataCell(v != null ? v.toString() : "", f, center));
                }
            }
            doc.add(table);
            addFooter(doc, f);
        });
    }

    /** PDF thống kê trúng tuyển — bảng 2 tầng header như biểu mẫu mẫu. */
    public static boolean exportThongKeTrungTuyen(Component parent, List<ThongKeTrungTuyenRow> rows) {
        if (rows == null || rows.isEmpty()) {
            showEmpty(parent);
            return false;
        }
        return savePdf(parent, "thong_ke_trung_tuyen.pdf", doc -> {
            PdfFonts f = PdfFonts.load();
            addLetterhead(doc, f, "THỐNG KÊ TRÚNG TUYỂN", KY_TUYEN_SINH_PHU, null);
            doc.add(buildThongKeTable(rows, f));
            addFooter(doc, f);
        });
    }

    private static PdfPTable buildThongKeTable(List<ThongKeTrungTuyenRow> rows, PdfFonts f)
            throws DocumentException {
        // Cột 0 (Mã ngành) rộng hơn để mã không sát viền
        float[] widths = {15f, 22f, 8f, 8f, 8f, 10f, 8f, 8f, 9f};
        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100f);
        table.setWidths(widths);
        table.setSpacingBefore(8f);

        table.addCell(headerCellRowSpan("Mã\nngành", f, 2));
        table.addCell(headerCellRowSpan("Tên ngành", f, 2));
        table.addCell(headerCellColSpan("Chỉ tiêu tổng", f, 3));
        table.addCell(headerCellColSpan("Số lượng trúng tuyển", f, 4));

        table.addCell(headerCell("THPT", f));
        table.addCell(headerCell("ĐGNL", f));
        table.addCell(headerCell("VSAT", f));
        table.addCell(headerCell("PT1\n(THPT)", f));
        table.addCell(headerCell("PT2", f));
        table.addCell(headerCell("PT3", f));
        table.addCell(headerCell("Tổng", f));

        int sumThpt = 0;
        int sumDgnl = 0;
        int sumVsat = 0;
        int sumPt1 = 0;
        int sumPt2 = 0;
        int sumPt3 = 0;
        int sumTong = 0;

        for (ThongKeTrungTuyenRow r : rows) {
            table.addCell(dataCellMaNganh(s(r.getMaNganh()), f));
            table.addCell(dataCellTenNganh(s(r.getTenNganh()), f));
            table.addCell(dataCellNumber(r.getSlThptCt(), f));
            table.addCell(dataCellNumber(r.getSlDgnlCt(), f));
            table.addCell(dataCellNumber(r.getSlVsatCt(), f));
            table.addCell(dataCellNumber(r.getTrungPt1(), f));
            table.addCell(dataCellNumber(r.getTrungPt2(), f));
            table.addCell(dataCellNumber(r.getTrungPt3(), f));
            table.addCell(dataCellNumber(r.getTongTrung(), f));

            sumThpt += r.getSlThptCt();
            sumDgnl += r.getSlDgnlCt();
            sumVsat += r.getSlVsatCt();
            sumPt1 += r.getTrungPt1();
            sumPt2 += r.getTrungPt2();
            sumPt3 += r.getTrungPt3();
            sumTong += r.getTongTrung();
        }

        table.addCell(totalLabelCell("Tổng cộng", f));
        table.addCell(dataCellNumber(sumThpt, f, true));
        table.addCell(dataCellNumber(sumDgnl, f, true));
        table.addCell(dataCellNumber(sumVsat, f, true));
        table.addCell(dataCellNumber(sumPt1, f, true));
        table.addCell(dataCellNumber(sumPt2, f, true));
        table.addCell(dataCellNumber(sumPt3, f, true));
        table.addCell(dataCellNumber(sumTong, f, true));

        return table;
    }

    private static void addLetterhead(Document doc, PdfFonts f, String title, String kyTuyenSinh,
            String extraLine) throws DocumentException {
        PdfPTable head = new PdfPTable(2);
        head.setWidthPercentage(100f);
        head.setWidths(new float[]{55f, 45f});

        PdfPCell left = new PdfPCell();
        left.setBorder(PdfPCell.NO_BORDER);
        left.addElement(new Paragraph(TEN_TRUONG, f.bold(11)));
        left.addElement(new Paragraph("    PHÒNG ĐÀO TẠO", f.regular(10)));
        head.addCell(left);

        PdfPCell right = new PdfPCell();
        right.setBorder(PdfPCell.NO_BORDER);
        right.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Paragraph quocHieu = new Paragraph("CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM", f.bold(11));
        quocHieu.setAlignment(Element.ALIGN_RIGHT);
        right.addElement(quocHieu);
        Paragraph docLap = new Paragraph("Độc lập - Tự do - Hạnh phúc", f.italic(10));
        docLap.setAlignment(Element.ALIGN_RIGHT);
        right.addElement(docLap);
        Paragraph line = new Paragraph("--------------------------------", f.regular(10));
        line.setAlignment(Element.ALIGN_RIGHT);
        right.addElement(line);
        Paragraph ngayIn = new Paragraph("Ngày in: " + LocalDateTime.now().format(NGAY_GIO), f.regular(10));
        ngayIn.setAlignment(Element.ALIGN_RIGHT);
        right.addElement(ngayIn);
        head.addCell(right);

        doc.add(head);

        Paragraph gap = new Paragraph(" ", f.regular(6));
        gap.setSpacingAfter(4f);
        doc.add(gap);

        Paragraph pTitle = new Paragraph(title, f.bold(13));
        pTitle.setAlignment(Element.ALIGN_CENTER);
        pTitle.setSpacingAfter(2f);
        doc.add(pTitle);

        Paragraph pKy = new Paragraph(kyTuyenSinh, f.bold(11));
        pKy.setAlignment(Element.ALIGN_CENTER);
        pKy.setSpacingAfter(6f);
        doc.add(pKy);

        if (extraLine != null && !extraLine.isEmpty()) {
            Paragraph pExtra = new Paragraph(extraLine, f.italic(10));
            pExtra.setAlignment(Element.ALIGN_CENTER);
            pExtra.setSpacingAfter(4f);
            doc.add(pExtra);
        }
    }

    private static void addFooter(Document doc, PdfFonts f) throws DocumentException {
        Paragraph space = new Paragraph(" ", f.regular(8));
        space.setSpacingBefore(16f);
        doc.add(space);

        LocalDateTime now = LocalDateTime.now();
        String ngayChu = String.format("..., ngày %d tháng %d năm %d",
                now.getDayOfMonth(), now.getMonthValue(), now.getYear());
        Paragraph pNgay = new Paragraph(ngayChu, f.italic(10));
        pNgay.setAlignment(Element.ALIGN_RIGHT);
        pNgay.setSpacingAfter(24f);
        doc.add(pNgay);

        Paragraph pRole = new Paragraph("Trưởng phòng Đào tạo", f.bold(11));
        pRole.setAlignment(Element.ALIGN_RIGHT);
        pRole.setSpacingAfter(4f);
        doc.add(pRole);

        Paragraph pSign = new Paragraph("(Ký và đóng dấu)", f.italic(10));
        pSign.setAlignment(Element.ALIGN_RIGHT);
        doc.add(pSign);
    }

    private static PdfPCell headerCell(String text, PdfFonts f) {
        PdfPCell cell = new PdfPCell(new Phrase(text, f.bold(9)));
        styleHeader(cell);
        return cell;
    }

    private static PdfPCell headerCellRowSpan(String text, PdfFonts f, int rowSpan) {
        PdfPCell cell = new PdfPCell(new Phrase(text, f.bold(9)));
        cell.setRowspan(rowSpan);
        styleHeader(cell);
        return cell;
    }

    private static PdfPCell headerCellColSpan(String text, PdfFonts f, int colSpan) {
        PdfPCell cell = new PdfPCell(new Phrase(text, f.bold(9)));
        cell.setColspan(colSpan);
        styleHeader(cell);
        return cell;
    }

    private static void styleHeader(PdfPCell cell) {
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(new Color(242, 242, 242));
        cell.setPaddingTop(6f);
        cell.setPaddingBottom(6f);
        cell.setPaddingLeft(5f);
        cell.setPaddingRight(5f);
    }

    /** Mã ngành: căn giữa, padding ngang rộng hơn. */
    private static PdfPCell dataCellMaNganh(String text, PdfFonts f) {
        PdfPCell cell = new PdfPCell(new Phrase(text, f.regular(9)));
        cell.setPaddingTop(5f);
        cell.setPaddingBottom(5f);
        cell.setPaddingLeft(8f);
        cell.setPaddingRight(8f);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    private static PdfPCell dataCellTenNganh(String text, PdfFonts f) {
        PdfPCell cell = new PdfPCell(new Phrase(text, f.regular(9)));
        cell.setPadding(5f);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }

    private static PdfPCell dataCellNumber(int value, PdfFonts f) {
        return dataCellNumber(value, f, false);
    }

    private static PdfPCell dataCellNumber(int value, PdfFonts f, boolean bold) {
        Font font = bold ? f.bold(9) : f.regular(9);
        PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(value), font));
        cell.setPadding(5f);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        if (bold) {
            cell.setBackgroundColor(new Color(248, 248, 248));
        }
        return cell;
    }

    private static PdfPCell totalLabelCell(String text, PdfFonts f) {
        PdfPCell cell = new PdfPCell(new Phrase(text, f.bold(9)));
        cell.setColspan(2);
        styleHeader(cell);
        return cell;
    }

    private static PdfPCell dataCell(String text, PdfFonts f, boolean center) {
        PdfPCell cell = new PdfPCell(new Phrase(text, f.regular(9)));
        cell.setPadding(5f);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(center ? Element.ALIGN_CENTER : Element.ALIGN_LEFT);
        return cell;
    }

    private static void setWidthsAuto(PdfPTable table, int cols) throws DocumentException {
        float[] w = new float[cols];
        for (int i = 0; i < cols; i++) {
            w[i] = 100f / cols;
        }
        table.setWidths(w);
    }

    private interface PdfWriteTask {
        void write(Document doc) throws DocumentException, IOException;
    }

    private static boolean savePdf(Component parent, String defaultName, PdfWriteTask task) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu file PDF");
        chooser.setSelectedFile(new File(defaultName));
        chooser.setFileFilter(new FileNameExtensionFilter("PDF (*.pdf)", "pdf"));
        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new File(file.getAbsolutePath() + ".pdf");
        }
        try {
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            task.write(document);
            document.close();
            JOptionPane.showMessageDialog(parent, "Đã lưu PDF:\n" + file.getAbsolutePath(),
                    "Xuất PDF", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Không xuất được PDF:\n" + ex.getMessage(),
                    "Xuất PDF", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static void showEmpty(Component parent) {
        JOptionPane.showMessageDialog(parent, "Bảng đang trống. Hãy làm mới dữ liệu trước khi xuất.",
                "Xuất PDF", JOptionPane.WARNING_MESSAGE);
    }

    private static String s(String v) {
        return v != null ? v : "";
    }

    /** Times New Roman (ưu tiên) + fallback Unicode. */
    private static final class PdfFonts {
        final BaseFont regular;
        final BaseFont bold;
        final BaseFont italic;

        private PdfFonts(BaseFont regular, BaseFont bold, BaseFont italic) {
            this.regular = regular;
            this.bold = bold;
            this.italic = italic;
        }

        static PdfFonts load() throws DocumentException, IOException {
            BaseFont reg = loadFace("times.ttf");
            if (reg == null) {
                reg = loadFace("Times New Roman.ttf");
            }
            if (reg == null) {
                throw new IOException(
                        "Không tìm thấy font Times New Roman (times.ttf). Cài font trên Windows/Linux.");
            }
            BaseFont b = loadFace("timesbd.ttf");
            BaseFont it = loadFace("timesi.ttf");
            if (b == null) {
                b = reg;
            }
            if (it == null) {
                it = reg;
            }
            return new PdfFonts(reg, b, it);
        }

        private static BaseFont loadFace(String fileName) throws DocumentException, IOException {
            String path = resolveFontPath(fileName);
            if (path != null) {
                return BaseFont.createFont(path, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            }
            return null;
        }

        Font regular(float size) {
            return new Font(regular, size, Font.NORMAL);
        }

        Font bold(float size) {
            return new Font(bold, size, Font.BOLD);
        }

        Font italic(float size) {
            return new Font(italic, size, Font.ITALIC);
        }
    }

    private static String resolveFontPath(String fileName) {
        String[] dirs = {
                "C:\\Windows\\Fonts\\",
                "/usr/share/fonts/truetype/msttcorefonts/",
                "/usr/share/fonts/truetype/liberation/",
                "/System/Library/Fonts/Supplemental/"
        };
        for (String dir : dirs) {
            File f = new File(dir + fileName);
            if (f.isFile()) {
                return f.getAbsolutePath();
            }
        }
        if ("times.ttf".equals(fileName)) {
            for (String dir : dirs) {
                File f = new File(dir + "Times New Roman.ttf");
                if (f.isFile()) {
                    return f.getAbsolutePath();
                }
            }
        }
        return null;
    }
}
