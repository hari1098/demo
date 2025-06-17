package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customerid")
    private Long id;

    @Column(name = "customer_name")
    private String customername;

    @Column(name = "email_id")
    private String emailid;

    @Column(name = "mobile_number")
    private long mobilenumber; // changed from int to long

    @Column(name = "company_name")
    private String companyname;

    @Column(name = "address")
    private String address;

    @Column(name = "reffered_by")
    private String refferedby;

    @Column(name = "user_no")
    private int userno;

    public Customer(Long id, String customername, String emailid, long mobilenumber, String companyname, String address, String refferedby, int userno) {
        this.id = id;
        this.customername = customername;
        this.emailid = emailid;
        this.mobilenumber = mobilenumber;
        this.companyname = companyname;
        this.address = address;
        this.refferedby = refferedby;
        this.userno = userno;
    }

    public Customer() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public long getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(long mobilenumber) {
        this.mobilenumber = mobilenumber;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRefferedby() {
        return refferedby;
    }

    public void setRefferedby(String refferedby) {
        this.refferedby = refferedby;
    }

    public int getUserno() {
        return userno;
    }

    public void setUserno(int userno) {
        this.userno = userno;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", customername='" + customername + '\'' +
                ", emailid='" + emailid + '\'' +
                ", mobilenumber=" + mobilenumber +
                ", companyname='" + companyname + '\'' +
                ", address='" + address + '\'' +
                ", refferedby='" + refferedby + '\'' +
                ", userno=" + userno +
                '}';
    }
}
