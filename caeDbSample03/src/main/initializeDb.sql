-------------------------------------------
-- Initialize Table / テーブルの初期化 --
-------------------------------------------

SHOW databases;
USE cae;
SHOW tables;
SELECT database();
-- drop table comment;
CREATE TABLE product
(
  id      INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  company VARCHAR(100) NOT NULL,
  name    VARCHAR(100) NOT NULL,
  price   INT UNSIGNED NOT NULL,
  last    TIMESTAMP
);
DESC product;

SELECT * FROM product;
DELETE FROM product;
-- show variables like 'char%';

/*

+---------+------------------+------+-----+---------------------+-------------------------------+
| Field   | Type             | Null | Key | Default             | Extra                         |
+---------+------------------+------+-----+---------------------+-------------------------------+
| id      | int(10) unsigned | NO   | PRI | NULL                | auto_increment                |
| company | varchar(100)     | NO   |     | NULL                |                               |
| name    | varchar(100)     | NO   |     | NULL                |                               |
| price   | int(10) unsigned | NO   |     | NULL                |                               |
| last    | timestamp        | NO   |     | current_timestamp() | on update current_timestamp() |
+---------+------------------+------+-----+---------------------+-------------------------------+
 
timestamp
http://en.wikibooks.org/wiki/Structured_Query_Language/Data_Types#Temporal
http://www.techscore.com/tech/sql/SQL2/02_04.html/

*/