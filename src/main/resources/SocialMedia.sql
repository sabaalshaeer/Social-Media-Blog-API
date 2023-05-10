drop table if exists message;
drop table if exists account;
create table account (
    account_id int primary key auto_increment, --define a primary key column,A primary key is a column or set of columns in a table that uniquely identifies each row in the table
    username varchar(255) unique, --varcher is used to store character strings of varying lengths up to a maximum specified length
    password varchar(255)
);
create table message (
    message_id int primary key auto_increment, --define a primary key column
    posted_by int,
    message_text varchar(255),
    time_posted_epoch bigint, -- bigint is a data type in SQL that is used to store large integer values--
                              --it is likely used to store a Unix timestamp,for tracking time and date-related information.
    foreign key (posted_by) references  account(account_id) -- defines a foreign key constraint
);

insert into account (username, password) values ('testuser1', 'password');
insert into message (posted_by, message_text, time_posted_epoch) values (1,'test message 1',1669947792);
