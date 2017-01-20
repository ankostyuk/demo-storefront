/* ИД аккаунтов компаний начинаются с 100 */
/* 
    Тестовая компания 1
    Пароль: 'company1'
*/
INSERT INTO account(acc_id, acc_email, acc_email_authenticated_date, acc_registration_date, acc_password, acc_type)
VALUES (101, 'company1@example.com', (SELECT CURRENT_TIMESTAMP + INTERVAL '1 second'), (SELECT CURRENT_TIMESTAMP), '198ed2ce72a2cbcf7288f3dcd79da075911f9778d66edc97c5bbd719c8a114d0', 'COMPANY');

INSERT INTO company(com_id,
    com_name,
    com_address,
    com_contact_phone,
    com_contact_person,
    com_site,
    com_schedule,
    com_scope,
    com_delivery_conditions,
    com_payment_conditions,
    com_payment_cash,
    com_payment_cashless,
    com_region_id
)
VALUES ((SELECT acc_id FROM account WHERE acc_email = 'company1@example.com'), 
    'company1',
    'Россия, Москва. Кремль',
    '+7 (495) 123-45-67',
    'Кирпичев Иван Андреевич',
    'http://www.mycompany.ru',
    'Ежедневно с 10:00 до 23:00',
    'Продажа строительных, ремонтных материалов и инструмента',
    'Москва от 180 руб. в пределах МКАД, от 365 руб. за МКАД',
    'При доставке по Москве оплата заказа производится наличными курьеру',
    TRUE,
    FALSE,
    (SELECT reg_id FROM region WHERE reg_name = 'Москва')
);

/* 
    Тестовая компания 2 (для тестирования дополнительных валют)
    Пароль: 'company2'
*/
INSERT INTO account(acc_id, acc_email, acc_email_authenticated_date, acc_registration_date, acc_password, acc_type)
VALUES (102, 'company2@example.com', (SELECT CURRENT_TIMESTAMP + INTERVAL '2 seconds'), (SELECT CURRENT_TIMESTAMP), 'cb95766ad8b651340e9bf67cd2b6a408b616852b44ab9c5b2cdc83d9bf2df8d4', 'COMPANY');

INSERT INTO company(com_id, com_name, com_address, com_contact_phone, com_region_id)
VALUES ((SELECT acc_id FROM account WHERE acc_email = 'company2@example.com'), 'company2', 'Россия. Санкт-Петербург', '87-654-21', (SELECT reg_id FROM region WHERE reg_name = 'Санкт-Петербург'));

/* 
    Тестовая компания 3 (для тестирования геотаргетинга)
    Пароль: 'company3'
*/
INSERT INTO account(acc_id, acc_email, acc_email_authenticated_date, acc_registration_date, acc_password, acc_type)
VALUES (103, 'company3@example.com', (SELECT CURRENT_TIMESTAMP + INTERVAL '3 seconds'), (SELECT CURRENT_TIMESTAMP), 'fd56553bc9ef8bd62d7565689e67f241640f936384d2a72df1965413ac61a1a6', 'COMPANY');

INSERT INTO company(com_id, com_name, com_address, com_contact_phone, com_region_id)
VALUES ((SELECT acc_id FROM account WHERE acc_email = 'company3@example.com'), 'company3', 'Россия, Тверская область, Вышний Волочек', '111-222-333', (SELECT reg_id FROM region WHERE reg_name = 'Вышний Волочек'));

/*
    Тестовая компания 4 (email не подтвержден)
    Пароль: 'company4'
*/
INSERT INTO account(acc_id, acc_email, acc_registration_date, acc_password, acc_type)
VALUES (104, 'company4@example.com', (SELECT CURRENT_TIMESTAMP), '1c16e159b4a9043dcbf90171c150a50422b21090fa6e8eca9a2f953ecbd930eb', 'COMPANY');

INSERT INTO company(com_id, com_name, com_address, com_contact_phone, com_region_id)
VALUES ((SELECT acc_id FROM account WHERE acc_email = 'company4@example.com'), 'company4', 'Россия', '777-88-99', (SELECT reg_id FROM region WHERE reg_name = 'Обнинск'));

/*
    Тестовая компания 5 (демо аккаунт)
    Логин: 'demo@bildika.ru'
    Пароль: 'demodemo'
*/
INSERT INTO account(acc_id, acc_email, acc_email_authenticated_date, acc_registration_date, acc_password, acc_type)
VALUES (105, 'demo@bildika.ru', (SELECT CURRENT_TIMESTAMP), (SELECT CURRENT_TIMESTAMP), 'dee1fcce4cdaf3bb3144a9227ca11a81fe32d34a287e3285b75caf2ca689f471', 'COMPANY');

INSERT INTO company(com_id, com_name, com_address, com_contact_phone, com_region_id)
VALUES ((SELECT acc_id FROM account WHERE acc_email = 'demo@bildika.ru'), 'Демо-аккаунт', 'Россия. Москва', '(495) 777-77-77', (SELECT reg_id FROM region WHERE reg_name = 'Москва'));

/* ИД аккаунтов менеджеров начинаются с 200 */
/* 
    Тестовый менеджер 1
    Пароль: 'manager1'
*/
INSERT INTO account(acc_id, acc_email, acc_registration_date, acc_password, acc_type)
VALUES (201, 'manager1@example.com', (SELECT CURRENT_TIMESTAMP), '4cbe4874348dadbed820e9d2a9482f1e322b1ccb870d6c5b08b5af77ca633a30', 'MANAGER');

INSERT INTO role(role_account_id, role_type) 
VALUES ((SELECT acc_id FROM account WHERE acc_email = 'manager1@example.com'), 'ROLE_MANAGER_CATALOG');

