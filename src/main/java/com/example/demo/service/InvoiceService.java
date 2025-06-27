package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepo invoiceRepo;

    @Autowired
    private InvoiceItemRepo invoiceItemRepo;

    @Autowired
    private QuatRepo quatRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private LoginRepo loginRepo;

    @Autowired
    private ItemRepos itemRepo;

    @Autowired
    private QitemRepo qitemRepo;

    public List<Invoice> getAllInvoices() {
        return invoiceRepo.findAll();
    }

    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepo.findById(id);
    }

    public List<Invoice> getInvoicesByCustomerId(Long customerId) {
        return invoiceRepo.findByCustomerId(customerId);
    }

    public List<Invoice> getInvoicesByUserId(Long userId) {
        return invoiceRepo.findByUserId(userId);
    }

    public List<Invoice> getInvoicesByStatus(Invoice.InvoiceStatus status) {
        return invoiceRepo.findByStatus(status);
    }

    public Optional<Invoice> getInvoiceByQuotationId(Long quatId) {
        return invoiceRepo.findByQuotationId(quatId);
    }

    @Transactional
    public Invoice createInvoice(Invoice invoice) {
        // Validate and set quotation
        if (invoice.getQuotation() != null && invoice.getQuotation().getId() != null) {
            Optional<Quat> quat = quatRepo.findById(invoice.getQuotation().getId());
            if (quat.isPresent()) {
                invoice.setQuotation(quat.get());
                invoice.setCustomer(quat.get().getCustomer());
                invoice.setUser(quat.get().getUser());
            } else {
                throw new RuntimeException("Quotation not found with id: " + invoice.getQuotation().getId());
            }
        }

        // Check if invoice number already exists
        if (invoiceRepo.existsByInvoiceNo(invoice.getInvoiceNo())) {
            throw new RuntimeException("Invoice number already exists: " + invoice.getInvoiceNo());
        }

        // Set default values
        if (invoice.getInvoiceDate() == null) {
            invoice.setInvoiceDate(LocalDateTime.now());
        }
        if (invoice.getStatus() == null) {
            invoice.setStatus(Invoice.InvoiceStatus.DRAFT);
        }
        if (invoice.getTaxRate() == null) {
            invoice.setTaxRate(BigDecimal.valueOf(18.0)); // Default 18% GST
        }
        if (invoice.getDiscountRate() == null) {
            invoice.setDiscountRate(BigDecimal.ZERO);
        }

        Invoice savedInvoice = invoiceRepo.save(invoice);

        // Create invoice items from quotation items
        if (invoice.getQuotation() != null) {
            createInvoiceItemsFromQuotation(savedInvoice, invoice.getQuotation().getId());
        }

        return savedInvoice;
    }

    @Transactional
    public Invoice createInvoiceFromQuotation(Long quatId, BigDecimal taxRate, BigDecimal discountRate, 
                                            String paymentTerms, String notes) {
        Optional<Quat> quatOpt = quatRepo.findById(quatId);
        if (!quatOpt.isPresent()) {
            throw new RuntimeException("Quotation not found with id: " + quatId);
        }

        Quat quat = quatOpt.get();

        // Check if invoice already exists for this quotation
        if (invoiceRepo.findByQuotationId(quatId).isPresent()) {
            throw new RuntimeException("Invoice already exists for quotation: " + quat.getQuatno());
        }

        // Generate invoice number
        String invoiceNo = generateInvoiceNumber();

        Invoice invoice = new Invoice();
        invoice.setInvoiceNo(invoiceNo);
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setDueDate(LocalDateTime.now().plusDays(30)); // Default 30 days
        invoice.setTaxRate(taxRate != null ? taxRate : BigDecimal.valueOf(18.0));
        invoice.setDiscountRate(discountRate != null ? discountRate : BigDecimal.ZERO);
        invoice.setPaymentTerms(paymentTerms != null ? paymentTerms : "Net 30 days");
        invoice.setNotes(notes);
        invoice.setStatus(Invoice.InvoiceStatus.DRAFT);
        invoice.setQuotation(quat);
        invoice.setCustomer(quat.getCustomer());
        invoice.setUser(quat.getUser());
        invoice.setCreatedBy(quat.getUser().getUsername());

        Invoice savedInvoice = invoiceRepo.save(invoice);
        createInvoiceItemsFromQuotation(savedInvoice, quatId);

        return savedInvoice;
    }

    private void createInvoiceItemsFromQuotation(Invoice invoice, Long quatId) {
        List<Qitem> qitems = qitemRepo.findByQuotationId(quatId);
        
        for (Qitem qitem : qitems) {
            InvoiceItem invoiceItem = new InvoiceItem();
            invoiceItem.setInvoice(invoice);
            invoiceItem.setItem(qitem.getItem());
            invoiceItem.setQuantity(qitem.getQuantity());
            invoiceItem.setUnitPrice(qitem.getUnitPrice());
            invoiceItem.setLicenseType(qitem.getLicenseType());
            invoiceItem.setDescription(qitem.getItem().getItemname());
            
            invoiceItemRepo.save(invoiceItem);
        }

        // Recalculate totals
        invoice.calculateTotals();
        invoiceRepo.save(invoice);
    }

    public Invoice updateInvoice(Long id, Invoice updatedInvoice) {
        return invoiceRepo.findById(id).map(invoice -> {
            if (updatedInvoice.getInvoiceNo() != null) {
                if (!invoice.getInvoiceNo().equals(updatedInvoice.getInvoiceNo()) && 
                    invoiceRepo.existsByInvoiceNo(updatedInvoice.getInvoiceNo())) {
                    throw new RuntimeException("Invoice number already exists: " + updatedInvoice.getInvoiceNo());
                }
                invoice.setInvoiceNo(updatedInvoice.getInvoiceNo());
            }
            if (updatedInvoice.getInvoiceDate() != null) {
                invoice.setInvoiceDate(updatedInvoice.getInvoiceDate());
            }
            if (updatedInvoice.getDueDate() != null) {
                invoice.setDueDate(updatedInvoice.getDueDate());
            }
            if (updatedInvoice.getTaxRate() != null) {
                invoice.setTaxRate(updatedInvoice.getTaxRate());
            }
            if (updatedInvoice.getDiscountRate() != null) {
                invoice.setDiscountRate(updatedInvoice.getDiscountRate());
            }
            if (updatedInvoice.getPaymentTerms() != null) {
                invoice.setPaymentTerms(updatedInvoice.getPaymentTerms());
            }
            if (updatedInvoice.getNotes() != null) {
                invoice.setNotes(updatedInvoice.getNotes());
            }
            if (updatedInvoice.getStatus() != null) {
                invoice.setStatus(updatedInvoice.getStatus());
            }
            
            invoice.setUpdatedBy(updatedInvoice.getUpdatedBy());
            invoice.setUpdatedOn(LocalDateTime.now());
            
            return invoiceRepo.save(invoice);
        }).orElse(null);
    }

    public boolean deleteInvoice(Long id) {
        if (invoiceRepo.existsById(id)) {
            invoiceRepo.deleteById(id);
            return true;
        }
        return false;
    }

    private String generateInvoiceNumber() {
        String prefix = "INV";
        String year = String.valueOf(LocalDateTime.now().getYear());
        long count = invoiceRepo.count() + 1;
        return String.format("%s-%s-%04d", prefix, year, count);
    }

    public List<InvoiceItem> getInvoiceItemsByInvoiceId(Long invoiceId) {
        return invoiceItemRepo.findByInvoiceId(invoiceId);
    }
}