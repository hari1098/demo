package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "invoice_item")
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "license_type")
    private String licenseType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Foreign key to Invoice
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false, foreignKey = @ForeignKey(name = "FK_invoice_item_invoice"))
    private Invoice invoice;

    // Foreign key to Item
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "FK_invoice_item_item"))
    private Item item;

    public InvoiceItem() {
    }

    public InvoiceItem(Integer quantity, BigDecimal unitPrice, String licenseType, 
                      String description, Invoice invoice, Item item) {
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.licenseType = licenseType;
        this.description = description;
        this.invoice = invoice;
        this.item = item;
        calculateTotalPrice();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateTotalPrice();
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotalPrice();
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        if (item != null && unitPrice == null) {
            this.unitPrice = item.getPrice();
            calculateTotalPrice();
        }
    }

    // Helper methods
    public Long getInvoiceId() {
        return invoice != null ? invoice.getId() : null;
    }

    public Long getItemId() {
        return item != null ? item.getId() : null;
    }

    public String getItemName() {
        return item != null ? item.getItemname() : null;
    }

    private void calculateTotalPrice() {
        if (quantity != null && unitPrice != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    @PrePersist
    @PreUpdate
    private void prePersist() {
        calculateTotalPrice();
    }

    @Override
    public String toString() {
        return "InvoiceItem{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                ", licenseType='" + licenseType + '\'' +
                '}';
    }
}