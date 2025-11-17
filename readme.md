

# Pharmacy Inventory Management System (IMS)

## 📌 Project Overview

The Pharmacy Inventory Management System (IMS) is a Spring Boot + MySQL–based application designed to manage pharmacy operations efficiently.
It provides a structured way to track products, suppliers, purchases, sales, and inventory levels while ensuring accuracy through logging and validation mechanisms.

The project simulates a real-world pharmacy workflow with proper handling of product batches, supplier relationships, purchase/sales transactions, and stock adjustments.

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6+

### Installation
1. Clone the repository
2. Configure database connection in `src/main/resources/application.properties`
3. Run the application: `mvn spring-boot:run`
4. The API will be available at `http://localhost:8080`

### Default Configuration
- **Database**: MySQL (pharmatrack)
- **Port**: 8080
- **JWT Secret**: Configure via `JWT_SECRET` environment variable
- **CORS**: Enabled for `http://localhost:5173`

---

# 📚 API Documentation

## 🔐 Authentication

The API uses JWT (JSON Web Token) for authentication. All protected endpoints require a valid Bearer token in the Authorization header.

### Authentication Flow
1. **Login** → Get access token and refresh token
2. **Use access token** → Include in `Authorization: Bearer <token>` header
3. **Refresh token** → When access token expires, use refresh token to get new tokens
4. **Logout** → Blacklist current token

### Base URL
```
http://localhost:8080
```

---

## 🔑 Authentication Endpoints

### POST /auth/login
Authenticate user and receive JWT tokens.

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@pharmacy.com",
    "role": {
      "id": 1,
      "name": "ADMIN"
    }
  }
}
```

**Error Responses:**
- `400 Bad Request` - Invalid credentials
- `429 Too Many Requests` - Rate limit exceeded

### POST /auth/refresh
Refresh access token using refresh token.

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### POST /auth/logout
Logout user and blacklist tokens.

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response (200 OK):**
```json
{
  "message": "User logged out successfully!"
}
```

---

## 👥 User Management

### POST /api/v1/users/signup
Create a new user account.

**Headers:** `Authorization: Bearer <admin_token>`

**Request Body:**
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "roleId": 1
}
```

**Response (200 OK):**
```json
"User created successfully"
```

### GET /api/v1/users/me
Get current user information.

**Headers:** `Authorization: Bearer <token>`

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@pharmacy.com",
  "role": {
    "id": 1,
    "name": "ADMIN"
  }
}
```

### GET /api/v1/users
Get all users.

**Headers:** `Authorization: Bearer <admin_token>`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "username": "admin",
    "email": "admin@pharmacy.com",
    "role": {
      "id": 1,
      "name": "ADMIN"
    }
  }
]
```

### GET /api/v1/users/{id}
Get user by ID.

**Headers:** `Authorization: Bearer <admin_token>`

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@pharmacy.com",
  "role": {
    "id": 1,
    "name": "ADMIN"
  }
}
```

### PUT /api/v1/users/{id}
Update user information.

**Headers:** `Authorization: Bearer <admin_token>`

**Request Body:**
```json
{
  "username": "string",
  "email": "string",
  "roleId": 1
}
```

**Response (200 OK):**
```json
"Successfully updated profile"
```

---

## 🏷️ Category Management

### GET /api/v1/categories
Get all categories.

**Headers:** `Authorization: Bearer <admin_token>`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Pain Relief",
    "description": "Pain relief medications"
  }
]
```

### GET /api/v1/categories/{id}
Get category by ID.

