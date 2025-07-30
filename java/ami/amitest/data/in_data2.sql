// #1
// create table
:admin|admin
create public table primary (s String, a int, b int);
insert into primary values ("FE", 18, 14);

// #2
// select
:joe0|joeschmo123
select * from primary;

// #3
// alter table - add
alter public table primary add c int;

// #4
// alter table - rename
alter public table primary rename s to addr;

// #5
// alter table - before
alter public table primary add x int before c;
insert into primary values ("FE", 18, 14, 1, 28);

// #6
// alter table - change column type
alter public table primary modify c as c double;
insert into primary values ("FE", 18, 14, 67, 28.6);

// #7
// alter table - drop
drop public table primary;

// #8
// stored procedure
:admin|admin
drop public table if exists primary;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 16, 9, 12);
insert into primary values ("BC", 12, 9, 12);
create procedure test_procedure oftype amiscript use arguments="String _s,int _a,int _b,int _c" script="delete from primary where a<_a;insert into primary values(_s,_a,_b,_c)";
create procedure test_procedure2 oftype amiscript use arguments="" script="select count(*) from primary";

// #9
// call stored procedure with read/write
:joe0|joeschmo123
call test_procedure("EA", 13, 8, 11);

// #10
// call stored procedure with read
call test_procedure2();

// #11
// create table
:admin|admin
drop public table if not exists primary;
create public table primary (s String, a int, b int);
insert into primary values ("FE", 18, 14);

// #12
// select
:joe1|joeschmo123
select * from primary;

// #13
// alter table - add
alter public table primary add c int;

// #14
// alter table - rename
alter public table primary rename s to addr;

// #15
// alter table - before
alter public table primary add x int before c;
insert into primary values ("FE", 18, 14, 1, 28);

// #16
// alter table - change column type
alter public table primary modify c as c double;
insert into primary values ("FE", 18, 14, 67, 28.6);

// #17
// alter table - drop
drop public table primary;

// #18
// stored procedure
:admin|admin
drop public table if exists primary;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 16, 9, 12);
insert into primary values ("BC", 12, 9, 12);
create procedure if not exists test_procedure oftype amiscript use arguments="String _s,int _a,int _b,int _c" script="delete from primary where a<_a;insert into primary values(_s,_a,_b,_c)";

// #19
// call stored procedure with read/write
:joe1|joeschmo123
call test_procedure("EA", 13, 8, 11);

// #20
// call stored procedure with read
call test_procedure2();

