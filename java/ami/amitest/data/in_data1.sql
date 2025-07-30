// #1
// alter table - add
:admin
create public table primary (s String, a int, b int);
insert into primary values ("FE", 18, 14);
alter public table primary add c int;
insert into primary values ("BE", 12, 9, 12);
insert into primary values ("BC", 12, 9, 12);

// #2
// alter table - rename
alter public table primary rename s to addr;

// #3
// alter table - drop
drop public table primary;
create public table primary (s String, a int, b int, c int, d int);
insert into primary values ("FE", 18, 14, 28, 7);
insert into primary values ("BE", 12, 9, 12, 5);
insert into primary values ("BC", 12, 9, 12, 6);
alter public table primary drop d;

// #4
// alter table - before
drop public table primary;
create public table primary (s String, a int, b int, c int);
alter public table primary add x int before c;
insert into primary values ("FE", 18, 14, 1, 28);
insert into primary values ("BE", 12, 9, 2, 12);
insert into primary values ("BC", 12, 9, 13, 6);

// #5
// alter table - change column type
drop public table primary;
create public table primary (s  String, a int, b int, c double);
alter public table primary modify c as c int;
insert into primary values ("FE", 18, 14, 28);

// #6
// logical "and" and "as"
drop public table primary;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 12, 9, 12);
insert into primary values ("BC", 12, 9, 16);
create public table target as select * from primary where s=="BC" and c==16;

// #7
// logical "or"
drop public table target;
create public table target as select * from primary where s=="BC" or s=="BE";
drop public table primary;

// #8
// logical "not"
drop public table target;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 1);
insert into primary values ("BE", 12, 9, 2);
insert into primary values ("BC", 12, 9, 13);
create public table target as select * from primary where not s=="BC";
drop public table primary;

// #9
// order by "asc"
drop public table target;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 16, 9, 12);
insert into primary values ("BC", 12, 9, 12);
create public table target as select * from primary order by a asc limit 1;

// #10
// order by "desc" and "limit"
drop public table target;
create public table target as select * from primary order by a desc limit 1;
drop public table primary;

// #11
// "in" clause
drop public table target;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 12, 9, 12);
insert into primary values ("BC", 12, 9, 16);
create public table target as select * from primary where c in (12, 16);

// #12
// count(*)
drop public table target;
create public table target as select count(*) from primary;

// #13
// countUnique(*)
drop public table target;
create public table target as select countUnique(*) from primary;
drop public table primary;

// #14
// cat()
drop public table target;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 12, 9, 12);
insert into primary values ("BC", 12, 9, 16);
create public table target as select cat(b, "/", 3), cat(a, "\\", 2) from primary;
drop public table primary;

// #15
// avgGauss()
drop public table target;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 12, 9, 12);
insert into primary values ("BC", 12, 9, 16);
create public table target as select avgGauss(b, 0.2) from primary;
drop public table primary;

// #16
// avgGauss()
drop public table target;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 12, 9, 12);
insert into primary values ("BC", 12, 9, 16);
create public table target as select avgExp(b, 0.3, true) from primary;
create public table target2 as select avgExp(b, 0.3, false) from primary;
drop public table primary;

// #17
// sum()
drop public table target;
drop public table target2;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 16, 9, 12);
insert into primary values ("BC", 12, 9, 12);
create public table target as select sum(a),sum(b),sum(c) from primary;
drop public table primary;

// #18
// min()
drop public table target;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 16, 9, 12);
insert into primary values ("BC", 12, 9, 12);
create public table target as select min(a),min(b),min(c) from primary;
drop public table primary;

// #19
// max()
drop public table target;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 16, 9, 12);
insert into primary values ("BC", 12, 9, 12);
create public table target as select max(a),max(b),max(c) from primary;
drop public table primary;

// #20
// avg()
drop public table target;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 16, 9, 12);
insert into primary values ("BC", 12, 9, 12);
create public table target as select avg(a),avg(b),avg(c) from primary;
drop public table primary;

// #21
// var()
drop public table target;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 16, 9, 12);
insert into primary values ("BC", 12, 9, 12);
create public table target as select var(a),var(b),var(c) from primary;
drop public table primary;

// #22
// varS()
drop public table target;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 16, 9, 12);
insert into primary values ("BC", 12, 9, 12);
create public table target as select varS(a),varS(b),varS(c) from primary;
drop public table primary;

// #23
// stdev()
drop public table target;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 16, 9, 12);
insert into primary values ("BC", 12, 9, 12);
create public table target as select stdev(a),stdev(b),stdev(c) from primary;
drop public table primary;

// #24
// stdevS()
drop public table target;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 16, 9, 12);
insert into primary values ("BC", 12, 9, 12);
create public table target as select stdevS(a),stdevS(b),stdevS(c) from primary;
drop public table primary;

