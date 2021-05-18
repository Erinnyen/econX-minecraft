DROP DATABASE IF EXISTS `sql_econx`;
DROP DATABASE IF EXISTS `sql_playerdb`;
CREATE DATABASE `sql_econx`; 
USE `sql_econx`;

SET NAMES utf8 ;
SET character_set_client = utf8mb4 ;
CREATE TABLE `players` (
  `player_id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `credit` double(20, 2) NOT NULL,
  `last_online` timestamp NOT NULL,
  PRIMARY KEY (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `transactions` (
  `transaction_id` int(20) NOT NULL AUTO_INCREMENT,
  `sender` int(11) NOT NULL,
  `receiver` int(11) NOT NULL,
  `amount` double(20, 2) NOT NULL,
  `timestamp` timestamp NOT NULL,
  `transaction_type` int(5) NOT NULL,
  PRIMARY KEY (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `transaction_type` (
  `transaction_type` int(5) NOT NULL AUTO_INCREMENT,
  `transaction_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`transaction_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO sql_econx.transaction_type
	VAlUES (DEFAULT, 'commodity'),
		   (DEFAULT, 'transfer');


CREATE TABLE `open_sell_orders` (
    `order_id` int(20) NOT NULL AUTO_INCREMENT,
    `amount` int(5) NOT NULL,
    `type` VARCHAR(255) NOT NULL,
    `instance_price` double(20, 2) NOT NULL,
    `seller_name` VARCHAR(50) NOT NUll,
    `seller_id` INT(11) NOT NUll,
    `timestamp` timestamp NOT NULL,
    `transaction_type` INT(5) NOT NUll,
    `JSONString` VARCHAR(1023) NOT NUll,
     PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE USER 'your-username'@'localhost' IDENTIFIED BY 'your-password';
GRANT DELETE, INSERT, SELECT, UPDATE ON sql_econx.* TO 'your-username'@'localhost';
           

