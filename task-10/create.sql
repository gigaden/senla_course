-- Добавил ограничения NOT NULL ко всем полям.
-- Не совсем понял из изображения к ТЗ по столбцу price - он должен иметь возможность быть NULL?
-- Хотел оставить его таким, но тогда при селектах было бы не соесем удобно
-- Как вариант сделать его DEFAULT 0, но решил просто сделать NOT NULL
-- TinyINT в постгре нет, поэтому заменил на smallint
-- Добавил индексы по модели, т.к. предполагаю, что по ним будет много запросов в селектах
-- Оставил тип price MONEY, хотя, возможно, проще было бы использовать NUMERIC, чтоб не кастовать потом в запросах

CREATE TABLE IF NOT EXISTS product
(
    maker VARCHAR(10) NOT NULL,
    model VARCHAR(50) PRIMARY KEY,
    type  VARCHAR(50) NOT NULL,
    CHECK ( type in ('PC', 'Laptop', 'Printer') )
);

CREATE TABLE IF NOT EXISTS pc
(
    code  INT PRIMARY KEY,
    model VARCHAR(50) NOT NULL,
    speed SMALLINT    NOT NULL,
    ram   SMALLINT    NOT NULL,
    hd    REAL        NOT NULL,
    cd    VARCHAR(10) NOT NULL,
    price MONEY       NOT NULL,
    CONSTRAINT fk_pc_product_model FOREIGN KEY (model) REFERENCES product (model),
    CHECK ( price > 0::money )
);

CREATE TABLE IF NOT EXISTS laptop
(
    code   INT PRIMARY KEY,
    model  VARCHAR(50) NOT NULL,
    speed  SMALLINT    NOT NULL,
    ram    SMALLINT    NOT NULL,
    hd     REAL        NOT NULL,
    price  MONEY       NOT NULL,
    screen REAL        NOT NULL,
    CONSTRAINT fk_laptop_product_model FOREIGN KEY (model) REFERENCES product (model),
    CHECK ( price > 0::money ),
    CHECK ( screen > 0 )
);

CREATE TABLE IF NOT EXISTS printer
(
    code  INT primary key,
    model VARCHAR(50) NOT NULL,
    color CHAR(1)     NOT NULL,
    type  VARCHAR(10) NOT NULL,
    price MONEY       NOT NULL,
    CONSTRAINT fk_printer_product_model FOREIGN KEY (model) REFERENCES product (model),
    CHECK ( type IN ('Laser', 'Jet', 'Matrix') ),
    CHECK ( color IN ('y', 'n') ),
    CHECK ( price > 0::money )
);

CREATE INDEX idx_pc_model ON pc (model);
CREATE INDEX idx_laptop_model ON laptop (model);
CREATE INDEX idx_printer_model ON printer (model);