// #25
// first()
drop public table target;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 16, 9, 12);
insert into primary values ("BC", 12, 9, 12);
create public table target as select first(a),first(b),first(c) from primary;
drop public table primary;

// #26
// last()
drop public table target;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 16, 9, 12);
insert into primary values ("BC", 12, 9, 12);
create public table target as select last(a),last(b),last(c) from primary;
drop public table primary;

// #27
// covar()
drop public table target;
create public table primary (a int, b int, c int);
insert into primary values (18, 14, 28);
insert into primary values (16, 9, 12);
insert into primary values (12, 9, 12);
create public table target as select covar(a,b),covar(b,c) from primary;
drop public table primary;

// #28
// covarS()
drop public table target;
create public table primary (a int, b int, c int);
insert into primary values (18, 14, 28);
insert into primary values (16, 9, 12);
insert into primary values (12, 9, 12);
create public table target as select covarS(a,b),covarS(b,c) from primary;
drop public table primary;

// #29
// cor()
drop public table target;
create public table primary (a int, b int, c int);
insert into primary values (18, 14, 28);
insert into primary values (16, 9, 12);
insert into primary values (12, 9, 12);
create public table target as select cor(a,b),cor(b,c) from primary;
drop public table primary;

// #30
// beta()
drop public table target;
create public table primary (a int, b int, c int);
insert into primary values (18, 14, 28);
insert into primary values (16, 9, 12);
insert into primary values (12, 9, 12);
create public table target as select beta(a,b),beta(b,c) from primary;
drop public table primary;

// #31
// median()
drop public table target;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 16, 9, 12);
insert into primary values ("BC", 12, 9, 12);
create public table target as select median(a),median(b),median(c) from primary;
drop public table primary;

// #32
// describe table
drop public table target;
create public table primary (s String, a int, b int, c int);
create public table target as describe table primary;
drop public table primary;

// #33
// show table
drop public table target;
create public table primary (s String, a int, b int, c int);
create public table target as show table primary;
drop public table primary;

// #34
// delete
drop public table target;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 16, 9, 12);
insert into primary values ("BC", 12, 9, 12);
delete from primary where s=="BC";

// #35
// except
drop public table primary;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 16, 9, 12);
insert into primary values ("BC", 12, 9, 12);
create public table target as select * except(c) from primary;
drop public table primary;

// #36
// group by
drop public table target;
create public table primary (num int, country String);
insert into primary values (1, "Argentina");
insert into primary values (2, "Argentina");
insert into primary values (2, "Austria");
insert into primary values (6, "Austria");
insert into primary values (7, "Austria");
insert into primary values (2, "Belgium");
insert into primary values (9, "Brazil");
insert into primary values (8, "Brazil");
create public table target as select count(num), country from primary group by country;

// #37
// having
drop public table target;
create public table target as select count(num), country from primary group by country having count(num) > 2;
drop public table primary;

// #38
// index and show
drop public table target;
create public table primary (s String, a int, b int, c int);
create index primaryIndex on primary (s HASH) USE CONSTRAINT="PRIMARY";
create public table target as show indexes where IndexName=="primaryIndex";
drop public table primary;

// #39
// set and update
drop public table target;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 16, 7, 12);
insert into primary values ("BC", 12, 9, 12);
update primary set s = "EE",b = 14,c = 27 where b == 14;
create public table target as select * from primary;
drop public table primary;

// #40
// truncate
drop public table target;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 1);
insert into primary values ("BE", 12, 9, 2);
insert into primary values ("BC", 12, 9, 13);
truncate table primary;
insert into primary values ("YZ", 11, 6, 10);

// #41
// realtime - left join
drop public table primary;
create public table duplicates (s String, x int, y int, z int);
create public table primary (s String, a int, b int, c int);
insert into duplicates values ("FE", 28, 7, 10);
insert into duplicates values ("FA", 21, 6, 12);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 12, 9, 12);
insert into primary values ("BC", 12, 9, 12);
create public table target as select * from primary left join duplicates on duplicates.s==primary.s;
drop public table primary;
drop public table duplicates;

// #42
// realtime - right join
drop public table target;
create public table duplicates (s String, x int, y int, z int);
create public table primary (s String, a int, b int, c int);
insert into duplicates values ("FE", 28, 7, 10);
insert into duplicates values ("FA", 21, 6, 12);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 12, 9, 12);
insert into primary values ("BC", 12, 9, 12);
create public table target as select * from primary right join duplicates on duplicates.s==primary.s;
drop public table primary;
drop public table duplicates;

