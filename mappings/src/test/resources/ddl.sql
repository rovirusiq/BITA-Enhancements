create table stable1(
	id number not null,
	desc varchar2(500)
);
create table ttablea(
	id number not null,
	desc varchar2(500),
	upd_dt date default sysdate
);