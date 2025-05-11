ALTER TABLE payments
    DROP CONSTRAINT IF EXISTS payments_status_check;

ALTER TABLE payments
    ADD CONSTRAINT payments_status_check
        CHECK (status IN (
                          'PENDING',
                          'PAID',
                          'CANCELLED',
                          'REFUNDED',
                          'REFUND_FAILED'
            ));
