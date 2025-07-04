package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.model.Invoice;
import com.example.demo.model.InvoiceItem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class InvoicePdfService {

    @Autowired
    private InvoiceService invoiceService;

    // Company Information
    private static final String COMPANY_NAME = "TechSolutions Pvt Ltd";
    private static final String COMPANY_ADDRESS = "123 Business Park, Coimbatore - 641001, Tamil Nadu";
    private static final String COMPANY_EMAIL = "sales@techsolutions.com";
    private static final String COMPANY_PHONE = "+91 9876543210";
    private static final String COMPANY_WEBSITE = "www.techsolutions.com";
    private static final String COMPANY_GSTIN = "33AAAAA0000A1Z5";
    private static final String BANK_DETAILS = "Bank: HDFC Bank | A/C: 12345678901 | IFSC: HDFC0001234";

    public byte[] generateInvoicePdf(Long invoiceId) throws IOException {
        // Validate and fetch data
        Invoice invoice = invoiceService.getInvoiceById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));

        Customer customer = invoice.getCustomer();
        if (customer == null) {
            throw new RuntimeException("Customer not found for invoice ID: " + invoiceId);
        }

        List<InvoiceItem> invoiceItems = invoiceService.getInvoiceItemsByInvoiceId(invoiceId);

        // Create PDF document
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float yPosition = page.getMediaBox().getHeight() - 50;

                // Add company header
                yPosition = addCompanyHeader(contentStream, yPosition);
                yPosition -= 30;

                // Add invoice title
                yPosition = addInvoiceTitle(contentStream, yPosition);
                yPosition -= 20;

                // Add invoice and customer details
                yPosition = addInvoiceAndCustomerDetails(contentStream, invoice, customer, yPosition);
                yPosition -= 30;

                // Add items table
                yPosition = addItemsTable(contentStream, invoiceItems, yPosition);
                yPosition -= 20;

                // Add totals with tax and discount
                yPosition = addInvoiceTotals(contentStream, invoice, yPosition);
                yPosition -= 30;

                // Add payment details and footer
                addInvoiceFooter(contentStream, invoice, yPosition);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    private float addCompanyHeader(PDPageContentStream contentStream, float yPosition) throws IOException {
        // Company name
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 20);
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText(COMPANY_NAME);
        contentStream.endText();

        yPosition -= 25;

        // Company details
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText(COMPANY_ADDRESS);
        contentStream.endText();

        yPosition -= 15;

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText("GSTIN: " + COMPANY_GSTIN + " | Email: " + COMPANY_EMAIL + " | Phone: " + COMPANY_PHONE);
        contentStream.endText();

        yPosition -= 15;

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText("Website: " + COMPANY_WEBSITE);
        contentStream.endText();

        // Add line separator
        yPosition -= 20;
        contentStream.moveTo(50, yPosition);
        contentStream.lineTo(545, yPosition);
        contentStream.stroke();

        return yPosition;
    }

    private float addInvoiceTitle(PDPageContentStream contentStream, float yPosition) throws IOException {
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 24);
        contentStream.newLineAtOffset(250, yPosition);
        contentStream.showText("INVOICE");
        contentStream.endText();

        return yPosition;
    }

    private float addInvoiceAndCustomerDetails(PDPageContentStream contentStream, Invoice invoice, 
                                             Customer customer, float yPosition) throws IOException {
        // Left side - Invoice details
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText("Invoice Details:");
        contentStream.endText();

        yPosition -= 20;

        String[] invoiceDetails = {
            "Invoice No: " + invoice.getInvoiceNo(),
            "Invoice Date: " + invoice.getInvoiceDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")),
            "Due Date: " + (invoice.getDueDate() != null ? invoice.getDueDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) : "N/A"),
            "Status: " + invoice.getStatus().toString(),
            "Reference: " + (invoice.getQuotation() != null ? invoice.getQuotation().getQuatno() : "N/A")
        };

        for (String detail : invoiceDetails) {
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText(detail);
            contentStream.endText();
            yPosition -= 15;
        }

        // Right side - Customer details
        float rightColumnX = 320;
        yPosition += (invoiceDetails.length * 15) + 20; // Reset to top

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.newLineAtOffset(rightColumnX, yPosition);
        contentStream.showText("Bill To:");
        contentStream.endText();

        yPosition -= 20;

        String[] customerDetails = {
            customer.getCustomername(),
            customer.getCompanyname() != null ? customer.getCompanyname() : "",
            customer.getAddress() != null ? customer.getAddress() : "",
            "Email: " + customer.getEmailid(),
            "Phone: " + customer.getMobilenumber()
        };

        for (String detail : customerDetails) {
            if (!detail.isEmpty()) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.newLineAtOffset(rightColumnX, yPosition);
                contentStream.showText(detail);
                contentStream.endText();
                yPosition -= 15;
            }
        }

        return yPosition - 10;
    }

    private float addItemsTable(PDPageContentStream contentStream, List<InvoiceItem> invoiceItems, float yPosition) throws IOException {
        // Table headers
        float tableTop = yPosition;
        float tableMargin = 50;
        float tableWidth = 495;
        float rowHeight = 20;
        float cellMargin = 5;

        // Column widths
        float[] columnWidths = {40, 200, 80, 60, 80, 80};
        String[] headers = {"#", "Description", "License", "Qty", "Unit Price", "Amount"};

        // Draw table header
        contentStream.setNonStrokingColor(Color.DARK_GRAY);
        contentStream.addRect(tableMargin, yPosition - rowHeight, tableWidth, rowHeight);
        contentStream.fill();

        contentStream.setNonStrokingColor(Color.WHITE);
        float currentX = tableMargin;
        for (int i = 0; i < headers.length; i++) {
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
            contentStream.newLineAtOffset(currentX + cellMargin, yPosition - 15);
            contentStream.showText(headers[i]);
            contentStream.endText();
            currentX += columnWidths[i];
        }

        yPosition -= rowHeight;
        contentStream.setNonStrokingColor(Color.BLACK);

        // Draw table rows
        int rowNum = 1;
        for (InvoiceItem item : invoiceItems) {
            String itemName = item.getItem() != null ? item.getItem().getItemname() : "Unknown Item";
            String licenseType = item.getLicenseType() != null ? item.getLicenseType() : "Standard";
            BigDecimal unitPrice = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
            BigDecimal itemTotal = item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO;

            String[] rowData = {
                String.valueOf(rowNum++),
                itemName,
                licenseType,
                String.valueOf(item.getQuantity()),
                String.format("₹%.2f", unitPrice),
                String.format("₹%.2f", itemTotal)
            };

            // Alternate row background
            if (rowNum % 2 == 0) {
                contentStream.setNonStrokingColor(new Color(245, 245, 245));
                contentStream.addRect(tableMargin, yPosition - rowHeight, tableWidth, rowHeight);
                contentStream.fill();
                contentStream.setNonStrokingColor(Color.BLACK);
            }

            currentX = tableMargin;
            for (int i = 0; i < rowData.length; i++) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
                contentStream.newLineAtOffset(currentX + cellMargin, yPosition - 15);
                contentStream.showText(rowData[i]);
                contentStream.endText();
                currentX += columnWidths[i];
            }

            yPosition -= rowHeight;
        }

        // Draw table border
        contentStream.setStrokingColor(Color.BLACK);
        contentStream.addRect(tableMargin, yPosition, tableWidth, tableTop - yPosition);
        contentStream.stroke();

        // Draw column separators
        currentX = tableMargin;
        for (int i = 0; i < columnWidths.length - 1; i++) {
            currentX += columnWidths[i];
            contentStream.moveTo(currentX, tableTop);
            contentStream.lineTo(currentX, yPosition);
            contentStream.stroke();
        }

        return yPosition;
    }

    private float addInvoiceTotals(PDPageContentStream contentStream, Invoice invoice, float yPosition) throws IOException {
        float boxWidth = 200;
        float boxX = 345;
        float lineHeight = 20;

        BigDecimal subtotal = invoice.getSubtotal() != null ? invoice.getSubtotal() : BigDecimal.ZERO;
        BigDecimal discountAmount = invoice.getDiscountAmount() != null ? invoice.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal taxAmount = invoice.getTaxAmount() != null ? invoice.getTaxAmount() : BigDecimal.ZERO;
        BigDecimal totalAmount = invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO;

        // Subtotal
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
        contentStream.newLineAtOffset(boxX + 10, yPosition);
        contentStream.showText("Subtotal:");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
        contentStream.newLineAtOffset(boxX + 120, yPosition);
        contentStream.showText(String.format("₹%.2f", subtotal));
        contentStream.endText();

        yPosition -= lineHeight;

        // Discount
        if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
            contentStream.newLineAtOffset(boxX + 10, yPosition);
            contentStream.showText("Discount (" + invoice.getDiscountRate() + "%):");
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
            contentStream.newLineAtOffset(boxX + 120, yPosition);
            contentStream.showText(String.format("-₹%.2f", discountAmount));
            contentStream.endText();

            yPosition -= lineHeight;
        }

        // Tax
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
        contentStream.newLineAtOffset(boxX + 10, yPosition);
        contentStream.showText("Tax (" + invoice.getTaxRate() + "%):");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
        contentStream.newLineAtOffset(boxX + 120, yPosition);
        contentStream.showText(String.format("₹%.2f", taxAmount));
        contentStream.endText();

        yPosition -= lineHeight + 5;

        // Total box
        float totalBoxHeight = 25;
        contentStream.setNonStrokingColor(new Color(0, 51, 102));
        contentStream.addRect(boxX, yPosition - totalBoxHeight, boxWidth, totalBoxHeight);
        contentStream.fill();

        contentStream.setNonStrokingColor(Color.WHITE);

        // Total amount
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.newLineAtOffset(boxX + 10, yPosition - 18);
        contentStream.showText("Total Amount:");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.newLineAtOffset(boxX + 120, yPosition - 18);
        contentStream.showText(String.format("₹%.2f", totalAmount));
        contentStream.endText();

        contentStream.setNonStrokingColor(Color.BLACK);

        return yPosition - totalBoxHeight;
    }

    private void addInvoiceFooter(PDPageContentStream contentStream, Invoice invoice, float yPosition) throws IOException {
        yPosition -= 20;

        // Payment terms
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText("Payment Terms:");
        contentStream.endText();

        yPosition -= 15;

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText(invoice.getPaymentTerms() != null ? invoice.getPaymentTerms() : "Net 30 days");
        contentStream.endText();

        yPosition -= 20;

        // Bank details
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText("Bank Details:");
        contentStream.endText();

        yPosition -= 15;

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText(BANK_DETAILS);
        contentStream.endText();

        yPosition -= 20;

        // Notes
        if (invoice.getNotes() != null && !invoice.getNotes().isEmpty()) {
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Notes:");
            contentStream.endText();

            yPosition -= 15;

            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText(invoice.getNotes());
            contentStream.endText();

            yPosition -= 20;
        }

        // Thank you message
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD_OBLIQUE), 12);
        contentStream.newLineAtOffset(200, yPosition);
        contentStream.showText("Thank you for your business!");
        contentStream.endText();
    }
}