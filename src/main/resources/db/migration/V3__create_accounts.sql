CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    balance NUMERIC(19,2) NOT NULL DEFAULT 0,
    client_id BIGINT NOT NULL REFERENCES clients(id)
);
