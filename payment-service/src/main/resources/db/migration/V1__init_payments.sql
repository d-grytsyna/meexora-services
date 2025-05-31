CREATE TABLE payments (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                          booking_id UUID NOT NULL,
                          user_id UUID NOT NULL,
                          event_id UUID NOT NULL,
                          amount NUMERIC(10, 2) NOT NULL CHECK (amount >= 0),
                          status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'PAID', 'EXPIRED', 'CANCELLED')),

                          created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
                          updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
);