/* 
    Тестовый менеджер 2
    Пароль: 'manager2'
*/
INSERT INTO account(acc_id, acc_email, acc_registration_date, acc_password, acc_type)
VALUES (202, 'manager2@example.com', (SELECT CURRENT_TIMESTAMP), 'a7b64a09c6a006e16e66873fa680c47f524ec019e49fabdf387288ff0e6c4804', 'MANAGER');

INSERT INTO role(role_account_id, role_type) 
VALUES
((SELECT acc_id FROM account WHERE acc_email = 'manager2@example.com'), 'ROLE_MANAGER_DICTIONARY'),
((SELECT acc_id FROM account WHERE acc_email = 'manager2@example.com'), 'ROLE_MANAGER_MODERATOR_OFFER')
;

/*
    Тестовый менеджер 3
    Пароль: 'manager3'
*/
INSERT INTO account(acc_id, acc_email, acc_registration_date, acc_password, acc_type)
VALUES (203, 'manager3@example.com', (SELECT CURRENT_TIMESTAMP), '4a117f82ebd96d49bc131e4a75827dd32d233e07e99a2034ca965f867e9bb0df', 'MANAGER');

INSERT INTO role(role_account_id, role_type)
VALUES
((SELECT acc_id FROM account WHERE acc_email = 'manager3@example.com'), 'ROLE_MANAGER_MODEL'),
((SELECT acc_id FROM account WHERE acc_email = 'manager3@example.com'), 'ROLE_MANAGER_BRAND')
;

/* Тестовые разделы каталога */
/* Важно! 
    Для автоматических тестов в первой секции (Инструменты) 
    должна идти первой категория (Пилы) и в ней должно быть
    хотя бы одно товарное предложение 
   Важно!
    Если в таблице catalog_item есть запись с типом 'CATEGORY', то
    в таблице category должен ОБЯЗАТЕЛЬНО быть соотвтетсвующий элемент.
   Важно! 
    Для автоматических тестов сохранить ниже следующую иерархию, 
    допускается добавляение разделов и категорий после...
*/
/*
    sec:Инструменты
        cat:Пилы
        sec:Электроинструменты
            cat:Дрели
            cat:Шлифовальные машины (Debur machine)
            
    sec:Сантехника
        cat:Ванны
        cat:Смесители 
        sec:Трубы 
            cat:Водопроводные
            cat:Канализационные
        
    sec:Вентиляция
    
    sec:Двери
        sec:Замки
        sec:Ручки 
        sec:Межкомнатные двери 
    
    sec:Электрика
        cat:Провода, кабели
        sec:Измерительный инструмент
            sec:Высоковольтный
                cat:Current meter
        
    sec:Окна
*/
INSERT INTO catalog_item(ci_name, ci_path, ci_type, ci_active) VALUES
('Инструменты', '00', 'SECTION', TRUE),
    ('Пилы', '0000', 'CATEGORY', TRUE),
    ('Электроинструменты', '0001', 'SECTION', TRUE),
        ('Дрели', '000100', 'CATEGORY', TRUE),
        ('Шлифовальные машины (Debur machine)', '000101', 'CATEGORY', TRUE),
('Сантехника', '01', 'SECTION', TRUE),
    ('Ванны', '0100', 'CATEGORY', TRUE),
    ('Смесители', '0101', 'CATEGORY', TRUE),
    ('Трубы', '0102', 'SECTION', TRUE),
        ('Водопроводные', '010200', 'CATEGORY', TRUE),
        ('Канализационные', '010201', 'CATEGORY', FALSE),
('Вентиляция', '02', 'SECTION', TRUE),
('Двери', '03', 'SECTION', TRUE),
    ('Замки', '0300', 'SECTION', TRUE),
    ('Ручки', '0301', 'SECTION', TRUE),
    ('Межкомнатные двери', '0302', 'SECTION', TRUE),
('Электрика', '04', 'SECTION', TRUE),
    ('Провода, кабели', '0400', 'CATEGORY', TRUE),
    ('Измерительный инструмент', '0401', 'SECTION', TRUE),
        ('Высоковольтный', '040100', 'SECTION', TRUE),
            ('Current meter', '04010000', 'CATEGORY', TRUE),
('Окна (Windows)', '05', 'SECTION', FALSE)
;

