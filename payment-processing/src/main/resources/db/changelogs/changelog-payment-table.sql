create table if not exists payment(
    id bigserial primary key,
    payer varchar(250) not null,
    recipient varchar(250) not null,
    amount numeric(20, 2) not null,
    status varchar(50) not null,
    row_insert_time timestamp not null,
    row_update_time timestamp not null,
    comment varchar(5000)
);

alter table payment
    add constraint payment_status_c1 check (
        status in ('NEW', 'VERIFICATION', 'ACCEPTED', 'DECLINED'))
