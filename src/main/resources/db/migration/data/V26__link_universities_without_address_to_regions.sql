-- Проставляет universities.region_id вузам без адреса (address_id IS NULL), добавленным из каталога
-- vuzopedia.ru в V19 (issue #80). Источник региона — город, к которому vuzopedia.ru относит вуз на
-- его странице /vuz/{id}; 77 записей поправлены вручную по названию вуза: подмосковные и
-- ленинградские филиалы, отнесённые источником к Москве/Санкт-Петербургу, города вне справочника
-- источника и единичные явные ошибки. Метод и перечень правок — scripts/README.md, исходные данные —
-- scripts/vuzopedia_university_region.csv. Записи-не-вузы и зарубежные филиалы сюда не входят — они
-- удаляются в V27.

-- Республика Адыгея
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '01')
WHERE id IN (
    'd7471402-f7b4-4943-929b-a4c0104f0f80',
    '45f72e18-6c49-44fc-81b1-7dc9a1f998ba'
);

-- Республика Башкортостан
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '02')
WHERE id IN (
    'a5f9fd35-0af7-4e7a-9f06-f9e1aeddc4ce',
    '694322cc-d8d9-41f2-83ba-1cb63abe48df',
    '2121a3b1-0fe0-4282-96c0-11ffbbf8ea16',
    '33a93223-7351-4a5d-bcdf-138ad09f001d',
    '455f085c-a4e2-4c7f-9475-223ab36414e7',
    '3c01fd3b-c35a-4c10-9170-74a90f9ecbd1',
    '634f9887-f1fd-463d-ac02-ccb2f01e091a',
    '0056403f-f36a-449b-bb8e-b8419e946918',
    '865eb1e8-6902-4ccb-a139-646b0d0614fa',
    'f4ad092e-e38d-4f5c-be96-3255f510e261',
    'fbab02f4-83dd-45ad-8de8-03d537171e0f',
    '0031f0b1-4ab3-4418-84d9-b34c53f645fe',
    'a687ad6b-8c0c-4d4e-8c00-0c629e5a7585',
    '6db7efb8-e553-4470-98a7-931d13b1f40a',
    '0013a8dd-bd9b-42ca-9fe8-7f3ec3d9ac29',
    'f726046f-9b04-4788-9aec-97300b18dff6',
    '82b4276f-f4be-4651-9410-547d33c8b10b',
    '8ebda584-66a3-46c0-98c0-5da7860efefb',
    '22b53c2d-bd6c-4a97-a98c-35cd48ff784a',
    'f4ba6363-b9ae-4693-80df-70d30f746d5d',
    '9438e7a9-4c1f-4eeb-8898-4e818e01fb8d',
    'b24d163d-4e8e-4a26-a5ac-a8f62c110f7a',
    '81105f71-41ee-4841-89dc-d267c0ceef4d'
);

-- Республика Бурятия
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '03')
WHERE id IN (
    '3a8f5be5-9b62-4a13-a728-b72f403d2b75',
    '01c84b09-341e-447a-bfd2-0f662c0b6bab',
    '730f7b19-eb8e-42e6-941c-b9787e66c20c',
    '12c5c9c3-62ee-4210-9609-c9cdaa6c5b92'
);

-- Республика Дагестан
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '05')
WHERE id IN (
    '93e17605-f7bd-4d54-b93a-329741bdc8cc',
    'ef943549-88fa-4bd9-bb5f-6fe2238c2035',
    '3e420d90-a747-4c13-9071-af5868add3d9',
    '9d25c799-ea36-484f-87c2-1385fd63ee60',
    'a25e83d3-4353-43bc-b8f5-3d521384692d',
    'd0ca2b2b-91c4-4dd9-898c-60770bcd9b52',
    '2c129bb8-30ab-4f2a-bf71-113c04e3a6d6',
    '1fd4999c-fd98-4a3c-a271-0ad879eabe0a',
    '7ede8187-d71a-4321-b542-45200dd3d9ca',
    'eab3e09b-5a45-4d2a-a5f9-3dc9740d2402',
    'acba9044-26d4-4741-a2f1-9aaf027e91fc',
    'd547d572-5081-4add-bd8e-8c43174b0d8f',
    'e74aeeb3-ba52-450e-8728-3307a563e269',
    '30fc0cf1-bd21-4f36-8d2e-02c8fa6eca68',
    '2d3c9997-5f5d-47d2-86c9-d7f0a615f307',
    'cdcfd7af-53fb-4234-b01a-b6cd5a510437',
    '0ba326e1-a8a2-4d08-86d1-5fb25b0dee5f',
    '4961d14b-b55d-4138-9624-a912aa6160dd',
    '01319bba-d622-4799-9c30-2122f88e2698'
);

-- Кабардино-Балкарская Республика
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '07')
WHERE id IN (
    '079bf57a-c190-4e11-adb5-7334fa500937'
);

-- Республика Калмыкия
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '08')
WHERE id IN (
    'efd6ddfa-9d19-4b01-9cb0-e632f1d5489c',
    '62062470-1539-4abc-ab1f-f25232d663fc',
    '77d71361-167b-4cef-a4d1-1de87dd583b3'
);

-- Карачаево-Черкесская Республика
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '09')
WHERE id IN (
    '1e36892a-103f-46f1-9379-489dd6bd4f0c',
    'e4cc8281-0751-4c6d-8ca2-4e7c34368915',
    '90cc9276-55ca-43ce-81f9-a80888e5705e',
    'be939b32-9b93-42e7-a906-62dd9dd06331',
    '52af6507-c00f-4d1d-b235-dca4f0afda73',
    'c3440eb5-9502-47e3-a00e-ef5131c5b721'
);

-- Республика Карелия
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '10')
WHERE id IN (
    '760d9cae-0da7-42b5-b94f-e3acbfe00099',
    '2b4cc4d3-70c9-4f8d-89ee-11cfadf3dfb5',
    'bed37687-2049-4aa6-a2ba-dd115171736e',
    'd3f1c40f-aad7-4567-81b4-2685b6d541b9',
    'db4d1fef-9a43-44b4-93f7-eb1408d9a791'
);

-- Республика Коми
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '11')
WHERE id IN (
    'b08a99e7-47e3-4c70-a3f9-48a321edac1c',
    'f6e317c8-ca49-4307-a896-2a0aa4efd006',
    '4e28a4a0-58bd-4f55-a052-7271c8bdbcb9',
    '7e74b91d-c7f0-4b93-b6b6-27543ceefaa8',
    '4e4fe8f3-c170-441e-9957-a0c708f1670e',
    '709503f2-7a1c-4c2d-8c58-e99d6eebdcf7'
);

-- Республика Мордовия
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '13')
WHERE id IN (
    '78aaec85-c80f-43e5-83a0-4bade6438472',
    '829e08ce-cfe0-4e15-9f18-4f1c88f4116e',
    '32770383-7521-4616-95c6-4197d12b9587',
    '9242d10f-8c55-48fd-8bfb-4c0ba298ebda'
);

-- Республика Саха (Якутия)
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '14')
WHERE id IN (
    '11d74be3-b56b-4dca-bf2a-cfe6164a0fb5',
    'bc25d702-2b34-4302-bb42-0745a8457d54',
    'c7eb0b09-d3b9-4f71-8264-3f18549c15c4',
    '178d6d7f-f2c1-4251-8f5a-9b1830b1b2bc',
    '92bdd75e-6d5f-48c6-9aa5-794b6cefee93',
    '0d2c2243-c8da-416b-a6f5-991cb688b7c1',
    '8c8f5fe3-9f74-4a3b-b802-a3c558137692',
    '894836d6-1b37-4c53-85fb-521ef64ea40d',
    '9c29fbd6-00fd-48b6-991a-f98701d2da1b'
);

-- Республика Северная Осетия — Алания
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '15')
WHERE id IN (
    '8f47804e-443f-4674-b6f0-57314838d7b2',
    '97f693f4-f3fa-4f8f-a113-ae99c8e83035'
);