**Headers:** `Authorization: Bearer <admin_token>`

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Pain Relief",
  "description": "Pain relief medications"
}
```

### POST /api/v1/categories/create
Create a new category.

**Headers:** `Authorization: Bearer <admin_token>`

**Request Body:**
```json
{
  "name": "string",
  "description": "string"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Pain Relief",
  "description": "Pain relief medications"
}
```

### PUT /api/v1/categories/{id}
Update category.

**Headers:** `Authorization: Bearer <admin_token>`

**Request Body:**
```json
{
  "name": "string",
  "description": "string"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Pain Relief",
  "description": "Pain relief medications"
}
```

---

## 💊 Product Management

### GET /api/v1/products
Get all products.

**Headers:** `Authorization: Bearer <admin_token>`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Paracetamol 500mg",
    "brand": "Generic",
    "manufacturer": "PharmaCorp",
    "dosageForm": "Tablet",
    "strength": "500mg",
    "minimumStock": 100,
    "drugClassification": "OTC",
    "description": "Pain relief medication",
    "barcode": "1234567890123",
    "category": {
      "id": 1,
      "name": "Pain Relief"
    }
  }
]
```

### GET /api/v1/products/{id}
Get product by ID.

**Headers:** `Authorization: Bearer <admin_token>`

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Paracetamol 500mg",
  "brand": "Generic",
  "manufacturer": "PharmaCorp",
  "dosageForm": "Tablet",
  "strength": "500mg",
  "minimumStock": 100,
  "drugClassification": "OTC",
  "description": "Pain relief medication",
  "barcode": "1234567890123",
  "category": {
    "id": 1,
    "name": "Pain Relief"
  }
}
```

### POST /api/v1/products/create
Create a new product.

**Headers:** `Authorization: Bearer <admin_token>`

**Request Body:**
```json
{
  "name": "string",
  "brand": "string",
  "manufacturer": "string",
  "dosageForm": "string",
  "strength": "string",
  "minimumStock": 100,
  "drugClassification": "OTC",
  "description": "string",
  "barcode": "string",
  "categoryId": 1
}
```

**Validation Rules:**
- `name`: Required, max 150 characters
- `brand`: Required, max 100 characters
- `manufacturer`: Required, max 100 characters
- `dosageForm`: Required, max 50 characters
- `strength`: Required, max 50 characters
- `minimumStock`: Required, minimum 0
- `drugClassification`: Required, enum (OTC, RX)
- `barcode`: Required, max 50 characters
- `categoryId`: Required

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Paracetamol 500mg",
  "brand": "Generic",
  "manufacturer": "PharmaCorp",
  "dosageForm": "Tablet",
  "strength": "500mg",
  "minimumStock": 100,
  "drugClassification": "OTC",
  "description": "Pain relief medication",
  "barcode": "1234567890123",
  "category": {
    "id": 1,
    "name": "Pain Relief"
  }
}
```

### PUT /api/v1/products/{id}
Update product.

**Headers:** `Authorization: Bearer <admin_token>`

**Request Body:** Same as create product

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Paracetamol 500mg",
  "brand": "Generic",
  "manufacturer": "PharmaCorp",
  "dosageForm": "Tablet",
  "strength": "500mg",
  "minimumStock": 100,
  "drugClassification": "OTC",
  "description": "Pain relief medication",
  "barcode": "1234567890123",
  "category": {
    "id": 1,
    "name": "Pain Relief"
  }
}
```

---

## 📦 Product Batch Management

### GET /api/v1/productBatches
Get all product batches.

**Headers:** `Authorization: Bearer <staff_token>`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "batchNumber": "BATCH001",
    "quantity": 1000,
    "expiryDate": "2024-12-31",
    "purchasePrice": 5.50,
    "status": "ACTIVE",
    "product": {
      "id": 1,
      "name": "Paracetamol 500mg"
    }
  }
]
```

### GET /api/v1/productBatches/{id}
Get product batch by ID.

**Headers:** `Authorization: Bearer <staff_token>`

**Response (200 OK):**
```json
{
  "id": 1,
  "batchNumber": "BATCH001",
  "quantity": 1000,
  "expiryDate": "2024-12-31",
  "purchasePrice": 5.50,
  "status": "ACTIVE",
  "product": {
    "id": 1,
    "name": "Paracetamol 500mg"
  }
}
```

### GET /api/v1/productBatches/{id}/batches
Get all batches for a specific product.