INSERT INTO category(cat_id, cat_unit_id) VALUES
((SELECT ci_id FROM catalog_item WHERE ci_name = 'Пилы'), (SELECT unit_id FROM unit WHERE unit_abbr='шт.')),
((SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'), (SELECT unit_id FROM unit WHERE unit_abbr='шт.')),
((SELECT ci_id FROM catalog_item WHERE ci_name = 'Шлифовальные машины (Debur machine)'), (SELECT unit_id FROM unit WHERE unit_abbr='шт.')),
((SELECT ci_id FROM catalog_item WHERE ci_name = 'Ванны'), (SELECT unit_id FROM unit WHERE unit_abbr='шт.')),
((SELECT ci_id FROM catalog_item WHERE ci_name = 'Смесители'), (SELECT unit_id FROM unit WHERE unit_abbr='шт.')),
((SELECT ci_id FROM catalog_item WHERE ci_name = 'Водопроводные'), (SELECT unit_id FROM unit WHERE unit_abbr='п. м.')),
((SELECT ci_id FROM catalog_item WHERE ci_name = 'Канализационные'), (SELECT unit_id FROM unit WHERE unit_abbr='п. м.')),
((SELECT ci_id FROM catalog_item WHERE ci_name = 'Провода, кабели'), (SELECT unit_id FROM unit WHERE unit_abbr='п. м.')),
((SELECT ci_id FROM catalog_item WHERE ci_name = 'Current meter'), (SELECT unit_id FROM unit WHERE unit_abbr='шт.'))
;

/* Параметры */
INSERT INTO param_set_descriptor(psd_name, psd_table_name) 
VALUES('drill params', 'param_set_category_drills');

UPDATE category SET
cat_psd_id = (SELECT psd_id FROM param_set_descriptor WHERE psd_table_name = 'param_set_category_drills')
WHERE cat_id = (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели');

INSERT INTO param_group(pg_psd_id, pg_name, pg_ordinal)
VALUES ((SELECT psd_id FROM param_set_descriptor WHERE psd_table_name = 'param_set_category_drills'), 'Общие характеристики', 0);

INSERT INTO param_group(pg_psd_id, pg_name, pg_ordinal)
VALUES ((SELECT psd_id FROM param_set_descriptor WHERE psd_table_name = 'param_set_category_drills'), 'Дополнительная информация', 1);

----
INSERT INTO param(par_psd_id, par_pg_id, par_name, par_description, par_column_name, par_type, par_ordinal, par_base)
VALUES ((SELECT psd_id FROM param_set_descriptor WHERE psd_table_name = 'param_set_category_drills'), (SELECT pg_id FROM param_group WHERE pg_name = 'Общие характеристики'),
'Тип', 'По конструкции, принципу работы и кругу решаемых задач дрели можно разделить на несколько основных типов.', 
'psx_category_drills_param_type', 'SELECT', 0, TRUE);

INSERT INTO param_select_option(pso_param_id, pso_name, pso_ordinal)
VALUES 
    ((SELECT par_id FROM param WHERE par_name = 'Тип'), 'безударная', 0),
    ((SELECT par_id FROM param WHERE par_name = 'Тип'), 'для алмазного сверления', 1),
    ((SELECT par_id FROM param WHERE par_name = 'Тип'), 'угловая', 2),
    ((SELECT par_id FROM param WHERE par_name = 'Тип'), 'ударная', 3)
;

----
INSERT INTO param(par_psd_id, par_pg_id, par_name, par_description, par_column_name, par_type, par_ordinal, par_base)
VALUES ((SELECT psd_id FROM param_set_descriptor WHERE psd_table_name = 'param_set_category_drills'), (SELECT pg_id FROM param_group WHERE pg_name = 'Общие характеристики'),
'Тип патрона', E'Тип патрона для крепления сверла.\r\nСовременные дрели оснащаются двумя типами патронов: ключевыми или быстрозажимными.',
'psx_category_drills_param_chuck_type', 'SELECT', 1, FALSE);

INSERT INTO param_select_option(pso_param_id, pso_name, pso_ordinal)
VALUES
    ((SELECT par_id FROM param WHERE par_name = 'Тип патрона'), 'быстрозажимной', 0),
    ((SELECT par_id FROM param WHERE par_name = 'Тип патрона'), 'ключевой', 1)
;

----
INSERT INTO param(par_psd_id, par_pg_id, par_name, par_description, par_column_name, par_type, par_ordinal, par_base)
VALUES ((SELECT psd_id FROM param_set_descriptor WHERE psd_table_name = 'param_set_category_drills'), (SELECT pg_id FROM param_group WHERE pg_name = 'Общие характеристики'),
'Потребляемая мощность', 'Мощность, потребляемая дрелью при работе.', 
'psx_category_drills_param_power', 'NUMBER', 2, TRUE);

INSERT INTO param_number(pn_id, pn_min_value, pn_max_value, pn_unit_id, pn_precision)
VALUES ((SELECT par_id FROM param WHERE par_name = 'Потребляемая мощность'), 14.0, 3000.0, (SELECT unit_id FROM unit WHERE unit_abbr='Вт'), 0);

----
INSERT INTO param(par_psd_id, par_pg_id, par_name, par_description, par_column_name, par_type, par_ordinal, par_base)
VALUES ((SELECT psd_id FROM param_set_descriptor WHERE psd_table_name = 'param_set_category_drills'), (SELECT pg_id FROM param_group WHERE pg_name = 'Общие характеристики'),
'Максимальный крутящий момент', 'Максимальный крутящий момент.', 
'psx_category_drills_param_max_torque', 'NUMBER', 3, FALSE);

INSERT INTO param_number(pn_id, pn_min_value, pn_max_value, pn_unit_id, pn_precision)
VALUES ((SELECT par_id FROM param WHERE par_name = 'Максимальный крутящий момент'), 1.0, 220.0, (SELECT unit_id FROM unit WHERE unit_abbr='Н*м'), 0);

----
INSERT INTO param(par_psd_id, par_pg_id, par_name, par_description, par_column_name, par_type, par_ordinal, par_base)
VALUES ((SELECT psd_id FROM param_set_descriptor WHERE psd_table_name = 'param_set_category_drills'), (SELECT pg_id FROM param_group WHERE pg_name = 'Общие характеристики'),
'Работа от аккумулятора', 'Возможность работы дрели от автономного питания.', 
'psx_category_drills_param_accu', 'BOOLEAN', 4, TRUE);

INSERT INTO param_boolean(pb_id, pb_true_name, pb_false_name)
VALUES ((SELECT par_id FROM param WHERE par_name = 'Работа от аккумулятора'), 'Есть', 'Нет');

----
INSERT INTO param(par_psd_id, par_pg_id, par_name, par_description, par_column_name, par_type, par_ordinal, par_base)
VALUES ((SELECT psd_id FROM param_set_descriptor WHERE psd_table_name = 'param_set_category_drills'), (SELECT pg_id FROM param_group WHERE pg_name = 'Общие характеристики'),
'Режим работы - сверление с ударом', '',
'psx_category_drills_param_strike', 'BOOLEAN', 5, FALSE);

INSERT INTO param_boolean(pb_id, pb_true_name, pb_false_name)
VALUES ((SELECT par_id FROM param WHERE par_name = 'Режим работы - сверление с ударом'), 'Есть', 'Нет');

----
INSERT INTO param(par_psd_id, par_pg_id, par_name, par_description, par_column_name, par_type, par_ordinal, par_base)
VALUES ((SELECT psd_id FROM param_set_descriptor WHERE psd_table_name = 'param_set_category_drills'), (SELECT pg_id FROM param_group WHERE pg_name = 'Дополнительная информация'),
'Кейс в комплекте', '    ',
'psx_category_drills_param_case', 'BOOLEAN', 0, FALSE);

INSERT INTO param_boolean(pb_id, pb_true_name, pb_false_name)
VALUES ((SELECT par_id FROM param WHERE par_name = 'Кейс в комплекте'), 'Есть', 'Нет');

----
INSERT INTO param(par_psd_id, par_pg_id, par_name, par_description, par_column_name, par_type, par_ordinal, par_base)
VALUES ((SELECT psd_id FROM param_set_descriptor WHERE psd_table_name = 'param_set_category_drills'), (SELECT pg_id FROM param_group WHERE pg_name = 'Дополнительная информация'),
'Вес', 'Вес дрели в рабочем состоянии.',
'psx_category_drills_param_weight', 'NUMBER', 1, FALSE);

INSERT INTO param_number(pn_id, pn_min_value, pn_max_value, pn_unit_id, pn_precision)
VALUES ((SELECT par_id FROM param WHERE par_name = 'Вес'), 0.1, 1000.0, (SELECT unit_id FROM unit WHERE unit_abbr='кг'), 1);

----
INSERT INTO param(par_psd_id, par_pg_id, par_name, par_description, par_column_name, par_type, par_ordinal, par_base)
VALUES ((SELECT psd_id FROM param_set_descriptor WHERE psd_table_name = 'param_set_category_drills'), (SELECT pg_id FROM param_group WHERE pg_name = 'Дополнительная информация'),
'Дополнительный аккумулятор', 'Наличие дополнительного аккумулятора для увеличения продолжительности работы в автономном режиме.',
'psx_category_drills_param_ex_accu', 'BOOLEAN', 2, FALSE);

INSERT INTO param_boolean(pb_id, pb_true_name, pb_false_name)
VALUES ((SELECT par_id FROM param WHERE par_name = 'Дополнительный аккумулятор'), 'Есть', 'Нет');

/* Производители */
INSERT INTO brand(br_name, br_keywords, br_site) VALUES
('BOSCH', 'БОШ', 'http://www.bosch.ru'),
('STIHL', 'штиль', 'http://www.stihl.ru'),
('HITACHI', 'Хитачи', 'http://www.hitachi.ru'),
('Husqvarna', 'хускварна', 'http://www.husqvarna.ru'),
('Makita', 'макита', 'http://www.makita.com.ru'),
('Metabo', 'метабо', 'http://www.metabo.ru'),
('ИНТЕРСКОЛ', 'INTERSKOL', 'http://www.interskol.ru')
;

/* Товарные предложения */
INSERT INTO offer(
    off_category_id,
    off_company_id,
    off_name,
    off_description,
    off_origin_country,
    off_price,
    off_currency,
    off_ratio,
    off_unit_price,
    off_actual_date,
    off_active,
    off_available,
    off_delivery,
    off_create_date,
    off_edit_date
) 
VALUES
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Пилы'),
    (SELECT com_id FROM company WHERE com_name = 'company1'),
    'Бензопила Hitachi CS 45 EM',
    'Бензопила Hitachi CS 45 EM Особенности: Для профессионального использования Лучшее соотношение веса и мощности. Предназначена для длительных  работ в лесу Легкий запуск двигателя благодаря встроенному декомпрессору',
    'CN',
    14794,
    'RUB',
    1,
    14794,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp)
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Пилы'),
    (SELECT com_id FROM company WHERE com_name = 'company1'),
    'Бензопила STIHL MS 180',
    'Бензопила Stihl MS 180 14" Бензопила STIHL MS 180 - одна из самых лучших любительских бензопил компании STHIL. Оборудована системой электронного зажигания, системой быстрой остановки и антивибрационной системой. Бензопила STIHL MS 180 имеет легкий и быстрый запуск (возможен пуск с руки!), а управление с помощью одного переключателя обеспечивает комфорт при работе.',
    'DE',
    200,
    'USD',
    1,
    0,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp)
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Пилы'),
    (SELECT com_id FROM company WHERE com_name = 'company1'),
    'Бензопила Husqvarna 137 e (15")',
    'Цепная бензопила HUSQVARNA 137. Мощность 1.6 КВт/2,2 л.с. Механизм облегченного запуска обеспечивает мгновенный старт. Пила оснащена цепью LowVib, обладающей низкой вибрацией. Низкий уровень шума. Небольшой вес всех движущихся деталей мотора предполагает быстрый разгон.',
    'SE',
    150,
    'EUR',
    1,
    0,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp)
)
;

