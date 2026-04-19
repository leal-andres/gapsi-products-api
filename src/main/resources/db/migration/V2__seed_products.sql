-- V2__seed_products.sql
-- Seed data for local development and Postman/demo usage.

INSERT INTO products (id, name, description, price, model) VALUES
    ('PRD0000001', 'MacBook Pro M4',  'High-performance Macbook with latest-generation processor M and 16 GB of RAM.', 6999.99, 'MCBOOKM4A0'),
    ('PRD0000002', 'iPhone 15 Pro',   'Flagship smartphone with A17 Pro chip, titanium body and advanced camera system.', 1299.99, 'IPH15PROM1'),
    ('PRD0000003', 'iPad Air M2',     'Lightweight tablet powered by the M2 processor with 8 GB of RAM.',                899.99, 'IPADAIRM2A');
