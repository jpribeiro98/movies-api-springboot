DROP TABLE Movie IF EXISTS;

CREATE TABLE IF NOT EXISTS Movie (
    id BIGINT AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    launchDate DATE NOT NULL,
    rating DECIMAL(3,1) NOT NULL,
    revenue BIGINT NOT NULL,
    PRIMARY KEY (id)
);