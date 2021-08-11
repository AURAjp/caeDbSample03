-------------------------------------------
-- Initialize Table / テーブルの初期化 --
-------------------------------------------

show databases;
use cae;
show tables;
select database();
-- drop table comment;
CREATE TABLE comment
(
  id int unsigned not null auto_increment primary key,
  name varchar(100),
  text varchar(150),
  ip varchar(50),
  last timestamp
);
describe comment;

select * from comment;
delete from comment;
-- show variables like 'char%';

/*

Field Type             Null Key Default           Extra
 ----- ---------------- ---- --- ----------------- --------------
 id    int(10) unsigned NO   PRI NULL              auto_increment
 name  varchar(100)     YES      NULL              
 text  varchar(150)     YES      NULL              
 ip    varchar(50)      YES      NULL              
 last  timestamp        NO       CURRENT_TIMESTAMP 
 
timestamp
http://en.wikibooks.org/wiki/Structured_Query_Language/Data_Types#Temporal
http://www.techscore.com/tech/sql/SQL2/02_04.html/

*/