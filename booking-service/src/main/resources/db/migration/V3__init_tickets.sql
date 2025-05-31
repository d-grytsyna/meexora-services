CREATE TABLE tickets (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         booking_id UUID NOT NULL REFERENCES bookings(id),
                         user_name VARCHAR(255) NOT NULL,
                         status VARCHAR(20) NOT NULL CHECK (status IN ('RESERVED', 'PAID', 'USED', 'CANCELLED')),
                         created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                         updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
