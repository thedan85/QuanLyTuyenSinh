package com.example.ui;

import com.example.dao.DiemThiDAO;
import com.example.entity.DiemThi;
import com.example.entity.ThiSinh;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class ThiSinhDetailDialog extends JDialog {

    public ThiSinhDetailDialog(JFrame parent, ThiSinh ts) {
        super(parent, "Chi tiết hồ sơ thí sinh: " + ts.getHo() + " " + ts.getTen(), true);
        setSize(700, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- 1. THÔNG TIN CÁ NHÂN ---
        JPanel infoPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder("Thông tin cá nhân"), new EmptyBorder(10, 10, 10, 10)));
        
        infoPanel.add(new JLabel("CCCD: " + ts.getCccd()));
        infoPanel.add(new JLabel("Số báo danh: " + (ts.getSobaodanh() != null ? ts.getSobaodanh() : "N/A")));
        infoPanel.add(new JLabel("Họ và tên: " + ts.getHo() + " " + ts.getTen()));
        infoPanel.add(new JLabel("Ngày sinh: " + ts.getNgaySinh()));
        infoPanel.add(new JLabel("Giới tính: " + ts.getGioiTinh()));
        infoPanel.add(new JLabel("Số điện thoại: " + ts.getDienThoai()));
        infoPanel.add(new JLabel("Email: " + ts.getEmail()));
        infoPanel.add(new JLabel("Nơi sinh: " + ts.getNoiSinh()));
        infoPanel.add(new JLabel("Đối tượng ưu tiên: " + ts.getDoiTuong()));
        infoPanel.add(new JLabel("Khu vực: " + ts.getKhuVuc()));
        
        add(infoPanel, BorderLayout.NORTH);

        // --- 2. THÔNG TIN ĐIỂM SỐ CHI TIẾT ---
        JPanel scorePanel = new JPanel(new BorderLayout());
        scorePanel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder("Chi tiết Điểm thi"), new EmptyBorder(10, 10, 10, 10)));

        JTextArea txtScores = new JTextArea();
        txtScores.setEditable(false);
        txtScores.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        DiemThiDAO diemDao = new DiemThiDAO();
        List<DiemThi> dsDiem = diemDao.getDiemByCccd(ts.getCccd());

        StringBuilder sb = new StringBuilder();
        if (dsDiem == null || dsDiem.isEmpty()) {
            sb.append("Thí sinh này chưa có điểm thi trong hệ thống.\n");
        } else {
            for (DiemThi d : dsDiem) {
                sb.append("--------------------------------------------------\n");
                sb.append("1. Điểm thi THPT:\n");
                sb.append(String.format(" - Toán: %s | Lý: %s | Hóa: %s | Sinh: %s\n", d.getDiemToan(), d.getDiemLy(), d.getDiemHoa(), d.getDiemSinh()));
                sb.append(String.format(" - Văn: %s | Sử: %s | Địa: %s | GDCD: %s\n", d.getDiemVan(), d.getDiemSu(), d.getDiemDia(), d.getKtpl()));
                sb.append(String.format(" - Ngoại ngữ gốc (N1_Thi): %s | Ngoại ngữ quy đổi (N1_CC): %s\n", d.getN1Thi(), d.getN1Cc()));
                
                sb.append("\n2. Điểm thi ĐGNL:\n");
                sb.append(" - Điểm ĐGNL (NL1): ").append(d.getNl1() != null ? d.getNl1() : "Không có").append("\n");

                sb.append("\n3. Điểm VSAT (thang 150 — PT3):\n");
                sb.append(String.format(" - Toán: %s | Lý: %s | Hóa: %s | Sinh: %s\n",
                        fmt(d.getVsatTo()), fmt(d.getVsatLi()), fmt(d.getVsatHo()), fmt(d.getVsatSi())));
                sb.append(String.format(" - Sử: %s | Địa: %s | Văn: %s | Anh: %s\n",
                        fmt(d.getVsatSu()), fmt(d.getVsatDi()), fmt(d.getVsatVa()), fmt(d.getVsatN1())));

                sb.append("\n4. Năng khiếu (nếu có):\n");
                sb.append(" - NK1: ").append(d.getNk1() != null ? d.getNk1() : "Không có").append("\n");
                sb.append(" - NK2: ").append(d.getNk2() != null ? d.getNk2() : "Không có").append("\n");
            }
        }
        txtScores.setText(sb.toString());
        scorePanel.add(new JScrollPane(txtScores), BorderLayout.CENTER);

        add(scorePanel, BorderLayout.CENTER);

        // --- 3. NÚT ĐÓNG ---
        JPanel bottomPanel = new JPanel();
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        bottomPanel.add(btnClose);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private static String fmt(Double v) {
        return v != null ? String.valueOf(v) : "—";
    }
}