**Headers:** `Authorization: Bearer <staff_token>`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "batchNumber": "BATCH001",
    "quantity": 1000,
    "expiryDate": "2024-12-31",
    "purchasePrice": 5.50,
    "status": "ACTIVE",
    "product": {
      "id": 1,
      "name": "Paracetamol 500mg"
    }
  }
]
```

### GET /api/v1/productBatches/earliest
Get earliest batch for all products (FIFO).

**Headers:** `Authorization: Bearer <staff_token>`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "batchNumber": "BATCH001",
    "quantity": 1000,
    "expiryDate": "2024-12-31",
    "purchasePrice": 5.50,
    "status": "ACTIVE",
    "product": {
      "id": 1,
      "name": "Paracetamol 500mg"
    }
  }
]
```

### PUT /api/v1/productBatches/{id}
Update product batch.

**Headers:** `Authorization: Bearer <staff_token>`

**Request Body:**
```json
{
  "quantity": 950,
  "status": "ACTIVE"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "batchNumber": "BATCH001",
  "quantity": 950,
  "expiryDate": "2024-12-31",
  "purchasePrice": 5.50,
  "status": "ACTIVE",
  "product": {
    "id": 1,
    "name": "Paracetamol 500mg"
  }
}
```

### POST /api/v1/productBatches/check
Check if product batch exists.

**Headers:** `Authorization: Bearer <staff_token>`

**Request Body:**
```json
{
  "productId": 1,
  "batchNumber": "BATCH001"
}
```

**Response (200 OK):**
```json
{
  "exists": true
}
```

---

## 🏢 Supplier Management

### GET /api/v1/suppliers
Get all suppliers.

**Headers:** `Authorization: Bearer <admin_token>`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "PharmaCorp Ltd",
    "contactPerson": "John Doe",
    "email": "john@pharmacorp.com",
    "phone": "+1234567890",
    "address": "123 Pharma Street, City, Country"
  }
]
```

### GET /api/v1/suppliers/{id}
Get supplier by ID.

**Headers:** `Authorization: Bearer <admin_token>`

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "PharmaCorp Ltd",
  "contactPerson": "John Doe",
  "email": "john@pharmacorp.com",
  "phone": "+1234567890",
  "address": "123 Pharma Street, City, Country"
}
```

### POST /api/v1/suppliers/create
Create a new supplier.

**Headers:** `Authorization: Bearer <admin_token>`

**Request Body:**
```json
{
  "name": "string",
  "contactPerson": "string",
  "email": "string",
  "phone": "string",
  "address": "string"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "PharmaCorp Ltd",
  "contactPerson": "John Doe",
  "email": "john@pharmacorp.com",
  "phone": "+1234567890",
  "address": "123 Pharma Street, City, Country"
}
```

### PUT /api/v1/suppliers/{id}
Update supplier.

**Headers:** `Authorization: Bearer <admin_token>`

**Request Body:** Same as create supplier

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "PharmaCorp Ltd",
  "contactPerson": "John Doe",
  "email": "john@pharmacorp.com",
  "phone": "+1234567890",
  "address": "123 Pharma Street, City, Country"
}
```

---

## 👤 Customer Management

### GET /api/v1/customers
Get all customers.

**Headers:** `Authorization: Bearer <staff_token>`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Jane Smith",
    "email": "jane@email.com",
    "phone": "+1234567890",
    "address": "456 Customer Ave, City, Country",
    "isActive": true
  }
]
```

### GET /api/v1/customers/{id}
Get customer by ID.

**Headers:** `Authorization: Bearer <staff_token>`

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Jane Smith",
  "email": "jane@email.com",
  "phone": "+1234567890",
  "address": "456 Customer Ave, City, Country",
  "isActive": true
}
```

### GET /api/v1/customers/walkIn
Get walk-in customer.

**Headers:** `Authorization: Bearer <staff_token>`

**Response (200 OK):**
```json
{
  "id": 0,
  "name": "Walk-in Customer",
  "email": null,
  "phone": null,
  "address": null,
  "isActive": true
}
```

### GET /api/v1/customers/active
Get all active customers.

**Headers:** `Authorization: Bearer <staff_token>`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Jane Smith",
    "email": "jane@email.com",
    "phone": "+1234567890",
    "address": "456 Customer Ave, City, Country",
    "isActive": true
  }
]
```

