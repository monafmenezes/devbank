CREATE TABLE transfers (
    id BIGSERIAL PRIMARY KEY,
    amount NUMERIC(19,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    origin_account_id BIGINT NOT NULL REFERENCES accounts(id),
    destination_account_id BIGINT NOT NULL REFERENCES accounts(id)
);