/* Параметры моделей и предложений в категории "Дрели" */
INSERT INTO param_set_category_drills(
    psx_category_drills_param_type,
    psx_category_drills_param_chuck_type,
    psx_category_drills_param_power,
    psx_category_drills_param_max_torque,
    psx_category_drills_param_accu,
    psx_category_drills_param_strike,
    psx_category_drills_param_case
)
VALUES
/* Параметры моделей */
(
    (SELECT pso_id FROM param_select_option WHERE pso_name = 'безударная'),
    (SELECT pso_id FROM param_select_option WHERE pso_name = 'быстрозажимной'),
    210.0,
    21,
    TRUE,
    FALSE,
    TRUE
),
(
    (SELECT pso_id FROM param_select_option WHERE pso_name = 'безударная'),
    (SELECT pso_id FROM param_select_option WHERE pso_name = 'быстрозажимной'),
    190.0,
    19,
    TRUE,
    FALSE,
    FALSE
),
(
    (SELECT pso_id FROM param_select_option WHERE pso_name = 'безударная'),
    (SELECT pso_id FROM param_select_option WHERE pso_name = 'быстрозажимной'),
    190.0,
    19,
    TRUE,
    FALSE,
    FALSE
),
/* Параметры предложений */
(
    (SELECT pso_id FROM param_select_option WHERE pso_name = 'безударная'),
    (SELECT pso_id FROM param_select_option WHERE pso_name = 'быстрозажимной'),
    101.0,
    26,
    TRUE,
    FALSE,
    TRUE
),
(
    (SELECT pso_id FROM param_select_option WHERE pso_name = 'ударная'),
    (SELECT pso_id FROM param_select_option WHERE pso_name = 'ключевой'),
    123.0,
    30,
    FALSE,
    TRUE,
    TRUE
),
(
    (SELECT pso_id FROM param_select_option WHERE pso_name = 'безударная'),
    (SELECT pso_id FROM param_select_option WHERE pso_name = 'ключевой'),
    150.0,
    50,
    FALSE,
    TRUE,
    TRUE
),
(
    (SELECT pso_id FROM param_select_option WHERE pso_name = 'угловая'),
    (SELECT pso_id FROM param_select_option WHERE pso_name = 'ключевой'),
    200.0,
    71,
    TRUE,
    FALSE,
    TRUE
)
;

