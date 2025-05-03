CREATE TABLE ticket_availability (
                                     event_id UUID PRIMARY KEY,
                                     remaining_tickets INTEGER NOT NULL CHECK (remaining_tickets >= 0),
                                     updated_at TIMESTAMP DEFAULT now()
);