-- Республика Татарстан
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '16')
WHERE id IN (
    'f35032e3-96f1-4ecc-8368-9e6adae25ac2',
    '0222b572-97bd-44d7-a771-cceeebdae157',
    'b7c83f2e-1513-4e61-a568-9b5a3f08a2d7',
    '3c540815-248d-4daf-abc9-c13334a9f7a2',
    '32bcbc87-5656-4713-b026-50362cd0f110',
    'b88d950f-75d2-42ca-b8f0-078cc9cbdabf',
    'bdbd4ca7-c325-4824-be7f-42bb25a4dfe0',
    'e8f0ae6b-12ac-4b44-bcd9-a21035689856',
    'e4accb22-7e50-44b7-8f64-fc8c0652688f',
    '5c1d9a4e-dec7-448c-a157-7291a6e550ac',
    'db6f0acf-5b96-4eea-9623-c57843de99f3',
    '11adb1e0-b0a5-419e-87d3-56f546019515',
    'bd158f59-da78-4e33-8999-6d7a50f86625',
    'd5ce9a8a-edf0-43d0-8074-1b0867738a5a',
    'bf2e3ea4-0673-4637-b94d-86ea35d5f371',
    'f0e22085-bfca-48d6-875a-4ec27ec4bdaf',
    '9044f08f-c5ed-4655-8789-e49c55e70560',
    'f9c4943e-f20b-4a05-b9a0-0e606e25568e',
    '2099fac3-e37d-4a78-8f69-18980af3ba7b',
    'a797e3ff-765e-4d1e-bc86-ca6a8e1ece62',
    '71494b48-9de8-4dd5-a99c-ffe49d3286fa',
    'baf21a41-80e0-460d-b511-4f734f92b3e4',
    '7d18b9b5-d06c-4394-9aff-3e66bb8066a9',
    'f504e6fb-3e07-4595-ab37-59baf8da5fcc',
    '627ba548-c473-408f-9500-dd51b35f29ec',
    'c1e21e04-4341-40bf-b2e8-40333f2fa6bd',
    '9af4fabc-79bd-44e4-bce3-ab042df30c85',
    'b921cde0-e58e-4865-b300-66586225a5c1'
);

-- Удмуртская Республика
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '18')
WHERE id IN (
    'adb7a8d9-56f9-4478-9e50-39ecc0ae2f61',
    '0765ab83-ba21-4e34-ae83-b691fd20209e',
    'be40830c-8068-402e-a2a7-5e3bea425c8c',
    'a2cd3f26-82a7-42a5-a065-285b19d3af90',
    'af8320f7-d2bf-4c9a-b918-3069aad2455f',
    'e9b0fbf6-3d5c-4f4c-93f8-385b8cb1fd37',
    '06e8b9b0-02e2-406e-9cd4-9384ea29db16',
    'cbf2b931-df31-4f85-b37e-e2cf270d9b34',
    'f8ccf193-f242-4487-8e6c-f9e56476dbd8'
);

-- Республика Хакасия
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '19')
WHERE id IN (
    '3c9d5a68-6084-41b1-b79b-fd5e4aebc7df',
    '7f2a07b2-7b24-4c6b-9af3-959837f6ee5a'
);

-- Чеченская Республика
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '20')
WHERE id IN (
    '73a81d7b-ef4b-4258-ab1c-c01c0a668df0'
);

-- Чувашская Республика — Чувашия
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '21')
WHERE id IN (
    'dd56bcfe-1d0d-4e61-8a73-152226eb598e',
    '31d8ea9c-fc36-4e44-afb1-24478280a8db',
    '0ac49c60-1d42-4386-9875-96badd7b4b66',
    '506bcee2-ad08-45ab-a6ab-d4b24304a254',
    '14de36fa-7d10-4be8-946d-6a64f8a7d597',
    '229161f9-8648-412b-b3fb-68cbd134396b',
    'd2b6b189-81a4-4ccd-a59a-3a283118e048',
    'eb882b82-e156-4bfc-9149-24c04f185992'
);

-- Алтайский край
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '22')
WHERE id IN (
    'bae7fd60-99cf-4f5d-8594-1106cfa3d3ee',
    'c9f8eb28-4df2-4ddf-bfbb-bb3df2a86a39',
    '4eb7d067-a4e7-4c01-8ba2-38e6c44e05c3',
    '423b77a0-2688-4bb3-a641-68ac205d03b2',
    'afb9913f-30f2-4002-a293-5dfc21daad22',
    '8c35e9e0-ad0c-4bfa-abec-67203221d5c0',
    '18070e58-038f-4f94-a394-fce3fca17454',
    '671e9c9a-7ad2-4b19-bfff-a7e8228652d1',
    '1e07e72a-fbfc-4b62-9720-43dcb915e529',
    '216aea54-9e78-4983-b444-52468eefb494',
    'bb7bf035-f0c4-4d20-9cea-fb5403075275',
    '7b495cba-d6b0-44a8-b08b-685ec15a5386',
    '94aa29c1-8837-42f7-a89a-69e210fdae44',
    'd2047957-3e52-4a10-8b9f-1684b79241e7'
);

-- Краснодарский край
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '23')
WHERE id IN (
    '9b379344-7372-404c-a58c-a041c264b763',
    '05723e39-5b4c-4eeb-bbd3-9992ac45c378',
    'ac6ed614-8e2d-45f8-90a2-1b65dfc0de65',
    '8e124fe5-9eb5-46d0-9ceb-db5b0e6b7a5d',
    'ae9f05ad-5bfc-47ab-8c9d-78ea8a9979bd',
    'bb45442e-9109-4bf5-ae51-f068e1a4167b',
    'b6f3f27f-4cb2-457e-bbdf-12289f4ff4ab',
    '648674a3-5d6f-45f0-82cc-c2562e6b5591',
    'ecd7dd43-f20a-4bc7-8568-ff12ebd92e21',
    '2a9cdd88-f1ab-4de7-9c88-55f37fbadab7',
    'c226e974-b2ea-48e2-b7a3-43bc85eba5a5',
    'c2b2bbe7-1373-4534-9334-fa35a6138980',
    '2956d9ee-dfac-4c69-80ac-9a61357b1b8d',
    '689667ae-2fe0-4c98-a576-94cba5648647',
    'cc2b0b91-0251-41fd-b9d4-02750d9eee52',
    '34e94270-3861-41f8-9a82-f95cdfe029ac',
    '7efd856f-bcc6-48a4-8314-bce2bcf5c4fd',
    '62becffc-7358-43fe-8cc6-4ee529d0c88f',
    'ce209e62-d59d-4f13-aa5f-cbc58d6b8d77',
    'f689e068-ba06-4a5a-a8db-78c11cf6eb67',
    'ae249082-9f17-4439-ba92-8183a470a259',
    'e1d714be-4caf-46a9-83a6-bae9548deb27',
    '2b2b3254-102f-4913-bc6e-613ef1eb95e8',
    'fb5e793b-2c38-4cc6-97d7-5eebcc209e7b',
    '76d0c446-4cd4-4bc5-8ded-47cf6c5a6db2',
    '0b5db983-d1b0-4869-bc92-133a27463e51',
    '64322fbe-3acb-42be-8ef8-089bcbe2237d',
    '1d75eb18-2f10-4177-958a-32e3e178e3f1',
    '68f4fff5-a7b0-4316-b9d9-4bde586b9ece',
    '9c182bb9-ec00-40a5-97ed-98c68304f490'
);

-- Красноярский край
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '24')
WHERE id IN (
    '895dd770-2e59-490a-afc3-a715505c82b1',
    '35160fac-f8eb-4752-8b15-780da0b43e7b',
    '7ea8dcaa-6335-4c41-9da3-23a42d0d93a4',
    'd2bd6afc-e9e1-4791-86b8-6fed89bc205a',
    'ac42712e-a4a0-4b97-90ae-5454713bc123',
    'a293b2e2-fc6e-4364-b3f6-b27c3dcef412',
    '6944f63d-5c36-4a9d-b3a5-a0af88dd0190',
    '0b031346-6f4a-48fa-8e7d-46940a67da22',
    'd2164d0c-6316-40eb-8420-f026389e682d',
    'e9095044-231e-400e-9c23-d161d49de05a',
    '51ab237f-e4ca-4c9c-baed-cbf78e931581',
    'e63fad21-02b7-43c1-acfb-5e698dd1caff',
    '5f7bae82-ccb3-42aa-8814-a43ae1e7b57e',
    '51a5b151-a547-4038-8776-e839368cb561'
);