// #43
// realtime - outer join
drop public table target;
create public table duplicates (s String, x int, y int, z int);
create public table primary (s String, a int, b int, c int);
insert into duplicates values ("FE", 28, 7, 10);
insert into duplicates values ("FA", 21, 6, 12);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 12, 9, 12);
insert into primary values ("BC", 12, 9, 12);
create public table target as select * from primary outer join duplicates on duplicates.s==primary.s;
drop public table primary;
drop public table duplicates;

// #44
// realtime - left only join
drop public table target;
create public table duplicates (s String, x int, y int, z int);
create public table primary (s String, a int, b int, c int);
insert into duplicates values ("FE", 28, 7, 10);
insert into duplicates values ("FA", 21, 6, 12);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 12, 9, 12);
insert into primary values ("BC", 12, 9, 16);
create public table target as select * from primary left only join duplicates on duplicates.s==primary.s;
drop public table primary;
drop public table duplicates;

// #45
// realtime - right only join
drop public table target;
create public table duplicates (s String, x int, y int, z int);
create public table primary (s String, a int, b int, c int);
insert into duplicates values ("FE", 28, 7, 10);
insert into duplicates values ("FA", 27, 8, 10);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 12, 9, 12);
insert into primary values ("BC", 12, 9, 16);
create public table target as select * from primary right only join duplicates on duplicates.s==primary.s;
drop public table primary;
drop public table duplicates;

// #46
// realtime - inner join
drop public table target;
create public table duplicates (s String, x int, y int, z int);
create public table primary (s String, a int, b int, c int);
insert into duplicates values ("FE", 28, 7, 10);
insert into duplicates values ("FA", 27, 8, 10);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 12, 9, 12);
insert into primary values ("BC", 12, 9, 16);
create public table target as select * from primary join duplicates on duplicates.s==primary.s;
drop public table primary;
drop public table duplicates;

// #47
// nearest
drop public table target;
create public table a (time int, px double);
create public table b (time int, px double);
insert into a values(0, 12);
insert into a values(10, 14);
insert into a values(20, 16);
insert into b values(3, 11);
insert into b values(9, 14);
insert into b values(11, 15);
insert into b values(20, 19);
create public table c as select * from b join a on a.time==b.time;
create public table target as select * from b join a nearest a.time==b.time;
drop public table a;
drop public table b;
drop public table c;

// #48
// use
drop public table target;
create public table target as select * from use ds="AMI" show tables;

// #49
// concurrent
concurrent { create public table a (time int, px double);create public table b (time int, px double); };
insert into a values(0, 12);
insert into a values(10, 14);
insert into a values(20, 16);
insert into b values(3, 11);
insert into b values(9, 14);
insert into b values(11, 15);
insert into b values(20, 19);

// #50
// partition by
drop public table a;
drop public table b;
drop public table target;
create public table primary (x int, y int);
insert into primary values (1, 10);
insert into primary values (1, 8);
insert into primary values (2, 12);
create public table target as prepare *, sum(y) from primary partition by x;
drop public table primary;

// #51
// unpack
drop public table target;
create public table primary (orderId String,accounts String);
insert into primary values("ord1","a1,a2,a3"),("ord2","b4,b5"),("ord3","");
create public table target as select * from primary unpack accounts ON ",";
drop public table primary;

// #52
// analyze and window
drop public table target;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 16, 9, 12);
insert into primary values ("BC", 12, 9, 12);
create public table target as analyze sum(win.a) as sum, min(win.a) as min, max(win.b) as max from primary window win on win.c <= 26;
drop public table primary;

// #53
// prepare and stack()
drop public table target;
create public table primary (orderid string,account string,qty long);
insert into primary values("ord1","a1",1200),("ord2","b5",800),("ord3","b7",500);
create public table target as prepare account, qty, stack(qty) as acc_qty from primary;
drop public table primary;

// #54
// norm()
drop public table target;
create public table primary (orderid string,account string,qty long);
insert into primary values("ord1","a1",1200),("ord2","b5",800),("ord3","b7",500);
create public table target as prepare account, qty, norm(qty) as acc_qty from primary;
drop public table primary;

// #55
// dnorm()
drop public table target;
create public table primary (orderid string,account string,qty long);
insert into primary values("ord1","a1",1200),("ord2","b5",800),("ord3","b7",500);
create public table target as prepare account, qty, dnorm(qty) as acc_qty from primary;
drop public table primary;

// #56
// rank()
drop public table target;
create public table primary (orderid string,account string,qty long);
insert into primary values("ord1","a1",1200),("ord2","b5",800),("ord3","b7",500);
create public table target as prepare rank(qty) as acc_qty from primary;
drop public table primary;

// #57
// urank()
drop public table target;
create public table primary (orderid string,account string,qty long);
insert into primary values("ord1","a1",1200),("ord2","b5",800),("ord3","b7",500);
create public table target as prepare urank(qty) as acc_qty from primary;
drop public table primary;

