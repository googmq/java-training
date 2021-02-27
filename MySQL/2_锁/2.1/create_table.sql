create table t_2_1 (
	id int not null AUTO_INCREMENT,
	last_name varchar(32) not null default '',
	name varchar(32) not null default '',
	age smallint not null default 10,
	mobile varchar(11) not null default '' ,
    data_version bigint not null default 1,
	PRIMARY key (id) ,
	key age_mobile (age, mobile(8)) using BTREE,
	key mobile (mobile(8)) using BTREE,
	key name (name) using BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = UTF8 ROW_FORMAT = Dynamic;

insert into t_2_1 values (1, 'Lee', 'Mike', 12, '13812341234', 1);
insert into t_2_1 values (2, 'Lee', 'Mike1', 14, '13812320001', 1);
insert into t_2_1 values (3, 'Lee', 'Mike2', 12, '13812330002', 1);
insert into t_2_1 values (4, 'Lee', 'Mike3', 15, '13812340003', 1);
insert into t_2_1 values (5, 'Lee', 'Mike4', 15, '13812350004', 1);
insert into t_2_1 values (6, 'Wang', 'Jonh', 13, '13811231000', 1);
insert into t_2_1 values (7, 'Wang', 'Jonh1', 16, '13811232001', 1);
insert into t_2_1 values (8, 'Wang', 'Jonh2', 20, '13811233002', 1);
insert into t_2_1 values (9, 'Wang', 'Jonh3', 22, '13811234003', 1);
insert into t_2_1 values (10, 'Wang', 'Jonh4', 24, '13811254004', 1);
insert into t_2_1 values (11, 'Fang', 'Jack', 15, '18611231000', 1);
insert into t_2_1 values (12, 'Fang', 'Jack1', 16,  '18611232001', 1);
insert into t_2_1 values (13, 'Fang', 'Jack2', 17, '18611233002', 1);
insert into t_2_1 values (14, 'Fang', 'Jack3', 18, '18611234003', 1);
insert into t_2_1 values (15, 'Fang', 'Jack4', 19, '18611235004', 1);