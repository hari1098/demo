package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quat")
public class Quat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quat_id")
    private int id;

    @Column(name = "quatno")
    private String quatno;

    @Column(name = "quat_date")
    private LocalDateTime quatDate;

    @Column(name = "validity")
    private int validity;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "user_id")
    private int userId;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "customer_id", referencedColumnName = "customerid", insertable = false, updatable = false),
            @JoinColumn(name = "user_id", referencedColumnName = "user_no", insertable = false, updatable = false)
    })
    private Customer customer;

    public Quat() {}

    public Quat(String quatno, LocalDateTime quatDate, int validity, Long customerId, int userId) {
        this.quatno = quatno;
        this.quatDate = quatDate;
        this.validity = validity;
        this.customerId = customerId;
        this.userId = userId;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuatno() {
        return quatno;
    }

    public void setQuatno(String quatno) {
        this.quatno = quatno;
    }

    public LocalDateTime getQuatDate() {
        return quatDate;
    }

    public void setQuatDate(LocalDateTime quatDate) {
        this.quatDate = quatDate;
    }

    public int getValidity() {
        return validity;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
