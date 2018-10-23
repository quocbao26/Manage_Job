package com.example.asus.model;

import java.io.Serializable;

public class Job implements Serializable{
    private String id;
    private String tenCongViec;
    private String noiDung;
    private String diaDiem;
    private String ngay;
    private String gio;
    private String keyUuTien;
    private boolean trangThai;

    public Job() {
    }

    public Job(String id, String tenCongViec, String noiDung, String diaDiem, String ngay, String gio, String keyUuTien, boolean trangThai) {
        this.id = id;
        this.tenCongViec = tenCongViec;
        this.noiDung = noiDung;
        this.diaDiem = diaDiem;
        this.ngay = ngay;
        this.gio = gio;
        this.keyUuTien = keyUuTien;
        this.trangThai = trangThai;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenCongViec() {
        return tenCongViec;
    }

    public void setTenCongViec(String tenCongViec) {
        this.tenCongViec = tenCongViec;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getDiaDiem() {
        return diaDiem;
    }

    public void setDiaDiem(String diaDiem) {
        this.diaDiem = diaDiem;
    }

    public String getNgay() {
        return ngay;
    }

    public void setNgay(String ngay) {
        this.ngay = ngay;
    }

    public String getGio() {
        return gio;
    }

    public void setGio(String gio) {
        this.gio = gio;
    }

    public String getKeyUuTien() {
        return keyUuTien;
    }

    public void setKeyUuTien(String keyUuTien) {
        this.keyUuTien = keyUuTien;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }
}
