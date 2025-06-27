package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.model.Qitem;
import com.example.demo.model.Quat;
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
public class QuotationPdfService {

    @Autowired
    private QuatService quatService;

    @Autowired
    private QitemService qitemService;

    // Company Information
    private static final String COMPANY_NAME = "TechSolutions Pvt Ltd";
    private static final String COMPANY_ADDRESS = "123 Business Park, Coimbatore - 641001, Tamil Nadu";
    private static final String COMPANY_EMAIL = "sales@techsolutions.com";
    private static final String COMPANY_PHONE = "+91 9876543210";
    private static final String COMPANY_WEBSITE = "www.techsolutions.com";
    private static final String COMPANY_GSTIN = "33AAAAA0000A1Z5";

    public byte[] generateQuotationPdf(Long quatId) throws IOException {
        // Validate and fetch data
        Quat quat = quatService.getQuatById(quatId)
                .orElseThrow(() -> new RuntimeException("Quotation not found with ID: " + quatId));

        Customer customer = quat.getCustomer();
        if (customer == null) {
            throw new RuntimeException("Customer not found for quotation ID: " + quatId);
        }

        List<Qitem> quotationItems = qitemService.getQitemsByQuotationId(quatId);

        // Create PDF document
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float yPosition = page.getMediaBox().getHeight() - 50;

                // Add company header
                yPosition = addCompanyHeader(contentStream, yPosition);
                yPosition -= 30;

                // Add quotation title
                yPosition = addQuotationTitle(contentStream, yPosition);
                yPosition -= 20;

                // Add quotation and customer details
                yPosition = addQuotationAndCustomerDetails(contentStream, quat, customer, yPosition);
                yPosition -= 30;

                // Add items table
                yPosition = addItemsTable(contentStream, quotationItems, yPosition);
                yPosition -= 20;

                // Add subtotal (no tax/discount for quotation)
                yPosition = addQuotationTotals(contentStream, quotationItems, yPosition);
                yPosition -= 30;

                // Add footer
                addQuotationFooter(contentStream, quat, yPosition);
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

    private float addQuotationTitle(PDPageContentStream contentStream, float yPosition) throws IOException {
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 24);
        contentStream.newLineAtOffset(250, yPosition);
        contentStream.showText("QUOTATION");
        contentStream.endText();

        return yPosition;
    }

    private float addQuotationAndCustomerDetails(PDPageContentStream contentStream, Quat quat, 
                                               Customer customer, float yPosition) throws IOException {
        // Left side - Quotation details
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText("Quotation Details:");
        contentStream.endText();

        yPosition -= 20;

        String[] quotationDetails = {
            "Quotation No: " + quat.getQuatno(),
            "Date: " + quat.getQuatDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")),
            "Valid Until: " + quat.getQuatDate().plusDays(quat.getValidity()).format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")),
            "Prepared By: " + (quat.getUser() != null ? quat.getUser().getFirstName() + " " + quat.getUser().getLastName() : "Sales Team")
        };

        for (String detail : quotationDetails) {
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText(detail);
            contentStream.endText();
            yPosition -= 15;
        }

        // Right side - Customer details
        float rightColumnX = 320;
        yPosition += (quotationDetails.length * 15) + 20; // Reset to top

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

    private float addItemsTable(PDPageContentStream contentStream, List<Qitem> qitems, float yPosition) throws IOException {
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
        for (Qitem qitem : qitems) {
            String itemName = qitem.getItem() != null ? qitem.getItem().getItemname() : "Unknown Item";
            String licenseType = qitem.getLicenseType() != null ? qitem.getLicenseType() : "Standard";
            BigDecimal unitPrice = qitem.getUnitPrice() != null ? qitem.getUnitPrice() : BigDecimal.ZERO;
            BigDecimal itemTotal = qitem.getTotalPrice() != null ? qitem.getTotalPrice() : BigDecimal.ZERO;

            String[] rowData = {
                String.valueOf(rowNum++),
                itemName,
                licenseType,
                String.valueOf(qitem.getQuantity()),
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

    private float addQuotationTotals(PDPageContentStream contentStream, List<Qitem> qitems, float yPosition) throws IOException {
        BigDecimal subtotal = qitems.stream()
                .map(qitem -> qitem.getTotalPrice() != null ? qitem.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total box
        float boxWidth = 200;
        float boxHeight = 40;
        float boxX = 345;

        contentStream.setNonStrokingColor(new Color(240, 240, 240));
        contentStream.addRect(boxX, yPosition - boxHeight, boxWidth, boxHeight);
        contentStream.fill();

        contentStream.setStrokingColor(Color.BLACK);
        contentStream.addRect(boxX, yPosition - boxHeight, boxWidth, boxHeight);
        contentStream.stroke();

        contentStream.setNonStrokingColor(Color.BLACK);

        // Subtotal label
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.newLineAtOffset(boxX + 10, yPosition - 20);
        contentStream.showText("Total Amount:");
        contentStream.endText();

        // Subtotal amount
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.newLineAtOffset(boxX + 120, yPosition - 20);
        contentStream.showText(String.format("₹%.2f", subtotal));
        contentStream.endText();

        return yPosition - boxHeight;
    }

    private void addQuotationFooter(PDPageContentStream contentStream, Quat quat, float yPosition) throws IOException {
        yPosition -= 20;

        // Terms and conditions
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText("Terms & Conditions:");
        contentStream.endText();

        yPosition -= 15;

        String[] terms = {
            "1. This quotation is valid for " + quat.getValidity() + " days from the date of issue.",
            "2. Prices are subject to change without prior notice.",
            "3. Payment terms: 50% advance, 50% before delivery.",
            "4. Delivery within 15 working days after order confirmation.",
            "5. All disputes subject to Coimbatore jurisdiction only."
        };

        for (String term : terms) {
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText(term);
            contentStream.endText();
            yPosition -= 12;
        }

        yPosition -= 20;

        // Thank you message
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD_OBLIQUE), 12);
        contentStream.newLineAtOffset(200, yPosition);
        contentStream.showText("Thank you for your business!");
        contentStream.endText();
    }
}