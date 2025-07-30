// #1
// create table
:admin
create public table primary (s String, a int, b int);
insert into primary values ("FE", 18, 14);

// #2
// select
:joe2|joeschmo123
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

// #6
// alter table - change column type
alter public table primary modify c as c double;

// #7
// drop table and create table
create public table target (s String, a int, b int);
drop public table primary;

// #8
// stored procedure
:admin
drop public table if exists primary;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 16, 9, 12);
insert into primary values ("BC", 12, 9, 12);
create procedure if not exists test_procedure oftype amiscript use arguments="String _s,int _a,int _b,int _c" script="delete from primary where a<_a;insert into primary values(_s,_a,_b,_c)";
create procedure test_procedure2 oftype amiscript use arguments="" script="select count(*) from primary";

// #9
// call stored procedure with read/write
:joe2|joeschmo123
call test_procedure("EA", 13, 8, 11);

// #10
// call stored procedure with read
call test_procedure2();