-- Приморский край
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '25')
WHERE id IN (
    '322cedba-e8c5-46b5-8ba2-323a89648f43',
    '0b119991-f1ea-487e-b16d-32cdca33083e',
    'df678470-709e-40a5-877a-55608b23f9b5',
    '7d70b069-01ab-43ec-9186-3c08d590a21e',
    '1fc3a503-a615-46c0-be5f-d6e881ebb262',
    '11927de8-09aa-433f-91dd-c413d71bc69d',
    'e29fa519-5871-4a57-9242-393753dce41b'
);

-- Ставропольский край
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '26')
WHERE id IN (
    '0a761f1e-11a0-4f74-a384-7decd34fec03',
    '7974d665-6ea4-4c54-b7b9-7491bc232bf2',
    '2f380659-1a84-490b-8769-7efaed7a9fc0',
    '6d72cdac-4145-48c7-9eea-0c1634c34938',
    '24adee3b-21e0-4ebd-aa8e-b1647b7a05cb',
    '4d4e217a-c53d-4265-a985-21a86bd26c6e',
    'ab6c4e57-c848-431b-b61e-95ffcad78f5e',
    '2f6529d4-6e3b-4fc9-9716-89c836a88ac8',
    'e552e83a-e894-452d-aab0-c5f8611814c6',
    'c0d3b1cb-b436-49c1-923e-56f78f8e4c66',
    'f85f3d19-c975-4697-98d8-e3612e7f3492',
    '9011e49d-2f98-4574-b27f-7164b5c7571e',
    'e15069bf-e51d-41fe-ac81-613d085bfb18',
    '74e1d37f-2725-4dbc-8b44-6a385244e231',
    '6de8734e-61e3-44db-b65f-4e05ba9de332',
    'a33df3ff-1ea6-4895-9013-60fe705f9d1c',
    'a558e8b0-7eda-45c2-a3f9-d3ae05e9ace9',
    'f7e9170a-ca35-4689-9f27-fb76f4686a12',
    '0b7d0287-c36f-463c-ba1b-2d9bc4b22e72',
    '3511ba32-aaa0-4b95-accd-79ae2a67445b',
    '6ad465e7-5dca-4e05-b78d-7cd16c1f16de',
    '784a4987-bd2b-4362-900c-1f121dcd80cc',
    'e0dd532f-07ce-4c27-be4e-b1f19a5111d4',
    '4d4f8ec5-32ce-4358-897e-f44dd5d3f7e6'
);

-- Хабаровский край
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '27')
WHERE id IN (
    'd241fd86-8750-4730-a5ab-7bc9935cb845',
    '7656fff9-8b6c-4228-8079-50a7cf105718',
    '4c3dd25c-1a89-43b6-9244-87fd9139968a',
    '606adc90-7e21-4f9d-808a-a3592b6d70c2',
    '049e02c6-2b7e-464b-bfa3-318abfe11a90'
);

-- Амурская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '28')
WHERE id IN (
    'fb36a54e-e165-4db5-b4cf-6a153cdbeb2a',
    '14eb5edd-4892-438d-bf39-35eb336ebd7e',
    '920669eb-10f5-4899-9475-5e55981897f3',
    '263bfa6b-4343-4bba-b18a-3485d6136a84'
);

-- Архангельская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '29')
WHERE id IN (
    '9fff0c68-1a09-4892-a40a-6a1b27f5b2e8',
    'a768a383-97db-4361-81cc-93fb96614ec4',
    'bb8de314-89ca-468f-8fe3-352037aa3e7a'
);

-- Астраханская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '30')
WHERE id IN (
    '3997c117-6500-47de-b6d6-222beeea14a4',
    '2052e692-33e2-45a3-b747-c2834a80f4ca',
    'c5950ad7-3f85-4f70-8d79-963f091db633',
    '9daccae5-9c8c-4a29-bff3-becd583df0df',
    'cf9ffb1b-eeee-46fd-8276-c253ff6d7973',
    'a401d84c-a8fa-4d78-b93e-ffa37828d064',
    'c59058a4-493d-4a46-bac0-58d9429abc20',
    'ca47b6b1-5ed3-4f6a-8097-cdf2c838e249'
);

-- Белгородская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '31')
WHERE id IN (
    '894a0bba-d028-46a4-979f-d90fe21071e3',
    'e9816404-059e-4457-9045-f5f30a3c1fef',
    '6c2e2ca3-fd86-4126-81a3-6727a42a1d94',
    '28c3f83d-88c2-42ec-b1f0-5c3ff96853ef',
    '795e54c6-5e12-4bef-8075-e85d8bb6f711',
    '16292c20-bb7a-4dd5-ac52-cad96ea65d48',
    '4820573a-989c-4baa-9577-452e7d9d3b45',
    '1bd57c13-a404-41c1-a430-7981dfc56e88'
);

-- Брянская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '32')
WHERE id IN (
    '0e5ecdd6-6c79-4a31-91c2-77a2962143a7',
    'b5c531a6-cfd8-46d2-9988-d1ff62d98f28',
    '68693445-c0d1-4bbc-aee9-b8e9df28e252',
    '78747948-920d-495b-b907-653437417f0d',
    '73410259-70f7-4ba3-add3-7532ea13fc5e',
    '3ad867b7-1fa9-464c-bc67-59e66f286b1c',
    'cab62126-3a4f-4635-8a59-544fb0c7d6cf',
    '1cd2e21c-37de-49fa-a4cb-6118c48232fa',
    '07e2bf98-5619-410f-94ef-ffc47245c379'
);

-- Владимирская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '33')
WHERE id IN (
    'f24d87a3-25bb-4fb0-bcad-9431d5749c7f',
    '44ebe0e4-b75f-459a-bb2a-f13781ea7fc6',
    '3a11752d-8f9e-4cea-b98c-f0481f3b6808',
    'f9958682-bf3c-40ee-87e2-48b558d23f45',
    '7fe579ec-97fe-49cd-95c3-905ecf19b482',
    '57e35633-a977-4139-8d41-99337e1e9ec4',
    '2d346b9e-c9df-422c-ba03-3414169b6ae4',
    '937fa133-91f6-4ae6-919e-a4fb598fc72a',
    '8eb368ea-f725-49bc-a5a8-83c5dea1e491'
);

-- Волгоградская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '34')
WHERE id IN (
    '8872470f-5380-4bbf-887a-11c4a4ae63c4',
    '83963f31-2875-4d52-ad9a-7966de756e49',
    '489ab03c-8ad0-4539-91ea-130ff39e8f7c',
    '1f05b5be-f950-4402-ba6a-1e78d14ec55f',
    '8d64732d-e970-4952-80e5-b6c7caddc436',
    '7ba65978-f3a2-4772-8230-6ece64d6eb3a',
    '4a8b8dd7-898c-421a-a469-c7639ffa82fd',
    '2b80a5a8-c20c-4868-9a84-8e720442d0c3',
    '1afc3708-531d-4b88-bfd2-3b7c7f15ef30',
    'cc570726-d817-49bc-99cd-1ee12811cb30',
    'a741abcd-058c-4e80-ad0d-fc0648401dad'
);

-- Вологодская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '35')
WHERE id IN (
    '364a6fea-2f1c-4a1d-b219-14d1041ae3f6',
    '33379cd2-453d-4fa9-8201-1280246b8959',
    'f55b8d11-d1fd-4f34-9430-ac6159845fca',
    '4b91041f-8b73-4303-96dd-b36bac1c001c',
    'a3d55f7b-11b2-4601-8abd-bb9d01c62fdd',
    'b94a41de-5cbf-468d-b3b6-615e3be06605',
    'b1438799-7abd-461f-a8ac-da7546db3948'
);

