// #1
// insert data - no index
:admin
create public table uc2(id string,id2 String bitmap,name string);
insert into uc2 values("abc",null,"rob");
insert into uc2 values("abc","def","rob");
insert into uc2 values("abc","def","rob2");

// #2
// complex index - insert data
:admin
create public table uc(id string,id2 String bitmap,name string);
create index t on uc(id,id2) use constraint="PRIMARY";
insert into uc values("abc",null,"rob");
insert into uc values("abc","def","rob");
insert into uc values("abc","def","rob2");

// #3
// type conversion index- valid 1
:admin
drop public table uc;
drop public table uc2;
create public table indexTest(b int);
create index idx on indexTest(b);
insert into indexTest values(123);
select * from indexTest where b=="123";

// #4
// type conversion index - valid 2
:admin
insert into indexTest values("0xff");
select * from indexTest where b=="0xff";

// #5
// type conversion index - valid 3 
:admin
insert into indexTest values("0f");
insert into indexTest values("0d");
select * from indexTest where b=="0f" || b=="0d";

// #6
// type conversion index - invalid 4
:admin
select * from indexTest where b=="";

// #7
// type conversion index - invalid 5 
:admin
select * from indexTest where b=="ac";
