ALTER TABLE payments
    ADD COLUMN payment_intent_id VARCHAR(255),
    ADD COLUMN client_secret VARCHAR(255);