-- Воронежская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '36')
WHERE id IN (
    '96ebf44a-d98e-4524-93ab-1cde5a1dde8d',
    'e5917eae-e18d-43ff-a0d3-ad5bf8322adf',
    '58425534-63f0-499d-b29d-f4112d5c5a90',
    '352634df-2218-4279-9b2b-74e4eb47e337',
    '8e0b1b02-36c1-4d55-8ef5-d2c275cc8609',
    '87cd67ec-10e1-4b01-8fc7-d7c86f48c351',
    '0b57a66a-21b3-470f-bf68-2ff16673c1f2',
    '0906e454-8d42-4d2b-acad-be3436dcf97b',
    '70de848d-da98-4709-9b88-405eb001347a',
    '08885bc0-a2bf-4302-87f1-69d76d6af100',
    '92aa820b-4646-4fad-9066-912abd76e4ab'
);

-- Ивановская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '37')
WHERE id IN (
    '1a1d182f-0e38-420f-a643-7419707f54d1',
    'd06febda-5b71-4065-a483-63285e9a2fe0',
    '71fcb636-1fee-4183-857b-579c93956812',
    '006dd2f2-c067-472a-9a36-f045ecf486be',
    '43393b58-088f-416c-ad35-4212b9c27325',
    '84f9c272-4602-43e7-a09d-892d5da97d69',
    '21fbb028-39ec-403a-ab8d-d8dc5854b144'
);

-- Иркутская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '38')
WHERE id IN (
    'f4a5348c-4068-4887-b407-6bf6807568bd',
    '571847b7-5229-4438-896a-cb8c319e1472',
    'a121e417-565c-4456-9c9b-9200cbb463df',
    'a315fda2-4379-4371-b6c2-fdcab11fa9b1',
    '7871193c-e177-4a39-a5cb-5ee60a67ed65',
    'fd90b677-1b08-4cfd-b958-c452971c4588',
    '17000b06-344c-4d80-aa59-d0f4931fe0f5',
    '0a3b61da-ebe0-4014-92be-806b145aa3ec'
);

-- Калининградская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '39')
WHERE id IN (
    'e6f22670-4228-49fc-8e94-7cfd89f0df36',
    '2edc150b-8ce5-4612-945a-58287dec708c',
    '006d640d-d699-47aa-9e30-7796a8fb2ea8',
    'd1a113dc-8b2e-4c9f-8b29-b2d5a5a1476e',
    '08f73de2-6ecc-4250-a255-eb44f37e5842',
    'e96528f9-1d18-482a-aa11-8d1bd9c049b6',
    '1fb025b3-dc53-474a-b021-59719312d500',
    '784a91af-74a7-44be-9dce-8bb9143b8336',
    '5450909e-83f5-4485-9a51-9cae3a1533f8',
    '253cac02-51fc-44cd-9ec6-259f89bafd5f',
    'bcf3af6f-1a33-4c71-b64d-c65e08d35fec',
    'b30be88a-55d0-49d6-a065-d7441f4872cd'
);

-- Калужская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '40')
WHERE id IN (
    '572c59e3-f464-42da-9f2c-d47ed478b566',
    '9a08e9fd-9fd9-41f9-b96a-b2b10e4d8ffc',
    '81b5e4a1-625b-4f6a-aa65-69e7d9fa8fdf',
    '0ff525e3-e7b5-47cf-aa9f-3102f0581193',
    '0f774a6f-691e-4f70-a3c8-299c0d098164',
    'f06a795b-2fa2-4693-9dd5-39e63adea7e9',
    '80d6c043-bebc-42fb-b291-837df5225f1a',
    '08ca6878-a03f-4f18-960b-7a056ebef8c3',
    '19786cda-bcc5-4f6f-ada5-445714991edf',
    '19ea0d47-c3c0-498d-981e-b28eff865911',
    '1ece31c4-0aea-4b16-b998-1709411a3178',
    '68616844-644d-43da-a36f-98ab9b77c709',
    '59711756-6db6-4f90-879a-65bdb8bec551'
);

-- Камчатский край
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '41')
WHERE id IN (
    '79740475-41c4-4aca-82ad-d92181122a3a',
    '14e34b9b-91cb-4554-82d1-e170c8a8390d',
    'c704a231-8bc0-4afa-a0c0-ea2ec107abac'
);

-- Кемеровская область — Кузбасс
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '42')
WHERE id IN (
    '18bd0ea3-daa9-4c24-81d0-5824cde8f8a7',
    '27729f67-b3f4-4158-8887-d13865dac72b',
    'b7fb80c5-7332-4458-bd58-469227d55d1f',
    '227685c5-1056-4fed-91d2-6dda1daed5c0',
    '15deab6c-1ad7-4385-9875-a7ebce5de3e7',
    'd3fa90c8-d97b-4e4e-b9ba-e8aab39b0ce2',
    '7ebab2b0-639a-4a5e-8e7a-717d0db0de6e',
    'e0518580-52a2-4d4c-b089-c2ba42cf7ec2',
    '6a302b11-3031-412e-a8c8-3f6623aed68c',
    '6c5cbf63-ef62-4d39-9d20-058934654282',
    '350d7611-83ce-417e-877c-decd076a632d',
    '46fb1f67-0b9a-4e39-8a52-e73c5d8214b1'
);

-- Кировская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '43')
WHERE id IN (
    'b7437981-e085-46c3-97e2-a48618af4e1e',
    'db04590a-1bbe-474a-bcde-1d5dc769aef5',
    '5a713c9e-e005-425f-b737-3612966ae5f1',
    '2221b2e8-baf6-4305-9812-fcba86e3d180',
    '15dae166-10e0-445b-ab9f-af7b4d824a86',
    '434e9b0e-da3f-4f48-aa60-18e0f5093406',
    '9ff73ecf-3aca-47fb-848b-af9a5d5fe8d5',
    'b72a6e87-84fe-4dc7-9603-9ebd63f11498',
    'ea14e043-53ec-4daf-bf46-cb8bb5c2772b'
);

-- Курганская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '45')
WHERE id IN (
    'e27840a8-7301-4495-a150-5ebbf88eb3ca',
    'dff04b17-a5ea-49c7-863a-16cc0f191bab',
    'c46f171e-bb5a-4f5f-b172-fe616189c27e'
);

-- Курская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '46')
WHERE id IN (
    '3ce93fd9-f388-497d-aa89-1be18f1a2e59',
    'c4b24ba1-ae8a-4af3-a0fd-4f987ae2f3af',
    '90e0a4aa-49d7-4884-b222-06c89d27192a',
    'ee86d2b1-e783-4ad5-b90f-e9006c3b3971'
);

-- Ленинградская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '47')
WHERE id IN (
    '4c82e5ad-8dec-4ee3-a0c5-ddb3aba1084b',
    '26be8ba9-7372-4ae8-95b4-5543a9e41cf7',
    '725052c4-2a3e-4e7f-87db-2d30a82e2284',
    '23a7533a-9ddf-4222-aacc-89fe5240659d',
    'f6650deb-e6ad-41e8-a874-ec800f305c74',
    '1d0e5d7a-62b4-4167-9fad-5120ad2202f3',
    'de117272-6dbc-4e46-8daa-e6d06e5de4fb',
    '1944966f-5b92-4b9f-b029-ca2d730fdbf4',
    '7fb62e8e-7de6-446a-bb97-7f6ce57f916c',
    'b89baa5c-83aa-4737-a203-28c1f456acc9',
    '6bcda613-9819-4a57-81c3-df9de0fd357b',
    '5f02c5d7-aafb-4071-ab44-b1460de39d92',
    'c790f717-8f15-4b6c-84ae-0474778978b5'
);

-- Липецкая область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '48')
WHERE id IN (
    '7fbb1e83-e55b-45b2-b0b0-1e3f67d2ac44',
    '739e6701-7608-4685-8436-6093252fe78c',
    '09de0ebe-fafd-490e-a6e6-c21cd0712d96',
    '454b17b8-9387-4c3f-9549-562987850b9e',
    '3b1e2892-fcd6-44b1-8bd4-6266eb9d548d',
    'a9698106-0f63-401c-b89d-741a1cb66637',
    '31c0c875-02ba-4910-b7b0-28c6fb8ecab4',
    'fe59aa53-5d7c-4b35-be66-12caadfcf4de'
);

