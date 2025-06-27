# Complete Postman Testing Guide - Spring Boot JWT Application with Invoice System

## Overview
This comprehensive guide provides Postman testing scenarios for the Spring Boot application with JWT authentication, including the new Invoice system with separate Quotation and Invoice PDF generation.

## Base URL
```
http://localhost:8080
```

## Environment Variables Setup

Create these environment variables in Postman:

| Variable Name | Initial Value | Current Value |
|---------------|---------------|---------------|
| `baseUrl` | `http://localhost:8080` | `http://localhost:8080` |
| `jwtToken` | | (will be set automatically) |
| `customerId` | | (will be set from responses) |
| `itemId` | | (will be set from responses) |
| `quatId` | | (will be set from responses) |
| `userId` | | (will be set from responses) |
| `invoiceId` | | (will be set from responses) |

## 1. Authentication Endpoints

### 1.1 Register New User (Admin)
```
POST {{baseUrl}}/api/auth/register
```

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "username": "admin",
    "password": "admin123",
    "email": "admin@techsolutions.com",
    "firstName": "Admin",
    "lastName": "User",
    "userType": "ADMIN"
}
```

**Expected Response:** `200 OK`
```json
"User registered successfully with username: admin"
```

### 1.2 Register New User (Regular User)
```
POST {{baseUrl}}/api/auth/register
```

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "username": "salesuser",
    "password": "password123",
    "email": "sales@techsolutions.com",
    "firstName": "Sales",
    "lastName": "User",
    "userType": "USER"
}
```

### 1.3 Login (Admin)
```
POST {{baseUrl}}/api/auth/login
```

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "username": "admin",
    "password": "admin123"
}
```

**Expected Response:** `200 OK`
```json
{
    "jwtToken": "eyJhbGciOiJIUzUxMiJ9...",
    "username": "admin"
}
```

**Test Script (to save token):**
```javascript
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set("jwtToken", response.jwtToken);
    console.log("JWT Token saved:", response.jwtToken);
}
```

### 1.4 Get Current User Info
```
GET {{baseUrl}}/api/auth/user
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

## 2. Customer Management

### 2.1 Create Customer
```
POST {{baseUrl}}/api/customer
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "customername": "Acme Corporation",
    "emailid": "contact@acmecorp.com",
    "mobilenumber": 9876543210,
    "companyname": "Acme Corp Pvt Ltd",
    "address": "123 Business District, Coimbatore - 641001, Tamil Nadu",
    "refferedby": "Website",
    "userno": 1
}
```

**Test Script:**
```javascript
if (pm.response.code === 201) {
    const response = pm.response.json();
    pm.environment.set("customerId", response.id);
    console.log("Customer ID saved:", response.id);
}
```

### 2.2 Get All Customers
```
GET {{baseUrl}}/api/customer
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

### 2.3 Get Customer by ID
```
GET {{baseUrl}}/api/customer/{{customerId}}
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

### 2.4 Update Customer
```
PUT {{baseUrl}}/api/customer/{{customerId}}
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "customername": "Acme Corporation Ltd",
    "emailid": "info@acmecorp.com",
    "mobilenumber": 9876543211,
    "companyname": "Acme Corp Pvt Ltd",
    "address": "456 Updated Business Park, Coimbatore - 641001, Tamil Nadu",
    "refferedby": "Partner Referral",
    "userno": 1
}
```

### 2.5 Delete Customer
```
DELETE {{baseUrl}}/api/customer/{{customerId}}
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

## 3. Item Management

### 3.1 Create Item
```
POST {{baseUrl}}/api/items
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "idname": "SOFT001",
    "licensetype": "Enterprise",
    "price": 25000.00,
    "createdby": "admin",
    "itemname": "Enterprise Software License",
    "isactive": true
}
```

**Test Script:**
```javascript
if (pm.response.code === 201) {
    const response = pm.response.json();
    pm.environment.set("itemId", response.id);
    console.log("Item ID saved:", response.id);
}
```

### 3.2 Create Another Item
```
POST {{baseUrl}}/api/items
```

**Body (JSON):**
```json
{
    "idname": "SUPP001",
    "licensetype": "Annual",
    "price": 5000.00,
    "createdby": "admin",
    "itemname": "Annual Support Package",
    "isactive": true
}
```

### 3.3 Get All Items
```
GET {{baseUrl}}/api/items
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

