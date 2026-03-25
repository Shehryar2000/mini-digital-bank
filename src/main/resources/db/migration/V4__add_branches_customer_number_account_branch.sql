CREATE TABLE branches (
    id SERIAL PRIMARY KEY, 
    code VARCHAR(4) UNIQUE NOT NULL, 
    name VARCHAR(100) NOT NULL, 
    area VARCHAR(100), 
    created_at TIMESTAMP(6) NOT NULL DEFAULT now(), 
    updated_at TIMESTAMP(6) NOT NULL DEFAULT now() ); 
    
CREATE SEQUENCE customer_seq START 1000000;

ALTER TABLE customers
    ADD COLUMN customer_number BIGINT UNIQUE DEFAULT 
nextval('customer_seq') NOT NULL; 

ALTER TABLE accounts
ADD COLUMN branch_id INT NOT NULL REFERENCES branches(id);

CREATE SEQUENCE account_seq START 1010000;