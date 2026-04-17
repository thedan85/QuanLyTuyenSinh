package com.example.entity;

import javax.persistence.*;

@Entity
@Table(name = "xt_diemcongxetuyen")
public class DiemCong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int iddiemcong;

    @Column(name = "ts_cccd")
    private String tsCccd;

    @Column(name = "manganh")
    private String manganh;

    @Column(name = "matohop")
    private String matohop;

    @Column(name = "phuongthuc")
    private String phuongthuc;

    @Column(name = "diemCC")
    private Double diemCC;

    @Column(name = "diemUtxt")
    private Double diemUtxt;

    @Column(name = "diemTong")
    private Double diemTong;

    @Column(name = "ghichu")
    private String ghichu;

    @Column(name = "dc_keys", unique = true)
    private String dcKeys;

    // Getters and Setters
    public int getIddiemcong() { return iddiemcong; }
    public void setIddiemcong(int iddiemcong) { this.iddiemcong = iddiemcong; }
    public String getTsCccd() { return tsCccd; }
    public void setTsCccd(String tsCccd) { this.tsCccd = tsCccd; }
    public String getManganh() { return manganh; }
    public void setManganh(String manganh) { this.manganh = manganh; }
    public String getMatohop() { return matohop; }
    public void setMatohop(String matohop) { this.matohop = matohop; }
    public String getPhuongthuc() { return phuongthuc; }
    public void setPhuongthuc(String phuongthuc) { this.phuongthuc = phuongthuc; }
    public Double getDiemCC() { return diemCC; }
    public void setDiemCC(Double diemCC) { this.diemCC = diemCC; }
    public Double getDiemUtxt() { return diemUtxt; }
    public void setDiemUtxt(Double diemUtxt) { this.diemUtxt = diemUtxt; }
    public Double getDiemTong() { return diemTong; }
    public void setDiemTong(Double diemTong) { this.diemTong = diemTong; }
    public String getGhichu() { return ghichu; }
    public void setGhichu(String ghichu) { this.ghichu = ghichu; }
    public String getDcKeys() { return dcKeys; }
    public void setDcKeys(String dcKeys) { this.dcKeys = dcKeys; }
}