ALTER TABLE customers
ALTER COLUMN customer_number SET NOT NULL;

CREATE INDEX IF NOT EXISTS idx_customer_number
ON customers(customer_number);

CREATE INDEX IF NOT EXISTS idx_customer_email
ON customers(email);