### 3.4 Get Item by ID
```
GET {{baseUrl}}/api/items/{{itemId}}
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

### 3.5 Update Item
```
PUT {{baseUrl}}/api/items/{{itemId}}
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "idname": "SOFT001-V2",
    "licensetype": "Enterprise Plus",
    "price": 30000.00,
    "itemname": "Enterprise Plus Software License",
    "updatedby": "admin",
    "isactive": true
}
```

## 4. Quotation Management

### 4.1 Create Quotation
```
POST {{baseUrl}}/api/quat
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "quatno": "QT-2024-001",
    "quatDate": "2024-01-15T10:30:00",
    "validity": 30,
    "customer": {
        "id": {{customerId}}
    },
    "user": {
        "id": 1
    }
}
```

**Test Script:**
```javascript
if (pm.response.code === 201) {
    const response = pm.response.json();
    pm.environment.set("quatId", response.id);
    console.log("Quotation ID saved:", response.id);
}
```

### 4.2 Get All Quotations
```
GET {{baseUrl}}/api/quat
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

### 4.3 Get Quotation by ID
```
GET {{baseUrl}}/api/quat/{{quatId}}
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

### 4.4 Get Quotations by Customer ID
```
GET {{baseUrl}}/api/quat/customer/{{customerId}}
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

## 5. Quotation Items Management

### 5.1 Create Quotation Item
```
POST {{baseUrl}}/api/qitem
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "quantity": 2,
    "unitPrice": 25000.00,
    "licenseType": "Enterprise",
    "quotation": {
        "id": {{quatId}}
    },
    "item": {
        "id": {{itemId}}
    }
}
```

### 5.2 Create Another Quotation Item
```
POST {{baseUrl}}/api/qitem
```

**Body (JSON):**
```json
{
    "quantity": 1,
    "unitPrice": 5000.00,
    "licenseType": "Annual",
    "quotation": {
        "id": {{quatId}}
    },
    "item": {
        "id": 2
    }
}
```

### 5.3 Get All Quotation Items
```
GET {{baseUrl}}/api/qitem
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

### 5.4 Get Quotation Items by Quotation ID
```
GET {{baseUrl}}/api/qitem/quotation/{{quatId}}
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

## 6. PDF Generation

### 6.1 Generate Quotation PDF (No Tax/Discount)
```
GET {{baseUrl}}/api/quat/{{quatId}}/quotation-pdf
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

**Expected Response:** PDF file download (Quotation format without tax/discount)

## 7. Invoice Management

### 7.1 Create Invoice from Quotation
```
POST {{baseUrl}}/api/invoices/from-quotation/{{quatId}}
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

**Query Parameters:**
- `taxRate`: 18.0 (optional, default 18%)
- `discountRate`: 5.0 (optional, default 0%)
- `paymentTerms`: Net 30 days (optional)
- `notes`: Thank you for your business (optional)

**Full URL Example:**
```
POST {{baseUrl}}/api/invoices/from-quotation/{{quatId}}?taxRate=18.0&discountRate=5.0&paymentTerms=Net 30 days&notes=Thank you for choosing our services
```

**Test Script:**
```javascript
if (pm.response.code === 201) {
    const response = pm.response.json();
    pm.environment.set("invoiceId", response.id);
    console.log("Invoice ID saved:", response.id);
}
```

### 7.2 Create Invoice Manually
```
POST {{baseUrl}}/api/invoices
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "invoiceNo": "INV-2024-001",
    "invoiceDate": "2024-01-20T10:30:00",
    "dueDate": "2024-02-20T10:30:00",
    "taxRate": 18.0,
    "discountRate": 10.0,
    "paymentTerms": "Net 30 days",
    "notes": "Thank you for your business. Please pay within 30 days.",
    "status": "SENT",
    "quotation": {
        "id": {{quatId}}
    }
}
```

### 7.3 Get All Invoices
```
GET {{baseUrl}}/api/invoices
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

### 7.4 Get Invoice by ID
```
GET {{baseUrl}}/api/invoices/{{invoiceId}}
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

### 7.5 Get Invoices by Customer ID
```
GET {{baseUrl}}/api/invoices/customer/{{customerId}}
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

### 7.6 Get Invoices by Status
```
GET {{baseUrl}}/api/invoices/status/DRAFT
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

**Available Statuses:** DRAFT, SENT, PAID, OVERDUE, CANCELLED

### 7.7 Get Invoice by Quotation ID
```
GET {{baseUrl}}/api/invoices/quotation/{{quatId}}
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

### 7.8 Update Invoice
```
PUT {{baseUrl}}/api/invoices/{{invoiceId}}
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "status": "PAID",
    "notes": "Payment received. Thank you!",
    "updatedBy": "admin"
}
```

### 7.9 Generate Invoice PDF (With Tax/Discount)
```
GET {{baseUrl}}/api/invoices/{{invoiceId}}/pdf
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

