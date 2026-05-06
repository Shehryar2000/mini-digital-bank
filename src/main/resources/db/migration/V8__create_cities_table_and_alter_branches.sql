CREATE TABLE cities (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    prefix VARCHAR(10) NOT NULL UNIQUE,
    created_at TIMESTAMP(6) DEFAULT now(),
    updated_at TIMESTAMP(6) DEFAULT now()
);

ALTER TABLE branches
ADD COLUMN city_id INT NOT NULL;

ALTER TABLE branches
ADD CONSTRAINT fk_branch_city
FOREIGN KEY (city_id)
REFERENCES cities(id);