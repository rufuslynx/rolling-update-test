--важно: обновляем пачками и только подходящие записи
update test set username='default_username' where id between 1 and 1000 and username is null;
commit;
update test set username='default_username' where id between 1000 and 2000 and username is null;
commit;
update test set username='default_username' where id between 2000 and 3000 and username is null;
commit;
--теперь можем выставлять ограничение: предыдущая версия всегда пишет заполненное значение,
--и в текущей все записи обновлены на значение по-умолчанию
alter table test alter username varchar2(200) not null;

--в принципе, теперь можем установить и значение по умолчанию, в этом случае получим совместимость с v1
--alter table test alter username varchar2(200) not null default 'default_username';