/* Модели в категории "Дрели" */
INSERT INTO model(
    mod_category_id,
    mod_param_set_id,
    mod_brand_id,
    mod_name,
    mod_description,
    mod_vendor_code
)
VALUES
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT psx_id FROM param_set_category_drills OFFSET 0 LIMIT 1),
    (SELECT br_id FROM brand WHERE br_name = 'HITACHI'),
    'Hitachi DS12DVF3',
    'дрель безударная,быстрозажимной патрон, аккумуляторная, количество скоростей: 2, патрон 10 мм, реверс, вес 1.7 кг, кейс в комплекте',
    'DS12DVF3'
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT psx_id FROM param_set_category_drills OFFSET 1 LIMIT 1),
    (SELECT br_id FROM brand WHERE br_name = 'Makita'),
    'Makita 6271DWPE',
    'дрель безударная,быстрозажимной патрон, аккумуляторная, количество скоростей: 2, патрон 10 мм, реверс, фиксация шпинделя, вес 1.5 кг, кейс в комплекте',
    '6271DWPE'
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT psx_id FROM param_set_category_drills OFFSET 2 LIMIT 1),
    (SELECT br_id FROM brand WHERE br_name = 'BOSCH'),
    'Bosch GSR 10,8 V-Li-2 Professional',
    'дрель безударная,быстрозажимной патрон, аккумуляторная, количество скоростей: 2, патрон 10 мм, реверс, вес 1.1 кг, кейс в комплекте ',
    'GSR 10,8 V-Li-2 Professional'
)
;