**Expected Response:** PDF file download (Invoice format with tax/discount calculations)

### 7.10 Delete Invoice
```
DELETE {{baseUrl}}/api/invoices/{{invoiceId}}
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

## 8. User Management (Admin Only)

### 8.1 Get All Users
```
GET {{baseUrl}}/api/logins
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

### 8.2 Get User by ID
```
GET {{baseUrl}}/api/logins/1
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

### 8.3 Create User (Admin)
```
POST {{baseUrl}}/api/logins
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "username": "manager",
    "password": "manager123",
    "email": "manager@techsolutions.com",
    "firstName": "Sales",
    "lastName": "Manager",
    "userType": "USER",
    "isActive": true
}
```

## 9. Error Testing Scenarios

### 9.1 Test Unauthorized Access
```
GET {{baseUrl}}/api/customer
```

**Headers:** (No Authorization header)

**Expected Response:** `401 Unauthorized`

### 9.2 Test Invalid JWT Token
```
GET {{baseUrl}}/api/customer
```

**Headers:**
```
Authorization: Bearer invalid_token_here
```

**Expected Response:** `401 Unauthorized`

### 9.3 Test Duplicate Invoice Creation
```
POST {{baseUrl}}/api/invoices/from-quotation/{{quatId}}
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

**Expected Response:** `400 Bad Request` (Invoice already exists for quotation)

### 9.4 Test Invalid Quotation ID for Invoice
```
POST {{baseUrl}}/api/invoices/from-quotation/99999
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

**Expected Response:** `400 Bad Request`

## 10. Complete Testing Workflow

### Phase 1: Setup and Authentication
1. Register Admin user
2. Register Regular user
3. Login as Admin (save token)

### Phase 2: Master Data Creation
1. Create Customer (save ID)
2. Create Items (save IDs)

### Phase 3: Quotation Process
1. Create Quotation (save ID)
2. Add Quotation Items
3. Generate Quotation PDF (no tax/discount)

### Phase 4: Invoice Process
1. Create Invoice from Quotation
2. Generate Invoice PDF (with tax/discount)
3. Update Invoice status

### Phase 5: Data Retrieval
1. Get all records for each entity
2. Get records by relationships (customer invoices, etc.)
3. Test filtering by status

### Phase 6: Security Testing
1. Test unauthorized access
2. Test role-based access
3. Test with invalid tokens

## 11. Test Scripts for Automation

### Collection Pre-request Script:
```javascript
// Set base URL if not already set
if (!pm.environment.get("baseUrl")) {
    pm.environment.set("baseUrl", "http://localhost:8080");
}

// Add timestamp for unique data
pm.globals.set("timestamp", Date.now());
```

### Generic Success Test:
```javascript
pm.test("Status code is success", function () {
    pm.expect(pm.response.code).to.be.oneOf([200, 201]);
});

pm.test("Response time is less than 2000ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(2000);
});

pm.test("Content-Type is application/json", function () {
    pm.expect(pm.response.headers.get("Content-Type")).to.include("application/json");
});
```

### PDF Download Test:
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Content-Type is PDF", function () {
    pm.expect(pm.response.headers.get("Content-Type")).to.include("application/pdf");
});

pm.test("Response has PDF content", function () {
    pm.expect(pm.response.responseSize).to.be.above(1000);
});
```

## 12. Key Differences Between Quotation and Invoice PDFs

### Quotation PDF Features:
- Simple total calculation (no tax/discount)
- Validity period display
- Terms & conditions focused on quotation
- Professional quotation format

### Invoice PDF Features:
- Detailed tax calculations (GST)
- Discount calculations
- Payment terms and bank details
- Invoice-specific status and due dates
- Professional invoice format with color coding

## 13. Environment Variables for Different Stages

### Development Environment:
```json
{
  "baseUrl": "http://localhost:8080",
  "jwtToken": "",
  "customerId": "",
  "itemId": "",
  "quatId": "",
  "userId": "",
  "invoiceId": ""
}
```

### Testing Environment:
```json
{
  "baseUrl": "http://test-server:8080",
  "jwtToken": "",
  "customerId": "",
  "itemId": "",
  "quatId": "",
  "userId": "",
  "invoiceId": ""
}
```

This comprehensive testing guide covers all aspects of the application including the new Invoice system with professional PDF generation using Apache PDFBox.