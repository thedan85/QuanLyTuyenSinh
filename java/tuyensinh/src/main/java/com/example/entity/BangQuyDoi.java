package com.example.entity;

import javax.persistence.*;

@Entity
@Table(name = "xt_bangquydoi")
public class BangQuyDoi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idqd;

    @Column(name = "d_phuongthuc")
    private String dPhuongthuc;

    @Column(name = "d_tohop")
    private String dTohop;

    @Column(name = "d_mon")
    private String dMon;

    @Column(name = "d_diema")
    private Double dDiema;

    @Column(name = "d_diemb")
    private Double dDiemb;

    @Column(name = "d_diemc")
    private Double dDiemc;

    @Column(name = "d_diemd")
    private Double dDiemd;

    @Column(name = "d_maquydoi", unique = true)
    private String dMaquydoi;

    @Column(name = "d_phanvi")
    private String dPhanvi;

    // Getters and Setters
    public int getIdqd() { return idqd; }
    public void setIdqd(int idqd) { this.idqd = idqd; }
    public String getdPhuongthuc() { return dPhuongthuc; }
    public void setdPhuongthuc(String dPhuongthuc) { this.dPhuongthuc = dPhuongthuc; }
    public String getdTohop() { return dTohop; }
    public void setdTohop(String dTohop) { this.dTohop = dTohop; }
    public String getdMon() { return dMon; }
    public void setdMon(String dMon) { this.dMon = dMon; }
    public Double getdDiema() { return dDiema; }
    public void setdDiema(Double dDiema) { this.dDiema = dDiema; }
    public Double getdDiemb() { return dDiemb; }
    public void setdDiemb(Double dDiemb) { this.dDiemb = dDiemb; }
    public Double getdDiemc() { return dDiemc; }
    public void setdDiemc(Double dDiemc) { this.dDiemc = dDiemc; }
    public Double getdDiemd() { return dDiemd; }
    public void setdDiemd(Double dDiemd) { this.dDiemd = dDiemd; }
    public String getdMaquydoi() { return dMaquydoi; }
    public void setdMaquydoi(String dMaquydoi) { this.dMaquydoi = dMaquydoi; }
    public String getdPhanvi() { return dPhanvi; }
    public void setdPhanvi(String dPhanvi) { this.dPhanvi = dPhanvi; }
}