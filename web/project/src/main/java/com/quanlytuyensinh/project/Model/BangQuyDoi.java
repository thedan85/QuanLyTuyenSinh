package com.quanlytuyensinh.project.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "xt_bangquydoi")
public class BangQuyDoi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idqd;

    @Column(name = "d_phuongthuc")
    private String phuongThuc;

    @Column(name = "d_tohop")
    private String toHop;

    @Column(name = "d_mon")
    private String mon;

    @Column(name = "d_diema")
    private Double diemA;

    @Column(name = "d_diemb")
    private Double diemB;

    @Column(name = "d_diemc")
    private Double diemC;

    @Column(name = "d_diemd")
    private Double diemD;

    @Column(name = "d_maquydoi")
    private String maQuyDoi;

    @Column(name = "d_phanvi")
    private String phanVi;

    public int getIdqd() {
        return idqd;
    }

    public void setIdqd(int idqd) {
        this.idqd = idqd;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public String getToHop() {
        return toHop;
    }

    public void setToHop(String toHop) {
        this.toHop = toHop;
    }

    public String getMon() {
        return mon;
    }

    public void setMon(String mon) {
        this.mon = mon;
    }

    public Double getDiemA() {
        return diemA;
    }

    public void setDiemA(Double diemA) {
        this.diemA = diemA;
    }

    public Double getDiemB() {
        return diemB;
    }

    public void setDiemB(Double diemB) {
        this.diemB = diemB;
    }

    public Double getDiemC() {
        return diemC;
    }

    public void setDiemC(Double diemC) {
        this.diemC = diemC;
    }

    public Double getDiemD() {
        return diemD;
    }

    public void setDiemD(Double diemD) {
        this.diemD = diemD;
    }

    public String getMaQuyDoi() {
        return maQuyDoi;
    }

    public void setMaQuyDoi(String maQuyDoi) {
        this.maQuyDoi = maQuyDoi;
    }

    public String getPhanVi() {
        return phanVi;
    }

    public void setPhanVi(String phanVi) {
        this.phanVi = phanVi;
    }
}