// #58
// streaming realtime - left join trigger
drop public table target;
create public table duplicates (s String, x int, y int, z int);
create public table primary (s String, a int, b int, c int);
create public table target (s String, x int, y int, z int, a int, b int, c int);
create trigger test_primary_join oftype join on primary, duplicates, target use type="left" on="duplicates.s==primary.s" selects="s=primary.s, x=x, y=y, z=z, a=a, b=b, c=c";
insert into duplicates values ("FG", 28, 7, 10);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 12, 9, 12);
insert into primary values ("BE", 12, 9, 12);
drop trigger test_primary_join;
drop public table primary;
drop public table duplicates;

// #59
// streaming realtime - right join trigger
drop public table target;
create public table duplicates (s String, x int, y int, z int);
create public table primary (s String, a int, b int, c int);
create public table target (s String, x int, y int, z int, a int, b int, c int);
create trigger test_primary_join oftype join on primary, duplicates, target use type="right" on="duplicates.s==primary.s" selects="s=primary.s, x=x, y=y, z=z, a=a, b=b, c=c";
insert into primary values ("EB", 24, 11, 16);
insert into duplicates values ("AE", 3, 25, 19);
drop trigger test_primary_join;
drop public table primary;
drop public table duplicates;

// #60
// streaming realtime - outer join trigger
drop public table target;
create public table duplicates (s String, x int, y int, z int);
create public table primary (s String, a int, b int, c int);
create public table target (s String, x int, y int, z int, a int, b int, c int);
create trigger test_primary_join oftype join on primary, duplicates, target use type="outer" on="duplicates.s==primary.s" selects="s=primary.s, x=x, y=y, z=z, a=a, b=b, c=c";
insert into primary values ("CB", 12, 0, 25);
insert into primary values ("EA", 1, 18, 19);
insert into primary values ("AG", 2, 2, 0);
insert into duplicates values ("GA", 14, 22, 28);
insert into primary values ("BD", 8, 10, 10);
drop trigger test_primary_join;
drop public table primary;
drop public table duplicates;

// #61
// streaming realtime - left only join trigger
drop public table target;
create public table duplicates (s String, x int, y int, z int);
create public table primary (s String, a int, b int, c int);
create public table target (s String, x int, y int, z int, a int, b int, c int);
create trigger test_primary_join oftype join on primary, duplicates, target use type="left only" on="duplicates.s==primary.s" selects="s=primary.s, x=x, y=y, z=z, a=a, b=b, c=c";
insert into duplicates values ("AB", 7, 9, 19);
insert into primary values ("CC", 28, 24, 29);
insert into primary values ("CA", 23, 24, 0);
insert into primary values ("GF", 19, 0, 8);
insert into duplicates values ("GC", 24, 19, 18);
insert into duplicates values ("CB", 3, 1, 1);
drop trigger test_primary_join;
drop public table primary;
drop public table duplicates;

// #62
// streaming realtime - right only join trigger
drop public table target;
create public table duplicates (s String, x int, y int, z int);
create public table primary (s String, a int, b int, c int);
create public table target (s String, x int, y int, z int, a int, b int, c int);
create trigger test_primary_join oftype join on primary, duplicates, target use type="right only" on="duplicates.s==primary.s" selects="s=primary.s, x=x, y=y, z=z, a=a, b=b, c=c";
insert into duplicates values ("EG", 21, 8, 26);
insert into duplicates values ("BC", 24, 24, 20);
insert into duplicates values ("AB", 24, 5, 15);
insert into primary values ("CA", 11, 25, 12);
insert into duplicates values ("AF", 6, 1, 21);
insert into duplicates values ("AG", 12, 26, 2);
insert into duplicates values ("BE", 27, 16, 16);
insert into primary values ("GD", 18, 11, 28);
insert into primary values ("AD", 23, 11, 21);
insert into duplicates values ("CG", 6, 11, 24);
insert into duplicates values ("CG", 0, 0, 17);
insert into duplicates values ("GG", 21, 17, 28);
drop trigger test_primary_join;
drop public table primary;
drop public table duplicates;

// #63
// streaming realtime - outer only join trigger
drop public table target;
create public table duplicates (s String, x int, y int, z int);
create public table primary (s String, a int, b int, c int);
create public table target (s String, x int, y int, z int, a int, b int, c int);
create trigger test_primary_join oftype join on primary, duplicates, target use type="outer only" on="duplicates.s==primary.s" selects="s=primary.s, x=x, y=y, z=z, a=a, b=b, c=c";
insert into primary values ("CB", 22, 14, 3);
insert into primary values ("DC", 12, 17, 6);
update duplicates set s = "CA",z = 29 where z == 6;
update duplicates set s = "EB",z = 20 where z == 3;
insert into primary values ("AF", 3, 7, 21);
insert into duplicates values ("EG", 20, 28, 3);
insert into duplicates values ("AC", 21, 13, 17);
insert into primary values ("FE", 0, 14, 3);
insert into primary values ("EA", 19, 10, 20);
update duplicates set y = 22,z = 1 where y == 17;
insert into primary values ("CE", 20, 24, 17);
drop trigger test_primary_join;
drop public table primary;
drop public table duplicates;

