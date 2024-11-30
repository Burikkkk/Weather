CREATE DATABASE weather_data
    CHARACTER SET utf8
    COLLATE UTF8_GENERAL_CI;


USE weather_data;

CREATE TABLE personal_settings
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    phone         VARCHAR(15) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    notifications BOOLEAN                                                DEFAULT FALSE,
    temperature   VARCHAR(15)                                            DEFAULT 'C',
    pressure      VARCHAR(15)                                            DEFAULT 'мм рт. ст.',
    speed         VARCHAR(15)                                            DEFAULT 'м/с'
) CHARACTER SET utf8
  COLLATE utf8_general_ci;

CREATE TABLE user
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    login       VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci UNIQUE NOT NULL,
    password    VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci        NOT NULL,
    role        VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci         NOT NULL DEFAULT 'user',
    settings_id INT,
    FOREIGN KEY (settings_id) REFERENCES personal_settings (id) ON DELETE CASCADE
) CHARACTER SET utf8
  COLLATE utf8_general_ci;

CREATE TABLE location
(
    id      INT AUTO_INCREMENT PRIMARY KEY,
    town    VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    country VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL
) CHARACTER SET utf8
  COLLATE utf8_general_ci;

CREATE TABLE weather_name
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL
) CHARACTER SET utf8
  COLLATE utf8_general_ci;

CREATE TABLE weather_parameters
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    temperature   DECIMAL(5, 2) NOT NULL,
    pressure      INT           NOT NULL,
    humidity      INT           NOT NULL,
    precipitation DECIMAL(5, 2),
    wind_speed    DECIMAL(5, 2) NOT NULL
) CHARACTER SET utf8
  COLLATE utf8_general_ci;

CREATE TABLE day
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    date          DATE NOT NULL,
    parameters_id INT,
    weather_id    INT,
    location_id   INT,
    FOREIGN KEY (parameters_id) REFERENCES weather_parameters (id) ON DELETE CASCADE,
    FOREIGN KEY (weather_id) REFERENCES weather_name (id),
    FOREIGN KEY (location_id) REFERENCES location (id)
) CHARACTER SET utf8
  COLLATE utf8_general_ci;

CREATE TABLE dashboard
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    start_date_id INT NOT NULL,
    end_date_id   INT NOT NULL,
    user_id       INT,
    FOREIGN KEY (start_date_id) REFERENCES day (id) ON DELETE CASCADE,
    FOREIGN KEY (end_date_id) REFERENCES day (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE
) CHARACTER SET utf8
  COLLATE utf8_general_ci;



