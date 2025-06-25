# Postman Testing Guide - Spring Boot JWT Application

## Overview
This guide provides comprehensive Postman testing scenarios for the Spring Boot application with JWT authentication.

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
    "email": "admin@example.com",
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
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "firstName": "Test",
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

### 1.4 Login (Regular User)
```
POST {{baseUrl}}/api/auth/login
```

**Body (JSON):**
```json
{
    "username": "testuser",
    "password": "password123"
}
```

### 1.5 Get Current User Info
```
GET {{baseUrl}}/api/auth/user
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

**Expected Response:** `200 OK`
```json
{
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "firstName": "Admin",
    "lastName": "User",
    "userType": "ADMIN",
    "isActive": true
}
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
    "customername": "John Doe",
    "emailid": "john.doe@example.com",
    "mobilenumber": 9876543210,
    "companyname": "ABC Corp",
    "address": "123 Main St, City, State 12345",
    "refferedby": "Website",
    "userno": 1
}
```

**Test Script (to save customer ID):**
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
    "customername": "John Doe Updated",
    "emailid": "john.doe.updated@example.com",
    "mobilenumber": 9876543211,
    "companyname": "ABC Corp Updated",
    "address": "456 Updated St, City, State 12345",
    "refferedby": "Referral",
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
    "idname": "ITEM001",
    "licensetype": "Standard",
    "price": 1500.00,
    "createdby": "admin",
    "itemname": "Software License",
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

### 3.2 Get All Items
```
GET {{baseUrl}}/api/items
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

### 3.3 Get Item by ID
```
GET {{baseUrl}}/api/items/{{itemId}}
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

### 3.4 Update Item
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
    "idname": "ITEM001-UPDATED",
    "licensetype": "Premium",
    "price": 2000.00,
    "itemname": "Premium Software License",
    "updatedby": "admin",
    "isactive": true
}
```

### 3.5 Delete Item
```
DELETE {{baseUrl}}/api/items/{{itemId}}
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
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
    "quatno": "QT001",
    "quatDate": "2024-01-15T10:30:00",
    "validity": 30,
    "customer": {
        "id": {{customerId}}
    },
    "user": {
        "id": {{userId}}
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

### 4.5 Update Quotation
```
PUT {{baseUrl}}/api/quat/{{quatId}}
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "quatno": "QT001-UPDATED",
    "quatDate": "2024-01-16T10:30:00",
    "validity": 45,
    "customer": {
        "id": {{customerId}}
    },
    "user": {
        "id": {{userId}}
    }
}
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
    "quantity": 5,
    "unitPrice": 1500.00,
    "licenseType": "Standard",
    "quotation": {
        "id": {{quatId}}
    },
    "item": {
        "id": {{itemId}}
    }
}
```

### 5.2 Get All Quotation Items
```
GET {{baseUrl}}/api/qitem
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

### 5.3 Get Quotation Items by Quotation ID
```
GET {{baseUrl}}/api/qitem/quotation/{{quatId}}
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

## 6. Invoice Generation

### 6.1 Generate Invoice PDF
```
GET {{baseUrl}}/api/invoices/generate/{{quatId}}
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

**Expected Response:** PDF file download

### 6.2 Generate Invoice PDF (Alternative endpoint)
```
GET {{baseUrl}}/api/quat/{{quatId}}/invoice
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

## 7. User Management (Admin Only)

### 7.1 Get All Users
```
GET {{baseUrl}}/api/logins
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

**Note:** Only accessible by ADMIN role

### 7.2 Get User by ID
```
GET {{baseUrl}}/api/logins/1
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```

### 7.3 Create User (Admin)
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
    "username": "newuser",
    "password": "password123",
    "email": "newuser@example.com",
    "firstName": "New",
    "lastName": "User",
    "userType": "USER",
    "isActive": true
}
```

## 8. Error Testing Scenarios

### 8.1 Test Unauthorized Access
```
GET {{baseUrl}}/api/customer
```

**Headers:** (No Authorization header)

**Expected Response:** `401 Unauthorized`

### 8.2 Test Invalid JWT Token
```
GET {{baseUrl}}/api/customer
```

**Headers:**
```
Authorization: Bearer invalid_token_here
```

**Expected Response:** `401 Unauthorized`

### 8.3 Test Role-Based Access (USER trying to access ADMIN endpoint)
```
GET {{baseUrl}}/api/logins
```

**Headers:**
```
Authorization: Bearer {{jwtToken}}
```
(Use USER role token)

**Expected Response:** `403 Forbidden`

### 8.4 Test Invalid Login Credentials
```
POST {{baseUrl}}/api/auth/login
```

**Body (JSON):**
```json
{
    "username": "wronguser",
    "password": "wrongpassword"
}
```

**Expected Response:** `401 Unauthorized`

## 9. Collection Tests

### Pre-request Script (Collection Level)
```javascript
// Set base URL if not already set
if (!pm.environment.get("baseUrl")) {
    pm.environment.set("baseUrl", "http://localhost:8080");
}
```

### Test Script Templates

#### For Login Requests:
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has JWT token", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('jwtToken');
    pm.expect(jsonData).to.have.property('username');
    
    // Save token for future requests
    pm.environment.set("jwtToken", jsonData.jwtToken);
});
```

#### For Protected Endpoints:
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response time is less than 2000ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(2000);
});

pm.test("Content-Type is application/json", function () {
    pm.expect(pm.response.headers.get("Content-Type")).to.include("application/json");
});
```

#### For Create Operations:
```javascript
pm.test("Status code is 201", function () {
    pm.response.to.have.status(201);
});

pm.test("Response has ID", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('id');
    pm.expect(jsonData.id).to.be.a('number');
});
```

## 10. Testing Workflow

### Recommended Testing Order:
1. **Authentication Setup**
   - Register Admin user
   - Register Regular user
   - Login as Admin (save token)

2. **Basic CRUD Operations**
   - Create Customer
   - Create Item
   - Create Quotation
   - Create Quotation Item

3. **Read Operations**
   - Get all customers, items, quotations
   - Get specific records by ID

4. **Update Operations**
   - Update customer, item, quotation

5. **Invoice Generation**
   - Generate PDF invoice

6. **Admin Operations**
   - Test user management endpoints

7. **Security Testing**
   - Test unauthorized access
   - Test role-based access
   - Test with invalid tokens

### Environment Setup for Different Environments:

#### Development Environment
```json
{
  "baseUrl": "http://localhost:8080",
  "jwtToken": "",
  "customerId": "",
  "itemId": "",
  "quatId": "",
  "userId": ""
}
```

#### Production Environment
```json
{
  "baseUrl": "https://your-production-url.com",
  "jwtToken": "",
  "customerId": "",
  "itemId": "",
  "quatId": "",
  "userId": ""
}
```

## Notes:
- Always ensure the JWT token is valid and not expired
- Admin users can access all endpoints
- Regular users cannot access `/api/logins/**` endpoints
- PDF responses will trigger file downloads in Postman
- Use environment variables to maintain data consistency across requests
- Test both success and error scenarios for comprehensive coverage