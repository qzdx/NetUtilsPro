create table player ( 
    id INT,
    name VARCHAR(10),
    exp INT
);

insert into player values (1,"qzd",100);

insert into player values (1,"qzd",100) , (1,"pzbdedada",100);

alter table player modify exp int default 0;

update player set exp = 50 where name="jack";

select * from player where exp > 15 and exp < 100;

-- 联合查询

SELECT AVG(exp) from player
select * from player where exp > (SELECT AVG(exp) from player);

