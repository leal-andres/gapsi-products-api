-- V1__create_products_table.sql
-- Creates the products table for the GAPSI catalog.

CREATE TABLE products (
    id          VARCHAR(10)    NOT NULL,
    name        VARCHAR(20)    NOT NULL,
    description VARCHAR(200)   NOT NULL,
    price       NUMERIC(12, 2) NOT NULL,
    model       VARCHAR(10)    NOT NULL,

    CONSTRAINT pk_products PRIMARY KEY (id),
    CONSTRAINT chk_products_id_format    CHECK (id ~ '^[A-Za-z0-9]{10}$'),
    CONSTRAINT chk_products_name_format  CHECK (name ~ '^[A-Za-z0-9 ]{1,20}$'),
    CONSTRAINT chk_products_model_format CHECK (model ~ '^[A-Za-z0-9]{10}$'),
    CONSTRAINT chk_products_price        CHECK (price >= 0)
);