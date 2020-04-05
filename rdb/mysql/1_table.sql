DROP SCHEMA IF EXISTS market;
CREATE SCHEMA market;
USE market;

CREATE TABLE `user_credentials` (
    `id` VARCHAR(255) NOT NULL,
    `mail` VARCHAR(255) NOT NULL,
    `pass` VARCHAR(255) NOT NULL,
    `activated` bit NOT NULL default false,
    PRIMARY KEY (`id`),
    UNIQUE KEY (`mail`)
);
