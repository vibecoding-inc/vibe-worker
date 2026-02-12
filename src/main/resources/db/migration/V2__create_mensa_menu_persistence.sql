CREATE TABLE mensa_menu (
    id SERIAL PRIMARY KEY,
    restaurant_name VARCHAR(255) NOT NULL,
    menu_date DATE NOT NULL,
    restaurant_id BIGINT NOT NULL,
    fetched_at TIMESTAMP NOT NULL
);

CREATE TABLE mensa_dish (
    id SERIAL PRIMARY KEY,
    menu_id INT REFERENCES mensa_menu(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    price DOUBLE PRECISION,
    category VARCHAR(50)
);

CREATE INDEX idx_mensa_menu_restaurant_date ON mensa_menu(restaurant_id, menu_date);
