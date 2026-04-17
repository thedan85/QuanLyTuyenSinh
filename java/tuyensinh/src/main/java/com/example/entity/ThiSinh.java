package com.example.entity;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "xt_thisinhxettuyen25")
public class ThiSinh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idthisinh;

    @Column(name = "cccd", unique = true)
    private String cccd;

    @Column(name = "sobaodanh")
    private String sobaodanh;

    @Column(name = "ho")
    private String ho;

    @Column(name = "ten")
    private String ten;

    @Column(name = "ngay_sinh")
    private String ngaySinh;

    @Column(name = "dien_thoai")
    private String dienThoai;

    @Column(name = "password")
    private String password;

    @Column(name = "gioi_tinh")
    private String gioiTinh;

    @Column(name = "email")
    private String email;

    @Column(name = "noi_sinh")
    private String noiSinh;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "doi_tuong")
    private String doiTuong;

    @Column(name = "khu_vuc")
    private String khuVuc;

    // --- GETTERS AND SETTERS ---
    public int getIdthisinh() { return idthisinh; }
    public void setIdthisinh(int idthisinh) { this.idthisinh = idthisinh; }
    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }
    public String getSobaodanh() { return sobaodanh; }
    public void setSobaodanh(String sobaodanh) { this.sobaodanh = sobaodanh; }
    public String getHo() { return ho; }
    public void setHo(String ho) { this.ho = ho; }
    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }
    public String getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(String ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getDienThoai() { return dienThoai; }
    public void setDienThoai(String dienThoai) { this.dienThoai = dienThoai; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNoiSinh() { return noiSinh; }
    public void setNoiSinh(String noiSinh) { this.noiSinh = noiSinh; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public String getDoiTuong() { return doiTuong; }
    public void setDoiTuong(String doiTuong) { this.doiTuong = doiTuong; }
    public String getKhuVuc() { return khuVuc; }
    public void setKhuVuc(String khuVuc) { this.khuVuc = khuVuc; }
}