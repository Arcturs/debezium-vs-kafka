create table if not exists statistic(
    id bigserial primary key,
    status varchar(50) not null,
    count int not null default 0
);

alter table statistic
    add constraint statistic_status_c1 check (status in ('SUCCESS', 'FAILURE'))
