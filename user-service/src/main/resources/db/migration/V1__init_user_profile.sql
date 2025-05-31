CREATE TABLE user_profile (
                              id UUID PRIMARY KEY,
                              user_id UUID UNIQUE NOT NULL,
                              first_name VARCHAR(64),
                              last_name VARCHAR(64),
                              birthdate DATE,
                              location VARCHAR(128),
                              created_at TIMESTAMP DEFAULT now(),
                              updated_at TIMESTAMP DEFAULT now()
);
