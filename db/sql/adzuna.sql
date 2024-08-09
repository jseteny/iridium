CREATE TABLE IF NOT EXISTS categories (
    tag varchar(255) NOT NULL,
    label varchar(255) NOT NULL,
    PRIMARY KEY (tag)
);

SELECT * FROM categories;