/* Предложения c параметрами и моделями в категории "Дрели" */
INSERT INTO offer(
    off_category_id,
    off_company_id,
    off_name,
    off_description,
    off_brand_name,
    off_model_name,
    off_origin_country,
    off_price,
    off_currency,
    off_ratio,
    off_unit_price,
    off_actual_date,
    off_active,
    off_available,
    off_delivery,
    off_create_date,
    off_edit_date,
    off_brand_id,
    off_param_set_id,
    off_model_id
)
VALUES
/* Предложения с моделями */
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT com_id FROM company WHERE com_name = 'company1'),
    'Аккумуляторная дрель Hitachi DS12DVF3',
    'Официальная гарантия. Напряжение (В) - 12; Емкость аккумулятора (Ач) - 1,4; Две скорости вращения (об/мин) - 300/1200; Крутящий момент (Нм) - 26; Вес (кг) - 1,7.',
    'Хитачи',
    'DS12DVF3',
    'CN',
    4055,
    'RUB',
    1,
    4055,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    (SELECT br_id FROM brand WHERE br_name = 'HITACHI'),
    NULL,
    (SELECT mod_id FROM model OFFSET 0 LIMIT 1)
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT com_id FROM company WHERE com_name = 'company2'),
    'Аккумуляторная дрель-шуруповерт HITACHI DS12DVF3 2.0 Ah',
    'Особенности HITACHI DS12DVF3: электронное управление числом оборотов 22 установки крутящего момента поддержка заданной скорости вращения быстрозажимной сверлильный патрон удобная прорезиненная рукоятка регулируемый крючок для переноса инструмента с держателем реверс для завинчивания и вывинчивания шурупов 2 механические скорости для сверления и завинчивания/отвинчивания шурупов на подходящей скорости: низкая скорость имеет малое количество оборотов в минуту и большой крутящий момент, высокая скорость...',
    'HITACHI',
    'DS12DVF3 2.0 Ah',
    'CN',
    4290,
    'RUB',
    1,
    4290,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    (SELECT br_id FROM brand WHERE br_name = 'HITACHI'),
    NULL,
    (SELECT mod_id FROM model OFFSET 0 LIMIT 1)
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT com_id FROM company WHERE com_name = 'company3'),
    'Аккумуляторныe шуруповерты Hitachi DS12DVF3',
    'Шуруповерт аккумуляторный Hitachi DS12DVF3+фонарь+2 акк; 12V. Вес 1,7кг.',
    'Hitachi',
    'DS12DVF3',
    'CN',
    4064,
    'RUB',
    1,
    4064,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    (SELECT br_id FROM brand WHERE br_name = 'HITACHI'),
    NULL,
    (SELECT mod_id FROM model OFFSET 0 LIMIT 1)
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT com_id FROM company WHERE com_name = 'company1'),
    'Шуруповерт аккумуляторный Makita 6271 DWPE 12V NiCd',
    'Частота холостого хода: 1 скорость 0-350 об/мин. 2 скорость 0-1.200 об/мин. Диаметр сверления: в стали - 10 мм, в дереве - 24 мм.',
    'Макита',
    '6271 DWPE',
    'US',
    250,
    'USD',
    1,
    0,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    (SELECT br_id FROM brand WHERE br_name = 'Makita'),
    NULL,
    (SELECT mod_id FROM model OFFSET 1 LIMIT 1)
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT com_id FROM company WHERE com_name = 'company2'),
    'Инструмент Makita 6271DWPE',
    'Код производителя: 6271DWPE Официальная гарантия.',
    'MAKITA',
    '6271DWPE',
    'US',
    3930,
    'RUB',
    1,
    3930,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    (SELECT br_id FROM brand WHERE br_name = 'Makita'),
    NULL,
    (SELECT mod_id FROM model OFFSET 1 LIMIT 1)
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT com_id FROM company WHERE com_name = 'company3'),
    'Аккумуляторная дрель Makita 6271 DWPE',
    'Официальная гарантия. Аккумуляторная дрель-шуруповерт MAKITA 6270 DWAE. Профессиональная. Напряжение (В) - 12; Емкость аккумулятора (Ач) - 1.3; Две скорости',
    'Makita',
    '6271 DWPE',
    'US',
    4440,
    'RUB',
    1,
    4440,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    (SELECT br_id FROM brand WHERE br_name = 'Makita'),
    NULL,
    (SELECT mod_id FROM model OFFSET 1 LIMIT 1)
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT com_id FROM company WHERE com_name = 'company1'),
    'BOSCH Аккумуляторная дрель-шуруповёрт GSR 10,8 V-LI-2',
    'Макс. крутящий момент (жесткое заворачивание шурупов) 30 Нм Макс. крутящий момент (мягкое заворачивание шурупов) 11 Нм Число Оборотов',
    'БОШ',
    'GSR 10,8 V-LI-2',
    'DE',
    6100,
    'RUB',
    1,
    6100,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    (SELECT br_id FROM brand WHERE br_name = 'BOSCH'),
    NULL,
    (SELECT mod_id FROM model OFFSET 2 LIMIT 1)
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT com_id FROM company WHERE com_name = 'company1'),
    'Аккумуляторная дрель Bosch Professional GSR 10,8 V-LI-2 (0.601.868.008)',
    'Официальная гарантия, страна производства: Германия.Макс. крутящий момент (жесткое заворачивание шурупов) 30 Нм Макс. крутящий момент (мягкое заворачивание шурупов) 11 Нм',
    'BOSCH',
    'GSR 10,8',
    'DE',
    6285,
    'RUB',
    1,
    6285,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    (SELECT br_id FROM brand WHERE br_name = 'BOSCH'),
    NULL,
    (SELECT mod_id FROM model OFFSET 2 LIMIT 1)
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT com_id FROM company WHERE com_name = 'company1'),
    'Аккумуляторная дрель-шуруповерт Bosch GSR 10,8 V-LI-2 Professional Body (тушка)',
    'Код производителя: 0 601 868 005',
    'Bosch',
    'GSR 10,8 V-LI-2 Professional',
    'DE',
    3608,
    'RUB',
    1,
    3608,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    (SELECT br_id FROM brand WHERE br_name = 'BOSCH'),
    NULL,
    (SELECT mod_id FROM model OFFSET 2 LIMIT 1)
),
/* Предложения с параметрами */
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT com_id FROM company WHERE com_name = 'company1'),
    'Hitachi DS12DVF3',
    'дрель безударная,быстрозажимной патрон, аккумуляторная, количество скоростей: 2, патрон 10 мм, реверс, вес 1.7 кг, кейс в комплекте',
    'Хитачи',
    NULL,
    'CN',
    2407,
    'RUB',
    1,
    2407,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    (SELECT br_id FROM brand WHERE br_name = 'HITACHI'),
    (SELECT psx_id FROM param_set_category_drills OFFSET 3 LIMIT 1),
    NULL
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT com_id FROM company WHERE com_name = 'company1'),
    'Makita 6271DWPE',
    'дрель безударная,быстрозажимной патрон, аккумуляторная, количество скоростей: 2, патрон 10 мм, реверс, фиксация шпинделя, вес 1.5 кг, кейс в комплекте',
    'MakiTA',
    NULL,
    'DE',
    3800,
    'RUB',
    1,
    3800,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    (SELECT br_id FROM brand WHERE br_name = 'Makita'),
    (SELECT psx_id FROM param_set_category_drills OFFSET 4 LIMIT 1),
    NULL
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT com_id FROM company WHERE com_name = 'company2'),
    'AEG BS 12 C IQ',
    'дрель безударная,быстрозажимной патрон, аккумуляторная, количество скоростей: 1, патрон 10 мм, реверс, вес 1.1 кг, кейс в комплекте ',
    'AEG',
    NULL,
    'DE',
    5074,
    'RUB',
    1,
    5074,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    NULL,
    (SELECT psx_id FROM param_set_category_drills OFFSET 5 LIMIT 1),
    NULL
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT com_id FROM company WHERE com_name = 'company2'),
    'Metabo BSZ 12 Impuls 1.4 Ah',
    'дрель безударная,быстрозажимной патрон, аккумуляторная, количество скоростей: 2, патрон 13 мм, реверс, фиксация шпинделя, вес 2 кг, кейс в комплекте',
    'Metabo',
    NULL,
    'DE',
    6690,
    'RUB',
    1,
    6690,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    NULL,
    (SELECT psx_id FROM param_set_category_drills OFFSET 6 LIMIT 1),
    NULL
),
/* Простые предложения */
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT com_id FROM company WHERE com_name = 'company1'),
    'Интерскол ДА-18ЭР',
    'дрель безударная,быстрозажимной патрон, аккумуляторная, количество скоростей: 2, патрон 13 мм, реверс, вес 2.1 кг, кейс в комплекте ',
    'Интерскол',
    NULL,
    'RU',
    2650,
    'RUB',
    1,
    2650,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    (SELECT br_id FROM brand WHERE br_name = 'ИНТЕРСКОЛ'),
    NULL,
    NULL
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT com_id FROM company WHERE com_name = 'company2'),
    'Интерскол ДА-12ЭР-01',
    'дрель безударная,быстрозажимной патрон, аккумуляторная, количество скоростей: 2, патрон 10 мм, реверс, фиксация шпинделя, вес 1.7 кг, кейс в комплекте ',
    'Интерскол',
    NULL,
    'RU',
    1850,
    'RUB',
    1,
    1850,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    (SELECT br_id FROM brand WHERE br_name = 'ИНТЕРСКОЛ'),
    NULL,
    NULL
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT com_id FROM company WHERE com_name = 'company3'),
    'Калибр ДЭ-300Ш',
    'дрель безударная,быстрозажимной патрон, количество скоростей: 1, мощность 300 Вт, патрон 10 мм, реверс ',
    'Калибр',
    'ДЭ-300Ш',
    'RU',
    800,
    'RUB',
    1,
    800,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    NULL,
    NULL,
    NULL
)
;

