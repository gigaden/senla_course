CREATE TABLE IF NOT EXISTS books
(
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title               VARCHAR(256) NOT NULL,
    author              VARCHAR(256) NOT NULL,
    description         TEXT         NOT NULL,
    date_of_publication DATE         NOT NULL,
    price               NUMERIC      NOT NULL,
    status              VARCHAR(256) NOT NULL,
    CONSTRAINT ch_book_price CHECK ( price > 0 ),
    CONSTRAINT ch_book_status CHECK ( status IN ('AVAILABLE', 'ABSENT') )
);

CREATE TABLE IF NOT EXISTS clients
(
    id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name     VARCHAR(256) NOT NULL,
    surname  VARCHAR(256) NOT NULL,
    email    VARCHAR(256) NOT NULL,
    login    VARCHAR(256) NOT NULL,
    password VARCHAR(256) NOT NULL
);

CREATE TABLE IF NOT EXISTS orders
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    book_id      BIGINT NOT NULL,
    client_id    BIGINT NOT NULL,
    created_on   DATE         DEFAULT CURRENT_DATE,
    completed_on DATE,
    order_status VARCHAR(128) DEFAULT 'NEW',
    CONSTRAINT fk_orders_books FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT fk_orders_clients FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT ch_order_status CHECK ( order_status IN ('NEW', 'COMPLETED', 'CANCELED') )
);

CREATE TABLE IF NOT EXISTS requests
(
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    book_id        BIGINT NOT NULL,
    client_id      BIGINT NOT NULL,
    request_status VARCHAR(128) DEFAULT 'OPENED',
    created_on     DATE         DEFAULT CURRENT_DATE,
    CONSTRAINT fk_requests_books FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT fk_requests_clients FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT ch_request_status CHECK ( request_status IN ('OPENED', 'CLOSED') )
);