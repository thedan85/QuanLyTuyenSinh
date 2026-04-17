package com.example.entity;

import javax.persistence.*;

@Entity
@Table(name = "xt_nganh_tohop")
public class NganhToHop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "manganh", nullable = false)
    private String manganh;

    @Column(name = "matohop", nullable = false)
    private String matohop;

    @Column(name = "th_mon1") private String thMon1;
    @Column(name = "hsmon1") private Integer hsMon1;
    @Column(name = "th_mon2") private String thMon2;
    @Column(name = "hsmon2") private Integer hsMon2;
    @Column(name = "th_mon3") private String thMon3;
    @Column(name = "hsmon3") private Integer hsMon3;

    @Column(name = "tb_keys", unique = true)
    private String tbKeys;

    // --- CÁC CỜ ĐÁNH DẤU MÔN THI (0 hoặc 1) ---
    @Column(name = "N1") private Integer n1;
    @Column(name = "`TO`") private Integer to; // Bắt buộc bọc backtick vì TO là từ khóa SQL
    @Column(name = "LI") private Integer li;
    @Column(name = "HO") private Integer ho;
    @Column(name = "SI") private Integer si;
    @Column(name = "VA") private Integer va;
    @Column(name = "SU") private Integer su;
    @Column(name = "DI") private Integer di;
    @Column(name = "TI") private Integer ti;
    @Column(name = "KHAC") private Integer khac;
    @Column(name = "KTPL") private Integer ktpl;

    // --- ĐỘ LỆCH ĐIỂM ---
    @Column(name = "dolech") private Double dolech;

    // --- GETTERS AND SETTERS ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getManganh() { return manganh; }
    public void setManganh(String manganh) { this.manganh = manganh; }
    public String getMatohop() { return matohop; }
    public void setMatohop(String matohop) { this.matohop = matohop; }
    
    public String getThMon1() { return thMon1; }
    public void setThMon1(String thMon1) { this.thMon1 = thMon1; }
    public Integer getHsMon1() { return hsMon1; }
    public void setHsMon1(Integer hsMon1) { this.hsMon1 = hsMon1; }
    public String getThMon2() { return thMon2; }
    public void setThMon2(String thMon2) { this.thMon2 = thMon2; }
    public Integer getHsMon2() { return hsMon2; }
    public void setHsMon2(Integer hsMon2) { this.hsMon2 = hsMon2; }
    public String getThMon3() { return thMon3; }
    public void setThMon3(String thMon3) { this.thMon3 = thMon3; }
    public Integer getHsMon3() { return hsMon3; }
    public void setHsMon3(Integer hsMon3) { this.hsMon3 = hsMon3; }
    
    public String getTbKeys() { return tbKeys; }
    public void setTbKeys(String tbKeys) { this.tbKeys = tbKeys; }

    public Integer getN1() { return n1; }
    public void setN1(Integer n1) { this.n1 = n1; }
    public Integer getTo() { return to; }
    public void setTo(Integer to) { this.to = to; }
    public Integer getLi() { return li; }
    public void setLi(Integer li) { this.li = li; }
    public Integer getHo() { return ho; }
    public void setHo(Integer ho) { this.ho = ho; }
    public Integer getSi() { return si; }
    public void setSi(Integer si) { this.si = si; }
    public Integer getVa() { return va; }
    public void setVa(Integer va) { this.va = va; }
    public Integer getSu() { return su; }
    public void setSu(Integer su) { this.su = su; }
    public Integer getDi() { return di; }
    public void setDi(Integer di) { this.di = di; }
    public Integer getTi() { return ti; }
    public void setTi(Integer ti) { this.ti = ti; }
    public Integer getKhac() { return khac; }
    public void setKhac(Integer khac) { this.khac = khac; }
    public Integer getKtpl() { return ktpl; }
    public void setKtpl(Integer ktpl) { this.ktpl = ktpl; }
    
    public Double getDolech() { return dolech; }
    public void setDolech(Double dolech) { this.dolech = dolech; }
}