-- Тестовый студент для ручного прогона сценариев (SCENARIOS.md, Swagger UI, curl) — общий dev-фикстур
-- команды, задокументирован в README.md: логин student1, пароль student123, роль ROLE_STUDENT в
-- Keycloak (univer-realm). students.id ниже равен реальному Keycloak user id этого аккаунта, как и
-- проставляет StudentServiceImpl при настоящей регистрации (см. CLAUDE.md) — привязан к группе
-- «У533 РПиС» 1 курса (V11__insert_ktie_full_week_schedule.sql, dgtu/2026) ДГТУ.
INSERT INTO public.persons (id, username, email, firstname, lastname, fullname, phone) VALUES
    ('de532b59-8577-4279-92d9-7dbdeeb2fb72', 'student1', 'student1@dstu.ru', 'Тест', 'Тестов', 'Тестов Тест Тестович', NULL);

INSERT INTO public.students (id, person_id, enrollment_date, university_id, group_id, created_at, updated_at) VALUES
    ('e31d7355-7257-4795-8aa7-9232e014f932', 'de532b59-8577-4279-92d9-7dbdeeb2fb72', '2026-09-01', '796c5ad4-52ba-482f-a3bb-31c5de38762d', '11c4f49a-af05-4ce5-bd17-118909fee773', now(), now());