-- Магаданская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '49')
WHERE id IN (
    '492f976e-813f-4617-abb8-758fe7be9b1c'
);

-- Московская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '50')
WHERE id IN (
    '57c096a9-2c32-4dae-af8a-8a106ebfdaa7',
    '7d924806-883d-4adc-b1b5-b6cc3c2ef469',
    '8dd1e240-58de-4162-968c-2092d374245e',
    'e60ff907-b293-4ca8-892a-4303d05faaba',
    '21019a6f-1c91-42fa-b0c2-d5f0dcc1924e',
    '264ffc20-98f1-44ae-9535-98ef18f79ba0',
    'fb25cc1e-1c08-4302-99fc-cd4ab025697d',
    '6454f626-e708-45fb-b8a2-18743e4f7fc4',
    '3f5a14e3-4cbc-4860-8177-930ecbb76a21',
    '67268781-946a-4960-97bd-e1784012bb64',
    'e1aaf594-6012-4d9d-9e2a-0f889df568f1',
    'e1219df6-8c21-48d4-8115-1446eb143a0d',
    '59788805-0464-47a7-a1a6-2213c8b0cd6c',
    '6641a87c-b6f2-401d-8182-d2f0c83feb03',
    'f61f9107-c931-4ff5-afed-01cbbd261f5c',
    'af9f2a49-8755-4652-bcab-bb7927ebc233',
    '5d24a87d-463d-42cd-a846-debaed6dc0bd',
    'a42ceff5-69b6-47d0-9642-10acb81aaa47',
    '717b165b-f654-465e-9dba-74e20cfd5fdf',
    '39c5d234-90d8-432e-bfb6-eb7d213c65c9',
    '7bb2b320-7556-4dd9-92cd-6cf63d095aa2',
    '932e3bf2-3ebd-4dcc-901a-8828f45c8717',
    '1f5c0443-f987-4fb9-b556-61a51a728d21',
    '7307c0e7-2750-45ed-9a24-6e5ad4b9af0c',
    '0ad3272b-f1c0-4e6c-a971-e027c68a8dd3',
    '277ef2d4-6967-4e79-815a-53a8a0a27974',
    '9f73f7e4-3f2d-4a6b-b00d-0770bfd1565a',
    'e3b5a61a-ef77-4055-b348-b38f46f30e13',
    '4d4b7534-7700-41d6-812b-ade6e1fe3387',
    'f4a19846-ca32-4249-b095-6b262b357a91',
    '8b6e46ab-6035-4f6c-af82-bc2d9daeea7e',
    '0e1b184f-c4ab-4fc3-9da6-dbecb607c931',
    '22787f0d-2dd1-410f-999a-e422a8255800',
    'd5a220fa-cf0b-46bb-b9f6-a67f86281313',
    '91e6607a-2f1f-4ba9-825e-1e05b8835d1f',
    '7d6981d8-1302-4326-8876-4dfc4015047e',
    'fe2cb673-d9d1-4b5c-9e33-5c892db78596',
    '105def01-580c-4adf-a3e8-1305f8510eb8',
    'da02f223-4a79-4e3e-bfcc-c9a480245140',
    '5250b078-a6f1-49a5-b649-58d15aaa1b3d',
    '7966854a-05c3-4b32-8507-4adc608fffb2',
    'b319c789-db93-46fd-9ffa-84690bb8a901',
    'dccd16ad-bbc3-45fc-b91c-54239d265df0',
    '3c1e6e33-5d70-4297-9d34-c1ea5fe4b61b',
    '69d6291c-6a7e-4ffb-80a3-d014cc969f40',
    '1ad94951-20cd-4d24-b846-4fbb4b8196c4',
    '69d3102a-a2a7-4fce-a462-e3f6ba720bee',
    'adf712ad-9a0d-4e5d-911d-3f04b7e34624',
    '9b388be9-34aa-43d2-b5ec-c796678e99ac',
    'a335a48c-8e48-4ea1-92a8-3f37abace90c',
    '6120fa03-8b3f-41a5-bab2-910cf967ebca',
    '7e7cc5f3-985e-472f-975c-d9504d1cc011',
    'fef50fb8-19c4-4aa9-b0c8-7c0c51da73f0',
    'fb01177f-619e-404b-a147-f4f1d81188c6',
    '011412b5-abe2-4a6c-9bd2-f17b8cbf1500',
    '91f50b26-9962-4ebe-91f2-ed6d3ccafcfa',
    '0b3a1ac2-2e3d-47b4-b31d-56fb87616447',
    '706ab119-965c-43b7-a589-feb70e765260',
    '5b00d8f7-df35-43fa-8ec7-e28f5f5cdd32',
    '126b2fe6-c8cb-47dd-906b-07660eca5fd5'
);

-- Мурманская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '51')
WHERE id IN (
    'e26817b6-d50d-4ab6-a7bc-f855b8f30a68',
    '4fc09629-2344-452f-b94b-7ddf13324735',
    '9b5a15c3-d051-4093-8c9d-d629852e9328',
    '99ba2f70-f87d-458c-a6b5-1a5078031e7c',
    'b19b4e97-5ee9-4c0f-a4dd-7ea566f087e5',
    '31e53916-adbe-407b-86e0-8899eb9dd22a',
    'f046a4c9-4dd9-4ab7-a660-cc0e8b5f9d95',
    '0b8c0fc8-d1e9-4e67-9a09-7c3c5ca6263b',
    '6d25e7bc-5609-41c5-b0be-b9145e0d8d5d',
    'ebc819a1-31de-4e40-b0c3-c6b9e019ca6c'
);

-- Нижегородская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '52')
WHERE id IN (
    '457dcd92-ea2c-4682-b2b9-9dd8d925e9c0',
    '2c007395-1d7b-416c-9e5f-fe49b3a2f277',
    '7bbb0c7b-e964-4f89-8332-19ce3bdd7a25',
    'd910f855-08ce-4bb1-bb84-0ca1e27f9de4',
    'e60bfd24-00b2-4cfe-ae49-69f30d1b3d5b',
    '4398b46e-2e1f-4da4-91b4-c48c19ed431e',
    '09ff06c1-8ead-41c5-b003-46d76257128f',
    '29ac7876-168d-4de4-ba9a-64a0f0121db2',
    '28e46234-5480-4c28-8dfc-74c15cde6112',
    '037394b2-7692-4767-bccd-a3a79e67e7a3',
    'aeeb8e70-bacd-4ef2-8ad1-b388ad338537',
    '5b1e91b7-1551-4bae-9ee2-b841b2411e92',
    'e205048f-afa6-4fe6-a921-7b7a69b61db8',
    '9cb675d2-77a2-4401-9bd5-9915930bfbc4',
    '49e386ad-ff13-49d2-82a8-20dce63551b0',
    '4c6b6815-8efe-43fa-906d-fe785b2c7b75',
    'e4a2790d-c0f1-4fa6-9cff-7978c2dda5ee',
    'a50e53a3-b678-4a56-9adf-a8ee3b9d2a44',
    '0f82ed93-8b19-4209-99ea-82fb63efb36b',
    '7b004d3c-b759-4efe-bab5-bcc25719067b',
    'b097b9e7-e859-43bb-9780-6253ad114627',
    'ad7b969b-e582-482a-964f-fbc2402af5b4',
    '1d1ab22a-1233-4bdb-9e56-be05d35098fe',
    '6f8ca8dd-1a95-4bbe-8f80-3773e091383c'
);

-- Новгородская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '53')
WHERE id IN (
    '0f9baa13-ff9a-4ddd-afd7-e517aa7df9b6',
    'e2a6641e-d83a-4bcb-8121-b1d56e1b0596',
    '627bc8ec-c747-4cee-9523-7ebdf1053493'
);

