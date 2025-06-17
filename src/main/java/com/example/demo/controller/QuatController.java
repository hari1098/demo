package com.example.demo.controller;

import com.example.demo.model.Quat;
import com.example.demo.service.InvoicePdfService; // Import the new service
import com.example.demo.service.QuatService;
import com.itextpdf.text.DocumentException; // Import for PDF exceptions
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quat")
public class QuatController {

    @Autowired
    private QuatService quatService;

    @Autowired
    private InvoicePdfService invoicePdfService; // Inject the PDF service

    @PostMapping
    public ResponseEntity<Quat> createItem(@RequestBody Quat quat) {
        Quat createdQuat = quatService.createQuat(quat);
        return new ResponseEntity<>(createdQuat, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quat> updateQuat(@PathVariable int id, @RequestBody Quat quat) {
        Quat updated = quatService.updateQuat(id, quat);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<Quat> getAllQuats() {
        return quatService.getAllQuats();
    }

    @GetMapping("/{id}")
    public Optional<Quat> getQuatById(@PathVariable int id) {
        return quatService.getQuatById(id);
    }

    // --- New Endpoint for Invoice Generation ---
    @GetMapping("/{quatId}/invoice")
    public ResponseEntity<byte[]> generateInvoice(@PathVariable int quatId) {
        try {
            byte[] pdfBytes = invoicePdfService.generateInvoicePdf(quatId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = "invoice_" + quatId + ".pdf";
            headers.setContentDispositionFormData("attachment", filename); // Forces download
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(("Error generating PDF: " + e.getMessage()).getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage().getBytes(), HttpStatus.NOT_FOUND);
        }
    }
    // --- End New Endpoint ---
}