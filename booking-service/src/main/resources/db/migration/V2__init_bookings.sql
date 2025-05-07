CREATE TABLE bookings (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          user_id UUID NOT NULL,
                          event_id UUID NOT NULL,
                          event_title VARCHAR(255) NOT NULL,
                          event_location VARCHAR(500) NOT NULL,
                          event_date_time TIMESTAMPTZ NOT NULL,
                          total_price NUMERIC(10, 2) NOT NULL CHECK (total_price >= 0),
                          status VARCHAR(20) NOT NULL CHECK (status IN ('RESERVED', 'PAID', 'EXPIRED', 'CANCELLED')),
                          created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                          updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