// #64
// streaming realtime - inner join trigger
drop public table target;
create public table duplicates (s String, x int, y int, z int);
create public table primary (s String, a int, b int, c int);
create public table target (s String, x int, y int, z int, a int, b int, c int);
create trigger test_primary_join oftype join on primary, duplicates, target use type="inner" on="duplicates.s==primary.s" selects="s=primary.s, x=x, y=y, z=z, a=a, b=b, c=c";
insert into primary values ("CB", 22, 14, 3);
insert into primary values ("DC", 12, 17, 6);
update duplicates set s = "CB",z = 29 where z == 6;
update duplicates set s = "DC",z = 20 where z == 3;
insert into primary values ("AF", 3, 7, 21);
insert into duplicates values ("EA", 20, 28, 3);
insert into duplicates values ("AF", 21, 13, 17);
insert into primary values ("FE", 0, 14, 3);
insert into primary values ("EA", 19, 10, 20);
update duplicates set y = 22,z = 1 where y == 17;
insert into primary values ("CE", 20, 24, 17);
drop trigger test_primary_join;
drop public table primary;
drop public table duplicates;

// #65
// streaming realtime - left join trigger disable
drop public table target;
create public table duplicates (s String, x int, y int, z int);
create public table primary (s String, a int, b int, c int);
create public table target (s String, x int, y int, z int, a int, b int, c int);
create trigger test_primary_join oftype join on primary, duplicates, target use type="left" on="duplicates.s==primary.s" selects="s=primary.s, x=x, y=y, z=z, a=a, b=b, c=c";
disable trigger test_primary_join;
insert into duplicates values ("FG", 28, 7, 10);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 12, 9, 12);
insert into primary values ("BE", 12, 9, 12);

// #66
// streaming realtime - left join trigger enable
enable trigger test_primary_join;
insert into duplicates values ("FG", 29, 8, 11);
insert into primary values ("FE", 19, 15, 29);
insert into primary values ("BE", 13, 10, 13);
insert into primary values ("BE", 13, 10, 13);
drop trigger test_primary_join;
drop public table primary;
drop public table duplicates;

// #67
// priority
drop public table target;
create public table primary (s string, a long, b long, c long);
create trigger test_priority oftype amiscript on primary priority 1 use canMutateRow="true" onInsertingScript="c=a*b";
insert into primary(s,a,b) values ("fe", 18, 14);
insert into primary(s,a,b) values ("be", 16, 9);
insert into primary(s,a,b) values ("bc", 12, 9);
create public table target as select * from primary;
drop trigger test_priority;
drop public table primary;

// #68
// sync
drop public table target;
create public table primary (s String, a int, b int, c int);
create public table target as select * from primary;
insert into target values ("FE", 18, 14, 28);
insert into primary values ("BE", 12, 9, 12);
insert into primary values ("BC", 12, 9, 1);
insert into primary values ("BC", 12, 9, 1);
sync target select * from primary;
drop public table primary;

// #69
// step/for
drop public table target;
create public table primary (s string, a int, b int, c int);
int i; insert into primary for i=1 to 100 step 2 values ("BC", 12, 9, 7);
create public table target as select * from primary;
drop public table primary;

// #70
// stored procedure and call
drop public table target;
create public table primary (s String, a int, b int, c int);
insert into primary values ("FE", 18, 14, 28);
insert into primary values ("BE", 16, 9, 12);
insert into primary values ("BC", 12, 9, 12);
create procedure test_procedure oftype amiscript use arguments="String _s,int _a,int _b,int _c" script="delete from primary where a<_a;insert into primary values(_s,_a,_b,_c)";
call test_procedure("EA", 13, 8, 11);
create public table target as select * from primary;
drop procedure test_procedure;
drop public table primary;

// #71
// subqueries
drop public table target;
create public table marks (studentid string, total_marks int);
insert into marks values("V09786", 95);
insert into marks values("V09735", 80);
insert into marks values("V09661", 74);
insert into marks values("V08641", 82);
create public table target as select studentid, total_marks from marks where total_marks in (select total_marks from marks where studentid == "V09735");
drop public table marks;

// #72
// insering table with NULL/not NULL columns
drop public table target;
create public table primary (s String, a int, b String NoNull, c int);
insert into primary(a, b, c) values (18, "14", 28);
insert into primary(s, a, b, c) values ("BE", 12, "9", 12);
insert into primary(a, c) values (12, 16);
create public table target as select * from primary;
drop public table primary;

