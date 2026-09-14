-- universities.address_id был NOT NULL, что не позволяло сохранить вуз, для которого пока
-- известно только название (например, филиалы из vuzopedia.ru, см. issue #73 и
-- scripts/README.md — город/адрес у большинства из них скрыт на сайте-источнике за кнопкой
-- "посмотреть" и недоступен статически). На уровне JPA (University.address,
-- UniversityDto.address) поле и так не обязательно — ограничение было только в схеме БД.
-- Снимаем его, чтобы не приходилось придумывать адрес или пропускать такие вузы целиком.
ALTER TABLE universities ALTER COLUMN address_id DROP NOT NULL;
