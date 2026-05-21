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

    @Column(name = "VSAT_TO") private Double vsatTo;
    @Column(name = "VSAT_LI") private Double vsatLi;
    @Column(name = "VSAT_HO") private Double vsatHo;
    @Column(name = "VSAT_SI") private Double vsatSi;
    @Column(name = "VSAT_SU") private Double vsatSu;
    @Column(name = "VSAT_DI") private Double vsatDi;
    @Column(name = "VSAT_VA") private Double vsatVa;
    @Column(name = "VSAT_N1") private Double vsatN1;
    
    @Column(name = "NL1") private Double nl1; // Điểm Đánh giá năng lực
    
    @Column(name = "NK1") private Double nk1; // Năng khiếu 1
    @Column(name = "NK2") private Double nk2; // Năng khiếu 2
    @Column(name = "NK3") private Double nk3; // Năng khiếu 3
    @Column(name = "NK4") private Double nk4; // Năng khiếu 4
    @Column(name = "NK5") private Double nk5; // Năng khiếu 5
    @Column(name = "NK6") private Double nk6; // Năng khiếu 6

    public int getIddiemthi() { return iddiemthi; }
    public void setIddiemthi(int iddiemthi) { this.iddiemthi = iddiemthi; }
    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }
    public String getSobaodanh() { return sobaodanh; }
    public void setSobaodanh(String sobaodanh) { this.sobaodanh = sobaodanh; }
    
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

    public Double getVsatTo() { return vsatTo; }
    public void setVsatTo(Double vsatTo) { this.vsatTo = vsatTo; }
    public Double getVsatLi() { return vsatLi; }
    public void setVsatLi(Double vsatLi) { this.vsatLi = vsatLi; }
    public Double getVsatHo() { return vsatHo; }
    public void setVsatHo(Double vsatHo) { this.vsatHo = vsatHo; }
    public Double getVsatSi() { return vsatSi; }
    public void setVsatSi(Double vsatSi) { this.vsatSi = vsatSi; }
    public Double getVsatSu() { return vsatSu; }
    public void setVsatSu(Double vsatSu) { this.vsatSu = vsatSu; }
    public Double getVsatDi() { return vsatDi; }
    public void setVsatDi(Double vsatDi) { this.vsatDi = vsatDi; }
    public Double getVsatVa() { return vsatVa; }
    public void setVsatVa(Double vsatVa) { this.vsatVa = vsatVa; }
    public Double getVsatN1() { return vsatN1; }
    public void setVsatN1(Double vsatN1) { this.vsatN1 = vsatN1; }
    
    public Double getNl1() { return nl1; }
    public void setNl1(Double nl1) { this.nl1 = nl1; }
    
    public Double getNk1() { return nk1; }
    public void setNk1(Double nk1) { this.nk1 = nk1; }
    public Double getNk2() { return nk2; }
    public void setNk2(Double nk2) { this.nk2 = nk2; }
    public Double getNk3() { return nk3; }
    public void setNk3(Double nk3) { this.nk3 = nk3; }
    public Double getNk4() { return nk4; }
    public void setNk4(Double nk4) { this.nk4 = nk4; }
    public Double getNk5() { return nk5; }
    public void setNk5(Double nk5) { this.nk5 = nk5; }
    public Double getNk6() { return nk6; }
    public void setNk6(Double nk6) { this.nk6 = nk6; }
}