// #73
// abs()
drop public table target;
create public table target as select abs(-23.5) as absnum;

// #74
// acos()
drop public table target;
create public table target (a double);
insert into target values(acos(0));
insert into target values(acos(0.25));
insert into target values(acos(1));

// #75
// asin()
drop public table target;
create public table target (a double);
insert into target values(asin(0));
insert into target values(asin(0.25));
insert into target values(asin(1));

// #76
// atan()
drop public table target;
create public table target (a double);
insert into target values(atan(0));
insert into target values(atan(0.25));
insert into target values(atan(1));

// #77
// brighten()
drop public table target;
create public table target (a String);
insert into target values(brighten("#FF0000",0));
insert into target values(brighten("#FF0000",0.5));
insert into target values(brighten("#FF0000",-0.5));
insert into target values(brighten("#880088",0));
insert into target values(brighten("#880088",0.1));
insert into target values(brighten("#880880",-1));

// #78
// colorCycle()
drop public table target;
create public table target (a String);
insert into target values(colorCycle(0));
insert into target values(colorCycle(5));
insert into target values(colorCycle(-1));

// #79
// cos()
drop public table target;
create public table target (a double);
insert into target values(cos(0));
insert into target values(cos(10.8));
insert into target values(cos(1));

// #80
// cycle()
drop public table target;
create public table primary (s String);
insert into primary values(cycle(0,"zero","one","two","three"));
insert into primary values(cycle(5,"zero","one","two","three"));
insert into primary values(cycle(-1,"zero","one","two","three"));
insert into primary values(cycle(17,"even","odd"));
create public table target as select * from primary;
drop public table primary;

// #81
// datePart() and datePartNum()
drop public table target;
create public table target (a long);
insert into target values(datePart(100000000,"y","EST5EDT"));
insert into target values(datePart(100000000,"HmsS","UTC"));
insert into target values(datePartNum(100000000,"y","EST5EDT"));
insert into target values(datePartNum(100000000,"HmsS","UTC"));
insert into target values(datePartNum(100000000,"E","UTC"));

// #82
// digamma()
drop public table target;
create public table primary (s double);
insert into primary values(digamma(-1));
insert into primary values(digamma(0));
insert into primary values(digamma(1));
insert into primary values(digamma(2.5));
create public table target as select * from primary;
drop public table primary;

// #83
// exp()
drop public table target;
create public table target as select exp(4.8) as value;

// #84
// gradient()
drop public table target;
create public table target (a String);
insert into target values(gradient(0.5D,0,"#000000",1,"#FFFFFF"));

// #85
// isInstanceOf()
drop public table target;
create public table target (a boolean);
insert into target values(isInstanceOf(1234L,"Long"));
insert into target values(isInstanceOf(1234L,"Integer"));
insert into target values(isInstanceOf("test","String"));
insert into target values(isInstanceOf(null,"String"));

// #86
// jsonExtract()
drop public table target;
create public table target (a String);
insert into target values(jsonExtract("\"simple\"",""));
insert into target values(jsonExtract("{a:1,b:2}","a"));
insert into target values(jsonExtract("{a:1,b:['rob','dave','steve\\'s']}","b"));
insert into target values(jsonExtract("{a:1,b:['rob','dave','steve\\'s']}","b.2"));
insert into target values(jsonExtract("{a:1,b:['rob','dave','steve\\'s',{e:1,f:2}]]},","b.2.f"));

// #87
// ln()
drop public table target;
create public table target (a double);
insert into target values(ln(0));
insert into target values(ln(10));
insert into target values(ln(2.71828));

// #88
// lnGamma()
drop public table target;
create public table target (a double);
insert into target values(lnGamma(0));
insert into target values(lnGamma(10));
insert into target values(lnGamma(2.71828));

// #89
// log()
drop public table target;
create public table target (a double);
insert into target values(log(100,10));
insert into target values(log(1000,10));
insert into target values(log(4,2));
insert into target values(log(8,2));
insert into target values(log(16,2));

// #90
// noNull()
drop public table target;
create public table target (a int);
insert into target values(noNull(32,46));
insert into target values(noNull(null,46));
insert into target values(noNull(null,null,20,null));
insert into target values(noNull(null,null,null,null));

// #91
// parseDate()
drop public table target;
create public table target (a long);
insert into target values(parseDate("19991213-11:00:05.765", "yyyyMMdd-HH:mm:ss.SSS"));
insert into target values(parseDate("20155030-10:30:40.123", "yyyyMMdd-HH:mm:ss.SSS", "EST5EDT"));
insert into target values(parseDate("19:01:53", "HH:mm:ss"));

