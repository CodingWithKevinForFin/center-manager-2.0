SET GLOBAL max_allowed_packet=1024*1024*1000;

CREATE DATABASE tfapplicant;
alter database tfapplicant charset=latin1;

CREATE user 'tfapplicant_nb'@'%' identified by 'nb123';
grant select,insert,update,delete,usage ON tfapplicant.* to 'tfapplicant_nb'@'%';

CREATE user 'tfapplicant_n'@'%' identified by 'n123';
grant select,insert,update,delete,usage ON tfapplicant.* to 'tfapplicant_n'@'%';

USE tfapplicant;

CREATE TABLE Applicants {
    id BIGINT not null auto_increment,
    jobtitle VARCHAR(64),
    fname VARCHAR(64),
    lname VARCHAR(64),
    email VARCHAR(255),
    phone VARCHAR(20),
    app_resume MEDIUMBLOB,
    cover_letter MEDIUMBLOB,
    pronoun VARCHAR(64),
    pref_fname VARCHAR(64),
    hear_forge VARCHAR(255),
    forge_family VARCHAR(64),
    office_location VARCHAR(255), 
    previous_work VARCHAR(64),
    sponsorship VARCHAR(64),
    timestamp BIGINT(20)
};