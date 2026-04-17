package com.example.entity;

import javax.persistence.*;

@Entity
@Table(name = "xt_diemthixettuyen")
public class DiemThi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int iddiemthi;

    @Column(name = "cccd", nullable = false, unique = true)
    private String cccd;

    @Column(name = "sobaodanh")
    private String sobaodanh;

    @Column(name = "d_phuongthuc")
    private String dPhuongthuc;

    @Column(name = "`TO`") private Double diemToan;
    @Column(name = "LI") private Double diemLy;
    @Column(name = "HO") private Double diemHoa;
    @Column(name = "SI") private Double diemSinh;
    @Column(name = "SU") private Double diemSu;
    @Column(name = "DI") private Double diemDia;
    @Column(name = "VA") private Double diemVan;
    
    @Column(name = "N1_THI") private Double n1Thi; // Điểm Anh văn thi thực tế
    @Column(name = "N1_CC") private Double n1Cc;   // Điểm Anh văn quy đổi từ chứng chỉ
    
    @Column(name = "CNCN") private Double cncn;
    @Column(name = "CNNN") private Double cnnn;
    @Column(name = "TI") private Double diemTin;
    @Column(name = "KTPL") private Double ktpl;
    
    @Column(name = "NL1") private Double nl1; // Điểm Đánh giá năng lực
    
    @Column(name = "NK1") private Double nk1; // Năng khiếu 1
    @Column(name = "NK2") private Double nk2; // Năng khiếu 2

    public int getIddiemthi() { return iddiemthi; }
    public void setIddiemthi(int iddiemthi) { this.iddiemthi = iddiemthi; }
    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }
    public String getSobaodanh() { return sobaodanh; }
    public void setSobaodanh(String sobaodanh) { this.sobaodanh = sobaodanh; }
    public String getdPhuongthuc() { return dPhuongthuc; }
    public void setdPhuongthuc(String dPhuongthuc) { this.dPhuongthuc = dPhuongthuc; }
    
    public Double getDiemToan() { return diemToan; }
    public void setDiemToan(Double diemToan) { this.diemToan = diemToan; }
    public Double getDiemLy() { return diemLy; }
    public void setDiemLy(Double diemLy) { this.diemLy = diemLy; }
    public Double getDiemHoa() { return diemHoa; }
    public void setDiemHoa(Double diemHoa) { this.diemHoa = diemHoa; }
    public Double getDiemSinh() { return diemSinh; }
    public void setDiemSinh(Double diemSinh) { this.diemSinh = diemSinh; }
    public Double getDiemSu() { return diemSu; }
    public void setDiemSu(Double diemSu) { this.diemSu = diemSu; }
    public Double getDiemDia() { return diemDia; }
    public void setDiemDia(Double diemDia) { this.diemDia = diemDia; }
    public Double getDiemVan() { return diemVan; }
    public void setDiemVan(Double diemVan) { this.diemVan = diemVan; }
    
    public Double getN1Thi() { return n1Thi; }
    public void setN1Thi(Double n1Thi) { this.n1Thi = n1Thi; }
    public Double getN1Cc() { return n1Cc; }
    public void setN1Cc(Double n1Cc) { this.n1Cc = n1Cc; }
    
    public Double getCncn() { return cncn; }
    public void setCncn(Double cncn) { this.cncn = cncn; }
    public Double getCnnn() { return cnnn; }
    public void setCnnn(Double cnnn) { this.cnnn = cnnn; }
    public Double getDiemTin() { return diemTin; }
    public void setDiemTin(Double diemTin) { this.diemTin = diemTin; }
    public Double getKtpl() { return ktpl; }
    public void setKtpl(Double ktpl) { this.ktpl = ktpl; }
    
    public Double getNl1() { return nl1; }
    public void setNl1(Double nl1) { this.nl1 = nl1; }
    
    public Double getNk1() { return nk1; }
    public void setNk1(Double nk1) { this.nk1 = nk1; }
    public Double getNk2() { return nk2; }
    public void setNk2(Double nk2) { this.nk2 = nk2; }
}