// #92
// parseJson()
drop public table target;
create public table target (a int, b String, c boolean);
insert into target values(parseJson("17"), parseJson("'red'"), parseJson("true"));

// #93
// power()
drop public table target;
create public table target (a double);
insert into target values(power(10,1));
insert into target values(power(10,0));
insert into target values(power(10,3));
insert into target values(power(5.3,4.2));

// #94
// quote()
drop public table target;
create public table target (a String);
insert into target values(quote("simple"));
insert into target values(quote("this \"is\" less simple"));

// #95
// round()
drop public table target;
create public table target (a double);
insert into target values(round(32.5));
insert into target values(round(-32.5));
insert into target values(round(32.2));
insert into target values(round(-32.2));
insert into target values(round(32.8));
insert into target values(round(-32.8));
insert into target values(round(14));
insert into target values(round(-14));

// #96
// roundDown()
drop public table target;
create public table target (a double);
insert into target values(roundDown(32.5));
insert into target values(roundDown(-32.5));
insert into target values(roundDown(32.2));
insert into target values(roundDown(-32.2));
insert into target values(roundDown(32.8));
insert into target values(roundDown(-32.8));
insert into target values(roundDown(14));
insert into target values(roundDown(-14));

// #97
// scale()
drop public table target;
create public table target (a double);
insert into target values(scale(0.5,0,10,1,20,2,100));
insert into target values(scale(1.5,0,10,1,20,2,100));
insert into target values(scale(2.5,0,10,1,20,2,100));
insert into target values(scale(-0.5,0,10,1,20,2,100));

// #98
// sin()
drop public table target;
create public table target (a double);
insert into target values(sin(0));
insert into target values(sin(10));
insert into target values(sin(3));

// #99
// strAfter()
drop public table target;
create public table target (a String);
insert into target values(strAfter("What now"," ",true));
insert into target values(strAfter("what,now"," ",true));
insert into target values(strAfter("what,now"," ",false));
insert into target values(strAfter("what,now",null,false));
insert into target values(strAfter("what,now",null,true));
insert into target values(strAfter(null," ",false));
insert into target values(strAfter("An example,of, multiple, delims",", ",true));

// #100
// strAfterLast()
drop public table target;
create public table target (a String);
insert into target values(strAfterLast("What now"," ",true));
insert into target values(strAfterLast("what,now"," ",true));
insert into target values(strAfterLast("what,now"," ",false));
insert into target values(strAfterLast("what,now",null,false));
insert into target values(strAfterLast("what,now",null,true));
insert into target values(strAfterLast(null," ",false));
insert into target values(strAfterLast("An example,of, multiple, delims",", ",true));

// #101
// strBefore()
drop public table target;
create public table target (a String);
insert into target values(strBefore("What now"," ",true));
insert into target values(strBefore("what,now"," ",true));
insert into target values(strBefore("what,now"," ",false));
insert into target values(strBefore("what,now",null,false));
insert into target values(strBefore("what,now",null,true));
insert into target values(strBefore(null," ",false));
insert into target values(strBefore("An example,of, multiple, delims",", ",true));

// #102
// strBeforeLast()
drop public table target;
create public table target (a String);
insert into target values(strBeforeLast("What now"," ",true));
insert into target values(strBeforeLast("what,now"," ",true));
insert into target values(strBeforeLast("what,now"," ",false));
insert into target values(strBeforeLast("what,now",null,false));
insert into target values(strBeforeLast("what,now",null,true));
insert into target values(strBeforeLast(null," ",false));
insert into target values(strBeforeLast("An example,of, multiple, delims",", ",true));

// #103
// strIndexOf()
drop public table target;
create public table target (a int);
insert into target values(strIndexOf("What now"," ",0,true));
insert into target values(strIndexOf("what,now","hat"));
insert into target values(strIndexOf("what,now","what"));
insert into target values(strIndexOf("What now"," "));
insert into target values(strIndexOf("what,now"," "));
insert into target values(strIndexOf("what,now",""));
insert into target values(strIndexOf("what,now",null));
insert into target values(strIndexOf(null,"test"));
insert into target values(strIndexOf("An example,of, multiple, delims",", "));

// #104
// strLen()
drop public table target;
create public table target (a int);
insert into target values(strLen(""));
insert into target values(strLen("AMI Rocks"));
insert into target values(strLen("\n\n\n"));

// #105
// strReplace()
drop public table target;
create public table target (a String);
insert into target values(strReplace("cat in the hat","at","an"));
insert into target values(strReplace("tististis","tis","a"));
insert into target values(strReplace("Some example","",","));
insert into target values(strReplace("Several\nlines\nare\none\nline","\n"," "));

