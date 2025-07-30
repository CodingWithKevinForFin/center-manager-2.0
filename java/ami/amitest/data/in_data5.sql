// #1 
// prepare statement with a limit.
:admin
create public table prep(fname string, lname string, age int);
insert into prep values("mir", "ahmed", 23);
insert into prep values("robert", "cooke", 41);
insert into prep values("george", "lin", 26);
insert into prep values("peter", "sibirzeff", 23);
insert into prep values("bill", "cooke", 41);
insert into prep values("david", "lee", 26);
insert into prep values("marc", "weinstein", 26);

create public table preptest1 as prepare fname, lname, age from prep limit;
create public table preptest2 as prepare fname, lname, age from prep limit 0;
create public table preptest3 as prepare fname, lname, age from prep limit 3;
create public table preptest4 as prepare fname, lname, age from prep limit 2, 0;
create public table preptest5 as prepare fname, lname, age from prep limit 2, 1;

// upto last row: skip first 4 rows and return next 3
create public table preptest6 as prepare fname, lname, age from prep limit 4, 3;

// out of bounds
create public table preptest7 as prepare fname, lname, age from prep limit 4, 4;


