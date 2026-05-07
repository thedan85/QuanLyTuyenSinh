package com.quanlytuyensinh.project.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "xt_tohop_monthi")
public class ToHopMon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idtohop;

    @Column(name = "matohop", unique = true, nullable = false)
    private String maToHop;

    @Column(name = "mon1", nullable = false)
    private String mon1;

    @Column(name = "mon2", nullable = false)
    private String mon2;

    @Column(name = "mon3", nullable = false)
    private String mon3;

    @Column(name = "tentohop")
    private String tenToHop;

    public int getIdtohop() {
        return idtohop;
    }

    public void setIdtohop(int idtohop) {
        this.idtohop = idtohop;
    }

    public String getMaToHop() {
        return maToHop;
    }

    public void setMaToHop(String maToHop) {
        this.maToHop = maToHop;
    }

    public String getMon1() {
        return mon1;
    }

    public void setMon1(String mon1) {
        this.mon1 = mon1;
    }

    public String getMon2() {
        return mon2;
    }

    public void setMon2(String mon2) {
        this.mon2 = mon2;
    }

    public String getMon3() {
        return mon3;
    }

    public void setMon3(String mon3) {
        this.mon3 = mon3;
    }

    public String getTenToHop() {
        return tenToHop;
    }

    public void setTenToHop(String tenToHop) {
        this.tenToHop = tenToHop;
    }
}
