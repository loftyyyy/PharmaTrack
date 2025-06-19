CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL
);

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role_id BIGINT REFERENCES roles(id),
                       email VARCHAR(100) NOT NULL UNIQUE,
                       created_at TIMESTAMP,
                       updated_at TIMESTAMP,
                       updated_by BIGINT REFERENCES users(id)
);

CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL
);

CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(150) NOT NULL,
                          brand VARCHAR(100),
                          description TEXT,
                          category_id BIGINT REFERENCES categories(id),
                          barcode VARCHAR(50),
                          created_at TIMESTAMP,
                          updated_at TIMESTAMP,
                          updated_by BIGINT REFERENCES users(id)
);

CREATE TABLE product_batches (
                                 id BIGSERIAL PRIMARY KEY,
                                 product_id BIGINT REFERENCES products(id),
                                 batch_number VARCHAR(100) NOT NULL,
                                 quantity INT NOT NULL,
                                 purchase_price_per_unit DECIMAL(10,2),
                                 expiry_date DATE,
                                 manufacturing_date DATE,
                                 location VARCHAR(50),
                                 created_at TIMESTAMP,
                                 created_by BIGINT REFERENCES users(id),
                                 updated_at TIMESTAMP,
                                 updated_by BIGINT REFERENCES users(id)
);

CREATE TABLE suppliers (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(150) NOT NULL,
                           contact_person VARCHAR(100),
                           phone VARCHAR(20),
                           email VARCHAR(100),
                           address_street VARCHAR(255),
                           address_city VARCHAR(100),
                           address_state VARCHAR(100),
                           address_zip_code VARCHAR(20),
                           created_at TIMESTAMP,
                           updated_at TIMESTAMP,
                           updated_by BIGINT REFERENCES users(id)
);

CREATE TABLE product_suppliers (
                                   id BIGSERIAL PRIMARY KEY,
                                   product_id BIGINT REFERENCES products(id),
                                   supplier_id BIGINT REFERENCES suppliers(id),
                                   preferred_supplier BOOLEAN DEFAULT FALSE,
                                   supplier_product_code VARCHAR(100)
);

CREATE TABLE purchases (
                           id BIGSERIAL PRIMARY KEY,
                           supplier_id BIGINT REFERENCES suppliers(id),
                           total_amount DECIMAL(10,2),
                           purchase_date DATE,
                           status VARCHAR(50),
                           created_by BIGINT REFERENCES users(id),
                           created_at TIMESTAMP,
                           updated_at TIMESTAMP,
                           updated_by BIGINT REFERENCES users(id)
);

CREATE TABLE purchase_items (
                                id BIGSERIAL PRIMARY KEY,
                                purchase_id BIGINT REFERENCES purchases(id),
                                product_batch_id BIGINT REFERENCES product_batches(id),
                                quantity INT NOT NULL,
                                unit_price DECIMAL(10,2)
);

CREATE TABLE customers (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(100),
                           phone VARCHAR(20),
                           email VARCHAR(100),
                           address_street VARCHAR(255),
                           address_city VARCHAR(100),
                           address_state VARCHAR(100),
                           address_zip_code VARCHAR(20),
                           created_at TIMESTAMP,
                           updated_at TIMESTAMP,
                           updated_by BIGINT REFERENCES users(id)
);

CREATE TABLE sales (
                       id BIGSERIAL PRIMARY KEY,
                       customer_id BIGINT REFERENCES customers(id),
                       total_amount DECIMAL(10,2),
                       sale_date DATE,
                       payment_method VARCHAR(50),
                       discount_amount DECIMAL(10,2),
                       created_by BIGINT REFERENCES users(id),
                       created_at TIMESTAMP,
                       updated_at TIMESTAMP,
                       updated_by BIGINT REFERENCES users(id)
);

CREATE TABLE sale_items (
                            id BIGSERIAL PRIMARY KEY,
                            sale_id BIGINT REFERENCES sales(id),
                            product_id BIGINT REFERENCES products(id),
                            product_batch_id BIGINT REFERENCES product_batches(id),
                            quantity INT,
                            unit_price DECIMAL(10,2)
);

CREATE TABLE inventory_logs (
                                id BIGSERIAL PRIMARY KEY,
                                product_id BIGINT REFERENCES products(id),
                                product_batch_id BIGINT REFERENCES product_batches(id),
                                change_type VARCHAR(50),
                                quantity_changed INT,
                                reason TEXT,
                                sale_id BIGINT REFERENCES sales(id),
                                purchase_id BIGINT REFERENCES purchases(id),
                                adjustment_ref VARCHAR(100),
                                created_by BIGINT REFERENCES users(id),
                                created_at TIMESTAMP
);