### POST /api/v1/customers/create
Create a new customer.

**Headers:** `Authorization: Bearer <staff_token>`

**Request Body:**
```json
{
  "name": "string",
  "email": "string",
  "phone": "string",
  "address": "string"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Jane Smith",
  "email": "jane@email.com",
  "phone": "+1234567890",
  "address": "456 Customer Ave, City, Country",
  "isActive": true
}
```

### PUT /api/v1/customers/{id}
Update customer.

**Headers:** `Authorization: Bearer <staff_token>`

**Request Body:** Same as create customer

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Jane Smith",
  "email": "jane@email.com",
  "phone": "+1234567890",
  "address": "456 Customer Ave, City, Country",
  "isActive": true
}
```

### PUT /api/v1/customers/activate/{id}
Activate customer.

**Headers:** `Authorization: Bearer <staff_token>`

**Response (200 OK):**
```json
"Customer activated successfully"
```

### PUT /api/v1/customers/deactivate/{id}
Deactivate customer.

**Headers:** `Authorization: Bearer <staff_token>`

**Response (200 OK):**
```json
"Customer deactivated successfully"
```

---

## 🛒 Purchase Management

### GET /api/v1/purchases
Get all purchases.

**Headers:** `Authorization: Bearer <admin_token>`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "purchaseDate": "2024-01-15",
    "totalAmount": 550.00,
    "status": "CONFIRMED",
    "supplier": {
      "id": 1,
      "name": "PharmaCorp Ltd"
    },
    "items": [
      {
        "id": 1,
        "quantity": 1000,
        "unitPrice": 5.50,
        "totalPrice": 550.00,
        "product": {
          "id": 1,
          "name": "Paracetamol 500mg"
        }
      }
    ]
  }
]
```

### GET /api/v1/purchases/{id}
Get purchase by ID.

**Headers:** `Authorization: Bearer <admin_token>`

**Response (200 OK):**
```json
{
  "id": 1,
  "purchaseDate": "2024-01-15",
  "totalAmount": 550.00,
  "status": "CONFIRMED",
  "supplier": {
    "id": 1,
    "name": "PharmaCorp Ltd"
  },
  "items": [
    {
      "id": 1,
      "quantity": 1000,
      "unitPrice": 5.50,
      "totalPrice": 550.00,
      "product": {
        "id": 1,
        "name": "Paracetamol 500mg"
      }
    }
  ]
}
```

### POST /api/v1/purchases/create
Create a new purchase.

**Headers:** `Authorization: Bearer <admin_token>`

**Request Body:**
```json
{
  "purchaseDate": "2024-01-15",
  "supplierId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 1000,
      "unitPrice": 5.50,
      "batchNumber": "BATCH001",
      "expiryDate": "2024-12-31"
    }
  ]
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "purchaseDate": "2024-01-15",
  "totalAmount": 550.00,
  "status": "PENDING",
  "supplier": {
    "id": 1,
    "name": "PharmaCorp Ltd"
  },
  "items": [
    {
      "id": 1,
      "quantity": 1000,
      "unitPrice": 5.50,
      "totalPrice": 550.00,
      "product": {
        "id": 1,
        "name": "Paracetamol 500mg"
      }
    }
  ]
}
```

### PUT /api/v1/purchases/{id}
Update purchase.

**Headers:** `Authorization: Bearer <admin_token>`

**Request Body:** Same as create purchase

**Response (200 OK):**
```json
{
  "id": 1,
  "purchaseDate": "2024-01-15",
  "totalAmount": 550.00,
  "status": "PENDING",
  "supplier": {
    "id": 1,
    "name": "PharmaCorp Ltd"
  },
  "items": [
    {
      "id": 1,
      "quantity": 1000,
      "unitPrice": 5.50,
      "totalPrice": 550.00,
      "product": {
        "id": 1,
        "name": "Paracetamol 500mg"
      }
    }
  ]
}
```

