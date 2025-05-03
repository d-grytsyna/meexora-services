CREATE TABLE events (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        creator_id UUID NOT NULL,
                        title VARCHAR(255) NOT NULL,
                        description TEXT,
                        date TIMESTAMP NOT NULL,
                        latitude DOUBLE PRECISION NOT NULL,
                        longitude DOUBLE PRECISION NOT NULL,
                        address VARCHAR(500),
                        total_tickets INTEGER NOT NULL CHECK (total_tickets >= 0),


                        price NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
                        dynamic_pricing_enabled BOOLEAN NOT NULL DEFAULT FALSE,
                        min_price NUMERIC(10, 2),
                        max_price NUMERIC(10, 2),
                        created_at TIMESTAMP DEFAULT now(),
                        updated_at TIMESTAMP DEFAULT now()
);
