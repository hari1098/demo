package com.example.demo.model;
import jakarta.persistence.*;

@Entity
@Table(name = "qitem")
public class Qitem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "qitem")
    private String qitem;

    @Column(name = "item_id")
    private int itemId;

    @Column(name = "quantity")
    private int qty;

    @Column(name = "license_type")
    private String licenseType;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQitem() {
        return qitem;
    }

    public void setQitem(String qitem) {
        this.qitem = qitem;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public Qitem(int id, String qitem, int itemId, int qty, String licenseType) {
        this.id = id;
        this.qitem = qitem;
        this.itemId = itemId;
        this.qty = qty;
        this.licenseType = licenseType;
    }


    public Qitem() {

    }
}
