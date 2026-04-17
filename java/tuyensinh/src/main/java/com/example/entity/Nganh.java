package com.example.entity;

import javax.persistence.*;

@Entity
@Table(name = "xt_nganh")
public class Nganh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idnganh;

    @Column(name = "manganh", nullable = false)
    private String manganh;

    @Column(name = "tennganh", nullable = false)
    private String tennganh;

    @Column(name = "n_tohopgoc")
    private String nTohopgoc;

    @Column(name = "n_chitieu")
    private Integer nChitieu;

    @Column(name = "n_diemsan")
    private Double nDiemsan;

    @Column(name = "n_diemtrungtuyen")
    private Double nDiemtrungtuyen;

    @Column(name = "n_tuyenthang")
    private String nTuyenthang;

    @Column(name = "n_dgnl")
    private String nDgnl;

    @Column(name = "n_thpt")
    private String nThpt;

    @Column(name = "n_vsat")
    private String nVsat;

    @Column(name = "sl_xtt")
    private Integer slXtt;

    @Column(name = "sl_dgnl")
    private Integer slDgnl;

    @Column(name = "sl_vsat")
    private Integer slVsat;

    @Column(name = "sl_thpt")
    private String slThpt; // Ánh xạ theo varchar(45) trong DB

    // --- GETTERS AND SETTERS ---
    public int getIdnganh() { return idnganh; }
    public void setIdnganh(int idnganh) { this.idnganh = idnganh; }
    
    public String getManganh() { return manganh; }
    public void setManganh(String manganh) { this.manganh = manganh; }
    
    public String getTennganh() { return tennganh; }
    public void setTennganh(String tennganh) { this.tennganh = tennganh; }
    
    public String getnTohopgoc() { return nTohopgoc; }
    public void setnTohopgoc(String nTohopgoc) { this.nTohopgoc = nTohopgoc; }
    
    public Integer getnChitieu() { return nChitieu; }
    public void setnChitieu(Integer nChitieu) { this.nChitieu = nChitieu; }
    
    public Double getnDiemsan() { return nDiemsan; }
    public void setnDiemsan(Double nDiemsan) { this.nDiemsan = nDiemsan; }
    
    public Double getnDiemtrungtuyen() { return nDiemtrungtuyen; }
    public void setnDiemtrungtuyen(Double nDiemtrungtuyen) { this.nDiemtrungtuyen = nDiemtrungtuyen; }
    
    public String getnTuyenthang() { return nTuyenthang; }
    public void setnTuyenthang(String nTuyenthang) { this.nTuyenthang = nTuyenthang; }
    
    public String getnDgnl() { return nDgnl; }
    public void setnDgnl(String nDgnl) { this.nDgnl = nDgnl; }
    
    public String getnThpt() { return nThpt; }
    public void setnThpt(String nThpt) { this.nThpt = nThpt; }
    
    public String getnVsat() { return nVsat; }
    public void setnVsat(String nVsat) { this.nVsat = nVsat; }
    
    public Integer getSlXtt() { return slXtt; }
    public void setSlXtt(Integer slXtt) { this.slXtt = slXtt; }
    
    public Integer getSlDgnl() { return slDgnl; }
    public void setSlDgnl(Integer slDgnl) { this.slDgnl = slDgnl; }
    
    public Integer getSlVsat() { return slVsat; }
    public void setSlVsat(Integer slVsat) { this.slVsat = slVsat; }
    
    public String getSlThpt() { return slThpt; }
    public void setSlThpt(String slThpt) { this.slThpt = slThpt; }
}