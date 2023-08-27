DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(128) NOT NULL,
    email VARCHAR(64) NOT NULL,
        CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    description VARCHAR(256) NOT NULL,
    requester_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
        CONSTRAINT fk_requests_requester_id FOREIGN KEY (requester_id) REFERENCES users
            ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(128) NOT NULL,
    description VARCHAR(256) NOT NULL,
    available BOOLEAN NOT NULL,
    owner_id BIGINT,
    request_id BIGINT,
        CONSTRAINT fk_items_owner_id FOREIGN KEY (owner_id) REFERENCES users
            ON DELETE CASCADE ON UPDATE CASCADE,
        CONSTRAINT fk_items_request_id FOREIGN KEY (request_id) REFERENCES requests
            ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    status VARCHAR(15) NOT NULL,
        CONSTRAINT fk_bookings_item_id FOREIGN KEY (item_id) REFERENCES items
            ON DELETE CASCADE ON UPDATE CASCADE,
        CONSTRAINT fk_bookings_booker_id FOREIGN KEY (booker_id) REFERENCES users
            ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    text VARCHAR(1000) NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
        CONSTRAINT fk_comments_item_id FOREIGN KEY (item_id) REFERENCES items
            ON DELETE CASCADE ON UPDATE CASCADE,
        CONSTRAINT fk_comments_author_id FOREIGN KEY (author_id) REFERENCES users
            ON DELETE CASCADE ON UPDATE CASCADE
);