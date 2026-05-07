package com.quanlytuyensinh.project.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "xt_nganh_tohop")
public class NganhToHop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "manganh", nullable = false)
    private String maNganh;

    @Column(name = "matohop", nullable = false)
    private String maToHop;

    @Column(name = "th_mon1")
    private String thMon1;

    @Column(name = "hsmon1")
    private Integer hsMon1;

    @Column(name = "th_mon2")
    private String thMon2;

    @Column(name = "hsmon2")
    private Integer hsMon2;

    @Column(name = "th_mon3")
    private String thMon3;

    @Column(name = "hsmon3")
    private Integer hsMon3;

    @Column(name = "dolech")
    private Double dolech;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaNganh() {
        return maNganh;
    }

    public void setMaNganh(String maNganh) {
        this.maNganh = maNganh;
    }

    public String getMaToHop() {
        return maToHop;
    }

    public void setMaToHop(String maToHop) {
        this.maToHop = maToHop;
    }

    public String getThMon1() {
        return thMon1;
    }

    public void setThMon1(String thMon1) {
        this.thMon1 = thMon1;
    }

    public Integer getHsMon1() {
        return hsMon1;
    }

    public void setHsMon1(Integer hsMon1) {
        this.hsMon1 = hsMon1;
    }

    public String getThMon2() {
        return thMon2;
    }

    public void setThMon2(String thMon2) {
        this.thMon2 = thMon2;
    }

    public Integer getHsMon2() {
        return hsMon2;
    }

    public void setHsMon2(Integer hsMon2) {
        this.hsMon2 = hsMon2;
    }

    public String getThMon3() {
        return thMon3;
    }

    public void setThMon3(String thMon3) {
        this.thMon3 = thMon3;
    }

    public Integer getHsMon3() {
        return hsMon3;
    }

    public void setHsMon3(Integer hsMon3) {
        this.hsMon3 = hsMon3;
    }

    public Double getDolech() {
        return dolech;
    }

    public void setDolech(Double dolech) {
        this.dolech = dolech;
    }
}