-- Новосибирская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '54')
WHERE id IN (
    '87a99d0e-8df4-416e-8fc8-f39877dfdbc9',
    '24d7d051-7230-4726-8dce-bde9cb2fd735',
    '4af84d88-0c23-4e19-95e5-43cba2a6cecd',
    '58ae9533-e76c-4128-9644-b42da168ae04',
    '43cf9811-cf40-4881-aad1-944e2eb7c139',
    'bc9881ef-d4a7-40ea-8a69-3ecb81383db6'
);

-- Омская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '55')
WHERE id IN (
    'dc86a7a7-6177-446e-a215-549b6cb969cb',
    '1c677e36-048f-4364-8879-20af35aac59c',
    '3cf29b74-e1ed-4877-bdd2-63b6c0962b7a',
    '4f29c710-b535-428b-8358-fa7ca51fbbe4',
    '5851e233-5099-40b7-8023-ca4b8092e0e0',
    'd789ef71-7cf8-481c-8167-09680720c2d9',
    '72798ea3-1cf7-4b8d-802c-fea4cba16c7c',
    'acf8a277-c34b-4cd4-add8-b4e1f06b9bcd'
);

-- Оренбургская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '56')
WHERE id IN (
    '5032879e-66b2-42a4-a4b7-44e386aa4d3e',
    'd5b30ede-0d08-40a8-938b-1c83fe6d79fe',
    '8f890a5a-a19b-44ed-8379-136dd7f8230d',
    'fc0dca22-f320-4bfd-8ffa-9b285e2deaa5',
    '9cbdcb1b-25d7-4e05-b0dd-527ed025365f',
    'd16525cf-222d-4a27-b4fd-02773c375ee9',
    'cf642ce1-43f2-440f-aee5-05e4a16a5dc1',
    '3032f789-95b8-41bb-befc-343b72a7088a',
    '25486ff5-3346-428c-954b-4cc919ebb107',
    '2c732647-089b-47dc-a4bf-2e234c19d314',
    '30067265-43b0-4385-8ff9-5b953bbd882a',
    '0dc5d319-3495-4295-a3ab-086442c0ca2e',
    '85477e25-21a3-4bef-afbe-72fba0839616'
);

-- Орловская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '57')
WHERE id IN (
    'c73e0fd9-271d-4af0-838f-2862f645f722',
    'f476fb90-5b53-4de9-9674-2a4012309c2d',
    'ca2a167f-136d-40c6-84d5-dc482037dc98',
    '812d9a01-5856-41a5-8afe-b8e786561bbd',
    '0146395b-9433-4779-99e2-9d0a15275e5d',
    'ec584985-4f3f-4f2d-824b-544c9e2ff26c'
);

-- Пензенская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '58')
WHERE id IN (
    '02cda22c-c63b-4974-b03f-7fd0a003c956',
    '78cc3027-18bd-4b84-b655-1bac9f988312',
    '19bc2ed3-ab11-4d9c-bd70-41af2962b170',
    '68e5fbcb-15fc-4994-adae-83a6ddd1187d',
    '8f640b77-a850-4ee3-94aa-6f22bfed328b'
);

-- Пермский край
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '59')
WHERE id IN (
    '658f117c-2b1e-4577-93e8-5b4997eccb9e',
    '5e8f6d3b-264b-471f-aca3-42f66abb0398',
    'b86e6edb-d2c4-42c7-9ac9-bc204399b9bf',
    '6020cffd-4375-407f-a285-03e6a0a28501',
    '01dd79f3-08f8-4e2a-b104-80a2f313ee70',
    '20f686b8-114f-43e3-8dc7-4cb075b34df8',
    '8905b055-5c87-4e8b-b30d-4f46a934b833',
    'c8b3e708-b161-4f6b-82c6-c9a833f14f76',
    'cb50d0b4-eee1-4d58-945f-4a54a0bbe042',
    '2f7edc92-9418-41ed-9714-581ae241b10c',
    '7d40e5ac-e352-49be-9adb-47216b60c863',
    '88d4d0ce-b0d6-4baf-9a64-5a660815797f',
    'c4e59259-2823-47f9-8685-5cce09f44f86',
    'e458c8df-13b6-4272-a7b1-adb160b5946b'
);

-- Псковская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '60')
WHERE id IN (
    '126e79bc-13ca-4678-9cf6-09f3c4f30463',
    '7d81618a-3fa1-4d69-a273-1a4d3362fcbf',
    '54bcfe9f-3b31-4fd9-87df-f6c54bd02a9a',
    '02e7565d-9a7e-4b2d-843d-5b5b77d0ab97',
    '6b222c74-a952-4403-ba16-e98a534fe335'
);

-- Ростовская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '61')
WHERE id IN (
    '8a05f96c-537e-40ca-bbca-7c9747c2a583',
    '3ee4ab1a-fa0b-45dd-a34f-4d51bef79e1a',
    '0429a506-3928-49c7-8e10-0cc77adc04c9',
    '6906d71b-8b69-445f-a71d-c83aa2caf007',
    '0842aebf-99d1-4fe9-b0af-71595b90dfb2',
    '7cffc8ce-ecc0-4555-b096-d209c591db16',
    '2f97a68f-bd26-4e52-8b95-853e9690056a',
    '8b8ba89b-97ba-4927-9022-4616151ada74',
    'c23c1a92-d26e-45bf-8249-13a48250a951',
    '3c4e79b9-c6b8-4fee-b4b8-90c6e3bae537',
    'e5a0178b-ff03-4949-aa9f-747a484df094',
    'b0bccd4c-69af-4e29-be71-54d65fbcc1e6',
    'b7f7acfa-9e8a-4a8f-90ea-d0ee2eb28926',
    'ead06ca4-e7a2-4b97-b953-d818605f1030',
    'c6b3dbf7-eb9e-41f2-bf53-323564fc07dd',
    '19211854-832b-4840-a2c7-a423b6dd6533',
    '3fba568f-d97d-4de8-8812-2279af170c2a',
    '91db1717-210b-40ad-aded-cbf5f5afef13',
    'e70a7e44-c70e-4f98-a114-45e19e4ac26f',
    '9e6e67f1-4dd7-400b-a9e2-a86ac54ff869',
    '9231addd-e4c3-40ea-8be4-27345592ebac',
    'e16a0726-a9f8-4ece-afda-479230ffa569',
    '46db88d3-42bb-481f-bec8-7ef62806ce53',
    'de125067-df8a-43db-ae7f-5e6e8af7459a',
    'db8d92a7-8eb1-4b48-b197-efbdb24be181',
    'f5d1e090-7480-4c04-a188-e32ad69e07bd',
    '3e1d802a-a39e-4669-a6db-774fb07e7439',
    '83901c0a-a3d1-436d-a54f-3c948be9d19e'
);

-- Рязанская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '62')
WHERE id IN (
    'c31dd56a-95d0-43c4-bfa6-fa90a044aedc',
    '16f7230e-10e4-4e82-afe6-34c9d3f0b9e5',
    '7a6f6c1e-6eb9-4e53-99ed-a57d67c72735',
    'd9acc5e2-adc3-4449-a8f9-8a83e9a2fd26',
    '6793203f-9c09-4958-97f3-fbd86721c21c',
    '2f0667a1-f8c5-41ae-9330-23275ce54bd5',
    'f0601761-46e4-4a3b-a64d-9fe1cc05b380',
    'b01b02f4-5953-4f5d-8245-2a375c98d51a',
    'ebb35526-4054-4825-923c-bdf429b18b84',
    'a316dbe7-f636-4354-ab38-58ac5354e3c3',
    '14c4ab24-26db-46bd-990d-702933978ccc',
    '84bc657a-663d-4f9f-a778-348acb4df40f'
);

-- Самарская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '63')
WHERE id IN (
    'ca53cca4-dec0-4d23-a7f6-13d64bb3045b',
    'd80501d2-2116-40ab-ba19-4ec2d8e7f252',
    '718dec2d-c5bb-42c1-a336-3c29ce82d51c',
    '4c6f2dd9-17d7-412a-8e6c-14b80618fd55',
    '43e38fec-214b-4326-9b63-2fe7ef752d0f',
    'c8bd4dcc-a0f1-4d5a-a142-e5359a38a1d2',
    '2370acad-7644-45e8-8c3d-ee9437e3283f',
    'bc9e503d-e75d-4a9b-b921-19de46f67fd5',
    '168eb458-5b14-43aa-878e-8c69ab5b665a',
    'c09cb9e2-cbf0-456e-b2c6-c51f7621f605',
    '76550107-32f7-467c-88ba-eb88caa4ae1e'
);

