-- MySQL Schema for Pharmacy Inventory Management System

-- Drop tables in reverse order of dependency to avoid foreign key constraints issues during recreation
DROP TABLE IF EXISTS inventory_logs;
DROP TABLE IF EXISTS sale_items;
DROP TABLE IF EXISTS purchase_items;
DROP TABLE IF EXISTS sales;
DROP TABLE IF EXISTS purchases;
DROP TABLE IF EXISTS product_suppliers;
DROP TABLE IF EXISTS product_batches;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;

-- Table for Roles
CREATE TABLE roles (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(50) NOT NULL UNIQUE
);

-- Table for Users
-- created_by for users could be NULL initially or point to a special system user
CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role_id BIGINT NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       updated_by BIGINT, -- Can be NULL initially, self-referencing for updates
                       FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT ON UPDATE CASCADE,
                       FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE
);

-- Table for Categories
CREATE TABLE categories (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            name VARCHAR(100) NOT NULL UNIQUE,
                            active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Table for Products
CREATE TABLE products (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          name VARCHAR(150) NOT NULL,
                          brand VARCHAR(100) NOT NULL,
                          manufacturer VARCHAR(100) NOT NULL,
                          dosage_form VARCHAR(50) NOT NULL,
                          strength VARCHAR(50) NOT NULL,
                          minimum_stock INT NOT NULL CHECK (products.minimum_stock >= 0),
                          drug_classification ENUM('OTC', 'RX') NOT NULL DEFAULT 'OTC',
                          active BOOLEAN NOT NULL DEFAULT TRUE,
                          description TEXT,
                          category_id BIGINT NOT NULL,
                          barcode VARCHAR(50) UNIQUE NOT NULL, -- Assuming barcodes are unique
                          created_by BIGINT NOT NULL, -- Added: User who created this product record
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          updated_by BIGINT,
                          FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT ON UPDATE CASCADE,
                          FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE, -- Added FK constraint
                          FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE
);

-- Table for Product Batches (key for inventory management)
CREATE TABLE product_batches (
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 product_id BIGINT NOT NULL,
                                 batch_number VARCHAR(100) NOT NULL,
                                 quantity INT NOT NULL CHECK (product_batches.quantity >= 0), -- Quantity should not be negative
                                 purchase_price_per_unit DECIMAL(10,2) NOT NULL,
                                 expiry_date DATE NOT NULL,
                                 manufacturing_date DATE NOT NULL,
                                 location VARCHAR(50),
                                 batch_status ENUM('AVAILABLE', 'UNAVAILABLE', 'SOLD_OUT', 'RECALLED', 'EXPIRED') NOT NULL DEFAULT 'AVAILABLE',
                                 created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                 created_by BIGINT NOT NULL,
                                 updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 updated_by BIGINT,
                                 FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT ON UPDATE CASCADE,
                                 FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE,
                                 FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE,
                                 UNIQUE (product_id, batch_number) -- A product should have unique batch numbers
);

-- Table for Suppliers
CREATE TABLE suppliers (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           name VARCHAR(150) NOT NULL UNIQUE,
                           contact_person VARCHAR(100),
                           phone_number VARCHAR(20) NOT NULL,
                           email VARCHAR(100),
                           address_street VARCHAR(255),
                           address_city VARCHAR(100),
                           address_state VARCHAR(100),
                           address_zip_code VARCHAR(20),
                           created_by BIGINT NOT NULL, -- Added: User who created this supplier record
                           created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                           updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           updated_by BIGINT,
                           FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE, -- Added FK constraint
                           FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE
);

-- Junction Table for Product-Supplier Many-to-Many relationship
CREATE TABLE product_suppliers (
                                   id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   product_id BIGINT NOT NULL,
                                   supplier_id BIGINT NOT NULL,
                                   preferred_supplier BOOLEAN DEFAULT FALSE,
                                   supplier_product_code VARCHAR(100),
                                   FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE ON UPDATE CASCADE,
                                   FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE CASCADE ON UPDATE CASCADE,
                                   UNIQUE (product_id, supplier_id), -- A product and supplier pair should be unique
                                   UNIQUE (supplier_id, supplier_product_code)               -- Prevent duplicate code per supplier
);

-- Table for Purchases
CREATE TABLE purchases (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           supplier_id BIGINT NOT NULL,
                           total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
                           purchase_date DATE NOT NULL,
                           status ENUM('PENDING', 'ORDERED', 'RECEIVED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
                           created_by BIGINT NOT NULL,
                           created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                           updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           updated_by BIGINT,
                           tax_amount DECIMAL(10,2) DEFAULT 0.00,
                           grand_total DECIMAL(10,2) DEFAULT 0.00,
                           FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE RESTRICT ON UPDATE CASCADE,
                           FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE,
                           FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE
);

-- Table for Purchase Items (details of each item in a purchase)
CREATE TABLE purchase_items (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                purchase_id BIGINT NOT NULL,
                                product_batch_id BIGINT NOT NULL,
                                quantity INT NOT NULL CHECK (quantity > 0),
                                unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
                                FOREIGN KEY (purchase_id) REFERENCES purchases(id) ON DELETE CASCADE ON UPDATE CASCADE,
                                FOREIGN KEY (product_batch_id) REFERENCES product_batches(id) ON DELETE RESTRICT ON UPDATE CASCADE,
                                UNIQUE (purchase_id, product_batch_id) -- A specific batch should only appear once per purchase
);

-- Table for Customers
CREATE TABLE customers (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           name VARCHAR(100) NOT NULL,
                           phone_number VARCHAR(20),
                           email VARCHAR(100),
                           address_street VARCHAR(255),
                           address_city VARCHAR(100),
                           address_state VARCHAR(100),
                           address_zip_code VARCHAR(20),
                           created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                           updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           updated_by BIGINT,
                           FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE
);

-- Table for Sales
CREATE TABLE sales (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       customer_id BIGINT, -- Can be NULL for walk-in customers
                       total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
                       sale_date DATE NOT NULL,
                       payment_method ENUM('CASH', 'CREDIT CARD', 'MOBILE PAY', 'OTHER') NOT NULL DEFAULT 'CASH',
                       discount_amount DECIMAL(10,2) DEFAULT 0.00 CHECK (discount_amount >= 0),
                       created_by BIGINT NOT NULL,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       updated_by BIGINT,
                       is_voided BOOLEAN DEFAULT FALSE, -- Soft-delete alternative
                       void_reason TEXT,
                       voided_by BIGINT,
                       voided_at DATETIME,
                       sale_status ENUM('PENDING', 'CONFIRMED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
                       tax_amount DECIMAL(10,2) DEFAULT 0.00,
                       grand_total DECIMAL(10,2) DEFAULT 0.00,
                       FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL ON UPDATE CASCADE,
                       FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE,
                       FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE,
                       FOREIGN KEY (voided_by) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE
);

-- Table for Sale Items (details of each item in a sale)
CREATE TABLE sale_items (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            sale_id BIGINT NOT NULL,
                            product_id BIGINT NOT NULL, -- General product reference
                            product_batch_id BIGINT NOT NULL, -- Specific batch sold
                            quantity INT NOT NULL CHECK (quantity > 0),
                            unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0), -- Selling price at time of sale
                            sub_total DECIMAL(10,2) NOT NULL CHECK (sub_total >= 0), -- Sub-total price at time of sale
                            FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE CASCADE ON UPDATE CASCADE,
                            FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT ON UPDATE CASCADE,
                            FOREIGN KEY (product_batch_id) REFERENCES product_batches(id) ON DELETE RESTRICT ON UPDATE CASCADE,
                            UNIQUE (sale_id, product_batch_id) -- Ensure a specific batch is only logged once per sale item
);

-- Table for Inventory Logs (audit trail of inventory movements)
CREATE TABLE inventory_logs (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                product_id BIGINT NOT NULL,
                                product_batch_id BIGINT, -- Can be NULL for general product adjustments or if batch info is not relevant for a log entry
                                change_type ENUM('IN', 'OUT', 'ADJUST', 'INITIAL') NOT NULL,
                                quantity_changed INT NOT NULL,
                                reason TEXT,
                                sale_id BIGINT, -- Link to sales if change is due to a sale
                                purchase_id BIGINT, -- Link to purchases if change is due to a purchase
                                adjustment_ref VARCHAR(100), -- For manual adjustments not linked to sale/purchase
                                created_by BIGINT NOT NULL,
                                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT ON UPDATE CASCADE,
                                FOREIGN KEY (product_batch_id) REFERENCES product_batches(id) ON DELETE SET NULL ON UPDATE CASCADE,
                                FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE SET NULL ON UPDATE CASCADE,
                                FOREIGN KEY (purchase_id) REFERENCES purchases(id) ON DELETE SET NULL ON UPDATE CASCADE,
                                FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE
);
CREATE TABLE stock_adjustments (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                product_id BIGINT NOT NULL,
                                product_batch_id BIGINT, -- optional: adjustment may apply to a batch or general product
                                adjustment_type ENUM('IN', 'OUT', 'CORRECTION') NOT NULL, -- IN = add stock, OUT = reduce stock
                                quantity_adjusted INT NOT NULL,
                                reason TEXT NOT NULL, -- explanation for the manual adjustment
                                created_by BIGINT NOT NULL,
                                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                updated_by BIGINT,

                                FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT ON UPDATE CASCADE,
                                FOREIGN KEY (product_batch_id) REFERENCES product_batches(id) ON DELETE SET NULL ON UPDATE CASCADE,
                                FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE,
                                FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE
);
