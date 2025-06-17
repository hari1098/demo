package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.model.Item;
import com.example.demo.model.Qitem;
import com.example.demo.model.Quat;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoicePdfService {

    @Autowired
    private QuatService quatService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private QitemService qitemService;

    @Autowired
    private ItemService itemService;

    // Company Information (configurable)
    private static final String COMPANY_NAME = "Your Company Name";
    private static final String COMPANY_ADDRESS = "123 Business Street, Coimbatore - 641001";
    private static final String COMPANY_EMAIL = "sales@yourcompany.com";
    private static final String COMPANY_PHONE = "+91 9876543210";
    private static final String COMPANY_WEBSITE = "www.yourcompany.com";
    private static final String COMPANY_GSTIN = "22AAAAA0000A1Z5";
    private static final String COMPANY_LOGO_PATH = "src/main/resources/static/images/logo.png";

    private static final float TAX_RATE = 0.18f;

    public byte[] generateInvoicePdf(int quatId) throws DocumentException, IOException {
        // Validate quatId
        if (quatId <= 0) {
            throw new IllegalArgumentException("Invalid quotation ID");
        }

        // Fetch quotation data
        Quat quat = quatService.getQuatById(quatId)
                .orElseThrow(() -> new RuntimeException("Quotation not found with ID: " + quatId));

        // Fetch customer data
        Customer customer = customerService.getCustomerById(quat.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found for quotation ID: " + quatId));

        // Fetch relevant items
        List<Qitem> relevantQitems = getRelevantQitems(quat);

        // Create PDF document
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 60, 36); // Margins: left, right, top, bottom
        PdfWriter.getInstance(document, baos);
        document.open();

        // Font definitions
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, new BaseColor(0, 51, 102));
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font smallFont = new Font(Font.FontFamily.HELVETICA, 8);
        Font footerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC);

        // Add company header
        addCompanyHeader(document, titleFont, normalFont, quat);

        // Add quotation details
        addQuotationDetails(document, boldFont, normalFont, quat);

        // Add customer details
        addCustomerDetails(document, boldFont, normalFont, customer);

        // Add items table
        BigDecimal[] totals = addItemsTable(document, headerFont, normalFont, boldFont, relevantQitems);

        // Add totals section
        addTotalsSection(document, boldFont, normalFont, totals[0], totals[1]);

        // Add footer
        addFooter(document, footerFont, quat);

        document.close();
        return baos.toByteArray();
    }

    private List<Qitem> getRelevantQitems(Quat quat) {
        List<Qitem> allQitems = qitemService.getQitems();

        // Filter Qitems related to this quotation
        List<Qitem> relevantQitems = allQitems.stream()
                .filter(q -> q.getQitem().contains(quat.getQuatno())) // Adjust this filter as needed
                .collect(Collectors.toList());

        if (relevantQitems.isEmpty()) {
            // Fallback to all items if no specific ones found
            System.out.println("No specific Qitems found for quat " + quat.getQuatno() + ". Including all items.");
            relevantQitems = allQitems;
        }

        return relevantQitems;
    }

    private void addCompanyHeader(Document document, Font titleFont, Font normalFont, Quat quat) throws DocumentException {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{30, 70});
        headerTable.setSpacingAfter(20);

        // Left column - Logo
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        try {
            Image logo = Image.getInstance(COMPANY_LOGO_PATH);
            logo.scaleToFit(100, 100);
            logoCell.addElement(logo);
        } catch (Exception e) {
            logoCell.addElement(new Paragraph(COMPANY_NAME, titleFont));
        }
        headerTable.addCell(logoCell);

        // Right column - Company info
        PdfPCell infoCell = new PdfPCell();
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Paragraph companyInfo = new Paragraph();
        companyInfo.add(new Chunk(COMPANY_NAME + "\n", titleFont));
        companyInfo.add(new Chunk(COMPANY_ADDRESS + "\n", normalFont));
        companyInfo.add(new Chunk("GSTIN: " + COMPANY_GSTIN + "\n", normalFont));
        companyInfo.add(new Chunk("Email: " + COMPANY_EMAIL + "\n", normalFont));
        companyInfo.add(new Chunk("Phone: " + COMPANY_PHONE + "\n", normalFont));
        companyInfo.add(new Chunk("Website: " + COMPANY_WEBSITE + "\n", normalFont));

        infoCell.addElement(companyInfo);
        headerTable.addCell(infoCell);

        document.add(headerTable);
    }

    private void addQuotationDetails(Document document, Font boldFont, Font normalFont, Quat quat) throws DocumentException {
        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.setWidths(new float[]{50, 50});
        detailsTable.setSpacingAfter(15);

        // Left column - Quotation info
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);

        Paragraph leftPara = new Paragraph();
        leftPara.add(new Chunk("QUOTATION\n", boldFont));
        leftPara.add(new Chunk("No: " + quat.getQuatno() + "\n", normalFont));
        leftPara.add(new Chunk("Date: " + quat.getQuatDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) + "\n", normalFont));
        leftPara.add(new Chunk("Valid Until: " + quat.getQuatDate().plusDays(quat.getValidity())
                .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")), normalFont));

        leftCell.addElement(leftPara);
        detailsTable.addCell(leftCell);

        // Right column - Additional info
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Paragraph rightPara = new Paragraph();
        rightPara.add(new Chunk("Prepared By: Sales Team\n", normalFont));
        rightPara.add(new Chunk("Reference: " + (quat.getCustomer() != null ? quat.getCustomer().getRefferedby() : "N/A") + "\n", normalFont));

        rightCell.addElement(rightPara);
        detailsTable.addCell(rightCell);

        document.add(detailsTable);
    }

    private void addCustomerDetails(Document document, Font boldFont, Font normalFont, Customer customer) throws DocumentException {
        PdfPTable customerTable = new PdfPTable(2);
        customerTable.setWidthPercentage(100);
        customerTable.setWidths(new float[]{50, 50});
        customerTable.setSpacingAfter(15);

        // Bill To section
        PdfPCell billToCell = new PdfPCell();
        billToCell.setBorder(Rectangle.NO_BORDER);

        Paragraph billToPara = new Paragraph();
        billToPara.add(new Chunk("Bill To:\n", boldFont));
        billToPara.add(new Chunk(customer.getCustomername() + "\n", normalFont));
        billToPara.add(new Chunk(customer.getCompanyname() + "\n", normalFont));
        billToPara.add(new Chunk(customer.getAddress() + "\n", normalFont));
        billToPara.add(new Chunk("Email: " + customer.getEmailid() + "\n", normalFont));
        billToPara.add(new Chunk("Phone: " + customer.getMobilenumber(), normalFont));

        billToCell.addElement(billToPara);
        customerTable.addCell(billToCell);

        // Ship To section (same as Bill To in this implementation)
        PdfPCell shipToCell = new PdfPCell();
        shipToCell.setBorder(Rectangle.NO_BORDER);

        Paragraph shipToPara = new Paragraph();
        shipToPara.add(new Chunk("Ship To:\n", boldFont));
        shipToPara.add(new Chunk(customer.getCustomername() + "\n", normalFont));
        shipToPara.add(new Chunk(customer.getCompanyname() + "\n", normalFont));
        shipToPara.add(new Chunk(customer.getAddress() + "\n", normalFont));
        shipToPara.add(new Chunk("Contact: " + customer.getMobilenumber(), normalFont));

        shipToCell.addElement(shipToPara);
        customerTable.addCell(shipToCell);

        document.add(customerTable);
        document.add(Chunk.NEWLINE);
    }

    private BigDecimal[] addItemsTable(Document document, Font headerFont, Font normalFont, Font boldFont, List<Qitem> qitems) throws DocumentException {
        PdfPTable itemTable = new PdfPTable(6);
        itemTable.setWidthPercentage(100);
        itemTable.setSpacingBefore(10);
        itemTable.setSpacingAfter(20);

        // Dynamic column widths - adjust based on content
        float[] columnWidths = {0.8f, 3.5f, 1f, 1.2f, 1f, 1.5f};
        itemTable.setWidths(columnWidths);

        // Table headers
        String[] headers = {"#", "Description", "License", "Qty", "Unit Price", "Amount"};
        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
            headerCell.setBackgroundColor(new BaseColor(0, 51, 102));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerCell.setPadding(5);
            itemTable.addCell(headerCell);
        }

        BigDecimal subTotal = BigDecimal.ZERO;
        int rowNum = 1;

        for (Qitem qitem : qitems) {
            Optional<Item> optionalItem = itemService.getItemById((long) qitem.getItemId());
            Item item = optionalItem.orElse(null);

            String itemName = item != null ? item.getItemname() : qitem.getQitem();
            String licenseType = qitem.getLicenseType() != null ? qitem.getLicenseType() : "Standard";
            BigDecimal unitPrice = item != null && item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(qitem.getQty()));
            subTotal = subTotal.add(itemTotal);

            // Row number
            itemTable.addCell(createCell(String.valueOf(rowNum++), normalFont, Element.ALIGN_CENTER, false));

            // Description
            itemTable.addCell(createCell(itemName, normalFont, Element.ALIGN_LEFT, false));

            // License type
            itemTable.addCell(createCell(licenseType, normalFont, Element.ALIGN_CENTER, false));

            // Quantity
            itemTable.addCell(createCell(String.valueOf(qitem.getQty()), normalFont, Element.ALIGN_CENTER, false));

            // Unit price
            itemTable.addCell(createCell(String.format("₹%.2f", unitPrice), normalFont, Element.ALIGN_RIGHT, false));

            // Amount
            itemTable.addCell(createCell(String.format("₹%.2f", itemTotal), normalFont, Element.ALIGN_RIGHT, false));
        }

        document.add(itemTable);

        BigDecimal taxAmount = subTotal.multiply(BigDecimal.valueOf(TAX_RATE));
        return new BigDecimal[]{subTotal, taxAmount};
    }

    private void addTotalsSection(Document document, Font boldFont, Font normalFont, BigDecimal subTotal, BigDecimal taxAmount) throws DocumentException {
        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(40);
        totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalsTable.setSpacingAfter(20);
        totalsTable.setWidths(new float[]{60, 40});

        // Subtotal
        addTotalRow(totalsTable, "Subtotal:", String.format("₹%.2f", subTotal), normalFont, boldFont);

        // Tax
        addTotalRow(totalsTable, "Tax (" + (TAX_RATE * 100) + "%):", String.format("₹%.2f", taxAmount), normalFont, boldFont);

        // Grand Total
        addTotalRow(totalsTable, "Grand Total:", String.format("₹%.2f", subTotal.add(taxAmount)), boldFont, boldFont);

        document.add(totalsTable);
    }

    private void addFooter(Document document, Font footerFont, Quat quat) throws DocumentException {
        Paragraph footer = new Paragraph();
        footer.add(new Chunk("Notes:\n", footerFont));
        footer.add(new Chunk("1. This is a computer generated quotation and does not require signature.\n", footerFont));
        footer.add(new Chunk("2. Prices are valid for " + quat.getValidity() + " days from the date of issue.\n", footerFont));
        footer.add(new Chunk("3. Payment terms: 50% advance, 50% before delivery.\n", footerFont));
        footer.add(new Chunk("4. Delivery within 15 days after order confirmation.\n\n", footerFont));
        footer.add(new Chunk("Thank you for your business!", footerFont));

        document.add(footer);
    }

    private PdfPCell createCell(String content, Font font, int alignment, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        cell.setPaddingBottom(7);
        if (isHeader) {
            cell.setBackgroundColor(new BaseColor(0, 51, 102));
        } else {
            cell.setBorderColor(BaseColor.LIGHT_GRAY);
        }
        return cell;
    }

    private void addTotalRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPaddingRight(10);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }
}