### PUT /api/v1/purchases/{id}/confirm
Confirm purchase.

**Headers:** `Authorization: Bearer <admin_token>`

**Response (200 OK):**
```json
{
  "id": 1,
  "purchaseDate": "2024-01-15",
  "totalAmount": 550.00,
  "status": "CONFIRMED",
  "supplier": {
    "id": 1,
    "name": "PharmaCorp Ltd"
  },
  "items": [
    {
      "id": 1,
      "quantity": 1000,
      "unitPrice": 5.50,
      "totalPrice": 550.00,
      "product": {
        "id": 1,
        "name": "Paracetamol 500mg"
      }
    }
  ]
}
```

### PUT /api/v1/purchases/{id}/cancel
Cancel purchase.

**Headers:** `Authorization: Bearer <admin_token>`

**Response (200 OK):**
```json
{
  "id": 1,
  "purchaseDate": "2024-01-15",
  "totalAmount": 550.00,
  "status": "CANCELLED",
  "supplier": {
    "id": 1,
    "name": "PharmaCorp Ltd"
  },
  "items": [
    {
      "id": 1,
      "quantity": 1000,
      "unitPrice": 5.50,
      "totalPrice": 550.00,
      "product": {
        "id": 1,
        "name": "Paracetamol 500mg"
      }
    }
  ]
}
```

---

## 💰 Sales Management

### GET /api/v1/sales
Get all sales.

