CREATE TABLE mensa_restaurant (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Migrate existing data from mensa_menu to mensa_restaurant
INSERT INTO mensa_restaurant (id, name)
SELECT DISTINCT restaurant_id, restaurant_name FROM mensa_menu
ON CONFLICT (id) DO NOTHING;

-- Drop redundant column from mensa_menu
ALTER TABLE mensa_menu DROP COLUMN restaurant_name;

-- Add foreign key constraint
ALTER TABLE mensa_menu ADD CONSTRAINT fk_mensa_menu_restaurant FOREIGN KEY (restaurant_id) REFERENCES mensa_restaurant(id);
