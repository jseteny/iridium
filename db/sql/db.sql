
drop table if exists favourites;

CREATE TABLE favourites (
	id INT PRIMARY KEY,
	name VARCHAR(255) UNIQUE NOT NULL
);

GRANT SELECT, INSERT, UPDATE, DELETE ON favourites TO iridium;