**Headers:** `Authorization: Bearer <staff_token>`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "saleDate": "2024-01-15",
    "totalAmount": 12.50,
    "discountAmount": 0.00,
    "status": "CONFIRMED",
    "paymentMethod": "CASH",
    "customer": {
      "id": 1,
      "name": "Jane Smith"
    },
    "items": [
      {
        "id": 1,
        "quantity": 2,
        "unitPrice": 6.25,
        "totalPrice": 12.50,
        "product": {
          "id": 1,
          "name": "Paracetamol 500mg"
        }
      }
    ]
  }
]
```

### GET /api/v1/sales/{id}
Get sale by ID.

**Headers:** `Authorization: Bearer <staff_token>`

**Response (200 OK):**
```json
{
  "id": 1,
  "saleDate": "2024-01-15",
  "totalAmount": 12.50,
  "discountAmount": 0.00,
  "status": "CONFIRMED",
  "paymentMethod": "CASH",
  "customer": {
    "id": 1,
    "name": "Jane Smith"
  },
  "items": [
    {
      "id": 1,
      "quantity": 2,
      "unitPrice": 6.25,
      "totalPrice": 12.50,
      "product": {
        "id": 1,
        "name": "Paracetamol 500mg"
      }
    }
  ]
}
```

### POST /api/v1/sales/create
Create a new sale.

**Headers:** `Authorization: Bearer <staff_token>`

**Request Body:**
```json
{
  "customerId": 1,
  "saleDate": "2024-01-15",
  "paymentMethod": "CASH",
  "discountAmount": 0.00,
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "unitPrice": 6.25
    }
  ]
}
```

**Validation Rules:**
- `saleDate`: Required
- `paymentMethod`: Required, enum (CASH, CREDIT_CARD, MOBILE_PAY, OTHER)
- `discountAmount`: Minimum 0.00
- `items`: Required array

**Response (200 OK):**
```json
{
  "id": 1,
  "saleDate": "2024-01-15",
  "totalAmount": 12.50,
  "discountAmount": 0.00,
  "status": "PENDING",
  "paymentMethod": "CASH",
  "customer": {
    "id": 1,
    "name": "Jane Smith"
  },
  "items": [
    {
      "id": 1,
      "quantity": 2,
      "unitPrice": 6.25,
      "totalPrice": 12.50,
      "product": {
        "id": 1,
        "name": "Paracetamol 500mg"
      }
    }
  ]
}
```

### POST /api/v1/sales/{id}/confirm
Confirm sale.

**Headers:** `Authorization: Bearer <staff_token>`

**Response (200 OK):**
```json
{
  "id": 1,
  "saleDate": "2024-01-15",
  "totalAmount": 12.50,
  "discountAmount": 0.00,
  "status": "CONFIRMED",
  "paymentMethod": "CASH",
  "customer": {
    "id": 1,
    "name": "Jane Smith"
  },
  "items": [
    {
      "id": 1,
      "quantity": 2,
      "unitPrice": 6.25,
      "totalPrice": 12.50,
      "product": {
        "id": 1,
        "name": "Paracetamol 500mg"
      }
    }
  ]
}
```

### POST /api/v1/sales/{id}/void
Void sale.

**Headers:** `Authorization: Bearer <staff_token>`

**Request Body:**
```json
{
  "reason": "Customer requested refund"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "saleDate": "2024-01-15",
  "totalAmount": 12.50,
  "discountAmount": 0.00,
  "status": "VOIDED",
  "paymentMethod": "CASH",
  "customer": {
    "id": 1,
    "name": "Jane Smith"
  },
  "items": [
    {
      "id": 1,
      "quantity": 2,
      "unitPrice": 6.25,
      "totalPrice": 12.50,
      "product": {
        "id": 1,
        "name": "Paracetamol 500mg"
      }
    }
  ]
}
```

### POST /api/v1/sales/{id}/cancel
Cancel sale.

**Headers:** `Authorization: Bearer <staff_token>`

**Response (200 OK):**
```json
{
  "id": 1,
  "saleDate": "2024-01-15",
  "totalAmount": 12.50,
  "discountAmount": 0.00,
  "status": "CANCELLED",
  "paymentMethod": "CASH",
  "customer": {
    "id": 1,
    "name": "Jane Smith"
  },
  "items": [
    {
      "id": 1,
      "quantity": 2,
      "unitPrice": 6.25,
      "totalPrice": 12.50,
      "product": {
        "id": 1,
        "name": "Paracetamol 500mg"
      }
    }
  ]
}
```

---

## 📊 Stock Management

### GET /api/v1/stockAdjustments
Get all stock adjustments.

**Headers:** `Authorization: Bearer <admin_token>`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "adjustmentDate": "2024-01-15",
    "adjustmentType": "INCREASE",
    "quantity": 50,
    "reason": "Stock count correction",
    "product": {
      "id": 1,
      "name": "Paracetamol 500mg"
    }
  }
]
```

### GET /api/v1/stockAdjustments/{id}
Get stock adjustment by ID.

**Headers:** `Authorization: Bearer <admin_token>`

**Response (200 OK):**
```json
{
  "id": 1,
  "adjustmentDate": "2024-01-15",
  "adjustmentType": "INCREASE",
  "quantity": 50,
  "reason": "Stock count correction",
  "product": {
    "id": 1,
    "name": "Paracetamol 500mg"
  }
}
```

### POST /api/v1/stockAdjustments/create
Create a new stock adjustment.

**Headers:** `Authorization: Bearer <admin_token>`

**Request Body:**
```json
{
  "adjustmentDate": "2024-01-15",
  "adjustmentType": "INCREASE",
  "quantity": 50,
  "reason": "Stock count correction",
  "productId": 1
}
```

**Validation Rules:**
- `adjustmentDate`: Required
- `adjustmentType`: Required, enum (INCREASE, DECREASE)
- `quantity`: Required, positive number
- `reason`: Required
- `productId`: Required

**Response (200 OK):**
```json
{
  "id": 1,
  "adjustmentDate": "2024-01-15",
  "adjustmentType": "INCREASE",
  "quantity": 50,
  "reason": "Stock count correction",
  "product": {
    "id": 1,
    "name": "Paracetamol 500mg"
  }
}
```

---

## 🚨 Alert Management

### GET /api/v1/alerts/count
Get count of unresolved alerts.

**Headers:** `Authorization: Bearer <staff_token>`

