CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       archive BOOLEAN DEFAULT FALSE
);

CREATE TABLE bank_cards (
                            id BIGSERIAL PRIMARY KEY,
                            user_id BIGINT NOT NULL,
                            card_number_encrypted VARCHAR(255) NOT NULL UNIQUE,
                            expiration_date DATE NOT NULL,
                            status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                            balance DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                            archive BOOLEAN DEFAULT FALSE,
                            CONSTRAINT fk_bank_cards_user FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE
);

CREATE TABLE transfers (
                           id BIGSERIAL PRIMARY KEY,
                           from_card_id BIGINT NOT NULL,
                           to_card_id BIGINT NOT NULL,
                           amount DECIMAL(15,2) NOT NULL,
                           status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                           CONSTRAINT fk_transfers_from_card FOREIGN KEY (from_card_id) REFERENCES bank_cards(id) ON UPDATE CASCADE,
                           CONSTRAINT fk_transfers_to_card FOREIGN KEY (to_card_id) REFERENCES bank_cards(id) ON UPDATE CASCADE
);
