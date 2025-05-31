CREATE TABLE issued_tickets (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                ticket_id UUID NOT NULL,
                                booking_id UUID NOT NULL,
                                event_id UUID NOT NULL,
                                user_id UUID NOT NULL,
                                user_email VARCHAR(255) NOT NULL,
                                user_name VARCHAR(255) NOT NULL,
                                event_title VARCHAR(255) NOT NULL,
                                event_location VARCHAR(255) NOT NULL,
                                event_date TIMESTAMPTZ NOT NULL,
                                price NUMERIC(10, 2) NOT NULL,
                                qr_code TEXT NOT NULL,
                                created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