**Response (200 OK):**
```json
5
```

### GET /api/v1/alerts/unresolved
Get all unresolved alerts.

**Headers:** `Authorization: Bearer <staff_token>`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "alertDate": "2024-01-15",
    "severity": "HIGH",
    "message": "Low stock alert for Paracetamol 500mg",
    "isResolved": false,
    "product": {
      "id": 1,
      "name": "Paracetamol 500mg",
      "currentStock": 25,
      "minimumStock": 100
    }
  }
]
```

### GET /api/v1/alerts/resolved
Get all resolved alerts.

**Headers:** `Authorization: Bearer <staff_token>`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "alertDate": "2024-01-15",
    "severity": "HIGH",
    "message": "Low stock alert for Paracetamol 500mg",
    "isResolved": true,
    "resolvedDate": "2024-01-16",
    "product": {
      "id": 1,
      "name": "Paracetamol 500mg",
      "currentStock": 150,
      "minimumStock": 100
    }
  }
]
```

### PUT /api/v1/alerts/{id}/resolve
Resolve an alert.

**Headers:** `Authorization: Bearer <staff_token>`

**Response (200 OK):**
```json
"Alert resolved successfully"
```

### POST /api/v1/alerts/createOrUpdate
Create or update low stock alerts.

**Headers:** `Authorization: Bearer <staff_token>`

**Response (200 OK):**
```json
"Alerts updated successfully"
```

---

## 📋 Inventory Logs

### GET /api/v1/inventoryLogs/
Get all inventory logs.

**Headers:** `Authorization: Bearer <admin_token>`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "changeDate": "2024-01-15T10:30:00",
    "changeType": "PURCHASE",
    "quantityChange": 1000,
    "previousQuantity": 0,
    "newQuantity": 1000,
    "reason": "Purchase from PharmaCorp",
    "product": {
      "id": 1,
      "name": "Paracetamol 500mg"
    }
  }
]
```

### GET /api/v1/inventoryLogs/{id}
Get inventory log by ID.

**Headers:** `Authorization: Bearer <admin_token>`

**Response (200 OK):**
```json
{
  "id": 1,
  "changeDate": "2024-01-15T10:30:00",
  "changeType": "PURCHASE",
  "quantityChange": 1000,
  "previousQuantity": 0,
  "newQuantity": 1000,
  "reason": "Purchase from PharmaCorp",
  "product": {
    "id": 1,
    "name": "Paracetamol 500mg"
  }
}
```

### POST /api/v1/inventoryLogs/create
Create a new inventory log.

**Headers:** `Authorization: Bearer <admin_token>`

**Request Body:**
```json
{
  "changeType": "PURCHASE",
  "quantityChange": 1000,
  "previousQuantity": 0,
  "newQuantity": 1000,
  "reason": "Purchase from PharmaCorp",
  "productId": 1
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "changeDate": "2024-01-15T10:30:00",
  "changeType": "PURCHASE",
  "quantityChange": 1000,
  "previousQuantity": 0,
  "newQuantity": 1000,
  "reason": "Purchase from PharmaCorp",
  "product": {
    "id": 1,
    "name": "Paracetamol 500mg"
  }
}
```

---

## 🔐 Role Management

### GET /api/v1/roles
Get all roles.

**Headers:** `Authorization: Bearer <admin_token>`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "ADMIN",
    "description": "Administrator role with full access"
  },
  {
    "id": 2,
    "name": "STAFF",
    "description": "Staff role with limited access"
  }
]
```

### GET /api/v1/roles/{id}
Get role by ID.

