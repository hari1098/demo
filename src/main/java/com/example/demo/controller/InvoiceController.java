package com.example.demo.controller;

import com.example.demo.model.Invoice;
import com.example.demo.service.InvoicePdfService;
import com.example.demo.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private InvoicePdfService invoicePdfService;

    @GetMapping
    public List<Invoice> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public List<Invoice> getInvoicesByCustomerId(@PathVariable Long customerId) {
        return invoiceService.getInvoicesByCustomerId(customerId);
    }

    @GetMapping("/user/{userId}")
    public List<Invoice> getInvoicesByUserId(@PathVariable Long userId) {
        return invoiceService.getInvoicesByUserId(userId);
    }

    @GetMapping("/status/{status}")
    public List<Invoice> getInvoicesByStatus(@PathVariable Invoice.InvoiceStatus status) {
        return invoiceService.getInvoicesByStatus(status);
    }

    @GetMapping("/quotation/{quatId}")
    public ResponseEntity<Invoice> getInvoiceByQuotationId(@PathVariable Long quatId) {
        return invoiceService.getInvoiceByQuotationId(quatId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        try {
            Invoice createdInvoice = invoiceService.createInvoice(invoice);
            return new ResponseEntity<>(createdInvoice, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/from-quotation/{quatId}")
    public ResponseEntity<Invoice> createInvoiceFromQuotation(
            @PathVariable Long quatId,
            @RequestParam(required = false, defaultValue = "18.0") BigDecimal taxRate,
            @RequestParam(required = false, defaultValue = "0.0") BigDecimal discountRate,
            @RequestParam(required = false, defaultValue = "Net 30 days") String paymentTerms,
            @RequestParam(required = false) String notes) {
        try {
            Invoice invoice = invoiceService.createInvoiceFromQuotation(quatId, taxRate, discountRate, paymentTerms, notes);
            return new ResponseEntity<>(invoice, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable Long id, @RequestBody Invoice invoice) {
        try {
            Invoice updated = invoiceService.updateInvoice(id, invoice);
            return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInvoice(@PathVariable Long id) {
        boolean deleted = invoiceService.deleteInvoice(id);
        return deleted ? ResponseEntity.ok("Invoice deleted successfully.") : ResponseEntity.notFound().build();
    }

    @GetMapping("/{invoiceId}/pdf")
    public ResponseEntity<byte[]> generateInvoicePdf(@PathVariable Long invoiceId) {
        try {
            byte[] pdfBytes = invoicePdfService.generateInvoicePdf(invoiceId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = "invoice_" + invoiceId + ".pdf";
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(("Error generating PDF: " + e.getMessage()).getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}