// #106
// strSplice()
drop public table target;
create public table target (a String);
insert into target values(strSplice("this is test",5,2,"was"));
insert into target values(strSplice("this is test",5,2,null));
insert into target values(strSplice("this is test",8,0,"inserted "));
insert into target values(strSplice("this is test",100,0," of limits"));

// #107
// strStrip()
drop public table target;
create public table target (a String);
insert into target values(strStrip("What is Going On","What","On"));
insert into target values(strStrip("WhatOn","What","On"));
insert into target values(strStrip("When are we on","What","On"));

// #108
// strSubstring
drop public table target;
create public table target (a String);
insert into target values(strSubstring("this",1,2));
insert into target values(strSubstring("this",0,4));
insert into target values(strSubstring("this",2,100));
insert into target values(strSubstring("this",-10,1));

// #109
// strTrim()
drop public table target;
create public table target (a String);
insert into target values(strTrim("No Change!"));
insert into target values(strTrim(" Change \t\n\r "));
insert into target values(strTrim(" "));

// #110
// switch()
drop public table target;
create public table target (a String);
insert into target values(switch(0,"NA",0,"ZERO",1,"ONE",2,"TWO",null,"NULL"));
insert into target values(switch(2,"NA",0,"ZERO",1,"ONE",2,"TWO",null,"NULL"));
insert into target values(switch(7,"NA",0,"ZERO",1,"ONE",2,"TWO",null,"NULL"));
insert into target values(switch(null,"NA",0,"ZERO",1,"ONE",2,"TWO",null,"NULL"));

// #111
// tan()
drop public table target;
create public table target (a double);
insert into target values(tan(0));
insert into target values(tan(10));
insert into target values(tan(3));

// #112
// timezoneOffset()
drop public table target;
create public table target (a long);
insert into target values(timezoneOffset(0,"EST5EDT"));
insert into target values(timezoneOffset(1152590400000L,"EST5EDT"));
insert into target values(timezoneOffset(1165640400000L,"EST5EDT"));
insert into target values(timezoneOffset(1234567L,"UTC"));

// #113
// toJson
drop public table target;
create public table target (a String);
insert into target values(toJson("test",true));

// #114
// primary index -> will do upsert
drop public table target;
create public table primary (s String, a int, b int, c int, d int);
create index primary_idx on primary(s hash) use constraint="primary";
insert into primary values ("FE", 17, 14, 28, 7);
insert into primary values ("FE", 18, 15, 28, 8);
insert into primary values ("FE", 19, 16, 28, 9);
drop index primary_idx on primary;

// #115
// unique index with hash
drop public table primary;
create public table primary (s String, a int, b int, c int, d int);
create index unique_idx on primary(s hash) use constraint="unique";
insert into primary values ("FE", 17, 14, 28, 7);
insert into primary values ("FE", 18, 15, 28, 8);
insert into primary values ("FE", 19, 16, 28, 9);
drop index unique_idx on primary;

// #116
// unique index with sort
drop public table primary;
create public table primary (s String, a int, b int, c int, d int);
create index unique_idx on primary(s sort) use constraint="unique";
insert into primary values ("FE", 17, 14, 28, 7);
insert into primary values ("FE", 18, 15, 28, 8);
insert into primary values ("FE", 19, 16, 28, 9);
drop index unique_idx on primary;

// #117
// unique index with series
drop public table primary;
create public table primary (s long, a int, b int, c int, d int);
create index unique_idx on primary(s series) use constraint="unique";
insert into primary values (0xFE, 17, 14, 28, 7);
insert into primary values (0xFE, 18, 15, 28, 8);
insert into primary values (0xFE, 19, 16, 28, 9);
drop index unique_idx on primary;

// #118
// no constraint index
drop public table primary;
create public table primary (s String, a int, b int, c int, d int);
create index simple_idx on primary(s hash);
insert into primary values ("FE", 17, 14, 28, 7);
insert into primary values ("FE", 18, 15, 28, 8);
insert into primary values ("FE", 19, 16, 28, 9);
drop index simple_idx on primary;

// #119
// table name with backticks and out to compare with checksum data
drop public table primary;
create public table `primary tbl` (s String, a int, b int, c int, d int);
insert into `primary tbl` values ("FE", 17, 14, 28, 7);
insert into `primary tbl` values ("FA", 18, 15, 28, 8);
insert into `primary tbl` values ("FD", 19, 16, 28, 9);

// #120
// table name with backticks
drop public table `primary tbl`;
create public table `primary tbl` (s String, a int, b int, c int, d int);
insert into `primary tbl` values ("FE", 17, 14, 28, 7);
insert into `primary tbl` values ("FA", 18, 15, 28, 8);
insert into `primary tbl` values ("FD", 19, 16, 28, 9);
create public table target as select * from `primary tbl`;