**Headers:** `Authorization: Bearer <admin_token>`

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "ADMIN",
  "description": "Administrator role with full access"
}
```

### GET /api/v1/roles/{id}/users/count
Get user count for a role.

**Headers:** `Authorization: Bearer <admin_token>`

**Response (200 OK):**
```json
3
```

### POST /api/v1/roles/create
Create a new role.

**Headers:** `Authorization: Bearer <admin_token>`

**Request Body:**
```json
{
  "name": "MANAGER",
  "description": "Manager role with elevated permissions"
}
```

**Response (200 OK):**
```json
"Role created successfully: MANAGER"
```

### PUT /api/v1/roles/{id}
Update role.

**Headers:** `Authorization: Bearer <admin_token>`

**Request Body:**
```json
{
  "name": "MANAGER",
  "description": "Manager role with elevated permissions"
}
```

**Response (200 OK):**
```json
"Role Updated Successfully"
```

---

## 📝 Data Models

### Enums

#### DrugClassification
```json
"OTC" | "RX"
```

#### PaymentMethod
```json
"CASH" | "CREDIT_CARD" | "MOBILE_PAY" | "OTHER"
```

#### PurchaseStatus
```json
"PENDING" | "CONFIRMED" | "CANCELLED"
```

#### SaleStatus
```json
"PENDING" | "CONFIRMED" | "CANCELLED" | "VOIDED"
```

#### AdjustmentType
```json
"INCREASE" | "DECREASE"
```

#### ChangeType
```json
"PURCHASE" | "SALE" | "ADJUSTMENT" | "EXPIRED"
```

#### Severity
```json
"LOW" | "MEDIUM" | "HIGH" | "CRITICAL"
```

#### BatchStatus
```json
"ACTIVE" | "EXPIRED" | "DEPLETED"
```

---

## 🔒 Security & Authorization

### Role-Based Access Control

| Endpoint | ADMIN | STAFF |
|----------|-------|-------|
| `/auth/**` | ✅ | ✅ |
| `/api/v1/users/**` | ✅ | ❌ |
| `/api/v1/roles/**` | ✅ | ❌ |
| `/api/v1/products/**` | ✅ | ❌ |
| `/api/v1/productBatches/**` | ✅ | ✅ |
| `/api/v1/customers/**` | ✅ | ✅ |
| `/api/v1/stockAdjustments/**` | ✅ | ❌ |
| `/api/v1/sales/**` | ✅ | ✅ |
| `/api/v1/inventoryLogs/**` | ✅ | ❌ |
| `/api/v1/alerts/**` | ✅ | ✅ |

### Rate Limiting
- **Login attempts**: 5 per minute per IP
- **Token refresh**: 10 per minute per IP

### Security Features
- JWT-based authentication
- Token blacklisting on logout
- Refresh token rotation
- CORS configuration
- Rate limiting
- Input validation
- SQL injection protection

---

## ❌ Error Handling

### Standard Error Response Format
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/products/create",
  "details": [
    {
      "field": "name",
      "message": "Name is required"
    }
  ]
}
```

### HTTP Status Codes
- `200 OK` - Success
- `201 Created` - Resource created
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource already exists
- `422 Unprocessable Entity` - Validation errors
- `429 Too Many Requests` - Rate limit exceeded
- `500 Internal Server Error` - Server error

---

## 🧪 Testing

### Authentication Test
```bash
# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "password"}'

# Use token in subsequent requests
curl -X GET http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer <access_token>"
```

### Sample Product Creation
```bash
curl -X POST http://localhost:8080/api/v1/products/create \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Paracetamol 500mg",
    "brand": "Generic",
    "manufacturer": "PharmaCorp",
    "dosageForm": "Tablet",
    "strength": "500mg",
    "minimumStock": 100,
    "drugClassification": "OTC",
    "description": "Pain relief medication",
    "barcode": "1234567890123",
    "categoryId": 1
  }'
```

---

## 📈 API Versioning

The API uses URL-based versioning with the format `/api/v1/`. Future versions will be available at `/api/v2/`, etc.

---

## 🔄 Pagination & Filtering

Currently, the API returns all records for list endpoints. Future versions will include:
- Pagination with `page` and `size` parameters
- Sorting with `sort` parameter
- Filtering capabilities

---

## 📞 Support

For API support and questions:
- Check the error responses for detailed validation messages
- Ensure proper authentication headers are included
- Verify user permissions for protected endpoints
- Review the security configuration for CORS and rate limiting settings