/* Тестовые предложения для модерации */
INSERT INTO offer(
    off_category_id,
    off_company_id,
    off_name,
    off_description,
    off_origin_country,
    off_price,
    off_currency,
    off_ratio,
    off_unit_price,
    off_actual_date,
    off_active,
    off_available,
    off_delivery,
    off_create_date,
    off_edit_date,
    off_status,
    off_moderator_id,
    off_moderation_start_date,
    off_rejection_mask
)
VALUES
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Пилы'),
    (SELECT com_id FROM company WHERE com_name = 'company1'),
    'Husqvarna 235e',
    'бензопила, мощность: 1300 Вт / 1.7 л. с., макс. длина шины: 40 см, шаг цепи: 0.325 дюйма, вес: 4.70 кг ',
    'CN',
    5967,
    'RUB',
    1,
    5967,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    'APPROVED',
    (SELECT acc_id FROM account WHERE acc_email = 'manager2@example.com'),
    (SELECT current_timestamp),
    NULL
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Пилы'),
    (SELECT com_id FROM company WHERE com_name = 'company1'),
    'Metabo KS 216 Lasercut',
    'торцовочная, настольная, мощность: 1350 Вт, высота пропила: 60 мм, диаметр диска: 216 мм, скорость вращения: 5000 об/мин, лазерный маркер',
    'CN',
    4399,
    'RUB',
    1,
    4399,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    'PENDING',
    (SELECT acc_id FROM account WHERE acc_email = 'manager2@example.com'),
    (SELECT current_timestamp),
    NULL
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Пилы'),
    (SELECT com_id FROM company WHERE com_name = 'company1'),
    'Makita 5704R',
    'дисковая, ручная, мощность: 1200 Вт, высота пропила: 66 мм, диаметр диска: 190 мм, скорость вращения: 4900 об/мин, вес: 4.60 кг ',
    'CN',
    3905,
    'RUB',
    1,
    3905,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    'REJECTED',
    (SELECT acc_id FROM account WHERE acc_email = 'manager2@example.com'),
    (SELECT current_timestamp),
    1
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Пилы'),
    (SELECT com_id FROM company WHERE com_name = 'company1'),
    'Bosch PTS 10',
    'ДИСКОВАЯ, станок, мощность: 1400 Вт, высота пропила: 75 мм, диаметр диска: 254 мм, скорость вращения: 5000 об/мин ',
    'CN',
    2780,
    'RUB',
    1,
    2780,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    'REJECTED',
    (SELECT acc_id FROM account WHERE acc_email = 'manager2@example.com'),
    (SELECT current_timestamp),
    2
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT com_id FROM company WHERE com_name = 'company1'),
    'Makita 6271DWALE',
    'дрель-шуруповерт, патрон: быстрозажимной, аккумуляторная, количество скоростей: 2, патрон 10 мм, реверс, фиксация шпинделя, вес 1.6 кг, кейс в комплекте',
    'CN',
    4320,
    'RUB',
    1,
    4320,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    'APPROVED',
    (SELECT acc_id FROM account WHERE acc_email = 'manager2@example.com'),
    (SELECT current_timestamp),
    NULL
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT com_id FROM company WHERE com_name = 'company1'),
    'Bosch PSR 12',
    'дрель-шуруповерт, патрон: быстрозажимной, аккумуляторная, количество скоростей: 1, патрон 10 мм, реверс, кейс в комплекте ',
    'CN',
    2389,
    'RUB',
    1,
    2389,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    'PENDING',
    (SELECT acc_id FROM account WHERE acc_email = 'manager2@example.com'),
    (SELECT current_timestamp),
    NULL
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT com_id FROM company WHERE com_name = 'company1'),
    'AEG BS 12 C IQ',
    'дрель-шуруповерт, патрон: быстрозажимной, аккумуляторная, количество скоростей: 1, патрон 10 мм, реверс, вес 1.1 кг, кейс в комплекте',
    'CN',
    4990,
    'RUB',
    1,
    4990 ,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    'REJECTED',
    (SELECT acc_id FROM account WHERE acc_email = 'manager2@example.com'),
    (SELECT current_timestamp),
    4
),
(
    (SELECT ci_id FROM catalog_item WHERE ci_name = 'Дрели'),
    (SELECT com_id FROM company WHERE com_name = 'company1'),
    'Bosch GSB 13 RE (БЗП)',
    'дрель-шуруповерт, патрон: быстрозажимной, количество скоростей: 1, мощность 600 Вт, патрон 13 мм, реверс, вес 1.6 кг',
    'CN',
    4990,
    'RUB',
    1,
    4990 ,
    (SELECT current_timestamp + interval '1 month'),
    TRUE,
    TRUE,
    TRUE,
    (SELECT current_timestamp), (SELECT current_timestamp),
    'REJECTED',
    (SELECT acc_id FROM account WHERE acc_email = 'manager2@example.com'),
    (SELECT current_timestamp),
    8
)
;