-- Саратовская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '64')
WHERE id IN (
    '725393ab-3f32-44c6-9d5e-0e08e687cf15',
    '5d659ee3-dd0d-4b0d-8d0c-8d5d8049d98c',
    'b157e9c0-b4ea-4cbd-9803-21390cb7e26a',
    '044d8fd3-87e3-40c1-b7a3-c277d6ad213f',
    '527b80a3-08df-43c3-b0f7-266b8636d5d5',
    '4cf37bd4-a4fc-43df-910b-1b2ed73ccce9',
    'c9ee9c41-1358-4e9c-8e0e-9c0b5531faa4',
    '8566949e-1e8a-4e86-8101-ebc583bd0f4b',
    '188f1d5f-88fc-4230-b382-b348fa503069',
    '51ffb7d5-055c-4190-b7a6-640a7567525d',
    'b63a2db3-6e0a-41e0-80ad-20781ad5bf72',
    '6eb10a0d-6d7e-4966-b9d5-177812c78a16',
    'e97d1acf-bc2d-420a-82cb-57ad48048bb4',
    'c80b70a6-4bf6-4c6e-ae33-1323ab41226c'
);

-- Сахалинская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '65')
WHERE id IN (
    '30ced7e8-1d3d-470a-8371-f8691a26b5e1'
);

-- Свердловская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '66')
WHERE id IN (
    'a207f087-c792-44c3-b538-a51db831b61b',
    'b949e348-1c45-4eb4-b5be-62c653aa3400',
    'e18d38a3-446b-464d-bd68-885a16f50602',
    'aff2d9d8-a9f3-4f90-9150-fad0ed95f2fc',
    '530e12eb-1ea3-4775-bef9-17d7d8fbbcec',
    '076c42fd-641d-47f9-8657-43d55742544e',
    '87f7512a-a4d4-4c1b-9a95-ce4616a05d25',
    '0cec0a69-147b-45c3-bd51-e4afb1365295',
    '0a570d02-1380-4c8c-b095-2650f8e47885',
    '1e6d8417-f13f-4ddd-a28d-2e3733f3c7c1',
    'd8c15470-e65a-4a95-b895-3ff740828b73',
    '3117707a-707b-4c4a-8ffc-5314a9668a37',
    '54c7088f-8d74-4d40-98ba-ec1e000b8065',
    'a439ced4-628c-467f-8345-a6282ef83089',
    'a1ccf2a8-4c35-488b-a3c7-e82fa5a7526c',
    'ee76771b-8b66-49f4-8521-d21e2cfaa987',
    '4590dc89-df77-4161-920f-4997c9d3e3d1',
    '343c5aad-24a9-44cb-a228-ddabcc6dd9f3',
    'dda6d4f9-d0df-4378-887b-1da8b3194beb',
    '053e9a45-9387-4531-80ea-a4d93660cffe',
    '70328ead-18e1-4e32-a08f-98fcb761fced',
    '5e20b0e6-b3aa-42ff-b209-b7c9678c6ce7'
);

-- Смоленская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '67')
WHERE id IN (
    'a284fdc9-4a1f-4d72-a6f6-c450da651d53',
    'c9f42e6a-c097-4454-91b5-16326b838df7',
    'e4753ba8-19fd-4afc-84cd-72a45a52ebf6',
    'a1914e8d-9db6-4e82-9752-d4f65e140bc6',
    'd4037e3e-907c-4d90-b49e-7dfd2c997988',
    '5e31b449-9d8e-4791-b3f0-4bee8d7f25dc',
    '9f526df3-a08d-4548-8fb4-7af8539d3c7c',
    'f9516fcb-aad2-48b8-80f0-b518e4f0b780',
    'df8a548c-e21b-4bf2-b19a-fc2c7e9a3cbc',
    '97d95709-15da-4cbd-8157-1216dfab4727',
    '2190e38c-aabe-441a-bb8d-1523ec64ca7d',
    '6e0fff64-ba3f-42d1-9bb5-064b717711e1',
    'ba8d8f59-2cb5-4979-af12-61a5aa92cf16',
    'e1266d9c-811a-4cab-bd2b-aca3cf70bc50',
    '63f2a741-5149-43c7-9e6a-14035b7b7883'
);

-- Тамбовская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '68')
WHERE id IN (
    '87d22d8e-e856-4569-ba52-3f7a5cc4fb61',
    '007eea1d-8ea7-4916-b333-6eb61a5fa281',
    '45299309-ce47-424d-8ee7-40033f6f55eb',
    '9810b340-d36d-4359-afc4-f69a72b76fe3',
    '1611b735-2725-4f14-9cdb-dd4aa2ef3ae8',
    '9890dd4f-30cc-4636-8f0e-1af9bde37939'
);

-- Тверская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '69')
WHERE id IN (
    '074b92c2-8809-469c-9cec-28271f078dc2',
    '430dc1fd-b743-4892-a194-f04116b3084f',
    '10b12432-1fec-4955-9f48-e9cd76831e97',
    '216afd46-ce01-4027-b54d-a63feaf74f61',
    '75b9c75d-483a-43c9-8f88-03b7afa6c75c',
    '198b1586-0950-4409-9826-3de8e111f34d',
    'e992079e-c5cb-4031-9806-d74b8c689757'
);

-- Томская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '70')
WHERE id IN (
    '53c98598-c872-451b-9996-5e8c6e8d3521',
    'f30e34ac-276b-48e3-a9ea-4073eb26c7ea',
    '3b9e29b0-3a80-4c7e-99f4-1c343a3a5089',
    '7289dd6f-c0d2-4698-b09f-dcc5f8ba2bdf',
    '6e505b08-9f16-4086-ba70-ed0b84e2657f'
);

-- Тульская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '71')
WHERE id IN (
    '5e4cc112-2c23-4270-92fc-1589132383d3',
    '17ef100b-265b-4e16-be50-438a5770f92c',
    'e3b1a2dc-6023-4437-a894-a49cd5fb7709',
    '6376922c-5b7e-459c-90c9-60d4a1d2d3b2',
    'd81dff30-641e-4dd9-9c2e-dd33ada09f3a',
    '86c9aee9-5b5b-478d-baf3-3136ff94adbd',
    '90dc548c-c9be-41f0-80b6-60f3b8674e66',
    'e0947a77-d8a9-4f3b-a07d-56f633ee3591',
    '44a4ed5b-cad2-44ba-8aaa-420814ff1585',
    '565d91da-98be-4b22-b87b-a78406fcaf39'
);

-- Тюменская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '72')
WHERE id IN (
    '046837f3-12a3-4b33-ac5f-8c345169f50e',
    'ff5636a3-00cf-41e6-ae93-64dbb08c7bb5',
    '24df792e-f259-4512-b22b-20b88222df1b',
    'dffe6483-094f-4321-aad9-7c661ba7f859',
    '96e7a427-1541-4247-a707-9ae9d55f7ae4',
    '1f12c289-f02b-4b0d-bf2e-7077cf7d2d62',
    'a28e4576-c5c7-4818-8400-a016ca99eb49',
    '1460113d-1de5-423d-a576-7ba79c321319'
);

-- Ульяновская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '73')
WHERE id IN (
    '72dd4fc2-b7cb-478c-882b-0b74e8fbdb8f',
    '7d081bab-de1a-406b-bbec-f901d139a394',
    '1045289c-0e1f-4b72-8450-8ad66a8caaaa',
    '98de6a96-f27e-4528-884b-7f37cfc8315f',
    'f33346e0-f8e9-40bb-8923-4aea2927351c',
    '7f1848e0-d441-465f-884f-baa9dff560df',
    '97bf7a4b-14ec-4b9b-9239-021b8a6799b3'
);

