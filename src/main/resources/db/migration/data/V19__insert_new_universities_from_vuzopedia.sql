-- Вузы и филиалы, добавленные в explore/*.md с vuzopedia.ru (issue #73, продолжение) --
-- см. scripts/generate_new_universities_from_vuzopedia.py и scripts/README.md.
-- У большинства нет ни сайта (email/website = NULL), ни точного города: там, где город
-- определился по названию -- есть address (город/регион, без точной улицы); где нет --
-- address_id = NULL (см. V18__make_university_address_id_nullable.sql). Ректор/год
-- основания/число студентов -- NULL везде, кроме единичных случаев, где были в тексте
-- названия (в подавляющем большинстве эти поля скрыты источником, не выдумываем).

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('bb2846ee-0cbc-4609-888e-0576e185c3c0', 'г. Нижний Новгород, Нижегородская область', 'Россия', 'Нижегородская область', 'Нижний Новгород', 'г. Нижний Новгород');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('cd5fd01d-048c-473e-a47f-64ec44ddaec1', 'Филиал Московского института права в г. Нижний Новгород', 'Филиал Московского института права в г. Нижний Новгород', 'bb2846ee-0cbc-4609-888e-0576e185c3c0', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2c732647-089b-47dc-a4bf-2e234c19d314', 'Орский филиал Московского института права', 'Орский филиал Московского института права', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e5917eae-e18d-43ff-a0d3-ad5bf8322adf', 'Воронежский филиал ГУМРФ им Макарова', 'Воронежский филиал ГУМРФ им Макарова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0b031346-6f4a-48fa-8e7d-46940a67da22', 'Красноярский филиал Санкт-Петербургского Гуманитарного университета профсоюзов', 'Красноярский филиал Санкт-Петербургского Гуманитарного университета профсоюзов', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9fff0c68-1a09-4892-a40a-6a1b27f5b2e8', 'Котласский филиал ГУМРФ им. Макарова', 'Котласский филиал ГУМРФ им. Макарова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('99ba2f70-f87d-458c-a6b5-1a5078031e7c', 'Мурманский филиал ГУМРФ им. Макарова', 'Мурманский филиал ГУМРФ им. Макарова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('627bc8ec-c747-4cee-9523-7ebdf1053493', 'Новгородский филиал Санкт-Петербургского института управления и права', 'Новгородский филиал Санкт-Петербургского института управления и права', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f504e6fb-3e07-4595-ab37-59baf8da5fcc', 'Нижнекамский филиал Казанского инновационного университета имени В.Г. Тимирясова (ИЭУП)', 'Нижнекамский филиал Казанского инновационного университета имени В.Г. Тимирясова (ИЭУП)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('31d8ea9c-fc36-4e44-afb1-24478280a8db', 'ИЭУП. Новочебоксарский филиал', 'ИЭУП. Новочебоксарский филиал', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5c1d9a4e-dec7-448c-a157-7291a6e550ac', 'ИЭУП. Чистопольский филиал', 'ИЭУП. Чистопольский филиал', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('78c365b6-69e9-4cf0-8b9c-ebbf179f5fd6', 'Нефтеюганский филиал Омского государственного технического университета', 'Нефтеюганский филиал Омского государственного технического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c5e304d8-eedd-416f-8963-5ce871397f38', 'Нижневартовский филиал Омского государственного технического университета', 'Нижневартовский филиал Омского государственного технического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6e0fa9c8-230a-49f1-acfe-6b931aca2e7a', 'Сургутский филиал Омского государственного технического университета', 'Сургутский филиал Омского государственного технического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6c5cbf63-ef62-4d39-9d20-058934654282', 'Тайгинский институт железнодорожного транспорта Омского государственного университета путей сообщения', 'Тайгинский институт железнодорожного транспорта Омского государственного университета путей сообщения', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d789ef71-7cf8-481c-8167-09680720c2d9', 'Тарский филиал Омского государственного педагогического университета', 'Тарский филиал Омского государственного педагогического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('33a93223-7351-4a5d-bcdf-138ad09f001d', 'Белебеевский филиал Самарского государственного архитектурно-строительного университета', 'Белебеевский филиал Самарского государственного архитектурно-строительного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ca53cca4-dec0-4d23-a7f6-13d64bb3045b', 'Открытый институт (Филиал) Самарского государственного архитектурно-строительного университета в г. Похвистнево', 'Открытый институт (Филиал) Самарского государственного архитектурно-строительного университета в г. Похвистнево', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f0e22085-bfca-48d6-875a-4ec27ec4bdaf', 'Казанский филиал Поволжского государственного университета телекоммуникаций и информатики', 'Казанский филиал Поволжского государственного университета телекоммуникаций и информатики', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d5b30ede-0d08-40a8-938b-1c83fe6d79fe', 'Оренбургский институт путей сообщения филиал Приволжского государственного университета путей сообщения', 'Оренбургский институт путей сообщения филиал Приволжского государственного университета путей сообщения', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6db7efb8-e553-4470-98a7-931d13b1f40a', 'Уфимский институт путей сообщения Самарского государственного университета путей сообщения', 'Уфимский институт путей сообщения Самарского государственного университета путей сообщения', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0dc5d319-3495-4295-a3ab-086442c0ca2e', 'Филиал в г. Орске Самарского государственного университета путей сообщения', 'Филиал в г. Орске Самарского государственного университета путей сообщения', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9242d10f-8c55-48fd-8bfb-4c0ba298ebda', 'Филиал в г. Рузаевке Самарского государственного университета путей сообщения', 'Филиал в г. Рузаевке Самарского государственного университета путей сообщения', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0842aebf-99d1-4fe9-b0af-71595b90dfb2', 'Волгодонский институт экономики, управления и права (филиал) Южного федерального университета', 'Волгодонский институт экономики, управления и права (филиал) Южного федерального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d547d572-5081-4add-bd8e-8c43174b0d8f', 'Махачкалинский филиал Южного федерального университета', 'Махачкалинский филиал Южного федерального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f689e068-ba06-4a5a-a8db-78c11cf6eb67', 'Филиал в г. Геленджике Южного федерального университета', 'Филиал в г. Геленджике Южного федерального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6ad465e7-5dca-4e05-b78d-7cd16c1f16de', 'Филиал в г. Железноводске Южного федерального университета', 'Филиал в г. Железноводске Южного федерального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0ba326e1-a8a2-4d08-86d1-5fb25b0dee5f', 'Филиал в г. Кизляре Южного федерального университета', 'Филиал в г. Кизляре Южного федерального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e16a0726-a9f8-4ece-afda-479230ffa569', 'Филиал в г. Новошахтинске Южного федерального университета', 'Филиал в г. Новошахтинске Южного федерального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('46db88d3-42bb-481f-bec8-7ef62806ce53', 'Филиал в ст. Вешенской Южного федерального университета', 'Филиал в ст. Вешенской Южного федерального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('90cc9276-55ca-43ce-81f9-a80888e5705e', 'Филиал в с. Учкекен Южного федерального университета', 'Филиал в с. Учкекен Южного федерального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('91db1717-210b-40ad-aded-cbf5f5afef13', 'Северо-Кавказский филиал МТУСИ', 'Северо-Кавказский филиал МТУСИ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d910f855-08ce-4bb1-bb84-0ca1e27f9de4', 'Волго-Вятский филиал МТУСИ', 'Волго-Вятский филиал МТУСИ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c3e0ad1a-053f-460c-9383-85340153fc7a', 'Биробиджанский филиал Хабаровского государственного университета экономики и права', 'Биробиджанский филиал Хабаровского государственного университета экономики и права', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('30ced7e8-1d3d-470a-8371-f8691a26b5e1', 'Южно-Сахалинский филиал Хабаровского государственного университета экономики и права', 'Южно-Сахалинский филиал Хабаровского государственного университета экономики и права', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('263bfa6b-4343-4bba-b18a-3485d6136a84', 'Благовещенский филиал Хабаровского государственного университета экономики и права', 'Благовещенский филиал Хабаровского государственного университета экономики и права', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('6674cfbe-9b55-410a-bad2-10fcd073da05', 'г. Киров, Кировская область', 'Россия', 'Кировская область', 'Киров', 'г. Киров');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5ac4a71d-ef57-48f0-95ce-78929850dc93', 'Филиал в г. Кирово-Чепецк Вятского государственного университета', 'Филиал в г. Кирово-Чепецк Вятского государственного университета', '6674cfbe-9b55-410a-bad2-10fcd073da05', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b72a6e87-84fe-4dc7-9603-9ebd63f11498', 'Филиал в г. Вятские Поляны Вятского государственного гуманитарного университета', 'Филиал в г. Вятские Поляны Вятского государственного гуманитарного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('a58609f4-99be-4ff7-9072-13ee1330f5d5', 'г. Ижевск, Удмуртия', 'Россия', 'Удмуртия', 'Ижевск', 'г. Ижевск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5708e9f3-37c6-4825-a38a-0e3c20487ff0', 'Филиал в г. Ижевске Вятского государственного гуманитарного университета', 'Филиал в г. Ижевске Вятского государственного гуманитарного университета', 'a58609f4-99be-4ff7-9072-13ee1330f5d5', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('5b49aa82-5d0c-43ab-a1fb-eb0a759fe5ce', 'г. Киров, Кировская область', 'Россия', 'Кировская область', 'Киров', 'г. Киров');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('87d61c18-4f21-4a29-a74a-20aa1c64346a', 'Филиал в г. Кирово-Чепецке Вятского государственного гуманитарного университета', 'Филиал в г. Кирово-Чепецке Вятского государственного гуманитарного университета', '5b49aa82-5d0c-43ab-a1fb-eb0a759fe5ce', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('932e3bf2-3ebd-4dcc-901a-8828f45c8717', 'Московский областной казачий институт технологий и управления (филиал МГУТУ)', 'Московский областной казачий институт технологий и управления (филиал МГУТУ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a1914e8d-9db6-4e82-9752-d4f65e140bc6', 'Смоленский казачий институт промышленных технологий и бизнеса (филиал)  МГУТУ им. К.Г. Разумовского (ПКУ)', 'Смоленский казачий институт промышленных технологий и бизнеса (филиал)  МГУТУ им. К.Г. Разумовского (ПКУ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3b1e2892-fcd6-44b1-8bd4-6266eb9d548d', 'Липецкий казачий институт технологий и управления (филиал) МГУТУ им. К.Г. Разумовского (ПКУ)', 'Липецкий казачий институт технологий и управления (филиал) МГУТУ им. К.Г. Разумовского (ПКУ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a5f9fd35-0af7-4e7a-9f06-f9e1aeddc4ce', 'Башкирский институт технологий и управления (филиал МГУТУ)', 'Башкирский институт технологий и управления (филиал МГУТУ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e18d38a3-446b-464d-bd68-885a16f50602', 'Екатеринбургский филиал Ленинградского государственного университета имени А. С. Пушкина', 'Екатеринбургский филиал Ленинградского государственного университета имени А. С. Пушкина', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7ea8dcaa-6335-4c41-9da3-23a42d0d93a4', 'Заполярный филиал Ленинградского государственного университета имени А. С. Пушкина', 'Заполярный филиал Ленинградского государственного университета имени А. С. Пушкина', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1ee80887-9eb9-4ce3-bd33-b13b18bd03b0', 'Московский филиал Ленинградского государственного университета имени А.С. Пушкина', 'Московский филиал Ленинградского государственного университета имени А.С. Пушкина', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7307c0e7-2750-45ed-9a24-6e5ad4b9af0c', 'Московский областной филиал «Институт искусств и информационных технологий» Санкт-Петербургского Гуманитарного университета профсоюзов', 'Московский областной филиал «Институт искусств и информационных технологий» Санкт-Петербургского Гуманитарного университета профсоюзов', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('894836d6-1b37-4c53-85fb-521ef64ea40d', 'Якутский филиал Санкт-Петербургского Гуманитарного Университета Профсоюзов', 'Якутский филиал Санкт-Петербургского Гуманитарного Университета Профсоюзов', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9cbdcb1b-25d7-4e05-b0dd-527ed025365f', 'Оренбургский филиал Московского технологического института', 'Оренбургский филиал Московского технологического института', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('364a6fea-2f1c-4a1d-b219-14d1041ae3f6', 'ВКА им. Можайского. Череповецкий военный инженерный институт радиоэлектроники', 'ВКА им. Можайского. Череповецкий военный инженерный институт радиоэлектроники', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ab460f34-15c3-4be2-a5da-9f56a612c893', 'Санкт-Петербургский филиал РТА им. В.Б. Бобкова', 'Санкт-Петербургский филиал РТА им. В.Б. Бобкова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('795e54c6-5e12-4bef-8075-e85d8bb6f711', 'Старооскольский филиал МГРИ-РГГРУ', 'Старооскольский филиал МГРИ-РГГРУ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('24d7d051-7230-4726-8dce-bde9cb2fd735', 'Новосибирский технологический институт (филиал РГУ им. Косыгина)', 'Новосибирский технологический институт (филиал РГУ им. Косыгина)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('db04590a-1bbe-474a-bcde-1d5dc769aef5', 'Кирово-Чепецкий филиал Вятского социально-экономического института', 'Кирово-Чепецкий филиал Вятского социально-экономического института', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9ff73ecf-3aca-47fb-848b-af9a5d5fe8d5', 'Слободской филиал Вятского социально-экономического института', 'Слободской филиал Вятского социально-экономического института', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('571847b7-5229-4438-896a-cb8c319e1472', 'Восточно-Сибирский филиал Российский государственный университет правосудия им. В.М. Лебедева', 'Восточно-Сибирский филиал Российский государственный университет правосудия им. В.М. Лебедева', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4c3dd25c-1a89-43b6-9244-87fd9139968a', 'Дальневосточный филиал РГУП', 'Дальневосточный филиал РГУП', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('53c98598-c872-451b-9996-5e8c6e8d3521', 'Западно-Сибирский филиал РГУП', 'Западно-Сибирский филиал РГУП', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9044f08f-c5ed-4655-8789-e49c55e70560', 'Казанский филиал Российского государственного университета правосудия', 'Казанский филиал Российского государственного университета правосудия', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7b004d3c-b759-4efe-bab5-bcc25719067b', 'Приволжский филиал РГУП', 'Приволжский филиал РГУП', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3fba568f-d97d-4de8-8812-2279af170c2a', 'Ростовский филиал Российского государственного университета правосудия', 'Ростовский филиал Российского государственного университета правосудия', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a1d57607-3f54-45c3-b4e3-ac08d3141b24', 'Северо-Западный филиал Российского государственного университета правосудия им. В.М. Лебедева', 'Северо-Западный филиал Российского государственного университета правосудия им. В.М. Лебедева', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('cc2b0b91-0251-41fd-b9d4-02750d9eee52', 'Северо-Кавказский филиал РГУП', 'Северо-Кавказский филиал РГУП', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d0afed7d-d91d-41fd-91ca-f042066e66ae', 'Уральский филиал РГУП', 'Уральский филиал РГУП', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('92aa820b-4646-4fad-9066-912abd76e4ab', 'Центральный филиал РГУП им. В.М. Лебедева', 'Центральный филиал РГУП им. В.М. Лебедева', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5ef8919a-aecd-43f0-94ba-d9f6eca222f6', 'Крымский филиал РГУП им. В.М. Лебедева', 'Крымский филиал РГУП им. В.М. Лебедева', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3a8f5be5-9b62-4a13-a728-b72f403d2b75', 'Байкальский филиал ГИ', 'Байкальский филиал ГИ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4c6b6815-8efe-43fa-906d-fe785b2c7b75', 'Нижегородский филиал ГИ', 'Нижегородский филиал ГИ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3e1d802a-a39e-4669-a6db-774fb07e7439', 'Шахтинский филиал ГИ', 'Шахтинский филиал ГИ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a401d84c-a8fa-4d78-b93e-ffa37828d064', 'Астраханский филиал УРИО', 'Астраханский филиал УРИО', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b08a99e7-47e3-4c70-a3f9-48a321edac1c', 'Воркутинский филиал УРИО', 'Воркутинский филиал УРИО', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e9095044-231e-400e-9c23-d161d49de05a', 'Красноярский филиал УРИО', 'Красноярский филиал УРИО', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('17ef100b-265b-4e16-be50-438a5770f92c', 'Новомосковский филиал УРИО', 'Новомосковский филиал УРИО', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e4a2790d-c0f1-4fa6-9cff-7978c2dda5ee', 'Нижегородский филиал УРИО', 'Нижегородский филиал УРИО', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c8bd4dcc-a0f1-4d5a-a142-e5359a38a1d2', 'Самарский филиал УРИО', 'Самарский филиал УРИО', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('168eb458-5b14-43aa-878e-8c69ab5b665a', 'Тольяттинский филиал УРИО', 'Тольяттинский филиал УРИО', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b1438799-7abd-461f-a8ac-da7546db3948', 'Череповецкий филиал УРИО', 'Череповецкий филиал УРИО', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('99fefe48-1984-4eac-8d8e-1f1e727a3c01', 'Челябинский филиал УРИО', 'Челябинский филиал УРИО', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a9698106-0f63-401c-b89d-741a1cb66637', 'Липецкий филиал МГТА', 'Липецкий филиал МГТА', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7ba65978-f3a2-4772-8230-6ece64d6eb3a', 'Волгоградский филиал МСИ', 'Волгоградский филиал МСИ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('074b92c2-8809-469c-9cec-28271f078dc2', 'Вышневолоцкий филиал МСИ', 'Вышневолоцкий филиал МСИ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('78cc3027-18bd-4b84-b655-1bac9f988312', 'Пензенский филиал МНЭПУ', 'Пензенский филиал МНЭПУ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f4a19846-ca32-4249-b095-6b262b357a91', 'Орехово-Зуевский филиал МНЭПУ', 'Орехово-Зуевский филиал МНЭПУ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e6f22670-4228-49fc-8e94-7cfd89f0df36', 'Балтийская государственная академия рыбопромыслового флота Калининградского государственного технического университета', 'Балтийская государственная академия рыбопромыслового флота Калининградского государственного технического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2956d9ee-dfac-4c69-80ac-9a61357b1b8d', 'Новороссийский филиал Пятигорского государственного университета', 'Новороссийский филиал Пятигорского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('24adee3b-21e0-4ebd-aa8e-b1647b7a05cb', 'Железноводский филиал Ставропольского государственного педагогического института', 'Железноводский филиал Ставропольского государственного педагогического института', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6d72cdac-4145-48c7-9eea-0c1634c34938', 'Ессентукский филиал Ставропольского государственного педагогического института', 'Ессентукский филиал Ставропольского государственного педагогического института', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0a761f1e-11a0-4f74-a384-7decd34fec03', 'Буденновский филиал Ставропольского государственного педагогического института', 'Буденновский филиал Ставропольского государственного педагогического института', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2f6529d4-6e3b-4fc9-9716-89c836a88ac8', 'Невинномысский технологический институт (филиал) Северо-Кавказского федерального университета', 'Невинномысский технологический институт (филиал) Северо-Кавказского федерального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e552e83a-e894-452d-aab0-c5f8611814c6', 'Пятигорский институт (филиал) Северо-Кавказского федерального университета', 'Пятигорский институт (филиал) Северо-Кавказского федерального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7974d665-6ea4-4c54-b7b9-7491bc232bf2', 'Георгиевский филиал Невинномысского государственного гуманитарно-технического института', 'Георгиевский филиал Невинномысского государственного гуманитарно-технического института', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6020cffd-4375-407f-a285-03e6a0a28501', 'Пермский филиал МИГУП', 'Пермский филиал МИГУП', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7d81618a-3fa1-4d69-a273-1a4d3362fcbf', 'Псковский филиал МИГУП', 'Псковский филиал МИГУП', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6793203f-9c09-4958-97f3-fbd86721c21c', 'Рязанский филиал МИГУП', 'Рязанский филиал МИГУП', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9f526df3-a08d-4548-8fb4-7af8539d3c7c', 'Смоленский филиал МИГУП', 'Смоленский филиал МИГУП', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('90e0a4aa-49d7-4884-b222-06c89d27192a', 'Курский филиал МИГУП', 'Курский филиал МИГУП', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('dffe6483-094f-4321-aad9-7c661ba7f859', 'Тюменский филиал МИГУП', 'Тюменский филиал МИГУП', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d2b6b189-81a4-4ccd-a59a-3a283118e048', 'Чебоксарский филиал МИГУП', 'Чебоксарский филиал МИГУП', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c3440eb5-9502-47e3-a00e-ef5131c5b721', 'Черкесский филиал МИГУП', 'Черкесский филиал МИГУП', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8c8f5fe3-9f74-4a3b-b802-a3c558137692', 'Якутский филиал МИГУП', 'Якутский филиал МИГУП', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('651cf03c-b83b-46e2-be26-4786f5412201', 'г. Калуга, Калужская область', 'Россия', 'Калужская область', 'Калуга', 'г. Калуга');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('12364977-c722-4784-9043-908902949a46', 'Филиал ИМПЭ им. А.С. Грибоедова в г. Калуге', 'Филиал ИМПЭ им. А.С. Грибоедова в г. Калуге', '651cf03c-b83b-46e2-be26-4786f5412201', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('02a45de4-18d9-431c-8131-46e90e4252fa', 'г. Нижневартовск, Ханты-Мансийский автономный округ — Югра', 'Россия', 'Ханты-Мансийский автономный округ — Югра', 'Нижневартовск', 'г. Нижневартовск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1dfbf210-bb6d-48e3-bdd3-d6ed2caba521', 'Филиал ИМПЭ им. А.С. Грибоедова в г. Нижневартовске', 'Филиал ИМПЭ им. А.С. Грибоедова в г. Нижневартовске', '02a45de4-18d9-431c-8131-46e90e4252fa', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a3d55f7b-11b2-4601-8abd-bb9d01c62fdd', 'Филиал Института международного права и экономики имени А.С. Грибоедова в Вологде', 'Филиал Института международного права и экономики имени А.С. Грибоедова в Вологде', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('087fcf2a-2adc-44b9-b81e-1f02918a93e3', 'г. Липецк, Липецкая область', 'Россия', 'Липецкая область', 'Липецк', 'г. Липецк');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ad31683f-7467-4521-950c-2ce5e2702847', 'Филиал ИМПЭ им. А.С. Грибоедова в г. Липецке', 'Филиал ИМПЭ им. А.С. Грибоедова в г. Липецке', '087fcf2a-2adc-44b9-b81e-1f02918a93e3', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('50e334ea-e84c-4f4f-804f-9bc8291b8c3a', 'г. Ульяновск, Ульяновская область', 'Россия', 'Ульяновская область', 'Ульяновск', 'г. Ульяновск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('bb35a1f7-2cbc-4bd6-bbb4-ec87b72d3d4e', 'Филиал ИМПЭ им. А.С. Грибоедова в г. Ульяновске', 'Филиал ИМПЭ им. А.С. Грибоедова в г. Ульяновске', '50e334ea-e84c-4f4f-804f-9bc8291b8c3a', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('ab0bad2b-0829-42f3-9807-a601609522d9', 'г. Астрахань, Астраханская область', 'Россия', 'Астраханская область', 'Астрахань', 'г. Астрахань');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('012d2100-bc02-4891-999d-7787d83ed147', 'Филиал МИЭП в г. Астрахани', 'Филиал МИЭП в г. Астрахани', 'ab0bad2b-0829-42f3-9807-a601609522d9', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8ebda584-66a3-46c0-98c0-5da7860efefb', 'Филиал МИЭП в г. Белорецке', 'Филиал МИЭП в г. Белорецке', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('6d2de7bd-7e42-434a-a814-37c491029a6f', 'г. Брянск, Брянская область', 'Россия', 'Брянская область', 'Брянск', 'г. Брянск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8034ca8c-b424-4a0d-8717-65ec63627f27', 'Филиал МИЭП в г. Брянске', 'Филиал МИЭП в г. Брянске', '6d2de7bd-7e42-434a-a814-37c491029a6f', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('85379b2e-3329-4418-b716-195533c067cc', 'г. Владивосток, Приморский край', 'Россия', 'Приморский край', 'Владивосток', 'г. Владивосток');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('72be579f-2e1e-4bc3-aeb9-39017fd962e6', 'Филиал МИЭП в г. Владивостоке', 'Филиал МИЭП в г. Владивостоке', '85379b2e-3329-4418-b716-195533c067cc', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('a40370bf-e7b2-499e-b513-844abffa72a5', 'г. Волгоград, Волгоградская область', 'Россия', 'Волгоградская область', 'Волгоград', 'г. Волгоград');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('93f6e2cc-e330-4b63-abcb-ac4f63fa6f80', 'Филиал МИЭП в г. Волгограде', 'Филиал МИЭП в г. Волгограде', 'a40370bf-e7b2-499e-b513-844abffa72a5', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('4067cdb3-4ea9-4b07-8f62-6562f0cff786', 'г. Воронеж, Воронежская область', 'Россия', 'Воронежская область', 'Воронеж', 'г. Воронеж');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('402968d4-f4ef-4bc0-96cc-31129c92307b', 'Филиал МИЭП в г. Воронеже', 'Филиал МИЭП в г. Воронеже', '4067cdb3-4ea9-4b07-8f62-6562f0cff786', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('08e1f8ba-25de-4d5d-9060-1718b5e38366', 'г. Екатеринбург, Свердловская область', 'Россия', 'Свердловская область', 'Екатеринбург', 'г. Екатеринбург');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('30125a0d-3250-49c6-a29b-4f7d6d8be0cf', 'Филиал МИЭП в г. Екатеринбурге', 'Филиал МИЭП в г. Екатеринбурге', '08e1f8ba-25de-4d5d-9060-1718b5e38366', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('52e207df-79d9-4b9e-98d2-40f67bb1d7b5', 'г. Казань, Татарстан', 'Россия', 'Татарстан', 'Казань', 'г. Казань');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('70a6aa10-ec71-4421-a318-5bf6367ed8b4', 'Филиал МИЭП в г. Казани', 'Филиал МИЭП в г. Казани', '52e207df-79d9-4b9e-98d2-40f67bb1d7b5', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('8bceaac0-00b3-45fc-b596-18f28c5a3b1c', 'г. Калининград, Калининградская область', 'Россия', 'Калининградская область', 'Калининград', 'г. Калининград');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('81a691af-2c9b-47ac-a891-2ca2fb08692a', 'Филиал МИЭП в г. Калининграде', 'Филиал МИЭП в г. Калининграде', '8bceaac0-00b3-45fc-b596-18f28c5a3b1c', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('350d7611-83ce-417e-877c-decd076a632d', 'Филиал МИЭП в г. Киселёвске', 'Филиал МИЭП в г. Киселёвске', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2f7edc92-9418-41ed-9714-581ae241b10c', 'Филиал МИЭП в г. Краснокамске', 'Филиал МИЭП в г. Краснокамске', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('0f26e9a3-ebbf-4443-9e04-2edaf3b6d630', 'г. Курск, Курская область', 'Россия', 'Курская область', 'Курск', 'г. Курск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5341ed60-ec42-403e-bfb5-a70668fc53f9', 'Филиал МИЭП в г. Курске', 'Филиал МИЭП в г. Курске', '0f26e9a3-ebbf-4443-9e04-2edaf3b6d630', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2b658eff-ecfb-4b89-add7-e840233e957e', 'Филиал МИЭП в г. Магнитогорске', 'Филиал МИЭП в г. Магнитогорске', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('9094fa45-5182-4c4a-9738-736926f0d985', 'г. Мурманск, Мурманская область', 'Россия', 'Мурманская область', 'Мурманск', 'г. Мурманск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('77141e99-4be0-4763-887f-932e829500db', 'Филиал МИЭП в г. Мурманске', 'Филиал МИЭП в г. Мурманске', '9094fa45-5182-4c4a-9738-736926f0d985', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('0b1e0d4d-c661-42c4-8c6c-00098f6b6d05', 'г. Нижневартовск, Ханты-Мансийский автономный округ — Югра', 'Россия', 'Ханты-Мансийский автономный округ — Югра', 'Нижневартовск', 'г. Нижневартовск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('49acadb0-53cc-4a00-af52-14c494168a62', 'Филиал МИЭП в г. Нижнем Новгороде', 'Филиал МИЭП в г. Нижнем Новгороде', '0b1e0d4d-c661-42c4-8c6c-00098f6b6d05', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('2f4afca8-017d-46b1-9c7a-8b3ad02b9792', 'г. Набережные Челны, Татарстан', 'Россия', 'Татарстан', 'Набережные Челны', 'г. Набережные Челны');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('522c6264-25ed-4993-9f9e-95c72cc0a021', 'Филиал МИЭП в г. Набережные Челны', 'Филиал МИЭП в г. Набережные Челны', '2f4afca8-017d-46b1-9c7a-8b3ad02b9792', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e0518580-52a2-4d4c-b089-c2ba42cf7ec2', 'Сибирский Филиал МИЭП', 'Сибирский Филиал МИЭП', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2c007395-1d7b-416c-9e5f-fe49b3a2f277', 'Арзамасский филиал Нижегородского государственного университета им. Н.И. Лобачевского', 'Арзамасский филиал Нижегородского государственного университета им. Н.И. Лобачевского', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('85477e25-21a3-4bef-afbe-72fba0839616', 'Филиал МИЭП в г. Новотроицке', 'Филиал МИЭП в г. Новотроицке', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('acf8a277-c34b-4cd4-add8-b4e1f06b9bcd', 'Филиал МИЭП в г. Омске', 'Филиал МИЭП в г. Омске', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8f640b77-a850-4ee3-94aa-6f22bfed328b', 'Филиал МИЭП в г. Пензе', 'Филиал МИЭП в г. Пензе', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7d40e5ac-e352-49be-9adb-47216b60c863', 'Филиал МИЭП в г. Перми', 'Филиал МИЭП в г. Перми', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('4a1299f5-92e6-49bf-85ee-372be2fe34f4', 'г. Петрозаводск, Карелия', 'Россия', 'Карелия', 'Петрозаводск', 'г. Петрозаводск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6f5c1536-c4eb-4aec-8dd1-97c94072a2e4', 'Филиал МИЭП в г. Петрозаводске', 'Филиал МИЭП в г. Петрозаводске', '4a1299f5-92e6-49bf-85ee-372be2fe34f4', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f8608248-b5bb-4e19-a533-1d061ff9511f', 'Филиал МИЭП в г. Радужном', 'Филиал МИЭП в г. Радужном', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('47b9abfe-a484-4bc1-afee-61117fe45e8b', 'г. Рязань, Рязанская область', 'Россия', 'Рязанская область', 'Рязань', 'г. Рязань');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b05153ef-0dd4-4652-b124-2af118d528eb', 'Филиал МИЭП в г. Рязани', 'Филиал МИЭП в г. Рязани', '47b9abfe-a484-4bc1-afee-61117fe45e8b', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('9cf31510-ba60-4061-aee4-bdc19e9ce09c', 'г. Санкт-Петербург, Санкт-Петербург', 'Россия', 'Санкт-Петербург', 'Санкт-Петербург', 'г. Санкт-Петербург');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('633861fb-c26e-43a5-ad1e-e6fa8a693d1b', 'Филиал МИЭП в г. Санкт-Петербурге', 'Филиал МИЭП в г. Санкт-Петербурге', '9cf31510-ba60-4061-aee4-bdc19e9ce09c', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('94766c37-2b40-44a1-8647-b6942166e657', 'г. Смоленск, Смоленская область', 'Россия', 'Смоленская область', 'Смоленск', 'г. Смоленск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0231c197-8754-4e5c-a575-c6aed45faf64', 'Филиал МИЭП в г. Смоленске', 'Филиал МИЭП в г. Смоленске', '94766c37-2b40-44a1-8647-b6942166e657', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9890dd4f-30cc-4636-8f0e-1af9bde37939', 'Филиал МИЭП в г. Тамбове', 'Филиал МИЭП в г. Тамбове', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('198b1586-0950-4409-9826-3de8e111f34d', 'Филиал МИЭП в г. Твери', 'Филиал МИЭП в г. Твери', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('22b53c2d-bd6c-4a97-a98c-35cd48ff784a', 'Филиал МИЭП в г. Уфе', 'Филиал МИЭП в г. Уфе', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b694532a-be4b-4049-852d-eb950e83b347', 'Филиал МИЭП в г. Челябинске', 'Филиал МИЭП в г. Челябинске', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('44a4ed5b-cad2-44ba-8aaa-420814ff1585', 'Филиал МИЭП в г. Щекинo', 'Филиал МИЭП в г. Щекинo', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('4efafb44-43a8-4753-b5b6-b1bf42f9d3e0', 'г. Ярославль, Ярославская область', 'Россия', 'Ярославская область', 'Ярославль', 'г. Ярославль');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('326b42d9-1d0c-415a-9e99-a681d85e293a', 'Филиал МИЭП в г. Ярославле', 'Филиал МИЭП в г. Ярославле', '4efafb44-43a8-4753-b5b6-b1bf42f9d3e0', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7efd856f-bcc6-48a4-8314-bce2bcf5c4fd', 'Сочинский филиал Всероссийского государственного университета юстиции (РПА Минюста России)', 'Сочинский филиал Всероссийского государственного университета юстиции (РПА Минюста России)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('afb9913f-30f2-4002-a293-5dfc21daad22', 'Алтайский институт экономики (филиал СПбУТУиЭ)', 'Алтайский институт экономики (филиал СПбУТУиЭ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('08f73de2-6ecc-4250-a255-eb44f37e5842', 'Калининградский институт экономики (филиал СПбУТУиЭ)', 'Калининградский институт экономики (филиал СПбУТУиЭ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a293b2e2-fc6e-4364-b3f6-b27c3dcef412', 'Красноярский институт экономики (филиал СПбУТУиЭ)', 'Красноярский институт экономики (филиал СПбУТУиЭ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7a6f6c1e-6eb9-4e53-99ed-a57d67c72735', 'Рязанский институт экономики филиал Санкт-Петербургского университет технологий управления и экономики', 'Рязанский институт экономики филиал Санкт-Петербургского университет технологий управления и экономики', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('cbf90f5e-4e2d-41ef-946d-09dea1c08113', 'Институт экономики, менеджмента и информационных технологий', 'Институт экономики, менеджмента и информационных технологий', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e4753ba8-19fd-4afc-84cd-72a45a52ebf6', 'Смоленский институт экономики (филиал САУ)', 'Смоленский институт экономики (филиал САУ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('492f976e-813f-4617-abb8-758fe7be9b1c', 'Магаданский институт экономики (филиал САУ)', 'Магаданский институт экономики (филиал САУ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4fc09629-2344-452f-b94b-7ddf13324735', 'Мурманский институт экономики (филиал САУ)', 'Мурманский институт экономики (филиал САУ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1944966f-5b92-4b9f-b029-ca2d730fdbf4', 'Киришский филиал САУ', 'Киришский филиал САУ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4af84d88-0c23-4e19-95e5-43cba2a6cecd', 'Новосибирский филиал САУ', 'Новосибирский филиал САУ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('92bdd75e-6d5f-48c6-9aa5-794b6cefee93', 'Якутский институт экономики (филиал САУ)', 'Якутский институт экономики (филиал САУ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('784a91af-74a7-44be-9dce-8bb9143b8336', 'Калининградский филиал СПбГАУ', 'Калининградский филиал СПбГАУ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('84e12250-aa4c-43d1-b096-8fee74c84090', 'Филиал СПбГЭУ в г. Анадырь', 'Филиал СПбГЭУ в г. Анадырь', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('74fc4e2b-76b6-4139-b8e7-ecb14880ade9', 'г. Великие Луки, Псковская область', 'Россия', 'Псковская область', 'Великие Луки', 'г. Великие Луки');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1a7bd48f-f8aa-4c1c-ae7d-91ad61b3a957', 'Филиал СПбГЭУ в г. Великий Новгород', 'Филиал СПбГЭУ в г. Великий Новгород', '74fc4e2b-76b6-4139-b8e7-ecb14880ade9', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c790f717-8f15-4b6c-84ae-0474778978b5', 'Филиал СПбГЭУ в г. Выборг', 'Филиал СПбГЭУ в г. Выборг', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4961d14b-b55d-4138-9624-a912aa6160dd', 'Филиал СПбГЭУ в г. Кизляр', 'Филиал СПбГЭУ в г. Кизляр', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('f8a598db-3719-4a1d-8013-b982439c3232', 'г. Калуга, Калужская область', 'Россия', 'Калужская область', 'Калуга', 'г. Калуга');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('dbb56553-6780-4cf9-a12d-86199a8df857', 'Филиал СПбГЭУ в г. Калуга', 'Филиал СПбГЭУ в г. Калуга', 'f8a598db-3719-4a1d-8013-b982439c3232', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('9ef43862-cff3-4833-ba61-bc1972261a4e', 'г. Псков, Псковская область', 'Россия', 'Псковская область', 'Псков', 'г. Псков');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('cc1d8af2-63da-4576-97b2-79165801b095', 'Филиал СПбГЭУ в г. Псков', 'Филиал СПбГЭУ в г. Псков', '9ef43862-cff3-4833-ba61-bc1972261a4e', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b94a41de-5cbf-468d-b3b6-615e3be06605', 'Филиал СПбГЭУ в г. Череповец', 'Филиал СПбГЭУ в г. Череповец', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('126e79bc-13ca-4678-9cf6-09f3c4f30463', 'Великолукский филиал Петербургского государственного университета путей сообщения', 'Великолукский филиал Петербургского государственного университета путей сообщения', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('09ff06c1-8ead-41c5-b003-46d76257128f', 'Дзержинский филиал Нижегородского государственного университета им. Н.И. Лобачевского', 'Дзержинский филиал Нижегородского государственного университета им. Н.И. Лобачевского', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0f82ed93-8b19-4209-99ea-82fb63efb36b', 'Павловский филиал Нижегородского государственного университета им. Н.И. Лобачевского', 'Павловский филиал Нижегородского государственного университета им. Н.И. Лобачевского', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('457dcd92-ea2c-4682-b2b9-9dd8d925e9c0', 'Арзамасский политехнический институт Нижегородского Государственного Политехнического Университета им. Р.Е. Алексеева', 'Арзамасский политехнический институт Нижегородского Государственного Политехнического Университета им. Р.Е. Алексеева', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e60bfd24-00b2-4cfe-ae49-69f30d1b3d5b', 'Выксунский филиал Нижегородского Государственного Политехнического Университета им. Р.Е. Алексеева', 'Выксунский филиал Нижегородского Государственного Политехнического Университета им. Р.Е. Алексеева', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4398b46e-2e1f-4da4-91b4-c48c19ed431e', 'Дзержинский политехнический институт (филиал) Нижегородского Государственного Политехнического Университета им. Р.Е. Алексеева', 'Дзержинский политехнический институт (филиал) Нижегородского Государственного Политехнического Университета им. Р.Е. Алексеева', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('28e46234-5480-4c28-8dfc-74c15cde6112', 'Заволжский филиал Нижегородского Государственного Политехнического Университета им. Р.Е. Алексеева', 'Заволжский филиал Нижегородского Государственного Политехнического Университета им. Р.Е. Алексеева', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a50e53a3-b678-4a56-9adf-a8ee3b9d2a44', 'Павловский филиал Нижегородского Государственного Политехнического Университета им. Р.Е. Алексеева', 'Павловский филиал Нижегородского Государственного Политехнического Университета им. Р.Е. Алексеева', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('037394b2-7692-4767-bccd-a3a79e67e7a3', 'Институт пищевых технологий и дизайна Нижегородского государственного инженерно-экономического университета', 'Институт пищевых технологий и дизайна Нижегородского государственного инженерно-экономического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8e124fe5-9eb5-46d0-9ceb-db5b0e6b7a5d', 'Брюховецкий филиал Краснодарского края Московского психолого-социального университета', 'Брюховецкий филиал Краснодарского края Московского психолого-социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b5c531a6-cfd8-46d2-9988-d1ff62d98f28', 'Брянский филиал Московского психолого-социального университета', 'Брянский филиал Московского психолого-социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('430dc1fd-b743-4892-a194-f04116b3084f', 'Конаковский филиал Московского психолого-социального университета', 'Конаковский филиал Московского психолого-социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a42ceff5-69b6-47d0-9642-10acb81aaa47', 'Куровский филиал Московского психолого-социального университета', 'Куровский филиал Московского психолого-социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f4572ce5-2029-4d9e-b220-9eee564e95e0', 'Магнитогорский филиал Московского психолого-социального университета', 'Магнитогорский филиал Московского психолого-социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2d346b9e-c9df-422c-ba03-3414169b6ae4', 'Муромский филиал Московского психолого-социального университета', 'Муромский филиал Московского психолого-социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7f0259f1-5367-46a7-85e9-50169af8c601', 'Надымский филиал Московского психолого-социального университета', 'Надымский филиал Московского психолого-социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('689667ae-2fe0-4c98-a576-94cba5648647', 'Полтавский филиал Краснодарского края Московского психолого-социального университета', 'Полтавский филиал Краснодарского края Московского психолого-социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c9f42e6a-c097-4454-91b5-16326b838df7', 'Рославльский филиал Московского психолого-социального университета', 'Рославльский филиал Московского психолого-социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f4ad092e-e38d-4f5c-be96-3255f510e261', 'Стерлитамакский филиал Московского психолого-социального университета', 'Стерлитамакский филиал Московского психолого-социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1611b735-2725-4f14-9cdb-dd4aa2ef3ae8', 'Уваровский филиал Московского психолого-социального университета', 'Уваровский филиал Московского психолого-социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b30be88a-55d0-49d6-a065-d7441f4872cd', 'Черняховский филиал Московского психолого-социального университета', 'Черняховский филиал Московского психолого-социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5b00d8f7-df35-43fa-8ec7-e28f5f5cdd32', 'Электростальский филиал Московского психолого-социального университета', 'Электростальский филиал Московского психолого-социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8646232a-a70a-4aba-8742-8a729654f939', 'Ярославский филиал Московского психолого-социального университета', 'Ярославский филиал Московского психолого-социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2370acad-7644-45e8-8c3d-ee9437e3283f', 'Сызранский филиал Самарского государственного технического университета', 'Сызранский филиал Самарского государственного технического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('784a4987-bd2b-4362-900c-1f121dcd80cc', 'Филиал в г. Минеральные Воды Ростовского государственного университета путей сообщения', 'Филиал в г. Минеральные Воды Ростовского государственного университета путей сообщения', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ae249082-9f17-4439-ba92-8183a470a259', 'Филиал в г. Туапсе Ростовского государственного университета путей сообщения', 'Филиал в г. Туапсе Ростовского государственного университета путей сообщения', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('84f6d0c3-e1ca-4219-b9d7-d995cb530bda', 'Институт традиционного прикладного искусства - Московский филиал ВШНИ', 'Институт традиционного прикладного искусства - Московский филиал ВШНИ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d9acc5e2-adc3-4449-a8f9-8a83e9a2fd26', 'Рязанский филиал ВШНИ', 'Рязанский филиал ВШНИ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f9958682-bf3c-40ee-87e2-48b558d23f45', 'Мстёрский институт лаковой миниатюрной живописи имени Ф.А. Модорова филиал Высшей школы народных искусств', 'Мстёрский институт лаковой миниатюрной живописи имени Ф.А. Модорова филиал Высшей школы народных искусств', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0e1b184f-c4ab-4fc3-9da6-dbecb607c931', 'Сергиево-Посадский институт игрушки (филиал РУТХП)', 'Сергиево-Посадский институт игрушки (филиал РУТХП)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('de117272-6dbc-4e46-8daa-e6d06e5de4fb', 'Институт ядерной энергетики (филиал СПбПУ)', 'Институт ядерной энергетики (филиал СПбПУ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f6650deb-e6ad-41e8-a874-ec800f305c74', 'Выборгский филиал СПбГУГА', 'Выборгский филиал СПбГУГА', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d2164d0c-6316-40eb-8420-f026389e682d', 'Красноярский филиал СПбГУГА', 'Красноярский филиал СПбГУГА', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('049e02c6-2b7e-464b-bfa3-318abfe11a90', 'Хабаровский филиал СПбГУГА', 'Хабаровский филиал СПбГУГА', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0b8c0fc8-d1e9-4e67-9a09-7c3c5ca6263b', 'Мурманский филиал СПбУГПС', 'Мурманский филиал СПбУГПС', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('df678470-709e-40a5-877a-55608b23f9b5', 'Дальневосточная пожарно-спасательная академия (филиал СПбУГПС)', 'Дальневосточная пожарно-спасательная академия (филиал СПбУГПС)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9b5a15c3-d051-4093-8c9d-d629852e9328', 'Мурманский филиал БИЭПП', 'Мурманский филиал БИЭПП', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('dbce607a-a381-48af-9ffb-4c643a69c392', 'Челябинский филиал БИЭПП', 'Челябинский филиал БИЭПП', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('93e17605-f7bd-4d54-b93a-329741bdc8cc', 'Дагестанский государственный педагогический университет Филиал в г. Дербент', 'Дагестанский государственный педагогический университет Филиал в г. Дербент', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2c129bb8-30ab-4f2a-bf71-113c04e3a6d6', 'Дербентский филиал Дагестанского государственного университета', 'Дербентский филиал Дагестанского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7ede8187-d71a-4321-b542-45200dd3d9ca', 'Избербашский филиал Дагестанского государственного университета', 'Избербашский филиал Дагестанского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2d3c9997-5f5d-47d2-86c9-d7f0a615f307', 'Филиал в г. Каспийск Дагестанского государственного университета', 'Филиал в г. Каспийск Дагестанского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('cdcfd7af-53fb-4234-b01a-b6cd5a510437', 'Филиал в г. Кизилюрт Дагестанского государственного университета', 'Филиал в г. Кизилюрт Дагестанского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('eab3e09b-5a45-4d2a-a5f9-3dc9740d2402', 'Кизлярский филиал Дагестанского государственного университета', 'Кизлярский филиал Дагестанского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('01319bba-d622-4799-9c30-2122f88e2698', 'Хасавюртовский филиал Дагестанского государственного университета', 'Хасавюртовский филиал Дагестанского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6bcda613-9819-4a57-81c3-df9de0fd357b', 'Филиал ИВЭСЭП в г. Бокситогорск', 'Филиал ИВЭСЭП в г. Бокситогорск', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5f02c5d7-aafb-4071-ab44-b1460de39d92', 'Филиал ИВЭСЭП в г. Волхов', 'Филиал ИВЭСЭП в г. Волхов', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('9b759219-1ae8-48e8-b866-d010e8acc600', 'г. Калининград, Калининградская область', 'Россия', 'Калининградская область', 'Калининград', 'г. Калининград');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('530e0e1c-9197-47ed-987b-faa15f354b01', 'Филиал ИВЭСЭП в г. Калининграде', 'Филиал ИВЭСЭП в г. Калининграде', '9b759219-1ae8-48e8-b866-d010e8acc600', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('69d6291c-6a7e-4ffb-80a3-d014cc969f40', 'Филиал ИВЭСЭП в г. Наро-Фоминске', 'Филиал ИВЭСЭП в г. Наро-Фоминске', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e1d714be-4caf-46a9-83a6-bae9548deb27', 'Филиал ИВЭСЭП в г. Краснодаре', 'Филиал ИВЭСЭП в г. Краснодаре', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5f7bae82-ccb3-42aa-8814-a43ae1e7b57e', 'Филиал ИВЭСЭП в г. Красноярске', 'Филиал ИВЭСЭП в г. Красноярске', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c8b3e708-b161-4f6b-82c6-c9a833f14f76', 'Филиал ИВЭСЭП в г. Перми', 'Филиал ИВЭСЭП в г. Перми', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('7ac7f7b1-bbbf-4bde-96c2-1f6b7737aa68', 'г. Новосибирск, Новосибирская область', 'Россия', 'Новосибирская область', 'Новосибирск', 'г. Новосибирск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b17ecbe5-5349-487d-8091-5535d4c862b9', 'Филиал ИВЭСЭП в г. Новосибирске', 'Филиал ИВЭСЭП в г. Новосибирске', '7ac7f7b1-bbbf-4bde-96c2-1f6b7737aa68', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d8c15470-e65a-4a95-b895-3ff740828b73', 'Уральский технический институт связи и информатики (филиал СибГУТИ)', 'Уральский технический институт связи и информатики (филиал СибГУТИ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('606adc90-7e21-4f9d-808a-a3592b6d70c2', 'Хабаровский институт инфокоммуникаций (филиал СибГУТИ)', 'Хабаровский институт инфокоммуникаций (филиал СибГУТИ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('01c84b09-341e-447a-bfd2-0f662c0b6bab', 'Бурятский институт инфокоммуникаций (филиал СибГУТИ)', 'Бурятский институт инфокоммуникаций (филиал СибГУТИ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a797e3ff-765e-4d1e-bc86-ca6a8e1ece62', 'Набережночелнинский институт КФУ (филиал)', 'Набережночелнинский институт КФУ (филиал)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3c540815-248d-4daf-abc9-c13334a9f7a2', 'Елабужский институт КФУ (филиал)', 'Елабужский институт КФУ (филиал)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b7c83f2e-1513-4e61-a568-9b5a3f08a2d7', 'Бугульминский филиал КНИТУ', 'Бугульминский филиал КНИТУ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c1e21e04-4341-40bf-b2e8-40333f2fa6bd', 'Нижнекамский химико-технологический институт (филиал КНИТУ)', 'Нижнекамский химико-технологический институт (филиал КНИТУ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0222b572-97bd-44d7-a771-cceeebdae157', 'Альметьевский филиал "ТИСБИ"', 'Альметьевский филиал "ТИСБИ"', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5032879e-66b2-42a4-a4b7-44e386aa4d3e', 'Бузулукский гуманитарно-технологический институт Оренбургского государственного университета', 'Бузулукский гуманитарно-технологический институт Оренбургского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('bed37687-2049-4aa6-a2ba-dd115171736e', 'Петрозаводский филиал Петербургского государственного университета путей сообщения', 'Петрозаводский филиал Петербургского государственного университета путей сообщения', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('64322fbe-3acb-42be-8ef8-089bcbe2237d', 'Филиал Российского государственного гидрометеорологического университета в г. Туапсе', 'Филиал Российского государственного гидрометеорологического университета в г. Туапсе', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('b04b6ab3-9315-46bc-8b1a-0c2ea4c6e2f5', 'г. Калининград, Калининградская область', 'Россия', 'Калининградская область', 'Калининград', 'г. Калининград');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('68239329-c2f4-4e7d-b9fc-0705288a4590', 'Филиал ВУНЦ ВМФ «Военно-морская академия» в г. Калининграде', 'Филиал ВУНЦ ВМФ «Военно-морская академия» в г. Калининграде', 'b04b6ab3-9315-46bc-8b1a-0c2ea4c6e2f5', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1f12c289-f02b-4b0d-bf2e-7077cf7d2d62', 'Тюменский филиал Сибирского университета потребительской кооперации', 'Тюменский филиал Сибирского университета потребительской кооперации', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('634f9887-f1fd-463d-ac02-ccb2f01e091a', 'Кумертауский филиал Оренбургского государственного университета', 'Кумертауский филиал Оренбургского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('25486ff5-3346-428c-954b-4cc919ebb107', 'Орский гуманитарно-технологический институт (филиал) Оренбургского государственного университета', 'Орский гуманитарно-технологический институт (филиал) Оренбургского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d2bd6afc-e9e1-4791-86b8-6fed89bc205a', 'Красноярский институт водного транспорта (филиал) Сибирского государственного университета водного транспорта', 'Красноярский институт водного транспорта (филиал) Сибирского государственного университета водного транспорта', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('dc86a7a7-6177-446e-a215-549b6cb969cb', 'Омский институт водного транспорта (филиал) Сибирского государственного университета водного транспорта', 'Омский институт водного транспорта (филиал) Сибирского государственного университета водного транспорта', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('fd90b677-1b08-4cfd-b958-c452971c4588', 'Усть-Кутский институт водного транспорта (филиал) Сибирского государственного университета водного транспорта', 'Усть-Кутский институт водного транспорта (филиал) Сибирского государственного университета водного транспорта', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('178d6d7f-f2c1-4251-8f5a-9b1830b1b2bc', 'Якутский институт водного транспорта (филиал) Сибирского государственного университета водного транспорта', 'Якутский институт водного транспорта (филиал) Сибирского государственного университета водного транспорта', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('87a99d0e-8df4-416e-8fc8-f39877dfdbc9', 'Куйбышевский филиал Новосибирского государственного педагогического университета', 'Куйбышевский филиал Новосибирского государственного педагогического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4fbf0327-a9c3-4154-a151-fe62141e0454', 'Институт бизнеса и делового администрирования РАНХиГС', 'Институт бизнеса и делового администрирования РАНХиГС', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0013a8dd-bd9b-42ca-9fe8-7f3ec3d9ac29', 'Уфимский институт (филиал) Российского экономического университета им. Г.В. Плеханова', 'Уфимский институт (филиал) Российского экономического университета им. Г.В. Плеханова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5ba29025-355f-4e8d-8396-bf88ebda8410', 'ОЧНОГО ОТДЕЛЕНИЯ', 'ОЧНОГО ОТДЕЛЕНИЯ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('75d51827-a1fe-4d3c-b0de-ebf7b7b60df9', 'ЗАОЧНОГО ОТДЕЛЕНИЯ', 'ЗАОЧНОГО ОТДЕЛЕНИЯ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0b119991-f1ea-487e-b16d-32cdca33083e', 'Владивостокский филиал Санкт-Петербургского Гуманитарного университета профсоюзов', 'Владивостокский филиал Санкт-Петербургского Гуманитарного университета профсоюзов', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('654c718f-6cef-4e03-9fcd-83fc1be180c7', 'Экономический', 'Экономический', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('bfe5f4ed-e836-4bf3-acaa-93f3feb19e51', 'Юридический', 'Юридический', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('cf12e723-b446-4ca9-9d64-b0cf5a003e1d', 'Реклама и связи с общественностью', 'Реклама и связи с общественностью', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0d4613ca-475c-4641-a087-21cd52562680', 'Социально-культурная деятельность', 'Социально-культурная деятельность', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('fcd29978-4ba9-4ae1-86f8-d3143a6d709f', 'Юридический институт', 'Юридический институт', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d09833a4-340f-41b9-bcdc-cda5ea2e7d8e', 'Институт гуманитарных и социальных наук', 'Институт гуманитарных и социальных наук', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('905e1cb7-5c52-4276-94ed-f1229a866ecb', '1242-vuz-5193', '1242-vuz-5193', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('92777289-5109-463c-87e0-4e3c190d03a6', '1243-vuz-5194', '1243-vuz-5194', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c943b791-c2df-42f8-8fb0-09f47d80cfc3', '1244-vuz-5195', '1244-vuz-5195', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ea62e451-eee1-4e40-b020-2bb6d7cb16a7', 'Школа гуманитарных наук и искусств НИУ ВШЭ в Санкт-Петербурге', 'Школа гуманитарных наук и искусств НИУ ВШЭ в Санкт-Петербурге', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('40968679-21e4-46f5-bd87-586951bb0b71', 'Школа физико-математических и компьютерных наук НИУ ВШЭ в Санкт-Петербурге', 'Школа физико-математических и компьютерных наук НИУ ВШЭ в Санкт-Петербурге', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('446db09f-2b71-463c-b2ed-58be496608f3', 'Медицинский университет Реавиз в Москве', 'Медицинский университет Реавиз в Москве', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('76eb4ce4-c0b0-4d50-aee3-ee8a7ecadc0f', 'Университет РЕАВИЗ', 'Университет РЕАВИЗ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5a713c9e-e005-425f-b737-3612966ae5f1', 'Кировский институт (филиал) Московского гуманитарно-экономического университета', 'Кировский институт (филиал) Московского гуманитарно-экономического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1d0e5d7a-62b4-4167-9fad-5120ad2202f3', 'Ивангородский гуманитарно-технический институт (филиал) ГУАП', 'Ивангородский гуманитарно-технический институт (филиал) ГУАП', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6de8734e-61e3-44db-b65f-4e05ba9de332', 'Ставропольский институт кооперации (филиал) Белгородского университета кооперации, экономики и права', 'Ставропольский институт кооперации (филиал) Белгородского университета кооперации, экономики и права', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2099fac3-e37d-4a78-8f69-18980af3ba7b', 'Лениногорский филиал Казанского национального исследовательского технического университета им. А.Н. Туполева-КАИ', 'Лениногорский филиал Казанского национального исследовательского технического университета им. А.Н. Туполева-КАИ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3b9e29b0-3a80-4c7e-99f4-1c343a3a5089', 'Томский сельскохозяйственный институт - филиал Новосибирского ГАУ', 'Томский сельскохозяйственный институт - филиал Новосибирского ГАУ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1fb025b3-dc53-474a-b021-59719312d500', 'Калининградский филиал Московского финансово-юридического университета МФЮА', 'Калининградский филиал Московского финансово-юридического университета МФЮА', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8b6e46ab-6035-4f6c-af82-bc2d9daeea7e', 'Пущинский государственный естественно-научный институт', 'Пущинский государственный естественно-научный институт', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e992079e-c5cb-4031-9806-d74b8c689757', 'Филиал Российского государственного университета им. А.Н. Косыгина (Технологии. Дизайн. Искусство) в г. Твери', 'Филиал Российского государственного университета им. А.Н. Косыгина (Технологии. Дизайн. Искусство) в г. Твери', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('07e2bf98-5619-410f-94ef-ffc47245c379', 'Новозыбковский филиал Брянского Государственного Университета', 'Новозыбковский филиал Брянского Государственного Университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ebc819a1-31de-4e40-b0c3-c6b9e019ca6c', 'Филиал Мурманского арктического государственного университета в г. Апатиты', 'Филиал Мурманского арктического государственного университета в г. Апатиты', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e63fad21-02b7-43c1-acfb-5e698dd1caff', 'Лесосибирский филиал Сибирского государственного университета науки и технологий имени академика М.Ф. Решетнева', 'Лесосибирский филиал Сибирского государственного университета науки и технологий имени академика М.Ф. Решетнева', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('16f7230e-10e4-4e82-afe6-34c9d3f0b9e5', 'Рязанский институт (филиал) Московского Политехнического университета', 'Рязанский институт (филиал) Московского Политехнического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('14eb5edd-4892-438d-bf39-35eb336ebd7e', 'Байкало-Амурский институт железнодорожного транспорта (филиал ДВГУПС в г. Тында)', 'Байкало-Амурский институт железнодорожного транспорта (филиал ДВГУПС в г. Тында)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('fb36a54e-e165-4db5-b4cf-6a153cdbeb2a', 'Амурский институт железнодорожного транспорта (филиал ДВГУПС в г. Свободном)', 'Амурский институт железнодорожного транспорта (филиал ДВГУПС в г. Свободном)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7d70b069-01ab-43ec-9186-3c08d590a21e', 'Приморский институт железнодорожного транспорта (филиал ДВГУПС в г. Уссурийске)', 'Приморский институт железнодорожного транспорта (филиал ДВГУПС в г. Уссурийске)', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('4d369773-3d5f-4d6a-8379-b4ca77f959e9', 'г. Южно-Сахалинск, Сахалинская область', 'Россия', 'Сахалинская область', 'Южно-Сахалинск', 'г. Южно-Сахалинск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('70ab017c-35d7-4059-95bd-06d08a585d1f', 'Сахалинский институт железнодорожного транспорта (филиал ДВГУПС в г. Южно-Сахалинске)', 'Сахалинский институт железнодорожного транспорта (филиал ДВГУПС в г. Южно-Сахалинске)', '4d369773-3d5f-4d6a-8379-b4ca77f959e9', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1cd2e21c-37de-49fa-a4cb-6118c48232fa', 'Карачевский филиал  Орловского государственного университета имени И.С. Тургенева', 'Карачевский филиал  Орловского государственного университета имени И.С. Тургенева', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c73e0fd9-271d-4af0-838f-2862f645f722', 'Ливенский филиал Орловского государственного университета имени И.С. Тургенева', 'Ливенский филиал Орловского государственного университета имени И.С. Тургенева', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f476fb90-5b53-4de9-9674-2a4012309c2d', 'Мценский филиал Орловского государственного университета имени И.С. Тургенева', 'Мценский филиал Орловского государственного университета имени И.С. Тургенева', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f30e34ac-276b-48e3-a9ea-4073eb26c7ea', 'Северский технологический институт (филиал НИЯУ МИФИ)', 'Северский технологический институт (филиал НИЯУ МИФИ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('19ea0d47-c3c0-498d-981e-b28eff865911', 'Обнинский институт атомной энергетики (филиал НИЯУ МИФИ)', 'Обнинский институт атомной энергетики (филиал НИЯУ МИФИ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3e420d90-a747-4c13-9071-af5868add3d9', 'Дагестанский государственный технический университет Филиал в г.Каспийске', 'Дагестанский государственный технический университет Филиал в г.Каспийске', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ef943549-88fa-4bd9-bb5f-6fe2238c2035', 'Дагестанский государственный технический университет Филиал в г. Дербенте', 'Дагестанский государственный технический университет Филиал в г. Дербенте', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9d25c799-ea36-484f-87c2-1385fd63ee60', 'Дагестанский государственный технический университет Филиал в г. Кизляр', 'Дагестанский государственный технический университет Филиал в г. Кизляр', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b097b9e7-e859-43bb-9780-6253ad114627', 'Саровский физико-технический институт (филиал НИЯУ МИФИ)', 'Саровский физико-технический институт (филиал НИЯУ МИФИ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('725393ab-3f32-44c6-9d5e-0e08e687cf15', 'Балаковский инженерно-технологический институт (филиал НИЯУ МИФИ)', 'Балаковский инженерно-технологический институт (филиал НИЯУ МИФИ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('72dd4fc2-b7cb-478c-882b-0b74e8fbdb8f', 'Димитровградский инженерно-технологический институт (филиал НИЯУ МИФИ)', 'Димитровградский инженерно-технологический институт (филиал НИЯУ МИФИ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('11d74be3-b56b-4dca-bf2a-cfe6164a0fb5', 'Октёмский филиал Арктического государственного агротехнологического университета', 'Октёмский филиал Арктического государственного агротехнологического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6906d71b-8b69-445f-a71d-c83aa2caf007', 'Волгодонский инженерно-технический институт (филиал НИЯУ МИФИ)', 'Волгодонский инженерно-технический институт (филиал НИЯУ МИФИ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0cec0a69-147b-45c3-bd51-e4afb1365295', 'Технологический институт (филиал НИЯУ МИФИ)', 'Технологический институт (филиал НИЯУ МИФИ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('98de6a96-f27e-4528-884b-7f37cfc8315f', 'Технологический институт - филиал Ульяновского государственного аграрного университета имени П.А. Столыпина', 'Технологический институт - филиал Ульяновского государственного аграрного университета имени П.А. Столыпина', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f6e317c8-ca49-4307-a896-2a0aa4efd006', 'Воркутинский филиал Ухтинского государственного технического университета', 'Воркутинский филиал Ухтинского государственного технического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('709503f2-7a1c-4c2d-8c58-e99d6eebdcf7', 'Филиал Ухтинского государственного технического университета в г. Усинске', 'Филиал Ухтинского государственного технического университета в г. Усинске', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('706ab119-965c-43b7-a589-feb70e765260', 'Электростальский институт (филиал) Московского политехнического университета', 'Электростальский институт (филиал) Московского политехнического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('865eb1e8-6902-4ccb-a139-646b0d0614fa', 'Сибайский институт филиал Уфимского университета науки и технологий', 'Сибайский институт филиал Уфимского университета науки и технологий', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9c182bb9-ec00-40a5-97ed-98c68304f490', 'Филиал Сочинского государственного университета в г. Анапе Краснодарского края', 'Филиал Сочинского государственного университета в г. Анапе Краснодарского края', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4a0d310e-a3d1-4fe4-a300-41848e7d13b2', 'Керченский филиал Крымского инженерно-педагогического университета имени Февзи Якубова', 'Керченский филиал Крымского инженерно-педагогического университета имени Февзи Якубова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('229161f9-8648-412b-b3fb-68cbd134396b', 'Чебоксарский институт (филиал) Московского политехнического университета', 'Чебоксарский институт (филиал) Московского политехнического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('073be92e-d674-4a18-b165-d3d55ec3a5fc', 'г. Новокузнецк, Кемеровская область', 'Россия', 'Кемеровская область', 'Новокузнецк', 'г. Новокузнецк');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4fcf7f64-ce8e-4e62-be56-3de84153775a', 'Филиал Самарского государственного технического университета в г. Новокуйбышевске', 'Филиал Самарского государственного технического университета в г. Новокуйбышевске', '073be92e-d674-4a18-b165-d3d55ec3a5fc', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0906e454-8d42-4d2b-acad-be3436dcf97b', 'Филиал Воронежского государственного технического университета в городе Борисоглебске', 'Филиал Воронежского государственного технического университета в городе Борисоглебске', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('21fbb028-39ec-403a-ab8d-d8dc5854b144', 'Шуйский филиал Ивановского государственного университета', 'Шуйский филиал Ивановского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('076c42fd-641d-47f9-8657-43d55742544e', 'Новоуральский технологический институт (филиал НИЯУ МИФИ)', 'Новоуральский технологический институт (филиал НИЯУ МИФИ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('69b45855-b258-4fe5-8e79-e4b8bdf1f756', 'Снежинский физико-технический институт (филиал НИЯУ МИФИ)', 'Снежинский физико-технический институт (филиал НИЯУ МИФИ)', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('ea94f605-5fcc-47e5-9035-8fc273e392c7', 'г. Великие Луки, Псковская область', 'Россия', 'Псковская область', 'Великие Луки', 'г. Великие Луки');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a3bc0dfe-8aca-4e62-a1a3-c36c49c59588', 'Филиал Псковского государственного университета в г.Великие Луки Псковской области', 'Филиал Псковского государственного университета в г.Великие Луки Псковской области', 'ea94f605-5fcc-47e5-9035-8fc273e392c7', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3ae4f10c-b15f-40f7-8597-6c048628f1a7', 'Озерский технологический институт (филиал НИЯУ МИФИ)', 'Озерский технологический институт (филиал НИЯУ МИФИ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5086d7d6-7c7b-48bf-9080-2f82aa1657ff', 'Трехгорный технологический институт (филиал НИЯУ МИФИ)', 'Трехгорный технологический институт (филиал НИЯУ МИФИ)', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('699c1b87-95a8-4d0c-ba6f-3062579cde8f', 'г. Ставрополь, Ставропольский край', 'Россия', 'Ставропольский край', 'Ставрополь', 'г. Ставрополь');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0a6b5f75-1395-4657-acda-4f0b80baf5ad', 'Технологический институт сервиса (филиал ДГТУ в г. Ставрополе)', 'Технологический институт сервиса (филиал ДГТУ в г. Ставрополе)', '699c1b87-95a8-4d0c-ba6f-3062579cde8f', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8b8ba89b-97ba-4927-9022-4616151ada74', 'Институт сферы обслуживания и предпринимательства (филиал ДГТУ в г. Шахты)', 'Институт сферы обслуживания и предпринимательства (филиал ДГТУ в г. Шахты)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5250b078-a6f1-49a5-b649-58d15aaa1b3d', 'Тучковский филиал Московского политехнического университета', 'Тучковский филиал Московского политехнического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('af9f2a49-8755-4652-bcab-bb7927ebc233', 'Коломенский институт (филиал) Московского политехнического университета', 'Коломенский институт (филиал) Московского политехнического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b89baa5c-83aa-4737-a203-28c1f456acc9', 'Лужский институт (филиал) Ленинградского государственного университета имени А.С. Пушкина', 'Лужский институт (филиал) Ленинградского государственного университета имени А.С. Пушкина', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('54b65d16-0e9d-4779-908d-de10cec22965', 'г. Волгоград, Волгоградская область', 'Россия', 'Волгоградская область', 'Волгоград', 'г. Волгоград');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f686f6da-576d-4f50-9713-262c5761caae', 'Институт технологий (филиал) ДГТУ в г. Волгодонске', 'Институт технологий (филиал) ДГТУ в г. Волгодонске', '54b65d16-0e9d-4779-908d-de10cec22965', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4c82e5ad-8dec-4ee3-a0c5-ddb3aba1084b', 'Бокситогорский институт (филиал) Ленинградского государственного университета имени А.С. Пушкина', 'Бокситогорский институт (филиал) Ленинградского государственного университета имени А.С. Пушкина', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7fb62e8e-7de6-446a-bb97-7f6ce57f916c', 'Ломоносовский институт (филиал) Ленинградского государственного университета имени А.С. Пушкина', 'Ломоносовский институт (филиал) Ленинградского государственного университета имени А.С. Пушкина', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('78aaec85-c80f-43e5-83a0-4bade6438472', 'Рузаевский институт машиностроения (филиал) Мордовского государственного университета им. Н.П.Огарева', 'Рузаевский институт машиностроения (филиал) Мордовского государственного университета им. Н.П.Огарева', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('bb45442e-9109-4bf5-ae51-f068e1a4167b', 'Кубанский государственный университет филиал в г.Армавире', 'Кубанский государственный университет филиал в г.Армавире', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b6f3f27f-4cb2-457e-bbdf-12289f4ff4ab', 'Кубанский государственный университет филиал в г.Славянске-на-Кубани', 'Кубанский государственный университет филиал в г.Славянске-на-Кубани', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('648674a3-5d6f-45f0-82cc-c2562e6b5591', 'Кубанский государственный университет филиал в г.Тихорецке', 'Кубанский государственный университет филиал в г.Тихорецке', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('09fd2a0d-015f-4aa8-ae72-acbb165d806a', 'г. Новороссийск, Краснодарский край', 'Россия', 'Краснодарский край', 'Новороссийск', 'г. Новороссийск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f5d71c18-17ef-4d29-b9a5-ff71e168a290', 'Кубанский государственный университет Филиал в г. Новороссийске', 'Кубанский государственный университет Филиал в г. Новороссийске', '09fd2a0d-015f-4aa8-ae72-acbb165d806a', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b7f7acfa-9e8a-4a8f-90ea-d0ee2eb28926', 'Политехнический институт (филиал) ДГТУ в г. Таганроге', 'Политехнический институт (филиал) ДГТУ в г. Таганроге', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7d18b9b5-d06c-4394-9aff-3e66bb8066a9', 'Набережночелнинский филиал "Университет управления "ТИСБИ"', 'Набережночелнинский филиал "Университет управления "ТИСБИ"', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9231addd-e4c3-40ea-8be4-27345592ebac', 'Технологический институт (филиал) ДГТУ в г.Азове', 'Технологический институт (филиал) ДГТУ в г.Азове', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('06e8b9b0-02e2-406e-9cd4-9384ea29db16', 'Удмуртский государственный университет филиал в городе Воткинске', 'Удмуртский государственный университет филиал в городе Воткинске', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('cbf2b931-df31-4f85-b37e-e2cf270d9b34', 'Удмуртский государственный университет филиал в городе Можге', 'Удмуртский государственный университет филиал в городе Можге', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e9816404-059e-4457-9045-f5f30a3c1fef', 'Губкинский филиал Белгородского государственного технологического университета им. В.Г. Шухова', 'Губкинский филиал Белгородского государственного технологического университета им. В.Г. Шухова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('81105f71-41ee-4841-89dc-d267c0ceef4d', 'Филиал Уфимского университета науки и технологий Филиал в г. Кумертау', 'Филиал Уфимского университета науки и технологий Филиал в г. Кумертау', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b24d163d-4e8e-4a26-a5ac-a8f62c110f7a', 'Филиал Уфимского Университета Науки и Технологий в г. Ишимбае', 'Филиал Уфимского Университета Науки и Технологий в г. Ишимбае', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('19786cda-bcc5-4f6f-ada5-445714991edf', 'Московский государственный технический университет имени Н.Э. Баумана (Калужский филиал)', 'Московский государственный технический университет имени Н.Э. Баумана (Калужский филиал)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('227685c5-1056-4fed-91d2-6dda1daed5c0', 'Кузбасский государственный технический университет имени Т.Ф. Горбачева филиал в г. Междуреченске', 'Кузбасский государственный технический университет имени Т.Ф. Горбачева филиал в г. Междуреченске', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b7fb80c5-7332-4458-bd58-469227d55d1f', 'Кузбасский государственный технический университет имени Т.Ф. Горбачева филиал в г. Белово', 'Кузбасский государственный технический университет имени Т.Ф. Горбачева филиал в г. Белово', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('8444e853-225d-41bc-806c-86779f02ece5', 'г. Новокузнецк, Кемеровская область', 'Россия', 'Кемеровская область', 'Новокузнецк', 'г. Новокузнецк');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c972c330-a3f8-41b8-9c40-1f5695ec6b51', 'Кузбасский государственный технический университет имени Т.Ф. Горбачева филиал в г. Новокузнецке', 'Кузбасский государственный технический университет имени Т.Ф. Горбачева филиал в г. Новокузнецке', '8444e853-225d-41bc-806c-86779f02ece5', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('15deab6c-1ad7-4385-9875-a7ebce5de3e7', 'Кузбасский государственный технический университет имени Т.Ф. Горбачева филиал в г. Прокопьевске', 'Кузбасский государственный технический университет имени Т.Ф. Горбачева филиал в г. Прокопьевске', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('216aea54-9e78-4983-b444-52468eefb494', 'Бийский технологический институт (филиал) Алтайского государственного технического университета им. И.И. Ползунова', 'Бийский технологический институт (филиал) Алтайского государственного технического университета им. И.И. Ползунова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('bb7bf035-f0c4-4d20-9cea-fb5403075275', 'Рубцовский индустриальный институт (филиал) Алтайского государственного технического университета имени И.И.Ползунова', 'Рубцовский индустриальный институт (филиал) Алтайского государственного технического университета имени И.И.Ползунова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7b495cba-d6b0-44a8-b08b-685ec15a5386', 'Рубцовский институт (филиал) Алтайского государственного университета', 'Рубцовский институт (филиал) Алтайского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e70a7e44-c70e-4f98-a114-45e19e4ac26f', 'Таганрогский институт имени А. П. Чехова (филиал) Ростовского государственного экономического университета (РИНХ)', 'Таганрогский институт имени А. П. Чехова (филиал) Ростовского государственного экономического университета (РИНХ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7cffc8ce-ecc0-4555-b096-d209c591db16', 'Гуковский институт экономики и права (филиал) Ростовского государственного экономического университета (РИНХ)', 'Гуковский институт экономики и права (филиал) Ростовского государственного экономического университета (РИНХ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('68f4fff5-a7b0-4316-b9d9-4bde586b9ece', 'Филиал Ростовского государственного экономического университета (РИНХ) в г. Ейске', 'Филиал Ростовского государственного экономического университета (РИНХ) в г. Ейске', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4d4f8ec5-32ce-4358-897e-f44dd5d3f7e6', 'Филиал Ростовского государственного экономического университета (РИНХ) в г. Кисловодске Ставропольского края', 'Филиал Ростовского государственного экономического университета (РИНХ) в г. Кисловодске Ставропольского края', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('db8d92a7-8eb1-4b48-b197-efbdb24be181', 'Филиал Ростовский государственный экономический университет (РИНХ) в г. Миллерово', 'Филиал Ростовский государственный экономический университет (РИНХ) в г. Миллерово', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('52af6507-c00f-4d1d-b235-dca4f0afda73', 'Филиал Ростовский государственный экономический университет (РИНХ) в г. Черкесске', 'Филиал Ростовский государственный экономический университет (РИНХ) в г. Черкесске', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ff5636a3-00cf-41e6-ae93-64dbb08c7bb5', 'Тобольский индустриальный институт (филиал) Тюменского индустриального университета', 'Тобольский индустриальный институт (филиал) Тюменского индустриального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('61bf7cf4-16df-4606-abc5-c6bf482485cf', 'г. Нижневартовск, Ханты-Мансийский автономный округ — Югра', 'Россия', 'Ханты-Мансийский автономный округ — Югра', 'Нижневартовск', 'г. Нижневартовск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('81af8408-9d80-4ce7-bbfb-f461b205d26c', 'Филиал Тюменского индустриального университета в городе Нижневартовске', 'Филиал Тюменского индустриального университета в городе Нижневартовске', '61bf7cf4-16df-4606-abc5-c6bf482485cf', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4f8d5ae9-f7c2-415b-a9a4-39d5b3cab7d5', 'Сургутский институт нефти и газа (филиал) Тюменского индустриального университета', 'Сургутский институт нефти и газа (филиал) Тюменского индустриального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('416e44a7-d56a-43a7-b169-f616f5dbf4dd', 'Ноябрьский институт нефти и газа (филиал) Тюменского индустриального университета', 'Ноябрьский институт нефти и газа (филиал) Тюменского индустриального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0b7d0287-c36f-463c-ba1b-2d9bc4b22e72', 'Ставропольский филиал Российского технологического университета МИРЭА', 'Ставропольский филиал Российского технологического университета МИРЭА', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('30cff472-f408-4b4a-a2e6-b005ce0c75b2', 'Крымский кооперативный институт (филиал) Российского университета кооперации', 'Крымский кооперативный институт (филиал) Российского университета кооперации', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1bd57c13-a404-41c1-a430-7981dfc56e88', 'Филиал Национального исследовательского технологического университета «МИСИС» в г. Губкине', 'Филиал Национального исследовательского технологического университета «МИСИС» в г. Губкине', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('16292c20-bb7a-4dd5-ac52-cad96ea65d48', 'Филиал Воронежского экономико-правового института в г. Старый Оскол', 'Филиал Воронежского экономико-правового института в г. Старый Оскол', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('70de848d-da98-4709-9b88-405eb001347a', 'Филиал Воронежского экономико-правового института в г. Россошь', 'Филиал Воронежского экономико-правового института в г. Россошь', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('164eefd7-dae6-4609-a41c-e7e6b801f445', 'г. Липецк, Липецкая область', 'Россия', 'Липецкая область', 'Липецк', 'г. Липецк');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ec1d01b5-5f61-4613-ab43-4cc35f857fda', 'Филиал Воронежского экономико-правового института в г. Липецк', 'Филиал Воронежского экономико-правового института в г. Липецк', '164eefd7-dae6-4609-a41c-e7e6b801f445', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0146395b-9433-4779-99e2-9d0a15275e5d', 'Филиал Воронежского экономико-правового института в г. Орел', 'Филиал Воронежского экономико-правового института в г. Орел', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e1266d9c-811a-4cab-bd2b-aca3cf70bc50', 'Филиал Института деловой карьеры в Смоленской области', 'Филиал Института деловой карьеры в Смоленской области', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('02e7565d-9a7e-4b2d-843d-5b5b77d0ab97', 'Филиал Института деловой карьеры в Псковской области', 'Филиал Института деловой карьеры в Псковской области', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('cb50d0b4-eee1-4d58-945f-4a54a0bbe042', 'Филиал Института деловой карьеры в Пермском крае', 'Филиал Института деловой карьеры в Пермском крае', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('be939b32-9b93-42e7-a906-62dd9dd06331', 'Филиал Института деловой карьеры в Карачаево-Черкесской Республике', 'Филиал Института деловой карьеры в Карачаево-Черкесской Республике', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a28e4576-c5c7-4818-8400-a016ca99eb49', 'Филиал Института деловой карьеры в Тюменской области', 'Филиал Института деловой карьеры в Тюменской области', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c7eb0b09-d3b9-4f71-8264-3f18549c15c4', 'Филиал Института деловой карьеры в Республике Саха (Якутия)', 'Филиал Института деловой карьеры в Республике Саха (Якутия)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('14c4ab24-26db-46bd-990d-702933978ccc', 'Филиал Института деловой карьеры в Рязанской области', 'Филиал Института деловой карьеры в Рязанской области', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('870be8e8-c61c-4a99-99fd-db8bcee6c227', 'Читинский институт (филиал) Байкальского государственного университета', 'Читинский институт (филиал) Байкальского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('17000b06-344c-4d80-aa59-d0f4931fe0f5', 'Филиал Байкальского государственного университета г. Усть-Илимске', 'Филиал Байкальского государственного университета г. Усть-Илимске', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e9b0fbf6-3d5c-4f4c-93f8-385b8cb1fd37', 'Сарапульский политехнический институт Ижевский государственный технический университет имени М.Т. Калашникова', 'Сарапульский политехнический институт Ижевский государственный технический университет имени М.Т. Калашникова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('adb7a8d9-56f9-4478-9e50-39ecc0ae2f61', 'Воткинский филиал Ижевского государственного технического университета имени М.Т. Калашникова', 'Воткинский филиал Ижевского государственного технического университета имени М.Т. Калашникова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0765ab83-ba21-4e34-ae83-b691fd20209e', 'Глазовский инженерно-экономический институт (филиал) Ижевского государственного технического университета имени М.Т. Калашникова', 'Глазовский инженерно-экономический институт (филиал) Ижевского государственного технического университета имени М.Т. Калашникова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a207f087-c792-44c3-b538-a51db831b61b', 'Екатеринбургский институт физической культуры (филиал) Уральского государственного университета физической культуры', 'Екатеринбургский институт физической культуры (филиал) Уральского государственного университета физической культуры', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('694322cc-d8d9-41f2-83ba-1cb63abe48df', 'Башкирский институт физической культуры (филиал) Уральского государственного университета физической культуры', 'Башкирский институт физической культуры (филиал) Уральского государственного университета физической культуры', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('22787f0d-2dd1-410f-999a-e422a8255800', 'Сергиево-Посадский филиал Всероссийского государственного университета кинематографии имени С. А. Герасимова', 'Сергиево-Посадский филиал Всероссийского государственного университета кинематографии имени С. А. Герасимова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c6b3dbf7-eb9e-41f2-bf53-323564fc07dd', 'Ростовский-на-Дону филиал Всероссийского государственного университета кинематографии имени С.А. Герасимова', 'Ростовский-на-Дону филиал Всероссийского государственного университета кинематографии имени С.А. Герасимова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('cc570726-d817-49bc-99cd-1ee12811cb30', 'Себряковский филиал Волгоградского государственного технического университета', 'Себряковский филиал Волгоградского государственного технического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a315fda2-4379-4371-b6c2-fdcab11fa9b1', 'Иркутский филиал Всероссийского государственного университета кинематографии имени С.А. Герасимова', 'Иркутский филиал Всероссийского государственного университета кинематографии имени С.А. Герасимова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0031f0b1-4ab3-4418-84d9-b34c53f645fe', 'Уфимский государственный нефтяной технический университет, филиал в г. Октябрьском', 'Уфимский государственный нефтяной технический университет, филиал в г. Октябрьском', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3c01fd3b-c35a-4c10-9170-74a90f9ecbd1', 'Институт нефтепереработки и нефтехимии "Уфимский государственный нефтяной технический университет", филиал в г. Салавате', 'Институт нефтепереработки и нефтехимии "Уфимский государственный нефтяной технический университет", филиал в г. Салавате', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a687ad6b-8c0c-4d4e-8c00-0c629e5a7585', 'Уфимский государственный нефтяной технический университет, филиал в г. Стерлитамаке', 'Уфимский государственный нефтяной технический университет, филиал в г. Стерлитамаке', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e458c8df-13b6-4272-a7b1-adb160b5946b', 'Чайковский филиал Пермского национального исследовательского политехнического университета', 'Чайковский филиал Пермского национального исследовательского политехнического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('658f117c-2b1e-4577-93e8-5b4997eccb9e', 'Лысьвенский филиал Пермского национального исследовательского политехнического университета', 'Лысьвенский филиал Пермского национального исследовательского политехнического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2717b1b0-9e23-4d39-a73f-7fd6b7d03b54', 'Челябинский государственный университет Миасский филиал', 'Челябинский государственный университет Миасский филиал', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f35032e3-96f1-4ecc-8368-9e6adae25ac2', 'Альметьевский филиал Казанского национального исследовательского технического университета им. А.Н.Туполева-КАИ', 'Альметьевский филиал Казанского национального исследовательского технического университета им. А.Н.Туполева-КАИ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4fb2a2ec-4384-4b87-8f66-baae305facf6', 'Челябинский государственный университет Троицкий филиал', 'Челябинский государственный университет Троицкий филиал', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('05723e39-5b4c-4eeb-bbd3-9992ac45c378', 'Анапский филиал Московского педагогического государственного университета', 'Анапский филиал Московского педагогического государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('937fa133-91f6-4ae6-919e-a4fb598fc72a', 'Покровский филиал Московского педагогического государственного университета', 'Покровский филиал Московского педагогического государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1fd4999c-fd98-4a3c-a271-0ad879eabe0a', 'Дербентский филиал Московского педагогического государственного университета', 'Дербентский филиал Московского педагогического государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('baf21a41-80e0-460d-b511-4f734f92b3e4', 'Набережночелнинский филиал Казанского национального исследовательского технического университета им. А.Н. Туполева-КАИ', 'Набережночелнинский филиал Казанского национального исследовательского технического университета им. А.Н. Туполева-КАИ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b921cde0-e58e-4865-b300-66586225a5c1', 'Чистопольский филиал «Восток» Казанского национального исследовательского технического университета им. А.Н. Туполева-КАИ', 'Чистопольский филиал «Восток» Казанского национального исследовательского технического университета им. А.Н. Туполева-КАИ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a558e8b0-7eda-45c2-a3f9-d3ae05e9ace9', 'Ставропольский филиал Московского педагогического государственного университета', 'Ставропольский филиал Московского педагогического государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('bcf3af6f-1a33-4c71-b64d-c65e08d35fec', 'Черняховский Филиал Московского педагогического государственного университета', 'Черняховский Филиал Московского педагогического государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1045289c-0e1f-4b72-8450-8ad66a8caaaa', 'Поволжский казачий институт управления и пищевых технологий (филиал) МГУТУ им. К.Г. Разумовского', 'Поволжский казачий институт управления и пищевых технологий (филиал) МГУТУ им. К.Г. Разумовского', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c87642f2-09eb-44f2-bc64-b2771c5b04f5', 'Институт агроэкологии - филиал Южно-Уральского государственного аграрного университета', 'Институт агроэкологии - филиал Южно-Уральского государственного аграрного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('02cda22c-c63b-4974-b03f-7fd0a003c956', 'Пензенский казачий институт технологий (филиал) МГУТУ им. К.Г. Разумовского', 'Пензенский казачий институт технологий (филиал) МГУТУ им. К.Г. Разумовского', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a852a492-5cfc-46f1-aa64-64eef479f88b', 'Институт агроинженерии Южно-Уральского государственного аграрного университета', 'Институт агроинженерии Южно-Уральского государственного аграрного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2a9cdd88-f1ab-4de7-9c88-55f37fbadab7', 'Кубанский казачий государственный институт пищевой индустрии и бизнеса (филиал) МГУТУ им. К.Г. Разумовского', 'Кубанский казачий государственный институт пищевой индустрии и бизнеса (филиал) МГУТУ им. К.Г. Разумовского', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2f97a68f-bd26-4e52-8b95-853e9690056a', 'Донской казачий государственный институт пищевых технологий и бизнеса (филиал) МГУТУ им. К.Г. Разумовского', 'Донской казачий государственный институт пищевых технологий и бизнеса (филиал) МГУТУ им. К.Г. Разумовского', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4f29c710-b535-428b-8358-fa7ca51fbbe4', 'Сибирский казачий институт технологий и управления (филиал) МГУТУ им. К.Г. Разумовского', 'Сибирский казачий институт технологий и управления (филиал) МГУТУ им. К.Г. Разумовского', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e27840a8-7301-4495-a150-5ebbf88eb3ca', 'Курганский институт железнодорожного транспорта - филиал Уральского государственного университета путей сообщения', 'Курганский институт железнодорожного транспорта - филиал Уральского государственного университета путей сообщения', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('ea11181e-52f8-47d3-bbbb-5e8934e2595a', 'г. Нижневартовск, Ханты-Мансийский автономный округ — Югра', 'Россия', 'Ханты-Мансийский автономный округ — Югра', 'Нижневартовск', 'г. Нижневартовск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('78cd1de0-febe-4951-846b-385847be0a0c', 'Филиал Уральского государственного университета путей сообщения в. г. Нижнем Тагиле', 'Филиал Уральского государственного университета путей сообщения в. г. Нижнем Тагиле', 'ea11181e-52f8-47d3-bbbb-5e8934e2595a', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('455f085c-a4e2-4c7f-9475-223ab36414e7', 'Бирский филиал Уфимского университета науки и технологий', 'Бирский филиал Уфимского университета науки и технологий', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('fbab02f4-83dd-45ad-8de8-03d537171e0f', 'Стерлитамакский филиал Уфимского университета науки и технологий', 'Стерлитамакский филиал Уфимского университета науки и технологий', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('11bc4dcd-ef6f-4a66-b641-e7bb05d8533c', 'г. Тюмень, Тюменская область', 'Россия', 'Тюменская область', 'Тюмень', 'г. Тюмень');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c3efe099-958b-4bfc-9209-a1e867a11223', 'Филиал Уральского государственного университета путей сообщения в г. Тюмени', 'Филиал Уральского государственного университета путей сообщения в г. Тюмени', '11bc4dcd-ef6f-4a66-b641-e7bb05d8533c', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b599a7bc-ee03-406d-8f50-331763e4c252', 'Челябинский институт путей сообщения - филиал Уральского государственного университета путей сообщения', 'Челябинский институт путей сообщения - филиал Уральского государственного университета путей сообщения', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5e8f6d3b-264b-471f-aca3-42f66abb0398', 'Пермский институт железнодорожного транспорта - филиал Уральского государственного университета путей сообщения', 'Пермский институт железнодорожного транспорта - филиал Уральского государственного университета путей сообщения', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0056403f-f36a-449b-bb8e-b8419e946918', 'Нефтекамский филиал Уфимского университета науки и технологий', 'Нефтекамский филиал Уфимского университета науки и технологий', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b86e6edb-d2c4-42c7-9ac9-bc204399b9bf', 'Пермский филиал Волжского государственного университета водного транспорта', 'Пермский филиал Волжского государственного университета водного транспорта', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b88d950f-75d2-42ca-b8f0-078cc9cbdabf', 'Институт морского и речного флота имени Героя Советского Союза М.П. Девятаева Казанский филиал Волжского государственного университета водного транспорта', 'Институт морского и речного флота имени Героя Советского Союза М.П. Девятаева Казанский филиал Волжского государственного университета водного транспорта', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('718dec2d-c5bb-42c1-a336-3c29ce82d51c', 'Самарский филиал Волжского государственного университета водного транспорта', 'Самарский филиал Волжского государственного университета водного транспорта', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ca47b6b1-5ed3-4f6a-8097-cdf2c838e249', 'Каспийский институт морского и речного транспорта - филиал Волжского государственного университета водного транспорта', 'Каспийский институт морского и речного транспорта - филиал Волжского государственного университета водного транспорта', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a768a383-97db-4361-81cc-93fb96614ec4', 'Северный (Арктический) федеральный университет имени М.В. Ломоносова в г. Северодвинске', 'Северный (Арктический) федеральный университет имени М.В. Ломоносова в г. Северодвинске', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c226e974-b2ea-48e2-b7a3-43bc85eba5a5', 'Новороссийский политехнический институт (филиал) Кубанского государственного технологического университета', 'Новороссийский политехнический институт (филиал) Кубанского государственного технологического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ac6ed614-8e2d-45f8-90a2-1b65dfc0de65', 'Армавирский механико-технологический институт (филиал) Кубанского государственного технологического университета', 'Армавирский механико-технологический институт (филиал) Кубанского государственного технологического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4b91041f-8b73-4303-96dd-b36bac1c001c', 'Северо-Западный институт (филиал) Московского государственного юридического университета имени О.Е. Кутафина', 'Северо-Западный институт (филиал) Московского государственного юридического университета имени О.Е. Кутафина', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b7437981-e085-46c3-97e2-a48618af4e1e', 'Волго-Вятский институт (филиал) Московского государственного юридического университета имени О.Е. Кутафина', 'Волго-Вятский институт (филиал) Московского государственного юридического университета имени О.Е. Кутафина', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8f890a5a-a19b-44ed-8379-136dd7f8230d', 'Оренбургский институт (филиал)  Московского государственного юридического университета имени О.Е. Кутафина', 'Оренбургский институт (филиал)  Московского государственного юридического университета имени О.Е. Кутафина', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('28c3f83d-88c2-42ec-b1f0-5c3ff96853ef', 'Старооскольский филиал Белгородского государственного национального исследовательского университета', 'Старооскольский филиал Белгородского государственного национального исследовательского университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7fe579ec-97fe-49cd-95c3-905ecf19b482', 'Муромский институт (филиал) Владимирского государственного университета имени Александра Григорьевича и Николая Григорьевича Столетовых', 'Муромский институт (филиал) Владимирского государственного университета имени Александра Григорьевича и Николая Григорьевича Столетовых', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6f8ca8dd-1a95-4bbe-8f80-3773e091383c', 'Филиал Самарского государственного университета путей сообщения в Нижнем Новгороде', 'Филиал Самарского государственного университета путей сообщения в Нижнем Новгороде', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('488cfb5b-a4e9-4fc6-a78b-f9dc1b9fbe25', 'г. Саратов, Саратовская область', 'Россия', 'Саратовская область', 'Саратов', 'г. Саратов');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('90fba215-a3bb-4161-9bce-97b0cc2e2019', 'Филиал Самарского государственного университета путей сообщения в г. Саратове', 'Филиал Самарского государственного университета путей сообщения в г. Саратове', '488cfb5b-a4e9-4fc6-a78b-f9dc1b9fbe25', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('18bd0ea3-daa9-4c24-81d0-5824cde8f8a7', 'Беловский институт (филиал) Кемеровского государственного университета', 'Беловский институт (филиал) Кемеровского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d3fa90c8-d97b-4e4e-b9ba-e8aab39b0ce2', 'Кузбасский гуманитарно-педагогический институт (филиал) Кемеровского государственного университета', 'Кузбасский гуманитарно-педагогический институт (филиал) Кемеровского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('046837f3-12a3-4b33-ac5f-8c345169f50e', 'Ишимский педагогический институт им. П.П. Ершова (филиал) Тюменского государственного университета', 'Ишимский педагогический институт им. П.П. Ершова (филиал) Тюменского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('24df792e-f259-4512-b22b-20b88222df1b', 'Тобольский педагогический институт им. Д.И. Менделеева  (филиал) Тюменского государственного университета', 'Тобольский педагогический институт им. Д.И. Менделеева  (филиал) Тюменского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('dd9571aa-b815-4d5c-b31b-659a192cab92', 'г. Новороссийск, Краснодарский край', 'Россия', 'Краснодарский край', 'Новороссийск', 'г. Новороссийск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('45e22a2c-456a-436e-8b0c-dfb6e3b26bfc', 'Белгородский государственный технологический университет им. В.Г. Шухова филиал в г. Новороссийске', 'Белгородский государственный технологический университет им. В.Г. Шухова филиал в г. Новороссийске', 'dd9571aa-b815-4d5c-b31b-659a192cab92', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e15069bf-e51d-41fe-ac81-613d085bfb18', 'Северо-Кавказский филиал Белгородского государственного технологического университета им. В.Г. Шухова', 'Северо-Кавказский филиал Белгородского государственного технологического университета им. В.Г. Шухова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b319c789-db93-46fd-9ffa-84690bb8a901', 'Федоскинский институт лаковой миниатюрной живописи - филиал Высшей школы народных искусств (Академия)', 'Федоскинский институт лаковой миниатюрной живописи - филиал Высшей школы народных искусств (Академия)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8a05f96c-537e-40ca-bbca-7c9747c2a583', 'Азово-Черноморский инженерный институт (филиал) Донского государственного аграрного университета', 'Азово-Черноморский инженерный институт (филиал) Донского государственного аграрного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b0bccd4c-69af-4e29-be71-54d65fbcc1e6', 'Новочеркасский инженерно-мелиоративный институт имени А.К. Кортунова - филиал Донского государственного аграрного университета', 'Новочеркасский инженерно-мелиоративный институт имени А.К. Кортунова - филиал Донского государственного аграрного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('434e9b0e-da3f-4f48-aa60-18e0f5093406', 'Кировский филиал Санкт-Петербургского Гуманитарного университета профсоюзов', 'Кировский филиал Санкт-Петербургского Гуманитарного университета профсоюзов', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('43e38fec-214b-4326-9b63-2fe7ef752d0f', 'Самарский филиал Санкт-Петербургского Гуманитарного университета профсоюзов', 'Самарский филиал Санкт-Петербургского Гуманитарного университета профсоюзов', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3ce93fd9-f388-497d-aa89-1be18f1a2e59', 'Курский институт кооперации (филиал) Белгородского университета кооперации, экономики и права', 'Курский институт кооперации (филиал) Белгородского университета кооперации, экономики и права', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('454b17b8-9387-4c3f-9549-562987850b9e', 'Липецкий институт кооперации (филиал) Белгородского университета кооперации, экономики и права', 'Липецкий институт кооперации (филиал) Белгородского университета кооперации, экономики и права', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ac42712e-a4a0-4b97-90ae-5454713bc123', 'Красноярский институт железнодорожного транспорта  – филиал ИрГУПС', 'Красноярский институт железнодорожного транспорта  – филиал ИрГУПС', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('903d695e-a505-4905-94a8-ed63453fb664', 'Забайкальский институт железнодорожного транспорта – филиал ИрГУПС', 'Забайкальский институт железнодорожного транспорта – филиал ИрГУПС', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4eb7d067-a4e7-4c01-8ba2-38e6c44e05c3', 'Алтайский государственный университет филиал в г. Бийске', 'Алтайский государственный университет филиал в г. Бийске', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c9f8eb28-4df2-4ddf-bfbb-bb3df2a86a39', 'Алтайский государственный университет филиал в г. Белокурихе', 'Алтайский государственный университет филиал в г. Белокурихе', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('32bcbc87-5656-4713-b026-50362cd0f110', 'Зеленодольский институт машиностроения и информационных технологий (филиал) Казанского национального исследовательского технического университета им. А.Н.Туполева-КАИ', 'Зеленодольский институт машиностроения и информационных технологий (филиал) Казанского национального исследовательского технического университета им. А.Н.Туполева-КАИ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0ca5f774-5b92-4740-a55e-baee619f5934', 'Рыбинский государственный авиационный технический университет имени П.А. Соловьева - Тутаевский филиал', 'Рыбинский государственный авиационный технический университет имени П.А. Соловьева - Тутаевский филиал', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c1f815d3-63b1-43b5-b660-5081cdfd42bc', 'Керченский государственный морской технологический университет - филиал в г. Феодосия', 'Керченский государственный морской технологический университет - филиал в г. Феодосия', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e74aeeb3-ba52-450e-8728-3307a563e269', 'Санкт-Петербургский государственный экономический университет - филиал в г. Кизляре', 'Санкт-Петербургский государственный экономический университет - филиал в г. Кизляре', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a4416eb0-7a41-4f58-a6d8-4b337334bc20', 'Чукотский филиал Северо-Восточного федерального университета имени М.К. Аммосова', 'Чукотский филиал Северо-Восточного федерального университета имени М.К. Аммосова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('264ffc20-98f1-44ae-9535-98ef18f79ba0', 'Дмитровский рыбохозяйственный технологический институт (филиал) Астраханского государственного технического университета', 'Дмитровский рыбохозяйственный технологический институт (филиал) Астраханского государственного технического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3997c117-6500-47de-b6d6-222beeea14a4', 'Астраханский государственный университет имени В.Н. Татищева - филиал в г. Знаменске', 'Астраханский государственный университет имени В.Н. Татищева - филиал в г. Знаменске', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('aff2d9d8-a9f3-4f90-9150-fad0ed95f2fc', 'Нижнетагильский государственный социально-педагогический институт (филиал) Уральского государственного педагогического университета', 'Нижнетагильский государственный социально-педагогический институт (филиал) Уральского государственного педагогического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5851e233-5099-40b7-8023-ca4b8092e0e0', 'Тарский филиал Омского государственного аграрного университета им. П.А. Столыпина', 'Тарский филиал Омского государственного аграрного университета им. П.А. Столыпина', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f5d1e090-7480-4c04-a188-e32ad69e07bd', 'Шахтинский автодорожный институт (филиал) Южно-Российского государственного политехнического университета (НПИ) им. М.И. Платова', 'Шахтинский автодорожный институт (филиал) Южно-Российского государственного политехнического университета (НПИ) им. М.И. Платова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c23c1a92-d26e-45bf-8249-13a48250a951', 'Каменский технологический институт (филиал) Южно-Российского государственного политехнического университета (НПИ) им. М.И. Платова', 'Каменский технологический институт (филиал) Южно-Российского государственного политехнического университета (НПИ) им. М.И. Платова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0a570d02-1380-4c8c-b095-2650f8e47885', 'Удмуртский государственный университет филиал в городе Нижняя Тура', 'Удмуртский государственный университет филиал в городе Нижняя Тура', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('03865aac-fa04-463b-9811-c903fc13eea0', 'Севастопольский экономико-гуманитарный институт (филиал) Крымского федерального университета имени В.И. Вернадского', 'Севастопольский экономико-гуманитарный институт (филиал) Крымского федерального университета имени В.И. Вернадского', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('169fd077-d299-42a0-acf2-43573ef8a789', 'Институт педагогического образования и менеджмента (филиал) Крымского федерального университета имени В.И. Вернадского', 'Институт педагогического образования и менеджмента (филиал) Крымского федерального университета имени В.И. Вернадского', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('01194a98-0c27-4e54-b39a-c055951d7d49', 'Гуманитарно-педагогическая академия (филиал) Крымского федерального университета имени В.И. Вернадского', 'Гуманитарно-педагогическая академия (филиал) Крымского федерального университета имени В.И. Вернадского', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('401392af-b1e2-461f-9045-f42fb93b662b', 'Евпаторийский институт социальных наук (филиал) Крымского федерального университета имени В.И. Вернадского', 'Евпаторийский институт социальных наук (филиал) Крымского федерального университета имени В.И. Вернадского', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9b379344-7372-404c-a58c-a041c264b763', 'Адыгейский государственный университет - филиал в г. Белореченске', 'Адыгейский государственный университет - филиал в г. Белореченске', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('d179777e-1aa1-45f7-979b-b1fbbffb6fbd', 'г. Севастополь, Севастополь', 'Россия', 'Севастополь', 'Севастополь', 'г. Севастополь');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7516eed1-1e7b-4d72-9f45-7e36d6505859', 'Морской институт имени вице-адмирала В.А. Корнилова - филиал ГМУ им. адм. Ф.Ф. Ушакова в г. Севастополь', 'Морской институт имени вице-адмирала В.А. Корнилова - филиал ГМУ им. адм. Ф.Ф. Ушакова в г. Севастополь', 'd179777e-1aa1-45f7-979b-b1fbbffb6fbd', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('f5b23885-6a3f-45a5-b193-61a8d4f3b733', 'г. Ростов-на-Дону, Ростовская область', 'Россия', 'Ростовская область', 'Ростов-на-Дону', 'г. Ростов-на-Дону');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('76ca4ebf-7082-46bf-840e-9b22f649101b', 'Институт водного транспорта имени Г.Я. Седов - филиал ГМУ им. адм. Ф.Ф. Ушакова в г. Ростов-на-Дону', 'Институт водного транспорта имени Г.Я. Седов - филиал ГМУ им. адм. Ф.Ф. Ушакова в г. Ростов-на-Дону', 'f5b23885-6a3f-45a5-b193-61a8d4f3b733', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('a1646ee5-e3b7-4ebd-b7d1-2e947f3c46b6', 'г. Ижевск, Удмуртия', 'Россия', 'Удмуртия', 'Ижевск', 'г. Ижевск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('60f946f7-b149-4953-a257-a557ce29538e', 'Филиал Глазовского государственного инженерно-педагогического университета имени В.Г. Короленко в г. Ижевске', 'Филиал Глазовского государственного инженерно-педагогического университета имени В.Г. Короленко в г. Ижевске', 'a1646ee5-e3b7-4ebd-b7d1-2e947f3c46b6', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d0ca2b2b-91c4-4dd9-898c-60770bcd9b52', 'Дагестанский филиал Российского государственного педагогического университета им. А. И. Герцена', 'Дагестанский филиал Российского государственного педагогического университета им. А. И. Герцена', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7bbb0c7b-e964-4f89-8332-19ce3bdd7a25', 'Балахнинский филиал Национального исследовательского Нижегородского государственного университета им. Н.И. Лобачевского', 'Балахнинский филиал Национального исследовательского Нижегородского государственного университета им. Н.И. Лобачевского', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9438e7a9-4c1f-4eeb-8898-4e818e01fb8d', 'Филиал Самарского государственного технического университета в г. Белебее Республики Башкортостан', 'Филиал Самарского государственного технического университета в г. Белебее Республики Башкортостан', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d16525cf-222d-4a27-b4fd-02773c375ee9', 'Оренбургский филиал Поволжского государственного университета телекоммуникаций и информатики', 'Оренбургский филиал Поволжского государственного университета телекоммуникаций и информатики', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('dd56bcfe-1d0d-4e61-8a73-152226eb598e', 'Алатырский филиал Чувашского государственного университета имени И.Н. Ульянова', 'Алатырский филиал Чувашского государственного университета имени И.Н. Ульянова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b157e9c0-b4ea-4cbd-9803-21390cb7e26a', 'Балаковский филиал Саратовской государственной юридической академии', 'Балаковский филиал Саратовской государственной юридической академии', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('cf9ffb1b-eeee-46fd-8276-c253ff6d7973', 'Астраханский филиал Саратовской государственной юридической академии', 'Астраханский филиал Саратовской государственной юридической академии', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('044d8fd3-87e3-40c1-b7a3-c277d6ad213f', 'Балашовский институт (филиал) Саратовского национального исследовательского государственного университета им. Н.Г. Чернышевского', 'Балашовский институт (филиал) Саратовского национального исследовательского государственного университета им. Н.Г. Чернышевского', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d7471402-f7b4-4943-929b-a4c0104f0f80', 'Майкопский государственный технологический университет - филиал в поселке Яблоновском', 'Майкопский государственный технологический университет - филиал в поселке Яблоновском', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('895dd770-2e59-490a-afc3-a715505c82b1', 'Ачинский филиал Красноярского государственного аграрного университета', 'Ачинский филиал Красноярского государственного аграрного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('bc25d702-2b34-4302-bb42-0745a8457d54', 'Политехнический институт (филиал) Северо-Восточного федерального университета имени М.К.Аммосова в г. Мирном', 'Политехнический институт (филиал) Северо-Восточного федерального университета имени М.К.Аммосова в г. Мирном', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('007eea1d-8ea7-4916-b333-6eb61a5fa281', 'Тамбовский филиал Мичуринского государственного аграрного университета', 'Тамбовский филиал Мичуринского государственного аграрного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c0d3b1cb-b436-49c1-923e-56f78f8e4c66', 'Пятигорский медико-фармацевтический институт – филиал Волгоградского государственного медицинского университет', 'Пятигорский медико-фармацевтический институт – филиал Волгоградского государственного медицинского университет', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('82b4276f-f4be-4651-9410-547d33c8b10b', 'Филиал Магнитогорского государственного технического университета им. Г.И. Носова в г. Белорецк', 'Филиал Магнитогорского государственного технического университета им. Г.И. Носова в г. Белорецк', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c80b70a6-4bf6-4c6e-ae33-1323ab41226c', 'Энгельсский технологический институт (филиал) СГТУ', 'Энгельсский технологический институт (филиал) СГТУ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('73a81d7b-ef4b-4258-ab1c-c01c0a668df0', 'Гудермесский филиал Института финансов и права', 'Гудермесский филиал Института финансов и права', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('46fb1f67-0b9a-4e39-8a52-e73c5d8214b1', 'Юргинский технологический институт (филиал) Национального исследовательского Томского политехнического университета', 'Юргинский технологический институт (филиал) Национального исследовательского Томского политехнического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('201fae12-8757-4cce-9e19-ce43fbf7e1b9', 'Забайкальский аграрный институт - филиал Иркутской государственной сельскохозяйственной академии', 'Забайкальский аграрный институт - филиал Иркутской государственной сельскохозяйственной академии', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('58ae9533-e76c-4128-9644-b42da168ae04', 'Новосибирский юридический институт (филиал) Национального исследовательского Томского государственного университета', 'Новосибирский юридический институт (филиал) Национального исследовательского Томского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7d081bab-de1a-406b-bbec-f901d139a394', 'Инзенский филиал Ульяновского государственного университета', 'Инзенский филиал Ульяновского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('bc9e503d-e75d-4a9b-b921-19de46f67fd5', 'Сызранский филиал Самарского государственного экономического университета', 'Сызранский филиал Самарского государственного экономического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4e28a4a0-58bd-4f55-a052-7271c8bdbcb9', 'Сыктывкарский лесной институт (филиал) СПбГЛТУ', 'Сыктывкарский лесной институт (филиал) СПбГЛТУ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4d4b7534-7700-41d6-812b-ade6e1fe3387', 'Одинцовский филиал Московского государственного института международных отношений', 'Одинцовский филиал Московского государственного института международных отношений', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4a8b8dd7-898c-421a-a469-c7639ffa82fd', 'Волжский филиал Волгоградского государственного университета', 'Волжский филиал Волгоградского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ad7b969b-e582-482a-964f-fbc2402af5b4', 'Филиал Московского государственного университета имени М.В. Ломоносова в городе Сарове', 'Филиал Московского государственного университета имени М.В. Ломоносова в городе Сарове', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3c4e79b9-c6b8-4fee-b4b8-90c6e3bae537', 'Каменский филиал Российского нового университета', 'Каменский филиал Российского нового университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('bd158f59-da78-4e33-8999-6d7a50f86625', 'Казанский филиал Академии труда и социальных отношений', 'Казанский филиал Академии труда и социальных отношений', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('fc0dca22-f320-4bfd-8ffa-9b285e2deaa5', 'Оренбургский филиал Академии труда и социальных отношений', 'Оренбургский филиал Академии труда и социальных отношений', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a25e83d3-4353-43bc-b8f5-3d521384692d', 'Дагестанский гуманитарный институт (филиал) Академии труда и социальных отношений', 'Дагестанский гуманитарный институт (филиал) Академии труда и социальных отношений', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('dff04b17-a5ea-49c7-863a-16cc0f191bab', 'Курганский филиал Академии труда и социальных отношений', 'Курганский филиал Академии труда и социальных отношений', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9e1911db-f907-4acd-80ec-9d5a29aa1b6c', 'Уральский социально-экономический институт (филиал) Академии труда и социальных отношений', 'Уральский социально-экономический институт (филиал) Академии труда и социальных отношений', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6944f63d-5c36-4a9d-b3a5-a0af88dd0190', 'Красноярский филиал Академии труда и социальных отношений', 'Красноярский филиал Академии труда и социальных отношений', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9c29fbd6-00fd-48b6-991a-f98701d2da1b', 'Якутский экономико-правовой институт (филиал) Академии труда и социальных отношений', 'Якутский экономико-правовой институт (филиал) Академии труда и социальных отношений', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('a9867145-022a-4ad6-9513-7d810fb706ef', 'г. Омск, Омская область', 'Россия', 'Омская область', 'Омск', 'г. Омск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5e6cbd3a-0bc5-4c99-9bf7-b77141f00553', 'Университет «Синергия» филиал в г. Омск', 'Университет «Синергия» филиал в г. Омск', 'a9867145-022a-4ad6-9513-7d810fb706ef', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('253cac02-51fc-44cd-9ec6-259f89bafd5f', 'Филиал РГИСИ в Калининграде – «Балтийская Высшая школа музыкального и театрального искусства»', 'Филиал РГИСИ в Калининграде – «Балтийская Высшая школа музыкального и театрального искусства»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1a1d182f-0e38-420f-a643-7419707f54d1', 'Ивановский филиал Институт управления', 'Ивановский филиал Институт управления', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('59788805-0464-47a7-a1a6-2213c8b0cd6c', 'Клинский филиал Институт государственного администрирования', 'Клинский филиал Институт государственного администрирования', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ba8d8f59-2cb5-4979-af12-61a5aa92cf16', 'Смоленский филиал Саратовской государственной юридической академии', 'Смоленский филиал Саратовской государственной юридической академии', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('57c096a9-2c32-4dae-af8a-8a106ebfdaa7', 'Богородский институт художественной резьбы по дереву', 'Богородский институт художественной резьбы по дереву', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b63a2db3-6e0a-41e0-80ad-20781ad5bf72', 'Саратовский медицинский университет «Реавиз»', 'Саратовский медицинский университет «Реавиз»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2052e692-33e2-45a3-b747-c2834a80f4ca', 'Астраханский филиал Института социальных и гуманитарных знаний', 'Астраханский филиал Института социальных и гуманитарных знаний', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('444de88d-ae81-45bd-ac78-e77f5591186a', 'Индустриальный институт (филиал) Югорского государственного университета', 'Индустриальный институт (филиал) Югорского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f4a5348c-4068-4887-b407-6bf6807568bd', 'Байкальский государственный университет филиал в г. Братске', 'Байкальский государственный университет филиал в г. Братске', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7ebab2b0-639a-4a5e-8e7a-717d0db0de6e', 'Сибирская Высшая школа музыкального и театрального искусства', 'Сибирская Высшая школа музыкального и театрального искусства', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0d2c2243-c8da-416b-a6f5-991cb688b7c1', 'Якутский филиал Института государственного администрирования', 'Якутский филиал Института государственного администрирования', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('322cedba-e8c5-46b5-8ba2-323a89648f43', 'Владивостокский государственный университет в г. Находке', 'Владивостокский государственный университет в г. Находке', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('77d71361-167b-4cef-a4d1-1de87dd583b3', 'Московский финансово-промышленный университет «Синергия» Калмыцкий филиал', 'Московский финансово-промышленный университет «Синергия» Калмыцкий филиал', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e4cc8281-0751-4c6d-8ca2-4e7c34368915', 'Московский финансово-промышленный университет «Синергия» Карачаево-Черкесский филиал', 'Московский финансово-промышленный университет «Синергия» Карачаево-Черкесский филиал', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('68e5fbcb-15fc-4994-adae-83a6ddd1187d', 'Поволжская высшая школа интеллектуальной собственности', 'Поволжская высшая школа интеллектуальной собственности', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0629d077-0c2c-47c3-ba41-a173d80cdeda', 'Филиал Российского государственного социального университета в г. Минске Республики Беларусь', 'Филиал Российского государственного социального университета в г. Минске Республики Беларусь', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e140ffe2-9292-4017-aa79-14ccaf0466ed', 'Филиал Российского государственного социального университета в г. Ош Киргизской Республики', 'Филиал Российского государственного социального университета в г. Ош Киргизской Республики', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('572c59e3-f464-42da-9f2c-d47ed478b566', 'Инженерно-физический институт биомедицины ИАТЭ НИЯУ МИФИ', 'Инженерно-физический институт биомедицины ИАТЭ НИЯУ МИФИ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0b57a66a-21b3-470f-bf68-2ff16673c1f2', 'Нововоронежский политехнический институт НИЯУ МИФИ', 'Нововоронежский политехнический институт НИЯУ МИФИ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('72798ea3-1cf7-4b8d-802c-fea4cba16c7c', 'Филиал Военной академии материально-технического обеспечения имени генерала армии А.В. Хрулёва  в г. Омске', 'Филиал Военной академии материально-технического обеспечения имени генерала армии А.В. Хрулёва  в г. Омске', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ea832142-9f6b-4878-b5ea-f78aa52d07db', 'Институт востоковедения и африканистики НИУ ВШЭ — Санкт-Петербург', 'Институт востоковедения и африканистики НИУ ВШЭ — Санкт-Петербург', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a231ff79-bf77-4b36-9409-6d91fbeaacb0', 'Высшая школа экономики Москвы РЭУ им. Г. В. Плеханова', 'Высшая школа экономики Москвы РЭУ им. Г. В. Плеханова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5450909e-83f5-4485-9a51-9cae3a1533f8', 'Филиал Московского международного университета информационных технологий «Академия ТОП Калининград»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Калининград»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('079bf57a-c190-4e11-adb5-7334fa500937', 'Филиал Московского международного университета информационных технологий «Академия ТОП Нальчик»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Нальчик»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('db4d1fef-9a43-44b4-93f7-eb1408d9a791', 'Филиал Московского международного университета информационных технологий «Академия ТОП Петрозаводск»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Петрозаводск»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9a8af844-d898-46fe-ac0e-309e6e4318d1', 'Филиал Московского международного университета информационных технологий «Академия ТОП Санкт-Петербург»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Санкт-Петербург»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('63f2a741-5149-43c7-9e6a-14035b7b7883', 'Филиал Московского международного университета информационных технологий «Академия ТОП Смоленск»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Смоленск»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0b5db983-d1b0-4869-bc92-133a27463e51', 'Филиал Московского международного университета информационных технологий «Академия ТОП Сочи»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Сочи»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1460113d-1de5-423d-a576-7ba79c321319', 'Филиал Московского международного университета информационных технологий «Академия ТОП Тюмень»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Тюмень»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3c9d5a68-6084-41b1-b79b-fd5e4aebc7df', 'Филиал Московского международного университета информационных технологий «Академия ТОП Абакан»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Абакан»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2b2b3254-102f-4913-bc6e-613ef1eb95e8', 'Филиал Московского международного университета информационных технологий «Академия ТОП Анапа»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Анапа»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('94aa29c1-8837-42f7-a89a-69e210fdae44', 'Филиал Московского международного университета информационных технологий «Академия ТОП Барнаул»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Барнаул»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d2047957-3e52-4a10-8b9f-1684b79241e7', 'Филиал Московского международного университета информационных технологий «Академия ТОП Бийск»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Бийск»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('11927de8-09aa-433f-91dd-c413d71bc69d', 'Филиал Московского международного университета информационных технологий «Академия ТОП Владивосток»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Владивосток»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('97f693f4-f3fa-4f8f-a113-ae99c8e83035', 'Филиал Московского международного университета информационных технологий «Академия ТОП Владикавказ»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Владикавказ»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8eb368ea-f725-49bc-a5a8-83c5dea1e491', 'Филиал Московского международного университета информационных технологий «Академия ТОП Владимир»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Владимир»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a741abcd-058c-4e80-ad0d-fc0648401dad', 'Филиал Московского международного университета информационных технологий «Академия ТОП Волгоград»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Волгоград»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('08885bc0-a2bf-4302-87f1-69d76d6af100', 'Филиал Московского международного университета информационных технологий «Академия ТОП Воронеж»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Воронеж»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3117707a-707b-4c4a-8ffc-5314a9668a37', 'Филиал Московского международного университета информационных технологий «Академия ТОП Екатеринбург»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Екатеринбург»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('43393b58-088f-416c-ad35-4212b9c27325', 'Филиал Московского международного университета информационных технологий «Академия ТОП Иваново»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Иваново»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f8ccf193-f242-4487-8e6c-f9e56476dbd8', 'Филиал Московского международного университета информационных технологий «Академия ТОП Ижевск»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Ижевск»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0a3b61da-ebe0-4014-92be-806b145aa3ec', 'Филиал Московского международного университета информационных технологий «Академия ТОП Иркутск»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Иркутск»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9af4fabc-79bd-44e4-bce3-ab042df30c85', 'Филиал Московского международного университета информационных технологий «Академия ТОП Казань»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Казань»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ea14e043-53ec-4daf-bf46-cb8bb5c2772b', 'Филиал Московского международного университета информационных технологий «Академия ТОП Киров»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Киров»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('fb5e793b-2c38-4cc6-97d7-5eebcc209e7b', 'Филиал Московского международного университета информационных технологий «Академия ТОП Краснодар»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Краснодар»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('51a5b151-a547-4038-8776-e839368cb561', 'Филиал Московского международного университета информационных технологий «Академия ТОП Красноярск»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Красноярск»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e29fa519-5871-4a57-9242-393753dce41b', 'Филиал Московского международного университета информационных технологий «Академия ТОП Находка»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Находка»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1d1ab22a-1233-4bdb-9e56-be05d35098fe', 'Филиал Московского международного университета информационных технологий «Академия ТОП Нижний Новгород»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Нижний Новгород»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('76d0c446-4cd4-4bc5-8ded-47cf6c5a6db2', 'Филиал Московского международного университета информационных технологий «Академия ТОП Новороссийск»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Новороссийск»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('bc9881ef-d4a7-40ea-8a69-3ecb81383db6', 'Филиал Московского международного университета информационных технологий «Академия ТОП Новосибирск»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Новосибирск»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('59711756-6db6-4f90-879a-65bdb8bec551', 'Филиал Московского международного университета информационных технологий «Академия ТОП Обнинск»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Обнинск»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9b388be9-34aa-43d2-b5ec-c796678e99ac', 'Филиал Московского международного университета информационных технологий «Академия ТОП Одинцово»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Одинцово»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ec584985-4f3f-4f2d-824b-544c9e2ff26c', 'Филиал Московского международного университета информационных технологий «Академия ТОП Орел»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Орел»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('88d4d0ce-b0d6-4baf-9a64-5a660815797f', 'Филиал Московского международного университета информационных технологий «Академия ТОП Пермь»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Пермь»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6b222c74-a952-4403-ba16-e98a534fe335', 'Филиал Московского международного университета информационных технологий «Академия ТОП Псков»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Псков»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('de125067-df8a-43db-ae7f-5e6e8af7459a', 'Филиал Московского международного университета информационных технологий «Академия ТОП Ростов-на-Дону»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Ростов-на-Дону»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('84bc657a-663d-4f9f-a778-348acb4df40f', 'Филиал Московского международного университета информационных технологий «Академия ТОП Рязань»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Рязань»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c09cb9e2-cbf0-456e-b2c6-c51f7621f605', 'Филиал Московского международного университета информационных технологий «Академия ТОП Самара»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Самара»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e97d1acf-bc2d-420a-82cb-57ad48048bb4', 'Филиал Московского международного университета информационных технологий «Академия ТОП Саратов»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Саратов»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('be4757df-eb88-4f68-aefd-1d561f467e38', 'Филиал Московского международного университета информационных технологий «Академия ТОП Севастополь»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Севастополь»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a335a48c-8e48-4ea1-92a8-3f37abace90c', 'Филиал Московского международного университета информационных технологий «Академия ТОП Серпухов»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Серпухов»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e0dd532f-07ce-4c27-be4e-b1f19a5111d4', 'Филиал Московского международного университета информационных технологий «Академия ТОП Ставрополь»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Ставрополь»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4820573a-989c-4baa-9577-452e7d9d3b45', 'Филиал Московского международного университета информационных технологий «Академия ТОП Старый Оскол»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Старый Оскол»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4e4fe8f3-c170-441e-9957-a0c708f1670e', 'Филиал Московского международного университета информационных технологий «Академия ТОП Сыктывкар»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Сыктывкар»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('76550107-32f7-467c-88ba-eb88caa4ae1e', 'Филиал Московского международного университета информационных технологий «Академия ТОП Тольятти»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Тольятти»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('565d91da-98be-4b22-b87b-a78406fcaf39', 'Филиал Московского международного университета информационных технологий «Академия ТОП Тула»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Тула»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('12c5c9c3-62ee-4210-9609-c9cdaa6c5b92', 'Филиал Московского международного университета информационных технологий «Академия ТОП Улан-Удэ»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Улан-Удэ»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('97bf7a4b-14ec-4b9b-9239-021b8a6799b3', 'Филиал Московского международного университета информационных технологий «Академия ТОП Ульяновск»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Ульяновск»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f4ba6363-b9ae-4693-80df-70d30f746d5d', 'Филиал Московского международного университета информационных технологий «Академия ТОП Уфа»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Уфа»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('506bcee2-ad08-45ab-a6ab-d4b24304a254', 'Филиал Московского международного университета информационных технологий «Академия ТОП Чебоксары»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Чебоксары»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5152e65b-3d8e-46df-aa31-5c8ed24c896f', 'Филиал Московского международного университета информационных технологий «Академия ТОП Челябинск»', 'Филиал Московского международного университета информационных технологий «Академия ТОП Челябинск»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6896de16-914c-4808-9186-e8137643f7f7', 'Стахановский инженерно-педагогический институт филиал Луганского государственного университета имени Владимира Даля', 'Стахановский инженерно-педагогический институт филиал Луганского государственного университета имени Владимира Даля', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ba49029e-2ca2-4da3-9742-934b9bb4d0fd', 'Антрацитовский институт геосистем и технологий филиал Луганского государственного университета имени Владимира Даля', 'Антрацитовский институт геосистем и технологий филиал Луганского государственного университета имени Владимира Даля', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('064fec89-e7df-4c10-8079-23a7973b5e81', 'Северодонецкий технологический институт филиал Луганского государственного университета имени Владимира Даля', 'Северодонецкий технологический институт филиал Луганского государственного университета имени Владимира Даля', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e0aa8d39-d1fe-4aa8-a763-f7d0ccb4a750', 'Азовский морской институт филиал Севастопольского государственного университета', 'Азовский морской институт филиал Севастопольского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f29ca5e1-1f6e-4a5c-bb31-2474fbf61eb3', 'Краснолучский филиал Донбасского государственного технического университета', 'Краснолучский филиал Донбасского государственного технического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('61ce5c42-992f-4c49-b0df-903ed0724d60', 'Донецкий институт управления филиал Российской академии народного хозяйства и государственной службы при Президенте Российской Федерации', 'Донецкий институт управления филиал Российской академии народного хозяйства и государственной службы при Президенте Российской Федерации', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('68693445-c0d1-4bbc-aee9-b8e9df28e252', 'Брянский филиал Первого Московского государственного медицинского университета имени И.М. Сеченова', 'Брянский филиал Первого Московского государственного медицинского университета имени И.М. Сеченова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f24d87a3-25bb-4fb0-bcad-9431d5749c7f', 'Владимирский филиал Приволжского исследовательского медицинского университета', 'Владимирский филиал Приволжского исследовательского медицинского университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f55b8d11-d1fd-4f34-9430-ac6159845fca', 'Вологодский филиал Ярославского государственного медицинского университета', 'Вологодский филиал Ярославского государственного медицинского университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('fde91051-efeb-4d7e-a857-6e42f7ab72ff', 'Донбасская национальная академия строительства и архитектуры филиал Национального исследовательского Московского государственного строительного университета', 'Донбасская национальная академия строительства и архитектуры филиал Национального исследовательского Московского государственного строительного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7072d33c-6ecd-4e7e-9956-09dd56d10609', 'Приазовский государственный технический университет филиал Национального исследовательского Московского государственного строительного университета', 'Приазовский государственный технический университет филиал Национального исследовательского Московского государственного строительного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('bd11444b-1ec3-4645-8da0-f7d56ac49f71', 'Государственная академия промышленного менеджмента имени Н. П. Пастухова филиал Национального исследовательского Томского государственного университета', 'Государственная академия промышленного менеджмента имени Н. П. Пастухова филиал Национального исследовательского Томского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9bd0bb59-3403-4d15-849c-e7387ea9276f', 'Филиал Ярославского государственного педагогического университета им. К.Д. Ушинского в городе Рыбинске', 'Филиал Ярославского государственного педагогического университета им. К.Д. Ушинского в городе Рыбинске', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('d8aa9f7a-42b7-4ef8-b5c7-3a822ebe26f0', 'г. Хабаровск, Хабаровский край', 'Россия', 'Хабаровский край', 'Хабаровск', 'г. Хабаровск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7ba25a9a-bcf7-4685-8d25-0ea0907efe08', 'Филиал Всероссийского государственного университета кинематографии имени С.А. Герасимова в г. Хабаровске', 'Филиал Всероссийского государственного университета кинематографии имени С.А. Герасимова в г. Хабаровске', 'd8aa9f7a-42b7-4ef8-b5c7-3a822ebe26f0', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d070d46e-5ad0-4dac-bd7d-675e54fdb6a5', 'Энергодарский филиал Мелитопольского государственного университета', 'Энергодарский филиал Мелитопольского государственного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f8dbdc5f-c13e-4489-8287-ed40f89fa120', 'Старобельский филиал Луганского государственного педагогического университета', 'Старобельский филиал Луганского государственного педагогического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('96c14f80-67c1-4462-bf94-a2f6efe1516a', 'Автомобильно-дорожный институт филиал Донецкий национальный технический университет в г. Горловка', 'Автомобильно-дорожный институт филиал Донецкий национальный технический университет в г. Горловка', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c31dd56a-95d0-43c4-bfa6-fa90a044aedc', 'Рязанский институт традиционного прикладного искусства филиал Российского университета традиционных художественных промыслов', 'Рязанский институт традиционного прикладного искусства филиал Российского университета традиционных художественных промыслов', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3cf29b74-e1ed-4877-bdd2-63b6c0962b7a', 'Сибирский институт традиционного прикладного искусства филиал Российского университета традиционных художественных промыслов', 'Сибирский институт традиционного прикладного искусства филиал Российского университета традиционных художественных промыслов', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('84f9c272-4602-43e7-a09d-892d5da97d69', 'Холуйский институт лаковой миниатюрной живописи имени Н.Н. Харламова филиал Российского университета традиционных художественных промыслов', 'Холуйский институт лаковой миниатюрной живописи имени Н.Н. Харламова филиал Российского университета традиционных художественных промыслов', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1ad94951-20cd-4d24-b846-4fbb4b8196c4', 'Филиал Института деловой карьеры в г. Орехово-Зуево', 'Филиал Института деловой карьеры в г. Орехово-Зуево', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('9adb25da-9605-464d-9767-9bd6378573b8', 'г. Грозный, Чечня', 'Россия', 'Чечня', 'Грозный', 'г. Грозный');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d204dc61-ff46-472d-b10e-d3766ce49bc8', 'Филиал Московского государственного университета имени М.В. Ломоносова в г. Грозном', 'Филиал Московского государственного университета имени М.В. Ломоносова в г. Грозном', '9adb25da-9605-464d-9767-9bd6378573b8', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('adf712ad-9a0d-4e5d-911d-3f04b7e34624', 'Филиал Московского государственного университета имени М.В. Ломоносова в г. Дубне', 'Филиал Московского государственного университета имени М.В. Ломоносова в г. Дубне', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('311c8f50-16f0-4b9d-aac2-ce809022331f', 'г. Благовещенск, Амурская область', 'Россия', 'Амурская область', 'Благовещенск', 'г. Благовещенск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('bd7efd92-fe36-4bbd-81b1-5cb74ce5ef23', 'Филиал Российского института театрального искусства — ГИТИС в г. Благовещенске', 'Филиал Российского института театрального искусства — ГИТИС в г. Благовещенске', '311c8f50-16f0-4b9d-aac2-ce809022331f', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('b4281a74-a917-4f3b-8502-eb01d30e7fd1', 'г. Рязань, Рязанская область', 'Россия', 'Рязанская область', 'Рязань', 'г. Рязань');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9f3ea918-e524-4ba9-95c4-a7909d773582', 'Филиал Московского государственного института культуры в г. Рязань', 'Филиал Московского государственного института культуры в г. Рязань', 'b4281a74-a917-4f3b-8502-eb01d30e7fd1', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('8a9dcdfb-f53a-4674-a857-240ff948b1ee', 'г. Южно-Сахалинск, Сахалинская область', 'Россия', 'Сахалинская область', 'Южно-Сахалинск', 'г. Южно-Сахалинск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a524e5ec-c67b-46f3-8438-e14c6c7b1b1b', 'Филиал «Театральный институт имени Бориса Щукина при Государственном академическом театре имени Евгения Вахтангова» в г. Южно-Сахалинске', 'Филиал «Театральный институт имени Бориса Щукина при Государственном академическом театре имени Евгения Вахтангова» в г. Южно-Сахалинске', '8a9dcdfb-f53a-4674-a857-240ff948b1ee', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('10e1e976-920d-43dd-bfe5-8d5b3d66b942', 'г. Севастополь, Севастополь', 'Россия', 'Севастополь', 'Севастополь', 'г. Севастополь');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d66288f0-ebb4-476c-b98e-6158637013f4', 'Институт экономики и права филиал «Академия труда и социальных отношений» в г. Севастополе', 'Институт экономики и права филиал «Академия труда и социальных отношений» в г. Севастополе', '10e1e976-920d-43dd-bfe5-8d5b3d66b942', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('31c0c875-02ba-4910-b7b0-28c6fb8ecab4', 'Липецкий филиал Российского университета медицины', 'Липецкий филиал Российского университета медицины', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5b1e91b7-1551-4bae-9ee2-b841b2411e92', 'Нижегородский институт путей сообщения филиал Приволжского государственного университета путей сообщения', 'Нижегородский институт путей сообщения филиал Приволжского государственного университета путей сообщения', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('404fab38-bf46-451b-a5fd-63e61629ead1', 'Когалымский филиал Пермского национального исследовательского политехнического университета', 'Когалымский филиал Пермского национального исследовательского политехнического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ff0405b0-4d77-4586-b70e-f530b4b720eb', 'Рыбинское ордена «Знак Почета» училище имени В.И. Калашникова филиал Волжского государственного университета водного транспорта', 'Рыбинское ордена «Знак Почета» училище имени В.И. Калашникова филиал Волжского государственного университета водного транспорта', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f726046f-9b04-4788-9aec-97300b18dff6', 'Уфимский филиал Волжского государственного университета водного транспорта', 'Уфимский филиал Волжского государственного университета водного транспорта', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2edc150b-8ce5-4612-945a-58287dec708c', 'Балтийский филиал «Центральная музыкальная школа – Академия исполнительного искусства»', 'Балтийский филиал «Центральная музыкальная школа – Академия исполнительного искусства»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1fc3a503-a615-46c0-be5f-d6e881ebb262', 'Приморский филиал «Центральная музыкальная школа – Академия исполнительного искусства»', 'Приморский филиал «Центральная музыкальная школа – Академия исполнительного искусства»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6a302b11-3031-412e-a8c8-3f6623aed68c', 'Сибирский филиал «Центральная музыкальная школа – Академия исполнительного искусства»', 'Сибирский филиал «Центральная музыкальная школа – Академия исполнительного искусства»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7bb2b320-7556-4dd9-92cd-6cf63d095aa2', 'Московский государственный университет леса (Мытищинский филиал МГТУ им. Н. Э. Баумана)', 'Московский государственный университет леса (Мытищинский филиал МГТУ им. Н. Э. Баумана)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f13833dc-4b87-460b-b43f-e7805ad42076', 'Московский финансово-юридический университет', 'Московский финансово-юридический университет', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('da02f223-4a79-4e3e-bfcc-c9a480245140', 'Технологический университет имени дважды Героя Советского Союза, летчика-космонавта А.А. Леонова', 'Технологический университет имени дважды Героя Советского Союза, летчика-космонавта А.А. Леонова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('bae7fd60-99cf-4f5d-8594-1106cfa3d3ee', 'Алтайский государственный педагогический университет Бийский филиал им. В. М. Шукшина', 'Алтайский государственный педагогический университет Бийский филиал им. В. М. Шукшина', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('d56b93a6-1550-47b6-9a7b-53036e280a78', 'г. Севастополь, Севастополь', 'Россия', 'Севастополь', 'Севастополь', 'г. Севастополь');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('425c4944-142f-4e82-af4a-0923a2e363f4', 'Филиал Московского государственного университета имени М.В. Ломоносова в городе Севастополе', 'Филиал Московского государственного университета имени М.В. Ломоносова в городе Севастополе', 'd56b93a6-1550-47b6-9a7b-53036e280a78', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b8ec444e-675a-4a00-bca6-bc2de3020238', 'Филиал Московского государственного университета имени М.В. Ломоносова в г. Баку', 'Филиал Московского государственного университета имени М.В. Ломоносова в г. Баку', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('cf642ce1-43f2-440f-aee5-05e4a16a5dc1', 'Оренбургский филиал Российского государственного университета нефти и газа им. И.М. Губкина', 'Оренбургский филиал Российского государственного университета нефти и газа им. И.М. Губкина', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ef9e7f92-50f9-46d1-a4cc-4c66e9e1c6b1', 'Филиал Московского государственного университета имени М.В. Ломоносова в г. Душанбе', 'Филиал Московского государственного университета имени М.В. Ломоносова в г. Душанбе', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('045bdf1c-37c9-48dc-b231-72c3ff8a9dce', 'Филиал Московского государственного университета имени М.В. Ломоносова в Казахстане', 'Филиал Московского государственного университета имени М.В. Ломоносова в Казахстане', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c59058a4-493d-4a46-bac0-58d9429abc20', 'Ахтубинский филиал «Взлет» Московского авиационного института (национального исследовательского университета)', 'Ахтубинский филиал «Взлет» Московского авиационного института (национального исследовательского университета)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c49b8f58-d9e5-45cd-be74-c8e97fa522a1', 'Байконурский филиал «Восход» Московского авиационного института (национального исследовательского университета)', 'Байконурский филиал «Восход» Московского авиационного института (национального исследовательского университета)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('67268781-946a-4960-97bd-e1784012bb64', 'Жуковский филиал «Стрела» Московского авиационного института (национального исследовательского университета)', 'Жуковский филиал «Стрела» Московского авиационного института (национального исследовательского университета)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c4186553-fbf8-4f49-92a4-bfa535b56bc0', 'Ташкентский филиал Российского государственного университета нефти и газа им. И.М. Губкина', 'Ташкентский филиал Российского государственного университета нефти и газа им. И.М. Губкина', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7d6981d8-1302-4326-8876-4dfc4015047e', 'Ступинский филиал Московского авиационного института (национального исследовательского университета)', 'Ступинский филиал Московского авиационного института (национального исследовательского университета)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2f0667a1-f8c5-41ae-9330-23275ce54bd5', 'Рязанский филиал Московского государственного университета культуры и искусств', 'Рязанский филиал Московского государственного университета культуры и искусств', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7966854a-05c3-4b32-8507-4adc608fffb2', 'Учебный центр «Интеграция» МАИ (национального исследовательского университета) в г. Серпухове при МОУ «Институте инженерной физики»', 'Учебный центр «Интеграция» МАИ (национального исследовательского университета) в г. Серпухове при МОУ «Институте инженерной физики»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7e7cc5f3-985e-472f-975c-d9504d1cc011', 'Филиал «Ракетно-космическая техника» Московского авиационного института (национального исследовательского университета)', 'Филиал «Ракетно-космическая техника» Московского авиационного института (национального исследовательского университета)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d81dff30-641e-4dd9-9c2e-dd33ada09f3a', 'Тульский филиал Московского государственного университета культуры и искусств', 'Тульский филиал Московского государственного университета культуры и искусств', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('acba9044-26d4-4741-a2f1-9aaf027e91fc', 'Махачкалинский филиал Российского государственного университета туризма и сервиса', 'Махачкалинский филиал Российского государственного университета туризма и сервиса', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3f5a14e3-4cbc-4860-8177-930ecbb76a21', 'Егорьевский технологический институт Московского государственного технологического университета «Станкин»', 'Егорьевский технологический институт Московского государственного технологического университета «Станкин»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('443c24b1-161a-47f0-916e-b13a2ba46ba5', 'Ереванский филиал Российского государственного университета туризма и сервиса', 'Ереванский филиал Российского государственного университета туризма и сервиса', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('6503144f-8147-4158-b68b-a8b94032bbea', 'г. Москва, Москва', 'Россия', 'Москва', 'Москва', 'г. Москва');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('90e32e70-4df0-4b7b-988b-6eec205737e5', 'Институт туризма и гостеприимства (г. Москва) (филиал) Российского государственного университета туризма и сервиса', 'Институт туризма и гостеприимства (г. Москва) (филиал) Российского государственного университета туризма и сервиса', '6503144f-8147-4158-b68b-a8b94032bbea', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9f73f7e4-3f2d-4a6b-b00d-0770bfd1565a', 'Мытищинский филиал Национального исследовательского Московского государственного строительного университета', 'Мытищинский филиал Национального исследовательского Московского государственного строительного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d80501d2-2116-40ab-ba19-4ec2d8e7f252', 'Самарский колледж строительства и предпринимательства Национального исследовательского Московского государственного строительного университета', 'Самарский колледж строительства и предпринимательства Национального исследовательского Московского государственного строительного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('66f1d4a1-fba8-46f3-976f-716fd1d0ebda', 'Ереванский филиал Российского экономического университета имени Г.В. Плеханова', 'Ереванский филиал Российского экономического университета имени Г.В. Плеханова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('aeeb8e70-bacd-4ef2-8ad1-b388ad338537', 'Национальный исследовательский университет «Высшая школа экономики» в Нижнем Новгороде', 'Национальный исследовательский университет «Высшая школа экономики» в Нижнем Новгороде', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('27729f67-b3f4-4158-8887-d13865dac72b', 'Кемеровский институт (филиал) Российского экономического университета имени Г.В. Плеханова', 'Кемеровский институт (филиал) Российского экономического университета имени Г.В. Плеханова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('744c88e1-011f-4c67-83da-9937d6afc43f', 'Минский филиал Российского экономического университета имени Г.В. Плеханова', 'Минский филиал Российского экономического университета имени Г.В. Плеханова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1c677e36-048f-4364-8879-20af35aac59c', 'Омский институт (филиал) Российского экономического университета имени Г.В. Плеханова', 'Омский институт (филиал) Российского экономического университета имени Г.В. Плеханова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('01dd79f3-08f8-4e2a-b104-80a2f313ee70', 'Пермский филиал Национального исследовательского университета «Высшая школа экономики»', 'Пермский филиал Национального исследовательского университета «Высшая школа экономики»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e830d725-4897-4882-9749-7ada74b88601', 'Национальный исследовательский университет «Высшая школа экономики» — Санкт-Петербург', 'Национальный исследовательский университет «Высшая школа экономики» — Санкт-Петербург', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7abb12e2-44e4-43c1-9aa2-ba0c76137353', 'Школа социальных наук НИУ ВШЭ в Санкт-Петербурге', 'Школа социальных наук НИУ ВШЭ в Санкт-Петербурге', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ecd8fd05-b513-4877-b370-a34082ea990b', 'Школа экономики и менеджмента НИУ ВШЭ в Санкт-Петербурге', 'Школа экономики и менеджмента НИУ ВШЭ в Санкт-Петербурге', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4d4e217a-c53d-4265-a985-21a86bd26c6e', 'Кисловодский филиал Московского государственного института индустрии туризма имени Ю.А.Сенкевича', 'Кисловодский филиал Московского государственного института индустрии туризма имени Ю.А.Сенкевича', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7656fff9-8b6c-4228-8079-50a7cf105718', 'Дальневосточный институт (филиал) Всероссийского государственного университета юстиции (РПА Минюста России)', 'Дальневосточный институт (филиал) Всероссийского государственного университета юстиции (РПА Минюста России)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('be40830c-8068-402e-a2a7-5e3bea425c8c', 'Ижевский институт (филиал) Всероссийского государственного университета юстиции (РПА Минюста России)', 'Ижевский институт (филиал) Всероссийского государственного университета юстиции (РПА Минюста России)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a121e417-565c-4456-9c9b-9200cbb463df', 'Иркутский институт (филиал) Всероссийского государственного университета юстиции (РПА Минюста России)', 'Иркутский институт (филиал) Всероссийского государственного университета юстиции (РПА Минюста России)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('db6f0acf-5b96-4eea-9623-c57843de99f3', 'Казанский институт (филиал) Всероссийского государственного университета юстиции (РПА Минюста России)', 'Казанский институт (филиал) Всероссийского государственного университета юстиции (РПА Минюста России)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('81b5e4a1-625b-4f6a-aa65-69e7d9fa8fdf', 'Калужский институт (филиал) Всероссийского государственного университета юстиции', 'Калужский институт (филиал) Всероссийского государственного университета юстиции', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c9ee9c41-1358-4e9c-8e0e-9c0b5531faa4', 'Поволжский институт (филиал) Всероссийского государственного университета юстиции', 'Поволжский институт (филиал) Всероссийского государственного университета юстиции', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ead06ca4-e7a2-4b97-b953-d818605f1030', 'Ростовский институт (филиал) Всероссийского государственного университета юстиции (РПА Минюста России)', 'Ростовский институт (филиал) Всероссийского государственного университета юстиции (РПА Минюста России)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('434775a9-b3c1-4d06-9dff-954f915f17d0', 'Санкт-Петербургский институт (филиал) Всероссийского государственного университета юстиции (РПА Минюста России)', 'Санкт-Петербургский институт (филиал) Всероссийского государственного университета юстиции (РПА Минюста России)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d3f1c40f-aad7-4567-81b4-2685b6d541b9', 'Северный институт (филиал) Всероссийского государственного университета юстиции', 'Северный институт (филиал) Всероссийского государственного университета юстиции', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('30fc0cf1-bd21-4f36-8d2e-02c8fa6eca68', 'Северо-Кавказский институт (филиал) Всероссийского государственного университета юстиции', 'Северо-Кавказский институт (филиал) Всероссийского государственного университета юстиции', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('32770383-7521-4616-95c6-4197d12b9587', 'Средне-Волжский институт (филиал) Всероссийского государственного университета юстиции (РПА Минюста России)', 'Средне-Волжский институт (филиал) Всероссийского государственного университета юстиции (РПА Минюста России)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e3b1a2dc-6023-4437-a894-a49cd5fb7709', 'Тульский институт (филиал) Всероссийского государственного университета юстиции (РПА Минюста России)', 'Тульский институт (филиал) Всероссийского государственного университета юстиции (РПА Минюста России)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1afc3708-531d-4b88-bfd2-3b7c7f15ef30', 'Волжский филиал Национального исследовательского университета «МЭИ»', 'Волжский филиал Национального исследовательского университета «МЭИ»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('97d95709-15da-4cbd-8157-1216dfab4727', 'Смоленский филиал Национального исследовательского университета «МЭИ»', 'Смоленский филиал Национального исследовательского университета «МЭИ»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6c2e2ca3-fd86-4126-81a3-6727a42a1d94', 'Старооскольский технологический институт им. А.А. Угарова (филиал) Национального исследовательского технологического университета «МИСИС»', 'Старооскольский технологический институт им. А.А. Угарова (филиал) Национального исследовательского технологического университета «МИСИС»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0e5ecdd6-6c79-4a31-91c2-77a2962143a7', 'Брянский филиал Московского государственного университета путей сообщения Императора Николая II', 'Брянский филиал Московского государственного университета путей сообщения Императора Николая II', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('489ab03c-8ad0-4539-91ea-130ff39e8f7c', 'Волгоградский филиал Московского государственного университета путей сообщения Императора Николая II', 'Волгоградский филиал Московского государственного университета путей сообщения Императора Николая II', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('739e6701-7608-4685-8436-6093252fe78c', 'Елецкий филиал Московского государственного университета путей сообщения Императора Николая II', 'Елецкий филиал Московского государственного университета путей сообщения Императора Николая II', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a2cd3f26-82a7-42a5-a065-285b19d3af90', 'Ижевский филиал Московского государственного университета путей сообщения Императора Николая II', 'Ижевский филиал Московского государственного университета путей сообщения Императора Николая II', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d5ce9a8a-edf0-43d0-8074-1b0867738a5a', 'Казанский филиал Московского государственного университета путей сообщения Императора Николая II', 'Казанский филиал Московского государственного университета путей сообщения Императора Николая II', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e96528f9-1d18-482a-aa11-8d1bd9c049b6', 'Калининградский филиал Московского государственного университета путей сообщения Императора Николая II', 'Калининградский филиал Московского государственного университета путей сообщения Императора Николая II', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0ad3272b-f1c0-4e6c-a971-e027c68a8dd3', 'Московский областной филиал Московского университета Министерства внутренних дел Российской Федерации им. В.Я. Кикотя', 'Московский областной филиал Московского университета Министерства внутренних дел Российской Федерации им. В.Я. Кикотя', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ebb35526-4054-4825-923c-bdf429b18b84', 'Рязанский филиал Московского университета Министерства внутренних дел Российской Федерации имени В.Я. Кикотя', 'Рязанский филиал Московского университета Министерства внутренних дел Российской Федерации имени В.Я. Кикотя', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f85f3d19-c975-4697-98d8-e3612e7f3492', 'Пятигорский филиал Российского экономического университета имени Г.В. Плеханова', 'Пятигорский филиал Российского экономического университета имени Г.В. Плеханова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6eb10a0d-6d7e-4966-b9d5-177812c78a16', 'Саратовский социально-экономический институт (филиал) Российского экономического университета имени Г.В. Плеханова', 'Саратовский социально-экономический институт (филиал) Российского экономического университета имени Г.В. Плеханова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8905b055-5c87-4e8b-b30d-4f46a934b833', 'Уральский филиал Российской академии живописи, ваяния и зодчества Ильи Глазунова', 'Уральский филиал Российской академии живописи, ваяния и зодчества Ильи Глазунова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2190e38c-aabe-441a-bb8d-1523ec64ca7d', 'Смоленский филиал Российского экономического университета имени Г.В. Плеханова', 'Смоленский филиал Российского экономического университета имени Г.В. Плеханова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5adda983-9b30-426a-9a68-4e9ab33546c2', 'Ташкентский филиал Российского экономического университета имени Г.В. Плеханова', 'Ташкентский филиал Российского экономического университета имени Г.В. Плеханова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('eac5ac90-3383-4d15-a935-f7ea6fcfcc8b', 'Улан-Баторский филиал Российского экономического университета имени Г.В. Плеханова', 'Улан-Баторский филиал Российского экономического университета имени Г.В. Плеханова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('915bbd21-a5f4-49d1-8ce4-ae17520323ec', 'Усть-Каменогорский филиал Российского экономического университета имени Г.В. Плеханова', 'Усть-Каменогорский филиал Российского экономического университета имени Г.В. Плеханова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('52bd9bdc-0f86-47bf-8775-8e5a09b7cb1a', 'Ярославский филиал Российского экономического университета имени Г.В. Плеханова', 'Ярославский филиал Российского экономического университета имени Г.В. Плеханова', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8c35e9e0-ad0c-4bfa-abec-67203221d5c0', 'Алтайский филиал РМАТ', 'Алтайский филиал РМАТ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('531d7384-eded-4dcf-992c-cf18b5c116c1', 'Армянский инстиут туризма (филиал РМАТ)', 'Армянский инстиут туризма (филиал РМАТ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0f774a6f-691e-4f70-a3c8-299c0d098164', 'Калужский филиал Московского государственного университета путей сообщения Императора Николая II', 'Калужский филиал Московского государственного университета путей сообщения Императора Николая II', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8e0b1b02-36c1-4d55-8ef5-d2c275cc8609', 'Воронежский филиал Ростовского государственного университета путей сообщения', 'Воронежский филиал Ростовского государственного университета путей сообщения', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('44ebe0e4-b75f-459a-bb2a-f13781ea7fc6', 'Владимирский филиал РМАТ', 'Владимирский филиал РМАТ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7d924806-883d-4adc-b1b5-b6cc3c2ef469', 'Волоколамский институт гостеприимства (филиал РМАТ)', 'Волоколамский институт гостеприимства (филиал РМАТ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('671e9c9a-7ad2-4b19-bfff-a7e8228652d1', 'Алтайский филиал Финансового университета при Правительстве Российской Федерации', 'Алтайский филиал Финансового университета при Правительстве Российской Федерации', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('cab62126-3a4f-4635-8a59-544fb0c7d6cf', 'Брянский филиал Финансового университета при Правительстве Российской Федерации', 'Брянский филиал Финансового университета при Правительстве Российской Федерации', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('57e35633-a977-4139-8d41-99337e1e9ec4', 'Муромский филиал Московского государственного университета путей сообщения Императора Николая II', 'Муромский филиал Московского государственного университета путей сообщения Императора Николая II', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ca2a167f-136d-40c6-84d5-dc482037dc98', 'Орловский филиал Московского государственного университета путей сообщения Императора Николая II', 'Орловский филиал Московского государственного университета путей сообщения Императора Николая II', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('188f1d5f-88fc-4230-b382-b348fa503069', 'Поволжский филиал Московского государственного университета путей сообщения Императора Николая II', 'Поволжский филиал Московского государственного университета путей сообщения Императора Николая II', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('51ffb7d5-055c-4190-b7a6-640a7567525d', 'Ртищевский филиал Московского государственного университета путей сообщения Императора Николая II', 'Ртищевский филиал Московского государственного университета путей сообщения Императора Николая II', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f0601761-46e4-4a3b-a64d-9fe1cc05b380', 'Рязанский филиал Московского государственного университета путей сообщения Императора Николая II', 'Рязанский филиал Московского государственного университета путей сообщения Императора Николая II', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f9516fcb-aad2-48b8-80f0-b518e4f0b780', 'Смоленский филиал Московского государственного университета путей сообщения Императора Николая II', 'Смоленский филиал Московского государственного университета путей сообщения Императора Николая II', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7e74b91d-c7f0-4b93-b6b6-27543ceefaa8', 'Ухтинский филиал Московского государственного университета путей сообщения Императора Николая II', 'Ухтинский филиал Московского государственного университета путей сообщения Императора Николая II', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d3adf844-d26b-4aa3-b901-e5663f9931f1', 'Ярославский филиал Московского государственного университета путей сообщения Императора Николая II', 'Ярославский филиал Московского государственного университета путей сообщения Императора Николая II', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8dd1e240-58de-4162-968c-2092d374245e', 'Воскресенский институт туризма (филиал РМАТ)', 'Воскресенский институт туризма (филиал РМАТ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('80d6c043-bebc-42fb-b291-837df5225f1a', 'Калужский филиал Российского государственного аграрного университета - МСХА им. К.А. Тимирязева', 'Калужский филиал Российского государственного аграрного университета - МСХА им. К.А. Тимирязева', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a284fdc9-4a1f-4d72-a6f6-c450da651d53', 'Вяземский филиал РМАТ', 'Вяземский филиал РМАТ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e1aaf594-6012-4d9d-9e2a-0f889df568f1', 'Западно-Подмосковный институт туризма (филиал РМАТ)', 'Западно-Подмосковный институт туризма (филиал РМАТ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f9c4943e-f20b-4a05-b9a0-0e606e25568e', 'Казанский филиал Российской международной академии туризма', 'Казанский филиал Российской международной академии туризма', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d1a113dc-8b2e-4c9f-8b29-b2d5a5a1476e', 'Калининградский институт туризма (филиал РМАТ)', 'Калининградский институт туризма (филиал РМАТ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9a08e9fd-9fd9-41f9-b96a-b2b10e4d8ffc', 'Калужский институт туристского бизнеса (филиал РМАТ)', 'Калужский институт туристского бизнеса (филиал РМАТ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('760d9cae-0da7-42b5-b94f-e3acbfe00099', 'Карельский институт туризма (филиал) Российской международной академии туризма', 'Карельский институт туризма (филиал) Российской международной академии туризма', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3ee4ab1a-fa0b-45dd-a34f-4d51bef79e1a', 'Азовский филиал Российского государственного социального университета', 'Азовский филиал Российского государственного социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1d75eb18-2f10-4177-958a-32e3e178e3f1', 'Филиал Российского государственного социального университета в г. Анапе Краснодарского края', 'Филиал Российского государственного социального университета в г. Анапе Краснодарского края', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8b5af7c6-c1b6-4a85-8803-609907bd16e5', 'Московский филиал РМАТ (Измайлово)', 'Московский филиал РМАТ (Измайлово)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('54bcfe9f-3b31-4fd9-87df-f6c54bd02a9a', 'Псковский филиал Российской международной академии туризма', 'Псковский филиал Российской международной академии туризма', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('34e94270-3861-41f8-9a82-f95cdfe029ac', 'Сочинский институт курортной рекреации и гостеприимства (филиал) Российской международной академии туризма', 'Сочинский институт курортной рекреации и гостеприимства (филиал) Российской международной академии туризма', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e0947a77-d8a9-4f3b-a07d-56f633ee3591', 'Тульский филиал Российской международной академии туризма', 'Тульский филиал Российской международной академии туризма', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0429a506-3928-49c7-8e10-0cc77adc04c9', 'Батайский филиал Российского государственного социального университета', 'Батайский филиал Российского государственного социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('78747948-920d-495b-b907-653437417f0d', 'Брянский филиал Российского государственного социального университета', 'Брянский филиал Российского государственного социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('58425534-63f0-499d-b29d-f4112d5c5a90', 'Воронежский филиал Российского государственного социального университета', 'Воронежский филиал Российского государственного социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('7d0aad1c-3770-47f8-bc8d-9bbfd8f1d1cc', 'г. Саратов, Саратовская область', 'Россия', 'Саратовская область', 'Саратов', 'г. Саратов');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c43cc48c-a585-4fb9-80fe-41743f3b28e6', 'Институт социального образования (филиал) в г. Саратов Российского государственного социального университета', 'Институт социального образования (филиал) в г. Саратов Российского государственного социального университета', '7d0aad1c-3770-47f8-bc8d-9bbfd8f1d1cc', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e5a0178b-ff03-4949-aa9f-747a484df094', 'Каменск-Шахтинский филиал Российского государственного социального университета', 'Каменск-Шахтинский филиал Российского государственного социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1e36892a-103f-46f1-9379-489dd6bd4f0c', 'Карачаево-Черкесский филиал Российского государственного социального университета', 'Карачаево-Черкесский филиал Российского государственного социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ab6c4e57-c848-431b-b61e-95ffcad78f5e', 'Кисловодский филиал Российского государственного социального университета', 'Кисловодский филиал Российского государственного социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1ece31c4-0aea-4b16-b998-1709411a3178', 'Обнинский филиал Государственного университета управления', 'Обнинский филиал Государственного университета управления', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6454f626-e708-45fb-b8a2-18743e4f7fc4', 'Домодедовский филиал Российского нового университета', 'Домодедовский филиал Российского нового университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('09de0ebe-fafd-490e-a6e6-c21cd0712d96', 'Елецкий филиал Российского нового университета', 'Елецкий филиал Российского нового университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('105def01-580c-4adf-a3e8-1305f8510eb8', 'Ступинский филиал Российского нового университета', 'Ступинский филиал Российского нового университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9e6e67f1-4dd7-400b-a9e2-a86ac54ff869', 'Таганрогский филиал Российского нового университета', 'Таганрогский филиал Российского нового университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('45299309-ce47-424d-8ee7-40033f6f55eb', 'Тамбовский филиал Российского нового университета', 'Тамбовский филиал Российского нового университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('fef50fb8-19c4-4aa9-b0c8-7c0c51da73f0', 'Филиал Российского государственного социального университета в г. Клину Московской области', 'Филиал Российского государственного социального университета в г. Клину Московской области', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c4b24ba1-ae8a-4af3-a0fd-4f987ae2f3af', 'Курский институт социального образования (филиал) Российского государственного социального университета', 'Курский институт социального образования (филиал) Российского государственного социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('39c5d234-90d8-432e-bfb6-eb7d213c65c9', 'Люберецкий филиал Российского государственного социального университета', 'Люберецкий филиал Российского государственного социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('45f72e18-6c49-44fc-81b1-7dc9a1f998ba', 'Майкопский филиал Российского государственного социального университета', 'Майкопский филиал Российского государственного социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('62062470-1539-4abc-ab1f-f25232d663fc', 'Калмыцкий филиал Московской академии экономики и права', 'Калмыцкий филиал Московской академии экономики и права', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8f47804e-443f-4674-b6f0-57314838d7b2', 'Моздокский филиал Московской академии экономики и права', 'Моздокский филиал Московской академии экономики и права', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('87cd67ec-10e1-4b01-8fc7-d7c86f48c351', 'Московская академия экономики и права Воронежский филиал', 'Московская академия экономики и права Воронежский филиал', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a316dbe7-f636-4354-ab38-58ac5354e3c3', 'Рязанский филиал Московской академии экономики и права', 'Рязанский филиал Московской академии экономики и права', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('df8a548c-e21b-4bf2-b19a-fc2c7e9a3cbc', 'Смоленский филиал Московской академии экономики и права', 'Смоленский филиал Московской академии экономики и права', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c601c6c8-9e47-45ca-ad9a-f2f0fce7283b', 'Тираспольский филиал Московской академии экономики и права', 'Тираспольский филиал Московской академии экономики и права', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('35160fac-f8eb-4752-8b15-780da0b43e7b', 'Железногорский филиал Сибирского федерального университета', 'Железногорский филиал Сибирского федерального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('51ab237f-e4ca-4c9c-baed-cbf78e931581', 'Лесосибирский педагогический институт Сибирского федерального университета', 'Лесосибирский педагогический институт Сибирского федерального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('31e53916-adbe-407b-86e0-8899eb9dd22a', 'Мурманский филиал Российского государственного социального университета', 'Мурманский филиал Российского государственного социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('68616844-644d-43da-a36f-98ab9b77c709', 'Обнинский филиал Российского государственного социального университета', 'Обнинский филиал Российского государственного социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('fb01177f-619e-404b-a147-f4f1d81188c6', 'Филиал Российского государственного социального университета в г. Павловском Посаде Московской области', 'Филиал Российского государственного социального университета в г. Павловском Посаде Московской области', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('073c1270-3ed5-458d-8626-3ef242c478f6', 'г. Пятигорск, Ставропольский край', 'Россия', 'Ставропольский край', 'Пятигорск', 'г. Пятигорск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('02378f5c-37d8-43c3-818c-ab1b1b787b2c', 'Филиал Российского государственного социального университета в г. Пятигорске', 'Филиал Российского государственного социального университета в г. Пятигорске', '073c1270-3ed5-458d-8626-3ef242c478f6', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a4074f80-8461-4f71-8ddb-69897f5f3a73', 'Советский филиал Российского государственного социального университета', 'Советский филиал Российского государственного социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('bb6b9500-830a-4370-8d83-b050547c6809', 'г. Сочи, Краснодарский край', 'Россия', 'Краснодарский край', 'Сочи', 'г. Сочи');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('80276c8f-86ce-4423-9048-bc420e285804', 'Филиал Российского государственного социального университета в г. Сочи Краснодарского края', 'Филиал Российского государственного социального университета в г. Сочи Краснодарского края', 'bb6b9500-830a-4370-8d83-b050547c6809', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f7e9170a-ca35-4689-9f27-fb76f4686a12', 'Ставропольский филиал Российского государственного социального университета', 'Ставропольский филиал Российского государственного социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5e4cc112-2c23-4270-92fc-1589132383d3', 'Новомосковский институт (филиал) Российского химико-технологического университета имени Д.И. Менделеева', 'Новомосковский институт (филиал) Российского химико-технологического университета имени Д.И. Менделеева', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e60ff907-b293-4ca8-892a-4303d05faaba', 'Воскресенский филиал Российского государственного гуманитарного университета', 'Воскресенский филиал Российского государственного гуманитарного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('fb25cc1e-1c08-4302-99fc-cd4ab025697d', 'Дмитровский филиал Российского государственного гуманитарного университета', 'Дмитровский филиал Российского государственного гуманитарного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('02750f7d-f911-4ba2-8ad5-93af8bf0d6b0', 'г. Великие Луки, Псковская область', 'Россия', 'Псковская область', 'Великие Луки', 'г. Великие Луки');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1360414f-cde3-4997-9eee-e2e0e9145a3d', 'Филиал в г. Великий Новгород Российского государственного гуманитарного университета', 'Филиал в г. Великий Новгород Российского государственного гуманитарного университета', '02750f7d-f911-4ba2-8ad5-93af8bf0d6b0', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('aada4c81-16a3-4068-9b96-9f93c4db9cf0', 'г. Калуга, Калужская область', 'Россия', 'Калужская область', 'Калуга', 'г. Калуга');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('28a88854-ca69-4d8a-8395-d0dde3a58e51', 'Филиал в г. Калуге Российского государственного гуманитарного университета', 'Филиал в г. Калуге Российского государственного гуманитарного университета', 'aada4c81-16a3-4068-9b96-9f93c4db9cf0', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('088c325e-1192-462f-89df-20591069c33d', 'г. Тверь, Тверская область', 'Россия', 'Тверская область', 'Тверь', 'г. Тверь');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5c6c6b98-7d9e-4609-96b8-efbb2943ee3c', 'Филиал в г. Тверь Российского государственного гуманитарного университета', 'Филиал в г. Тверь Российского государственного гуманитарного университета', '088c325e-1192-462f-89df-20591069c33d', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('f44563db-7a51-48fe-b192-59c0003ee8f2', 'г. Тольятти, Самарская область', 'Россия', 'Самарская область', 'Тольятти', 'г. Тольятти');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c06be8d8-c625-44bf-ad68-797db1672367', 'Филиал в г. Тольятти Российского государственного гуманитарного университета', 'Филиал в г. Тольятти Российского государственного гуманитарного университета', 'f44563db-7a51-48fe-b192-59c0003ee8f2', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('126b2fe6-c8cb-47dd-906b-07660eca5fd5', 'Электростальский филиал Российского государственного гуманитарного университета', 'Электростальский филиал Российского государственного гуманитарного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('5a233611-cbab-4841-8718-c0933ecf59b6', 'г. Иркутск, Иркутская область', 'Россия', 'Иркутская область', 'Иркутск', 'г. Иркутск');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e8ac87aa-1956-4cc2-a8ea-6be9f377b9da', 'Филиал «Российский университет спорта «ГЦОЛИФК» в г. Иркутске', 'Филиал «Российский университет спорта «ГЦОЛИФК» в г. Иркутске', '5a233611-cbab-4841-8718-c0933ecf59b6', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0ac49c60-1d42-4386-9875-96badd7b4b66', 'Новочебоксарский филиал Российского государственного университета физической культуры, спорта, молодежи и туризма', 'Новочебоксарский филиал Российского государственного университета физической культуры, спорта, молодежи и туризма', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('79740475-41c4-4aca-82ad-d92181122a3a', 'Дальневосточный филиал Всероссийской академии внешней торговли Министерства экономического развития России', 'Дальневосточный филиал Всероссийской академии внешней торговли Министерства экономического развития России', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4c6f2dd9-17d7-412a-8e6c-14b80618fd55', 'Самарский филиал Московского городского педагогического университета', 'Самарский филиал Московского городского педагогического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('894a0bba-d028-46a4-979f-d90fe21071e3', 'Белгородский филиал Российского университета дружбы народов', 'Белгородский филиал Российского университета дружбы народов', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2f380659-1a84-490b-8769-7efaed7a9fc0', 'Ессентукский филиал Российского университета дружбы народов', 'Ессентукский филиал Российского университета дружбы народов', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ce209e62-d59d-4f13-aa5f-cbc58d6b8d77', 'Сочинский филиал Российского университета дружбы народов', 'Сочинский филиал Российского университета дружбы народов', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('18070e58-038f-4f94-a394-fce3fca17454', 'Алтайский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Алтайский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9daccae5-9c8c-4a29-bff3-becd583df0df', 'Астраханский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Астраханский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5d659ee3-dd0d-4b0d-8d0c-8d5d8049d98c', 'Балаковский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Балаковский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3ad867b7-1fa9-464c-bc67-59e66f286b1c', 'Брянский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Брянский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3a11752d-8f9e-4cea-b98c-f0481f3b6808', 'Владимирский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Владимирский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8872470f-5380-4bbf-887a-11c4a4ae63c4', 'Волгоградский институт управления (филиал Российской академии народного хозяйства и государственной службы при Президенте Российской Федерации)', 'Волгоградский институт управления (филиал Российской академии народного хозяйства и государственной службы при Президенте Российской Федерации)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('33379cd2-453d-4fa9-8201-1280246b8959', 'Вологодский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Вологодский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('352634df-2218-4279-9b2b-74e4eb47e337', 'Воронежский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Воронежский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('23a7533a-9ddf-4222-aacc-89fe5240659d', 'Выборгский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Выборгский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d241fd86-8750-4730-a5ab-7bc9935cb845', 'Дальневосточный институт управления - филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Дальневосточный институт управления - филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('29ac7876-168d-4de4-ba9a-64a0f0121db2', 'Дзержинский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Дзержинский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('006d640d-d699-47aa-9e30-7796a8fb2ea8', 'Западный филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Западный филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('006dd2f2-c067-472a-9a36-f045ecf486be', 'Ивановский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Ивановский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('af8320f7-d2bf-4c9a-b918-3069aad2455f', 'Ижевский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Ижевский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('08ca6878-a03f-4f18-960b-7a056ebef8c3', 'Калужский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Калужский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2b4cc4d3-70c9-4f8d-89ee-11cfadf3dfb5', 'Карельский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Карельский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('15dae166-10e0-445b-ab9f-af7b4d824a86', 'Кировский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Кировский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('277ef2d4-6967-4e79-815a-53a8a0a27974', 'Московский областной филиал Российской академии народного хозяйства и государственной службы при Президенте РФ (г. Красногорск)', 'Московский областной филиал Российской академии народного хозяйства и государственной службы при Президенте РФ (г. Красногорск)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c46f171e-bb5a-4f5f-b172-fe616189c27e', 'Курганский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Курганский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('fe59aa53-5d7c-4b35-be66-12caadfcf4de', 'Липецкий филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Липецкий филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f046a4c9-4dd9-4ab7-a660-cc0e8b5f9d95', 'Мурманский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Мурманский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e205048f-afa6-4fe6-a921-7b7a69b61db8', 'Нижегородский институт управления – филиал Российской академии народного хозяйства и государственной службы при Президенте Российской Федерации', 'Нижегородский институт управления – филиал Российской академии народного хозяйства и государственной службы при Президенте Российской Федерации', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e2a6641e-d83a-4bcb-8121-b1d56e1b0596', 'Новгородский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Новгородский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3032f789-95b8-41bb-befc-343b72a7088a', 'Оренбургский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Оренбургский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('812d9a01-5856-41a5-8afe-b8e786561bbd', 'Среднерусский институт управления - филиал Российской академии народного хозяйства и государственной службы при Президенте Российской Федерации', 'Среднерусский институт управления - филиал Российской академии народного хозяйства и государственной службы при Президенте Российской Федерации', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7289dd6f-c0d2-4698-b09f-dcc5f8ba2bdf', 'Томский филиал Российского государственного социального университета', 'Томский филиал Российского государственного социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('dccd16ad-bbc3-45fc-b91c-54239d265df0', 'Филиал в г. Дедовске Российского государственного социального университета', 'Филиал в г. Дедовске Российского государственного социального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('20f686b8-114f-43e3-8dc7-4cb075b34df8', 'Пермский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Пермский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c704a231-8bc0-4afa-a0c0-ea2ec107abac', 'Петропавловск-Камчатский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Петропавловск-Камчатский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4cf37bd4-a4fc-43df-910b-1b2ed73ccce9', 'Поволжский институт управления имени П.А. Столыпина Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Поволжский институт управления имени П.А. Столыпина Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('829e08ce-cfe0-4e15-9f18-4f1c88f4116e', 'Саранский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Саранский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4d60a7fa-2682-4f1f-b564-652475f2ef18', 'Северо-Западный институт управления филиал РАНХиГС Санкт-Петербург', 'Северо-Западный институт управления филиал РАНХиГС Санкт-Петербург', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9011e49d-2f98-4574-b27f-7164b5c7571e', 'Северо-Кавказский институт – филиал РАНХиГС', 'Северо-Кавказский институт – филиал РАНХиГС', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('43cf9811-cf40-4881-aad1-944e2eb7c139', 'Сибирский институт управления Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Сибирский институт управления Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('62becffc-7358-43fe-8cc6-4ee529d0c88f', 'Сочинский филиал Московского автомобильно-дорожного государственного технического университета', 'Сочинский филиал Московского автомобильно-дорожного государственного технического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d5a220fa-cf0b-46bb-b9f6-a67f86281313', 'Серпуховский филиал Московского технологического университета (МИРЭА, МГУПИ,МИТХТ)', 'Серпуховский филиал Московского технологического университета (МИРЭА, МГУПИ,МИТХТ)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('91f50b26-9962-4ebe-91f2-ed6d3ccafcfa', 'Фрязинский филиал Российского технологического университета МИРЭА', 'Фрязинский филиал Российского технологического университета МИРЭА', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7871193c-e177-4a39-a5cb-5ee60a67ed65', 'Иркутский филиал Московского государственного технического университета гражданской авиации', 'Иркутский филиал Московского государственного технического университета гражданской авиации', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('19211854-832b-4840-a2c7-a423b6dd6533', 'Ростовский филиал Московского государственного технического университета гражданской авиации', 'Ростовский филиал Московского государственного технического университета гражданской авиации', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('38f85fbc-0f9f-4dac-8b38-3df5e7e9ed51', 'г. Владивосток, Приморский край', 'Россия', 'Приморский край', 'Владивосток', 'г. Владивосток');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('928dc1f2-e2af-47be-8299-d10471ab4c19', 'ЦДД в г. Владимир Московского финансово-юридического университета МФЮА', 'ЦДД в г. Владимир Московского финансово-юридического университета МФЮА', '38f85fbc-0f9f-4dac-8b38-3df5e7e9ed51', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8d64732d-e970-4952-80e5-b6c7caddc436', 'Волгоградский филиал Московского финансово-юридического университета МФЮА', 'Волгоградский филиал Московского финансово-юридического университета МФЮА', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f06a795b-2fa2-4693-9dd5-39e63adea7e9', 'Калужский филиал Московского финансово-юридического университета МФЮА', 'Калужский филиал Московского финансово-юридического университета МФЮА', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6e0fff64-ba3f-42d1-9bb5-064b717711e1', 'Смоленский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Смоленский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3511ba32-aaa0-4b95-accd-79ae2a67445b', 'Ставропольский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Ставропольский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9810b340-d36d-4359-afc4-f69a72b76fe3', 'Тамбовский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Тамбовский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('75b9c75d-483a-43c9-8f88-03b7afa6c75c', 'Тверской филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Тверской филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6e505b08-9f16-4086-ba70-ed0b84e2657f', 'Томский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Томский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('90dc548c-c9be-41f0-80b6-60f3b8674e66', 'Тульский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Тульский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('96e7a427-1541-4247-a707-9ae9d55f7ae4', 'Тюменский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Тюменский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7f1848e0-d441-465f-884f-baa9dff560df', 'Ульяновский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Ульяновский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1e6d8417-f13f-4ddd-a28d-2e3733f3c7c1', 'Уральский институт управления Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Уральский институт управления Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('eb882b82-e156-4bfc-9149-24c04f185992', 'Чебоксарский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Чебоксарский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4e3b5cee-d1d6-43f2-a3c8-87e2c7101425', 'Челябинский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Челябинский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('784af650-a0ba-4394-97af-386ef3855dfb', 'Читинский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Читинский филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('83901c0a-a3d1-436d-a54f-3c948be9d19e', 'Южно-Российский институт управления - филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', 'Южно-Российский институт управления - филиал Российской академии народного хозяйства и государственной службы при Президенте РФ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2221b2e8-baf6-4305-9812-fcba86e3d180', 'Кировский филиал Московского финансово-юридического университета МФЮА', 'Кировский филиал Московского финансово-юридического университета МФЮА', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1f5c0443-f987-4fb9-b556-61a51a728d21', 'Московский областной филиал в г. Сергиев Посад Московского финансово-юридического университета МФЮА', 'Московский областной филиал в г. Сергиев Посад Московского финансово-юридического университета МФЮА', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('30067265-43b0-4385-8ff9-5b953bbd882a', 'Орский филиал Московского финансово-юридического университета МФЮА', 'Орский филиал Московского финансово-юридического университета МФЮА', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('423b77a0-2688-4bb3-a641-68ac205d03b2', 'Алтайский институт труда и права (филиал) Академии труда и социальных отношений', 'Алтайский институт труда и права (филиал) Академии труда и социальных отношений', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('730f7b19-eb8e-42e6-941c-b9787e66c20c', 'Бурятский филиал Академии труда и социальных отношений', 'Бурятский филиал Академии труда и социальных отношений', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b949e348-1c45-4eb4-b5be-62c653aa3400', 'Екатеринбургский филиал Академии труда и социальных отношений', 'Екатеринбургский филиал Академии труда и социальных отношений', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8261d83d-aa03-4ed1-96f1-887ae00f15ff', 'Институт экономики и права (филиал) Академии труда и социальных отношений', 'Институт экономики и права (филиал) Академии труда и социальных отношений', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ecd7dd43-f20a-4bc7-8568-ff12ebd92e21', 'Кубанский институт социоэкономики и права Академии труда и социальных отношений', 'Кубанский институт социоэкономики и права Академии труда и социальных отношений', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('49e386ad-ff13-49d2-82a8-20dce63551b0', 'Нижегородский филиал Академия труда и социальных отношений', 'Нижегородский филиал Академия труда и социальных отношений', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8984fcd6-3741-47c2-bff3-c4f7cb7e9d58', 'Ярославский филиал Академии труда и социальных отношений', 'Ярославский филиал Академии труда и социальных отношений', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2121a3b1-0fe0-4282-96c0-11ffbbf8ea16', 'Башкирский кооперативный институт (филиал) Российского университета кооперации', 'Башкирский кооперативный институт (филиал) Российского университета кооперации', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('73410259-70f7-4ba3-add3-7532ea13fc5e', 'Брянский филиал Российского Университета Кооперации', 'Брянский филиал Российского Университета Кооперации', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('71fcb636-1fee-4183-857b-579c93956812', 'Ивановский филиал Российского университета кооперации', 'Ивановский филиал Российского университета кооперации', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('11adb1e0-b0a5-419e-87d3-56f546019515', 'Казанский кооперативный институт Российского университета кооперации', 'Казанский кооперативный институт Российского университета кооперации', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('14e34b9b-91cb-4554-82d1-e170c8a8390d', 'Камчатский филиал Российского университета кооперации', 'Камчатский филиал Российского университета кооперации', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('87d22d8e-e856-4569-ba52-3f7a5cc4fb61', 'Мичуринский филиал Российского университета кооперации', 'Мичуринский филиал Российского университета кооперации', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0f9baa13-ff9a-4ddd-afd7-e517aa7df9b6', 'Новгородский филиал Российского университета кооперации', 'Новгородский филиал Российского университета кооперации', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('8566949e-1e8a-4e86-8101-ebc583bd0f4b', 'Поволжский кооперативный институт Российского университета кооперации', 'Поволжский кооперативный институт Российского университета кооперации', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d4037e3e-907c-4d90-b49e-7dfd2c997988', 'Смоленский кооперативный институт (филиал) Российского университета кооперации', 'Смоленский кооперативный институт (филиал) Российского университета кооперации', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('fe2cb673-d9d1-4b5c-9e33-5c892db78596', 'Ступинский филиал Московского финансово-юридического университета МФЮА', 'Ступинский филиал Московского финансово-юридического университета МФЮА', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('527b80a3-08df-43c3-b0f7-266b8636d5d5', 'Вольский филиал Института экономики и антикризисного управления', 'Вольский филиал Института экономики и антикризисного управления', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7fbb1e83-e55b-45b2-b0b0-1e3f67d2ac44', 'Данковский филиал Института экономики и антикризисного управления', 'Данковский филиал Института экономики и антикризисного управления', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('725052c4-2a3e-4e7f-87db-2d30a82e2284', 'Выборгский филиал Российского государственного педагогического университета им. Герцена', 'Выборгский филиал Российского государственного педагогического университета им. Герцена', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('26be8ba9-7372-4ae8-95b4-5543a9e41cf7', 'Волховский филиал Российского государственного педагогического университета им. Герцена', 'Волховский филиал Российского государственного педагогического университета им. Герцена', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e26817b6-d50d-4ab6-a7bc-f855b8f30a68', 'Мончегорский филиал Национального государственного университета физической культуры, cпорта и здоровья имени П. Ф. Лесгафта', 'Мончегорский филиал Национального государственного университета физической культуры, cпорта и здоровья имени П. Ф. Лесгафта', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('216afd46-ce01-4027-b54d-a63feaf74f61', 'Тверской филиал Московского финансово-юридического университета МФЮА', 'Тверской филиал Московского финансово-юридического университета МФЮА', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('87f7512a-a4d4-4c1b-9a95-ce4616a05d25', 'Политехнический институт (филиал) Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Каменске-Уральском', 'Политехнический институт (филиал) Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Каменске-Уральском', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('54c7088f-8d74-4d40-98ba-ec1e000b8065', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Алапаевск', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Алапаевск', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f33346e0-f8e9-40bb-8923-4aea2927351c', 'Ульяновский филиал Московского финансово-юридического университета МФЮА', 'Ульяновский филиал Московского финансово-юридического университета МФЮА', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a439ced4-628c-467f-8345-a6282ef83089', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Верхняя Салда', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Верхняя Салда', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0b3a1ac2-2e3d-47b4-b31d-56fb87616447', 'Чеховский филиал Московского финансово-юридического университета МФЮА', 'Чеховский филиал Московского финансово-юридического университета МФЮА', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a1ccf2a8-4c35-488b-a3c7-e82fa5a7526c', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Ирбит', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Ирбит', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ee76771b-8b66-49f4-8521-d21e2cfaa987', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Краснотурьинск', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Краснотурьинск', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('4590dc89-df77-4161-920f-4997c9d3e3d1', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Красноуральск', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Красноуральск', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f3cba116-2636-45cf-b362-58c5238422ab', 'Ярославский филиал Московского финансово-юридического университета МФЮА', 'Ярославский филиал Московского финансово-юридического университета МФЮА', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('343c5aad-24a9-44cb-a228-ddabcc6dd9f3', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Невьянске', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Невьянске', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('dda6d4f9-d0df-4378-887b-1da8b3194beb', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Новоуральск', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Новоуральск', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ae44393c-be88-4c78-b64a-1d326c4d6877', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Ноябрьск', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Ноябрьск', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('053e9a45-9387-4531-80ea-a4d93660cffe', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Первоуральск', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Первоуральск', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('70328ead-18e1-4e32-a08f-98fcb761fced', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Серов', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Серов', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c4e59259-2823-47f9-8685-5cce09f44f86', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Чусовой', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Чусовой', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5e20b0e6-b3aa-42ff-b209-b7c9678c6ce7', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Среднеуральск', 'Филиал Уральского федерального университета имени первого Президента России Б.Н. Ельцина в г. Среднеуральск', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a75043ae-99a0-46b3-aa52-fc5233218a4d', 'Кыштымский филиал Южно-Уральского государственного университета (национальный исследовательский университет)', 'Кыштымский филиал Южно-Уральского государственного университета (национальный исследовательский университет)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e9320292-e46d-4c1a-826e-d5314c80d517', 'Нижневартовский филиал Южно-Уральского государственного университета (национальный исследовательский университет)', 'Нижневартовский филиал Южно-Уральского государственного университета (национальный исследовательский университет)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d26142cf-c806-4980-941c-a194198cd688', 'Озерский филиал Южно-Уральского государственного университета (национальный исследовательский университет)', 'Озерский филиал Южно-Уральского государственного университета (национальный исследовательский университет)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3823eb54-dea6-4422-89e2-c95fb74e5d40', 'Филиал в г. Аша Южно-Уральского государственного университета (национальный исследовательский университет)', 'Филиал в г. Аша Южно-Уральского государственного университета (национальный исследовательский университет)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f116affd-99f9-448b-9bcb-dec93435dd8c', 'Филиал в г. Златоуст Южно-Уральского государственного университета НИУ', 'Филиал в г. Златоуст Южно-Уральского государственного университета НИУ', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('82e58345-41e9-4dc3-b353-45355991bd86', 'Филиал в г. Миассе Южно-Уральского государственного университета (национальный исследовательский университет)', 'Филиал в г. Миассе Южно-Уральского государственного университета (национальный исследовательский университет)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('701745bf-ddf3-4c40-ad8c-76aca304e437', 'Филиал в г. Сатка Южно-Уральского государственного университета (национальный исследовательский университет)', 'Филиал в г. Сатка Южно-Уральского государственного университета (национальный исследовательский университет)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f4259f18-c0d8-4982-9db5-845121e5c036', 'Филиал в г. Снежинск Южно-Уральского государственного университета (национальный исследовательский университет)', 'Филиал в г. Снежинск Южно-Уральского государственного университета (национальный исследовательский университет)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('069a7bf1-7b92-4cab-9b66-c2d146b6ec51', 'Филиал в г. Усть-Катав Южно-Уральского государственного университета (национальный исследовательский университет)', 'Филиал в г. Усть-Катав Южно-Уральского государственного университета (национальный исследовательский университет)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c5950ad7-3f85-4f70-8d79-963f091db633', 'Астраханский филиал Международного юридического института', 'Астраханский филиал Международного юридического института', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('2b80a5a8-c20c-4868-9a84-8e720442d0c3', 'Волжский филиал Международного юридического института', 'Волжский филиал Международного юридического института', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('703ac767-e9d0-4a24-a47e-9386881e3abc', 'Нижневартовский филиал Сибирской государственной автомобильно-дорожной академии', 'Нижневартовский филиал Сибирской государственной автомобильно-дорожной академии', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f597d937-b6ef-40f0-b497-7a3f32799357', 'Сургутский филиал Сибирской государственной автомобильно-дорожной академии', 'Сургутский филиал Сибирской государственной автомобильно-дорожной академии', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('91e6607a-2f1f-4ba9-825e-1e05b8835d1f', 'Серпуховской филиал Военной академии ракетных войск стратегического назначения им. Петра Великого Министерства обороны Российской Федерации', 'Серпуховской филиал Военной академии ракетных войск стратегического назначения им. Петра Великого Министерства обороны Российской Федерации', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('bdbd4ca7-c325-4824-be7f-42bb25a4dfe0', 'ИЭУП. Альметьевский филиал', 'ИЭУП. Альметьевский филиал', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('d06febda-5b71-4065-a483-63285e9a2fe0', 'Ивановский филиал Международного юридического института', 'Ивановский филиал Международного юридического института', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e8f0ae6b-12ac-4b44-bcd9-a21035689856', 'ИЭУП. Бугульминский филиал', 'ИЭУП. Бугульминский филиал', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e4accb22-7e50-44b7-8f64-fc8c0652688f', 'ИЭУП. Зеленодольский филиал', 'ИЭУП. Зеленодольский филиал', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('71494b48-9de8-4dd5-a99c-ffe49d3286fa', 'Набережночелнинский филиал Казанского инновационного университета имени В.Г. Тимирясова (ИЭУП)', 'Набережночелнинский филиал Казанского инновационного университета имени В.Г. Тимирясова (ИЭУП)', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5d24a87d-463d-42cd-a846-debaed6dc0bd', 'Королевский филиал Международного юридического института', 'Королевский филиал Международного юридического института', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('530e12eb-1ea3-4775-bef9-17d7d8fbbcec', 'Нижнетагильский филиал Международного юридического института', 'Нижнетагильский филиал Международного юридического института', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e3b5a61a-ef77-4055-b348-b38f46f30e13', 'Одинцовский филиал Международного юридического института', 'Одинцовский филиал Международного юридического института', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('5e31b449-9d8e-4791-b3f0-4bee8d7f25dc', 'Смоленский филиал Международного юридического института', 'Смоленский филиал Международного юридического института', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6376922c-5b7e-459c-90c9-60d4a1d2d3b2', 'Тульский филиал Международного юридического института', 'Тульский филиал Международного юридического института', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1f05b5be-f950-4402-ba6a-1e78d14ec55f', 'Волгоградский филиал Московского гуманитарно-экономического университета', 'Волгоградский филиал Московского гуманитарно-экономического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('0ff525e3-e7b5-47cf-aa9f-3102f0581193', 'Калужский (институт) филиал Московского гуманитарно-экономического университета', 'Калужский (институт) филиал Московского гуманитарно-экономического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6641a87c-b6f2-401d-8182-d2f0c83feb03', 'Клинский филиал Московского гуманитарно-экономического университета', 'Клинский филиал Московского гуманитарно-экономического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7f2a07b2-7b24-4c6b-9af3-959837f6ee5a', 'Хакасский технический институт Сибирского федерального университета', 'Хакасский технический институт Сибирского федерального университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('83963f31-2875-4d52-ad9a-7966de756e49', 'Волгоградский филиал Московского государственного гуманитарно-экономический института', 'Волгоградский филиал Московского государственного гуманитарно-экономический института', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('efd6ddfa-9d19-4b01-9cb0-e632f1d5489c', 'Калмыцкий филиал Московского государственного гуманитарно-экономического университета', 'Калмыцкий филиал Московского государственного гуманитарно-экономического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('7c710130-92fb-4058-9a28-c09fc85d08f2', 'Челябинский филиал Московского государственного гуманитарно-экономического университета', 'Челябинский филиал Московского государственного гуманитарно-экономического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ae9f05ad-5bfc-47ab-8c9d-78ea8a9979bd', 'Краснодарский филиал Московского университета имени С.Ю. Витте', 'Краснодарский филиал Московского университета имени С.Ю. Витте', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('19bc2ed3-ab11-4d9c-bd70-41af2962b170', 'Пензенский филиал Московского университета имени С.Ю. Витте', 'Пензенский филиал Московского университета имени С.Ю. Витте', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b01b02f4-5953-4f5d-8245-2a375c98d51a', 'Рязанский филиал Московского университета имени С.Ю. Витте', 'Рязанский филиал Московского университета имени С.Ю. Витте', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('86c9aee9-5b5b-478d-baf3-3136ff94adbd', 'Тульский филиал Московского университета имени С.Ю. Витте', 'Тульский филиал Московского университета имени С.Ю. Витте', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('96ebf44a-d98e-4524-93ab-1cde5a1dde8d', 'Воронежский институт (филиал) Московского гуманитарно-экономического университета', 'Воронежский институт (филиал) Московского гуманитарно-экономического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('bfdb0a26-2561-4650-aed0-52c7c006ac52', 'г. Нижний Новгород, Нижегородская область', 'Россия', 'Нижегородская область', 'Нижний Новгород', 'г. Нижний Новгород');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('28730ffd-f10d-44b2-bdd9-60e915efa24c', 'Филиал Московского университета имени С.Ю. Витте в г. Нижний Новгород', 'Филиал Московского университета имени С.Ю. Витте в г. Нижний Новгород', 'bfdb0a26-2561-4650-aed0-52c7c006ac52', NULL, NULL, NULL);

INSERT INTO public.address (id, address, country, region, city, street) VALUES ('9ec80672-0601-460a-a7ee-59b6613e533e', 'г. Ростов-на-Дону, Ростовская область', 'Россия', 'Ростовская область', 'Ростов-на-Дону', 'г. Ростов-на-Дону');
INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f43a78a2-a6e5-444b-a668-a4f911aba064', 'Филиал в г. Ростов-на-Дону Московского университета имени С.Ю. Витте', 'Филиал в г. Ростов-на-Дону Московского университета имени С.Ю. Витте', '9ec80672-0601-460a-a7ee-59b6613e533e', NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('3c1e6e33-5d70-4297-9d34-c1ea5fe4b61b', 'Филиал в г. Сергиев Посад Московского университета имени С.Ю. Витте', 'Филиал в г. Сергиев Посад Московского университета имени С.Ю. Витте', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('21019a6f-1c91-42fa-b0c2-d5f0dcc1924e', 'Дмитровский институт непрерывного образования Государственного университета «Дубна»', 'Дмитровский институт непрерывного образования Государственного университета «Дубна»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('69d3102a-a2a7-4fce-a462-e3f6ba720bee', 'Филиал «Котельники» Государственного университета «Дубна»', 'Филиал «Котельники» Государственного университета «Дубна»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6120fa03-8b3f-41a5-bab2-910cf967ebca', 'Филиал «Протвино» Государственного университета «Дубна»', 'Филиал «Протвино» Государственного университета «Дубна»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('011412b5-abe2-4a6c-9bd2-f17b8cbf1500', 'Филиал «Угреша» Государственного университета «Дубна»', 'Филиал «Угреша» Государственного университета «Дубна»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('717b165b-f654-465e-9dba-74e20cfd5fdf', 'Лыткаринский промышленно-гуманитарный колледж Университета «Дубна»', 'Лыткаринский промышленно-гуманитарный колледж Университета «Дубна»', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('e1219df6-8c21-48d4-8115-1446eb143a0d', 'Зарайский педагогический колледж Государственного социально-гуманитарного университета', 'Зарайский педагогический колледж Государственного социально-гуманитарного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('f61f9107-c931-4ff5-afed-01cbbd261f5c', 'Колледж педагогики и искусства Государственного социально-гуманитарного университета', 'Колледж педагогики и искусства Государственного социально-гуманитарного университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('1e07e72a-fbfc-4b62-9720-43dcb915e529', 'Барнаульский филиал Московской академии предпринимательства при Правительстве Москвы', 'Барнаульский филиал Московской академии предпринимательства при Правительстве Москвы', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('920669eb-10f5-4899-9475-5e55981897f3', 'Благовещенский филиал Московской академии предпринимательства при Правительстве Москвы', 'Благовещенский филиал Московской академии предпринимательства при Правительстве Москвы', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('bf2e3ea4-0673-4637-b94d-86ea35d5f371', 'Казанский филиал Московской академии предпринимательства при Правительстве Москвы', 'Казанский филиал Московской академии предпринимательства при Правительстве Москвы', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('b19b4e97-5ee9-4c0f-a4dd-7ea566f087e5', 'Мурманский филиал Московской академии предпринимательства при Правительстве Москвы', 'Мурманский филиал Московской академии предпринимательства при Правительстве Москвы', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('9cb675d2-77a2-4401-9bd5-9915930bfbc4', 'Нижегородский институт (филиал) Московского гуманитарно-экономического университета', 'Нижегородский институт (филиал) Московского гуманитарно-экономического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('627ba548-c473-408f-9500-dd51b35f29ec', 'Нижнекамский филиал Московского гуманитарно-экономического университета', 'Нижнекамский филиал Московского гуманитарно-экономического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('c2b2bbe7-1373-4534-9334-fa35a6138980', 'Новороссийский филиал Московского гуманитарно-экономического института', 'Новороссийский филиал Московского гуманитарно-экономического института', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('bb8de314-89ca-468f-8fe3-352037aa3e7a', 'Северный филиал Московского гуманитарно-экономического университета', 'Северный филиал Московского гуманитарно-экономического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('6d25e7bc-5609-41c5-b0be-b9145e0d8d5d', 'Северо-Западный институт (филиал) Московского гуманитарно-экономического университета', 'Северо-Западный институт (филиал) Московского гуманитарно-экономического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('74e1d37f-2725-4dbc-8b44-6a385244e231', 'Северо-Кавказский филиал Московского гуманитарно-экономического института', 'Северо-Кавказский филиал Московского гуманитарно-экономического института', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('a33df3ff-1ea6-4895-9013-60fe705f9d1c', 'Ставропольский филиал Московского гуманитарно-экономического университета', 'Ставропольский филиал Московского гуманитарно-экономического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('10b12432-1fec-4955-9f48-e9cd76831e97', 'Тверской институт (филиал) Московского гуманитарно-экономического университета', 'Тверской институт (филиал) Московского гуманитарно-экономического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('14de36fa-7d10-4be8-946d-6a64f8a7d597', 'Чебоксарский институт (филиал) Московского гуманитарно-экономического университета', 'Чебоксарский институт (филиал) Московского гуманитарно-экономического университета', NULL, NULL, NULL, NULL);

INSERT INTO public.universities (id, name, description, address_id, rector, founding_year, student_count) VALUES ('ee86d2b1-e783-4ad5-b90f-e9006c3b3971', 'Курский филиал Московского института права', 'Курский филиал Московского института права', NULL, NULL, NULL, NULL);