/* Термины */
INSERT INTO term(term_name, term_description, term_source) VALUES
('45', E'Сорок пять', NULL),
('44', E'Сорок четыре', NULL),
('3D', E'Раздел компьютерной графики, совокупность приемов и инструментов (как программных, так и аппаратных), предназначенных для изображения объёмных объектов.\r\nБольше всего применяется для создания изображений на плоскости экрана или листа печатной продукции в архитектурной визуализации, кинематографе, телевидении, компьютерных играх, печатной продукции, а также в науке и промышленности.', NULL),
('33', E'Тридцать три', NULL),
('блокировка кнопки включения', E'Возможность заблокировать кнопку включения.\r\nЕсли вы положили инструмент, то блокировка не позволит запустить дрель от случайного нажатия (особенно это важно, если рядом находятся дети).', NULL),
('Балка', E'Горизонтальная несущая конструкция зданий и сооружений, имеющая опору в двух или более точках.\r\nБалка, перекрывающая один пролет и имеющая две опоры, называется разрезной. Балка, перекрывающая несколько пролетов и имеющая несколько опор, называется неразрезной многопролетной.', 'stroit.ru'),
('Быстрозажимной патрон', E'Наличие у перфоратора быстрозажимного патрона (БЗП).\r\nДля закрепления сверла в быстрозажимном патроне достаточно лишь небольшого усилия руки. Качество зажима такого патрона не хуже ключевого.\r\nРазличают одно и двухгильзовые быстрозажимные патроны. У двухгильзового патрона есть два вращающихся кольца - для зажатия и ослабления.\r\nПростые в обращении одно-гильзовые патроны (press-lock и auto-lock) предлагают самый удобный способ для смены сверла.', 'http://market.yandex.ru/faq.xml?CAT_ID=948412'),
('ДСП', E'Древесно-стружечная плита (ДСтП, ДСП)', NULL),
('DIY', E'Do It Youself («Сделай сам»).', NULL),
('Электронная регулировка частоты вращения', E'Наличие электронной регулировки частоты вращения шпинделя. Скорость вращения зависит от силы нажатия на кнопку пуска. Специальный процессор, в зависимости от выставленного типа материала и определенного самим перфоратором диаметра сверла, выбирает оптимальную скорость вращения и поддерживает ее вне зависимости от нагрузки на инструмент (все конечно в разумных пределах). Требуется при выполнении ответственных работ, а также подбирается для разных типов сверл и материала, обеспечивая увеличение срока службы инструмента, производительность и качество работы.', 'http://market.yandex.ru/faq.xml?CAT_ID=948412')
;

/* Регионы доставки компаний */
INSERT INTO delivery(del_company_id, del_region_id) VALUES
((SELECT com_id FROM company WHERE com_name = 'company1'), (SELECT reg_id FROM region WHERE reg_name = 'Москва')),
((SELECT com_id FROM company WHERE com_name = 'company1'), (SELECT reg_id FROM region WHERE reg_name = 'Химки')),
((SELECT com_id FROM company WHERE com_name = 'company1'), (SELECT reg_id FROM region WHERE reg_name = 'Клин')),
((SELECT com_id FROM company WHERE com_name = 'company1'), (SELECT reg_id FROM region WHERE reg_name = 'Тверь')),
--
((SELECT com_id FROM company WHERE com_name = 'company2'), (SELECT reg_id FROM region WHERE reg_name = 'Тверь')),
((SELECT com_id FROM company WHERE com_name = 'company3'), (SELECT reg_id FROM region WHERE reg_name = 'Тверская область'))
;

INSERT INTO extra_currency(ec_company_id, ec_currency, ec_percent, ec_fixed_rate) VALUES
((SELECT com_id FROM company WHERE com_name = 'company2'), 'USD', 10, NULL),
((SELECT com_id FROM company WHERE com_name = 'company2'), 'EUR', NULL, 50)
;

/* Контактная информация */
INSERT INTO contact(con_account_id, con_type, con_value, con_label) VALUES
((SELECT com_id FROM company WHERE com_name = 'company1'), 'ICQ', '333-444-555', NULL),
((SELECT com_id FROM company WHERE com_name = 'company1'), 'SKYPE', 'general.director', NULL),
((SELECT com_id FROM company WHERE com_name = 'company1'), 'PHONE', '1234567', 'Отдел продаж'),
((SELECT com_id FROM company WHERE com_name = 'company1'), 'PHONE', '7654321', 'Отдел покупки')
;