-- Челябинская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '74')
WHERE id IN (
    'a852a492-5cfc-46f1-aa64-64eef479f88b',
    'c87642f2-09eb-44f2-bc64-b2771c5b04f5',
    'a75043ae-99a0-46b3-aa52-fc5233218a4d',
    'f4572ce5-2029-4d9e-b220-9eee564e95e0',
    '3ae4f10c-b15f-40f7-8597-6c048628f1a7',
    'd26142cf-c806-4980-941c-a194198cd688',
    '69b45855-b258-4fe5-8e79-e4b8bdf1f756',
    '5086d7d6-7c7b-48bf-9080-2f82aa1657ff',
    '9e1911db-f907-4acd-80ec-9d5a29aa1b6c',
    'd0afed7d-d91d-41fd-91ca-f042066e66ae',
    '3823eb54-dea6-4422-89e2-c95fb74e5d40',
    'f116affd-99f9-448b-9bcb-dec93435dd8c',
    '82e58345-41e9-4dc3-b353-45355991bd86',
    '701745bf-ddf3-4c40-ad8c-76aca304e437',
    'f4259f18-c0d8-4982-9db5-845121e5c036',
    '069a7bf1-7b92-4cab-9b66-c2d146b6ec51',
    '2b658eff-ecfb-4b89-add7-e840233e957e',
    'b694532a-be4b-4049-852d-eb950e83b347',
    '5152e65b-3d8e-46df-aa31-5c8ed24c896f',
    '2717b1b0-9e23-4d39-a73f-7fd6b7d03b54',
    '4fb2a2ec-4384-4b87-8f66-baae305facf6',
    'b599a7bc-ee03-406d-8f50-331763e4c252',
    'dbce607a-a381-48af-9ffb-4c643a69c392',
    '7c710130-92fb-4058-9a28-c09fc85d08f2',
    '4e3b5cee-d1d6-43f2-a3c8-87e2c7101425',
    '99fefe48-1984-4eac-8d8e-1f1e727a3c01'
);

-- Забайкальский край
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '75')
WHERE id IN (
    '201fae12-8757-4cce-9e19-ce43fbf7e1b9',
    '903d695e-a505-4905-94a8-ed63453fb664',
    '870be8e8-c61c-4a99-99fd-db8bcee6c227',
    '784af650-a0ba-4394-97af-386ef3855dfb'
);

-- Ярославская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '76')
WHERE id IN (
    'bd11444b-1ec3-4645-8da0-f7d56ac49f71',
    '0ca5f774-5b92-4740-a55e-baee619f5934',
    'ff0405b0-4d77-4586-b70e-f530b4b720eb',
    '9bd0bb59-3403-4d15-849c-e7387ea9276f',
    '8984fcd6-3741-47c2-bff3-c4f7cb7e9d58',
    'd3adf844-d26b-4aa3-b901-e5663f9931f1',
    '8646232a-a70a-4aba-8742-8a729654f939',
    'f3cba116-2636-45cf-b362-58c5238422ab',
    '52bd9bdc-0f86-47bf-8775-8e5a09b7cb1a'
);

-- Москва
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '77')
WHERE id IN (
    'a231ff79-bf77-4b36-9409-6d91fbeaacb0',
    '4fbf0327-a9c3-4154-a151-fe62141e0454',
    '84f6d0c3-e1ca-4219-b9d7-d995cb530bda',
    '446db09f-2b71-463c-b2ed-58be496608f3',
    '1ee80887-9eb9-4ce3-bd33-b13b18bd03b0',
    '8b5af7c6-c1b6-4a85-8803-609907bd16e5',
    'f13833dc-4b87-460b-b43f-e7805ad42076'
);

-- Санкт-Петербург
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '78')
WHERE id IN (
    'ea832142-9f6b-4878-b5ea-f78aa52d07db',
    'e830d725-4897-4882-9749-7ada74b88601',
    '434775a9-b3c1-4d06-9dff-954f915f17d0',
    'ab460f34-15c3-4be2-a5da-9f56a612c893',
    '4d60a7fa-2682-4f1f-b564-652475f2ef18',
    'a1d57607-3f54-45c3-b4e3-ac08d3141b24',
    '76eb4ce4-c0b0-4d50-aee3-ee8a7ecadc0f',
    '9a8af844-d898-46fe-ac0e-309e6e4318d1',
    'ea62e451-eee1-4e40-b020-2bb6d7cb16a7',
    '7abb12e2-44e4-43c1-9aa2-ba0c76137353',
    '40968679-21e4-46f5-bd87-586951bb0b71',
    'ecd8fd05-b513-4877-b370-a34082ea990b'
);

-- Еврейская автономная область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '79')
WHERE id IN (
    'c3e0ad1a-053f-460c-9383-85340153fc7a'
);

-- Ханты-Мансийский автономный округ — Югра
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '86')
WHERE id IN (
    '444de88d-ae81-45bd-ac78-e77f5591186a',
    '404fab38-bf46-451b-a5fd-63e61629ead1',
    '78c365b6-69e9-4cf0-8b9c-ebbf179f5fd6',
    'c5e304d8-eedd-416f-8963-5ce871397f38',
    '703ac767-e9d0-4a24-a47e-9386881e3abc',
    'e9320292-e46d-4c1a-826e-d5314c80d517',
    'a4074f80-8461-4f71-8ddb-69897f5f3a73',
    '4f8d5ae9-f7c2-415b-a9a4-39d5b3cab7d5',
    '6e0fa9c8-230a-49f1-acfe-6b931aca2e7a',
    'f597d937-b6ef-40f0-b497-7a3f32799357',
    'f8608248-b5bb-4e19-a533-1d061ff9511f'
);

-- Чукотский автономный округ
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '87')
WHERE id IN (
    '84e12250-aa4c-43d1-b096-8fee74c84090',
    'a4416eb0-7a41-4f58-a6d8-4b337334bc20'
);

-- Ямало-Ненецкий автономный округ
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '89')
WHERE id IN (
    '7f0259f1-5367-46a7-85e9-50169af8c601',
    '416e44a7-d56a-43a7-b169-f616f5dbf4dd',
    'ae44393c-be88-4c78-b64a-1d326c4d6877'
);

-- Запорожская область
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '90')
WHERE id IN (
    'd070d46e-5ad0-4dac-bd7d-675e54fdb6a5'
);

-- Республика Крым
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '91')
WHERE id IN (
    '01194a98-0c27-4e54-b39a-c055951d7d49',
    '401392af-b1e2-461f-9045-f42fb93b662b',
    '169fd077-d299-42a0-acf2-43573ef8a789',
    'c1f815d3-63b1-43b5-b660-5081cdfd42bc',
    '4a0d310e-a3d1-4fe4-a300-41848e7d13b2',
    '30cff472-f408-4b4a-a2e6-b005ce0c75b2',
    '5ef8919a-aecd-43f0-94ba-d9f6eca222f6'
);

-- Севастополь
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '92')
WHERE id IN (
    '8261d83d-aa03-4ed1-96f1-887ae00f15ff',
    '03865aac-fa04-463b-9811-c903fc13eea0',
    'be4757df-eb88-4f68-aefd-1d561f467e38'
);

-- Донецкая Народная Республика
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '93')
WHERE id IN (
    '96c14f80-67c1-4462-bf94-a2f6efe1516a',
    'e0aa8d39-d1fe-4aa8-a763-f7d0ccb4a750',
    'fde91051-efeb-4d7e-a857-6e42f7ab72ff',
    '61ce5c42-992f-4c49-b0df-903ed0724d60',
    '7072d33c-6ecd-4e7e-9956-09dd56d10609'
);

-- Луганская Народная Республика
UPDATE public.universities
SET region_id = (SELECT id FROM public.regions WHERE code = '94')
WHERE id IN (
    'ba49029e-2ca2-4da3-9742-934b9bb4d0fd',
    'f29ca5e1-1f6e-4a5c-bb31-2474fbf61eb3',
    '064fec89-e7df-4c10-8079-23a7973b5e81',
    'f8dbdc5f-c13e-4489-8287-ed40f89fa120',
    '6896de16-914c-4808-9186-e8137643f7f7'
);
