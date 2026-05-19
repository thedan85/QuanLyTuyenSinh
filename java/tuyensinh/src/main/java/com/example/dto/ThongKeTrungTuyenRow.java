package com.example.dto;

/** Thống kê trúng tuyển theo ngành (mục 6c). */
public class ThongKeTrungTuyenRow {
    private String maNganh;
    private String tenNganh;
    private int chiTieu;
    private int trungPt1;
    private int trungPt2;
    private int trungPt3;
    private int tongTrung;
    private int slThptCt;
    private int slDgnlCt;
    private int slVsatCt;

    public String getMaNganh() { return maNganh; }
    public void setMaNganh(String maNganh) { this.maNganh = maNganh; }
    public String getTenNganh() { return tenNganh; }
    public void setTenNganh(String tenNganh) { this.tenNganh = tenNganh; }
    public int getChiTieu() { return chiTieu; }
    public void setChiTieu(int chiTieu) { this.chiTieu = chiTieu; }
    public int getTrungPt1() { return trungPt1; }
    public void setTrungPt1(int trungPt1) { this.trungPt1 = trungPt1; }
    public int getTrungPt2() { return trungPt2; }
    public void setTrungPt2(int trungPt2) { this.trungPt2 = trungPt2; }
    public int getTrungPt3() { return trungPt3; }
    public void setTrungPt3(int trungPt3) { this.trungPt3 = trungPt3; }
    public int getTongTrung() { return tongTrung; }
    public void setTongTrung(int tongTrung) { this.tongTrung = tongTrung; }
    public int getSlThptCt() { return slThptCt; }
    public void setSlThptCt(int slThptCt) { this.slThptCt = slThptCt; }
    public int getSlDgnlCt() { return slDgnlCt; }
    public void setSlDgnlCt(int slDgnlCt) { this.slDgnlCt = slDgnlCt; }
    public int getSlVsatCt() { return slVsatCt; }
    public void setSlVsatCt(int slVsatCt) { this.slVsatCt = slVsatCt; }
}
