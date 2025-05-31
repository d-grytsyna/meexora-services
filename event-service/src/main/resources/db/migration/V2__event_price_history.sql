CREATE TABLE event_price_history (
     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
     event_id UUID NOT NULL REFERENCES events(id) ON DELETE CASCADE,
     new_price NUMERIC(10,2) NOT NULL,
     calculated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
     expected_progress NUMERIC(5,4) NOT NULL,
     actual_progress NUMERIC(5,4) NOT NULL,
     reason TEXT NOT NULL
);
