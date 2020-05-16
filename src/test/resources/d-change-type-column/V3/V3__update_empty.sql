--важно: обновляем пачками и только подходящие записи
update test set idd=to_char(id) where id between 1 and 1000 and idd is null;
commit;
update test set idd=to_char(id) where id between 1000 and 2000 and idd is null;
commit;
update test set idd=to_char(id) where id between 2000 and 3000 and idd is null;
commit;
