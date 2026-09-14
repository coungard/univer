-- pairs.day_of_week был ограничен буднями (CHECK ... IN ('MONDAY'..'FRIDAY')), а на уровне
-- сервиса (PairServiceImpl.resolveSchedule) день недели с субботы по воскресенье отклонялся с
-- ValidationException. Реальные вузы (см. explore/*.md, например ЮФУ -- "МУАМ по дополнительному
-- расписанию" по субботам) проводят по субботам, а иногда и воскресеньям, дополнительные/
-- подготовительные занятия -- при проектировании схемы это не было учтено. Снимаем ограничение
-- на уровне БД; соответствующая проверка в PairServiceImpl убрана отдельно.
ALTER TABLE pairs DROP CONSTRAINT pairs_day_of_week_check;
ALTER TABLE pairs ADD CONSTRAINT pairs_day_of_week_check
    CHECK (day_of_week IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'));
