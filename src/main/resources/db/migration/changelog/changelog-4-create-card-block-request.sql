CREATE TABLE card_block_requests
(
    id         BIGSERIAL PRIMARY KEY,
    card_id    BIGINT      NOT NULL,
    user_id    BIGINT      NOT NULL,
    status     VARCHAR(20) NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_block_request_card FOREIGN KEY (card_id) REFERENCES cards (id),
    CONSTRAINT fk_block_request_user FOREIGN KEY (user_id) REFERENCES users (id)
);
