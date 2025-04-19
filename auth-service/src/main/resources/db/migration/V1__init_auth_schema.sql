CREATE TABLE roles (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       name VARCHAR(50) UNIQUE NOT NULL
);
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role_id UUID NOT NULL REFERENCES roles(id),
                       created_at TIMESTAMP DEFAULT now()
);

INSERT INTO roles (name) VALUES ('USER'), ('ORGANIZER');
