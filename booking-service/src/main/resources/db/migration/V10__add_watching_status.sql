ALTER TABLE bookings
    DROP CONSTRAINT IF EXISTS bookings_status_check;

ALTER TABLE bookings
    ADD CONSTRAINT bookings_status_check
        CHECK (status IN (
                          'RESERVED',
                          'PAID',
                          'CANCELLED',
                          'REFUNDED',
                          'EXPIRED',
                          'REFUND_FAILED',
                          'WATCHING'
            ));

ALTER TABLE tickets
    DROP CONSTRAINT IF EXISTS tickets_status_check;

ALTER TABLE tickets
    ADD CONSTRAINT tickets_status_check
        CHECK (status IN (
                          'RESERVED',
                          'PAID',
                          'CANCELLED',
                          'REFUNDED',
                          'REFUND_FAILED',
                          'WATCHING',
                          'EXPIRED'
            ));
